package com.androditry;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginSchool extends ActionBarActivity {
	
	EditText etUsername, etPass;
    TextView tvUName, tvPass, tvNewUsr;
	Button btnLoginSchool, btnSignUpSchool;
    ProgressDialog pd;

    String username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_school);
        setTitle("Kriger Campus");


      final Button resetbtn=(Button)findViewById(R.id.reset_pwd);
        resetbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(LoginSchool.this,reset.class);
                startActivity(i);

            }
        });
		
		etUsername = (EditText) findViewById(R.id.etUsernameLoginSchool);
		etPass  = (EditText) findViewById(R.id.etPassLoginSchool);

        tvUName = (TextView) findViewById(R.id.tvUNameSchool);
        tvPass = (TextView) findViewById(R.id.tvPassSchool);
        tvNewUsr = (TextView) findViewById(R.id.tvNewUserSchool);
		
		btnLoginSchool  = (Button) findViewById(R.id.btnLoginSchool);
		btnSignUpSchool = (Button) findViewById(R.id.btnStartSignupSchool);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
        etUsername.setTypeface(face);
        etPass.setTypeface(face);
        tvUName.setTypeface(face);
        tvPass.setTypeface(face);
        tvNewUsr.setTypeface(face);
        btnLoginSchool.setTypeface(face);
        btnSignUpSchool.setTypeface(face);
		
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
        protected void onPreExecute() {
            pd = new ProgressDialog(LoginSchool.this);
            pd.setMessage("Please wait while logging in...");
            pd.show();
        }

        @Override
        protected LoginTaskState doInBackground(Void... params) {
            publishProgress(false);
            pd.setMessage("Logging in...");

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
                    //e.printStackTrace();
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
            pd.setMessage("");
            pd.dismiss();

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
