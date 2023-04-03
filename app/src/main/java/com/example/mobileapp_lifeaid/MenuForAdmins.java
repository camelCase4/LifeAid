package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MenuForAdmins extends AppCompatActivity {

    ImageView home;
    Button giveCert,faqs,lgout,editacc,askAid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_for_admins);
        overridePendingTransition(R.anim.slide_in_from_right,0);

        giveCert = (Button) findViewById(R.id.givecert);
        //4/4/2023
        faqs = (Button) findViewById(R.id.faqbtn);
        lgout = (Button) findViewById(R.id.loginbutton3);
        editacc = (Button) findViewById(R.id.editacc);
        askAid = (Button) findViewById(R.id.askaid);
        home = (ImageView) findViewById(R.id.back);

        editacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,UserEditingPage.class);
                startActivity(intent);
            }
        });

        faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,FAQPage.class);
                startActivity(intent);
            }
        });
        lgout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,MainActivity.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        //---

        giveCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuForAdmins.this,AdminGiveCerts.class);
                startActivity(intent);
            }
        });
    }
}