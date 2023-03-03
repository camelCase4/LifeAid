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

public class MapsActivityAidProvider extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsAidProviderBinding binding;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private final long min_dist = 5;
    private final long min_time = 1000;

    private LatLng latLng;

    AidProviderMainDash apm = new AidProviderMainDash();

    Button resp_btn, supp_btn;

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
                Toast.makeText(MapsActivityAidProvider.this, "The button auto clicks!", Toast.LENGTH_SHORT).show();
            }
        });

        resp_btn.performClick();
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


}