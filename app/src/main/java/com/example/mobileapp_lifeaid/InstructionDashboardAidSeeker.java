package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class InstructionDashboardAidSeeker extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_dashboard_aid_seeker);

        imageView = (ImageView) findViewById(R.id.img_next);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InstructionDashboardAidSeeker.this,AidSeekerMainDash.class);
                startActivity(intent);
            }
        });



    }
}