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
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
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

import com.google.android.gms.maps.model.Marker; //4/16/2023
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
import com.google.maps.model.TravelMode;

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

    boolean mapSpanOnce = true; //4/16/2023
    boolean mapSpanOnce2 = true; //4/16/2023

    boolean ifAllAndNotPrio = false; //4/18/2023

    String arrivalTimeHolder = "",commendCount = "";//4/21/2023


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
                //msgGetter(); commented on 4/18/2023
                /*if(isItFinal) {
                    whatdidyoudo = "Respond";
                    Date currentDTime = Calendar.getInstance().getTime();
                    dateAndTime = currentDTime.toString();
                    savingToHistory();
                }*/
                resp_btn.setEnabled(false);
                supp_btn.setEnabled(false); //4/12/2023
            }
        });

        supp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apm.ignoredID.add(apm.seeker_id);//4/14/2023
                whatdidyoudo = "Support";
                updatingSuppCount();//3/17/2023
                Date currentDateTime = Calendar.getInstance().getTime();
                dateAndTime = currentDateTime.toString();
                savingToHistory("");
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
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f)); commented on 4/16/2023
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f));

                    //4/16/2023
                    if(mapSpanOnce)
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));
                        mapSpanOnce = false;
                    }
                    //---

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

        //4/10/2023
        getTripDuration(context,originstart.latitude,originstart.longitude,destinationend.latitude,destinationend.longitude);
        //--



    }
    //-----

    public void seekerloc()
    {

        //3/15/2023 Latlng
        seekerPosition = new LatLng(Double.parseDouble(apm.latiOfSeeker), Double.parseDouble(apm.longiOfSeeker));
        //mMap.addMarker(new MarkerOptions().position(seekerPosition).title("Seeker's Location! Phone # : "+apm.seekerPhoneNum)).showInfoWindow(); original commented on 16
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(seekerPosition)); commented on 16

        //4/16/2023
        Marker seekerMarker = mMap.addMarker(new MarkerOptions().position(seekerPosition).title("Seeker's Location!  |  Name: "+apm.seekerfName));
        seekerMarker.showInfoWindow();


        if(mapSpanOnce2)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(seekerPosition));
            mapSpanOnce2 = false;
            Toast.makeText(MapsActivityAidProvider.this,"Tap seeker's position to call!",Toast.LENGTH_SHORT).show();
        }
        //---

        //4/16/2023
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if(marker.equals(seekerMarker))
                {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i){
                                case DialogInterface.BUTTON_POSITIVE:
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+apm.seekerPhoneNum));
                                        startActivity(intent);

                                    }
                                    catch (Exception e)
                                    {
                                        Toast.makeText(MapsActivityAidProvider.this,"Number Calling Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //do nothing
                                    break;

                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivityAidProvider.this);
                    builder.setMessage("Call Aid - Seeker?").setPositiveButton("YES!",dialogClickListener).setNegativeButton("No, misclicked",dialogClickListener).show();
                }
                return false;
            }
        });
        //---



    }

    //checkpoint 3/3/2023
    public void savingToHistory(String ffb) //added ffb parameter on 4/18/2023
    {

        //checkpoint 3/4/2023
        ProviderHistory ph = new ProviderHistory(dateAndTime,apm.seekerfName,whatdidyoudo,apm.seeker_id,ma.userid,ffb,locationOfIncident);
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
                                String la = String.valueOf(snaps.child("lati").getValue()); //4/7/2023
                                //3/25/2023
                                if(who.equals("AidProvider"))
                                {
                                    Toast.makeText(MapsActivityAidProvider.this,"You are helping a fellow Aid - Provider!",Toast.LENGTH_LONG).show();
                                }
                                //4/7/2023
                                else if(who.equals("Admin"))
                                {
                                    Toast.makeText(MapsActivityAidProvider.this,"You are helping an Admin!",Toast.LENGTH_LONG).show();
                                }
                                //---
                                //-----
                                if(providerChecker.equals("") || providerChecker.isEmpty())
                                {
                                    //removeLatandLong(); original
                                    //4/7/2023
                                    if(!la.equals(""))
                                    {
                                        removeLatandLong();
                                        msgGetter(); // 4/18/2023
                                    }
                                    else
                                    {
                                        //cd.cancel(); commented on 4/22
                                        gettingRidOfPartnerUID();
                                        Toast.makeText(MapsActivityAidProvider.this,"Seeker is in good hands, Thank you for your service!",Toast.LENGTH_SHORT).show();
                                        mMap.clear(); //3/15/2023
                                        Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                                        startActivity(intent);
                                    }
                                    //---
                                }
                                else
                                {
                                    /*cd.cancel();//3/27/2023
                                    Toast.makeText(MapsActivityAidProvider.this,"Supported! Somebody else is on the move!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                                    startActivity(intent);*/ //commented on 18 original
                                    //4/18/2023
                                    if(!apm.criticalEmergency)
                                    {
                                        //cd.cancel();//3/27/2023 commented on 4/22
                                        Toast.makeText(MapsActivityAidProvider.this,"Supported! Somebody else is on the move!",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        savingSeekerID();
                                        ifAllAndNotPrio = true;
                                        msgGetter();
                                    }
                                    //---

                                }
                            }
                            //3/26/2023
                            else
                            {
                                //cd.cancel(); commented on 4/22
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

            //4/16/2023
            if(address.length() > 50)
            {
                address = addresses.get(0).getAddressLine(0).substring(0,50);
            }

            //---

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
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                DataSnapshot snaps = task.getResult();
                                String chat = String.valueOf(snaps.child("message").getValue());
                                String ifCancelledReq = String.valueOf(snaps.child("commends").getValue()); // 4/5/2023
                                //3/11/2023
                                String checkIfStillOngGoing = String.valueOf(snaps.child("partner_uid").getValue());


                                if (checkIfStillOngGoing.equals("")) {
                                    cd.cancel();
                                    Toast.makeText(MapsActivityAidProvider.this, "Seeker Satisfied, Thank you for your service!", Toast.LENGTH_SHORT).show();
                                    mMap.clear(); //3/15/2023
                                    Intent intent = new Intent(MapsActivityAidProvider.this, AidProviderMainDash.class);
                                    startActivity(intent);
                                }
                                //4/7/2023
                                else {
                                    if (!checkIfStillOngGoing.equals(ma.userid)) {
                                        /*cd.cancel();
                                        gettingRidOfPartnerUID();
                                        Toast.makeText(MapsActivityAidProvider.this, "Someone else responded first, Thank you for your service!", Toast.LENGTH_SHORT).show();
                                        mMap.clear(); //3/15/2023
                                        Intent intent = new Intent(MapsActivityAidProvider.this, AidProviderMainDash.class);
                                        startActivity(intent);*/ //commented on 4/22/2023 orig

                                        //4/22/2023
                                        if(apm.criticalEmergency)
                                        {
                                            cd.cancel();
                                            respondClicked = false;
                                            sending.setEnabled(false);
                                            conversation.setText("Due to critical emergency requested by Seeker\n\n Communication is disabled.\nPlease show integrity in your profession and help if you clicked respond!\nRest-assured you get an auto-commend feedback in these situations.");
                                            conversation.setGravity(Gravity.CENTER);
                                            suwatan.setEnabled(false);
                                            startTimerForCriticalEm();
                                        }
                                        else
                                        {
                                            cd.cancel();
                                            gettingRidOfPartnerUID();
                                            Toast.makeText(MapsActivityAidProvider.this, "Someone else responded first, Thank you for your service!", Toast.LENGTH_SHORT).show();
                                            mMap.clear(); //3/15/2023
                                            Intent intent = new Intent(MapsActivityAidProvider.this, AidProviderMainDash.class);
                                            startActivity(intent);
                                        }
                                        //---
                                    }



                                }
                                //---
                                //------

                                //4/5/2023
                                if (ifCancelledReq.equals("1")) {
                                    cd.cancel();
                                    gettingRidOfPartnerUID();
                                    Toast.makeText(MapsActivityAidProvider.this, "Seeker Cancelled! Thank you for your service!", Toast.LENGTH_SHORT).show();
                                    mMap.clear(); //3/15/2023
                                    Intent intent = new Intent(MapsActivityAidProvider.this, AidProviderMainDash.class);
                                    startActivity(intent);
                                }
                                //----
                                if (!chat.equals("")) {
                                    if (!chat.equals(comparer)) {
                                        //conversation.append("\n                                             "+chat);
                                        addMessage("                                                                       " + chat + "\n\n");
                                        comparer = chat;
                                    }
                                }

                            } else {
                                if (!ifcancelled) {
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
        /*cd = new CountDownTimer(300000,1000)
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
        }.start();*/ //commented on 4/18/2023

        //4/18/2023
        if(!ifAllAndNotPrio)
        {
            cd = new CountDownTimer(60000,1000)
            {

                @Override
                public void onTick(long l) {
                    /*if((l/1000) % 2 == 0) {
                        gettingSeekerMSG();
                    }*/ //commented on 4/21

                    //4/21/2023
                    gettingSeekerMSG();
                    //---
                }

                @Override
                public void onFinish() {
                    askProviderForUpdate();

                }
            }.start();


        }
        else
        {
            respondClicked = false;
            sending.setEnabled(false);
            conversation.setText("Due to critical emergency requested by Seeker\n\n Communication is disabled.\nPlease show integrity in your profession and help if you clicked respond!\nRest-assured you get an auto-commend feedback in these situations.");
            conversation.setGravity(Gravity.CENTER);
            suwatan.setEnabled(false);
            startTimerForCriticalEm();
        }
        //---
    }
    //4/18/2023
    public void startTimerForCriticalEm()
    {
        cd = new CountDownTimer(60000,1000)
        {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                askProviderForUpdateCriticalEm();

            }
        }.start();
    }
    public void askProviderForUpdateCriticalEm()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        Date currentDateTime = Calendar.getInstance().getTime();
                        dateAndTime = currentDateTime.toString();
                        whatdidyoudo = "Respond";//4/22/2023
                        savingToHistory("1");
                        mMap.clear();
                        Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                        startActivity(intent);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        startTimerForCriticalEm();
                        Toast.makeText(MapsActivityAidProvider.this,"Okay! Please continue serving!",Toast.LENGTH_SHORT).show();

                        break;

                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).show(); //commented on 18 original
        builder.setMessage("Is the request complete?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).setCancelable(false).show();


    }
    //----

    public void askProviderForUpdate()
    {

        /*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

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
        //builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).show(); //commented on 18 original
        builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).setCancelable(false).show();
        */ //commented on 4/21/2023 original

        //4/21/2023
        if(Long.parseLong(arrivalTimeHolder) <= 3)
        {
            DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i){
                        case DialogInterface.BUTTON_POSITIVE:
                            updateCommendCount();
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
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            //builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).show(); //commented on 18 original
            builder2.setMessage("Done responding to Aid - Seeker?").setPositiveButton("Yes",dialogClickListener2).setNegativeButton("No",dialogClickListener2).setCancelable(false).show();

        }
        else
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
            //builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).show(); //commented on 18 original
            builder.setMessage("Are you still there?").setPositiveButton("Yes",dialogClickListener).setNegativeButton("No",dialogClickListener).setCancelable(false).show();

        }
        //---
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
                                //4/21/2023
                                commendCount = String.valueOf(snaps.child("commends").getValue());
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

    //4/10/2023
    public void getTripDuration(GeoApiContext context,double startla, double startlo, double endla, double endlo)
    {
        DirectionsApiRequest request = DirectionsApi.newRequest(context).origin(new com.google.maps.model.LatLng(startla,startlo))
                .destination(new com.google.maps.model.LatLng(endla,endlo))
                .mode(TravelMode.WALKING);


        try {
            DirectionsResult result = request.await();
            if (result.routes != null && result.routes.length > 0) {
                long durationInSeconds = result.routes[0].legs[0].duration.inSeconds;

                long durationInMinutes = durationInSeconds / 60;

                storingDuration(Long.toString(durationInMinutes) + " minutes");

            }
        }
        catch (Exception e)
        {

        }
    }
    public void storingDuration(String tobeput)
    {

        arrivalTimeHolder = tobeput.split(" ")[0]; //4/21/2023

        HashMap hm = new HashMap();
        hm.put("trustedname_1",tobeput); //trustedname_1 akoang ge himo nga temporary holder sa duration time

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });



    }
    //---

    //4/21/2023
    public void updateCommendCount()
    {
        HashMap hm = new HashMap();
        hm.put("commends",Integer.toString(Integer.parseInt(commendCount)+1));

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
        savingToHist();
    }
    public void savingToHist()
    {
        Date currentDateTime = Calendar.getInstance().getTime();
        String dateAndTime = currentDateTime.toString();

        ProviderHistory ph = new ProviderHistory(dateAndTime,apm.seekerfName,"Respond",apm.seeker_id,ma.userid,"1",locationOfIncident);

        FirebaseDatabase.getInstance().getReference("AidProviderHistory").push().setValue(ph).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MapsActivityAidProvider.this, "Thank you for your service!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MapsActivityAidProvider.this, "Failed to record!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        finalizeCleaning();
    }
    public void finalizeCleaning()
    {
        HashMap hm = new HashMap();
        hm.put("message","");
        hm.put("partner_uid","");
        hm.put("trustedname_1","");

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
                startActivity(intent);
            }
        });

    }
    //----

}