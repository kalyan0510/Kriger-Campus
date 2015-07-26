package com.androditry;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ViewAllCategories extends ActionBarActivity {
	
	TextView tvInfo;
	ListView lvAllCat;
	Button   btnAllCat;
	
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_all_categories);
		
		tvInfo    = (TextView) findViewById(R.id.tvInfoAllCategories);
		lvAllCat = (ListView) findViewById(R.id.lvAllCategories);
		
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lvAllCat.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
        lvAllCat.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Utilities.setCategory(list.get(position));
				Intent i = new Intent(ViewAllCategories.this,ViewOneCategory.class);
				startActivity(i);
			}
        	
        });
        
        tvInfo.setText("loading all the tags' names from cloud...");
        
        doPopulateListView();
		
	}
	
	private void doPopulateListView()
	{
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.AllTagsList);
		 
	    query.findInBackground(new FindCallback<ParseObject>() {
	 
	        @Override
	        public void done(List<ParseObject> postList, ParseException e) {
	        	tvInfo.setText("All tags loaded!");
	        	
	            if (e == null) {
	            	list.clear();
	            	Utilities.storeAllTags(postList);
	            	for(ParseObject tag: postList)
	            	{
	            		list.add(tag.getString(Utilities.alias_TAGNAME));
	            	}
	                adapter.notifyDataSetChanged();
	            } else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_all_categories, menu);
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
			Intent i = new Intent(ViewAllCategories.this,MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
