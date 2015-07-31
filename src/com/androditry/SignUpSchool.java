package com.androditry;

import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ParseException;

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
	
	TextView tvTerms;
	EditText etEmail,etPass,etName,etUsername,etRePass;
	Button btnSignUp;
	CheckBox cbTerms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_school);
		setTitle("Sign Up | Kriger Campus");
		
		etEmail    = (EditText) findViewById(R.id.etEmaleSignUpSchool);
		etPass     = (EditText) findViewById(R.id.etPassSignUpSchool);
		etName     = (EditText) findViewById(R.id.etFullnameSignUpSchool);
		etUsername = (EditText) findViewById(R.id.etUsernameSignUpSchool);
		etRePass   = (EditText) findViewById(R.id.etRePassSignUpSchool);
		btnSignUp  = (Button)   findViewById(R.id.btnSignupSchool);
		tvTerms    = (TextView) findViewById(R.id.tvTermsSchool);
		cbTerms    = (CheckBox) findViewById(R.id.cbTermsSchool);
		
		//Make the textView respond to user click on Link.
		tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
		
		btnSignUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!Utilities.isNetworkAvailable(SignUpSchool.this))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                    builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
            		return;
				}
				
				String username = etUsername.getText().toString();
                String password = etPass.getText().toString();
                String repass = etRePass.getText().toString();
                String email = etEmail.getText().toString();
                String name = etName.getText().toString();
 
                username = username.trim();
                password = password.trim();
                repass = repass.trim();
                email = email.trim();
                name.trim();
 
                if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                    builder.setMessage(R.string.signup_error_message)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if(email.endsWith(Utilities.IPM_EMAIL_SUFFIX)
                		&& email.startsWith(Utilities.IPM_EMAIL_PREFIX))
                {
                	AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                    builder.setMessage("IPM email is only allowed for Thought Leaders.\nPlease use any other email for signing up for a Student account.")
                        .setTitle("Email NOT allowed!")
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (!password.equals(repass)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                    builder.setMessage(R.string.pass_no_match_error_msg)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (!cbTerms.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                    builder.setMessage("Please read and agree to our terms and conditions before signing up.")
                        .setTitle("Agree to terms...")
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
 
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put("Name", name);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                        	
                            if (e == null) {
                            	Toast.makeText(SignUpSchool.this, "Sign Up Successful!\nCheck your e-mail for verification link!", Toast.LENGTH_SHORT).show();
                            	Utilities.InitialiseUserDetails(etUsername.getText().toString().trim());
                                // Success!
                                Intent intent = new Intent(SignUpSchool.this, LoginSchool.class);
                                startActivity(intent);
                            }
                            else {
                            	String errorMsg = e.getMessage().replace("parameters", "Email ID or password");
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpSchool.this);
                                builder.setMessage(errorMsg)
                                    .setTitle(R.string.signup_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
		});
	}
}
