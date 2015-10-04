package com.androditry;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SchoolSelect extends ActionBarActivity {
	
	Button btnSchool,btnIPM;
	TextView tvText;
	//TextView tvSchool, tvIPM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_select);
		setTitle("Kriger Campus");
		
		btnSchool = (Button) findViewById(R.id.btnSchool);
		btnIPM = (Button) findViewById(R.id.btnIPM);
		tvText = (TextView) findViewById(R.id.tvSchoolSelect);

		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/nevis.ttf");
		btnSchool.setTypeface(face);
		btnIPM.setTypeface(face);
		tvText.setTypeface(face);

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
