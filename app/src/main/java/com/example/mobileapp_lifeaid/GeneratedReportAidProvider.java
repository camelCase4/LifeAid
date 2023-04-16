package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GeneratedReportAidProvider extends AppCompatActivity {

    TextView td, act, seekn, em, add, inci, feedb, fbmsg,nm;
    ImageView downOrUp,exit;

    String seekerID = "";//3/23/2023

    AidProviderHistory aph = new AidProviderHistory();

    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
    DatabaseReference dr2 = FirebaseDatabase.getInstance().getReference("Aid-Provider");
    DatabaseReference dr3 = FirebaseDatabase.getInstance().getReference("Admin");

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
        nm = (TextView) findViewById(R.id.num);

        downOrUp = (ImageView) findViewById(R.id.thumb);
        exit = (ImageView) findViewById(R.id.back);

        //4/16/2023
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GeneratedReportAidProvider.this,AidProviderHistory.class);
                startActivity(intent);
            }
        });
        //---

        /*td.setText(aph.dt);
        act.setText(aph.rp);
        seekn.setText(aph.sn);
        inci.setText("Place Of Incident: "+aph.placeOfIncident);
        feedb.setText("Feedback: "+(aph.feedback.equals("1")?"Commended":(aph.feedback.equals("0")?"Unsatisfied":"Supported")));*/

        td.setText(getIntent().getStringExtra("time_date"));
        act.setText(getIntent().getStringExtra("action"));
        seekn.setText(getIntent().getStringExtra("seeker_name"));
        inci.setText("Incident: "+getIntent().getStringExtra("location_place"));
        feedb.setText("Feedback: "+(getIntent().getStringExtra("feedback").equals("1")?"Commended":(getIntent().getStringExtra("feedback").equals("0")?"Unsatisfied":"Supported")));


        //3/23/2023
        gettingAidSeekerAid();

        if(getIntent().getStringExtra("feedback").equals("1"))
        {
            fbmsg.setText("Keep up the good work, we really appreciate you!");
        }
        else if(getIntent().getStringExtra("feedback").equals("0"))
        {
            fbmsg.setText("It's okay! You still did great, keep your heads up!");
            downOrUp.setImageResource(R.drawable.down);
        }
        else
        {
            fbmsg.setText("Thank you for relaying the alert to other Aid - Providers!");
        }
        seekerID = getIntent().getStringExtra("seeker_uid");
        //----



    }
    
    public void gettingAidSeekerAid()
    {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dr.child(seekerID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();
                                String number = String.valueOf(snaps.child("phonenum").getValue());
                                String address = String.valueOf(snaps.child("address").getValue());
                                String email = String.valueOf(snaps.child("email").getValue());

                                nm.setText(number);
                                add.setText("Seeker Address: "+address);
                                em.setText(email);


                            }
                            else
                            {
                                //Toast.makeText(GeneratedReportAidProvider.this, "Data does not exist!", Toast.LENGTH_SHORT).show();
                                gettingAidSeekerDataInProviders();
                            }
                        }
                        else
                        {
                            Toast.makeText(GeneratedReportAidProvider.this,"Task was not successful!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //3/26/2023
    public void gettingAidSeekerDataInProviders()
    {
        dr2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dr2.child(seekerID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();
                                String number = String.valueOf(snaps.child("phonenum").getValue());
                                String address = String.valueOf(snaps.child("address").getValue());
                                String email = String.valueOf(snaps.child("email").getValue());

                                nm.setText(number);
                                add.setText("Seeker Address: "+address);
                                em.setText(email);


                            }
                            else
                            {
                                //Toast.makeText(GeneratedReportAidProvider.this, "Data does not exist!", Toast.LENGTH_SHORT).show();
                                gettingAidSeekerDataInAdmins();
                            }
                        }
                        else
                        {
                            Toast.makeText(GeneratedReportAidProvider.this,"Task was not successful!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void gettingAidSeekerDataInAdmins()
    {
        dr3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dr3.child(seekerID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();
                                String number = String.valueOf(snaps.child("phonenum").getValue());
                                String address = String.valueOf(snaps.child("address").getValue());
                                String email = String.valueOf(snaps.child("email").getValue());

                                nm.setText(number);
                                add.setText("Seeker Address: "+address);
                                em.setText(email);


                            }
                            else
                            {
                                //Toast.makeText(GeneratedReportAidProvider.this, "Data does not exist!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(GeneratedReportAidProvider.this,"Task was not successful!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //----
}