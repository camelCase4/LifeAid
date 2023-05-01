package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuForAdmins extends AppCompatActivity implements LocationListener {

    ImageView home,homepage;
    TextView homedash;
    Button giveCert,faqs,lgout,editacc,askAid,adminNotif;

    //4/7/2023
    private static int PERMISSION_REQUEST_CODE_LOC = 99;
    private static final int REQUEST_CHECK_SETTINGS = 1001;
    public static String generatedUID = "";
    boolean seekClicked = false;
    LocationManager lm;
    public static String latitudePos, longitudePos;
    MainActivity ma = new MainActivity();
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
    //---

    String whatIsClicked = "";//5/1
    boolean stopper = true;//4/26/2023

    CountDownTimer cdt;//5/1
    int numTimer = 0; //5/1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_for_admins);
        overridePendingTransition(R.anim.slide_in_from_right,0);

        giveCert = (Button) findViewById(R.id.givecert);
        //4/4/2023
        faqs = (Button) findViewById(R.id.faqbtn);
        lgout = (Button) findViewById(R.id.loginbutton3);
        editacc = (Button) findViewById(R.id.editacc);
        askAid = (Button) findViewById(R.id.askaid);
        home = (ImageView) findViewById(R.id.back);
        //4/16/2023
        homepage = (ImageView) findViewById(R.id.imageHomeDash);
        homedash = (TextView) findViewById(R.id.tv_homedash);

        //5/1/2023
        adminNotif = (Button) findViewById(R.id.andminNotif);

        informingAdmin();
        adminNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatIsClicked = "2";
                cdt.cancel();
                adminNotif.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                adminNotif.setTextColor(Color.BLACK);
                checkIfLocationIsOn();
            }
        });
        //----

        homedash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,AdminMainDash.class);
                startActivity(intent);
            }
        });
        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,AdminMainDash.class);
                startActivity(intent);
            }
        });
        //---

        editacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,UserEditingPage.class);
                startActivity(intent);
            }
        });

        faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,FAQPage.class);
                startActivity(intent);
            }
        });
        lgout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,MainActivity.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        //---

        giveCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,AdminGiveCerts.class);
                startActivity(intent);
            }
        });

        //4/7/2023
        askAid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatIsClicked = "1";//5/1
                checkIfLocationIsOn();
            }
        });
        //---
    }
    //4/7/2023
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHECK_SETTINGS)
        {
            if(resultCode == RESULT_OK)
            {
                askingForAssurance();
            }
            else
            {
                Toast.makeText(MenuForAdmins.this,"Please turn the location on!",Toast.LENGTH_SHORT).show();

            }
        }

    }
    public void checkIfLocationIsOn() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // Add an OnCompleteListener to handle the result of location settings check
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // Location settings are satisfied, proceed to get location updates
                    //getLoc();
                    askingForAssurance();
                } catch (ApiException e) {
                    // Location settings are not satisfied, show a dialog to prompt the user to enable it
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MenuForAdmins.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Handle the exception
                        }
                    }
                }
            }
        });
    }
    //---

    public void askingForAssurance()
    {
        /*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

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
        *///commetend on 5/1

        //5/1/2023
        if(whatIsClicked.equals("1"))
        {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i){
                        case DialogInterface.BUTTON_POSITIVE:
                            seekClicked = true; // 4/2/2023
                            whatIsClicked = "";
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
        else
        {
            whatIsClicked = "";
           Intent intent = new Intent(MenuForAdmins.this,MapsActivityNotificationOfAdmins.class);
           startActivity(intent);
        }
        //----
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
            Toast.makeText(MenuForAdmins.this, "Please Wait!", Toast.LENGTH_SHORT).show();//4/6/2023
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
        AdminAndProviderAid apa = new AdminAndProviderAid(ma.userrole,latitudePos,longitudePos,"","","all",ma.userid,ma.phonenum,ma.fullname.split(" ")[0]);//added fname on 4/23
        FirebaseDatabase.getInstance().getReference("Aid-Seeker").push().setValue(apa).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    gettingTheGeneratedUID(); // 3/26/2023

                }
                else
                {
                    Toast.makeText(MenuForAdmins.this, "Failed to call aid!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //generatedUID = FirebaseDatabase.getInstance().getReference("Aid-Seeker").push().getKey();

                /*Intent intent = new Intent(MenuForAdmins.this,SeekAidButNotSeekerAdmin.class);
                startActivity(intent);*/ //commented on 4/26

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to write data to the database
                Toast.makeText(MenuForAdmins.this, "Failed to call aid!", Toast.LENGTH_SHORT).show();
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

                    //4/26/2023
                    if(!stopper)
                    {
                        break;
                    }
                    //---
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

                                        stopper = false;//4/26/2023
                                        //4/26/2023
                                        Intent intent = new Intent(MenuForAdmins.this,SeekAidButNotSeekerAdmin.class);
                                        startActivity(intent);
                                        //---
                                    }

                                }
                                else
                                {
                                    Toast.makeText(MenuForAdmins.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(MenuForAdmins.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    /*if(!generatedUID.equals(""))
                    {
                        break;
                    }*/ //commented on 4/26

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

    //5/1/2023
    public void informingAdmin()
    {
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                /*cdt.cancel();
                adminNotif.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                adminNotif.setTextColor(Color.BLACK);*/ //commetend on 5/2
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

                                    if(!String.valueOf(snaps.child("lati").getValue()).equals(""))
                                    {
                                        blinkingEffect();
                                    }



                                }
                                else
                                {
                                    Toast.makeText(MenuForAdmins.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(MenuForAdmins.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
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

    public void blinkingEffect()
    {

        cdt = new CountDownTimer(300000,500) {
            @Override
            public void onTick(long l) {
                numTimer++;
                if(numTimer % 2 == 0)
                {
                    adminNotif.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff6666")));
                    adminNotif.setTextColor(Color.WHITE);
                }
                else
                {
                    adminNotif.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                    adminNotif.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    //---



}