package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
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

public class AdminManageRecordsSecondPage extends AppCompatActivity {

    TextView fullname,email,job,address,commendcount,provcount,ifnoStar,conts,password;
    ImageView star1,star2,star3,star4,bk;
    Button delbtn;

    TextView headerCol;//4/23/2023

    AdminManageRecords amr = new AdminManageRecords();
    AidSeekerLeaderboardDash ald = new AidSeekerLeaderboardDash(); //4/3/2023
    AidProviderLeaderboardDash apld = new AidProviderLeaderboardDash();//4/3/2023
    AdminLeaderboardDash adld = new AdminLeaderboardDash();//4/3/2023


    String CurrentUID = "";
    String toDelete= "";

    String phoneCall = "";//4/3/2023

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_records_second_page);
        overridePendingTransition(R.anim.slide_in_from_right,0);

        fullname = (TextView) findViewById(R.id.fullname);
        email = (TextView) findViewById(R.id.email);
        job = (TextView) findViewById(R.id.job);
        address = (TextView) findViewById(R.id.address);
        commendcount = (TextView) findViewById(R.id.commendcount);
        provcount = (TextView) findViewById(R.id.provcount);
        ifnoStar = (TextView) findViewById(R.id.tv_registration22);
        conts = (TextView) findViewById(R.id.contents);
        password = (TextView) findViewById(R.id.pw);

        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView)  findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        bk = (ImageView) findViewById(R.id.back);//4/3/2023

        star1.setVisibility(View.INVISIBLE);
        star2.setVisibility(View.INVISIBLE);
        star3.setVisibility(View.INVISIBLE);
        star4.setVisibility(View.INVISIBLE);

        delbtn = (Button) findViewById(R.id.delbutton);

        headerCol = (TextView) findViewById(R.id.leadersCol);//4/23/2023



        conts.setMovementMethod(new ScrollingMovementMethod());

        CurrentUID = getIntent().getStringExtra("UserUid");
        /*gettingUserData(amr.chosenRole);


        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case DialogInterface.BUTTON_POSITIVE:
                                deletingData();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //do nothing
                                break;

                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminManageRecordsSecondPage.this);
                builder.setMessage("Are you really sure?").setPositiveButton("YES!",dialogClickListener).setNegativeButton("No, misclicked",dialogClickListener).show();

            }
        });*/ //original

        //4/3/2023
        if(ald.isInfoclicked || apld.isInfoclicked || adld.isInfoclicked)
        {
            //4/23/2023
            adld.isInfoclicked = false;
            apld.isInfoclicked = false;
            ald.isInfoclicked = false;
            //---
            gettingUserData("Aid-Provider");
            delbtn.setText("Contact this provider?");

            delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //4/3/2023
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+phoneCall));
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(AdminManageRecordsSecondPage.this,"Number Calling Failed!",Toast.LENGTH_SHORT).show();
                    }
                    //---
                }
            });

            bk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
        else {
            gettingUserData(amr.chosenRole);

            //4/23/2023
            if(amr.chosenRole.equals("Aid-Seeker"))
            {
                headerCol.setText("DATE - TIME                EMERGENCY              PROVIDER - NAME");
            }
            else if(amr.chosenRole.equals("Aid-Provider"))
            {
                headerCol.setText("DATE - TIME                 ACTION                  SEEKER - NAME");
            }
            //----
            delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i){
                                case DialogInterface.BUTTON_POSITIVE:
                                    deletingData();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //do nothing
                                    break;

                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminManageRecordsSecondPage.this);
                    builder.setMessage("Are you really sure?").setPositiveButton("YES!",dialogClickListener).setNegativeButton("No, misclicked",dialogClickListener).show();

                }
            });

            bk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AdminManageRecordsSecondPage.this,AdminManageRecords.class);
                    startActivity(intent);
                }
            });
        }
        //---


    }
    //3/29/2023
    public void deletingData()
    {
        DatabaseReference drdel = FirebaseDatabase.getInstance().getReference(toDelete);
        drdel.child(CurrentUID).setValue(null);//---deleting

        Intent intent = new Intent(AdminManageRecordsSecondPage.this,AdminManageRecords.class);
        startActivity(intent);
    }
    //---

    public void gettingUserData(String whatRole)
    {
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference(whatRole);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    dr.child(CurrentUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    DataSnapshot snaps = task.getResult();
                                    String fullN = String.valueOf(snaps.child("fname").getValue())+ " "+String.valueOf(snaps.child("lname").getValue());
                                    String emaiL = String.valueOf(snaps.child("email").getValue());
                                    String joB = String.valueOf(snaps.child("job").getValue());
                                    String addresS = String.valueOf(snaps.child("address").getValue());
                                    String commendcounT= String.valueOf(snaps.child("commends").getValue());
                                    String provcounT = String.valueOf(snaps.child("provision_count").getValue());
                                    phoneCall = String.valueOf(snaps.child("phonenum").getValue());//4/3/2023

                                    fullname.setText("Name : "+fullN);
                                    email.setText("Email : "+emaiL);
                                    job.setText("Occupation : "+joB);
                                    address.setText("Address : "+addresS);
                                    commendcount.setText("Commends : "+commendcounT);
                                    provcount.setText("Provisions : "+provcounT);
                                    password.setText("Password: "+ String.valueOf(snaps.child("password").getValue()));//5/1

                                    /*if(whatRole.equals("Aid-Provider")) {
                                        formulaStars(commendcounT, provcounT);
                                        toDelete = "Aid-Provider";
                                    }
                                    else
                                    {
                                        ifnoStar.setText("Aid-Seeker!");
                                        commendcount.setText("---");
                                        provcount.setText("---");
                                        job.setText("---");
                                        toDelete = "Aid-Seeker";
                                    }*/
                                    //4/2/2023
                                    if(whatRole.equals("Aid-Seeker")) {
                                        ifnoStar.setText("Aid-Seeker!");
                                        commendcount.setText("---");
                                        provcount.setText("---");
                                        job.setText("---");
                                        toDelete = "Aid-Seeker";
                                    }
                                    else
                                    {
                                        formulaStars(commendcounT, provcounT);
                                        toDelete = "Aid-Provider";
                                    }
                                    //--

                                    String historyType = (whatRole.equals("Aid-Seeker")?"AidSeekerHistory":"AidProviderHistory");

                                    displayHistory(historyType);
                                }
                            }
                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void formulaStars(String cc,String tc)
    {
        double ratings = (Double.parseDouble(cc) / Double.parseDouble(tc)) * 100;


        if(ratings == 100)
        {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.VISIBLE);
            star4.setVisibility(View.VISIBLE);
        }
        else if(ratings >= 75 && ratings <= 99)
        {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.VISIBLE);
        }
        else if(ratings >= 50 && ratings <= 74)
        {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
        }
        else if(ratings >= 25 && ratings <= 49)
        {
            star1.setVisibility(View.VISIBLE);
        }
        else
        {
            ifnoStar.setText("POOR PERFORMANCE!");
        }
    }

    public void displayHistory(String whatType)
    {
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference(whatType);
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
             conts.setText("");

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
                                    if(whatType.equals("AidProviderHistory")) {
                                        if (String.valueOf(snaps.child("provider_id").getValue()).equals(CurrentUID)) {
                                            String dt = String.valueOf(snaps.child("timedate").getValue()).substring(4, 16);
                                            String rp = String.valueOf(snaps.child("aidORsupport").getValue());
                                            String sn = String.valueOf(snaps.child("seekername").getValue());


                                            String space = "                     ";
                                            if (rp.equals("Support")) {
                                                space = "                      ";
                                            }

                                            String temp = "   " + dt + "             " + rp + space + sn + "\n\n";


                                            conts.append(temp);

                                        }
                                    }
                                    else
                                    {
                                        if (String.valueOf(snaps.child("seekeruid").getValue()).equals(CurrentUID)) {
                                            String dt = String.valueOf(snaps.child("timedate").getValue()).substring(4,16);
                                            String et = String.valueOf(snaps.child("emergencytype").getValue());
                                            String pn = String.valueOf(snaps.child("providername").getValue());


                                            //5/1/2023
                                            if(et.equals("All"))
                                            {
                                                et = "Critical";
                                            }
                                            //---
                                           conts.append("   "+dt + "                  "+et+"                  "+pn+"\n\n");

                                        }
                                    }

                                }
                                else
                                {
                                    Toast.makeText(AdminManageRecordsSecondPage.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AdminManageRecordsSecondPage.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
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