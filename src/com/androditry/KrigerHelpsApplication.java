package com.androditry;

import com.parse.Parse;
import com.parse.ParseUser;

import android.app.Application;

public class KrigerHelpsApplication extends Application{
	static final String APPLICATION_ID="pdRfBqyvBrT6GpaAFrwKRBxigKFDJ9RIxGx3hPFW";
	static final String CLIENT_KEY="oeDvTojc5AZ89ztCpxihktkzpxsRM9ENxsFcXGIi";
	
	@Override
	public void onCreate() {
	    super.onCreate();
	    
	    Parse.enableLocalDatastore(this);
	    Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
	    ParseUser.enableRevocableSessionInBackground();
	}
}
