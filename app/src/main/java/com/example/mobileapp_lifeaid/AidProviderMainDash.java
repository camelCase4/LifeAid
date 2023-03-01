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

    ImageView seekerAlerts,alarmimage,alarm2;
    TextView tap;
    TextView als;

    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Aid-Seeker");

    boolean findSeekerClicked = false;

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

        checkForSeekers();

        alarmimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForSeekers();
                findSeekerClicked = true;
            }
        });




        seekerAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidProviderMainDash.this, MapsActivityAidProvider.class);
                startActivity(intent);
            }
        });


    }


    public void checkForSeekers()
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


                                    if(jobchoice.toLowerCase().equals(ma.ap_job.toLowerCase()) || jobchoice.equals("all"))
                                    {
                                        if(!temp_lat.equals(""))
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
                        Toast.makeText(AidProviderMainDash.this, "Please Respond!", Toast.LENGTH_SHORT).show();
                        tap.setText("S E E K E R   F O U N D !");
                        als.setText("ALERT FOUND!");
                        alarm2.setImageResource(R.drawable.redsiren);
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