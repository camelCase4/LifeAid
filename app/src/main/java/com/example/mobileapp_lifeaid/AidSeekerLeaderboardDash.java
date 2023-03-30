package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AidSeekerLeaderboardDash extends AppCompatActivity {

    ImageView menu;

    List<String> provCount_uid = new ArrayList<>();
    List<String> UIDinOrder = new ArrayList<>();
    List<String> Position = new ArrayList<>();
    List<String> fnames = new ArrayList<>();

    TextView leadProvs;

    //3/24/2023
    public static final int MENU_REQUEST_CODE = 1;
    //--


    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_seeker_leaderboard_dash);

        //3/24/2023
        menu = (ImageView) findViewById(R.id.imageView18);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(AidSeekerMainDash.this, MenuButtonForSeekers.class);
                startActivity(intent);*/
                Intent intent = new Intent(AidSeekerLeaderboardDash.this,MenuButtonForSeekers.class);
                startActivityForResult(intent,MENU_REQUEST_CODE);
            }
        });
        //---


        //3/16/2023

        leadProvs = findViewById(R.id.leaders2);

        leadProvs.setMovementMethod(new ScrollingMovementMethod());

        //---

        gettingCountAndUID();
        //amount();

    }
    //3/24/2023
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MENU_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the result
            // ...
        }
    }
    //---
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
                                        //Collections.sort(provCount_uid);
                                        //3/20/2023
                                        /*Collections.sort(provCount_uid, new Comparator<String>() {
                                            @Override
                                            public int compare(String s1, String s2) {
                                                String[] parts1 = s1.split("\\D+", 2); // split by non-digits
                                                String[] parts2 = s2.split("\\D+", 2);

                                                int num1 = Integer.parseInt(parts1[0]);
                                                int num2 = Integer.parseInt(parts2[0]);

                                                return Integer.compare(num1, num2);
                                            }
                                        });*/

                                        Collections.sort(provCount_uid, new Comparator<String>() {
                                            @Override
                                            public int compare(String s1, String s2) {
                                                int num1 = extractLeadingNumber(s1);
                                                int num2 = extractLeadingNumber(s2);
                                                return Integer.compare(num1, num2);
                                            }

                                            private int extractLeadingNumber(String s) {
                                                StringBuilder sb = new StringBuilder();
                                                for (char c : s.toCharArray()) {
                                                    if (Character.isDigit(c)) {
                                                        sb.append(c);
                                                    } else {
                                                        break;
                                                    }
                                                }
                                                return Integer.parseInt(sb.toString());
                                            }
                                        });
                                        //---
                                        Collections.reverse(provCount_uid);
                                        populatingUID();
                                    }



                                }
                                else
                                {
                                    Toast.makeText(AidSeekerLeaderboardDash.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AidSeekerLeaderboardDash.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
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

            String test = provCount_uid.get(i).split(" ")[0];//3/29/2023

            //String initialNums = String.format("%8s %19s %19s %23s \n\n",Integer.toString(i + 1),Position.get(i),provCount_uid.get(i).split(" ")[0],fnames.get(i));
       //original     //String initialNums = "     "+Integer.toString(i + 1) + space + Position.get(i) + "                     " + provCount_uid.get(i).split(" ")[0] + "                       " + fnames.get(i) + "\n\n";
            //3/29/2023

            String initialNums = "     "+Integer.toString(i + 1) + space + Position.get(i) + "                     " + provCount_uid.get(i).split(" ")[0] + "                       " + fnames.get(i);



            //SpannableString ss = new SpannableString(initialNums); 3/29/2023
            SpannableString spannableString = new SpannableString(initialNums);
            ClickableSpan clickableSpan = new ClickableSpan() {

                @Override
                public void onClick(View textView) {

                    /*Intent intent = new Intent(AidProviderHistory.this,GeneratedReportAidProvider.class);
                    intent.putExtra("time_date", dt);
                    intent.putExtra("action", rp);
                    intent.putExtra("seeker_name", sn);
                    intent.putExtra("location_place", placeOfIncident);
                    intent.putExtra("feedback", feedback);
                    intent.putExtra("seeker_uid", seekerID);
                    startActivity(intent);*/
                    Toast.makeText(AidSeekerLeaderboardDash.this,test,Toast.LENGTH_SHORT).show();

                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            spannableString.setSpan(clickableSpan, 57, initialNums.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            //leadProvs.append(spannableString);
            leadProvs.setMovementMethod(LinkMovementMethod.getInstance());
            leadProvs.setHighlightColor(Color.BLUE);


            //--

            //SpannableString spannableString = new SpannableString(initialNums); for a while lamangst
            if (i == 0) {
                ForegroundColorSpan goldspan = new ForegroundColorSpan(Color.rgb(255, 215, 0));
                spannableString.setSpan(goldspan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
                leadProvs.append("\n\n");//3/30/2023

            } else if (i == 1) {
                ForegroundColorSpan silverspan = new ForegroundColorSpan(Color.rgb(192, 192, 192));
                spannableString.setSpan(silverspan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
                leadProvs.append("\n\n");//3/30/2023

            } else if(i == 2){
                ForegroundColorSpan bronzespan = new ForegroundColorSpan(Color.rgb(205, 127, 50));
                spannableString.setSpan(bronzespan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
                leadProvs.append("\n\n");//3/30/2023

            }
            else
            {
                leadProvs.append(spannableString);
                leadProvs.append("\n\n");//3/30/2023
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
}