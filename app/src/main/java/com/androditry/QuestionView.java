package com.androditry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionView extends ActionBarActivity {
	
	TextView qTitle,qDetails;
	EditText etAnswer;
	Button btnPost;
	ListView lvAllAnswers;
	
	ArrayList<CustomListItem> list = new ArrayList<CustomListItem>();
	CustomListAdapter adapter;

    Timer myTimer;
	private boolean haveAllAnswers = false;
    private boolean timerCalledUpdate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_view);
		
		setTitle("Question in -- " + Utilities.getCategory());
		
		etAnswer     = (EditText) findViewById(R.id.etAnswerUser);
		btnPost      = (Button)   findViewById(R.id.btnPostAnswer);
		lvAllAnswers = (ListView) findViewById(R.id.lvAllAnswers);
		
		qTitle   = (TextView) findViewById(R.id.tvQuestionTitleShow);
		qDetails = (TextView) findViewById(R.id.tvQuestionDetailsShow);
		
		ParseObject obj = Utilities.getCurQuesObj();
		qTitle.setText(obj.getString(Utilities.alias_QTITLE));
		qDetails.setText(obj.getString(Utilities.alias_QDETAILS));
		
		adapter = new CustomListAdapter(this, list);
		lvAllAnswers.setAdapter(adapter);
        lvAllAnswers.setEnabled(false);
        list.add(new CustomListItem("","loading all answers..."));
        adapter.notifyDataSetChanged();
        
        btnPost.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!Utilities.isNetworkAvailable(QuestionView.this))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(QuestionView.this);
                    builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
            		return;
				}
				String strAns = etAnswer.getText().toString().trim();
				if(strAns.isEmpty())
				{
					Toast.makeText(QuestionView.this, "Can't post empty answer!!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				ParseObject testObject = new ParseObject(Utilities.AllClassesNames.getClassNameForQues(Utilities.getCurQuesObj().getObjectId()));
			    testObject.put(Utilities.alias_ANSBY, Utilities.getCurUsername());
			    testObject.put(Utilities.alias_ANSTEXT, strAns);
			    testObject.pinInBackground();
			    etAnswer.setText("");
			    testObject.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                    	if (e == null) {
                    		String usrQBy = Utilities.getCurQuesObj().getString(Utilities.alias_QBY);
                    		if(!usrQBy.equalsIgnoreCase(Utilities.getCurUsername()))
                    		{
                    			// Create our Installation query
                        		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
                        		pushQuery.whereEqualTo("username", usrQBy);

                        		// Send push notification to query
                        		ParsePush push = new ParsePush();
                        		push.setQuery(pushQuery); // Set our Installation query
                        		//String strAnsweredBy = (Utilities.getCurTagObject().getBoolean(Utilities.alias_TAGISANON) && Utilities.getCurUserType() == Utilities.UserType.USER_TYPE_IPM) ? Utilities.getCurName() : "Someone";
                        		push.setMessage("Someone has posted an answered to your question on " + Utilities.getCategory() + "!");
                        		push.sendInBackground();
                    		}
                    		
                    		Toast.makeText(QuestionView.this, "Answer posted Successfully!", Toast.LENGTH_SHORT).show();
                    		updateCurQuestionNumAnswers();
                    		doPopulateAllAnswers(false);
                    	} else {
                            // The save failed.
                            //Toast.makeText(getApplicationContext(), "Failed to Save\nPlease Check network!", Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getSimpleName(), "Answer Post error: " + e);
                        }
                    }
                });
			}
		});
        
        doPopulateAllAnswers(true);
        /**
		myTimer = new Timer();
        myTimer.schedule(new TimerTask() {          
            @Override
            public void run() {
                TimerMethod();
            }

        }, 30000, 30000); **/
	}
/**
    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {
        	//This method runs in the same thread as the UI.     
        	timerCalledUpdate = true;
        	doPopulateAllAnswers(true);
        }
    };
	**/
	private void updateCurQuestionNumAnswers() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForTag(Utilities.getCategory()));
		
		ParseObject obj = Utilities.getCurQuesObj();
		//Toast.makeText(getApplicationContext(), "Id to retrieve : " + obj.getObjectId(), Toast.LENGTH_SHORT).show();
        // Retrieve the object by id
        query.getInBackground(obj.getObjectId(), new GetCallback<ParseObject>() {
          public void done(ParseObject post, ParseException e) {
            if (e == null) {
            		// Now let's update it with some new data.
                	post.put(Utilities.alias_QNUMANSWERS, post.getInt(Utilities.alias_QNUMANSWERS)+1);
                	if(Utilities.getCurUsername().equalsIgnoreCase(post.getString(Utilities.alias_QBY)))
                	{
                		post.put(Utilities.alias_QNUMANSSEEN, post.getInt(Utilities.alias_QNUMANSWERS));
                	}
               		post.saveInBackground();/*(new SaveCallback(){
						@Override
						public void done(ParseException arg0) {
							
						}
               		});
               		Toast.makeText(getApplicationContext(), "NumAns Updated : " + post.getInt(Utilities.alias_QNUMANSWERS) + "  in : " + Utilities.AllClassesNames.getClassNameForTag(Utilities.getCategory()), Toast.LENGTH_SHORT).show();
               		*/
            }
            else
            {
            	Log.d(getClass().getSimpleName(), "Could not find Object by id error: " + e);
            }
          }
        });
	}

	private void doPopulateAllAnswers(final boolean forceUpdate) {
		if(!Utilities.isNetworkAvailable(QuestionView.this)
				&& (!haveAllAnswers || forceUpdate))
		{
			if(!timerCalledUpdate)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(QuestionView.this);
	            builder.setMessage(R.string.no_internet_msg)
	                .setTitle(R.string.no_internet_title)
	                .setPositiveButton(android.R.string.ok, null);
	            AlertDialog dialog = builder.create();
	            dialog.show();
	        }
    		return;
		}
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForQues(Utilities.getCurQuesObj().getObjectId()));
		
		if(Utilities.hasCurQuesAnsLoaded())
			haveAllAnswers = true;
		if(haveAllAnswers)
		{
			if(!forceUpdate)
				query.fromLocalDatastore();
			else
			{
				try {
					ParseObject.unpinAll();
				} catch (ParseException e1) {
					Toast.makeText(this, "Error while unpinning objects.", Toast.LENGTH_SHORT).show();
					e1.printStackTrace();
				}
			}
		}

		query.addAscendingOrder("createdAt");
	    query.findInBackground(new FindCallback<ParseObject>() {
	 
	        @Override
	        public void done(List<ParseObject> postList, ParseException e) {
	            if (e == null) {
	            	list.clear();
	            	int index = 0, totSize = postList.size() - 1;
	            	String temp;
	            	boolean isAnon = Utilities.getObjectByTagName(Utilities.getCategory()).getBoolean(Utilities.alias_TAGISANON);
	            	Utilities.storeAllAnswers(postList);
	            	for(ParseObject tag: postList)
	            	{
	            		temp = tag.getString(Utilities.alias_ANSBY);
	            		list.add(new CustomListItem("...",tag.getString(Utilities.alias_ANSTEXT)));
	            		if(index < totSize)
	            			setListNameforusername(index, temp, false, isAnon, forceUpdate);
	            		else
	            			setListNameforusername(index, temp, true, isAnon, forceUpdate);
	            		++index;
	            	}
	            	if(list.isEmpty())
	            	{
	            		list.add(new CustomListItem("","No answer yet for this question!"));
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
	            	else
	            	{
	            		lvAllAnswers.setEnabled(true);
	            		etAnswer.setHint("Write your response here!");
            			etAnswer.setEnabled(true);
	            		btnPost.setEnabled(true);
            			btnPost.setVisibility(View.VISIBLE);
	            	}
	            	if(Utilities.getCurUsername().equalsIgnoreCase(Utilities.getCurQuesObj().getString(Utilities.alias_QBY)))
	            	{
	            		setAllAnswersAsSeen();
	            	}
	            	ParseObject.pinAllInBackground(postList, new SaveCallback()
	            	{
						@Override
						public void done(ParseException arg0) {
							haveAllAnswers = true;
							Utilities.setCurQuesAnsLoaded();
						}
	            	});
	                adapter.notifyDataSetChanged();
	            } else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
	        }
	    });
	    timerCalledUpdate = false;
	}
	
	protected void setListNameforusername(final int index, String user, final boolean triggerUpdate, final boolean isAnon, boolean isForcedUpdate) {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("username", user);
		if(haveAllAnswers && !isForcedUpdate)
			query.fromLocalDatastore();
		query.findInBackground(new FindCallback<ParseUser>() {
		     public void done(List<ParseUser> objects, ParseException e) {
		         if (e == null) {
		        	 ParseUser usr = objects.get(0);
		        	 usr.pinInBackground();
		        	 boolean isTL = usr.getEmail().endsWith(Utilities.IPM_EMAIL_SUFFIX) 
		        			 		&& usr.getEmail().startsWith(Utilities.IPM_EMAIL_PREFIX);
		        	 if(isAnon && isTL)
		        		 list.get(index).setName("...");
		        	 else
		        		 list.get(index).setName(usr.getString(Utilities.alias_UFULLNAME));
		             if(triggerUpdate)
		             {
		            	 adapter.notifyDataSetChanged();
		             }
		         } else {
		             Log.d("Question View", e.getMessage());
		         }
		     }
		 });
	}

	private void setAllAnswersAsSeen()
	{
		ParseObject obj = Utilities.getCurQuesObj();
		obj.put(Utilities.alias_QNUMANSSEEN, obj.getInt(Utilities.alias_QNUMANSWERS));
		obj.saveInBackground();
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
			doPopulateAllAnswers(true);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
		doPopulateAllAnswers(false);
	}
}
