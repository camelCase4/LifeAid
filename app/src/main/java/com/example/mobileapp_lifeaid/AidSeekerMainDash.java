package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.os.Vibrator;
import android.telephony.SmsManager;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


// i added a new implements 2/22/2023
public class AidSeekerMainDash extends AppCompatActivity implements LocationListener {

    ImageView alertallbtn, menu;
    Button btncrime, btnfire, btnhealth;
    TextView openchat, mapcrisis;
    TextView seekerHist;

    TextView leaderB;

    MainActivity ma = new MainActivity();

    public static int presscounter = 0, whatjob = 0; //0? all, 1?crime, 2?fire, 3?health

    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Aid-Seeker");

    public static String theLatInStr = "", theLongInStr = "";
    //checkpoint 2/22/20233
    LocationManager lm;
    //----

    //3/5/2023
    boolean providerFound = false;
    public static String responderUID = "";
    //--------

    //3/10/2023
    public static boolean foundresponder = false;
    //-------

    //3/24/2023
    public static final int MENU_REQUEST_CODE = 1;
    //--

    //3/30/2023
    //public static boolean foundIt = false;
    //----

    //4/2/2023
    boolean ifusertap = false;
    //---

    private static final int PERMISSION_REQUEST_CODE = 100;//4/4/2023
    private static final int PERMISSION_REQUEST_CODE_SMS = 101;//4/4/2023
    private static final int PERMISSION_REQUEST_CODE_MAPS = 102;//4/5/2023
    private static final int REQUEST_CHECK_SETTINGS = 1001;//4/6/2023


    boolean isLocationEnabled = false;//4/6/2023

    //4/14/2023
    private static final String CHANNEL_ID = "my_channel";
    private static final int NOTIFICATION_ID = 1;
    public static int toOccurOnce = 0;
    private static final int VIBRATION_DURATION = 1000;
    //--


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_seeker_main_dash);

        alertallbtn = (ImageView) findViewById(R.id.imageView34);
        btncrime = (Button) findViewById(R.id.btn_register2);
        btnfire = (Button) findViewById(R.id.btn_register3);
        btnhealth = (Button) findViewById(R.id.btn_register5);
        openchat = (TextView) findViewById(R.id.tv_registration15);//3/10/2023
        //3/12/2023
        mapcrisis = (TextView) findViewById(R.id.tv_registration17);
        leaderB = (TextView) findViewById(R.id.tv_registration16);
        seekerHist = (TextView) findViewById(R.id.tv_registration14);

        menu = (ImageView) findViewById(R.id.imageView18);

        checkIfLocationIsOn();//4/6/2023

        createNotificationChannel();//4/14/2023


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(AidSeekerMainDash.this, MenuButtonForSeekers.class);
                startActivity(intent);*/
                Intent intent = new Intent(AidSeekerMainDash.this, MenuButtonForSeekers.class);
                startActivityForResult(intent, MENU_REQUEST_CODE);
            }
        });


        seekerHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidSeekerMainDash.this, AidSeekerHistory.class);
                startActivity(intent);
            }
        });


        leaderB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidSeekerMainDash.this, AidSeekerLeaderboardDash.class);
                startActivity(intent);
            }
        });


        mapcrisis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(AidSeekerMainDash.this,AidSeekerMapCrisis.class);
                startActivity(intent);*/

                //4/5/2023
               /* if (ContextCompat.checkSelfPermission(AidSeekerMainDash.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, so request it
                    ActivityCompat.requestPermissions(AidSeekerMainDash.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_MAPS);
                } else {

                    Intent intent = new Intent(AidSeekerMainDash.this, AidSeekerMapCrisis.class);
                    startActivity(intent);
                }*/
                //---

                //4/6/2023
                if (isLocationEnabled) {
                    if (ContextCompat.checkSelfPermission(AidSeekerMainDash.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, so request it
                        ActivityCompat.requestPermissions(AidSeekerMainDash.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_MAPS);
                    } else {

                        Intent intent = new Intent(AidSeekerMainDash.this, AidSeekerMapCrisis.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(AidSeekerMainDash.this, "Enable the location in your device, and try again.", Toast.LENGTH_LONG).show();
                    checkIfLocationIsOn();
                }
                //---
            }
        });
        //---------


        //3/10/2023
        openchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (foundresponder) {
                    foundresponder = false;
                    Intent intent = new Intent(AidSeekerMainDash.this, AidSeekerChat.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AidSeekerMainDash.this, "No Aid - Provider Yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //---------------
        alertallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*presscounter++;
                if (presscounter >= 2) {
                    //Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                    ifusertap = true; //4/2/2023
                    getLoc();
                    presscounter = 0;
                    //3/1/2023 checkpoint
                    btncrime.setEnabled(false);
                    btnfire.setEnabled(false);
                    btnhealth.setEnabled(false);
                    whatjob = 0;
                    //---------
                }*/ //original commented on 4/6/2023
                //storing();

                //4/6/2023
                if (isLocationEnabled) {
                    presscounter++;
                    if (presscounter >= 2) {
                        //Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                        toOccurOnce = 0;//4/14/2023
                        ifusertap = true; //4/2/2023
                        getLoc();
                        presscounter = 0;
                        //3/1/2023 checkpoint
                        btncrime.setEnabled(false);
                        btnfire.setEnabled(false);
                        btnhealth.setEnabled(false);
                        alertallbtn.setEnabled(false); //4/6/2023
                        whatjob = 0;
                        //---------
                    }
                } else {
                    presscounter++;
                    if (presscounter >= 2) {
                        Toast.makeText(AidSeekerMainDash.this, "Enable the location in your device, and try again.", Toast.LENGTH_LONG).show();
                        presscounter = 0;
                        checkIfLocationIsOn();
                    }
                }
                //---

            }
        });

        //checkpoint 3/1/2023
        btncrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*presscounter++;
                if (presscounter >= 2) {
                    //Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                    ifusertap = true; //4/2/2023
                    getLoc();
                    presscounter = 0;
                    whatjob = 1;
                    alertallbtn.setEnabled(false);
                    btnfire.setEnabled(false);
                    btnhealth.setEnabled(false);
                }*/ //commented on 4/6/2023 original
                //4/6/2023
                if (isLocationEnabled) {
                    presscounter++;
                    if (presscounter >= 2) {
                        //Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                        toOccurOnce = 0;//4/14/2023
                        ifusertap = true; //4/2/2023
                        getLoc();
                        presscounter = 0;
                        whatjob = 1;
                        alertallbtn.setEnabled(false);
                        btnfire.setEnabled(false);
                        btnhealth.setEnabled(false);
                        btncrime.setEnabled(false); //4/6/2023
                    }
                } else {
                    presscounter++;
                    if (presscounter >= 2) {
                        Toast.makeText(AidSeekerMainDash.this, "Enable the location in your device, and try again.", Toast.LENGTH_LONG).show();
                        presscounter = 0;
                        checkIfLocationIsOn();
                    }
                }
                //---
            }
        });

        btnfire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*presscounter++;
                if (presscounter >= 2) {
                    //Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                    ifusertap = true; //4/2/2023
                    getLoc();
                    presscounter = 0;
                    whatjob = 2;
                    alertallbtn.setEnabled(false);
                    btncrime.setEnabled(false);
                    btnhealth.setEnabled(false);
                }*/

                //4/6/2023
                if (isLocationEnabled) {
                    presscounter++;
                    if (presscounter >= 2) {
                        //Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                        toOccurOnce = 0;//4/14/2023
                        ifusertap = true; //4/2/2023
                        getLoc();
                        presscounter = 0;
                        whatjob = 2;
                        alertallbtn.setEnabled(false);
                        btncrime.setEnabled(false);
                        btnhealth.setEnabled(false);
                        btnfire.setEnabled(false);
                    }
                } else {
                    presscounter++;
                    if (presscounter >= 2) {
                        Toast.makeText(AidSeekerMainDash.this, "Enable the location in your device, and try again.", Toast.LENGTH_LONG).show();
                        presscounter = 0;
                        checkIfLocationIsOn();
                    }
                }
                //---
            }
        });

        btnhealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*presscounter++;
                if (presscounter >= 2) {
                    //Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                    ifusertap = true; //4/2/2023
                    getLoc();
                    presscounter = 0;
                    whatjob = 3;
                    alertallbtn.setEnabled(false);
                    btncrime.setEnabled(false);
                    btnfire.setEnabled(false);
                }*/
                //4/6/2023
                if (isLocationEnabled) {
                    presscounter++;
                    if (presscounter >= 2) {
                        //Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                        toOccurOnce = 0;//4/14/2023
                        ifusertap = true; //4/2/2023
                        getLoc();
                        presscounter = 0;
                        whatjob = 3;
                        alertallbtn.setEnabled(false);
                        btncrime.setEnabled(false);
                        btnfire.setEnabled(false);
                        btnhealth.setEnabled(false);
                    }
                } else {
                    presscounter++;
                    if (presscounter >= 2) {
                        Toast.makeText(AidSeekerMainDash.this, "Enable the location in your device, and try again.", Toast.LENGTH_LONG).show();
                        presscounter = 0;
                        checkIfLocationIsOn();
                    }
                }

                //---
            }
        });
        //-------
    }

    //4/6/2023
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
                    isLocationEnabled = true; //4/6/2023
                } catch (ApiException e) {
                    // Location settings are not satisfied, show a dialog to prompt the user to enable it
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(AidSeekerMainDash.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Handle the exception
                        }
                    }
                }
            }
        });
    }
    //---

    //3/24/2023
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MENU_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the result
            // ...
        }
        //4/6/2023
        else if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                isLocationEnabled = true;
            } else {
                Toast.makeText(AidSeekerMainDash.this, "Please turn the location on!", Toast.LENGTH_SHORT).show();

            }
        }
        //---
    }
    //---


    //4/4/2023
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLoc();
            } else {

                Toast.makeText(this, "Permission denied, Cannot Proceed With Aid-Request!", Toast.LENGTH_SHORT).show();
                btncrime.setEnabled(true);
                btnfire.setEnabled(true);
                btnhealth.setEnabled(true);
                alertallbtn.setEnabled(true);
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                partnerSMS();
            } else {
                Toast.makeText(this, "Permission denied, Cannot Proceed With SMS Request!", Toast.LENGTH_SHORT).show();
            }
        }
        //4/5/2023
        else if (requestCode == PERMISSION_REQUEST_CODE_MAPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(AidSeekerMainDash.this, AidSeekerMapCrisis.class);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Permission denied, Cannot Proceed With MapCrisis!", Toast.LENGTH_SHORT).show();
            }
        }
        //---
        //4/14/2023
        else if (requestCode == 69) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Vibration permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        //---

    }
    //---

    //checkpoint 2/22/2023
    public void getLoc() {
        /*if(ContextCompat.checkSelfPermission(AidSeekerMainDash.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(AidSeekerMainDash.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},100);
        }*/ // commented on 4/4/2023

        /*lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,AidSeekerMainDash.this);*/// commented on 4/4/2023

        //4/4/2023

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, so request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, so get the location updates
            lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
        }

        //---


    }

    //-------
    //checkpoint 2/22/2023
    @Override
    public void onLocationChanged(@NonNull Location location) {
        /*theLatInStr = Double.toString(location.getLatitude());
        theLongInStr = Double.toString(location.getLongitude());


        storing();
        smsSending();*/

        //3/30/2023
        /*if(!foundIt)
        {
            foundIt = true;
            theLatInStr = Double.toString(location.getLatitude());
            theLongInStr = Double.toString(location.getLongitude());


            storing();
            smsSending();
        }*/
        //---

        //4/2/2023
        if (ifusertap) {
            ifusertap = false;
            theLatInStr = Double.toString(location.getLatitude());
            theLongInStr = Double.toString(location.getLongitude());

            storing();
            smsSending();
        }
        //----


    }
    //-------

    //checkpoint 2/22/2023
    public void storing() {
        //3/1/2023 checkpoint
        String jobDesc = "";
        if (whatjob == 1) {
            jobDesc = "policeman";
        } else if (whatjob == 2) {
            jobDesc = "fireman";
        } else if (whatjob == 3) {
            jobDesc = "health";
        } else {
            jobDesc = "all";
        }
        //-----
        HashMap hm = new HashMap();
        hm.put("lati", theLatInStr);
        hm.put("longi", theLongInStr);
        hm.put("job", jobDesc);//checkopint 3/1/2023
        hm.put("commends", "0");//4/5/2023
        hm.put("partner_uid", "");//4/5/2023

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                waitforresponder();//test 3/5/2023

            }
        });


    }
    //----

    //checkpoint 2/26/2023
    public void smsSending() {

        if (ma.trustedcontact1.isEmpty() || ma.trustedcontact1.equals("")) {
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    dr.child(ma.userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    DataSnapshot snaps = task.getResult();

                                    ma.trustedcontact1 = String.valueOf(snaps.child("trustedphonenum_1").getValue());
                                    ma.trustedcontact2 = String.valueOf(snaps.child("trustedphonenum_2").getValue());

                                    //partnerSMS();

                                    //3/27/2023
                                    if (!ma.trustedcontact1.equals("")) {
                                        partnerSMS();
                                    }
                                    //----
                                } else {
                                    Toast.makeText(AidSeekerMainDash.this, "Data does not exist!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AidSeekerMainDash.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            partnerSMS();
        }
    }
    //

    public void partnerSMS() {
        /*ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);

        String messagetobesent = ma.fullname + " is at, Latitude: " + theLatInStr + ", " + "Longitude: " + theLongInStr + ", and in need of aid!";
        SmsManager smsManager = SmsManager.getDefault();

        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                smsManager.sendTextMessage(ma.trustedcontact1, null, messagetobesent, null, null);
            } else {
                smsManager.sendTextMessage(ma.trustedcontact2, null, messagetobesent, null, null);
            }
        }

        Toast.makeText(AidSeekerMainDash.this, "Trusted contacts informed!", Toast.LENGTH_SHORT).show();*/

        //4/4/2023
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, so request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE_SMS);
        } else {
            // Permission is already granted, so send the SMS messages
            String messagetobesent = "[This is an auto generated message by LifeAid] " + ma.fullname + " is at, Latitude: " + theLatInStr + ", " + "Longitude: " + theLongInStr + ", and in need of aid!";
            SmsManager smsManager = SmsManager.getDefault();


            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    smsManager.sendTextMessage(ma.trustedcontact1, null, messagetobesent, null, null);
                } else if (i == 1) {
                    smsManager.sendTextMessage(ma.trustedcontact2, null, messagetobesent, null, null);
                }
                //4/16/2023
                else {
                    // test rani nga LGU number we cant use the real 1
                    smsManager.sendTextMessage("09655502568", null, messagetobesent, null, null);
                }
                ///---
            }



            Toast.makeText(AidSeekerMainDash.this, "Trusted contacts and LGU informed!", Toast.LENGTH_SHORT).show();
        }
        //----
    }



    //checkpoint 3/5/2023
    public void waitforresponder()
    {
        /*
        for(;;)
        {
            try {

                gettingtheproviderID();
                if(providerFound)
                {
                    Toast.makeText(AidSeekerMainDash.this, "Aid Provider coming! Go To Provider Info!", Toast.LENGTH_SHORT).show();
                    break;
                }
                Thread.sleep(5000);


            }catch(InterruptedException e)
            {

            }

        }*/

        //checkpoint 3/6/2023
        new CountDownTimer(300000,1000)
        {

            @Override
            public void onTick(long l) {
                if((l/1000) % 2 == 0) {
                    gettingtheproviderID();
                    if (providerFound) {
                        Toast.makeText(AidSeekerMainDash.this, "Aid Provider coming! Go To Provider Info!", Toast.LENGTH_SHORT).show();
                        foundresponder = true;
                        cancel();
                    }
                }
            }

            @Override
            public void onFinish() {
                askingForAssurance();
            }
        }.start();
        //-------
    }

    public void gettingtheproviderID()
    {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dr.child(ma.userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();

                                responderUID = String.valueOf(snaps.child("partner_uid").getValue());
                                if(!responderUID.equals(""))
                                {
                                    providerFound = true;
                                    showNotification(); // 4/14/2023
                                }
                            }
                            else
                            {
                                Toast.makeText(AidSeekerMainDash.this, "Data does not exist!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(AidSeekerMainDash.this,"Task was not successful!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //--------

    //4/5/2023
    public void askingForAssurance()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        waitforresponder();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        cancelCleaner();
                        btncrime.setEnabled(true);
                        btnfire.setEnabled(true);
                        btnhealth.setEnabled(true);
                        alertallbtn.setEnabled(true);
                        Intent intent = new Intent(AidSeekerMainDash.this,AidSeekerMainDash.class);
                        startActivity(intent);
                        break;

                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sorry, there are no Aid-Providers right now, continue requesting?").setPositiveButton("YES!",dialogClickListener).setNegativeButton("No, nevermind!",dialogClickListener).show();
    }

    public void cancelCleaner()
    {
        HashMap hm = new HashMap();
        hm.put("lati","");
        hm.put("longi","");
        hm.put("commends","1"); // ge reuse nako ang commends as a flag nga mo determine if ge cancel ba sa seeker

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(AidSeekerMainDash.this, "Take Care!", Toast.LENGTH_SHORT).show();

            }
        });
    }
    //---
    //4/14/2023
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification() {
        if(toOccurOnce == 0) {
            phoneVibration();
            toOccurOnce++;
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Toast.makeText(this, "Please enable notifications for LifeAid next time!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.alarm)
                        .setContentTitle("LifeAid Alert!")
                        .setContentText("Aid - Provider Found! Go to Provider Info!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }
    public void phoneVibration()
    {
        if (hasVibrationPermission()) {

            vibrateDevice();
        } else {

            requestVibrationPermission();
        }
    }
    private boolean hasVibrationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(AidSeekerMainDash.this, Manifest.permission.VIBRATE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // Request vibration permission
    private void requestVibrationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                ActivityCompat.requestPermissions(AidSeekerMainDash.this, new String[]{Manifest.permission.VIBRATE}, 69);
            }
            catch (Exception e)
            {

            }
        }

    }
    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VIBRATION_DURATION);
        }
    }

    //---
}