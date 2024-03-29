package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class FAQPage extends AppCompatActivity {

    ImageView bk;

    MainActivity ma = new MainActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqpage);

        bk = (ImageView) findViewById(R.id.back);

        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ma.userrole.equals("AidProvider"))
                {
                    Intent intent = new Intent(FAQPage.this,MenuButtonForProviders.class);
                    startActivity(intent);

                }
                else if(ma.userrole.equals("AidSeeker"))
                {
                    Intent intent = new Intent(FAQPage.this,MenuButtonForSeekers.class);
                    startActivity(intent);

                }
                else
                {
                    Intent intent = new Intent(FAQPage.this,MenuForAdmins.class);
                    startActivity(intent);
                }
            }
        });
    }
}