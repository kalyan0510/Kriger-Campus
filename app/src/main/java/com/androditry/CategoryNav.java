package com.androditry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CategoryNav extends ActionBarActivity {
	
	TextView tvCatName, tvCatInfo, tvInfo;
	ListView lvCatQues;
	Button   btnNewQues;
    ProgressDialog pd;
	
	ArrayList<CustomQuesListItem> list = new ArrayList<>();
    CustomQuesListAdapter adapter;

    private Timer timer;
    private static final long whenToStart = 60*1000L; // 60 seconds
    private static final long howOften = 60*1000L; // 60 seconds


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_nav);
		setTitle("Kriger Campus");

		tvCatName = (TextView) findViewById(R.id.tvCatName);
        tvCatName.setText(Utilities.getCategory().replace('_', ' '));
		tvCatInfo = (TextView) findViewById(R.id.tvCatInfo);
        tvCatInfo.setText("Loading...");

        //tvCatInfo.setText(Utilities.getCurTagObject().getString(Utilities.alias_TAGDETAILS));
        tvInfo    = (TextView) findViewById(R.id.tvInfoInCat);

		lvCatQues = (ListView) findViewById(R.id.lvCategoryQuestions);
		btnNewQues = (Button)  findViewById(R.id.btnAddQuestion);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
        tvCatInfo.setTypeface(face);
        tvCatName.setTypeface(face);
        btnNewQues.setTypeface(face);

		adapter = new CustomQuesListAdapter(this, list);
        lvCatQues.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        lvCatQues.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Utilities.setCurQuesObj(Utilities.getQuesObjectByQIndex(position));
                //Toast.makeText(CategoryNav.this, "Curr QuesObj saved: " + Utilities.getCurQuesObj()
                //                .getString(Utilities.alias_QID), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CategoryNav.this, QuestionView.class);
                startActivity(i);
            }

        });

        btnNewQues.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(CategoryNav.this, NewQuestion.class);
                startActivity(i);
            }
        });

        if(Utilities.getCurUserType()==Utilities.UserType.USER_TYPE_IPM)
        {
        	btnNewQues.setEnabled(false);
        	btnNewQues.setVisibility(View.GONE);
        }
        new UpdateDetail().execute();
	}

    class UpdateDetail extends AsyncTask<Void, String, UpdateTaskState> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(CategoryNav.this);
            pd.setMessage("Loading category details...");
            pd.show();
        }

        @Override
        protected UpdateTaskState doInBackground(Void... params) {

            if(!Utilities.isNetworkAvailable(CategoryNav.this) && !Utilities.hasCurTagQuesLoaded())
            {
                publishProgress("The list of questions could not be updated!\nCheck your network!");
                return UpdateTaskState.NO_INTERNET;
            }

            if(Utilities.getCurTagObject() != null)
                return UpdateTaskState.SUCCESS;

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.AllTagsList);
            query.whereEqualTo(Utilities.alias_TAGNAME, Utilities.getCategory());
            try
            {
                List<ParseObject> postList = query.find();
                Utilities.setCurTagObject(postList.get(0));
            } catch (ParseException e) {
                publishProgress("An error occurred. Please try refresh. If questions still don't load then please logout and login again!");
                //Toast.makeText(HomeScreenSchool.this, "No tags could be loaded due to error: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                e.printStackTrace();
                return UpdateTaskState.EXCEPTION_THROWN;
            }

            return UpdateTaskState.SUCCESS;
        }

        @Override
        protected void onProgressUpdate(String... text) {
            tvCatInfo.setText(text[0]);
            //tvInfo.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        @Override
        protected void onPostExecute(UpdateTaskState state) {
            pd.setMessage("");
            pd.dismiss();

            // refresh UI
            if(state == UpdateTaskState.SUCCESS)
            {
                tvCatInfo.setText(Utilities.getCurTagObject().getString(Utilities.alias_TAGDETAILS));
                if(Utilities.hasCurTagQuesLoaded())
                {
                    new UpdateQuestionsTask().execute(false);
                }
                else
                {
                    new UpdateQuestionsTask().execute(true);
                }
                timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        CategoryNav.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new UpdateQuestionsTask().execute(true);
                            }
                        });
                    }
                };
                timer.scheduleAtFixedRate(task, whenToStart, howOften);
            }
            else if(state == UpdateTaskState.NO_INTERNET)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryNav.this);
                builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private enum UpdateTaskState
    {
        SUCCESS,
        NO_INTERNET,
        EXCEPTION_THROWN
    }

    class UpdateQuestionsTask extends AsyncTask<Boolean,String, UpdateTaskState> {
        boolean forceUpdate;

        @Override
        protected void onPreExecute() {
            pd.setMessage("Loading Questions...\nPlease wait...");
        }

        @Override
        protected UpdateTaskState doInBackground(Boolean... params) {
            forceUpdate = params[0];

            if(!Utilities.isNetworkAvailable(CategoryNav.this)
                    && (!Utilities.hasCurTagQuesLoaded() || forceUpdate))
            {
                publishProgress("The list of questions could not be updated!\nCheck your network!");
                return UpdateTaskState.NO_INTERNET;
            }

            if(!forceUpdate)
            {
                if(Utilities.hasCurTagQuesLoaded())
                {
                    List<ParseObject> allQues = Utilities.getCurTagQuestions();
                    list.clear();
                    if (allQues != null) {
                        for(ParseObject tag: allQues)
                        {
                            list.add(new CustomQuesListItem("...",tag.getString(Utilities.alias_QTITLE), tag.getInt(Utilities.alias_QNUMANSWERS)));
                        }
                    }

                    publishProgress("All questions loaded!");
                    return UpdateTaskState.SUCCESS;
                }
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForTag(Utilities.getCategory()));
            query.addAscendingOrder(Utilities.alias_QNUMANSWERS);
            try {
                List<ParseObject> postList = query.find();
                Utilities.saveCurTagQuestions(postList);

                list.clear();
                for(ParseObject tag: postList)
                {
                    list.add(new CustomQuesListItem("...",tag.getString(Utilities.alias_QTITLE), tag.getInt(Utilities.alias_QNUMANSWERS)));
                }
                publishProgress("All questions loaded!");
            } catch (ParseException e) {
                publishProgress("An error occurred. Please try refresh. If interests still don't load then please logout and login again!");
                //Toast.makeText(HomeScreenSchool.this, "No tags could be loaded due to error: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                e.printStackTrace();
                return UpdateTaskState.EXCEPTION_THROWN;
            }

            return UpdateTaskState.SUCCESS;
        }

        @Override
        protected void onProgressUpdate(String... text) {
            tvInfo.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        @Override
        protected void onPostExecute(UpdateTaskState state) {

            // refresh UI
            if(state == UpdateTaskState.SUCCESS)
            {
                adapter.notifyDataSetChanged();
                //Toast.makeText(CategoryNav.this, Utilities.getCategory() + ": Saved ques length :"+ Utilities.getCurTagQuestions().size(), Toast.LENGTH_SHORT).show();
                new SetListNamesFromUserNames().execute();
                tvInfo.setVisibility(View.GONE);
            }
            else if(state == UpdateTaskState.NO_INTERNET)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryNav.this);
                builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    class SetListNamesFromUserNames extends AsyncTask<Void,Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            Boolean ret = false;
            int i=0;
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            String usrname;
            ParseUser user;
            for(CustomQuesListItem ques : list)
            {
                usrname = Utilities.getQuesObjectByQIndex(i++).getString(Utilities.alias_QBY);
                if(Utilities.hasUserObject(usrname))
                {
                    user = Utilities.getUserObject(usrname);
                    ques.setName(user.getString(Utilities.alias_UFULLNAME));
                    ret = true;
                    continue;
                }

                query.whereEqualTo("username", usrname);
                try
                {
                    List<ParseUser> postList = query.find();
                    ParseUser usr = postList.get(0);
                    Utilities.saveUserObject(usr);
                    ques.setName(usr.getString(Utilities.alias_UFULLNAME));
                    ret = true;
                }
                catch (ParseException e)
                {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return ret;
        }

        @Override
        public void onPostExecute(Boolean isSuccess)
        {
            if(isSuccess)
            {
                adapter.notifyDataSetChanged();
            }
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.category_nav, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == R.id.action_logout)
		{
            timer.cancel();
            Utilities.contextLogout = CategoryNav.this;
            new Utilities.LogoutTask().execute();
		}
		else if(id == R.id.action_refresh)
		{
			Toast.makeText(CategoryNav.this, "Refreshing...", Toast.LENGTH_SHORT).show();
			new UpdateQuestionsTask().execute(true);
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    protected void onPause()
    {
        super.onPause();
        timer.cancel();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		new UpdateQuestionsTask().execute(false);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CategoryNav.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new UpdateQuestionsTask().execute(true);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, whenToStart, howOften);
	}
}
