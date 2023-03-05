package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

    ImageView alertallbtn;
    Button btncrime,btnfire,btnhealth;

    MainActivity ma = new MainActivity();

    int presscounter = 0, whatjob = 0; //0? all, 1?crime, 2?fire, 3?health

    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Aid-Seeker");

    String theLatInStr = "",theLongInStr = "";
    //checkpoint 2/22/20233
    LocationManager lm;
    //----

    //3/5/2023
    boolean providerFound = false;
    String responderUID = "";
    //--------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_seeker_main_dash);

        alertallbtn = (ImageView) findViewById(R.id.imageView34);
        btncrime = (Button) findViewById(R.id.btn_register2);
        btnfire = (Button) findViewById(R.id.btn_register3);
        btnhealth = (Button) findViewById(R.id.btn_register5);


        alertallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presscounter++;
                if(presscounter >= 2) {
                    getLoc();
                    presscounter = 0;
                    //3/1/2023 checkpoint
                    btncrime.setEnabled(false);
                    btnfire.setEnabled(false);
                    btnhealth.setEnabled(false);
                    whatjob = 0;
                    //---------
                }
                //storing();

            }
        });

        //checkpoint 3/1/2023
        btncrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presscounter++;
                if(presscounter >= 2)
                {
                    getLoc();
                    presscounter = 0;
                    whatjob = 1;
                    alertallbtn.setEnabled(false);
                    btnfire.setEnabled(false);
                    btnhealth.setEnabled(false);
                }
            }
        });

        btnfire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presscounter++;
                if(presscounter >= 2)
                {
                    getLoc();
                    presscounter = 0;
                    whatjob = 2;
                    alertallbtn.setEnabled(false);
                    btncrime.setEnabled(false);
                    btnhealth.setEnabled(false);
                }
            }
        });

        btnhealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presscounter++;
                if(presscounter >= 2)
                {
                    getLoc();
                    presscounter = 0;
                    whatjob = 3;
                    alertallbtn.setEnabled(false);
                    btncrime.setEnabled(false);
                    btnfire.setEnabled(false);
                }
            }
        });
        //-------
    }

    //checkpoint 2/22/2023
    public void getLoc()
    {
        if(ContextCompat.checkSelfPermission(AidSeekerMainDash.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(AidSeekerMainDash.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,AidSeekerMainDash.this);



    }
    //-------
    //checkpoint 2/22/2023
    @Override
    public void onLocationChanged(@NonNull Location location) {
        theLatInStr = Double.toString(location.getLatitude());
        theLongInStr = Double.toString(location.getLongitude());


        storing();
        smsSending();



    }
    //-------

    //checkpoint 2/22/2023
    public void storing()
    {
        //3/1/2023 checkpoint
        String jobDesc = "";
        if(whatjob == 1)
        {
            jobDesc = "policeman";
        }
        else if(whatjob == 2)
        {
            jobDesc = "fireman";
        }
        else if(whatjob == 3)
        {
            jobDesc = "health";
        }
        else
        {
            jobDesc = "all";
        }
        //-----
        HashMap hm = new HashMap();
        hm.put("lati",theLatInStr);
        hm.put("longi",theLongInStr);
        hm.put("job",jobDesc);//checkopint 3/1/2023

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(AidSeekerMainDash.this, "Wait for an Aid-Provider! Hang in there!", Toast.LENGTH_SHORT).show();
                waitforresponder();//test 3/5/2023

            }
        });


    }
    //----

    //checkpoint 2/26/2023
    public void smsSending()
    {

        if(ma.trustedcontact1.isEmpty() || ma.trustedcontact1.equals(""))
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

                                    ma.trustedcontact1 = String.valueOf(snaps.child("trustedphonenum_1").getValue());
                                    ma.trustedcontact2 = String.valueOf(snaps.child("trustedphonenum_2").getValue());

                                    partnerSMS();
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
        else {
            partnerSMS();
        }
    }
    //

    public void partnerSMS()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);

        String messagetobesent = ma.fullname + " is at, Latitude: " + theLatInStr + ", " + "Longitude: " + theLongInStr + ", and in need of aid!";
        SmsManager smsManager = SmsManager.getDefault();

        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                smsManager.sendTextMessage(ma.trustedcontact1, null, messagetobesent, null, null);
            } else {
                smsManager.sendTextMessage(ma.trustedcontact2, null, messagetobesent, null, null);
            }
        }

        Toast.makeText(AidSeekerMainDash.this, "Trusted contacts informed!", Toast.LENGTH_SHORT).show();
    }

    //checkpoint 3/5/2023
    public void waitforresponder()
    {
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

        }
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
}