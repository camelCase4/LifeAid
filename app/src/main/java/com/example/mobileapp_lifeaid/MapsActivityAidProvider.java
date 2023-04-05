package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mobileapp_lifeaid.databinding.ActivityMapsAidProviderBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Locale;
import java.util.Random;


public class MapsActivityAidProvider extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsAidProviderBinding binding;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private final long min_dist = 5;
    private final long min_time = 1000;

    private LatLng latLng;

    String whatdidyoudo = "";
    TextView seekerlocstr,yourlocstr;


    AidProviderMainDash apm = new AidProviderMainDash();
    MainActivity ma = new MainActivity();

    Button resp_btn, supp_btn;

    String dateAndTime = ""; //checkpoint 3/3/2023

    boolean gotLoc = false; //3/30/2023

    //checkpoint 3/4/2023
    String providerChecker = "";
    boolean isItFinal = false;
    Random rand = new Random();
    int sleepTime = rand.nextInt(701) + 1000; // ge buhat ni siya nako para walay instance na mag dungan og accept ang duha ka providers, all of them will be given a random number, and if ever gani nga nay magka parihas, then ang lowest na generate nga number ang first aight guyss
    //-----

    // checkpoint 3/8/2023
    boolean respondClicked = false;
    TextView sending,conversation;
    EditText suwatan;
    String textHolder = "";

    // -----

    // checkpoint 3/9/2023
    String comparer = "";
    boolean cdtimer = false;
    boolean timerdecider = true;
    DatabaseReference dbseek = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
    CountDownTimer cd;
    // -------

    //3/15/2023
    PolylineOptions opts;
    Polyline polyline;
    LatLng seekerPosition;
    //----

    //3/17/2023
    String provisionCount = "";
    String supportCount = "";
    //---

    //3/22/2023
    String locationOfIncident = "";
    //----

    //3/26/2023
    boolean ifcancelled = false;
    //---



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsAidProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},PackageManager.PERMISSION_GRANTED);

        //checkpoint 3/3/2023
        resp_btn = (Button) findViewById(R.id.respondbutton);
        supp_btn = (Button) findViewById(R.id.supportbutton);
        seekerlocstr = (TextView) findViewById(R.id.tv_registration5);//3/7/2023
        yourlocstr = (TextView) findViewById(R.id.tv_registration6);//3/7/2023
        //chcekpoint 3/8/2023
        sending = (TextView) findViewById(R.id.send);
        conversation = (TextView) findViewById(R.id.converse);
        suwatan = (EditText) findViewById(R.id.suwatanan);
        //--------



        //checkpoint 3/7/2023

        //seekerlocstr.setText(showAddress(apm.latiOfSeeker,apm.longiOfSeeker));

        //--------
        //3/22/2023
        locationOfIncident = showAddress(apm.latiOfSeeker,apm.longiOfSeeker);
        seekerlocstr.setText(locationOfIncident);
        //----
        gettingSupportAndRespondCount();//3/17/2023


        resp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkWhoIsFirst();
                updatingProvsCount();//3/17/2023
                respondClicked = true;
                msgGetter();
                /*if(isItFinal) {
                    whatdidyoudo = "Respond";
                    Date currentDTime = Calendar.getInstance().getTime();
                    dateAndTime = currentDTime.toString();
                    savingToHistory();
                }*/
                resp_btn.setEnabled(false);
            }
        });

        supp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatdidyoudo = "Support";
                updatingSuppCount();//3/17/2023
                Date currentDateTime = Calendar.getInstance().getTime();
                dateAndTime = currentDateTime.toString();
                savingToHistory();
                //3/19/2023
                mMap.clear();
                Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                startActivity(intent);
                //----
            }
        });

        //----

        //checkpoint 3/8/2023


        suwatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                suwatan.requestFocus();
            }
        });
        conversation.setMovementMethod(new ScrollingMovementMethod());
        sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(respondClicked)
                {
                    if(!suwatan.getText().toString().equals("")) {
                        textHolder = suwatan.getText().toString() + "\n\n";
                        savethemessage(textHolder);
                        /*conversation.setText(textHolder);
                        suwatan.setText("");
                        suwatan.setHint("Write Something");*/
                        //space needed = textHolder = "                                            "

                        addMessage("- "+suwatan.getText().toString()+"\n");
                    }

                }
                else
                {
                    Toast.makeText(MapsActivityAidProvider.this,"Click Respond First!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //-------

        // checkpoint 3/8/2023

        //--------


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        //2/24/2023
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    mMap.clear(); // 3/30/2023
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f));


                        seekerloc();
                        yourlocstr.setText(showAddress(Double.toString(location.getLatitude()),Double.toString(location.getLongitude())));
                        gettingPath();


                }catch (Exception e)
                {

                }
            }
        };


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, min_time,min_dist,locationListener);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,min_time,min_dist,locationListener);
        }catch (SecurityException se)
        {

        }
        //--------
    }
    //cp 3/15/2023
    public void gettingPath()
    {
        LatLng originstart = new LatLng(latLng.latitude, latLng.longitude);
        LatLng destinationend = new LatLng(seekerPosition.latitude, seekerPosition.longitude);


        /*
        ArrayList<LatLng> coordList = new ArrayList<LatLng>();
        coordList.add(originstart);
        coordList.add(destinationend);

        PolylineOptions polylineOptions = new PolylineOptions();

        polylineOptions.addAll(coordList);
        polylineOptions.width(10).color(Color.RED);

        mMap.addPolyline(polylineOptions);*/
        List<LatLng> path = new ArrayList();

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyB4g8hJ11Criq5kKj88FHguQZY9XCv7qV0")
                .build();

        DirectionsApiRequest req = DirectionsApi.getDirections(context, (Double.toString(originstart.latitude)+","+Double.toString(originstart.longitude)),(Double.toString(destinationend.latitude)+","+Double.toString(destinationend.longitude)));
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {

        }
        if (path.size() > 0) {

            opts = new PolylineOptions().addAll(path).color(Color.RED).width(5);
            //mMap.addPolyline(opts);
            polyline = this.mMap.addPolyline(opts);
        }



    }
    //-----

    public void seekerloc()
    {

            //3/15/2023 Latlng
            seekerPosition = new LatLng(Double.parseDouble(apm.latiOfSeeker), Double.parseDouble(apm.longiOfSeeker));
            mMap.addMarker(new MarkerOptions().position(seekerPosition).title("Seeker's Location!")).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(seekerPosition));



    }

    //checkpoint 3/3/2023
    public void savingToHistory()
    {

        //checkpoint 3/4/2023
        ProviderHistory ph = new ProviderHistory(dateAndTime,apm.seekerfName,whatdidyoudo,apm.seeker_id,ma.userid,"",locationOfIncident);
        //-----
        FirebaseDatabase.getInstance().getReference("AidProviderHistory").push().setValue(ph).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MapsActivityAidProvider.this, "God Speed!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MapsActivityAidProvider.this, "Failed to record!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //----------

    //checkpoint 3/4/2023
    public void removeLatandLong()
    {
        //isItFinal = true;

        HashMap hm = new HashMap();
        hm.put("lati","");
        hm.put("longi","");
        hm.put("partner_uid",ma.userid);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr.child(apm.seeker_id).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
        Toast.makeText(MapsActivityAidProvider.this, "God Speed!", Toast.LENGTH_SHORT).show();//3/23/2023
        whatdidyoudo = "Respond";
        Date currentDTime = Calendar.getInstance().getTime();
        dateAndTime = currentDTime.toString();
        //savingToHistory(); ge kuha nako ge change nako nga adto ra ma save sa history once naka rate na ang seeker 3/22/2023
        savingSeekerID();
    }

    public void checkWhoIsFirst()
    {
        for(;;)
        {
            try{
                Thread.sleep(sleepTime);
                findIfProviderIdExists();
                break;

            }catch(InterruptedException e)
            {

            }
        }
    }

    public void findIfProviderIdExists()
    {
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dr.child(apm.seeker_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();
                                String who = String.valueOf(snaps.child("whatRole").getValue());//3/25/2023
                                providerChecker = String.valueOf(snaps.child("partner_uid").getValue());
                                //3/25/2023
                                if(who.equals("AidProvider"))
                                {
                                    Toast.makeText(MapsActivityAidProvider.this,"You are helping a fellow Aid - Provider!",Toast.LENGTH_LONG).show();
                                }
                                //-----
                                if(providerChecker.equals("") || providerChecker.isEmpty())
                                {
                                    removeLatandLong();
                                }
                                else
                                {
                                    cd.cancel();//3/27/2023
                                    Toast.makeText(MapsActivityAidProvider.this,"Supported! Somebody else is on the move!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                                    startActivity(intent);

                                }
                            }
                            //3/26/2023
                            else
                            {
                                cd.cancel();
                                ifcancelled = true;
                                Toast.makeText(MapsActivityAidProvider.this,"Seeker Cancelled!",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                                startActivity(intent);
                            }
                            //----
                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //-----

    //checkpoint 3/6/2023
    public void savingSeekerID()
    {
        HashMap hm = new HashMap();
        hm.put("partner_uid",apm.seeker_id);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
    }
    //-----

    //checkpoint 3/7/2023
    public String showAddress(String latiloc, String longiloc)
    {
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        String address = "";
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latiloc),Double.parseDouble(longiloc),1);

            address = addresses.get(0).getAddressLine(0);

        }catch(IOException e)
        {

        }


        return address;
    }
    //-------
    // checkpoint 3/8/2023

    private void addMessage(String msg) {

        conversation.append(msg + "\n");

        final int scrollAmount = conversation.getLayout().getLineTop(conversation.getLineCount()) - conversation.getHeight();

        if (scrollAmount > 0)
            conversation.scrollTo(0, scrollAmount);
        else
            conversation.scrollTo(0, 0);

        suwatan.setText("");
        suwatan.setHint("Write Something");

    }

    public void savethemessage(String tobeput)
    {
        //checkpoint 3/9/2023
        HashMap hm = new HashMap();
        hm.put("message",tobeput);

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });

        //---------


    }

    public void gettingSeekerMSG()
    {

        dbseek.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dbseek.child(apm.seeker_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();
                                String chat = String.valueOf(snaps.child("message").getValue());
                                String ifCancelledReq = String.valueOf(snaps.child("commends").getValue()); // 4/5/2023
                                //3/11/2023
                                String checkIfStillOngGoing = String.valueOf(snaps.child("partner_uid").getValue());
                                if(checkIfStillOngGoing.equals(""))
                                {
                                    cd.cancel();
                                    Toast.makeText(MapsActivityAidProvider.this,"Thank you for your service!",Toast.LENGTH_SHORT).show();
                                    mMap.clear(); //3/15/2023
                                    Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                                    startActivity(intent);
                                }
                                //------

                                //4/5/2023
                                if(ifCancelledReq.equals("1"))
                                {
                                    cd.cancel();
                                    gettingRidOfPartnerUID();
                                    Toast.makeText(MapsActivityAidProvider.this,"Seeker Cancelled! Thank you for your service!",Toast.LENGTH_SHORT).show();
                                    mMap.clear(); //3/15/2023
                                    Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                                    startActivity(intent);
                                }
                                //----
                                if(!chat.equals(""))
                                {
                                    if(!chat.equals(comparer))
                                    {
                                        //conversation.append("\n                                             "+chat);
                                        addMessage("                                                                       "+chat+"\n\n");
                                        comparer = chat;
                                    }
                                }

                            }
                            else
                            {
                                if(!ifcancelled) {
                                    cd.cancel();
                                    Toast.makeText(MapsActivityAidProvider.this, "Thank you for your service!", Toast.LENGTH_SHORT).show();
                                    mMap.clear(); //3/15/2023
                                    Intent intent = new Intent(MapsActivityAidProvider.this, AidProviderMainDash.class);
                                    startActivity(intent);
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

    public void msgGetter()
    {
        /*new CountDownTimer(60000,1000)
        {

            @Override
            public void onTick(long l) {
                if((l/1000) % 2 == 0) {
                    gettingSeekerMSG();
                }
            }

            @Override
            public void onFinish() {
                if(!askProviderForUpdate())
                {
                    cancel();
                }
                else
                {
                    this.start();
                }

            }
        }.start();*/

        /*CountDownTimer cd = new CountDownTimer(60000,1000)
        {

            @Override
            public void onTick(long l) {
                if((l/1000) % 2 == 0) {
                    gettingSeekerMSG();
                }
            }

            @Override
            public void onFinish() {
               timerdecider = askProviderForUpdate();

            }
        };

        if(timerdecider)
        {
            cd.cancel();
            timerdecider = true;
            //msgGetter();
            cd.start();
        }
        else
        {
            cd.cancel();
            timerdecider = false;
        }*/
        cd = new CountDownTimer(300000,1000)
        {

            @Override
            public void onTick(long l) {
                if((l/1000) % 2 == 0) {
                    gettingSeekerMSG();
                }
            }

            @Override
            public void onFinish() {
                askProviderForUpdate();

            }
        }.start();
    }

    public void askProviderForUpdate()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        msgGetter();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //3/11/2023
                        msgGetter();
                        Toast.makeText(MapsActivityAidProvider.this,"Please continue serving",Toast.LENGTH_SHORT).show();
                        //---
                        //cd.cancel();
                        break;

                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).show();

    }

    //3/17/2023 cp
    public void gettingSupportAndRespondCount()
    {

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
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
                                provisionCount = String.valueOf(snaps.child("provision_count").getValue());
                                supportCount = String.valueOf(snaps.child("support_count").getValue());

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
    public void updatingProvsCount()
    {
        HashMap hm = new HashMap();
        hm.put("provision_count",Integer.toString(Integer.parseInt(provisionCount)+1));

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });

    }
    public void updatingSuppCount()
    {
        HashMap hm = new HashMap();
        hm.put("support_count",Integer.toString(Integer.parseInt(supportCount)+1));

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });

    }
    //----
    //--------

    //4/5/2023
    public void gettingRidOfPartnerUID()
    {

        HashMap hm = new HashMap();
        hm.put("message","");
        hm.put("partner_uid","");
        hm.put("provision_count",Integer.toString((Integer.parseInt(provisionCount)+1)-1));

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });


    }
    //---

}