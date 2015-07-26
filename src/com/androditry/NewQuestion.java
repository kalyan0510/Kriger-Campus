package com.androditry;

import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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
				btnPost.setEnabled(false);
				if(!Utilities.isNetworkAvailable(NewQuestion.this))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(NewQuestion.this);
                    builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    btnPost.setEnabled(true);
            		return;
				}
				
				String errorMsg = "";
				String qtitle = etQTitle.getText().toString().trim();
				String qdetail = etQDetails.getText().toString().trim();
				
				if(qtitle.isEmpty() || qtitle.length()<MIN_QUES_TITLE_LENGTH)
				{
					errorMsg = "Question title should be atleast " + MIN_QUES_TITLE_LENGTH +" characters long!";
				}
				if(!errorMsg.isEmpty())
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(NewQuestion.this);
	                builder.setMessage(errorMsg)
	                    .setTitle("Error")
	                    .setPositiveButton(android.R.string.ok, null);
	                AlertDialog dialog = builder.create();
	                dialog.show();
				}
				else
				{
					ParseObject testObject = new ParseObject(Utilities.AllClassesNames.getClassNameForTag(Utilities.getCategory()));
				    testObject.put(Utilities.alias_QTITLE, qtitle);
				    testObject.put(Utilities.alias_QDETAILS, qdetail);
				    testObject.put(Utilities.alias_QBY, Utilities.getCurUsername());
				    testObject.put(Utilities.alias_QNUMANSWERS, 0);
				    testObject.put(Utilities.alias_QNUMANSSEEN, 0);
				    
				    final ParseObject finalTestObject = testObject;
				    
				    ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForTag(Utilities.getCategory()));
				    if(Utilities.hasCurTagQuesLoaded())
				    	query.fromLocalDatastore();
					query.whereEqualTo(Utilities.alias_QTITLE, qtitle);
				    query.findInBackground(new FindCallback<ParseObject>() {
				        @Override
				        public void done(List<ParseObject> postList, ParseException e) {
				        	if(e == null)
				        	{
				        		if(!postList.isEmpty())
				        		{
				        			AlertDialog.Builder builder = new AlertDialog.Builder(NewQuestion.this);
				    	            builder.setMessage("A Question exists with the exactly same title.\nPlease consider reading that question instead.")
				    	                .setTitle("Duplicate Question!")
				    	                .setPositiveButton(android.R.string.ok, null);
				    	            AlertDialog dialog = builder.create();
				    	            dialog.show();
				        		}
				        		else
				        		{
				        			finalTestObject.pinInBackground();
				        			finalTestObject.saveInBackground(new SaveCallback() {
					                    public void done(ParseException e) {
					                    	if (e == null) {
					                    		updateNumQuestionsInCategory();
					                    		Toast.makeText(getApplicationContext(), "Question post succesful!", Toast.LENGTH_SHORT).show();
					                    		NewQuestion.this.finish();
					                    	} else {
					                            // The save failed.
					                            Toast.makeText(getApplicationContext(), "Failed to Save\nPlease Check network!", Toast.LENGTH_SHORT).show();
					                            Log.d(getClass().getSimpleName(), "Answer Post error: " + e);
					                        }
					                    }
					                });
				        		}
				        	}
				        	else
				        	{
				        		Log.d("New question asked...","Caused exception" + e.getMessage());
				        	}
				        }
				    });
				}
				btnPost.setEnabled(true);
			}
		});
	}
	
	private void updateNumQuestionsInCategory() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.AllTagsList);
		
		ParseObject obj = Utilities.getObjectByTagName(Utilities.getCategory());
		//Toast.makeText(getApplicationContext(), "Id to retrieve : " + obj.getObjectId(), Toast.LENGTH_SHORT).show();
        // Retrieve the object by id
		if(Utilities.haveAllTags)
			query.fromLocalDatastore();
        query.getInBackground(obj.getObjectId(), new GetCallback<ParseObject>() {
          public void done(ParseObject post, ParseException e) {
            if (e == null) {
            		// Now let's update it with some new data.
                	post.put(Utilities.alias_TAGQUES, post.getInt(Utilities.alias_TAGQUES) + 1);
               		post.saveEventually();
            }
            else
            {
            	Log.d(getClass().getSimpleName(), "Could not find Object by id error: " + e);
            }
          }
        });
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
		if (id == R.id.action_settings) {
			return true;
		}
		else if(id == R.id.action_logout)
		{
			Utilities.logOutCurUser();
			Intent i = new Intent(NewQuestion.this,MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
}
