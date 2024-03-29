package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //user values
    public static String fullname,gender,age,useremail,address,phonenum,userrole,username,addedcontact,approved_byadmin,contactPhoneNums;//added contactPhoneNums on 5/1
    public static String trustedcontact1,trustedcontact2;

    //checkpoint 3/1/2023
    public static String ap_job;
    //--

    //--
    EditText et_signin, et_passin;//for the username and password input
    TextView textview;//for the signin text view nga murag button
    Button button; // button for the login

    FirebaseAuth mAuth;
    ProgressBar progressBarlog;

    String[] as_info = new String[9];
    int as_info_indexer = 0;

    String email,password,role_pass;

    public static String userid;

    TextView forgotPass; //4/14/2023


    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_signin = (EditText) findViewById(R.id.editTextUsername);
        et_passin = (EditText) findViewById(R.id.edittextpass);
        progressBarlog = (ProgressBar) findViewById(R.id.progressBarlogin);

        forgotPass = (TextView) findViewById(R.id.txt_fgpass);//4/14/2023

        mAuth = FirebaseAuth.getInstance();
        //from login dash to reg dash
        textview = (TextView) findViewById(R.id.txt_signuplink);
        textview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,RegistrationDashboard.class);
                startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.loginbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker();

            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass();
            }
        });



    }
    //4/14/2023
    public void changePass()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(MainActivity.this);
        alert.setMessage("Enter Your Email");


        alert.setView(edittext);

        alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(edittext.getText().toString().equals("") || edittext.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Email is Empty!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!Patterns.EMAIL_ADDRESS.matcher(edittext.getText().toString()).matches())
                    {
                        Toast.makeText(MainActivity.this,"Email provided is invalid!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        progressBarlog.setVisibility(View.VISIBLE);
                        mAuth.sendPasswordResetEmail(edittext.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    progressBarlog.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this,"Please check your email notifications and change your password!",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this,"Something went wrong, try again!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();

    }
    //---
    private void checker()
    {
        et_signin = (EditText) findViewById(R.id.editTextUsername);
        et_passin = (EditText) findViewById(R.id.edittextpass);
        email = et_signin.getText().toString().trim();
        password = et_passin.getText().toString().trim();

        if(email.isEmpty())
        {
            et_signin.setError("Email is required!");
            et_signin.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            et_passin.setError("Password is required");
            et_passin.requestFocus();
            return;
        }

        if(password.length() < 6)
        {
            et_passin.setError("Minimum password length is 6!");
            et_passin.requestFocus();
            return;
        }


        progressBarlog.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    userid = task.getResult().getUser().getUid();

                    DatabaseReference as = ref.child("Aid-Seeker");

                    as.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                                if(datasnapshot.child(userid).exists())
                                {
                                    DatabaseReference as_details = ref.child(userid);
                                    as_details.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                                                //Toast.makeText(MainActivity.this, datasnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();

                                           // Intent intent = new Intent(MainActivity.this,AddTrustedContactDashboard.class);
                                            //startActivity(intent);

                                            /*Intent intent = new Intent(MainActivity.this, AidSeekerIntro1.class);
                                            startActivity(intent);*/


                                            //checkpoint in case error
                                            DatabaseReference testref = FirebaseDatabase.getInstance().getReference("Aid-Seeker");

                                            testref.child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        if(task.getResult().exists())
                                                        {

                                                            String fname,lname;
                                                            DataSnapshot ds = task.getResult();
                                                            fname = String.valueOf(ds.child("fname").getValue());
                                                            lname = String.valueOf(ds.child("lname").getValue());
                                                            fullname = fname +" "+lname;

                                                            age = String.valueOf(ds.child("age").getValue());
                                                            gender = String.valueOf(ds.child("gender").getValue());
                                                            username = String.valueOf(ds.child("username").getValue());
                                                            address = String.valueOf(ds.child("address").getValue());
                                                            useremail = String.valueOf(ds.child("email").getValue());
                                                            userrole = String.valueOf(ds.child("role").getValue());
                                                            phonenum = String.valueOf(ds.child("phonenum").getValue());
                                                            addedcontact = String.valueOf(ds.child("prompt_trustedContacts").getValue());
                                                            approved_byadmin = String.valueOf(ds.child("admin_approved").getValue());
                                                            trustedcontact1 = String.valueOf(ds.child("trustedphonenum_1").getValue());
                                                            trustedcontact2 = String.valueOf(ds.child("trustedphonenum_2").getValue());
                                                            contactPhoneNums = String.valueOf(ds.child("contactNumbers").getValue());

                                                            if(approved_byadmin.equals("true")) {

                                                                if (addedcontact.equals("true")) {


                                                                    Intent intent = new Intent(MainActivity.this, AidSeekerIntro1.class);
                                                                    startActivity(intent);
                                                                } else {
                                                                    Intent intent = new Intent(MainActivity.this, AidSeekerMainDash.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(MainActivity.this,"Pending Approval!",Toast.LENGTH_SHORT).show();
                                                                progressBarlog.setVisibility(View.INVISIBLE);
                                                            }


                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(MainActivity.this,"Failed to read!",Toast.LENGTH_SHORT).show();
                                                            progressBarlog.setVisibility(View.INVISIBLE); //4/1/2023

                                                        }
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(MainActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
                                                        progressBarlog.setVisibility(View.INVISIBLE);//4/1/2023

                                                    }
                                                }
                                            });

                                            //--
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                                else
                                {
                                    forAidProviders();
                                }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                else
                {
                    Toast.makeText(MainActivity.this, "Failed To login!",Toast.LENGTH_LONG).show();
                    progressBarlog.setVisibility(View.INVISIBLE);//4/1/2023

                }
            }
        });
    }

    public void forAidProviders()
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    userid = task.getResult().getUser().getUid();

                    DatabaseReference ap = ref.child("Aid-Provider");

                    ap.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                            if(datasnapshot.child(userid).exists())
                            {
                                DatabaseReference ap_details = ref.child(userid);
                                ap_details.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {



                                        //checkpoint 3/1/2023
                                        DatabaseReference testref = FirebaseDatabase.getInstance().getReference("Aid-Provider");
                                        testref.child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    if(task.getResult().exists())
                                                    {
                                                        String fname,lname;
                                                        DataSnapshot ds = task.getResult();
                                                        fname = String.valueOf(ds.child("fname").getValue());
                                                        lname = String.valueOf(ds.child("lname").getValue());
                                                        fullname = fname +" "+lname;

                                                        age = String.valueOf(ds.child("age").getValue());
                                                        gender = String.valueOf(ds.child("gender").getValue());
                                                        username = String.valueOf(ds.child("username").getValue());
                                                        address = String.valueOf(ds.child("address").getValue());
                                                        useremail = String.valueOf(ds.child("email").getValue());
                                                        userrole = String.valueOf(ds.child("role").getValue());
                                                        phonenum = String.valueOf(ds.child("phonenum").getValue());
                                                        addedcontact = String.valueOf(ds.child("prompt_trustedContacts").getValue());//check if first time login or not
                                                        approved_byadmin = String.valueOf(ds.child("admin_approved").getValue());
                                                        ap_job = String.valueOf(ds.child("job").getValue());

                                                        if(approved_byadmin.equals("true"))
                                                        {
                                                            if(addedcontact.equals("true"))
                                                            {
                                                                Intent intent = new Intent(MainActivity.this, InstructionDashboardAidProvider.class);
                                                                startActivity(intent);
                                                            }
                                                            else
                                                            {
                                                                Intent intent = new Intent(MainActivity.this, AidProviderMainDash.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(MainActivity.this,"Pending Approval!",Toast.LENGTH_SHORT).show();
                                                            progressBarlog.setVisibility(View.INVISIBLE);
                                                        }

                                                    }
                                                }
                                            }
                                        });
                                        //-----
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else
                            {
                                forTheAdmins();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    //3/27/2023
    public void forTheAdmins()
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    userid = task.getResult().getUser().getUid();

                    DatabaseReference ap = ref.child("Admin");

                    ap.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                            if(datasnapshot.child(userid).exists())
                            {
                                DatabaseReference ap_details = ref.child(userid);
                                ap_details.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {



                                        //checkpoint 3/1/2023
                                        DatabaseReference testref = FirebaseDatabase.getInstance().getReference("Admin");
                                        testref.child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    if(task.getResult().exists())
                                                    {
                                                        String fname,lname;
                                                        DataSnapshot ds = task.getResult();
                                                        fname = String.valueOf(ds.child("fname").getValue());
                                                        lname = String.valueOf(ds.child("lname").getValue());
                                                        fullname = fname +" "+lname;

                                                        age = String.valueOf(ds.child("age").getValue());
                                                        gender = String.valueOf(ds.child("gender").getValue());
                                                        username = String.valueOf(ds.child("username").getValue());
                                                        address = String.valueOf(ds.child("address").getValue());
                                                        useremail = String.valueOf(ds.child("email").getValue());
                                                        userrole = String.valueOf(ds.child("role").getValue());
                                                        phonenum = String.valueOf(ds.child("phonenum").getValue());
                                                        addedcontact = String.valueOf(ds.child("prompt_trustedContacts").getValue());//check if first time login or not
                                                        approved_byadmin = String.valueOf(ds.child("admin_approved").getValue());
                                                        ap_job = String.valueOf(ds.child("job").getValue());

                                                       Intent intent = new Intent(MainActivity.this,AdminMainDash.class);
                                                       startActivity(intent);

                                                    }
                                                }
                                            }
                                        });
                                        //-----
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    //----
}