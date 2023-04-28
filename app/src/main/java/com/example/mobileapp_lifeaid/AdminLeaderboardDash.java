package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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

public class AdminLeaderboardDash extends AppCompatActivity {

    TextView leadProvs, switching;
    ImageView menu;

    List<String> provCount_uid = new ArrayList<>();
    List<String> UIDinOrder = new ArrayList<>();
    List<String> Position = new ArrayList<>();
    List<String> fnames = new ArrayList<>();

    int index = 0;
    int determiner = 0;
    int counter = 0;

    boolean flag = true;

    int roleCounter = 0, roleSize = 0; // 4/3/2023


    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");


    //4/3/2023
    public static boolean isInfoclicked = false;
    //---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_leaderboard_dash);

        //3/16/2023
        leadProvs = (TextView) findViewById(R.id.leaders2);

        leadProvs.setMovementMethod(new ScrollingMovementMethod());

        //---

        //4/3/2023
        switching = (TextView) findViewById(R.id.switchrole);
        //--

        //4/16/2023
        menu = (ImageView) findViewById(R.id.imageView18);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminLeaderboardDash.this,MenuForAdmins.class);
                startActivity(intent);
            }
        });
        //---

        gettingCountAndUID();
        //amount();


        //4/3/2023
        switching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roleCounter++;// 0 = nurses, 1 = policemen, 2 = firemen, 3 = doctors;
                if (roleCounter == 0) {
                    switching.setText("P O L I C E M E N   > > >");
                } else if (roleCounter == 1) {
                    switching.setText("F I R E M E N   > > >");
                } else if (roleCounter == 2) {
                    switching.setText("D O C T O R S   > > >");
                } else {
                    switching.setText("N U R S E S   > > >");

                }
                gettingCountAndUID();
            }
        });
        //--


    }

    //3/17/2023
    public void gettingCountAndUID() {

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
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    roleSize++; //4/3/2023
                                    DataSnapshot snaps = task.getResult();

                                    /*provCount_uid.add((String.valueOf(snaps.child("provision_count").getValue()) + " " + uid));
                                    if (provCount_uid.size() == datasnapshot.getChildrenCount()) {
                                        //Collections.sort(provCount_uid);
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
                                        Collections.reverse(provCount_uid);
                                        populatingUID();
                                    }*/ // commented on 4/3

                                    //4/3/2023
                                    String occupationJob = String.valueOf(snaps.child("job").getValue());

                                    if (roleCounter == 0) {
                                        if (occupationJob.toLowerCase().contains("nurse")) {
                                            provCount_uid.add((String.valueOf(snaps.child("provision_count").getValue()) + " " + uid));
                                        }
                                        if (roleSize == datasnapshot.getChildrenCount()) {

                                            roleSize = 0;
                                            //Collections.sort(provCount_uid);
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
                                            Collections.reverse(provCount_uid);
                                            populatingUID();
                                        }
                                    } else if (roleCounter == 1) {
                                        if (occupationJob.toLowerCase().contains("police")) {
                                            provCount_uid.add((String.valueOf(snaps.child("provision_count").getValue()) + " " + uid));

                                        }
                                        if (roleSize == datasnapshot.getChildrenCount()) {
                                            roleSize = 0;
                                            //Collections.sort(provCount_uid);
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
                                            Collections.reverse(provCount_uid);
                                            populatingUID();
                                        }
                                    } else if (roleCounter == 2) {
                                        if (occupationJob.toLowerCase().contains("fire")) {
                                            provCount_uid.add((String.valueOf(snaps.child("provision_count").getValue()) + " " + uid));

                                        }
                                        if (roleSize == datasnapshot.getChildrenCount()) {
                                            roleSize = 0;
                                            //Collections.sort(provCount_uid);
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
                                            Collections.reverse(provCount_uid);
                                            populatingUID();
                                        }
                                    } else {
                                        if (occupationJob.toLowerCase().contains("doctor")) {
                                            provCount_uid.add((String.valueOf(snaps.child("provision_count").getValue()) + " " + uid));

                                        }
                                        if (roleSize == datasnapshot.getChildrenCount()) {
                                            roleCounter = -1;
                                            roleSize = 0;
                                            //Collections.sort(provCount_uid);
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
                                            Collections.reverse(provCount_uid);
                                            populatingUID();
                                        }
                                    }
                                    //----


                                } else {
                                    Toast.makeText(AdminLeaderboardDash.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AdminLeaderboardDash.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
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

    public void populatingUID() {
        for (String i : provCount_uid) {
            UIDinOrder.add(i.split(" ")[1]);
            if (UIDinOrder.size() == provCount_uid.size()) {
                gettingUIDvalue();
            }
        }
    }

    //public void displaying()
    //{
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
        /*String spaceForNurse = "                ";
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

            //String initialNums = "     "+Integer.toString(i + 1) + space + Position.get(i) + "                     " + provCount_uid.get(i).split(" ")[0] + "                       " + fnames.get(i) + "\n\n";
            //4/2/2023
            String initialNums = "     "+Integer.toString(i + 1) + space + Position.get(i) + "                     " + provCount_uid.get(i).split(" ")[0] + "                       " + fnames.get(i);

            //---

            SpannableString spannableString = new SpannableString(initialNums);
            if (i == 0) {
                ForegroundColorSpan goldspan = new ForegroundColorSpan(Color.rgb(255, 215, 0));
                spannableString.setSpan(goldspan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
                leadProvs.append("\n\n"); //4/2/2023
            } else if (i == 1) {
                ForegroundColorSpan silverspan = new ForegroundColorSpan(Color.rgb(192, 192, 192));
                spannableString.setSpan(silverspan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
                leadProvs.append("\n\n"); //4/2/2023

            } else if(i == 2){
                ForegroundColorSpan bronzespan = new ForegroundColorSpan(Color.rgb(205, 127, 50));
                spannableString.setSpan(bronzespan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
                leadProvs.append("\n\n"); //4/2/2023

            }
            else
            {
                leadProvs.append(initialNums);
                leadProvs.append("\n\n"); //4/2/2023

            }

        }
    }*/ //original
    public void displaying() {
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

            //String test = provCount_uid.get(i).split(" ")[0];//3/29/2023
            //4/3/2023
            String uidToUse = UIDinOrder.get(i);
            //---

            //String initialNums = String.format("%8s %19s %19s %23s \n\n",Integer.toString(i + 1),Position.get(i),provCount_uid.get(i).split(" ")[0],fnames.get(i));
            //original     //String initialNums = "     "+Integer.toString(i + 1) + space + Position.get(i) + "                     " + provCount_uid.get(i).split(" ")[0] + "                       " + fnames.get(i) + "\n\n";
            //3/29/2023

            //String initialNums = "     " + Integer.toString(i + 1) + space + Position.get(i).toLowerCase() + "                     " + provCount_uid.get(i).split(" ")[0] + "                       " + fnames.get(i); original on 14
            //4/14/23
            String nameTemp = fnames.get(i);
            if(nameTemp.length() > 5)//changed 7 to 5 4/28/2023
            {
                nameTemp = fnames.get(i).substring(0,5);
            }
            String initialNums = "     " + Integer.toString(i + 1) + space + Position.get(i).toLowerCase() + "                     " + provCount_uid.get(i).split(" ")[0] + "                       " + nameTemp;
            //---


            //SpannableString ss = new SpannableString(initialNums); 3/29/2023
            SpannableString spannableString = new SpannableString(initialNums);
            ClickableSpan clickableSpan = new ClickableSpan() {

                @Override
                public void onClick(View textView) {

                    isInfoclicked = true;//4/3/2023
                    Intent intent = new Intent(AdminLeaderboardDash.this, AdminManageRecordsSecondPage.class);
                    intent.putExtra("UserUid", uidToUse);
                    startActivity(intent);
                    //Toast.makeText(AidSeekerLeaderboardDash.this,uidToUse,Toast.LENGTH_SHORT).show();

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
            leadProvs.setHighlightColor(Color.TRANSPARENT);


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

            } else if (i == 2) {
                ForegroundColorSpan bronzespan = new ForegroundColorSpan(Color.rgb(205, 127, 50));
                spannableString.setSpan(bronzespan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                leadProvs.append(spannableString);
                leadProvs.append("\n\n");//3/30/2023

            } else {
                leadProvs.append(spannableString);
                leadProvs.append("\n\n");//3/30/2023
            }


        }
    }

    public void gettingUIDvalue() {


        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (int i = 0; i < UIDinOrder.size(); i++) {
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