package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class RegistrationDashboard extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static String username_holder;
    public static String password_holder;
    public static String email_holder;
    public static int role_holder = 0;


    TextView textview;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_dashboard);

        //the drop down operation

        Spinner sp = findViewById(R.id.sp_drop);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);
        sp.setSelection(role_holder);

        //controlling the spinner
        textview = findViewById(R.id.reg_instruction);
        if(role_holder == 0)  textview.setText("PROVIDE ID :");
        else if(role_holder == 1) textview.setText("  PROVIDE LICENSE :");

        EditText email = (EditText) findViewById(R.id.et_fname);
        EditText username = (EditText) findViewById(R.id.et_lname);
        EditText password = (EditText) findViewById(R.id.et_age);
            //maintaining the values in the registration
            email.setText(email_holder);
            username.setText(username_holder);
            password.setText(password_holder);



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

        button = (Button) findViewById(R.id.btn_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationDashboard.this,RegistratinDashboardFinal.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String temp = adapterView.getItemAtPosition(i).toString();

        textview = findViewById(R.id.reg_instruction);
        if(temp.equals("Aid - Provider"))
        {

            textview.setText("  PROVIDE LICENSE :");
        }
        else if(temp.equals("Aid - Seeker"))
        {
            textview.setText("PROVIDE ID :");
        }
        else
        {
            EditText email = (EditText)findViewById(R.id.et_fname);
            EditText username = (EditText)findViewById(R.id.et_lname);
            EditText password = (EditText)findViewById(R.id.et_age);

            //getting the edit text values
            email_holder = email.getText().toString();
            username_holder = username.getText().toString();
            password_holder = password.getText().toString();


            Intent intent = new Intent(RegistrationDashboard.this,RegistrationDashboardContinuation.class);
            startActivity(intent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}