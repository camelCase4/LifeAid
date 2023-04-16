package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
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

import java.util.Collections;

public class AidProviderHistory extends AppCompatActivity {
    TextView historyContents;
    ImageView menu;

    //3/23/2023
    //public static String dat = "",rap = "",san = "",placeOfIncident="",feedback="",seekerID= "";
    //----
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("AidProviderHistory");
    MainActivity ma = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_history);

        historyContents = (TextView) findViewById(R.id.contents);

        //4/17/2023
        menu = (ImageView) findViewById(R.id.imageView18);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidProviderHistory.this,MenuButtonForProviders.class);
                startActivity(intent);
            }
        });
        //---


        historyContents.setMovementMethod(new ScrollingMovementMethod());

        gettingData();


    }

    public void gettingData()
    {
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                historyContents.setText("");

                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String uid = ds.getKey();

                    dr.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();
                                    if(String.valueOf(snaps.child("provider_id").getValue()).equals(ma.userid)) {
                                        String dt = String.valueOf(snaps.child("timedate").getValue()).substring(4,16);
                                        String rp = String.valueOf(snaps.child("aidORsupport").getValue());
                                        String sn = String.valueOf(snaps.child("seekername").getValue());
                                        String placeOfIncident =  String.valueOf(snaps.child("locationPlace").getValue());
                                        String feedback = String.valueOf(snaps.child("feedback").getValue());
                                        String seekerID = String.valueOf(snaps.child("seeker_uid").getValue());


                                        //3/23/2023
                                        //dt = String.valueOf(snaps.child("timedate").getValue()).substring(4,16);
                                        //rp = String.valueOf(snaps.child("aidORsupport").getValue());
                                        //sn = String.valueOf(snaps.child("seekername").getValue());
                                        //placeOfIncident = String.valueOf(snaps.child("locationPlace").getValue());
                                        //feedback = String.valueOf(snaps.child("feedback").getValue());
                                        //seekerID = String.valueOf(snaps.child("seeker_uid").getValue());
                                        //----
                                        String space = "                     ";
                                        if(rp.equals("Support"))
                                        {
                                            space = "                      ";
                                        }
                                        //historyContents.append("   "+dt + "             "+rp+space+sn+"\n\n");
                                        //3/22/2023
                                        //String temp = "   "+dt+"             "+rp+space+sn+"\n\n"; orig
                                        //4/2/2023
                                        //String temp = "   "+dt+"             "+rp+space+sn; original on 14
                                        //---
                                        //4/14/2023
                                        String nameTemp = sn;
                                        if(nameTemp.length() > 7)
                                        {
                                            nameTemp = sn.substring(0,7);
                                        }
                                        String temp = "   "+dt+"             "+rp+space+nameTemp;
                                        //----
                                        SpannableString ss = new SpannableString(temp);
                                        ClickableSpan clickableSpan = new ClickableSpan() {

                                            @Override
                                            public void onClick(View textView) {
                                                //startActivity(new Intent(AidProviderHistory.this, NextActivity.class));
                                                //Toast.makeText(AidProviderHistory.this,dt+" clicked",Toast.LENGTH_SHORT).show();
                                                //Intent intent = new Intent(AidProviderHistory.this,GeneratedReportAidProvider.class);
                                                //startActivity(intent);

                                                Intent intent = new Intent(AidProviderHistory.this,GeneratedReportAidProvider.class);
                                                intent.putExtra("time_date", dt);
                                                intent.putExtra("action", rp);
                                                intent.putExtra("seeker_name", sn);
                                                intent.putExtra("location_place", placeOfIncident);
                                                intent.putExtra("feedback", feedback);
                                                intent.putExtra("seeker_uid", seekerID);
                                                startActivity(intent);

                                            }
                                            @Override
                                            public void updateDrawState(TextPaint ds) {
                                                super.updateDrawState(ds);
                                                ds.setUnderlineText(false);
                                            }
                                        };
                                        ss.setSpan(clickableSpan, 37, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                        /*TextView textView = (TextView) findViewById(R.id.hello);
                                        textView.setText(ss);
                                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                                        textView.setHighlightColor(Color.TRANSPARENT);*/

                                        historyContents.append(ss);
                                        historyContents.append("\n\n");
                                        historyContents.setMovementMethod(LinkMovementMethod.getInstance());
                                        historyContents.setHighlightColor(Color.TRANSPARENT);

                                        //------

                                    }

                                }
                                else
                                {
                                    Toast.makeText(AidProviderHistory.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AidProviderHistory.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });

    }
}