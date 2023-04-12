package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Objects;

public class AidProviderClaimCert extends AppCompatActivity {

    TextView commendsProvs,downloadCert;
    ImageView certificate;

    MainActivity ma = new MainActivity();
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");

    int amountOfCommends = 0;
    String status = ""; //1 = pwedi pa mo kuha, 2 = di na pwedi, zero ang initial
    String certificateURL = "";

    boolean flag = false;

    private static final int REQUEST_CODE = 1; //3/28/2023

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_claim_cert);

        commendsProvs = (TextView) findViewById(R.id.commendsandprovisions);
        certificate = (ImageView) findViewById(R.id.cert);
        downloadCert = (TextView) findViewById(R.id.dlcert);

        gettingProviderData();

        certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gettingProviderData();
                flag = true;
                /*if(status.equals("0")) {
                    if (amountOfCommends >= 50) {
                        updateProviderData();
                        Toast.makeText(AidProviderClaimCert.this, "Alright! Wait for 2-3 Working Days!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AidProviderClaimCert.this, "Still fell short, work harder!", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(status.equals("1"))
                {
                    if(certificateURL.equals("")) {
                        Toast.makeText(AidProviderClaimCert.this, "You already requested, Please Wait!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //claiming occurs
                    }

                }
                else
                {
                    Toast.makeText(AidProviderClaimCert.this, "You can only claim once!",Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        downloadCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProviderCannotClaim();
                //certificate.setImageResource(R.drawable.envelope);
                //3/28/2023
                if(ContextCompat.checkSelfPermission(AidProviderClaimCert.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    saveImage();
                }
                else
                {
                    ActivityCompat.requestPermissions(AidProviderClaimCert.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
                }
                //---

            }
        });


    }
    //3/28/2023
    public void saveImage()
    {
        Uri image;
        ContentResolver contentResolver = getContentResolver();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            image = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }
        else
        {
            image = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,System.currentTimeMillis()+".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE,"images/*");
        Uri uri = contentResolver.insert(image,contentValues);

        try{
            BitmapDrawable bitmapDrawable = (BitmapDrawable) certificate.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            Objects.requireNonNull(outputStream);

            Toast.makeText(AidProviderClaimCert.this, "Downloaded! Thank you for your service!",Toast.LENGTH_SHORT).show();
            certificate.setImageResource(R.drawable.envelope);

        }catch (Exception e)
        {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                saveImage();
            }
            else
            {
                Toast.makeText(AidProviderClaimCert.this,"Please Accept The Permission!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //----
    public void gettingProviderData()
    {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot datasnapshot) {

            dr.child(ma.userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            DataSnapshot snaps = task.getResult();

                            String commendC = String.valueOf(snaps.child("commends").getValue());
                            String provC = String.valueOf(snaps.child("provision_count").getValue());
                            status = String.valueOf(snaps.child("claimCert").getValue());
                            //3/26/2023
                            certificateURL = String.valueOf(snaps.child("certURL").getValue());
                            //----

                            amountOfCommends = Integer.parseInt(commendC);

                            commendsProvs.setText("Commends: "+commendC+"\n"+"Provisions: "+provC);

                            if(flag) {
                                //3/27/2023
                                if (status.equals("0")) {

                                    if (amountOfCommends >= 50) {
                                        updateProviderData();
                                        Toast.makeText(AidProviderClaimCert.this, "Alright! Wait for 2-3 Working Days!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AidProviderClaimCert.this, "Still fell short, work harder!", Toast.LENGTH_SHORT).show();
                                    }

                                } else if (status.equals("1")) {
                                        Toast.makeText(AidProviderClaimCert.this, "You already requested, Please Wait!", Toast.LENGTH_SHORT).show();


                                } else if(status.equals("2")){
                                    //3/28/2023
                                    Toast.makeText(AidProviderClaimCert.this, "Congratulations! We are Proud of you!", Toast.LENGTH_LONG).show();
                                    Picasso.get().load(certificateURL).into(certificate);
                                    downloadCert.setVisibility(View.VISIBLE);
                                    //--

                                }
                                else
                                {
                                    Toast.makeText(AidProviderClaimCert.this, "You can only claim a certificate once!", Toast.LENGTH_SHORT).show();
                                }
                                //----
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

    public void updateProviderData()
    {
        HashMap hm = new HashMap();
        hm.put("claimCert","1");


        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
    }
    public void updateProviderCannotClaim()
    {
        HashMap hm = new HashMap();
        hm.put("claimCert","3");


        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
    }





}