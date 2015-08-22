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
    private static final long whenToStart = 30*1000L; // 30 seconds
    private static final long howOften = 30*1000L; // 30 seconds

	private boolean storedAllAnswers = false;
    private boolean storedAllUsers = false;
    String strAns;
    //private boolean timerCalledUpdate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);

        setTitle("Question in -- " + Utilities.getCategory());

        etAnswer = (EditText) findViewById(R.id.etAnswerUser);
        btnPost = (Button) findViewById(R.id.btnPostAnswer);
        lvAllAnswers = (ListView) findViewById(R.id.lvAllAnswers);

        qTitle = (TextView) findViewById(R.id.tvQuestionTitleShow);
        qDetails = (TextView) findViewById(R.id.tvQuestionDetailsShow);

        ParseObject obj = Utilities.getCurQuesObj();
        qTitle.setText(obj.getString(Utilities.alias_QTITLE));
        qDetails.setText(obj.getString(Utilities.alias_QDETAILS));

        adapter = new CustomListAdapter(this, list);
        lvAllAnswers.setAdapter(adapter);
        lvAllAnswers.setEnabled(false);
        list.add(new CustomListItem("", "loading all answers..."));
        adapter.notifyDataSetChanged();

        btnPost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnPost.setEnabled(false);
                strAns = etAnswer.getText().toString().trim();

                new PostAnswerTask().execute();
            }
        });

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
                    testObject.pinInBackground();
                    publishProgress("");
                    testObject.pin();
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

                    Toast.makeText(QuestionView.this, "Answer posted Successfully!", Toast.LENGTH_SHORT).show();
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
                    && (!storedAllAnswers || forceUpdate))
            {
                publishProgress("The list of questions could not be updated!\nCheck your network!");
                return UpdateTaskState.NO_INTERNET;
            }

            if(forceUpdate)
            {
                try {
                    ParseQuery<ParseObject> tquery = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForQues(Utilities.getCurQuesObj().getObjectId()));
                    tquery.fromLocalDatastore();
                    List<ParseObject> objs = tquery.find();
                    ParseObject.unpinAll(objs);
                } catch (ParseException e1) {
                    //Toast.makeText(this, "Error while unpinning objects.", Toast.LENGTH_SHORT).show();
                    e1.printStackTrace();
                }
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForQues(Utilities.getCurQuesObj().getObjectId()));
            if(storedAllAnswers && !forceUpdate)
                query.fromLocalDatastore();
            query.addAscendingOrder("createdAt");
            try {
                List<ParseObject> postList = query.find();
                list.clear();

                Utilities.storeAllAnswers(postList);
                for(ParseObject tag: postList)
                {
                    list.add(new CustomListItem("...",tag.getString(Utilities.alias_ANSTEXT)));
                }
                if(list.isEmpty())
                {
                    list.add(new CustomListItem("","No answer yet for this question!"));
                    return UpdateTaskState.NO_COMMENTS;
                }
                if(Utilities.getCurUsername().equalsIgnoreCase(Utilities.getCurQuesObj().getString(Utilities.alias_QBY)))
                {
                    setAllAnswersAsSeen();
                }
                ParseObject.pinAllInBackground(postList, new SaveCallback() {
                    @Override
                    public void done(ParseException arg0) {
                        storedAllAnswers = true;
                        Utilities.setCurQuesAnsLoaded();
                    }
                });
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
                new SetListNamesFromUserNames().execute(forceUpdate);
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


    class SetListNamesFromUserNames extends AsyncTask<Boolean,Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... params) {
            boolean isForcedUpdate = params[0];

            Boolean ret = false;
            int i=0;
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            for(CustomListItem ans : list)
            {
                try
                {
                    query.whereEqualTo("username", Utilities.getAnswerObjByIndex(i++).getString(Utilities.alias_ANSBY));
                    if(storedAllUsers && !isForcedUpdate)
                        query.fromLocalDatastore();
                    List<ParseUser> postList = query.find();
                    ParseUser usr = postList.get(0);
                    usr.pinInBackground();
                    boolean isAnon = Utilities.getObjectByTagName(Utilities.getCategory()).getBoolean(Utilities.alias_TAGISANON);
                    boolean isTL = usr.getEmail().endsWith(Utilities.IPM_EMAIL_SUFFIX)
                            && usr.getEmail().startsWith(Utilities.IPM_EMAIL_PREFIX);
                    if(isAnon && isTL)
                        ans.setName("...");
                    else
                        ans.setName(usr.getString(Utilities.alias_UFULLNAME));
                    ret = true;
                }
                catch (ParseException e)
                {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                    e.printStackTrace();
                }
                catch (NullPointerException e)
                {
                    Log.d(getClass().getSimpleName(), "Object is null: " + e.getMessage());
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
			new Thread(new Runnable() {
				@Override
				public void run() {
					Utilities.logOutCurUser();
					Intent i = new Intent(QuestionView.this,MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
			}).start();
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
