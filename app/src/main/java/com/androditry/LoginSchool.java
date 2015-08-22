package com.androditry;

import com.parse.ParseException;
import com.parse.ParseUser;

import android.os.AsyncTask;
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

    String username, password;

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
                  username = etUsername.getText().toString();
                  password = etPass.getText().toString();

                  new LoginTask().execute();
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


    private enum LoginTaskState
    {
        SUCCESS,
        NO_INTERNET,
        EMPTY_LOGIN,
        EMAIL_UNVERIFIED,
        EXCEPTION_THROWN
    }

    String errMsg;
    class LoginTask extends AsyncTask<Void,Boolean, LoginTaskState> {

        @Override
        protected LoginTaskState doInBackground(Void... params) {
            publishProgress(false);

            if (!Utilities.isNetworkAvailable(LoginSchool.this)) {
                return LoginTaskState.NO_INTERNET;
            }
            username = username.trim();
            password = password.trim();
            if (username.isEmpty() || password.isEmpty()) {
                return LoginTaskState.EMPTY_LOGIN;
            } else {
                try {
                    ParseUser user = ParseUser.logIn(username, password);
                    boolean verified = user.getBoolean("emailVerified");
                    if (!verified) {
                        ParseUser.logOut();
                        return LoginTaskState.EMAIL_UNVERIFIED;
                    } else {
                        // Email Verified! :)
                        Utilities.setCurrentUser(user);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    errMsg = e.getMessage();
                    return LoginTaskState.EXCEPTION_THROWN;
                }
            }
            return LoginTaskState.SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Boolean... isEnabled) {
            btnLoginSchool.setEnabled(isEnabled[0]);
            btnSignUpSchool.setEnabled(isEnabled[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        @Override
        protected void onPostExecute(LoginTaskState state) {
            // refresh UI
            if (state == LoginTaskState.SUCCESS) {
                Intent intent = new Intent(LoginSchool.this, HomeScreenSchool.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (state == LoginTaskState.NO_INTERNET) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginSchool.this);
                builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (state == LoginTaskState.EMPTY_LOGIN) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginSchool.this);
                builder.setMessage(R.string.login_error_message)
                        .setTitle(R.string.login_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (state == LoginTaskState.EMAIL_UNVERIFIED) {
                // Not Verified Email!
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginSchool.this);
                builder.setMessage(R.string.verify_email_msg)
                        .setTitle(R.string.verify_email_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (state == LoginTaskState.EXCEPTION_THROWN) {
                // Fail
                String errorMsg = errMsg.replace("parameters", "Email ID or password");
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginSchool.this);
                builder.setMessage(errorMsg)
                        .setTitle(R.string.login_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            btnLoginSchool.setEnabled(true);
            btnSignUpSchool.setEnabled(true);
        }
    }
}
