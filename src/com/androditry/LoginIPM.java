package com.androditry;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginIPM extends ActionBarActivity {
	
	EditText etEmail,etPass;
	Button btnLoginIPM,btnSignUpIPM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_ipm);
		setTitle("Login | Kriger Campus");
		
		etEmail = (EditText) findViewById(R.id.etEmaleLoginIPM);
		etPass  = (EditText) findViewById(R.id.etPassLoginIPM);
		
		btnLoginIPM  = (Button) findViewById(R.id.btnLoginIPM);
		btnSignUpIPM = (Button) findViewById(R.id.btnStartSignupIPM);
		
		btnLoginIPM.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!Utilities.isNetworkAvailable(LoginIPM.this))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginIPM.this);
                    builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
            		return;
				}
				
				String username = etEmail.getText().toString();
                String password = etPass.getText().toString();
 
                username = username.trim();
                password = password.trim();
 
                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginIPM.this);
                    builder.setMessage(R.string.login_error_message)
                        .setTitle(R.string.login_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    username = "i" + username;
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                // Success!
                            	boolean verified = (boolean)user.getBoolean("emailVerified");
                            	if(verified==false)
                            	{
                            		ParseUser.logOutInBackground();
                            		// Not Verified Email!
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginIPM.this);
                                    builder.setMessage(R.string.verify_email_msg)
                                        .setTitle(R.string.verify_email_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                            		return;
                            	}
                            	else
                            	{
                            		// Email Verified! :)
                            		Utilities.setCurrentUser(user);
                                    Intent intent = new Intent(LoginIPM.this, HomeScreenIPM.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                            	}
                            }
                            else {
                                // Fail
                            	String errorMsg = e.getMessage().replace("parameters", "Email ID or password");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginIPM.this);
                                builder.setMessage(errorMsg)
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
			}
		});
		
		btnSignUpIPM.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginIPM.this,SignUpIPM.class);
				startActivity(i);
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_school, menu);
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
