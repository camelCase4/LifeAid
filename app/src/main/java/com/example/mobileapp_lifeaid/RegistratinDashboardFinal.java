package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;


public class RegistratinDashboardFinal extends AppCompatActivity {

    RegistrationDashboard rd = new RegistrationDashboard();

    EditText fname,lname,age,phonenum,address,gender;
    TextView textview;
    Button donebutton;
    ProgressBar progressBar;
    String firstName,lastName,edad,phoneNumber,lugar,kasarian;

    //FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registratin_dashboard_final);

        textview = (TextView) findViewById(R.id.txt_signinfromreg);
        textview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RegistratinDashboardFinal.this,MainActivity.class);
                startActivity(intent);
            }
        });

        fname = (EditText) findViewById(R.id.et_fname);
        lname = (EditText) findViewById(R.id.et_lname);
        age= (EditText) findViewById(R.id.et_age);
        phonenum = (EditText) findViewById(R.id.et_pn);
        address = (EditText) findViewById(R.id.et_address);
        gender= (EditText) findViewById(R.id.et_gender);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);



        //rd.mAuth = FirebaseAuth.getInstance();

        donebutton = (Button) findViewById(R.id.btn_register);
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = fname.getText().toString().trim();
                lastName = lname.getText().toString().trim();
                edad = age.getText().toString().trim();
                phoneNumber = phonenum.getText().toString().trim();
                lugar = address.getText().toString().trim();
                kasarian = gender.getText().toString().trim();
                boolean addedcontacts = true; //--------------------------newly
                String trustedname_1 ="",trustedname_2="",trustednum_1="",trustednum_2="";
                boolean admin_approved = false;

                //--

                progressBar.setVisibility(View.VISIBLE);
                (rd.mAuth).createUserWithEmailAndPassword(rd.email_holder,rd.password_holder)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    User us = new User(rd.email_holder,rd.username_holder,rd.password_holder,rd.user_role,firstName,lastName,edad,phoneNumber,lugar,kasarian,addedcontacts,trustednum_1,trustednum_2,trustedname_1,trustedname_2,admin_approved,rd.IMG_URI);
                                    if(rd.user_role.equals("AidSeeker"))
                                    {
                                        FirebaseDatabase.getInstance().getReference("Aid-Seeker")
                                                .child(rd.mAuth.getCurrentUser().getUid()) //this line change the auth logic
                                                .setValue(us).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(RegistratinDashboardFinal.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        } else {
                                                            Toast.makeText(RegistratinDashboardFinal.this, "Registration Failed! Try again!", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });


                                    }
                                    else if(rd.user_role.equals("AidProvider"))
                                    {
                                        FirebaseDatabase.getInstance().getReference("Aid-Provider")
                                                .child(rd.mAuth.getCurrentUser().getUid())
                                                .setValue(us).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(RegistratinDashboardFinal.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        } else {
                                                            Toast.makeText(RegistratinDashboardFinal.this, "Registration Failed! Try again!", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        FirebaseDatabase.getInstance().getReference("Admin")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(us).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(RegistratinDashboardFinal.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        } else {
                                                            Toast.makeText(RegistratinDashboardFinal.this, "Registration Failed! Try again!", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }
                                                });
                                    }

                                }
                                else
                                {
                                    Toast.makeText(RegistratinDashboardFinal.this,"Registration Failed!",Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
    }
}