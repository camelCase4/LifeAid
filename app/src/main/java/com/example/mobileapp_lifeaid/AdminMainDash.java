package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminMainDash extends AppCompatActivity {

    TextView tv,fortheprovider,lbs;
    ImageView iv,iv2;

    TextView manageRecs;

    ImageView menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main_dash);

        tv = findViewById(R.id.tv_registration14);
        iv = findViewById(R.id.imageView24);
        iv2 = findViewById(R.id.imageView19);
        fortheprovider = findViewById(R.id.tv_registration15);
        menu = (ImageView) findViewById(R.id.imageView18);
        manageRecs = (TextView) findViewById(R.id.tv_registration17);
        //4/3/2023
        lbs = (TextView) findViewById(R.id.tv_registration20);

        lbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainDash.this, AdminLeaderboardDash.class);
                startActivity(intent);
            }
        });
        //---

        //3/28/2023
        manageRecs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainDash.this,AdminManageRecords.class);
                startActivity(intent);
            }
        });
        //---

        //3/28/2023
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainDash.this,MenuForAdmins.class);
                startActivity(intent);
            }
        });
        //--

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainDash.this, Admin_AidSeeker_Validation.class);
                startActivity(intent);
            }
        });

        fortheprovider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainDash.this, Admin_AidProvider_Validation.class);
                startActivity(intent);
            }
        });
    }
}