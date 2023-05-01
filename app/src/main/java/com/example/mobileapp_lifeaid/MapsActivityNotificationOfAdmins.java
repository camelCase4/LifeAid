package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mobileapp_lifeaid.databinding.ActivityMapsNotificationOfAdminsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivityNotificationOfAdmins extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsNotificationOfAdminsBinding binding;

    private LocationManager locationManager;
    private LocationListener locationListener;



    private final long min_dist = 5;
    private final long min_time = 1000;

    private LatLng latLng;

    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");

    String name = "";
    boolean occurOnce = true;


    List<String> latis = new ArrayList<>();
    List<String> contactNumber = new ArrayList<>();
    List<String> EmergencyType = new ArrayList<>();
    List<String> locationOfTheEm = new ArrayList<>();

    String[] emergencyNumbers = {"09652202568","09652202568","09652202568"};

    TextView locationOfSeeker,emergencyOfseeker,numberOfSeeker,exitPage;
    ImageView exitImage;
    Button call;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsNotificationOfAdminsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         locationOfSeeker = (TextView) findViewById(R.id.tv_location);
         emergencyOfseeker = (TextView) findViewById(R.id.tv_emergencyType);
         numberOfSeeker = (TextView) findViewById(R.id.tv_number);
         exitImage = (ImageView) findViewById(R.id.ivexit);
         exitPage = (TextView) findViewById(R.id.tv_exitdis);
         call = (Button) findViewById(R.id.callingbtn);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},PackageManager.PERMISSION_GRANTED);


        exitPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivityNotificationOfAdmins.this,MenuForAdmins.class);
                startActivity(intent);
            }
        });

        exitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivityNotificationOfAdmins.this,MenuForAdmins.class);
                startActivity(intent);
            }
        });
        numberOfSeeker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numberOfSeeker.getText().toString().equals("Waiting for a marker ..."))
                {
                    Toast.makeText(MapsActivityNotificationOfAdmins.this,"Click an Aid - Seeker Marker",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i){
                                case DialogInterface.BUTTON_POSITIVE:
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+numberOfSeeker.getText().toString()));
                                        startActivity(intent);

                                    }
                                    catch (Exception e)
                                    {
                                        Toast.makeText(MapsActivityNotificationOfAdmins.this,"Number Calling Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //do nothing
                                    break;

                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivityNotificationOfAdmins.this);
                    builder.setMessage("Call Aid - Seeker?").setPositiveButton("YES!",dialogClickListener).setNegativeButton("No, misclicked",dialogClickListener).show();

                }
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationOfSeeker.getText().toString().equals("Waiting for a marker ..."))
                {
                    Toast.makeText(MapsActivityNotificationOfAdmins.this,"Click an Aid - Seeker Marker",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(emergencyOfseeker.getText().toString().toLowerCase().contains("fire"))
                    {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+emergencyNumbers[0]));
                            startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MapsActivityNotificationOfAdmins.this,"Number Calling Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(emergencyOfseeker.getText().toString().toLowerCase().contains("all"))
                    {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+emergencyNumbers[1]));
                            startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MapsActivityNotificationOfAdmins.this,"Number Calling Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(emergencyOfseeker.getText().toString().toLowerCase().contains("health"))
                    {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+emergencyNumbers[2]));
                            startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MapsActivityNotificationOfAdmins.this,"Number Calling Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+emergencyNumbers[0]));
                            startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MapsActivityNotificationOfAdmins.this,"Number Calling Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getSeekers();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                LatLng pos = marker.getPosition();

                double latiss,longiss;

                latiss = pos.latitude;
                longiss = pos.longitude;

                try {
                    locationOfSeeker.setText(locationOfTheEm.get(latis.indexOf(Double.toString(latiss))));
                    emergencyOfseeker.setText(EmergencyType.get(latis.indexOf(Double.toString(latiss))));
                    numberOfSeeker.setText(contactNumber.get(latis.indexOf(Double.toString(latiss))));

                }catch (ArrayIndexOutOfBoundsException e)
                {

                }

                return false;
            }
        });
    }

    public void getSeekers()
    {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.sosmarker),
                130, 130, false));

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mMap.clear();

                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String key = ds.getKey();

                    dr.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();

                                    if(!String.valueOf(snaps.child("lati").getValue()).equals("")) {
                                        latLng = new LatLng(Double.parseDouble(String.valueOf(snaps.child("lati").getValue())), Double.parseDouble(String.valueOf(snaps.child("longi").getValue())));

                                        latis.add(String.valueOf(snaps.child("lati").getValue()));
                                        locationOfTheEm.add(showAddress(String.valueOf(snaps.child("lati").getValue()),String.valueOf(snaps.child("longi").getValue())));
                                        contactNumber.add(String.valueOf(snaps.child("phonenum").getValue()));
                                        EmergencyType.add(String.valueOf(snaps.child("job").getValue()).toUpperCase()+" EMERGENCY");

                                        name = String.valueOf(snaps.child("fname").getValue());


                                        mMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(icon));

                                        if(occurOnce)
                                        {
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));
                                            occurOnce = false;
                                        }
                                    }



                                }
                                else
                                {
                                    Toast.makeText(MapsActivityNotificationOfAdmins.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(MapsActivityNotificationOfAdmins.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
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

    //5/1/2023
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
    //---
}