package com.androditry;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ViewOneCategory extends ActionBarActivity {
	
	TextView tvTitle,tvDetails,tvNumFollowers;
	Button btnFollow;
	
	String tagsFollowed = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_one_category);
		
		ParseObject tagObj = Utilities.getObjectByTagName(Utilities.tagName);
		if(tagObj==null)
		{
			Intent i = new Intent(ViewOneCategory.this,MainActivity.class);
			startActivity(i);
		}
		setTitle(Utilities.tagName);
		
		tvTitle  		= (TextView) findViewById(R.id.tvTitleOneCategory);
		tvDetails 		= (TextView) findViewById(R.id.tvDetailsOneCategory);
		tvNumFollowers	= (TextView) findViewById(R.id.tvNumFollowersOneCategory);
		btnFollow 		= (Button)   findViewById(R.id.btnFollowOneCategory);
		
		boolean doesFollow = Utilities.doesFollow(Utilities.tagName);
		if(doesFollow)
		{
			btnFollow.setText(R.string.unfollowOneCategory);
		}
		
		btnFollow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String btnFollowText = btnFollow.getText().toString().trim();
				btnFollow.setText("Saving...");
				btnFollow.setEnabled(false);
				ParseObject obj = Utilities.getUserDetailsObj();
				tagsFollowed = obj.getString(Utilities.alias_TAGSFOLLOWED);
				if(btnFollowText.equals("FOLLOW"))
				{
					if(tagsFollowed.length()>0)
						tagsFollowed += "-" + Utilities.tagName;
					else
						tagsFollowed = Utilities.tagName;
					
					//Toast.makeText(getApplicationContext(), "btnFollowText : "+ btnFollowText + "\ntagsFollowed(in add) : " + tagsFollowed, Toast.LENGTH_SHORT).show();
					
				}
				else
				{
					tagsFollowed = tagsFollowed.replaceAll(Utilities.getCategory() , "");
					tagsFollowed = tagsFollowed.replaceAll("--", "-");
					if(tagsFollowed.endsWith("-"))
						tagsFollowed = tagsFollowed.substring(0, tagsFollowed.length()-1);
					if(tagsFollowed.startsWith("-"))
						tagsFollowed = tagsFollowed.substring(1, tagsFollowed.length());
					
					Toast.makeText(getApplicationContext(), "btnFollowText : "+ btnFollowText + "\ntagsFollowed(in remove) : " + tagsFollowed, Toast.LENGTH_SHORT).show();
				}
				
				updateUserTagsFollowed(obj);
			}
		});
		
		tvTitle.setText(tagObj.getString(Utilities.alias_TAGNAME));
		tvDetails.setText(tagObj.getString(Utilities.alias_TAGDETAILS));
		tvNumFollowers.setText(tagObj.getInt(Utilities.alias_TAGFOLLOWERS) + " Followers");
	}
	
	private void updateUserTagsFollowed(ParseObject obj)
	{
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.UserDetails);
		
		//Toast.makeText(getApplicationContext(), "Id to retrieve : " + obj.getObjectId(), Toast.LENGTH_SHORT).show();
        // Retrieve the object by id
        query.getInBackground(obj.getObjectId(), new GetCallback<ParseObject>() {
          public void done(ParseObject post, ParseException e) {
            if (e == null) {
            		// Now let's update it with some new data.
                	post.put(Utilities.alias_TAGSFOLLOWED, tagsFollowed);
                	
                	//Toast.makeText(getApplicationContext(), "ID = " + post.getObjectId() + "\ntagsFollowed= "+tagsFollowed, Toast.LENGTH_SHORT).show();
                	
                	Utilities.setUserDetailsObj(post);
               		post.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                    	btnFollow.setText(Utilities.doesFollow(Utilities.tagName) ? 
                    						R.string.unfollowOneCategory : R.string.followOneCategory);
                        btnFollow.setEnabled(true);
                    	if (e == null) {
                            // Saved successfully.
                    		if(Utilities.doesFollow(Utilities.tagName))
                    		{
                    			changeCurNumFollowers(1);
                    		}
                    		else
                    		{
                    			changeCurNumFollowers(-1);
                    		}
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            // The save failed.
                            Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
                            Log.d(getClass().getSimpleName(), "User preference update error: " + e);
                        }
                    }

                });
            }
            else
            {
            	Log.d(getClass().getSimpleName(), "Could not find Object by id error: " + e);
            }
          }
        });
	}
	
	private void changeCurNumFollowers(final int _change) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.AllTagsList);
		
		ParseObject obj = Utilities.getObjectByTagName(Utilities.getCategory());
		//Toast.makeText(getApplicationContext(), "Id to retrieve : " + obj.getObjectId(), Toast.LENGTH_SHORT).show();
        // Retrieve the object by id
        query.getInBackground(obj.getObjectId(), new GetCallback<ParseObject>() {
          public void done(ParseObject post, ParseException e) {
            if (e == null) {
            		// Now let's update it with some new data.
            		int t = post.getInt(Utilities.alias_TAGFOLLOWERS) + _change;
            		t = t<0 ? 0 : t;
                	post.put(Utilities.alias_TAGFOLLOWERS, t );
               		post.saveInBackground();
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
		getMenuInflater().inflate(R.menu.view_one_category, menu);
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
			Intent i = new Intent(ViewOneCategory.this,MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
