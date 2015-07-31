package com.androditry;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginSchool extends ActionBarActivity {
	
	EditText etUsername,etPass;
	Button btnLoginSchool,btnSignUpSchool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_school);
		setTitle("Login | Kriger Campus");
		
		etUsername = (EditText) findViewById(R.id.etUsernameLoginSchool);
		etPass  = (EditText) findViewById(R.id.etPassLoginSchool);
		
		btnLoginSchool  = (Button) findViewById(R.id.btnLoginSchool);
		btnSignUpSchool = (Button) findViewById(R.id.btnStartSignupSchool);
		
		btnLoginSchool.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!Utilities.isNetworkAvailable(LoginSchool.this))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginSchool.this);
                    builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
            		return;
				}
				
				String username = etUsername.getText().toString();
                String password = etPass.getText().toString();
 
                username = username.trim();
                password = password.trim();
 
                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginSchool.this);
                    builder.setMessage(R.string.login_error_message)
                        .setTitle(R.string.login_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginSchool.this);
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
                                    Intent intent = new Intent(LoginSchool.this, HomeScreenSchool.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                            	}
                            }
                            else {
                                // Fail
                            	String errorMsg = e.getMessage().replace("parameters", "Email ID or password");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginSchool.this);
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
		
		btnSignUpSchool.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginSchool.this,SignUpSchool.class);
				startActivity(i);
			}
		});
	}
}
