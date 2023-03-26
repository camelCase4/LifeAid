package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AidProviderClaimCert extends AppCompatActivity {

    TextView commendsProvs;
    ImageView certificate;

    MainActivity ma = new MainActivity();
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");

    int amountOfCommends = 0;
    String status = ""; //1 = pwedi pa mo kuha, 2 = di na pwedi, zero ang initial
    String certificateURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_claim_cert);

        commendsProvs = (TextView) findViewById(R.id.commendsandprovisions);
        certificate = (ImageView) findViewById(R.id.cert);

        gettingProviderData();

        certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gettingProviderData();
                if(status.equals("0")) {
                    if (amountOfCommends >= 50) {
                        updateProviderData();
                        Toast.makeText(AidProviderClaimCert.this, "Alright! Wait for 2-3 Working Days!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AidProviderClaimCert.this, "Still fell short, work harder!", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(status.equals("1"))
                {
                    if(certificateURL.equals("")) {
                        Toast.makeText(AidProviderClaimCert.this, "You already requested, Please Wait!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //claiming occurs
                    }

                }
                else
                {
                    Toast.makeText(AidProviderClaimCert.this, "You can only claim once!",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    public void gettingProviderData()
    {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot datasnapshot) {

            dr.child(ma.userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            DataSnapshot snaps = task.getResult();

                            String commendC = String.valueOf(snaps.child("commends").getValue());
                            String provC = String.valueOf(snaps.child("provision_count").getValue());
                            status = String.valueOf(snaps.child("claimCert").getValue());
                            //3/26/2023
                            certificateURL = String.valueOf(snaps.child("certURL").getValue());
                            //----

                            amountOfCommends = Integer.parseInt(commendC);

                            commendsProvs.setText("Commends: "+commendC+"\n"+"Provisions: "+provC);

                        }
                    }
                }
            });

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

    }

    public void updateProviderData()
    {
        HashMap hm = new HashMap();
        hm.put("claimCert","1");


        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });
    }



}