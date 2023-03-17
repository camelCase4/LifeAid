package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
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

import java.util.Locale;

public class AidProviderMainDash extends AppCompatActivity {

    MainActivity ma = new MainActivity();

    public static String seeker_id = "",seekerfName = ""; //checkpoint 3/3/2023

    ImageView seekerAlerts,alarmimage,alarm2;
    TextView tap;
    TextView als;
    TextView leaderboard;

    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Aid-Seeker");

    boolean findSeekerClicked = false, seekerfound = false;

    public static String latiOfSeeker = "",longiOfSeeker = "";




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




        latiOfSeeker = "";
        longiOfSeeker = "";
        checkForSeekers();

        //checkpoint 3/5/2023
        //tap.setText("T A P   T O   F I N D   S E E K E R S");
        //alarm2.setImageResource(R.drawable.waitingalertstwo);
        //als.setText("WAITING ALERTS");
        //----------

        alarmimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForSeekers();
                findSeekerClicked = true;
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
                    seeker_id = key;

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
                                    seekerfName = String.valueOf(snaps.child("fname").getValue()); // checkpoint 3/3/2023


                                    if(jobchoice.toLowerCase().equals(ma.ap_job.toLowerCase()) || jobchoice.equals("all"))
                                    {
                                        if(!temp_lat.equals("") && providerID.equals(""))
                                        {
                                            latiOfSeeker = temp_lat;
                                            longiOfSeeker = temp_longi;

                                        }
                                    }//else if below 3/5/2023
                                    else if((jobchoice.equals("health") && ma.ap_job.toLowerCase().equals("nurse")) || (jobchoice.equals("health") && ma.ap_job.toLowerCase().equals("doctor")))
                                    {
                                        if(!temp_lat.equals("") && providerID.equals(""))
                                        {
                                            latiOfSeeker = temp_lat;
                                            longiOfSeeker = temp_longi;

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
                        break;
                    }

                }

                // checkpoint 2/26/2023
                if(latiOfSeeker.equals("") && findSeekerClicked)
                {
                    findSeekerClicked = false;
                    Toast.makeText(AidProviderMainDash.this, "No seekers for now!", Toast.LENGTH_SHORT).show();

                }
                //-----


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}