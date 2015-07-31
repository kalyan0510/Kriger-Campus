package com.androditry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.SaveCallback;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreenIPM extends ActionBarActivity {
	
	TextView tvInfo;
	ListView lvUserCat;
    ArrayList<CustomCatListItem> list = new ArrayList<CustomCatListItem>();
    CustomCatListAdapter adapter;
    
    Timer myTimer;
    private boolean storedAllInterests = false;
    private boolean timerCalledUpdate = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen_ipm);
		
		tvInfo    = (TextView) findViewById(R.id.tvUserHomeInfoIPM);
		lvUserCat = (ListView) findViewById(R.id.lvUserCategoriesIPM);
		
		adapter = new CustomCatListAdapter(this, list);
	    lvUserCat.setAdapter(adapter);
	    adapter.notifyDataSetChanged();
	     
        lvUserCat.setOnItemClickListener(new OnItemClickListener(){
        	@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
        		Utilities.setCategory(list.get(position).getName());
				Intent i = new Intent(HomeScreenIPM.this,CategoryNav.class);
				startActivity(i);
			}
        });
        
        if(Utilities.getCurrentUser()==null)
		{
			Intent i = new Intent(HomeScreenIPM.this,MainActivity.class);
			startActivity(i);
			finish();
		}
		else
		{
			String title = Utilities.getCurName();
			this.setTitle(title);
			tvInfo.setText("Hello " + title + "!  Please wait...\nLoading all Interests...");
			doPopulateListView(true);
		}
        
        Utilities.CheckUpdateSubscriptionInBackground();
        
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {          
            @Override
            public void run() {
                TimerMethod();
            }

        }, 30000, 30000);
    }

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
        	doPopulateListView(true);
        }
    };
	
	private void doPopulateListView(boolean forceUpdate)
	{
		if(!Utilities.isNetworkAvailable(HomeScreenIPM.this)
				&& (!storedAllInterests || forceUpdate))
		{
			if(!timerCalledUpdate)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenIPM.this);
	            builder.setMessage(R.string.no_internet_msg)
	                .setTitle(R.string.no_internet_title)
	                .setPositiveButton(android.R.string.ok, null);
	            AlertDialog dialog = builder.create();
	            dialog.show();
	            tvInfo.setText("The list of interests could not be updated!\nCheck your network!");
			}
    		return;
		}
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.AllTagsList);
		if(storedAllInterests && !forceUpdate)
		{
			query.fromLocalDatastore();
		}
		
		query.addAscendingOrder(Utilities.alias_TAGDISPORDER);
	    query.findInBackground(new FindCallback<ParseObject>() {
	 
	        @Override
	        public void done(List<ParseObject> postList, ParseException e) {
	        	tvInfo.setText("All interests loaded!");
	        	
	            if (e == null) {
	            	list.clear();
	            	Utilities.storeAllTags(postList);
	            	for(ParseObject tag: postList)
	            	{
	            		String tagName = tag.getString(Utilities.alias_TAGNAME).replace('_', ' ');
	            		CustomCatListItem item = new CustomCatListItem(tagName, 0, tag.getBoolean(Utilities.alias_TAGISANON));
	            		list.add(item);
	            	}
	            	ParseObject.pinAllInBackground(postList, new SaveCallback(){
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
	            	Toast.makeText(HomeScreenIPM.this, "No tags could be loaded due to error: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
	        }
	    });
	    timerCalledUpdate = false;
	}
	
	private void doCheckForNotifications()
	{
		for(CustomCatListItem interest : list)
		{
			interest.setNumNotifications(0);
			String interestName = interest.getName();
			ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForTag(interestName));
			query.whereEqualTo(Utilities.alias_QNUMANSWERS, 0);
			query.findInBackground(new FindCallback<ParseObject>() {
				 
		        @Override
		        public void done(List<ParseObject> postList, ParseException e) {
		            if (e == null) {
		            	int n = postList.size();
		            	if(n > 0)
		            	{
		            		String str = Utilities.AllClassesNames.getTagNameForClass(postList.get(0).getClassName());
		            		str = str.replace('_', ' ');
		            		int t = 0;
		            		for(CustomCatListItem obj : list)
		            		{
		            			if(obj.getName().equals(str))
		            			{
		            				list.get(t).setNumNotifications(n);
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
		getMenuInflater().inflate(R.menu.home_screen_ipm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml
		int id = item.getItemId();
		if(id == R.id.action_logout)
		{
			Utilities.logOutCurUser();
			Intent i = new Intent(HomeScreenIPM.this,MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
			finish();
		}
		else if(id == R.id.action_refresh)
		{
			Toast.makeText(HomeScreenIPM.this, "Refreshing...", Toast.LENGTH_SHORT).show();
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
