package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MenuButtonForSeekers extends AppCompatActivity {

    ImageView bk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_button_for_seekers);
        overridePendingTransition(R.anim.slide_in_from_right,0);

        bk = (ImageView) findViewById(R.id.back);

        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MenuButtonForSeekers.this,AidSeekerMainDash.class);
                //startActivity(intent);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }
}