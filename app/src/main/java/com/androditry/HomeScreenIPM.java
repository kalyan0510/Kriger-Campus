package com.androditry;

import android.app.AlertDialog;
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

public class HomeScreenIPM extends ActionBarActivity {

    TextView tvInfo;
    GridView grid;
    CustomGridView adapter;

    Timer timer;
    private static final long whenToStart = 0L; // 0 seconds
    private static final long howOften = 80*1000L; // 80 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_ipm);

        tvInfo    = (TextView) findViewById(R.id.tvUserHomeInfoIPM);
        grid      = (GridView) findViewById(R.id.gridIPM);
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

                if(Utilities.isNetworkAvailable(HomeScreenIPM.this))
                {
                    Utilities.setCategory(adapter.getString(position));
                    Intent i = new Intent(HomeScreenIPM.this, CategoryNav.class);
                    startActivity(i);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenIPM.this);
                    builder.setMessage(R.string.no_internet_msg)
                            .setTitle(R.string.no_internet_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        if(Utilities.getCurrentUser()==null)
        {
            Intent i = new Intent(HomeScreenIPM.this,MainActivity.class);
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
                query.whereEqualTo(Utilities.alias_QNUMANSWERS, 0);

                try {
                    List<ParseObject> postList = query.find();
                    int numNotif = postList.size();
                    if(numNotif > 0)
                    {
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Utilities.logOutCurUser();
                    Intent i = new Intent(HomeScreenIPM.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }).start();
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
