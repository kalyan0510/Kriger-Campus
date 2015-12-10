package com.androditry;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.List;


public class reset extends ActionBarActivity {

    EditText E,U;
    String emailid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        setTitle("Forgot Password");

        U=(EditText)findViewById(R.id.uname);
        E=(EditText)findViewById(R.id.verified);

        emailid=E.getText().toString();
        //final String emlid=emailid;

        Button resetbtn=(Button)findViewById(R.id.reset);
        resetbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String u=U.getText().toString();
                emailid=E.getText().toString();
                // Perform action on click
                //Toast.makeText(getApplicationContext(), emailid,
                //        Toast.LENGTH_SHORT).show();

                try {
                    ParseUser.requestPasswordReset(emailid);
                    //Toast.makeText(getApplicationContext(), "Pasword Reset Link Mail Sent",
                    //        Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
