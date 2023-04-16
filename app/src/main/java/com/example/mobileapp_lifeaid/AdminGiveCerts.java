package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Random;

public class AdminGiveCerts extends AppCompatActivity {

    TextView cont;
    ImageView upload,menu;
    Button grant;

    String providerUID;

    //3/28/2023
    Random rand = new Random();
    int sleepTime = rand.nextInt(701) + 1000;

    String IMG_URI = "";
    Uri imageUri;

    public static StorageReference sr = FirebaseStorage.getInstance().getReference();

    boolean clickedUID = false;

    //---

    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_give_certs);

        cont = (TextView) findViewById(R.id.content);
        upload = (ImageView) findViewById(R.id.imageView35);
        grant = (Button) findViewById(R.id.validatebutton);

        cont.setMovementMethod(new ScrollingMovementMethod());
        gettingProviderWithRequests();

        //4/16/2023
        menu = (ImageView) findViewById(R.id.imageView18);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminGiveCerts.this,MenuForAdmins.class);
                startActivity(intent);
            }
        });
        //---

        Toast.makeText(AdminGiveCerts.this,"Please Click a user UID!",Toast.LENGTH_SHORT).show();

        grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //3/28/2023
                for(;;)
                {
                    try{
                        Thread.sleep(sleepTime);
                        checkingIfStillValid();
                        break;

                    }catch(InterruptedException e)
                    {

                    }
                }
                //----
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,2);
            }
        });
    }

    //3/28/2023
    public void gettingProviderWithRequests()
    {
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                cont.setText("");
                upload.setImageResource(R.drawable.download);
                grant.setText("Grant Certificate");

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
                                    if(String.valueOf(snaps.child("claimCert").getValue()).equals("1")) {
                                       String tempproviderUID = uid;
                                       String con_num = String.valueOf(snaps.child("phonenum").getValue());
                                       String fullName = String.valueOf(snaps.child("fname").getValue()) +" "+String.valueOf(snaps.child("lname").getValue());


                                       //String temp = con_num +"             "+tempproviderUID+"\n\n"; original
                                       //4/2/2023
                                        String temp = con_num +"             "+tempproviderUID;
                                        //---
                                        SpannableString ss = new SpannableString(temp);
                                        ClickableSpan clickableSpan = new ClickableSpan() {

                                            @Override
                                            public void onClick(View textView) {
                                                providerUID = tempproviderUID;
                                                grant.setText("Grant Certificate To "+fullName);
                                                clickedUID = true;
                                                //Toast.makeText(AdminGiveCerts.this,providerUID,Toast.LENGTH_SHORT).show();


                                            }
                                            @Override
                                            public void updateDrawState(TextPaint ds) {
                                                super.updateDrawState(ds);
                                                ds.setUnderlineText(false);
                                            }
                                        };
                                        ss.setSpan(clickableSpan, 23, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);



                                        cont.append(ss);
                                        cont.append("\n\n"); //4/2/2023
                                        cont.setMovementMethod(LinkMovementMethod.getInstance());
                                        cont.setHighlightColor(Color.TRANSPARENT);

                                        //------

                                    }

                                }
                                else
                                {
                                    Toast.makeText(AdminGiveCerts.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AdminGiveCerts.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
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

    public void checkingIfStillValid()
    {
        if(clickedUID) {
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    dr.child(providerUID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    DataSnapshot snaps = task.getResult();
                                    String urlcontent = String.valueOf(snaps.child("certURL").getValue());
                                    if (urlcontent.equals("")) {
                                        storingImageURL();
                                    } else {
                                        Toast.makeText(AdminGiveCerts.this, "Other Admin was on it!", Toast.LENGTH_SHORT).show();
                                        upload.setImageResource(R.drawable.download);
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
        else
        {
            Toast.makeText(AdminGiveCerts.this,"No UID clicked!",Toast.LENGTH_SHORT).show();

        }
    }

    public void storingImageURL()
    {
        HashMap hm = new HashMap();
        hm.put("certURL",IMG_URI);
        hm.put("claimCert","2");

        if(IMG_URI.equals(""))
        {
            Toast.makeText(AdminGiveCerts.this, "Please Provide Certificate File or Please Wait for it to upload!", Toast.LENGTH_SHORT).show();
        }
        else {
            dr.child(providerUID).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    upload.setImageResource(R.drawable.download);
                    grant.setText("Grant Certificate");
                }
            });
        }
    }

    //3/28/2023
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            upload.setImageURI(imageUri);

            //checkpoint 2/17/2023
            if(imageUri != null)
            {
                StorageReference storRef = sr.child(System.currentTimeMillis()+"."+fileExt(imageUri));
                storRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                IMG_URI = uri.toString();
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
                        Toast.makeText(AdminGiveCerts.this, "Image Accumulation failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //---------

        }
    }
    //checkpoint 2/17/2023
    private String fileExt(Uri im_uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap m = MimeTypeMap.getSingleton();
        return m.getExtensionFromMimeType(cr.getType(im_uri));
    }
    //---
    //--
}