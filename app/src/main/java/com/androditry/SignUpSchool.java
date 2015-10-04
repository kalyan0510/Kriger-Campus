package com.androditry;

import com.parse.ParseUser;
import com.parse.ParseException;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpSchool extends ActionBarActivity {
	
	TextView tvTerms, tvFullName, tvEmail, tvUsername, tvPass, tvConfPass;
	EditText etEmail,etPass,etName,etUsername,etRePass;
	Button btnSignUp;
	CheckBox cbTerms;
    ProgressDialog pd;

    String username, password, repass, email, name;
    boolean isCbChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_school);
        setTitle("Kriger Campus");
		
		etEmail    = (EditText) findViewById(R.id.etEmaleSignUpSchool);
		etPass     = (EditText) findViewById(R.id.etPassSignUpSchool);
		etName     = (EditText) findViewById(R.id.etFullnameSignUpSchool);
		etUsername = (EditText) findViewById(R.id.etUsernameSignUpSchool);
		etRePass   = (EditText) findViewById(R.id.etRePassSignUpSchool);
		btnSignUp  = (Button)   findViewById(R.id.btnSignupSchool);
		tvTerms    = (TextView) findViewById(R.id.tvTermsSchool);
		cbTerms    = (CheckBox) findViewById(R.id.cbTermsSchool);
        tvFullName = (TextView) findViewById(R.id.tvFullNameSignUpSchool);
        tvEmail    = (TextView) findViewById(R.id.tvEmailSignUpSchool);
        tvUsername = (TextView) findViewById(R.id.tvUsernameSignUpSchool);
        tvPass     = (TextView) findViewById(R.id.tvPassSignUpSchool);
        tvConfPass = (TextView) findViewById(R.id.tvConfPassSignUpSchool);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
        etEmail.setTypeface(face);
        etPass.setTypeface(face);
        tvEmail.setTypeface(face);
        tvPass.setTypeface(face);
        etName.setTypeface(face);
        etUsername.setTypeface(face);
        etRePass.setTypeface(face);
        tvTerms.setTypeface(face);
        tvFullName.setTypeface(face);
        tvUsername.setTypeface(face);
        tvConfPass.setTypeface(face);
        btnSignUp.setTypeface(face);
		
		//Make the textView respond to user click on Link.
		tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
		
		btnSignUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				username = etUsername.getText().toString();
                password = etPass.getText().toString();
                repass = etRePass.getText().toString();
                email = etEmail.getText().toString();
                name = etName.getText().toString();
                isCbChecked = cbTerms.isChecked();

                new SignUpTask().execute();
            }
        });
	}


    private enum SignUpTaskState
    {
        SUCCESS,
        NO_INTERNET,
        EMPTY_SIGNUP,
        EMAIL_UNALLOWED,
        PASS_NOMATCH,
        TERMS_UNAGREED,
        EXCEPTION_THROWN
    }

    String errMsg;
    class SignUpTask extends AsyncTask<Void,Boolean, SignUpTaskState> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(SignUpSchool.this);
            pd.setMessage("Please wait while signing up...");
            pd.show();
        }

        @Override
        protected SignUpTaskState doInBackground(Void... params) {
            publishProgress(false);
            pd.setMessage("Signing up...");

            if (!Utilities.isNetworkAvailable(SignUpSchool.this)) {
                return SignUpTaskState.NO_INTERNET;
            }

            username = username.trim();
            password = password.trim();
            repass = repass.trim();
            email = email.trim();
            name = name.trim();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty())
            {
                return SignUpTaskState.EMPTY_SIGNUP;
            }
            else if(email.endsWith(Utilities.IPM_EMAIL_SUFFIX)
                    && email.startsWith(Utilities.IPM_EMAIL_PREFIX))
            {
                return SignUpTaskState.EMAIL_UNALLOWED;
            }
            else if (!password.equals(repass))
            {
                return SignUpTaskState.PASS_NOMATCH;
            }
            else if (!isCbChecked)
            {
                return SignUpTaskState.TERMS_UNAGREED;
            }
            else {
                try {
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put("Name", name);
                    newUser.signUp();

                    Utilities.InitialiseUserDetails(username);
                } catch (ParseException e) {
                    e.printStackTrace();
                    errMsg = e.getMessage();
                    return SignUpTaskState.EXCEPTION_THROWN;
                }
            }
            return SignUpTaskState.SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Boolean... isEnabled) {
            btnSignUp.setEnabled(isEnabled[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        @Override
        protected void onPostExecute(SignUpTaskState state) {
            pd.setMessage("");
            pd.dismiss();

            // refresh UI
            if (state == SignUpTaskState.SUCCESS) {
                // Success!
                Toast.makeText(SignUpSchool.this, "Sign Up Successful!\nCheck your e-mail for verification link!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpSchool.this, LoginSchool.class);
                startActivity(intent);
            }
            else if (state == SignUpTaskState.NO_INTERNET) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (state == SignUpTaskState.EMPTY_SIGNUP) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                builder.setMessage(R.string.signup_error_message)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (state == SignUpTaskState.EMAIL_UNALLOWED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                builder.setMessage("IPM email is only allowed for Thought Leaders.\nPlease use any other email for signing up for a Student account.")
                        .setTitle("Email NOT allowed!")
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if(state == SignUpTaskState.PASS_NOMATCH)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                builder.setMessage(R.string.pass_no_match_error_msg)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if (state == SignUpTaskState.TERMS_UNAGREED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                builder.setMessage("Please read and agree to our terms and conditions before signing up.")
                        .setTitle("Agree to terms...")
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if (state == SignUpTaskState.EXCEPTION_THROWN) {
                // Fail
                String errorMsg = errMsg.replace("parameters", "Email ID or password");
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                builder.setMessage(errorMsg)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            btnSignUp.setEnabled(true);
        }
    }
}
