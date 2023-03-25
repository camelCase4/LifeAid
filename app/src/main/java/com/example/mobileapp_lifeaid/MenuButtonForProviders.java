package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuButtonForProviders extends AppCompatActivity implements LocationListener {

    Button aidAsking;
    public static String latitudePos, longitudePos;
    LocationManager lm;

    public static String generatedUID = "";

    MainActivity ma = new MainActivity();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_button_for_providers);
        overridePendingTransition(R.anim.slide_in_from_right,0);

        aidAsking = (Button) findViewById(R.id.askaid);
        aidAsking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askingForAssurance();
            }
        });


    }

    public void askingForAssurance()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        seekAid();
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
        if(ContextCompat.checkSelfPermission(MenuButtonForProviders.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MenuButtonForProviders.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,MenuButtonForProviders.this);



    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitudePos = Double.toString(location.getLatitude());
        longitudePos = Double.toString(location.getLongitude());

        addingToSeekerList();

    }

    public void addingToSeekerList()
    {
        AdminAndProviderAid apa = new AdminAndProviderAid(ma.userrole,latitudePos,longitudePos,"","","all",ma.userid);
        FirebaseDatabase.getInstance().getReference("Aid-Seeker").push().setValue(apa).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MenuButtonForProviders.this, "Please Wait!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MenuButtonForProviders.this, "Failed to call aid!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                generatedUID = FirebaseDatabase.getInstance().getReference("Aid-Seeker").push().getKey();

                /*Intent intent = new Intent(MenuButtonForProviders.this,SeekAidButNotSeeker.class);
                startActivity(intent);*/
                Toast.makeText(MenuButtonForProviders.this, generatedUID, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to write data to the database
                Toast.makeText(MenuButtonForProviders.this, "Failed to call aid!", Toast.LENGTH_SHORT).show();
            }
        });

    }


}