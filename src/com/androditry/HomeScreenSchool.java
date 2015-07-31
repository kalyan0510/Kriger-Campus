package com.androditry;

import java.util.ArrayList;
import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreenSchool extends ActionBarActivity {
	
	TextView tvInfo;
	ListView lvUserCat;
	
	ArrayList<CustomCatListItem> list = new ArrayList<CustomCatListItem>();
    CustomCatListAdapter adapter;
    
    private boolean storedAllInterests = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen_school);
		
		tvInfo    = (TextView) findViewById(R.id.tvUserHomeInfoSchool);
		lvUserCat = (ListView) findViewById(R.id.lvUserCategoriesSchool);
		
		 adapter = new CustomCatListAdapter(this, list);
	     lvUserCat.setAdapter(adapter);
	     adapter.notifyDataSetChanged();
	     
	     lvUserCat.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Utilities.setCategory(list.get(position).getName());
				Intent i = new Intent(HomeScreenSchool.this,CategoryNav.class);
				startActivity(i);
			}
	     });
		
		if(Utilities.getCurrentUser()==null)
		{
			Intent i = new Intent(HomeScreenSchool.this,MainActivity.class);
			startActivity(i);
			finish();
		}
		else
		{
			String title = Utilities.getCurName();
			this.setTitle(title);
			tvInfo.setText("Hello " + title + "!  Please wait...\nLoading all Interests...");
			//btnAllCat.setEnabled(false);
			doPopulateListView(true);
		}
		
		Utilities.CheckUpdateSubscriptionInBackground();
    }
	
	private void doPopulateListView(boolean forceUpdate)
	{
		if(!Utilities.isNetworkAvailable(HomeScreenSchool.this)
				&& (!storedAllInterests || forceUpdate))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenSchool.this);
            builder.setMessage(R.string.no_internet_msg)
                .setTitle(R.string.no_internet_title)
                .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
            tvInfo.setText("The list of interests could not be updated!\nCheck your network!");
            return;
		}
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.AllTagsList);
		if(storedAllInterests)
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
		
		query.addAscendingOrder(Utilities.alias_TAGDISPORDER);
	    query.findInBackground(new FindCallback<ParseObject>() {
	 
	        @Override
	        public void done(List<ParseObject> postList, ParseException e) {
	        	
	            if (e == null) {
	            	tvInfo.setText("All interests loaded!");
	            	list.clear();
	            	Utilities.storeAllTags(postList);
	            	for(ParseObject tag: postList)
	            	{
	            		String tagName = tag.getString(Utilities.alias_TAGNAME).replace('_', ' ');
	            		CustomCatListItem item = new CustomCatListItem(tagName, 0, tag.getBoolean(Utilities.alias_TAGISANON));
	            		list.add(item);
	            	}
	            	ParseObject.pinAllInBackground(postList, new SaveCallback()
	            	{
						@Override
						public void done(ParseException arg0) {
							storedAllInterests = true;
							Utilities.haveAllTags = true;
						}
	            	});
	                adapter.notifyDataSetChanged();
	            	doCheckForNotifications();
	            } else {
	            	tvInfo.setText("An error occured. Please try refresh. If interests still don't load then please logout and login again!");
	            	Toast.makeText(HomeScreenSchool.this, "No tags could be loaded due to error: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
	        }
	    });
	}
	
	private void doCheckForNotifications()
	{
		for(CustomCatListItem interest : list)
		{
			//Toast.makeText(this, "notifications checking...", Toast.LENGTH_SHORT).show();
			interest.setNumNotifications(0);
			String interestName = interest.getName();
			ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForTag(interestName));
			query.whereEqualTo(Utilities.alias_QBY, Utilities.getCurUsername());
			query.findInBackground(new FindCallback<ParseObject>() {
				 
		        @Override
		        public void done(List<ParseObject> postList, ParseException e) {
		            if (e == null) {
		            	int n = postList.size();
		            	if(n > 0)
		            	{
		            		String str = Utilities.AllClassesNames.getTagNameForClass(postList.get(0).getClassName());
		            		str = str.replace('_', ' ');
		            		int numNotif = 0;
		            		for(ParseObject pQues : postList)
		            		{
		            			if(pQues.getInt(Utilities.alias_QNUMANSWERS) > pQues.getInt(Utilities.alias_QNUMANSSEEN))
		            				++numNotif;
		            		}
		            		
		            		int t = 0;
		            		for(CustomCatListItem obj : list)
		            		{
		            			if(obj.getName().equals(str))
		            			{
		            				list.get(t).setNumNotifications(numNotif);
		            				break;
		            			}
		            			++t;
		            		}
		            	}
		            	
		            	adapter.notifyDataSetChanged();
		            } else {
		                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
		            }
		        }
		    });
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen_school, menu);
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
			Intent i = new Intent(HomeScreenSchool.this,MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
			finish();
		}
		else if(id == R.id.action_refresh)
		{
			Toast.makeText(HomeScreenSchool.this, "Refreshing...", Toast.LENGTH_SHORT).show();
			doPopulateListView(true);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		doPopulateListView(false);
	}
}
