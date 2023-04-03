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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mobileapp_lifeaid.databinding.ActivityAidSeekerMapCrisisBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.PolyUtil;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
//3/15/2023
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


//---

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
    ImageView ex;
    Button callbtn;
    //------

    //3/13/2023
    int index = 1;
    List<String> stationnames = new ArrayList<>();
    List<String> stationcontact = new ArrayList<>();
    List<String> stationlats = new ArrayList<>();
    List<String> stationlongs = new ArrayList<>();
    List<Double> distanceBetween = new ArrayList<>();//3/15/2023


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

    //3/15/2023
    PolylineOptions opts;
    Polyline polyline;

    int occur = 0;
    int compareInstance = 2;
    //----

    //3/30/2023
    boolean gotLoc = false;
    boolean gotLocInFire = false;
    boolean gotLocInHealth = false;
    boolean gotLocInCrime = false;
    //----




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
        ex = (ImageView) findViewById(R.id.ivexit);
        //----

        //3/13/2023

        emergency.setText(whatEm+"    >>");
        displayPlaceFirestation();

        //3/16/2023 cp
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidSeekerMapCrisis.this,AidSeekerMainDash.class);
                mMap.clear();
                startActivity(intent);
            }
        });
        ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidSeekerMapCrisis.this,AidSeekerMainDash.class);
                mMap.clear();
                startActivity(intent);
            }
        });

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!contactnum.getText().toString().contains("-"))
                {
                    /*Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+contactnum.getText().toString()));
                    startActivity(intent);*/ // commented on 4/3

                    //4/3/2023
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+contactnum.getText().toString()));
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(AidSeekerMapCrisis.this,"Number Calling Failed!",Toast.LENGTH_SHORT).show();
                    }
                    //---
                }
                else
                {
                    Toast.makeText(AidSeekerMapCrisis.this,"Click a station!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //--------


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
                    /*latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f));
                    showAddress();//3/16/2023*/ //original

                    //3/30/2023
                    if(!gotLoc)
                    {
                        gotLoc = true;
                        latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16.0f));
                        showAddress();//3/16/2023


                    }
                    //----








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

        //3/15/2023
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if(polyline != null) {
                    polyline.remove();
                }
                LatLng pos = marker.getPosition();
                double latis = pos.latitude;
                double longis = pos.longitude;


               gettingPath(latis,longis);
               contactnum.setText(  ((stationcontact.get(stationlats.indexOf(Double.toString(latis)))).equals("0")?"No Number":stationcontact.get(stationlats.indexOf(Double.toString(latis))))  );
                //Toast.makeText(AidSeekerMapCrisis.this,"Lati: "+latis+", Longi:"+longis,Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        //-----

    }
    public void gettingPath(double lati, double longi)
    {
        LatLng originstart = new LatLng(latLng.latitude, latLng.longitude);
        LatLng destinationend = new LatLng(lati, longi);


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

    //3/15/2023
    /*
    public void gettingPath(double lati, double longi)
    {
        LatLng startPoint = new LatLng(latLng.latitude,latLng.longitude);
        LatLng endPoint = new LatLng(lati,longi);

        String apiKey = "AIzaSyB4g8hJ11Criq5kKj88FHguQZY9XCv7qV0";
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + "origin="+startPoint.latitude+","+startPoint.longitude+"&destination="+endPoint.latitude+","+endPoint.longitude+"&key="+apiKey;

        String jsonResponse = "";

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL apiUrl = new URL(url);
            connection = (HttpURLConnection) apiUrl.openConnection();
            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null)
            {
                jsonResponse += line;
            }
            reader.close();



        }catch (IOException e)
        {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                try {
                    InputStream inputStream = connection.getInputStream();
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        List<LatLng> polyline = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray routesArray = jsonObject.getJSONArray("routes");
            JSONObject route = routesArray.getJSONObject(0);
            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
            String encodedPolyline = overviewPolyline.getString("points");
            polyline = decodedPoly(encodedPolyline);

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        PolylineOptions polylineOptions = new PolylineOptions().addAll(polyline).color(Color.RED).width(10);
        mMap.addPolyline(polylineOptions);
    }

    public List<LatLng> decodedPoly(String encoded)
    {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;

    }
    //----------*/

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

        try {


            LatLng fireStationPosition;
            occur++;
            for (int i = 0; i < stationnames.size(); i++) {
                fireStationPosition = new LatLng(Double.parseDouble(stationlats.get(i)), Double.parseDouble(stationlongs.get(i)));
                //mMap.addMarker(new MarkerOptions().position(fireStationPosition).title(stationnames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                //4/2/2023
                if(emergency.getText().toString().contains("Fire"))
                {
                    mMap.addMarker(new MarkerOptions().position(fireStationPosition).title(stationnames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
                //--


                //3/15/2023
                if (occur > compareInstance) {
                    distanceBetween.add(distance(latLng.latitude, latLng.longitude, Double.parseDouble(stationlats.get(i)), Double.parseDouble(stationlongs.get(i))));
                }
                //----
            }

            //3/15/2023
            if (occur > compareInstance) {
                int nearSilingan = distanceBetween.indexOf(Collections.min(distanceBetween));

                //Toast.makeText(AidSeekerMapCrisis.this, stationnames.get(nearSilingan), Toast.LENGTH_SHORT).show();
                near.setText(stationnames.get(nearSilingan));//3/16/2023
                occur = 0;
                compareInstance = 1;
                distanceBetween.clear();
            }
        }
        catch (Exception e)
        {
            markerDisplayerFire();
        }
        //----

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

        try { //added try catch 4/2/2023

            occur++;
            LatLng policeStationPosition;
            for (int i = 0; i < stationnames.size(); i++) {
                policeStationPosition = new LatLng(Double.parseDouble(stationlats.get(i)), Double.parseDouble(stationlongs.get(i)));
                //mMap.addMarker(new MarkerOptions().position(policeStationPosition).title(stationnames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                //4/2/2023
                if(emergency.getText().toString().contains("Crime"))
                {
                    mMap.addMarker(new MarkerOptions().position(policeStationPosition).title(stationnames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
                //--


                //3/15/2023
                if (occur > compareInstance) {
                    distanceBetween.add(distance(latLng.latitude, latLng.longitude, Double.parseDouble(stationlats.get(i)), Double.parseDouble(stationlongs.get(i))));
                }
                //----
            }
            //3/15/2023
            if (occur > compareInstance) {
                int nearSilingan = distanceBetween.indexOf(Collections.min(distanceBetween));

                //Toast.makeText(AidSeekerMapCrisis.this, stationnames.get(nearSilingan), Toast.LENGTH_SHORT).show();
                near.setText(stationnames.get(nearSilingan));//3/16/2023
                occur = 0;
                compareInstance = 1;
                distanceBetween.clear();
            }
        }
        catch (Exception e)
        {
            markerDisplayCrime();
        }
        //----
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

        try {//added try catch 4/2/2023

            occur++;
            LatLng healthStationPosition;
            for (int i = 0; i < stationnames.size(); i++) {
                healthStationPosition = new LatLng(Double.parseDouble(stationlats.get(i)), Double.parseDouble(stationlongs.get(i)));
                //mMap.addMarker(new MarkerOptions().position(healthStationPosition).title(stationnames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                //4/2/2023
                if(emergency.getText().toString().contains("Health"))
                {
                    mMap.addMarker(new MarkerOptions().position(healthStationPosition).title(stationnames.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
                //--


                //3/15/2023
                if (occur > compareInstance) {
                    distanceBetween.add(distance(latLng.latitude, latLng.longitude, Double.parseDouble(stationlats.get(i)), Double.parseDouble(stationlongs.get(i))));
                }
                //----
            }
            //3/15/2023
            if (occur > compareInstance) {
                int nearSilingan = distanceBetween.indexOf(Collections.min(distanceBetween));

                //Toast.makeText(AidSeekerMapCrisis.this, stationnames.get(nearSilingan), Toast.LENGTH_SHORT).show();
                near.setText(stationnames.get(nearSilingan));//3/16/2023
                occur = 0;
                compareInstance = 1;
                distanceBetween.clear();
            }
        }
        catch (Exception e)
        {
            markerDisplayHealth();
        }
        //----

    }
    public void markerDisplayerFire()
    {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                try {
                    //mMap.clear(); // 3/30/2023

                    //4/2/2023
                    if(emergency.getText().toString().contains("Fire")) {
                        mMap.clear();

                        //---

                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

                        displayPlaceFirestation();
                    }



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
                    //mMap.clear(); // 3/30/2023
                    //4/2/2023
                    if(emergency.getText().toString().contains("Crime")) {
                        mMap.clear();

                        //---

                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

                        displayPlacePoliceStation();
                    }




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
                    //mMap.clear(); // 3/30/2023

                    //4/2/2023
                    if(emergency.getText().toString().contains("Health")) {
                        mMap.clear();

                        //---

                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")).showInfoWindow();
                        mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))).showInfoWindow();
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

                        displayPlaceHealthStation();
                    }




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

    //3/15/2023
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }
    //-------

    //3/16/2023 cp
    public void showAddress()
    {
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());

        String address = "";
        try {
            addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            address = addresses.get(0).getAddressLine(0);

        }catch(IOException e)
        {

        }
        yourloc.setText(address);
    }
    //-----



    //-------
}