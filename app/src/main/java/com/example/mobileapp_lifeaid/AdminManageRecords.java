package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AdminManageRecords extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    int role_holder = 0;
    String chosenRole = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_records);





        Spinner sp = findViewById(R.id.sp_drop);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.LifeAidUsers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);
        sp.setSelection(role_holder);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String temp = adapterView.getItemAtPosition(i).toString();


        if(temp.equals("Aid-Provider"))
        {
            Toast.makeText(AdminManageRecords.this,"Provider Selected",Toast.LENGTH_SHORT).show();
        }
        else if(temp.equals("Aid-Seeker"))
        {
            Toast.makeText(AdminManageRecords.this,"Seeker Selected",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}