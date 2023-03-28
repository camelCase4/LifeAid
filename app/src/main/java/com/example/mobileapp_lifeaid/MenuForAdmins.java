package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuForAdmins extends AppCompatActivity {

    Button giveCert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_for_admins);
        overridePendingTransition(R.anim.slide_in_from_right,0);

        giveCert = (Button) findViewById(R.id.givecert);

        giveCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,AdminGiveCerts.class);
                startActivity(intent);
            }
        });
    }
}