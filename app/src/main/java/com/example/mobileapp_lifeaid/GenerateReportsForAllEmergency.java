package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class GenerateReportsForAllEmergency extends AppCompatActivity {

    TextView timeOfIncident,nameOfMainResponder,numberOfMainResponder,emailOfResponder,addressOfResponder,locationOfIncident,amountOfProvider,print;
    ImageView bk;
    AidSeekerChat asc = new AidSeekerChat();
    AidSeekerMainDash asm = new AidSeekerMainDash();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_reports_for_all_emergency);


        timeOfIncident = (TextView) findViewById(R.id.timeD);
        nameOfMainResponder = (TextView) findViewById(R.id.mainResponder);
        numberOfMainResponder = (TextView) findViewById(R.id.num);
        emailOfResponder = (TextView) findViewById(R.id.emailrepo);
        addressOfResponder = (TextView) findViewById(R.id.addr);
        locationOfIncident = (TextView) findViewById(R.id.incident);
        amountOfProvider = (TextView) findViewById(R.id.amountOfProvider);
        print = (TextView) findViewById(R.id.switchrole);

        bk = (ImageView) findViewById(R.id.back);



        Date currentDT = Calendar.getInstance().getTime();
        timeOfIncident.setText("Time & Date of Incident : "+currentDT.toString());
        amountOfProvider.setText("Amount of Providers who Responded : "+asc.totalWhoResponded);
        locationOfIncident.setText("Location of Incident : "+asc.locationOfIncident);

        gettingProviderData();


        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GenerateReportsForAllEmergency.this,AidSeekerMainDash.class);
                startActivity(intent);
            }
        });




    }

    public void gettingProviderData()
    {
        DatabaseReference dbprovs = FirebaseDatabase.getInstance().getReference("Aid-Provider");

        dbprovs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                dbprovs.child(asm.responderUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot snaps = task.getResult();

                                nameOfMainResponder.setText("Full Name of Main Provider: "+String.valueOf(snaps.child("fname").getValue())+" "+String.valueOf(snaps.child("lname").getValue()));
                                numberOfMainResponder.setText("Number of Main Provider : "+String.valueOf(snaps.child("phonenum").getValue()));
                                emailOfResponder.setText("Email of Main Provider : "+String.valueOf(snaps.child("email").getValue()));
                                addressOfResponder.setText("Address of Main Provider : "+String.valueOf(snaps.child("address").getValue()));

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

    public void printing(View view)
    {

       /* View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        View rootView = decorView.getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshot = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);


        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().toString(), "emergencyreport.png"));
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Toast.makeText(GenerateReportsForAllEmergency.this,"Copy Saved!",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);*/

        //5/2/2023
        /*Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }*/
        //---

        //View view1 = view.getRootView();
        View view1 = getWindow().getDecorView().getRootView();

        Bitmap bitmap = Bitmap.createBitmap(view1.getWidth(), view1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view1.draw(canvas);
        File fileScreenshot = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                Calendar.getInstance().getTime().toString()+".jpg");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileScreenshot);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(GenerateReportsForAllEmergency.this,"Copy Saved!",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(GenerateReportsForAllEmergency.this,"Failed!",Toast.LENGTH_SHORT).show();
        }
    }


}