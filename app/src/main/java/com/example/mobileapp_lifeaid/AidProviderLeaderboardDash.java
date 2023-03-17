package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AidProviderLeaderboardDash extends AppCompatActivity {

    TextView leadProvs;

    List<String> provCount_uid = new ArrayList<>();
    List<String> UIDinOrder = new ArrayList<>();
    List<String> Position = new ArrayList<>();
    List<String> fnames = new ArrayList<>();

    int index = 0;
    int determiner = 0;
    int counter = 0;

    boolean flag = true;




    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_leaderboard_dash);

        //3/16/2023
        leadProvs = (TextView) findViewById(R.id.leaders);

        leadProvs.setMovementMethod(new ScrollingMovementMethod());

        //---

        gettingCountAndUID();
        //amount();






    }

    //3/17/2023
    public void gettingCountAndUID()
    {

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
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
                                    if(!String.valueOf(snaps.child("provision_count").getValue()).equals("0")) {
                                       //provCount_uid.add((String.valueOf(snaps.child("provision_count").getValue()) + " " + uid));
                                        //Toast.makeText(AidProviderLeaderboardDash.this, provCount_uid.get(0), Toast.LENGTH_SHORT).show();
                                        populating(String.valueOf(snaps.child("provision_count").getValue()) + " " + uid);

                                    }


                                }
                                else
                                {
                                    Toast.makeText(AidProviderLeaderboardDash.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AidProviderLeaderboardDash.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
               //Collections.sort(provCount_uid);



            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });




    }

    public void populating(String i)
    {
        provCount_uid.add(i);
        flag = false;
        if(!flag)
        {
            leadProvs.append(provCount_uid.size()+"");
        }

    }
    public void amount()
    {

    }
    public void displaying()
    {
        String initialNums = "";
        for (int i = 0; i < provCount_uid.size(); i++) {
            if (i <= 2) {
                initialNums = Integer.toString(i + 1) + "       " + Position.get(i) + "       " + provCount_uid.get(i) + "             " + fnames.get(i) + "\n\n";
                SpannableString spannableString = new SpannableString(initialNums);
                if (i == 0) {
                    ForegroundColorSpan goldspan = new ForegroundColorSpan(Color.rgb(255, 215, 0));
                    spannableString.setSpan(goldspan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leadProvs.append(initialNums);
                } else if (i == 1) {
                    ForegroundColorSpan silverspan = new ForegroundColorSpan(Color.rgb(192, 192, 192));
                    spannableString.setSpan(silverspan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leadProvs.append(initialNums);
                } else {
                    ForegroundColorSpan bronzespan = new ForegroundColorSpan(Color.rgb(205, 127, 50));
                    spannableString.setSpan(bronzespan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leadProvs.append(initialNums);
                }
            } else {
                leadProvs.append(Integer.toString(i + 1) + "       " + Position.get(i) + "       " + provCount_uid.get(i) + "             " + fnames.get(i) + "\n\n");
            }
        }
    }
    public void gettingUIDvalue()
    {

        for(;index<UIDinOrder.size(); index++) {
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    dr.child(UIDinOrder.get(index)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    DataSnapshot snaps = task.getResult();
                                    Position.add(String.valueOf(snaps.child("job").getValue()));
                                    fnames.add(String.valueOf(snaps.child("fname").getValue()));

                                }
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            determiner++;
        }

    }



    //---


}