package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AidSeekerIntro1 extends AppCompatActivity {

    ImageView iv;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_seeker_intro1);

        iv = (ImageView) findViewById(R.id.imageView33);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidSeekerIntro1.this,Intro2.class);
                startActivity(intent);
            }
        });

        tv = (TextView) findViewById(R.id.tv_registration21);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidSeekerIntro1.this,AidSeekerMainDash.class);
                startActivity(intent);
            }
        });
    }
}