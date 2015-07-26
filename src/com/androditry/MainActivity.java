package com.androditry;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends ActionBarActivity {
	
	ImageButton enterImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Welcome | Kriger Campus");
        
        enterImageBtn = (ImageButton) findViewById(R.id.enterImageBtn);
        enterImageBtn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,SchoolSelect.class);
				startActivity(i);
			}
        	
        });
        
        
        if(Utilities.checkLoggedInUser()==true)
        {
        	Intent i;
        	Utilities.UserType utype = Utilities.getCurUserType();
        	switch(utype)
        	{
        		case USER_TYPE_IPM:
        			i = new Intent(MainActivity.this,HomeScreenIPM.class);
        			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                	finish();
        			break;
        		case USER_TYPE_SCHOOL:
        			i = new Intent(MainActivity.this,HomeScreenSchool.class);
        			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                	finish();
        			break;
        		case USER_TYPE_NONE:
        			Utilities.logOutCurUser();
        			break;
        	}
        }
   
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        return super.onOptionsItemSelected(item);
    }
}
