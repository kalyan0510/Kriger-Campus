package com.androditry;

import java.util.ArrayList;
import java.util.List;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreenSchoolOld extends ActionBarActivity {

    TextView tvInfo;
    ListView lvUserCat;

    ArrayList<CustomCatListItem> list = new ArrayList<>();
    CustomCatListAdapter adapter;

	/*/private Timer timer;
    private TimerTask timerTask;
    private static final long whenToStart = 30*1000L; // 30 seconds
    private static final long howOften = 30*1000L; // 30 seconds*/

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

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
        tvInfo.setTypeface(face);

        lvUserCat.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Utilities.setCategory(list.get(position).getName());
                Intent i = new Intent(HomeScreenSchoolOld.this, CategoryNav.class);
                startActivity(i);
            }
        });

        if(Utilities.getCurrentUser()==null)
        {
            Intent i = new Intent(HomeScreenSchoolOld.this,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else
        {
            String title = Utilities.getCurName();
            HomeScreenSchoolOld.this.setTitle(title);
            tvInfo.setText("Hello " + title + "!  Please wait...\nLoading all Interests...");
            Utilities.CheckUpdateSubscriptionInBackground();
            new UpdateCategoriesTask().execute(true);
            //btnAllCat.setEnabled(false);
        }
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                new UpdateCategoriesTask().execute(true);
            }
        }).start();*/

    }

    private enum UpdateCategoriesTaskState
    {
        SUCCESS,
        NO_INTERNET,
        EXCEPTION_THROWN
    }

    class UpdateCategoriesTask extends AsyncTask<Boolean,String, UpdateCategoriesTaskState> {
        @Override
        protected UpdateCategoriesTaskState doInBackground(Boolean... params) {
            boolean forceUpdate = params[0];

            if(!Utilities.isNetworkAvailable(HomeScreenSchoolOld.this)
                    && (!storedAllInterests || forceUpdate))
            {
                publishProgress("The list of interests could not be updated!\nCheck your network!");
                return UpdateCategoriesTaskState.NO_INTERNET;
            }

            if(forceUpdate)
            {
                try {
                    ParseQuery<ParseObject> tquery = ParseQuery.getQuery(Utilities.AllClassesNames.AllTagsList);
                    tquery.fromLocalDatastore();
                    List<ParseObject> objs = tquery.find();
                    ParseObject.unpinAll(objs);
                } catch (ParseException e1) {
                    //Toast.makeText(this, "Error while unpinning objects.", Toast.LENGTH_SHORT).show();
                    e1.printStackTrace();
                }
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery(Utilities.AllClassesNames.AllTagsList);
            query.addAscendingOrder(Utilities.alias_TAGDISPORDER);
            if(storedAllInterests && !forceUpdate)
                query.fromLocalDatastore();
            try {
                List<ParseObject> postList = query.find();
                publishProgress("All interests loaded!");
                list.clear();
                //Utilities.storeAllTags(postList);
                for (ParseObject tag : postList) {
                    String tagName = tag.getString(Utilities.alias_TAGNAME).replace('_', ' ');
                    CustomCatListItem item = new CustomCatListItem(tagName, 0, tag.getBoolean(Utilities.alias_TAGISANON));
                    list.add(item);
                }
                ParseObject.pinAllInBackground(postList, new SaveCallback() {
                    @Override
                    public void done(ParseException arg0) {
                        storedAllInterests = true;
                        Utilities.haveAllTags = true;
                    }
                });
            } catch (ParseException e) {
                publishProgress("An error occurred. Please try refresh. If interests still don't load then please logout and login again!");
                //Toast.makeText(HomeScreenSchoolOld.this, "No tags could be loaded due to error: \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
                e.printStackTrace();
                return UpdateCategoriesTaskState.EXCEPTION_THROWN;
            }

            return UpdateCategoriesTaskState.SUCCESS;
        }

        @Override
        protected void onProgressUpdate(String... text) {
            tvInfo.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        @Override
        protected void onPostExecute(UpdateCategoriesTaskState state) {
            // refresh UI
            if(state == UpdateCategoriesTaskState.SUCCESS)
            {
                adapter.notifyDataSetChanged();
                new CheckNotificationsTask().execute();
            }
            else if(state == UpdateCategoriesTaskState.NO_INTERNET)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenSchoolOld.this);
                builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

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
                    Intent i = new Intent(HomeScreenSchoolOld.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    /*HomeScreenSchoolOld.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HomeScreenSchoolOld.this.finish();
                        }
                    });*/
                }
            }).start();
        }
        else if(id == R.id.action_refresh)
        {
            Toast.makeText(HomeScreenSchoolOld.this, "Refreshing...", Toast.LENGTH_SHORT).show();
            new UpdateCategoriesTask().execute(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();/*
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                new UpdateCategoriesTask().execute(false);
            }
        };
        timer.scheduleAtFixedRate(task, whenToStart, howOften);*/

        new UpdateCategoriesTask().execute(false);
    }

}
