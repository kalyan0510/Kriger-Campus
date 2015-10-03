package com.androditry;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	
	TextView tvAppMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Welcome | Kriger Campus");
        
        tvAppMain = (TextView) findViewById(R.id.tvAppMain);
        tvAppMain.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,SchoolSelect.class);
				startActivity(i);
			}
        	
        });
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				//do loading data or whatever hard here

				if(Utilities.checkLoggedInUser())
				{
					Intent i;
					Utilities.UserType utype = Utilities.getCurUserType();
					switch(utype)
					{
						case USER_TYPE_IPM:
							i = new Intent(MainActivity.this,HomeScreenIPM.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
							break;

						case USER_TYPE_SCHOOL:
							i = new Intent(MainActivity.this,HomeScreenSchool.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
							break;

						case USER_TYPE_NONE:
							Utilities.logOutCurUser();
					}
				}
			}
		}).start();

   
    }
}
