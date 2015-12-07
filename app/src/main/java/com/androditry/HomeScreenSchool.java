package com.androditry;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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
    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int notificationID1 = 200;
    private int notificationID2 = 300;
    private int numMessages = 0;

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
                if(Utilities.isNetworkAvailable(HomeScreenSchool.this)==false)
                {
                    Toast.makeText(getApplicationContext(), "NO NETWORK",
                           Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(HomeScreenSchool.this, offline.class);
                    startActivity(i);

                }
                else {
                    Intent i = new Intent(HomeScreenSchool.this, CategoryNav.class);
                    startActivity(i);
                }
            }
        });
        //faq part
        final Button fq=(Button)findViewById(R.id.faq);
        fq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Perform action on click
                Intent i = new Intent(HomeScreenSchool.this, faq.class);
                startActivity(i);

            }
        });
        //notification part
        final Button noti=(Button)findViewById(R.id.notifi);
       noti.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Perform action on click
               displayNotification();

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
            setTitle("Kriger Campus");
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

    //functions for notifications
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void displayNotification() {
        //1st notification
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("important notification 1");
        mBuilder.setContentText("click to view full");
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.drawable.funnparty);
        mBuilder.setNumber(++numMessages);
        Intent resultIntent = new Intent(this, noti.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(noti.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent(  0,PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder.setContentIntent(resultPendingIntent);


        //2nd notification
        NotificationCompat.Builder  mBuilder1 = new NotificationCompat.Builder(this);
        mBuilder1.setContentTitle("important notification 2");
        mBuilder1.setContentText("click to view full");
        mBuilder1.setTicker("New Message Alert!");
        mBuilder1.setSmallIcon(R.drawable.faculty);
        mBuilder1.setNumber(++numMessages);
        Intent resultIntent1 = new Intent(this, noti1.class);
        TaskStackBuilder stackBuilder1 = TaskStackBuilder.create(this);
        stackBuilder1.addParentStack(noti1.class);
        stackBuilder1.addNextIntent(resultIntent1);
        PendingIntent resultPendingIntent1 =  stackBuilder.getPendingIntent(  0,PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder1.setContentIntent(resultPendingIntent1);

        //3rd notification
        NotificationCompat.Builder  mBuilder2 = new NotificationCompat.Builder(this);
        mBuilder2.setContentTitle("important notification 3");
        mBuilder2.setContentText("click to view full");
        mBuilder2.setTicker("New Message Alert!");
        mBuilder2.setSmallIcon(R.drawable.campus);
        mBuilder2.setNumber(++numMessages);
        Intent resultIntent2 = new Intent(this, noti2.class);
        TaskStackBuilder stackBuilder2 = TaskStackBuilder.create(this);
        stackBuilder2.addParentStack(noti2.class);
        stackBuilder2.addNextIntent(resultIntent1);
        PendingIntent resultPendingIntent2 =  stackBuilder.getPendingIntent(  0,PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder2.setContentIntent(resultPendingIntent2);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.build());
        mNotificationManager.notify(notificationID1, mBuilder1.build());
        mNotificationManager.notify(notificationID2, mBuilder2.build());
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
        if(id==R.id.user_profile)
        {
            Intent i = new Intent(HomeScreenSchool.this, profile.class);
            startActivity(i);
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
