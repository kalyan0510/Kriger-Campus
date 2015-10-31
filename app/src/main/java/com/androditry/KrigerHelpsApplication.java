package com.androditry;

import com.parse.Parse;
import com.parse.ParseUser;

import android.app.Application;

public class KrigerHelpsApplication extends Application{
	static final String APPLICATION_ID="vYqrSskYroBI5OPi8NUQpF2ke4fpNRaJgj8Slab5";
	static final String CLIENT_KEY="rVrswITftDTPHZxaIlBeekPgaycUdRn6My6yIpL6";
	
	@Override
	public void onCreate() {
	    super.onCreate();

	    Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
	    //ParseUser.enableRevocableSessionInBackground();
	}
}
