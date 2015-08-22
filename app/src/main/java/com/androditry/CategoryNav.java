package com.androditry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
	
	TextView tvInfo, tvAbout;
	ListView lvCatQues;
	Button   btnNewQues;
	
	ArrayList<CustomQuesListItem> list = new ArrayList<>();
    CustomQuesListAdapter adapter;

    private Timer timer;
    private static final long whenToStart = 30*1000L; // 30 seconds
    private static final long howOften = 30*1000L; // 30 seconds

    private boolean storedAllQuestions = false;
    private boolean storedAllUsers = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_nav);
		setTitle(Utilities.getCategory().replace('_', ' '));

		tvAbout = (TextView) findViewById(R.id.tvAboutCat);
		tvAbout.setText(Utilities.getCurTagObject().getString(Utilities.alias_TAGDETAILS));
		tvInfo = (TextView) findViewById(R.id.tvInfoCatNav);
		tvInfo.setText("Loading Questions");

		lvCatQues = (ListView) findViewById(R.id.lvCategoryQuestions);
		btnNewQues = (Button)  findViewById(R.id.btnAddQuestion);

		adapter = new CustomQuesListAdapter(this, list);
        lvCatQues.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        lvCatQues.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Utilities.setCurQuesObj(Utilities.getQuesObjectByQIndex(position));
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
		new UpdateQuestionsTask().execute(true);

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


    private enum UpdateTaskState
    {
        SUCCESS,
        NO_INTERNET,
        EXCEPTION_THROWN
    }

    class UpdateQuestionsTask extends AsyncTask<Boolean,String, UpdateTaskState> {
        @Override
        protected UpdateTaskState doInBackground(Boolean... params) {
            boolean forceUpdate = params[0];

            if(!Utilities.isNetworkAvailable(CategoryNav.this)
                    && (!storedAllQuestions || forceUpdate))
            {
                publishProgress("The list of questions could not be updated!\nCheck your network!");
                return UpdateTaskState.NO_INTERNET;
            }

            if(forceUpdate)
            {
                try {
                    ParseQuery<ParseObject> tquery = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForTag(Utilities.getCategory()));
                    tquery.fromLocalDatastore();
                    List<ParseObject> objs = tquery.find();
                    ParseObject.unpinAll(objs);
                } catch (ParseException e1) {
                    //Toast.makeText(this, "Error while unpinning objects.", Toast.LENGTH_SHORT).show();
                    e1.printStackTrace();
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

                ParseObject.pinAllInBackground(postList, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        storedAllQuestions = true;
                        Utilities.haveAllTags = true;
                    }
                });
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
                new SetListNamesFromUserNames().execute();
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

    class SetListNamesFromUserNames extends AsyncTask<Boolean,Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... params) {
            boolean isForcedUpdate = params[0];

            Boolean ret = false;
            int i=0;
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            for(CustomQuesListItem ques : list)
            {
                query.whereEqualTo("username", Utilities.getQuesObjectByQIndex(i++).getString(Utilities.alias_QBY));
                if(storedAllUsers && !isForcedUpdate)
                    query.fromLocalDatastore();

                try
                {
                    List<ParseUser> postList = query.find();
                    ParseUser usr = postList.get(0);
                    usr.pinInBackground();
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
                storedAllUsers = true;
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Utilities.logOutCurUser();
                    Intent i = new Intent(CategoryNav.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }).start();
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
