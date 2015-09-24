package com.androditry;

import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreenSchool extends ActionBarActivity {
	
	TextView tvInfo;
    GridView grid;
    CustomGridView adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen_school);

		tvInfo    = (TextView) findViewById(R.id.tvUserHomeInfoSchool);
		grid      = (GridView) findViewById(R.id.grid);
        Utilities.CalcScreenWH(this);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
        Utilities.FontTypeFace = face;
        tvInfo.setTypeface(face);

        adapter = new CustomGridView(this, Utilities.AllCategoriesNamesString,
                                                        Utilities.AllCategoriesImagesIds);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(HomeScreenSchool.this, "U clicked on " + position,
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(HomeScreenSchool.this, "U choose" + adapter.getString(position),
                        Toast.LENGTH_SHORT).show();
                Utilities.setCategory(adapter.getString(position));
                Intent i = new Intent(HomeScreenSchool.this, CategoryNav.class);
                startActivity(i);
            }
        });

        if(Utilities.getCurrentUser()==null)
        {
            Intent i = new Intent(HomeScreenSchool.this,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else
        {
            String title = Utilities.getCurName();
            setTitle(title);
        }

    }
/*

    class CheckNotificationsTask extends AsyncTask<Void,Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean ret = false;
            for(CustomCatListItem interest : list)
            {
                //Toast.makeText(this, "notifications checking...", Toast.LENGTH_SHORT).show();
                interest.setNumNotifications(0);
                String interestName = interest.getName();
                ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.getClassNameForTag(interestName));
                query.whereEqualTo(Utilities.alias_QBY, Utilities.getCurUsername());

                try {
                    List<ParseObject> postList = query.find();
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
                        int t = 0;/*
                        for(CustomCatListItem obj : list)
                        {
                            if(obj.getName().equals(str))
                            {
                                list.get(t).setNumNotifications(numNotif);
                                break;
                            }
                            ++t;
                        }*/
    /*                }
                    ret = true;
                } catch (ParseException e) {
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
                adapter.notifyDataSetChanged();
        }
    }*/

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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Utilities.logOutCurUser();
                    Intent i = new Intent(HomeScreenSchool.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    /*HomeScreenSchool.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HomeScreenSchool.this.finish();
                        }
                    });*/
                }
            }).start();
		}
		else if(id == R.id.action_refresh)
		{
			Toast.makeText(HomeScreenSchool.this, "Refreshing...", Toast.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
