package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mobileapp_lifeaid.databinding.ActivityAidSeekerMapCrisisBinding;

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
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f));



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
    public void displayPlaceFirestation(String whatEm)
    {

    }
    //-------
}