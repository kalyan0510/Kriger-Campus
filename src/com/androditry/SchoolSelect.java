package com.androditry;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SchoolSelect extends ActionBarActivity {
	
	Button btnSchool,btnIPM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_select);
		setTitle("User Type | Kriger Campus");
		
		btnSchool = (Button) findViewById(R.id.btnSchool);
		btnIPM = (Button) findViewById(R.id.btnIPM);
		
		btnSchool.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SchoolSelect.this,LoginSchool.class);
				startActivity(i);
			}
		});
		
		btnIPM.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SchoolSelect.this,LoginIPM.class);
				startActivity(i);
			}
		});
	}
}
