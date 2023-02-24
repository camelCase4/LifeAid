package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AidProviderMainDash extends AppCompatActivity {

    ImageView seekerAlerts,alarmimage,alarm2;

    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Aid-Seeker");

    public static String latiOfSeeker = "",longiOfSeeker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_main_dash);

        seekerAlerts = (ImageView) findViewById(R.id.imageView20);
        alarmimage = (ImageView) findViewById(R.id.imageView25);
        alarm2 = (ImageView) findViewById(R.id.imageView23);


        checkForSeekers();

        alarmimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForSeekers();
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

                                    if(!temp_lat.equals(""))
                                    {
                                        latiOfSeeker = temp_lat;
                                        longiOfSeeker = temp_longi;

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
                        alarmimage.setImageResource(R.drawable.siren);
                        alarm2.setImageResource(R.drawable.redsiren);
                        break;
                    }

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}