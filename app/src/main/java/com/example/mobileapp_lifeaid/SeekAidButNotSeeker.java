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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SeekAidButNotSeeker extends AppCompatActivity {

    MainActivity ma = new MainActivity();
    TextView greetings,conversation,send;
    TextView fullnametv,numbertv,addresstv;
    EditText msgHolder;

    String responderUID = "";
    String repeaterChecker = "";

    Button complete;

    boolean providerFound = false;
    boolean cancelled = false;
    DatabaseReference dbprovs = FirebaseDatabase.getInstance().getReference("Aid-Provider");

    CountDownTimer cdt;

    String fnameprov = "",commendCount = "",unsatisfiedCount = "",contentgetter = "",locationOfIncident = "";


    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Aid-Seeker");

    MenuButtonForProviders mp = new MenuButtonForProviders();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_aid_but_not_seeker);


        greetings = (TextView) findViewById(R.id.tv_registration3);
        conversation = (TextView) findViewById(R.id.converse);
        send = (TextView) findViewById(R.id.tv_sendbtn);

        fullnametv = (TextView) findViewById(R.id.tvfullname);
        numbertv = (TextView) findViewById(R.id.tvnumber);
        addresstv = (TextView) findViewById(R.id.tvplace);

        msgHolder = (EditText) findViewById(R.id.editTextmsgholder);

        complete = (Button) findViewById(R.id.completeTransac);

        if(ma.userrole.equals("AidProvider"))
        {
            greetings.setText("LOGGED IN AS AN AID - PROVIDER");
        }
        else
        {
            greetings.setText("LOGGED IN AS AN ADMIN");
        }

        waitforresponder();
        gettingLocationName();

        conversation.setMovementMethod(new ScrollingMovementMethod());
        msgHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgHolder.requestFocus();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!providerFound)
                {
                    Toast.makeText(SeekAidButNotSeeker.this,"Still Seeking Aid!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!msgHolder.getText().toString().equals("")) {
                        contentgetter = msgHolder.getText().toString();
                        msgSaver(contentgetter);
                        messageAppender("                                                                       "+contentgetter+"\n\n");

                    }
                }
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (providerFound) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    ratingsPrompt();
                                    cleansingData();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //do nothing
                                    break;

                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(SeekAidButNotSeeker.this);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                }
                else
                {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //cdt.cancel();//3/26/2023;
                                    cancelled = true;
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //do nothing
                                    break;

                            }
                        }
                    };
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SeekAidButNotSeeker.this);
                    builder2.setMessage("Are you sure you don't need help?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                }
            }


        });
    }
    //3/26/2023
    public void cancelledReq()
    {
        DatabaseReference dr2 = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr2.child(mp.generatedUID).setValue(null);//---deleting
        Intent intent = new Intent(SeekAidButNotSeeker.this,MenuButtonForProviders.class);
        startActivity(intent);
    }
    //----

    public void cleansingData()
    {
        HashMap hm = new HashMap();
        hm.put("message","");
        hm.put("partner_uid","");

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(responderUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });

        /*DatabaseReference dr2 = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr2.child(mp.generatedUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

            }
        });*/
        DatabaseReference dr2 = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr2.child(mp.generatedUID).setValue(null);//---deleting


    }
    public void ratingsPrompt()
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
        builder.setMessage("Did the provider do well?").setPositiveButton("Commended",dialogClickListener).setNegativeButton("No",dialogClickListener).show();
    }
    public void updatingCommendCount()
    {

        HashMap hm = new HashMap();
        hm.put("commends",Integer.toString(Integer.parseInt(commendCount)+1));

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(responderUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });

        //3/22/2023
        savingProviderHistory("1");
        //-----
        Intent intent = new Intent(SeekAidButNotSeeker.this,AidProviderMainDash.class);
        startActivity(intent);
    }
    public void updatingUnsatisfiedCount()
    {
        HashMap hm = new HashMap();
        hm.put("decommends",Integer.toString(Integer.parseInt(unsatisfiedCount)+1));

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(responderUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
        //3/22/2023
        savingProviderHistory("0");
        //-----
        Intent intent = new Intent(SeekAidButNotSeeker.this,AidProviderMainDash.class);
        startActivity(intent);
    }
    public void gettingLocationName()
    {
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        String address = "";
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(mp.latitudePos),Double.parseDouble(mp.longitudePos),1);

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

        ProviderHistory ph = new ProviderHistory(dateAndTime,ma.fullname.split(" ")[0],"Respond",ma.userid,responderUID,fb,locationOfIncident);

        FirebaseDatabase.getInstance().getReference("AidProviderHistory").push().setValue(ph).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SeekAidButNotSeeker.this, "Thanks! Take care!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SeekAidButNotSeeker.this, "Failed to record!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void waitforresponder()
    {

        new CountDownTimer(300000,1000)
        {

            @Override
            public void onTick(long l) {
                if((l/1000) % 2 == 0) {
                    //gettingtheproviderID();
                    /*if (providerFound) {
                        Toast.makeText(SeekAidButNotSeeker.this,"Aid - Provider Is Here!",Toast.LENGTH_SHORT).show();
                        getProviderData();
                        msgLooper();
                        cancel();

                    }*/
                    if(cancelled)
                    {
                        cancel();
                        cancelledReq();
                    }
                    else
                    {
                        gettingtheproviderID();
                        if (providerFound) {
                            Toast.makeText(SeekAidButNotSeeker.this,"Aid - Provider Is Here!",Toast.LENGTH_SHORT).show();
                            getProviderData();
                            msgLooper();
                            cancel();

                        }
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    public void gettingtheproviderID()
    {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dr.child(mp.generatedUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                                    complete.setText("Complete Transac.");

                                }
                            }
                            else
                            {
                                Toast.makeText(SeekAidButNotSeeker.this, "Waiting for a responder!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(SeekAidButNotSeeker.this,"Task was not successful!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getProviderData()
    {
        dbprovs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dbprovs.child(responderUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                dbprovs.child(responderUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();
                                String msg = String.valueOf(snaps.child("message").getValue());
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
                if((l/1000) % 2 == 0) {
                    getProviderMessage();
                }
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
        dr.child(mp.generatedUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


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
                        Toast.makeText(SeekAidButNotSeeker.this,"Operation won't end unless you complete transaction!",Toast.LENGTH_SHORT).show();
                        //---
                        //cdt.cancel();
                        break;

                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).show();
    }
}