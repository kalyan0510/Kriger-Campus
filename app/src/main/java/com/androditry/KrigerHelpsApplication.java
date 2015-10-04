package com.androditry;

import com.parse.Parse;
import com.parse.ParseUser;

import android.app.Application;

public class KrigerHelpsApplication extends Application{
	static final String APPLICATION_ID="he0MHkv9kmsLr1p6CENzYOydf4GlhKslAPmgcVkv";
	static final String CLIENT_KEY="W9PGIpdmCOqPgM5aA4BJfwQvQnYp64nC6OiNREao";
	
	@Override
	public void onCreate() {
	    super.onCreate();

	    Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
	    //ParseUser.enableRevocableSessionInBackground();
	}
}
