package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class AidProviderRecords extends AppCompatActivity {

    //3/20/2023
    ImageView st1, st2, st3, st4, ivExit;
    TextView overallProvision, overallSupports, good,bad, exitTV,negativeStar;

    MainActivity ma = new MainActivity();

    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
    //----
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_provider_records);

        //3/20/2023
        st1 = (ImageView) findViewById(R.id.star1);
        st2 = (ImageView) findViewById(R.id.star2);
        st3 = (ImageView) findViewById(R.id.star3);
        st4 = (ImageView) findViewById(R.id.star4);
        ivExit = (ImageView) findViewById(R.id.exit);

        overallProvision = (TextView) findViewById(R.id.provisioncount);
        overallSupports = (TextView) findViewById(R.id.supportcount);
        good = (TextView) findViewById(R.id.commendcount);
        bad = (TextView) findViewById(R.id.reportcount);
        exitTV = (TextView) findViewById(R.id.tv_ex);
        negativeStar = (TextView) findViewById(R.id.tv_registration22);


        st1.setVisibility(View.INVISIBLE);
        st2.setVisibility(View.INVISIBLE);
        st3.setVisibility(View.INVISIBLE);
        st4.setVisibility(View.INVISIBLE);

        gettingProviderData();

        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidProviderRecords.this,AidProviderMainDash.class);
                startActivity(intent);
            }
        });
        exitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AidProviderRecords.this,AidProviderMainDash.class);
                startActivity(intent);
            }
        });
        //---
    }

    public void gettingProviderData() {

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                dr.child(ma.userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                DataSnapshot snaps = task.getResult();

                                String totalCount = String.valueOf(snaps.child("provision_count").getValue()); //3/23/2023
                                //overallProvision.setText(String.valueOf(snaps.child("provision_count").getValue()));
                                overallProvision.setText(totalCount);
                                overallSupports.setText(String.valueOf(snaps.child("support_count").getValue()));
                                String commendC = String.valueOf(snaps.child("commends").getValue());
                                String reportC = String.valueOf(snaps.child("decommends").getValue());


                                good.setText(commendC);
                                bad.setText(reportC);
                                displayRating(commendC,reportC,totalCount);
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

    public void displayRating(String cc, String rc, String tc)
    {
        /*int score =  Integer.parseInt(cc) - Integer.parseInt(rc);

        if(score >= 4)
        {
            st1.setVisibility(View.VISIBLE);
            st2.setVisibility(View.VISIBLE);
            st3.setVisibility(View.VISIBLE);
            st4.setVisibility(View.VISIBLE);
        }
        else
        {
            if(score == 1)
            {
                st1.setVisibility(View.VISIBLE);
            }
            else if(score == 2)
            {
                st1.setVisibility(View.VISIBLE);
                st2.setVisibility(View.VISIBLE);
            }
            else if(score == 3)
            {
                st1.setVisibility(View.VISIBLE);
                st2.setVisibility(View.VISIBLE);
                st3.setVisibility(View.VISIBLE);
            }
            else
            {
                negativeStar.setText("POOR PERFORMANCE!");
            }
        }*/

        // 3/23/2023
        double ratings = (Double.parseDouble(cc) / Double.parseDouble(tc)) * 100;



        if(ratings == 100)
        {
            st1.setVisibility(View.VISIBLE);
            st2.setVisibility(View.VISIBLE);
            st3.setVisibility(View.VISIBLE);
            st4.setVisibility(View.VISIBLE);
        }
        else if(ratings >= 75 && ratings <= 99)
        {
            st1.setVisibility(View.VISIBLE);
            st2.setVisibility(View.VISIBLE);
            st3.setVisibility(View.VISIBLE);
        }
        else if(ratings >= 50 && ratings <= 74)
        {
            st1.setVisibility(View.VISIBLE);
            st2.setVisibility(View.VISIBLE);
        }
        else if(ratings >= 25 && ratings <= 49)
        {
            st1.setVisibility(View.VISIBLE);
        }
        else
        {
            negativeStar.setText("POOR PERFORMANCE!");
        }


        //----

    }


}