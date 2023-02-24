package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AidProviderMainDash extends AppCompatActivity {

    ImageView seekerAlerts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_main_dash);

        seekerAlerts = (ImageView) findViewById(R.id.imageView20);

        seekerAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidProviderMainDash.this, MapsActivityAidProvider.class);
                startActivity(intent);
            }
        });
    }
}