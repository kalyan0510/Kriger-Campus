package com.androditry;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.support.v7.app.ActionBarActivity;
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
	
	ArrayList<CustomListItem> list = new ArrayList<CustomListItem>();
    CustomListAdapter adapter;
    
    private boolean haveAllQuestions = false;

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
		
		adapter = new CustomListAdapter(this, list);
        lvCatQues.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
        lvCatQues.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Utilities.setCurQuesObj(Utilities.getQuesObjectByQIndex(position));
				Intent i = new Intent(CategoryNav.this,QuestionView.class);
				startActivity(i);
			}
        	
        });
		
        btnNewQues.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(CategoryNav.this,NewQuestion.class);
				startActivity(i);
			}
		});
        
        if(Utilities.getCurUserType()==Utilities.UserType.USER_TYPE_IPM)
        {
        	btnNewQues.setEnabled(false);
        	btnNewQues.setVisibility(View.GONE);
        }
		doPopulateAllQuestions();
	}

	private void doPopulateAllQuestions() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForTag(Utilities.getCategory()));
		
		if(Utilities.hasCurTagQuesLoaded())
			haveAllQuestions = true;
		if(haveAllQuestions)
		{
			query.fromLocalDatastore();
			query.addAscendingOrder("createdAt");
		}
		
	    query.findInBackground(new FindCallback<ParseObject>() {
	 
	        @Override
	        public void done(List<ParseObject> postList, ParseException e) {
	        	tvInfo.setText("All questions loaded!");
	        	Utilities.saveCurTagQuestions(postList);
	            if (e == null) {
	            	list.clear();
	            	int index = 0, totSize = postList.size() - 1;
	            	String temp;
	            	//boolean isAnon = Utilities.getObjectByTagName(Utilities.getCategory()).getBoolean(Utilities.alias_TAGISANON);
	            	//Utilities.storeAllAnswers(postList);
	            	for(ParseObject tag: postList)
	            	{
	            		temp = tag.getString(Utilities.alias_QBY);
	            		list.add(new CustomListItem("...",tag.getString(Utilities.alias_QTITLE)));
	            		if(index < totSize)
	            			setListNameforusername(index, temp, false);//, isAnon);
	            		else
	            			setListNameforusername(index, temp, true);//, isAnon);
	            		++index;
	            	}
	            	ParseObject.pinAllInBackground(postList, new SaveCallback(){
						@Override
						public void done(ParseException arg0) {
							haveAllQuestions = true;
							Utilities.setCurTagQuesLoaded();
						}
	            	});
	                adapter.notifyDataSetChanged();
	            } else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
	        }
	    });
	}
	
	protected void setListNameforusername(final int index, String user, final boolean triggerUpdate)/*, final boolean isAnon)*/ {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("username", user);
		if(haveAllQuestions)
			query.fromLocalDatastore();
		query.findInBackground(new FindCallback<ParseUser>() {
		     public void done(List<ParseUser> objects, ParseException e) {
		         if (e == null) {
		        	 ParseUser usr = objects.get(0);
		        	 /*boolean isTL = usr.getEmail().endsWith(Utilities.IPM_EMAIL_SUFFIX);
		        	 if(isAnon && isTL)
		        		 list.get(index).setName("...");
		        	 else
		        		 */
		        	 usr.pinInBackground();
		        	 list.get(index).setName(usr.getString(Utilities.alias_UFULLNAME));
		             if(triggerUpdate)
		             {
		            	 adapter.notifyDataSetChanged();
		             }
		         } else {
		             Log.d("Category Nav", e.getMessage());
		         }
		     }
		 });
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
			Utilities.logOutCurUser();
			Intent i = new Intent(CategoryNav.this,MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
			finish();
		}
		else if(id == R.id.action_refresh)
		{
			Toast.makeText(CategoryNav.this, "Refreshing...", Toast.LENGTH_SHORT).show();
			doPopulateAllQuestions();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
		doPopulateAllQuestions();
	}
}
