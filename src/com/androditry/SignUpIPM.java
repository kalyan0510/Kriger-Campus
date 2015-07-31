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
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpIPM extends ActionBarActivity {
	
	TextView tvFullNameStart, tvTerms;
	EditText etEmail, etPass, etRepass, etFullNameEnd;
	Button btnSignUp;
	CheckBox cbTerms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_ipm);
		setTitle("Sign Up | Kriger Campus");
		
		etEmail = (EditText) findViewById(R.id.etEmaleSignUpIPM);
		etPass  = (EditText) findViewById(R.id.etPassSignUpIPM);
		etRepass = (EditText) findViewById(R.id.etRePassSignUpIPM);
		tvFullNameStart = (TextView) findViewById(R.id.tvFullNameStartIPM);
		etFullNameEnd   = (EditText) findViewById(R.id.etFullNameEndSignUpIPM);
		tvTerms         = (TextView) findViewById(R.id.tvTermsIPM);
		cbTerms         = (CheckBox) findViewById(R.id.cbTermsIPM);
		
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
			    	  tmp2 = Character.toUpperCase(tmp2.charAt(0)) + tmp2.substring(1);
			    	  tvFullNameStart.setText(tmp2 + " " + t);
		    	  }
		    	  else
		    	  {
		    		  tvFullNameStart.setText("");
		    	  }
		      }
		    }
		 });
		
		btnSignUp = (Button) findViewById(R.id.btnSignupIPM);
		btnSignUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!Utilities.isNetworkAvailable(SignUpIPM.this))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
                    builder.setMessage(R.string.no_internet_msg)
                        .setTitle(R.string.no_internet_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
            		return;
				}
				
				String username = etEmail.getText().toString();
                String password = etPass.getText().toString();
                String repass   = etRepass.getText().toString();
                String email    = username;
                String name     = tvFullNameStart.getText().toString()
                			    + etFullNameEnd.getText().toString();
 
                username = username.trim();
                password = password.trim();
                repass   = repass.trim();
                email    = email.trim();
                name     = name.trim();
 
                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
                    builder.setMessage(R.string.signup_error_message)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (!password.equals(repass)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
                    builder.setMessage(R.string.pass_no_match_error_msg)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (!cbTerms.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
                    builder.setMessage("Please read and agree to our terms and conditions before signing up.")
                        .setTitle("Agree to terms...")
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    
                    username = "i" + username;
                    email = "i" + email + "@iimidr.ac.in";
                    
                    /** NO MORE USED LIKE THIS
                    String name = "";
                    name = username.substring(3); **/
    
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    
                    newUser.put("Name", name);
                    
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
 
                            if (e == null) {
                                // Success!
                            	Toast.makeText(SignUpIPM.this, "Sign Up Successful!\nCheck your e-mail for verification link!", Toast.LENGTH_SHORT).show();
                            	Utilities.InitialiseUserDetails("i" + etEmail.getText().toString().trim());
                                Intent intent = new Intent(SignUpIPM.this, LoginIPM.class);
                                startActivity(intent);
                            }
                            else {
                            	String errorMsg = e.getMessage().replace("parameters", "Email ID or password");
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpIPM.this);
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
