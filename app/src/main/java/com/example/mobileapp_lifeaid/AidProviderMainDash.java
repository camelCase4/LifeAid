package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import java.util.Locale;

public class AidProviderMainDash extends AppCompatActivity {

    MainActivity ma = new MainActivity();

    public static String seeker_id = "",seekerfName = ""; //checkpoint 3/3/2023

    ImageView seekerAlerts,alarmimage,alarm2;
    TextView tap;
    TextView als;
    TextView leaderboard;
    TextView viewHistory;
    TextView providerRecords;

    ImageView menu;

    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Aid-Seeker");

    boolean findSeekerClicked = true, seekerfound = false;
    //3/31/2023 ge ilisan nako ang findsekersclicked from false to true;

    public static String latiOfSeeker = "",longiOfSeeker = "";

    //3/31/2023
    CountDownTimer cdFind;
    boolean ifreadytoclicktower = false;
    //---




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_main_dash);

        seekerAlerts = (ImageView) findViewById(R.id.imageView20);
        alarmimage = (ImageView) findViewById(R.id.imageView25);
        alarm2 = (ImageView) findViewById(R.id.imageView23);

        tap = (TextView) findViewById(R.id.taptap);
        als = (TextView) findViewById(R.id.tvAlertings);
        leaderboard = (TextView) findViewById(R.id.tv_registration15);
        viewHistory = (TextView) findViewById(R.id.tv_registration14);
        providerRecords = (TextView) findViewById(R.id.tv_registration16);

        menu = (ImageView) findViewById(R.id.imageView18);




        latiOfSeeker = "";
        longiOfSeeker = "";
        //checkForSeekers(); commented on 3/31/2023

        startingTheSearch();









        //checkpoint 3/5/2023
        //tap.setText("T A P   T O   F I N D   S E E K E R S");
        //alarm2.setImageResource(R.drawable.waitingalertstwo);
        //als.setText("WAITING ALERTS");
        //----------

        //3/24/2023
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidProviderMainDash.this,MenuButtonForProviders.class);
                startActivity(intent);
            }
        });
        //----
        //3/20/2023
        providerRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidProviderMainDash.this,AidProviderRecords.class);
                startActivity(intent);
            }
        });
        //----
        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidProviderMainDash.this,AidProviderHistory.class);
                startActivity(intent);
            }
        });

        alarmimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkForSeekers(); //commented on 3/31/2023

                //3/31/2023
                if(ifreadytoclicktower) {
                    startingTheSearch();
                    findSeekerClicked = true;
                }
                else
                {
                    Toast.makeText(AidProviderMainDash.this,"Still Searching...",Toast.LENGTH_SHORT).show();
                }
                //---
                //findSeekerClicked = true;//commented on 3/31/2023
            }
        });

        //3/17/2023 cp
        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidProviderMainDash.this,AidProviderLeaderboardDash.class);
                startActivity(intent);
            }
        });
        //----




        seekerAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(seekerfound) {
                    seekerfound = false;

                    Intent intent = new Intent(AidProviderMainDash.this, MapsActivityAidProvider.class);
                    startActivity(intent);


                }
                else
                {
                    Toast.makeText(AidProviderMainDash.this, "Find Seekers First!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    protected void checkForSeekers()
    {
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String key = ds.getKey();


                    dr.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();

                                    String temp_lat = String.valueOf(snaps.child("lati").getValue());
                                    String temp_longi = String.valueOf(snaps.child("longi").getValue());
                                    String jobchoice = String.valueOf(snaps.child("job").getValue()); //checkpoint 3/1/2023
                                    String providerID = String.valueOf(snaps.child("partner_uid").getValue()); //checkpoint 3/5/2023
                                    //seekerfName = String.valueOf(snaps.child("fname").getValue()); // checkpoint 3/3/2023



                                    if(jobchoice.toLowerCase().equals(ma.ap_job.toLowerCase()) || jobchoice.equals("all"))
                                    {
                                        if(!temp_lat.equals("") && providerID.equals(""))
                                        {
                                            latiOfSeeker = temp_lat;
                                            longiOfSeeker = temp_longi;
                                            seeker_id = key;//checkpoint 3/17/2023
                                            seekerfName = String.valueOf(snaps.child("fname").getValue()); //3/22/2023

                                        }
                                    }//else if below 3/5/2023
                                    else if((jobchoice.equals("health") && ma.ap_job.toLowerCase().equals("nurse")) || (jobchoice.equals("health") && ma.ap_job.toLowerCase().equals("doctor")))
                                    {
                                        if(!temp_lat.equals("") && providerID.equals(""))
                                        {
                                            latiOfSeeker = temp_lat;
                                            longiOfSeeker = temp_longi;
                                            seeker_id = key;//checkpoint 3/17/2023
                                            seekerfName = String.valueOf(snaps.child("fname").getValue()); //3/22/2023

                                        }
                                    }

                                }
                                else
                                {
                                    Toast.makeText(AidProviderMainDash.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AidProviderMainDash.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    if(!latiOfSeeker.equals(""))
                    {
                        //Toast.makeText(AidProviderMainDash.this, "Please Respond!", Toast.LENGTH_SHORT).show();
                        tap.setText("S E E K E R   F O U N D !");
                        als.setText("ALERT FOUND!");
                        alarm2.setImageResource(R.drawable.redsiren);
                        seekerfound = true;
                        //3/31/2023
                        cdFind.cancel();
                        //---
                        break;
                    }

                }

                // checkpoint 2/26/2023
                if(latiOfSeeker.equals("") && findSeekerClicked)
                {
                    findSeekerClicked = false;
                    //Toast.makeText(AidProviderMainDash.this, "No seekers for now!", Toast.LENGTH_SHORT).show(); commented on 31,2023
                    //3/31/2023
                    tap.setText("S E A R C H I N G . . .");
                    //---
                }
                //-----


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //3/31/2023
    public void askingForExtension()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        startingTheSearch();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(AidProviderMainDash.this,"Just tap tower when you are ready again!",Toast.LENGTH_SHORT).show();
                        ifreadytoclicktower = true;
                        cdFind.cancel();
                        tap.setText("T A P   T O   F I N D   S E E K E R S");
                        break;

                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(AidProviderMainDash.this);
        builder.setMessage("There are no Aid-Seekers for now, continue searching?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    public void startingTheSearch()
    {
        //3/31/2023
        cdFind = new CountDownTimer(300000,1000) {
            @Override
            public void onTick(long l) {
                checkForSeekers();

            }

            @Override
            public void onFinish() {
                askingForExtension();
            }
        }.start();
        //---
    }

    //----

}