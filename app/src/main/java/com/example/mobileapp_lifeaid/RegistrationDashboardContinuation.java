package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RegistrationDashboardContinuation extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    RegistrationDashboard rd = new RegistrationDashboard();
    TextView textview;
    Button button;
    EditText nameholder, passinput;

    String admin_password = "LifeAidAdmin";

    String allnames = "";
    List<String> allnames2 = new ArrayList<>();

    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Admin");

    //4/29/2023
    EditText email,username,password;
    boolean checker = true;
    //---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_dashboard_continuation);

        //the drop down operation
        Spinner sp = findViewById(R.id.sp_drop);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);

        //calling spinner value by index
        sp.setSelection(2);



        /*EditText email = (EditText) findViewById(R.id.et_email);
        EditText username = (EditText) findViewById(R.id.et_username);
        EditText password = (EditText) findViewById(R.id.et_password);*/ //commented on 4/29/2023

        email = (EditText) findViewById(R.id.et_email);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);

        nameholder = (EditText) findViewById(R.id.et_pass3);

        passinput = (EditText) findViewById(R.id.et_pass2);

        if(!rd.email_holder.equals(""))
        {
            email.setText(rd.email_holder);
        }

        if(!rd.username_holder.equals(""))
        {
            username.setText(rd.username_holder);
        }

        if(!rd.password_holder.equals(""))
        {
            password.setText(rd.password_holder);
        }

        textview = (TextView) findViewById(R.id.txt_signinfromreg);
        textview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(RegistrationDashboardContinuation.this,MainActivity.class);
                startActivity(intent);
            }
        });

        checkifnameExists();//new

        button = (Button) findViewById(R.id.btn_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rd.user_role = "Admin";
                checkifnameExists();
                validateInputs();//4/29/2023


                if(passinput.getText().toString().equals(admin_password) && checker) { //added checker 4/29/2023
                    if(allnames2.contains(nameholder.getText().toString().trim().toLowerCase())) { // added tolowercase on 4/29/2023

                        EditText email = (EditText) findViewById(R.id.et_email);
                        EditText username = (EditText) findViewById(R.id.et_username);
                        EditText password = (EditText) findViewById(R.id.et_password);

                        rd.email_holder = email.getText().toString();
                        rd.username_holder = username.getText().toString();
                        rd.password_holder = password.getText().toString();

                        Intent intent = new Intent(RegistrationDashboardContinuation.this, RegistratinDashboardFinal.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(RegistrationDashboardContinuation.this, "Admin name does not exist!", Toast.LENGTH_LONG).show();
                        nameholder.setText("");
                    }
                }
                else
                {
                    /*Toast.makeText(RegistrationDashboardContinuation.this, "Wrong Admin Key!", Toast.LENGTH_LONG).show();
                    passinput.setText("");*/ //commented on 4/29/2023
                    //4/29/2023
                    if(!checker)
                    {
                        checker = true;
                        Toast.makeText(RegistrationDashboardContinuation.this, "Please fill the necesarry requirements!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(RegistrationDashboardContinuation.this, "Wrong Admin Key!", Toast.LENGTH_LONG).show();
                        passinput.setText("");
                    }
                    //---

                }
            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String temp = adapterView.getItemAtPosition(i).toString();

        if(temp.equals("Aid - Seeker") || temp.equals("Aid - Provider"))
        {
            if(temp.equals("Aid - Seeker")) rd.role_holder = 0;
            else rd.role_holder = 1;

            EditText email = (EditText)findViewById(R.id.et_email);
            EditText username = (EditText)findViewById(R.id.et_username);
            EditText password = (EditText)findViewById(R.id.et_password);

            //getting the edit text values
            rd.email_holder = email.getText().toString();
            rd.username_holder = username.getText().toString();
            rd.password_holder = password.getText().toString();

            Intent intent = new Intent(RegistrationDashboardContinuation.this,RegistrationDashboard.class);
            startActivity(intent);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //checkpoint 2/19/2023

    public void checkifnameExists()
    {

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot ds : datasnapshot.getChildren())
                {
                    String key = ds.getKey();

                    dr.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();
                                    allnames += String.valueOf(snaps.child("fname").getValue()).toLowerCase() +" "+String.valueOf(snaps.child("lname").getValue()).toLowerCase()+" ";
                                    //added tolowercase on 4/29/2023
                                }
                                else
                                {
                                    Toast.makeText(RegistrationDashboardContinuation.this,"No Data Found!",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(RegistrationDashboardContinuation.this,"Data Fetching Failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        allnames2 = Arrays.asList(allnames.split(" "));
    }
    //-----

    //4/29/2023
    public void validateInputs()
    {
        rd.email_holder = email.getText().toString().trim();
        rd.username_holder = username.getText().toString().trim();
        rd.password_holder = password.getText().toString().trim();

        if(rd.email_holder.isEmpty())
        {
            email.setError("Email is required!");
            email.requestFocus();
            checker = false;
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(rd.email_holder).matches())
        {
            email.setError("Please provide valid email!");
            email.requestFocus();
            checker = false;
            return;
        }

        if(rd.username_holder.isEmpty())
        {
            username.setError("Username is required!");
            username.requestFocus();
            checker = false;
            return;
        }

        if(rd.password_holder.isEmpty())
        {
            password.setError("Password is required!");
            password.requestFocus();
            checker = false;
            return;
        }

        if(rd.password_holder.length() < 6)
        {
            password.setError("Password length too small!");
            password.requestFocus();
            checker = false;
            return;
        }
    }
    //---
}