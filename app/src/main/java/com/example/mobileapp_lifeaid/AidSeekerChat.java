package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AidSeekerChat extends AppCompatActivity {

    // checkpoint 3/9/2023
    TextView fullnametv,numbertv,addresstv,sendbtn,conversation,duration;
    EditText msgHolder;
    Button complete;
    AidSeekerMainDash asm = new AidSeekerMainDash();
    DatabaseReference dbprovs = FirebaseDatabase.getInstance().getReference("Aid-Provider");
    MainActivity ma = new MainActivity();
    CountDownTimer cdt;

    String repeaterChecker = "",contentgetter="",dateandtime="",fnameprov="";
    String commendCount = "", unsatisfiedCount = "";

    //-----------
    public static String locationOfIncident = "";//changed to public static 5/2

    ImageView profileP;

    boolean stillpartners = true; //4/21/2023

    public static int totalWhoResponded = 0;//5/2/2023
    int counterForProvs = 0;//5/2/2023
    boolean stopper = true; //5/2/2023

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_seeker_chat);

        //checkpoint 3/9/2023
        fullnametv = (TextView) findViewById(R.id.tvfullname);
        numbertv = (TextView) findViewById(R.id.tvnumber);
        addresstv = (TextView) findViewById(R.id.tvplace);
        sendbtn = (TextView) findViewById(R.id.tv_sendbtn);
        msgHolder = (EditText) findViewById(R.id.editTextmsgholder);
        conversation = (TextView) findViewById(R.id.converse);
        complete = (Button) findViewById(R.id.completeTransac);
        duration = (TextView) findViewById(R.id.tvdur);

        profileP = (ImageView) findViewById(R.id.imageView44);//4/14/2023


        getProviderData();
        msgLooper();
        gettingLocationName();


        conversation.setMovementMethod(new ScrollingMovementMethod());
        msgHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgHolder.requestFocus();
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!msgHolder.getText().toString().equals("")) {
                    contentgetter = msgHolder.getText().toString();
                    msgSaver(contentgetter);
                    messageAppender("                                                                       "+contentgetter+"\n\n");

                }
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //asm.foundIt = false; //3/30/2023 commented on 4/2/2023
                                savingToHistory();
                                    /*ratingsPrompt();
                                    cleansingData();*/ //commented on 4/21/2023 original
                                //4/21/2023
                                checkIfStillPartners();
                                //---
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //do nothing
                                break;

                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(AidSeekerChat.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }

        });


        //------

    }
    //4/14/2023
    public void imageDisplayer(String urlpic)
    {
        Picasso.get().load(urlpic).into(profileP);
    }
    //---

    public void getProviderData()
    {
        dbprovs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dbprovs.child(asm.responderUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                DataSnapshot snaps = task.getResult();
                                String fullname = String.valueOf(snaps.child("fname").getValue()) + " " + String.valueOf(snaps.child("lname").getValue());
                                fullnametv.setText(fullname);
                                fnameprov = String.valueOf(snaps.child("fname").getValue());
                                numbertv.setText(String.valueOf(snaps.child("phonenum").getValue()));
                                addresstv.setText(String.valueOf(snaps.child("address").getValue()));
                                // 3/10/2023 10 pm
                                commendCount = String.valueOf(snaps.child("commends").getValue());
                                unsatisfiedCount = String.valueOf(snaps.child("decommends").getValue());
                                //--------

                                //4/14/2023
                                String imagepic = String.valueOf(snaps.child("trustedname_2").getValue());
                                if(!imagepic.equals("")) {
                                    imageDisplayer(imagepic);
                                }
                                //---
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

    public void getProviderMessage()
    {
        dbprovs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dbprovs.child(asm.responderUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();
                                String msg = String.valueOf(snaps.child("message").getValue());
                                //4/10/2023
                                String timeHolder = String.valueOf(snaps.child("trustedname_1").getValue());
                                if(timeHolder.equals("0 minutes"))
                                {
                                    duration.setText("Arrived!");
                                }
                                else {
                                    duration.setText(String.valueOf(snaps.child("trustedname_1").getValue()));
                                }
                                //---
                                if(!msg.equals(""))
                                {
                                    if(!msg.equals(repeaterChecker))
                                    {
                                        messageAppender("- "+msg+"\n");
                                        repeaterChecker = msg;
                                    }
                                }

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
    public void messageAppender(String chatmsg)
    {
        conversation.append(chatmsg+"\n");
        final int scrollAmount = conversation.getLayout().getLineTop(conversation.getLineCount()) - conversation.getHeight();

        if (scrollAmount > 0)
            conversation.scrollTo(0, scrollAmount);
        else
            conversation.scrollTo(0, 0);

        msgHolder.setText("");
        msgHolder.setHint("Write Something");
    }

    public void msgLooper()
    {
        cdt = new CountDownTimer(300000,1000)
        {

            @Override
            public void onTick(long l) {
                /*if((l/1000) % 2 == 0) {
                    getProviderMessage();
                }*/ //commented on 4/21/2023

                //4/21/2023
                getProviderMessage();
                //---
            }

            @Override
            public void onFinish() {
                updateChecker();

            }
        }.start();
    }
    public void msgSaver(String tobeput)
    {
        HashMap hm = new HashMap();
        hm.put("message",tobeput);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
    }

    public void savingToHistory()
    {
        Date currentDT = Calendar.getInstance().getTime();
        dateandtime = currentDT.toString();
        String emergencyType = "";

        if(asm.whatjob == 1)
        {
            emergencyType = "Crime";
        }
        else if(asm.whatjob == 2)
        {
            emergencyType = "Fire";
        }
        else if(asm.whatjob == 3)
        {
            emergencyType = "Health";
        }
        else
        {
            emergencyType = "All";
        }

        SeekerHistory sh = new SeekerHistory(dateandtime,emergencyType,fnameprov,asm.responderUID,ma.userid);
        FirebaseDatabase.getInstance().getReference("AidSeekerHistory").push().setValue(sh).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    //do nothing
                }
                else
                {
                    Toast.makeText(AidSeekerChat.this, "Failed to record!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateChecker()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        msgLooper();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //3/11/2023
                        msgLooper();
                        Toast.makeText(AidSeekerChat.this,"Operation won't end unless you complete transaction!",Toast.LENGTH_SHORT).show();
                        //---
                        //cdt.cancel();
                        break;

                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).show(); commented on 19
        builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).setCancelable(false).show();
    }

    //3/10/2023 10 pm
    public void ratingsPrompt(boolean continuationStatus) //added boolean on 4/21/2023
    {
       /* cdt.cancel();//3/11/2023
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        updatingCommendCount();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        updatingUnsatisfiedCount();
                        break;

                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setMessage("Did the provider do well?").setPositiveButton("Commended",dialogClickListener).setNegativeButton("No",dialogClickListener).show(); original cmtend in 18
        builder.setMessage("Did the provider do well?").setPositiveButton("Commended",dialogClickListener).setNegativeButton("No",dialogClickListener).setCancelable(false).show();
        */ //commented on 4/21/2023 original

        //4/21/2023
        if(continuationStatus)
        {
            cdt.cancel();//3/11/2023
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i){
                        case DialogInterface.BUTTON_POSITIVE:
                            updatingCommendCount();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            updatingUnsatisfiedCount();
                            break;

                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setMessage("Did the provider do well?").setPositiveButton("Commended",dialogClickListener).setNegativeButton("No",dialogClickListener).show(); original cmtend in 18
            builder.setMessage("Did the provider do well?").setPositiveButton("Commended",dialogClickListener).setNegativeButton("No",dialogClickListener).setCancelable(false).show();


        }
        else
        {
            cdt.cancel();
            Toast.makeText(AidSeekerChat.this,"Aid - Provider left early!",Toast.LENGTH_SHORT).show();
            cleansingData(false);
        }

        //----

    }

    public void cleansingData(boolean contStats) //added boolean on 4/21/2023
    {

        /*HashMap hm = new HashMap();
        hm.put("message","");
        hm.put("partner_uid","");

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(asm.responderUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });

        DatabaseReference dr2 = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr2.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        });

        //4/10/2023
        gettingRidOfDuration();
        //---*/ //commented on 4/21/2023 original

        //4/21/2023
        boolean continueCleaningDuration = true;
        HashMap hm = new HashMap();
        hm.put("message","");
        hm.put("partner_uid","");
        if(contStats)
        {

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
            dr.child(asm.responderUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {


                }
            });

            DatabaseReference dr2 = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
            dr2.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                }
            });
            continueCleaningDuration = true;
        }
        else
        {
            DatabaseReference dr2 = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
            dr2.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                }
            });
            continueCleaningDuration = false;
        }
        gettingRidOfDuration(continueCleaningDuration);
        //----
    }

    public void updatingCommendCount()
    {

        HashMap hm = new HashMap();
        hm.put("commends",Integer.toString(Integer.parseInt(commendCount)+1));

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(asm.responderUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });

        //3/22/2023
        savingProviderHistory("1");
        //-----
        //4/21/2023
        //cleansingData(true); commented on 5/2/2023
        //----
        /*Intent intent = new Intent(AidSeekerChat.this,AidSeekerMainDash.class);
        startActivity(intent);*/ //original commented on 4/21/2023
        countTheProvidersWhoResponded();//5/2/2023
    }
    public void updatingUnsatisfiedCount()
    {
        HashMap hm = new HashMap();
        hm.put("decommends",Integer.toString(Integer.parseInt(unsatisfiedCount)+1));

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(asm.responderUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
        //3/22/2023
        savingProviderHistory("0");
        //-----
        //4/21/2023
        //cleansingData(true); commented on 5/2/2023
        //----
        /*Intent intent = new Intent(AidSeekerChat.this,AidSeekerMainDash.class);
        startActivity(intent);*/ //original commented on 4/21/2023

        countTheProvidersWhoResponded();//5/2/2023
    }

    //------
    //3/22/2023 cp
    public void gettingLocationName()
    {
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        String address = "";
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(asm.theLatInStr),Double.parseDouble(asm.theLongInStr),1);

            address = addresses.get(0).getAddressLine(0);

        }catch(IOException e)
        {

        }
        locationOfIncident = address;
    }
    public void savingProviderHistory(String fb)
    {
        Date currentDateTime = Calendar.getInstance().getTime();
        String dateAndTime = currentDateTime.toString();

        ProviderHistory ph = new ProviderHistory(dateAndTime,ma.fullname.split(" ")[0],"Respond",ma.userid,asm.responderUID,fb,locationOfIncident);

        FirebaseDatabase.getInstance().getReference("AidProviderHistory").push().setValue(ph).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(AidSeekerChat.this, "Thanks! Take care!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(AidSeekerChat.this, "Failed to record!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //------

    //4/10/2023
    public void gettingRidOfDuration(boolean continueDeleting)//added boolean on 4/21/2023
    {
        /*HashMap hm = new HashMap();
        hm.put("trustedname_1","");

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(asm.responderUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });*/ //commented on 4/21/2023 original

        //4/21/2023
        if(continueDeleting)
        {
            HashMap hm = new HashMap();
            hm.put("trustedname_1","");

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
            dr.child(asm.responderUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {


                }
            });
        }

        /*Intent intent = new Intent(AidSeekerChat.this,AidSeekerMainDash.class);
        startActivity(intent);*/ //commented on 5/2/2023

        //5/2/2023

        //----
        if(asm.whatjob == 0)
        {
            Intent intent = new Intent(AidSeekerChat.this,GenerateReportsForAllEmergency.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(AidSeekerChat.this,AidSeekerMainDash.class);
            startActivity(intent);
        }
        //----
    }
    //--

    //4/21/2023
    public void checkIfStillPartners()
    {
        dbprovs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dbprovs.child(asm.responderUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();
                                if(!String.valueOf(snaps.child("partner_uid").getValue()).equals(ma.userid))
                                {
                                    stillpartners = false;
                                }
                                else
                                {
                                    stillpartners = true;
                                }
                                ratingsPrompt(stillpartners);
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
    //---

    //5/2/2023

    public void countTheProvidersWhoResponded()
    {
        dbprovs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String key = ds.getKey();

                    if(!stopper)
                    {
                        break;
                    }
                    dbprovs.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();
                                    counterForProvs++;
                                    if(String.valueOf(snaps.child("partner_uid").getValue()).equals(ma.userid))
                                    {
                                        totalWhoResponded++;
                                    }

                                    if(counterForProvs == datasnapshot.getChildrenCount())
                                    {
                                        cleansingData(true);
                                        stopper = false;
                                    }

                                }
                                else
                                {
                                    Toast.makeText(AidSeekerChat.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AidSeekerChat.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
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
    //----
}