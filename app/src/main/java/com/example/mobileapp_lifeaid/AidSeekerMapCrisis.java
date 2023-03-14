package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mobileapp_lifeaid.databinding.ActivityAidSeekerMapCrisisBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AidSeekerMapCrisis extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityAidSeekerMapCrisisBinding binding;

    private LocationManager locationManager;
    private LocationListener locationListener;



    private final long min_dist = 5;
    private final long min_time = 1000;

    private LatLng latLng;


    //cp 3/12/2023
    String[] emTypes = {"Health","Crime","Fire"};
    TextView yourloc,emergency,contactnum,near,exit;
    Button callbtn;
    //------

    //3/13/2023
    int index = 1;
    List<String> stationnames = new ArrayList<>();
    List<String> stationcontact = new ArrayList<>();
    List<String> stationlats = new ArrayList<>();
    List<String> stationlongs = new ArrayList<>();


    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference firedb = fd.getReference().child("FireStations");
    DatabaseReference crimedb = fd.getReference().child("PoliceStations");
    DatabaseReference healthdb = fd.getReference().child("HealthStations");

    String whatEm = "Fire";


    CountDownTimer cdt;

    boolean newEmFire = true;
    boolean newEmCrime = true;
    boolean newEmHealth = true;
    //----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAidSeekerMapCrisisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //3/13/2023
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},PackageManager.PERMISSION_GRANTED);
        //------------


        //cp 3/12/2023
        yourloc = (TextView) findViewById(R.id.tv_location);
        emergency = (TextView) findViewById(R.id.tv_emergencyType);
        contactnum = (TextView) findViewById(R.id.tv_number);
        near = (TextView) findViewById(R.id.tv_nearest);
        exit = (TextView) findViewById(R.id.tv_exitdis);
        callbtn = (Button) findViewById(R.id.callingbtn);
        //----

        //3/13/2023

        emergency.setText(whatEm+"    >>");
        displayPlaceFirestation();


        cdt = new CountDownTimer(300000,1000) {
            @Override
            public void onTick(long l) {
                if(whatEm.equals("Fire") && newEmFire)
                {
                    //3/14/2023
                    mMap.clear();
                    displayPlaceFirestation();
                    //----
                    markerDisplayerFire();
                    newEmFire = false;
                    newEmCrime = true;
                    newEmHealth = true;


                }
                else if(whatEm.equals("Crime") && newEmCrime)
                {
                    mMap.clear();
                    displayPlacePoliceStation();
                    markerDisplayCrime();
                    newEmCrime = false;
                    newEmFire = true;
                    newEmHealth = true;


                }
                else if(whatEm.equals("Health") && newEmHealth)
                {
                    mMap.clear();
                    displayPlaceHealthStation();
                    markerDisplayHealth();
                    newEmHealth = false;
                    newEmCrime = true;
                    newEmFire = true;


                }

            }

            @Override
            public void onFinish() {

            }
        }.start();


        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                if(index == 1)
                {

                    whatEm = "Fire";
                    emergency.setText(whatEm+"    >>");
                    stationnames.clear();
                    stationcontact.clear();
                    stationlats.clear();
                    stationlongs.clear();


                }
                else if(index == 2)
                {

                    whatEm = "Crime";
                    emergency.setText(whatEm+"    >>");
                    stationnames.clear();
                    stationcontact.clear();
                    stationlats.clear();
                    stationlongs.clear();


                }
                else
                {

                    whatEm = "Health";
                    emergency.setText(whatEm+"    >>");
                    index = 0;
                    stationnames.clear();
                    stationcontact.clear();
                    stationlats.clear();
                    stationlongs.clear();


                }
            }
        });

        //---
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

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        //3/13/2023
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f));





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
        //---


    }

    //cp 3/123/2023
    public void displayPlaceFirestation()
    {
       /* stationnames.clear();
        stationcontact.clear();
        stationlats.clear();
        stationlongs.clear();*/

        firedb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String key = ds.getKey();

                    firedb.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();

                                    stationnames.add(String.valueOf(snaps.child("name").getValue()));
                                    stationcontact.add(String.valueOf(snaps.child("contact").getValue()));
                                    stationlats.add(String.valueOf(snaps.child("lat").getValue()));
                                    stationlongs.add(String.valueOf(snaps.child("long").getValue()));

                                    //Toast.makeText(AidSeekerMapCrisis.this, String.valueOf(snaps.child("name").getValue()), Toast.LENGTH_SHORT).show();


                                }
                                else
                                {
                                    Toast.makeText(AidSeekerMapCrisis.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AidSeekerMapCrisis.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LatLng fireStationPosition;
        for(int i = 0; i < stationnames.size(); i++)
        {
            fireStationPosition = new LatLng(Double.parseDouble(stationlats.get(i)),Double.parseDouble(stationlongs.get(i)));
            mMap.addMarker(new MarkerOptions().position(fireStationPosition).title(stationnames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }


    }
    public void displayPlacePoliceStation()
    {

        /*stationnames.clear();
        stationcontact.clear();
        stationlats.clear();
        stationlongs.clear();*/

        crimedb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String key = ds.getKey();

                    crimedb.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();

                                    stationnames.add(String.valueOf(snaps.child("name").getValue()));
                                    stationcontact.add(String.valueOf(snaps.child("contact").getValue()));
                                    stationlats.add(String.valueOf(snaps.child("lat").getValue()));
                                    stationlongs.add(String.valueOf(snaps.child("long").getValue()));

                                    //Toast.makeText(AidSeekerMapCrisis.this, String.valueOf(snaps.child("name").getValue()), Toast.LENGTH_SHORT).show();


                                }
                                else
                                {
                                    Toast.makeText(AidSeekerMapCrisis.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AidSeekerMapCrisis.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LatLng policeStationPosition;
        for(int i = 0; i < stationnames.size(); i++)
        {
            policeStationPosition = new LatLng(Double.parseDouble(stationlats.get(i)),Double.parseDouble(stationlongs.get(i)));
            mMap.addMarker(new MarkerOptions().position(policeStationPosition).title(stationnames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
    }
    public void displayPlaceHealthStation()
    {
        healthdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String key = ds.getKey();

                    healthdb.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();

                                    stationnames.add(String.valueOf(snaps.child("name").getValue()));
                                    stationcontact.add(String.valueOf(snaps.child("contact").getValue()));
                                    stationlats.add(String.valueOf(snaps.child("lat").getValue()));
                                    stationlongs.add(String.valueOf(snaps.child("long").getValue()));

                                    //Toast.makeText(AidSeekerMapCrisis.this, String.valueOf(snaps.child("name").getValue()), Toast.LENGTH_SHORT).show();


                                }
                                else
                                {
                                    Toast.makeText(AidSeekerMapCrisis.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AidSeekerMapCrisis.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LatLng healthStationPosition;
        for(int i = 0; i < stationnames.size(); i++)
        {
            healthStationPosition = new LatLng(Double.parseDouble(stationlats.get(i)),Double.parseDouble(stationlongs.get(i)));
            mMap.addMarker(new MarkerOptions().position(healthStationPosition).title(stationnames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

    }
    public void markerDisplayerFire()
    {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));

                    displayPlaceFirestation();



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
    }
    public void markerDisplayCrime()
    {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));

                    displayPlacePoliceStation();



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
    }
    public void markerDisplayHealth()
    {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));

                    displayPlaceHealthStation();



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

    }



    //-------
}