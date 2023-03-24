package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MenuButtonForProviders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_button_for_providers);
        overridePendingTransition(R.anim.slide_in_from_right,0);
    }
}