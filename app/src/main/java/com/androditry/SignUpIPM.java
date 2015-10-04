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
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpIPM extends ActionBarActivity {
	
	TextView tvFullNameStart, tvTerms, tvEmail, tvEmStart, tvEmEnd, tvPass, tvConfPass;
	EditText etEmail, etPass, etRepass, etFullNameEnd;
	Button btnSignUp;
	CheckBox cbTerms;
    ProgressDialog pd;

    String username, password, repass, email, name;
    boolean isCbChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_ipm);
        setTitle("Kriger Campus");
		
		etEmail         = (EditText) findViewById(R.id.etEmaleSignUpIPM);
		etPass          = (EditText) findViewById(R.id.etPassSignUpIPM);
		etRepass        = (EditText) findViewById(R.id.etRePassSignUpIPM);
		tvFullNameStart = (TextView) findViewById(R.id.tvFullNameStartIPM);
		etFullNameEnd   = (EditText) findViewById(R.id.etFullNameEndSignUpIPM);
		tvTerms         = (TextView) findViewById(R.id.tvTermsIPM);
		cbTerms         = (CheckBox) findViewById(R.id.cbTermsIPM);
        tvEmail         = (TextView) findViewById(R.id.tvEmailSignupIPM);
        tvEmStart       = (TextView) findViewById(R.id.tvEmailStartIPMSignUp);
        tvEmEnd         = (TextView) findViewById(R.id.tvEmailEndIPMSignUp);
        tvPass          = (TextView) findViewById(R.id.tvPassSignUpIPM);
        tvConfPass      = (TextView) findViewById(R.id.tvConfPassSignUpIPM);
        btnSignUp       = (Button)   findViewById(R.id.btnSignupIPM);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
        etEmail.setTypeface(face);
        etPass.setTypeface(face);
        tvEmail.setTypeface(face);
        tvPass.setTypeface(face);
        etRepass.setTypeface(face);
        tvFullNameStart.setTypeface(face);
        etFullNameEnd.setTypeface(face);
        tvTerms.setTypeface(face);
        tvEmStart.setTypeface(face);
        tvEmEnd.setTypeface(face);
        tvConfPass.setTypeface(face);
        btnSignUp.setTypeface(face);
		
		//Make the textView respond to user click on Link.
		tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
		
		etEmail.setOnFocusChangeListener(new OnFocusChangeListener() {

		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    /* When focus is lost check that the text field
		    * has valid values.
		    */
		      if (!hasFocus) {
		    	  String tmp = etEmail.getText().toString();
		    	  if(tmp.length()>2)
		    	  {
		    		  String tmp2 = tmp.substring(2);
			    	  char t = Character.toUpperCase(tmp.charAt(tmp.length()-1));
			    	  tmp2 = Character.toUpperCase(tmp2.charAt(0)) + tmp2.substring(1, tmp2.length() - 1);
			    	  tvFullNameStart.setText(tmp2 + " " + t);
		    	  }
		    	  else
		    	  {
		    		  tvFullNameStart.setText("");
		    	  }
		      }
		    }
		 });

		btnSignUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				username = etEmail.getText().toString();
                password = etPass.getText().toString();
                repass   = etRepass.getText().toString();
                email    = username;
                name     = tvFullNameStart.getText().toString()
                			    + etFullNameEnd.getText().toString();
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
        PASS_NOMATCH,
        TERMS_UNAGREED,
        EXCEPTION_THROWN
    }

    String errMsg;
    class SignUpTask extends AsyncTask<Void,Boolean, SignUpTaskState> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(SignUpIPM.this);
            pd.setMessage("Please wait while signing up...");
            pd.show();
        }

        @Override
        protected SignUpTaskState doInBackground(Void... params) {
            publishProgress(false);
            pd.setMessage("Signing Up...");

            if (!Utilities.isNetworkAvailable(SignUpIPM.this)) {
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
                    username = "i" + username;
                    email = "i" + email + "@iimidr.ac.in";

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
                Toast.makeText(SignUpIPM.this, "Sign Up Successful!\nCheck your e-mail for verification link!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpIPM.this, LoginIPM.class);
                startActivity(intent);
            }
            else if (state == SignUpTaskState.NO_INTERNET) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
                builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (state == SignUpTaskState.EMPTY_SIGNUP) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
                builder.setMessage(R.string.signup_error_message)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if(state == SignUpTaskState.PASS_NOMATCH)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
                builder.setMessage(R.string.pass_no_match_error_msg)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if (state == SignUpTaskState.TERMS_UNAGREED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
                builder.setMessage("Please read and agree to our terms and conditions before signing up.")
                        .setTitle("Agree to terms...")
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if (state == SignUpTaskState.EXCEPTION_THROWN) {
                // Fail
                String errorMsg = errMsg.replace("parameters", "Email ID or password");
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
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
