package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrationDashboard extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //firebase
    public static FirebaseAuth mAuth =  FirebaseAuth.getInstance();

    //checkpoint 2/17/2023
    public static StorageReference sr = FirebaseStorage.getInstance().getReference();
    //--------
    private boolean checker = true;

    public static String username_holder;
    public static String password_holder ="";
    public static String email_holder = "";
    public static int role_holder = 0;//decider number
    public static String user_role = "AidSeeker";//actual user role
    public static String IMG_URI = "";
    public static Uri imageUri;




    EditText email,username,password;
    //ProgressBar progressBar;

    TextView textview;
    Button button,buttonregister;

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_dashboard);


        img = (ImageView) findViewById(R.id.imageholder);
        //firebase
        //mAuth = FirebaseAuth.getInstance(); ------------------------

        //the drop down operation

        Spinner sp = findViewById(R.id.sp_drop);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);
        sp.setSelection(role_holder);

        //controlling the spinner
        textview = findViewById(R.id.reg_instruction);
        if(role_holder == 0) {
            textview.setText("PROVIDE ID :");
            user_role = "AidSeeker";
        }
        else if(role_holder == 1) {
            textview.setText("  PROVIDE LICENSE :");
            user_role = "AidProvider";
        }

        email = (EditText) findViewById(R.id.et_email);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        //progressBar = (ProgressBar)findViewById(R.id.progressBar);
            //maintaining the values in the registration
            email.setText(email_holder);
            username.setText(username_holder);
            password.setText(password_holder);

        if(role_holder == 0) {
            textview.setText("PROVIDE ID :");
            user_role = "AidSeeker";
        }
        else if(role_holder == 1) {
            textview.setText("  PROVIDE LICENSE :");
            user_role = "AidProvider";
        }



        //from reg dashboard to login dash
        textview = (TextView) findViewById(R.id.txt_signinfromreg);
        textview.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RegistrationDashboard.this,MainActivity.class);
                startActivity(intent);
            }
        });

       /* button = (Button) findViewById(R.id.btn_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationDashboard.this,RegistratinDashboardFinal.class);
                startActivity(intent);
            }
        });*/

        buttonregister = (Button)findViewById(R.id.btn_register);
        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInputs();
                if(checker) {
                    Intent intent = new Intent(RegistrationDashboard.this, RegistratinDashboardFinal.class);
                    startActivity(intent);
                }
                else
                {
                    checker = true;
                }
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,2);
            }
        });


    }

    //checkpoint

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            img.setImageURI(imageUri);

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
                        Toast.makeText(RegistrationDashboard.this, "Image Accumulation failed!", Toast.LENGTH_SHORT).show();
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
    //


    private void validateInputs()
    {
     /*   String em = email.getText().toString().trim();
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(em.isEmpty())
        {
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(em).matches())
        {
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;
        }

        if(user.isEmpty())
        {
            username.setError("Username is required!");
            username.requestFocus();
            return;
        }

        if(pass.isEmpty())
        {
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }

        if(pass.length() < 6)
        {
            password.setError("Password length too small!");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(em,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            User us = new User(em,user,pass);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(us).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(RegistrationDashboard.this,"Registered Successfully!",Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                            else
                                            {
                                                Toast.makeText(RegistrationDashboard.this,"Registration Failed! Try Again!",Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(RegistrationDashboard.this,"Registration Failed!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });*/

        email_holder = email.getText().toString().trim();
        username_holder = username.getText().toString().trim();
        password_holder = password.getText().toString().trim();

        if(email_holder.isEmpty())
        {
            email.setError("Email is required!");
            email.requestFocus();
            checker = false;
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email_holder).matches())
        {
            email.setError("Please provide valid email!");
            email.requestFocus();
            checker = false;
            return;
        }

        if(username_holder.isEmpty())
        {
            username.setError("Username is required!");
            username.requestFocus();
            checker = false;
            return;
        }

        if(password_holder.isEmpty())
        {
            password.setError("Password is required!");
            password.requestFocus();
            checker = false;
            return;
        }

        if(password_holder.length() < 6)
        {
            password.setError("Password length too small!");
            password.requestFocus();
            checker = false;
            return;
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String temp = adapterView.getItemAtPosition(i).toString();

        textview = findViewById(R.id.reg_instruction);
        if(temp.equals("Aid - Provider"))
        {

            textview.setText("  PROVIDE LICENSE :");
            user_role = "AidProvider";
        }
        else if(temp.equals("Aid - Seeker"))
        {
            textview.setText("PROVIDE ID :");
            user_role = "AidSeeker";
        }
        else
        {
            email = (EditText)findViewById(R.id.et_email);
            username = (EditText)findViewById(R.id.et_username);
            password = (EditText)findViewById(R.id.et_password);

            //getting the edit text values
            email_holder = email.getText().toString();
            username_holder = username.getText().toString();
            password_holder = password.getText().toString();

            user_role = "Admin";
            Intent intent = new Intent(RegistrationDashboard.this,RegistrationDashboardContinuation.class);
            startActivity(intent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}