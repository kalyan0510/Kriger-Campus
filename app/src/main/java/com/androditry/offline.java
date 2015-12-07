package com.androditry;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


public class offline extends ActionBarActivity {
    long  ctr=1,ctr1=1,ctr2=1,ctr3=1;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
        setTitle("Kriger Campus");

        final String category=Utilities.getItem();
        final String ques1[]={"When is the admisssion form out?","When is the exam held?","What are the centers for exams ?","What are the subjects one should prepare?","What is the weightage of the entrance exam ?"};
        final String ques2[]={"How many people are called for interview ? ","What are the centers for Interview?","Who takes the Interview?","What's the weightage of the Interview? ","When is the final result out?"};
        final String ques3[]={"Where is the campus?","How to get to the College campus?","How big is the campus?","What's the number of residents in campus?","Anything good about the campus?"};
        final String ques4[]={"Is there any sports facility?","Are there any sports events organised?","Do you people participate in sport fests?","How can i join the college team?","Is there any coaching faculty?"};
        final String ques5[]={"Is the campus ragging free?","Is there any anti ragging committee?","What about the first year students?","What is I am ragged?","How about reception of first years by the seniors?"};
        final String ques6[]={"What is the full form of IPM?","What degree do i get after 5 years?","IIM's award diploma,what about my degree?","What do they teach in IPM?","How is it better than other graduation options?"};
        final String ques7[]={"How many faculties?","What's their qualifications?","How is the faculty?","What about visiting faculty?","What methodology is used for teaching?"};
        final String ques8[]={"Which all festivals are celebrated?","What about in-campus parties/DJ nights?","Any good pub/party spot?","What about boozing in campus?","What is the late entry timing?"};




    //question1
       final Button b=(Button)findViewById(R.id.q1);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(category.equalsIgnoreCase("Test"))
                {
                    if(ctr%2==0) {
                        b.setText("ANS) yes we have an anti-ragging committee");
                        ctr++;
                    }
                    else
                    {
                        b.setText("Q)"+ques1[0]);
                        ctr++;
                    }
                }
                if(category.equalsIgnoreCase("Interview"))
                {
                    if(ctr%2==0) {
                        b.setText("ANS) ");
                        ctr++;
                    }
                    else
                    {
                        b.setText("Q)"+ques2[0]);
                        ctr++;
                    }
                }
                if(category.equalsIgnoreCase("Campus"))
                {
                    if(ctr%2==0) {
                        b.setText("ANS) yes we have an anti-ragging committee");
                        ctr++;
                    }
                    else
                    {
                        b.setText("Q)"+ques3[0]);
                        ctr++;
                    }
                }
                if(category.equalsIgnoreCase("Academics"))
                {
                    if(ctr%2==0) {
                        b.setText("ANS) yes we have an anti-ragging committee");
                        ctr++;
                    }
                    else
                    {
                        b.setText("Q)"+ques6[0]);
                        ctr++;
                    }
                }
                if(category.equalsIgnoreCase("Sports"))
                {
                    if(ctr%2==0) {
                        b.setText("ANS) We have all the sports facilities a college can have. These include cricket ground,two basketball courts,hand ball ground,3 badminton courts,2 gyms, Sports Complex which has squash,tennis,table tennis,Aerobics area,Olympic size swimming pool.");
                        ctr++;
                    }
                    else
                    {
                        b.setText("Q)"+ques4[0]);
                        ctr++;
                    }
                }
                if(category.equalsIgnoreCase("Ragging"))
                {
                    if(ctr%2==0) {
                        b.setText("ANS) Yes, the campus is totally ragging free and the authority here is tolerant about the same. If any student is found ragging or if any allegation is proved against him, (s)he is rusticated from the institute. But the environment is really friendly and the seniors are very supportive.");
                        ctr++;
                    }
                    else
                    {
                        b.setText("Q)"+ques5[0]);
                        ctr++;
                    }
                }
                if(category.equalsIgnoreCase("Faculty"))
                {
                    if(ctr%2==0) {
                        b.setText("ANS) yes we have an anti-ragging committee");
                        ctr++;
                    }
                    else
                    {
                        b.setText("Q)"+ques7[0]);
                        ctr++;
                    }
                }
                if(category.equalsIgnoreCase("Fun_N_Party"))
                {
                    if(ctr%2==0) {
                        b.setText("ANS) yes we have an anti-ragging committee");
                        ctr++;
                    }
                    else
                    {
                        b.setText("Q)"+ques8[0]);
                        ctr++;
                    }
                }

            }
        });

        //question2
        final Button b1=(Button)findViewById(R.id.q2);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(category.equalsIgnoreCase("Test"))
                {
                    if(ctr1%2==0) {
                        b1.setText("ANS) yes we have an anti-ragging committee");
                        ctr1++;
                    }
                    else
                    {
                        b1.setText("Q)"+ques1[1]);
                        ctr1++;
                    }
                }
                if(category.equalsIgnoreCase("Interview"))
                {
                    if(ctr1%2==0) {
                        b1.setText("ANS) yes we have an anti-ragging committee");
                        ctr1++;
                    }
                    else
                    {
                        b1.setText("Q)"+ques2[1]);
                        ctr1++;
                    }
                }
                if(category.equalsIgnoreCase("Campus"))
                {
                    if(ctr1%2==0) {
                        b1.setText("ANS) yes we have an anti-ragging committee");
                        ctr1++;
                    }
                    else
                    {
                        b1.setText("Q)" + ques3[1]);
                        ctr1++;
                    }
                }
                if(category.equalsIgnoreCase("Academics"))
                {
                    if(ctr1%2==0) {
                        b1.setText("ANS) yes we have an anti-ragging committee");
                        ctr1++;
                    }
                    else
                    {
                        b1.setText("Q)"+ques6[1]);
                        ctr1++;
                    }
                }
                if(category.equalsIgnoreCase("Sports"))
                {
                    if(ctr1%2==0) {
                        b1.setText("ANS) IPM Sports committee organizes inter batch tournaments. Students compete for titles in football,criket,basketball,TT,street football,volleyball,sports quiz and throw ball.");
                        ctr1++;
                    }
                    else
                    {
                        b1.setText("Q)"+ques4[1]);
                        ctr1++;
                    }
                }
                if(category.equalsIgnoreCase("Ragging"))
                {
                    if(ctr1%2==0) {
                        b1.setText("ANS) Yes, there is an anti-ragging commitee here which is headed by the director himself. There are also a bunch of faculty members in the committee as well.");
                        ctr1++;
                    }
                    else
                    {
                        b1.setText("Q)"+ques5[1]);
                        ctr1++;
                    }
                }
                if(category.equalsIgnoreCase("Faculty"))
                {
                    if(ctr1%2==0) {
                        b1.setText("ANS) yes we have an anti-ragging committee");
                        ctr1++;
                    }
                    else
                    {
                        b1.setText("Q)"+ques7[1]);
                        ctr1++;
                    }
                }
                if(category.equalsIgnoreCase("Fun_N_Party"))
                {
                    if(ctr1%2==0) {
                        b1.setText("ANS) yes we have an anti-ragging committee");
                        ctr1++;
                    }
                    else
                    {
                        b1.setText("Q)"+ques8[1]);
                        ctr1++;
                    }
                }
            }
        });

        //question3
        final Button b2=(Button)findViewById(R.id.q3);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(category.equalsIgnoreCase("Test"))
                {
                    if(ctr2%2==0) {
                        b2.setText("ANS) yes we have an anti-ragging committee");
                        ctr2++;
                    }
                    else
                    {
                        b2.setText("Q)"+ques1[2]);
                        ctr2++;
                    }
                }
                if(category.equalsIgnoreCase("Interview"))
                {
                    if(ctr2%2==0) {
                        b2.setText("ANS) yes we have an anti-ragging committee");
                        ctr2++;
                    }
                    else
                    {
                        b2.setText("Q)"+ques2[2]);
                        ctr2++;
                    }
                }
                if(category.equalsIgnoreCase("Campus"))
                {
                    if(ctr2%2==0) {
                        b2.setText("ANS) yes we have an anti-ragging committee");
                        ctr2++;
                    }
                    else
                    {
                        b2.setText("Q)"+ques3[2]);
                        ctr2++;
                    }
                }
                if(category.equalsIgnoreCase("Academics"))
                {
                    if(ctr2%2==0) {
                        b2.setText("ANS) yes we have an anti-ragging committee");
                        ctr2++;
                    }
                    else
                    {
                        b2.setText("Q)"+ques6[2]);
                        ctr2++;
                    }
                }
                if(category.equalsIgnoreCase("Sports"))
                {
                    if(ctr2%2==0) {
                        b2.setText("ANS) We do participate. We participated in Udghosh IIT KGP sports fest where Massom bagged 3rd prize in weight lifting and Surpreet Singh qualified to squash quarter finals.");
                        ctr2++;
                    }
                    else
                    {
                        b2.setText("Q)"+ques4[2]);
                        ctr2++;
                    }
                }
                if(category.equalsIgnoreCase("Ragging"))
                {
                    if(ctr2%2==0) {
                        b2.setText("ANS) Every IPM student is always very welcoming in his/her approach towards his/her junior and some even become the best pals or 'brothers from another mother'!!!. Till today no such case of ragging has ever been reported in our campus.   ");
                        ctr2++;
                    }
                    else
                    {
                        b2.setText("Q)"+ques5[2]);
                        ctr2++;
                    }
                }
                if(category.equalsIgnoreCase("Faculty"))
                {
                    if(ctr2%2==0) {
                        b2.setText("ANS) yes we have an anti-ragging committee");
                        ctr2++;
                    }
                    else
                    {
                        b2.setText("Q)"+ques7[2]);
                        ctr2++;
                    }
                }
                if(category.equalsIgnoreCase("Fun_N_Party"))
                {
                    if(ctr2%2==0) {
                        b2.setText("ANS) yes we have an anti-ragging committee");
                        ctr2++;
                    }
                    else
                    {
                        b2.setText("Q)"+ques8[2]);
                        ctr2++;
                    }
                }
            }
        });

        //question4
        final Button b3=(Button)findViewById(R.id.q4);
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(category.equalsIgnoreCase("Test"))
                {
                    if(ctr3%2==0) {
                        b3.setText("ANS) yes we have an anti-ragging committee");
                        ctr3++;
                    }
                    else
                    {
                        b3.setText("Q)"+ques1[3]);
                        ctr3++;
                    }
                }
                if(category.equalsIgnoreCase("Interview"))
                {
                    if(ctr3%2==0) {
                        b3.setText("ANS) yes we have an anti-ragging committee");
                        ctr3++;
                    }
                    else
                    {
                        b3.setText("Q)"+ques2[3]);
                        ctr3++;
                    }
                }
                if(category.equalsIgnoreCase("Campus"))
                {
                    if(ctr3%2==0) {
                        b3.setText("ANS) yes we have an anti-ragging committee");
                        ctr3++;
                    }
                    else
                    {
                        b3.setText("Q)"+ques3[3]);
                        ctr3++;
                    }
                }
                if(category.equalsIgnoreCase("Academics"))
                {
                    if(ctr3%2==0) {
                        b3.setText("ANS) yes we have an anti-ragging committee");
                        ctr3++;
                    }
                    else
                    {
                        b3.setText("Q)"+ques6[3]);
                        ctr3++;
                    }
                }
                if(category.equalsIgnoreCase("Sports"))
                {
                    if(ctr3%2==0) {
                        b3.setText("ANS) We do have selections rounds where you can show your skills to get selected in the college team.");
                        ctr3++;
                    }
                    else
                    {
                        b3.setText("Q)"+ques4[3]);
                        ctr3++;
                    }
                }
                if(category.equalsIgnoreCase("Ragging"))
                {
                    if(ctr3%2==0) {
                        b3.setText("ANS) If you are ragged(God Forbid), then u can lodge a complaint via an email or in person to the Officer of Hostel Office/Director/Anti-Ragging Committee and justice will done to you within no time");
                        ctr3++;
                    }
                    else
                    {
                        b3.setText("Q)"+ques5[3]);
                        ctr3++;
                    }
                }
                if(category.equalsIgnoreCase("Faculty"))
                {
                    if(ctr3%2==0) {
                        b3.setText("ANS) yes we have an anti-ragging committee");
                        ctr3++;
                    }
                    else
                    {
                        b3.setText("Q)"+ques7[3]);
                        ctr3++;
                    }
                }
                if(category.equalsIgnoreCase("Fun_N_Party"))
                {
                    if(ctr3%2==0) {
                        b3.setText("ANS) yes we have an anti-ragging committee");
                        ctr3++;
                    }
                    else
                    {
                        b3.setText("Q)"+ques8[3]);
                        ctr3++;
                    }
                }
            }
        });
        b.performClick();
        b1.performClick();
        b2.performClick();
        b3.performClick();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_offline, menu);
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
