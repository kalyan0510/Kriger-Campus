package com.androditry;

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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeScreenSchool extends ActionBarActivity {
	
	TextView tvInfo;
    GridView grid;
    CustomGridView adapter;

    Timer timer;
    private static final long whenToStart = 0L; // 0 seconds
    private static final long howOften = 80*1000L; // 80 seconds

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
                                        Utilities.AllCategoriesImagesIds, Utilities.AllCategoriesHaveNotif);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
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

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new CheckNotificationsTask().execute();
            }
        }, whenToStart, howOften);

        Utilities.CheckUpdateSubscriptionInBackground();
    }

    class CheckNotificationsTask extends AsyncTask<Void,Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean ret = false;
            int l = Utilities.AllCategoriesNamesString.length;
            for(int i=0; i<l; ++i)
            {
                Utilities.AllCategoriesHaveNotif[i] = false;
                String interestName = Utilities.AllCategoriesNamesString[i];
                ParseQuery<ParseObject> query = ParseQuery.getQuery(
                                        Utilities.AllClassesNames.getClassNameForTag(interestName));
                query.whereEqualTo(Utilities.alias_QBY, Utilities.getCurUsername());

                try {
                    List<ParseObject> postList = query.find();
                    int n = postList.size();
                    if(n > 0)
                    {
                        int numNotif = 0;
                        for(ParseObject pQues : postList)
                        {
                            if(pQues.getInt(Utilities.alias_QNUMANSWERS) > pQues.getInt(Utilities.alias_QNUMANSSEEN))
                                ++numNotif;
                        }
                        //Utilities.AllCategoriesNumNotif = numNotif;       /*NOT USED*/
                        Utilities.AllCategoriesHaveNotif[i] = numNotif>0;
                    }
                    ret = true;
                } catch (ParseException e) {
                    Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                    e.printStackTrace();
                }
                adapter.setHasNotif(Utilities.AllCategoriesHaveNotif);
            }
            return ret;
        }

        @Override
        public void onPostExecute(Boolean isSuccess)
        {
            if(isSuccess)
                adapter.notifyDataSetChanged();
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
            timer.cancel();
            Utilities.contextLogout = HomeScreenSchool.this;
            new Utilities.LogoutTask().execute();
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
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new CheckNotificationsTask().execute();
            }
        }, whenToStart, howOften);
	}

}
