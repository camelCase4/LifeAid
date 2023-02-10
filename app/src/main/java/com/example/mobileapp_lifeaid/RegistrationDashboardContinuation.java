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
import android.widget.Toast;

public class RegistrationDashboardContinuation extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    RegistrationDashboard rd = new RegistrationDashboard();
    TextView textview;
    Button button;

    String admin_password = "LifeAidAdmin";

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



        EditText email = (EditText) findViewById(R.id.et_email);
        EditText username = (EditText) findViewById(R.id.et_username);
        EditText password = (EditText) findViewById(R.id.et_password);

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

        button = (Button) findViewById(R.id.btn_register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rd.user_role = "Admin";

                EditText passinput = (EditText) findViewById(R.id.et_pass2);
                if(passinput.getText().toString().equals(admin_password)) {

                    EditText email = (EditText)findViewById(R.id.et_email);
                    EditText username = (EditText)findViewById(R.id.et_username);
                    EditText password = (EditText)findViewById(R.id.et_password);

                    rd.email_holder = email.getText().toString();
                    rd.username_holder = username.getText().toString();
                    rd.password_holder = password.getText().toString();

                    Intent intent = new Intent(RegistrationDashboardContinuation.this, RegistratinDashboardFinal.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(RegistrationDashboardContinuation.this, "Admin Password Incorrect! Try again!", Toast.LENGTH_LONG).show();
                    passinput.setText("");
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
}