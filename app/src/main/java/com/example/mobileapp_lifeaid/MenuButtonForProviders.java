package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MenuButtonForProviders extends AppCompatActivity implements LocationListener {

    Button aidAsking,claim,editAcc,faqs,lgout;
    ImageView bk,profilePic;
    public static String latitudePos, longitudePos;
    LocationManager lm;

    public static String generatedUID = "";

    MainActivity ma = new MainActivity();
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");


    //3/30/2023
    public static boolean gotLoc = false;
    //---

    //4/2/2023
    boolean seekClicked = false;
    //---

    private static final int PERMISSION_REQUEST_CODE_LOC = 104;
    //4/7/2023
    private static final int REQUEST_CHECK_SETTINGS = 1001;
    AidProviderMainDash apm = new AidProviderMainDash();
    //---

    //4/14/2023
    public static Uri imageUri;
    public static StorageReference sr = FirebaseStorage.getInstance().getReference();
    //---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_button_for_providers);
        overridePendingTransition(R.anim.slide_in_from_right,0);

        aidAsking = (Button) findViewById(R.id.askaid);
        claim = (Button) findViewById(R.id.claimcert);
        //4/3/2023
        editAcc = (Button) findViewById(R.id.editacc);
        faqs = (Button) findViewById(R.id.faqbtn);
        lgout = (Button) findViewById(R.id.loginbutton3);
        bk = (ImageView) findViewById(R.id.back);
        profilePic = (ImageView) findViewById(R.id.imageView49);

        //4/14/2023
        displayImage();
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case DialogInterface.BUTTON_POSITIVE:
                                addingProfilePic();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //do nothing
                                break;

                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuButtonForProviders.this);
                builder.setMessage("Do you want to change your profile picture?").setPositiveButton("YES!",dialogClickListener).setNegativeButton("No, misclicked",dialogClickListener).show();
            }
        });
        //---


        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        lgout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuButtonForProviders.this,MainActivity.class);
                startActivity(intent);
            }
        });

        faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuButtonForProviders.this,FAQPage.class);
                startActivity(intent);
            }
        });

        editAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuButtonForProviders.this,UserEditingPage.class);
                startActivity(intent);
            }
        });
        //--


        claim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuButtonForProviders.this,AidProviderClaimCert.class);
                startActivity(intent);
            }
        });

        aidAsking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //askingForAssurance(); original
                //4/7/2023
                checkIfLocationIsOn();
                /*if(apm.isLocationEnabled)
                {
                    askingForAssurance();
                }
                else
                {
                    checkIfLocationIsOn();
                }*/
                //---
            }
        });


    }
    //4/14/2023
    public void displayImage()
    {
        DatabaseReference dbprovs = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dbprovs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dbprovs.child(ma.userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                DataSnapshot snaps = task.getResult();
                                String imageurl = String.valueOf(snaps.child("trustedname_2").getValue());
                                if(!imageurl.equals("")) {
                                    Picasso.get().load(imageurl).into(profilePic);
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
    public void addingProfilePic()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,2);
    }
    private String fileExt(Uri im_uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap m = MimeTypeMap.getSingleton();
        return m.getExtensionFromMimeType(cr.getType(im_uri));
    }
    public void savingProfilePic(String urlImage)
    {
        HashMap hm = new HashMap();
        hm.put("trustedname_2",urlImage); // ge gamit nako ang trustedname_2 since di man needed sa provider

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MenuButtonForProviders.this,"Profile Picture Updated!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    //---
    //4/7/2023
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHECK_SETTINGS)
        {
            if(resultCode == RESULT_OK)
            {
                apm.isLocationEnabled = true;
                askingForAssurance();
            }
            else
            {
                Toast.makeText(MenuButtonForProviders.this,"Please turn the location on!",Toast.LENGTH_SHORT).show();

            }
        }
        //4/14/2023
        else if(requestCode == 2 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);

            if(imageUri != null)
            {
                StorageReference storRef = sr.child(System.currentTimeMillis()+"."+fileExt(imageUri));
                storRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                               savingProfilePic(uri.toString());
                            }
                        });


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuButtonForProviders.this, "Image Accumulation failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
        //--

    }
    public void checkIfLocationIsOn() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // Add an OnCompleteListener to handle the result of location settings check
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // Location settings are satisfied, proceed to get location updates
                    //getLoc();
                    apm.isLocationEnabled = true; //4/6/2023
                    askingForAssurance();
                } catch (ApiException e) {
                    // Location settings are not satisfied, show a dialog to prompt the user to enable it
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MenuButtonForProviders.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Handle the exception
                        }
                    }
                }
            }
        });
    }
    //---

    public void askingForAssurance()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        seekClicked = true; // 4/2/2023
                        seekAid();
                        //Toast.makeText(MenuButtonForProviders.this, "Please Wait!", Toast.LENGTH_SHORT).show();//3/26/2023
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //do nothing
                        break;

                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you really in need of assistance?").setPositiveButton("YES!",dialogClickListener).setNegativeButton("No, misclicked",dialogClickListener).show();
    }

    public void seekAid()
    {
        getLoc();

    }
    public void getLoc()
    {
        /*if(ContextCompat.checkSelfPermission(MenuButtonForProviders.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MenuButtonForProviders.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,MenuButtonForProviders.this);*/

        //4/6/2023
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, so request it
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_CODE_LOC);
        } else {
            // Permission is already granted, so get the location updates
            lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            Toast.makeText(MenuButtonForProviders.this, "Please Wait!", Toast.LENGTH_SHORT).show();//4/6/2023
        }
        //---


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
       /* if(!gotLoc) { //added if clause 3/30/2023
            gotLoc = true;

            latitudePos = Double.toString(location.getLatitude());
            longitudePos = Double.toString(location.getLongitude());

            addingToSeekerList();
        }*/

        //4/2/2023
        if(seekClicked) {
            seekClicked = false;
            latitudePos = Double.toString(location.getLatitude());
            longitudePos = Double.toString(location.getLongitude());

            addingToSeekerList();
        }
        //----

    }

    public void addingToSeekerList()
    {
        AdminAndProviderAid apa = new AdminAndProviderAid(ma.userrole,latitudePos,longitudePos,"","","all",ma.userid);
        FirebaseDatabase.getInstance().getReference("Aid-Seeker").push().setValue(apa).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    gettingTheGeneratedUID(); // 3/26/2023

                }
                else
                {
                    Toast.makeText(MenuButtonForProviders.this, "Failed to call aid!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //generatedUID = FirebaseDatabase.getInstance().getReference("Aid-Seeker").push().getKey();

                Intent intent = new Intent(MenuButtonForProviders.this,SeekAidButNotSeeker.class);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to write data to the database
                Toast.makeText(MenuButtonForProviders.this, "Failed to call aid!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //3/26/2023
    public void gettingTheGeneratedUID()
    {
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String uid = ds.getKey();

                    dr.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();
                                    if(String.valueOf(snaps.child("id").getValue()).equals(ma.userid))
                                    {
                                        generatedUID = uid;
                                    }

                                }
                                else
                                {
                                    Toast.makeText(MenuButtonForProviders.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(MenuButtonForProviders.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    if(!generatedUID.equals(""))
                    {
                        break;
                    }

                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });


    }
    //---
    //4/6/2023
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_LOC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLoc();
            } else {

                Toast.makeText(this, "Permission denied, Cannot Proceed With Aid-Request!", Toast.LENGTH_SHORT).show();

            }
        }

    }
    //---


}