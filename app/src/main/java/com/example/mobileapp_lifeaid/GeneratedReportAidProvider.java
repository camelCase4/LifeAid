package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class GeneratedReportAidProvider extends AppCompatActivity {

    TextView td, act, seekn, em, add, inci, feedb, fbmsg;
    ImageView downOrUp,exit;

    AidProviderHistory aph = new AidProviderHistory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_report_aid_provider);
        overridePendingTransition(R.anim.slide_in_from_right,0);

        td = (TextView) findViewById(R.id.timedate);
        act = (TextView) findViewById(R.id.action);
        seekn = (TextView) findViewById(R.id.seekername);
        em = (TextView) findViewById(R.id.emailrepo);
        add = (TextView) findViewById(R.id.addr);
        inci = (TextView) findViewById(R.id.incident);
        feedb = (TextView) findViewById(R.id.fbseek);
        fbmsg = (TextView) findViewById(R.id.fb);

        downOrUp = (ImageView) findViewById(R.id.thumb);
        exit = (ImageView) findViewById(R.id.back);


        /*td.setText(aph.dt);
        act.setText(aph.rp);
        seekn.setText(aph.sn);
        inci.setText("Place Of Incident: "+aph.placeOfIncident);
        feedb.setText("Feedback: "+(aph.feedback.equals("1")?"Commended":(aph.feedback.equals("0")?"Unsatisfied":"Supported")));*/

        td.setText(getIntent().getStringExtra("time_date"));
        act.setText(getIntent().getStringExtra("action"));
        seekn.setText(getIntent().getStringExtra("seeker_name"));
        inci.setText(getIntent().getStringExtra("location_place"));
        feedb.setText("Feedback: "+(getIntent().getStringExtra("feedback").equals("1")?"Commended":(getIntent().getStringExtra("feedback").equals("0")?"Unsatisfied":"Supported")));





    }
}