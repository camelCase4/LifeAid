package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AidProviderLeaderboardDash extends AppCompatActivity {

    TextView leadProvs;

    int providerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_leaderboard_dash);

        //3/16/2023
        leadProvs = (TextView) findViewById(R.id.leaders);

        leadProvs.setMovementMethod(new ScrollingMovementMethod());
        //---

    }


}