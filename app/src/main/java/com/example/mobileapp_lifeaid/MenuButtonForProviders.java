package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;

public class MenuButtonForProviders extends AppCompatActivity implements LocationListener {

    Button aidAsking,claim,editAcc,faqs,lgout;
    ImageView bk;
    public static String latitudePos, longitudePos;
    LocationManager lm;

    public static String generatedUID = "";

    MainActivity ma = new MainActivity();
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");


    //3/30/2023
    public static boolean gotLoc = false;
    //---

    //4/2/2023
    boolean seekClicked = false;
    //---

    private static final int PERMISSION_REQUEST_CODE_LOC = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_button_for_providers);
        overridePendingTransition(R.anim.slide_in_from_right,0);

        aidAsking = (Button) findViewById(R.id.askaid);
        claim = (Button) findViewById(R.id.claimcert);
        //4/3/2023
        editAcc = (Button) findViewById(R.id.editacc);
        faqs = (Button) findViewById(R.id.faqbtn);
        lgout = (Button) findViewById(R.id.loginbutton3);
        bk = (ImageView) findViewById(R.id.back);


        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        lgout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuButtonForProviders.this,MainActivity.class);
                startActivity(intent);
            }
        });

        faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuButtonForProviders.this,FAQPage.class);
                startActivity(intent);
            }
        });

        editAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuButtonForProviders.this,UserEditingPage.class);
                startActivity(intent);
            }
        });
        //--


        claim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuButtonForProviders.this,AidProviderClaimCert.class);
                startActivity(intent);
            }
        });

        aidAsking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askingForAssurance();
            }
        });


    }

    public void askingForAssurance()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        seekClicked = true; // 4/2/2023
                        seekAid();
                        //Toast.makeText(MenuButtonForProviders.this, "Please Wait!", Toast.LENGTH_SHORT).show();//3/26/2023
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //do nothing
                        break;

                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you really in need of assistance?").setPositiveButton("YES!",dialogClickListener).setNegativeButton("No, misclicked",dialogClickListener).show();
    }

    public void seekAid()
    {
        getLoc();

    }
    public void getLoc()
    {
        /*if(ContextCompat.checkSelfPermission(MenuButtonForProviders.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MenuButtonForProviders.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,MenuButtonForProviders.this);*/

        //4/6/2023
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, so request it
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_CODE_LOC);
        } else {
            // Permission is already granted, so get the location updates
            lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            Toast.makeText(MenuButtonForProviders.this, "Please Wait!", Toast.LENGTH_SHORT).show();//4/6/2023
        }
        //---


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
       /* if(!gotLoc) { //added if clause 3/30/2023
            gotLoc = true;

            latitudePos = Double.toString(location.getLatitude());
            longitudePos = Double.toString(location.getLongitude());

            addingToSeekerList();
        }*/

        //4/2/2023
        if(seekClicked) {
            seekClicked = false;
            latitudePos = Double.toString(location.getLatitude());
            longitudePos = Double.toString(location.getLongitude());

            addingToSeekerList();
        }
        //----

    }

    public void addingToSeekerList()
    {
        AdminAndProviderAid apa = new AdminAndProviderAid(ma.userrole,latitudePos,longitudePos,"","","all",ma.userid);
        FirebaseDatabase.getInstance().getReference("Aid-Seeker").push().setValue(apa).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    gettingTheGeneratedUID(); // 3/26/2023

                }
                else
                {
                    Toast.makeText(MenuButtonForProviders.this, "Failed to call aid!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //generatedUID = FirebaseDatabase.getInstance().getReference("Aid-Seeker").push().getKey();

                Intent intent = new Intent(MenuButtonForProviders.this,SeekAidButNotSeeker.class);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to write data to the database
                Toast.makeText(MenuButtonForProviders.this, "Failed to call aid!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //3/26/2023
    public void gettingTheGeneratedUID()
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
                                    if(String.valueOf(snaps.child("id").getValue()).equals(ma.userid))
                                    {
                                        generatedUID = uid;
                                    }

                                }
                                else
                                {
                                    Toast.makeText(MenuButtonForProviders.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(MenuButtonForProviders.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    if(!generatedUID.equals(""))
                    {
                        break;
                    }

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });


    }
    //---
    //4/6/2023
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_LOC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLoc();
            } else {

                Toast.makeText(this, "Permission denied, Cannot Proceed With Aid-Request!", Toast.LENGTH_SHORT).show();

            }
        }

    }
    //---


}