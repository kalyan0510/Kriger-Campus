package com.androditry;

import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

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
import android.widget.Toast;

public class NewQuestion extends ActionBarActivity {
	
	protected static final int MIN_QUES_TITLE_LENGTH = 10;
	EditText etQTitle,etQDetails;
	Button   btnPost;

    String qtitle, qdetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_question);
		setTitle("Ask a Question...");
		
		etQTitle   = (EditText) findViewById(R.id.etQuesTitle);
		etQDetails = (EditText) findViewById(R.id.etQuesDetails);
		btnPost    = (Button)   findViewById(R.id.btnPostQuestion);
		
		btnPost.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				qtitle = etQTitle.getText().toString().trim();
				qdetail = etQDetails.getText().toString().trim();

                new PostQuestionTask().execute();
			}
		});
	}


    private enum PostQuestionTaskState
    {
        SUCCESS,
        NO_INTERNET,
        TITLE_TOOSMALL,
        QUES_DUPLICATE,
        EXCEPTION_THROWN
    }

    String errMsg;
    class PostQuestionTask extends AsyncTask<Void,Boolean, PostQuestionTaskState> {

        @Override
        protected PostQuestionTaskState doInBackground(Void... params) {
            publishProgress(false);

            if (!Utilities.isNetworkAvailable(NewQuestion.this)) {
                return PostQuestionTaskState.NO_INTERNET;
            }

            if(qtitle.isEmpty() || qtitle.length()<MIN_QUES_TITLE_LENGTH)
            {
                return PostQuestionTaskState.TITLE_TOOSMALL;
            }
            else {
                try {
                    ParseObject testObject = new ParseObject(Utilities.AllClassesNames.getClassNameForTag(Utilities.getCategory()));
                    testObject.put(Utilities.alias_QTITLE, qtitle);
                    testObject.put(Utilities.alias_QDETAILS, qdetail);
                    testObject.put(Utilities.alias_QBY, Utilities.getCurUsername());
                    testObject.put(Utilities.alias_QNUMANSWERS, 0);
                    testObject.put(Utilities.alias_QNUMANSSEEN, 0);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForTag(Utilities.getCategory()));
                    if(Utilities.hasCurTagQuesLoaded())
                        query.fromLocalDatastore();
                    query.whereEqualTo(Utilities.alias_QTITLE, qtitle);
                    List<ParseObject> postList = query.find();
                    if (!postList.isEmpty()) {
                        return PostQuestionTaskState.QUES_DUPLICATE;
                    }
                    else
                    {
                        testObject.pin();
                        testObject.save();
                        updateNumQuestionsInCategory();
                        Toast.makeText(getApplicationContext(), "Question post successful!", Toast.LENGTH_SHORT).show();

                        ParsePush push = new ParsePush();
                        push.setChannel(Utilities.TL_CHANNEL_NAME);
                        push.setMessage("A new Question was asked in " + Utilities.getCategory() + "!");
                        push.send();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    errMsg = e.getMessage();
                    return PostQuestionTaskState.EXCEPTION_THROWN;
                }
            }
            return PostQuestionTaskState.SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Boolean... isEnabled) {
            btnPost.setEnabled(isEnabled[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        @Override
        protected void onPostExecute(PostQuestionTaskState state) {
            // refresh UI
            if (state == PostQuestionTaskState.SUCCESS) {
                // Success!
                finish();
            }
            else if (state == PostQuestionTaskState.NO_INTERNET) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewQuestion.this);
                builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (state == PostQuestionTaskState.TITLE_TOOSMALL) {
                String errorMsg = "Question title should be at least " + MIN_QUES_TITLE_LENGTH +" characters long!";
                AlertDialog.Builder builder = new AlertDialog.Builder(NewQuestion.this);
                builder.setMessage(errorMsg)
                        .setTitle("Error")
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if(state == PostQuestionTaskState.QUES_DUPLICATE)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewQuestion.this);
                builder.setMessage("A Question exists with the exactly same title.\nPlease consider reading that question instead.")
                        .setTitle("Duplicate Question!")
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if (state == PostQuestionTaskState.EXCEPTION_THROWN) {
                // The save failed.
                Toast.makeText(getApplicationContext(), "Failed to Save\nPlease Check network!", Toast.LENGTH_SHORT).show();
                Log.d(getClass().getSimpleName(), "Answer Post error: " + errMsg);
            }
            btnPost.setEnabled(true);
        }
    }

	private void updateNumQuestionsInCategory() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.AllTagsList);

        //Toast.makeText(getApplicationContext(), "Id to retrieve : " + obj.getObjectId(), Toast.LENGTH_SHORT).show();
        // Retrieve the object by id
        if (Utilities.haveAllTags)
            query.fromLocalDatastore();
        try {
            ParseObject obj = Utilities.getObjectByTagName(Utilities.getCategory());
            ParseObject post = query.get(obj.getObjectId());
            post.put(Utilities.alias_TAGQUES, post.getInt(Utilities.alias_TAGQUES) + 1);
            post.save();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(getClass().getSimpleName(), "Could not find Object by id error: " + e);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            Log.d(getClass().getSimpleName(), "The object for current tag was null!\n" + e.getMessage());
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_question, menu);
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
					Intent i = new Intent(NewQuestion.this,MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
			}).start();
		}
		return super.onOptionsItemSelected(item);
	}
}
