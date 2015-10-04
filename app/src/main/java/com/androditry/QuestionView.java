package com.androditry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionView extends ActionBarActivity {
	
	TextView qTitle,qDetails;
	EditText etAnswer;
	Button btnPost;
	ListView lvAllAnswers;
	
	ArrayList<CustomListItem> list = new ArrayList<>();
	CustomListAdapter adapter;

    private Timer timer;
    private static final long whenToStart = 60*1000L; // 60 seconds
    private static final long howOften = 60*1000L; // 60 seconds

    String strAns;
    //private boolean timerCalledUpdate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);

        setTitle("Kriger Campus");

        etAnswer = (EditText) findViewById(R.id.etAnswerUser);
        btnPost = (Button) findViewById(R.id.btnPostAnswer);
        lvAllAnswers = (ListView) findViewById(R.id.lvAllAnswers);

        qTitle = (TextView) findViewById(R.id.tvQuestionTitleShow);
        qDetails = (TextView) findViewById(R.id.tvQuestionDetailsShow);

        ParseObject obj = Utilities.getCurQuesObj();
        qTitle.setText(obj.getString(Utilities.alias_QTITLE));
        qDetails.setText(obj.getString(Utilities.alias_QDETAILS));

        list.add(new CustomListItem("", "loading all answers...", false));
        adapter = new CustomListAdapter(this, list);
        lvAllAnswers.setAdapter(adapter);
        lvAllAnswers.setEnabled(false);
        adapter.notifyDataSetChanged();

        btnPost.setEnabled(false);
        btnPost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnPost.setEnabled(false);
                strAns = etAnswer.getText().toString().trim();
                new PostAnswerTask().execute();
            }
        });

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
        btnPost.setTypeface(face);

        if(Utilities.hasCurQuesAnsLoaded())
            new UpdateAnswersTask().execute(false);
        else
            new UpdateAnswersTask().execute(true);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                QuestionView.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new UpdateAnswersTask().execute(true);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, whenToStart, howOften);
    }

    private enum PostAnswerTaskState
    {
        SUCCESS,
        NO_INTERNET,
        ANS_EMPTY,
        EXCEPTION_THROWN
    }

    String errMsg;
    class PostAnswerTask extends AsyncTask<Void,String, PostAnswerTaskState> {

        @Override
        protected PostAnswerTaskState doInBackground(Void... params) {

            if (!Utilities.isNetworkAvailable(QuestionView.this)) {
                return PostAnswerTaskState.NO_INTERNET;
            }
            if (strAns.isEmpty())
            {
                return PostAnswerTaskState.ANS_EMPTY;
            }
            else {
                try {
                    ParseObject testObject = new ParseObject(Utilities.AllClassesNames.getClassNameForQues(Utilities.getCurQuesObj().getObjectId()));
                    testObject.put(Utilities.alias_ANSBY, Utilities.getCurUsername());
                    testObject.put(Utilities.alias_ANSTEXT, strAns);
                    publishProgress("");
                    testObject.save();
                    String usrQBy = Utilities.getCurQuesObj().getString(Utilities.alias_QBY);
                    if (!usrQBy.equalsIgnoreCase(Utilities.getCurUsername())) {
                        // Create our Installation query
                        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
                        pushQuery.whereEqualTo("username", usrQBy);

                        // Send push notification to query
                        ParsePush push = new ParsePush();
                        push.setQuery(pushQuery); // Set our Installation query
                        //String strAnsweredBy = (Utilities.getCurTagObject().getBoolean(Utilities.alias_TAGISANON) && Utilities.getCurUserType() == Utilities.UserType.USER_TYPE_IPM) ? Utilities.getCurName() : "Someone";
                        push.setMessage("Someone has posted an answered to your question on " + Utilities.getCategory() + "!");
                        push.send();
                    }
                    Utilities.getCurQuesAnswers().add(testObject);
                    updateCurQuestionNumAnswers();
                } catch (ParseException e) {
                    e.printStackTrace();
                    errMsg = e.getMessage();
                    return PostAnswerTaskState.EXCEPTION_THROWN;
                }
            }
            return PostAnswerTaskState.SUCCESS;
        }

        @Override
        protected void onProgressUpdate(String... text) {
            etAnswer.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        @Override
        protected void onPostExecute(PostAnswerTaskState state) {
            // refresh UI
            if (state == PostAnswerTaskState.SUCCESS) {
                Toast.makeText(QuestionView.this, "Answer posted Successfully!", Toast.LENGTH_SHORT).show();
                new UpdateAnswersTask().execute(false);
            }
            else if (state == PostAnswerTaskState.NO_INTERNET) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionView.this);
                builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if (state == PostAnswerTaskState.ANS_EMPTY)
            {
                Toast.makeText(QuestionView.this, "Can't post empty answer!!", Toast.LENGTH_SHORT).show();
            }
            else if (state == PostAnswerTaskState.EXCEPTION_THROWN) {
                // The save failed.
                Toast.makeText(getApplicationContext(), "Failed to Save\nPlease Check network!", Toast.LENGTH_SHORT).show();
                Log.d(getClass().getSimpleName(), "Answer Post error: " + errMsg);
            }
            btnPost.setEnabled(true);
        }
    }



    private void setAllAnswersAsSeen() throws ParseException {
        ParseObject obj = Utilities.getCurQuesObj();
        obj.put(Utilities.alias_QNUMANSSEEN, obj.getInt(Utilities.alias_QNUMANSWERS));
        obj.save();
    }

	private void updateCurQuestionNumAnswers() throws ParseException {
        ParseObject obj = Utilities.getCurQuesObj();
        obj.put(Utilities.alias_QNUMANSWERS, obj.getInt(Utilities.alias_QNUMANSWERS) + 1);
        obj.save();
	}

    private enum UpdateTaskState
    {
        SUCCESS,
        NO_INTERNET,
        NO_COMMENTS,
        EXCEPTION_THROWN
    }

    class UpdateAnswersTask extends AsyncTask<Boolean,String, UpdateTaskState> {
        boolean forceUpdate;

        @Override
        protected UpdateTaskState doInBackground(Boolean... params) {
            forceUpdate = params[0];

            if(!Utilities.isNetworkAvailable(QuestionView.this)
                    && (!Utilities.hasCurQuesAnsLoaded() || forceUpdate))
            {
                publishProgress("The list of questions could not be updated!\nCheck your network!");
                return UpdateTaskState.NO_INTERNET;
            }

            if(!forceUpdate)
            {
                if(Utilities.hasCurQuesAnsLoaded())
                {
                    List<ParseObject> ans = Utilities.getCurQuesAnswers();
                    list.clear();
                    if(ans.isEmpty())
                    {
                        list.add(new CustomListItem("","No answer yet for this question!", false));
                        return UpdateTaskState.NO_COMMENTS;
                    }
                    for(ParseObject obj : ans)
                    {
                        list.add(new CustomListItem("...",obj.getString(Utilities.alias_ANSTEXT), false));
                    }
                    return UpdateTaskState.SUCCESS;
                }
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForQues(Utilities.getCurQuesObj().getObjectId()));
            query.addAscendingOrder("createdAt");
            try {
                List<ParseObject> postList = query.find();
                list.clear();
                Utilities.saveCurQuesAnswers(postList);
                if(postList.isEmpty())
                {
                    list.add(new CustomListItem("","No answer yet for this question!", false));
                    return UpdateTaskState.NO_COMMENTS;
                }
                for(ParseObject tag: postList)
                {
                    list.add(new CustomListItem("...",tag.getString(Utilities.alias_ANSTEXT), false));
                }
                if(Utilities.getCurUsername().equalsIgnoreCase(Utilities.getCurQuesObj().getString(Utilities.alias_QBY)))
                {
                    setAllAnswersAsSeen();
                }
            } catch (ParseException e) {
                //publishProgress("An error occurred. Please try refresh. If interests still don't load then please logout and login again!");
                //Toast.makeText(HomeScreenSchool.this, "No tags could be loaded due to error: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                e.printStackTrace();
                return UpdateTaskState.EXCEPTION_THROWN;
            }

            return UpdateTaskState.SUCCESS;
        }

        @Override
        protected void onProgressUpdate(String... text) {
            //tvInfo.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        @Override
        protected void onPostExecute(UpdateTaskState state) {
            // refresh UI
            if(state == UpdateTaskState.SUCCESS)
            {
                lvAllAnswers.setEnabled(true);
                etAnswer.setHint("Write your response here!");
                etAnswer.setEnabled(true);
                btnPost.setEnabled(true);
                btnPost.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                new SetListNamesFromUserNames().execute();
            }
            else if(state == UpdateTaskState.NO_INTERNET)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionView.this);
                builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if(state == UpdateTaskState.NO_COMMENTS)
            {
                lvAllAnswers.setEnabled(false);
                if(Utilities.getCurUserType()==Utilities.UserType.USER_TYPE_SCHOOL)
                {
                    //School users can join a discussion only when a thought leader has answered the question
                    btnPost.setEnabled(false);
                    btnPost.setVisibility(View.GONE);
                    etAnswer.setText("");
                    etAnswer.setHint("You can't comment here yet!");
                    etAnswer.setEnabled(false);
                }
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
            for(CustomListItem ans : list)
            {
                usrname = Utilities.getAnsObjectByAIndex(i++).getString(Utilities.alias_ANSBY);
                if(Utilities.hasUserObject(usrname))
                {
                    user = Utilities.getUserObject(usrname);
                    ans.setName(user.getString(Utilities.alias_UFULLNAME));
                    ans.setIsTL(Utilities.isIPMUser(user));
                    ret = true;
                    continue;
                }

                query.whereEqualTo("username", usrname);
                try
                {
                    List<ParseUser> postList = query.find();
                    ParseUser usr = postList.get(0);
                    Utilities.saveUserObject(usr);
                    ans.setName(usr.getString(Utilities.alias_UFULLNAME));
                    boolean isAnon = Utilities.getObjectByTagName(Utilities.getCategory()).getBoolean(Utilities.alias_TAGISANON);
                    boolean isTL = Utilities.isIPMUser(usr);
                    if(isAnon && isTL)
                        ans.setName("...");
                    else
                        ans.setName(usr.getString(Utilities.alias_UFULLNAME));
                    ans.setIsTL(isTL);
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
		getMenuInflater().inflate(R.menu.question_view, menu);
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
            Utilities.contextLogout = QuestionView.this;
            new Utilities.LogoutTask().execute();
		}
		else if(id == R.id.action_refresh)
		{
			Toast.makeText(QuestionView.this, "Refreshing...", Toast.LENGTH_SHORT).show();
			new UpdateAnswersTask().execute(true);
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
        new UpdateAnswersTask().execute(false);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                QuestionView.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new UpdateAnswersTask().execute(true);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, whenToStart, howOften);
    }
}
