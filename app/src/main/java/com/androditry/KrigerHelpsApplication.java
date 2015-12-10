package com.androditry;

import com.parse.Parse;
import com.parse.ParseUser;

import android.app.Application;

public class KrigerHelpsApplication extends Application{

	// Krigermotivation keys
	static final String APPLICATION_ID="vYqrSskYroBI5OPi8NUQpF2ke4fpNRaJgj8Slab5";
	static final String CLIENT_KEY="rVrswITftDTPHZxaIlBeekPgaycUdRn6My6yIpL6";


	//Krigercampus keys
	/*
	static final String APPLICATION_ID="odIPfeae9qYSoQMg0qMXtzfcWkXLFNNdzK2dhoiv";
	static final String CLIENT_KEY="iSIT7mJCmZFkxHvUEpk6QlXxolWxze5hBVjcnpDr";
	*/
	@Override
	public void onCreate() {
	    super.onCreate();

	    Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
	    //ParseUser.enableRevocableSessionInBackground();
	}
}
