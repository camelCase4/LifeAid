package com.example.mobileapp_lifeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AidProviderClaimCert extends AppCompatActivity {

    TextView commendsProvs;
    ImageView certificate;


    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_claim_cert);

        commendsProvs = (TextView) findViewById(R.id.commendsandprovisions);
        certificate = (ImageView) findViewById(R.id.cert);



    }
    public void gettingProviderData()
    {

    }

}