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
                //3/19/2023
                provCount_uid.clear();
                UIDinOrder.clear();
                Position.clear();
                fnames.clear();
                leadProvs.setText("");
                //-----

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

                                    provCount_uid.add((String.valueOf(snaps.child("provision_count").getValue()) + " " + uid));
                                    if (provCount_uid.size() == datasnapshot.getChildrenCount()) {
                                        Collections.sort(provCount_uid);
                                        Collections.reverse(provCount_uid);
                                        populatingUID();
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



            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });




    }

    public void populatingUID()
    {
        for(String i : provCount_uid)
        {
            UIDinOrder.add(i.split(" ")[1]);
            if(UIDinOrder.size() == provCount_uid.size())
            {
                gettingUIDvalue();
            }
        }
    }

    public void displaying()
    {
       /* String initialNums = "";
        for (int i = 0; i < provCount_uid.size(); i++) {
            if (i < 3) {
                initialNums = Integer.toString(i + 1) + "       " + Position.get(i) + "       " + provCount_uid.get(i).split(" ")[0] + "             " + fnames.get(i) + "\n\n";
                SpannableString spannableString = new SpannableString(initialNums);
                if (i == 0) {
                    ForegroundColorSpan goldspan = new ForegroundColorSpan(Color.rgb(255, 215, 0));
                    spannableString.setSpan(goldspan, 0, Integer.toString(i + 1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leadProvs.append(initialNums);
                } else if (i == 1) {
                    ForegroundColorSpan silverspan = new ForegroundColorSpan(Color.rgb(192, 192, 192));
                    spannableString.setSpan(silverspan, 0, Integer.toString(i + 1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leadProvs.append(initialNums);
                } else {
                    ForegroundColorSpan bronzespan = new ForegroundColorSpan(Color.rgb(205, 127, 50));
                    spannableString.setSpan(bronzespan, 0, Integer.toString(i + 1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    leadProvs.append(initialNums);
                }
            } else {
                leadProvs.append(Integer.toString(i + 1) + "       " + Position.get(i) + "       " + provCount_uid.get(i).split(" ")[0] + "             " + fnames.get(i) + "\n\n");
            }
        }*/
        String spaceForNurse = "                ";
        String spaceForFire = "             ";
        String spaceForPoliceman = "            ";
        String spaceForDoctor = "               ";
        String space = "";
        for (int i = 0; i < provCount_uid.size(); i++) {

            if (Position.get(i).toLowerCase().equals("policeman")) {
                space = spaceForPoliceman;
            } else if (Position.get(i).toLowerCase().equals("fireman")) {
                space = spaceForFire;
            } else if (Position.get(i).toLowerCase().equals("doctor")) {
                space = spaceForDoctor;
            } else {
                space = spaceForNurse;
            }

            String initialNums = "     "+Integer.toString(i + 1) + space + Position.get(i) + "                     " + provCount_uid.get(i).split(" ")[0] + "                       " + fnames.get(i) + "\n\n";
            SpannableString spannableString = new SpannableString(initialNums);
            if (i == 0) {
                ForegroundColorSpan goldspan = new ForegroundColorSpan(Color.rgb(255, 215, 0));
                spannableString.setSpan(goldspan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
            } else if (i == 1) {
                ForegroundColorSpan silverspan = new ForegroundColorSpan(Color.rgb(192, 192, 192));
                spannableString.setSpan(silverspan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
            } else if(i == 2){
                ForegroundColorSpan bronzespan = new ForegroundColorSpan(Color.rgb(205, 127, 50));
                spannableString.setSpan(bronzespan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
            }
            else
            {
                leadProvs.append(initialNums);
            }

        }
    }
    public void gettingUIDvalue()
    {


            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    for(int i = 0; i < UIDinOrder.size(); i++) {
                        dr.child(UIDinOrder.get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        DataSnapshot snaps = task.getResult();
                                        Position.add(String.valueOf(snaps.child("job").getValue()));
                                        fnames.add(String.valueOf(snaps.child("fname").getValue()));
                                        if (Position.size() == provCount_uid.size()) {
                                            displaying();
                                        }

                                    }
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



    //---


}