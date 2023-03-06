package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mobileapp_lifeaid.databinding.ActivityMapsAidProviderBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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

    AidProviderMainDash apm = new AidProviderMainDash();
    MainActivity ma = new MainActivity();

    Button resp_btn, supp_btn;

    String dateAndTime = ""; //checkpoint 3/3/2023

    //checkpoint 3/4/2023
    String providerChecker = "";
    boolean isItFinal = false;
    Random rand = new Random();
    int sleepTime = rand.nextInt(701) + 1000; // ge buhat ni siya nako para walay instance na mag dungan og accept ang duha ka providers, all of them will be given a random number, and if ever gani nga nay magka parihas, then ang lowest na generate nga number ang first aight guyss
    //-----


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

        resp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkWhoIsFirst();
                /*if(isItFinal) {
                    whatdidyoudo = "Respond";
                    Date currentDTime = Calendar.getInstance().getTime();
                    dateAndTime = currentDTime.toString();
                    savingToHistory();
                }*/

            }
        });

        supp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whatdidyoudo = "Support";
                Date currentDateTime = Calendar.getInstance().getTime();
                dateAndTime = currentDateTime.toString();
                savingToHistory();
            }
        });

        //----

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
                        latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        seekerloc();


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

    public void seekerloc()
    {
        LatLng seekerPosition = new LatLng(Double.parseDouble(apm.latiOfSeeker),Double.parseDouble(apm.longiOfSeeker));
        mMap.addMarker(new MarkerOptions().position(seekerPosition).title("Seeker's Location!")).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seekerPosition));

    }

    //checkpoint 3/3/2023
    public void savingToHistory()
    {

        //checkpoint 3/4/2023
        ProviderHistory ph = new ProviderHistory(dateAndTime,apm.seekerfName,whatdidyoudo,apm.seeker_id,ma.userid);
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

        whatdidyoudo = "Respond";
        Date currentDTime = Calendar.getInstance().getTime();
        dateAndTime = currentDTime.toString();
        savingToHistory();
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
                                providerChecker = String.valueOf(snaps.child("partner_uid").getValue());
                                if(providerChecker.equals("") || providerChecker.isEmpty())
                                {
                                    removeLatandLong();
                                }
                                else
                                {
                                    Toast.makeText(MapsActivityAidProvider.this,"Supported! Somebody else is on the move!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MapsActivityAidProvider.this,AidProviderMainDash.class);
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

}