package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;
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

    //4/2/2023
    int countertime = 0;
    //---


    //4/4/2023
    public static final int MENU_REQUEST_CODE = 1;
    //--

    private static final int PERMISSION_REQUEST_CODE_MAPS2 = 102;//4/5/2023

    //4/6/2023
    private static final int REQUEST_CHECK_SETTINGS = 1001;//4/6/2023
    public static boolean isLocationEnabled = false;
    //--

    //4/14/2023
    private static final String CHANNEL_ID = "my_channel";
    private static final int NOTIFICATION_ID = 1;
    public static int toOccurOnce = 0;
    private static final int VIBRATION_DURATION = 1000;

    public static List<String> ignoredID = new ArrayList<>();
    //--

    public static String seekerPhoneNum = ""; //4/16/2023


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

        checkIfLocationIsOn(); //4/6/2023


        createNotificationChannel();//4/14/2023







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
                //startActivity(intent);
                startActivityForResult(intent,MENU_REQUEST_CODE);//4/4/2023
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
                /*if(seekerfound) {
                    seekerfound = false;

                    //Intent intent = new Intent(AidProviderMainDash.this, MapsActivityAidProvider.class);
                    //startActivity(intent);


                    //4/5/2023
                    if (ContextCompat.checkSelfPermission(AidProviderMainDash.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Permission is not granted, so request it
                        ActivityCompat.requestPermissions(AidProviderMainDash.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_CODE_MAPS2);
                    } else {

                        Intent intent = new Intent(AidProviderMainDash.this,MapsActivityAidProvider.class);
                        startActivity(intent);
                    }
                    //---



                }
                else
                {
                    Toast.makeText(AidProviderMainDash.this, "Find Seekers First!", Toast.LENGTH_SHORT).show();
                }*/

                //4/6/2023
                if(isLocationEnabled)
                {
                    if(seekerfound) {
                        seekerfound = false;

                    /*Intent intent = new Intent(AidProviderMainDash.this, MapsActivityAidProvider.class);
                    startActivity(intent);*/


                        //4/5/2023
                        if (ContextCompat.checkSelfPermission(AidProviderMainDash.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted, so request it
                            ActivityCompat.requestPermissions(AidProviderMainDash.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_CODE_MAPS2);
                        } else {

                            Intent intent = new Intent(AidProviderMainDash.this,MapsActivityAidProvider.class);
                            startActivity(intent);
                        }
                        //---



                    }
                    else
                    {
                        Toast.makeText(AidProviderMainDash.this, "Find Seekers First!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(AidProviderMainDash.this, "Enable the location in your device, and try again.", Toast.LENGTH_LONG).show();
                    checkIfLocationIsOn();
                }
                //---
            }
        });


    }
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
                        .setContentText("We found an Aid - Seeker!")
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
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // Request vibration permission
    private void requestVibrationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, 69);
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



                                    /*if(jobchoice.toLowerCase().equals(ma.ap_job.toLowerCase()) || jobchoice.equals("all"))
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
                                    }*/ //commented on 14 original

                                    //4/14/2023
                                    if(!ignoredID.contains(key))
                                    {
                                        if(jobchoice.toLowerCase().equals(ma.ap_job.toLowerCase()) || jobchoice.equals("all") || jobchoice.toLowerCase().contains(ma.ap_job.toLowerCase()))//added a new or on 16
                                        {
                                            if(!temp_lat.equals("") && providerID.equals(""))
                                            {
                                                latiOfSeeker = temp_lat;
                                                longiOfSeeker = temp_longi;
                                                seeker_id = key;//checkpoint 3/17/2023
                                                seekerfName = String.valueOf(snaps.child("fname").getValue()); //3/22/2023
                                                seekerPhoneNum = String.valueOf(snaps.child("phonenum").getValue()); //4/16/2023

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
                                                seekerPhoneNum = String.valueOf(snaps.child("phonenum").getValue()); //4/16/2023


                                            }
                                        }
                                    }
                                    //---

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
                        showNotification(); // 4/14/2023
                        //Toast.makeText(AidProviderMainDash.this, "Please Respond!", Toast.LENGTH_SHORT).show();
                        tap.setText("S E E K E R   F O U N D !");
                        als.setText("ALERT FOUND!");
                        alarm2.setImageResource(R.drawable.redsiren);
                        seekerfound = true;
                        //3/31/2023
                        cdFind.cancel();
                        //---

                        //4/1/2023
                        findSeekerClicked = true;
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
                    tap.setText("S E A R C H I N G   S E E K E R S ");
                    //---
                }
                //-----


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                            resolvable.startResolutionForResult(AidProviderMainDash.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Handle the exception
                        }
                    }
                }
            }
        });
    }
    //---
    //4/4/2023
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MENU_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the result
            // ...
        }
        //4/6/2023
        else if(requestCode == REQUEST_CHECK_SETTINGS)
        {
            if(resultCode == RESULT_OK)
            {
                isLocationEnabled = true;
            }
            else
            {
                Toast.makeText(AidProviderMainDash.this,"Please turn the location on!",Toast.LENGTH_SHORT).show();

            }
        }
        //---
    }
    //---

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

        toOccurOnce = 0; // 4/14/2023
        //3/31/2023
        cdFind = new CountDownTimer(300000,1000) {
            @Override
            public void onTick(long l) {
                checkForSeekers();
                //4/2/2023
                countertime++;
                if(countertime == 1)
                {
                    tap.setText("S E A R C H I N G   S E E K E R S ");
                }
                else if(countertime == 2)
                {
                    tap.setText("S E A R C H I N G   S E E K E R S . ");
                }
                else if(countertime == 3)
                {
                    tap.setText("S E A R C H I N G   S E E K E R S . . ");

                }
                else
                {
                    tap.setText("S E A R C H I N G   S E E K E R S . . . ");
                    countertime = 0;
                }
                //---

            }

            @Override
            public void onFinish() {
                askingForExtension();
            }
        }.start();
        //---
    }

    //----
    //4/5/2023
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_MAPS2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(AidProviderMainDash.this,MapsActivityAidProvider.class);
                startActivity(intent);

            } else {

                Toast.makeText(this, "Permission denied, Cannot Proceed!", Toast.LENGTH_SHORT).show();

            }
        }
        //4/14/2023
        else if(requestCode == 69)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Vibration permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        //---


    }
    //---

}