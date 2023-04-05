package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AidSeekerHistory extends AppCompatActivity {

    TextView historyContents,sequenceOftype;


    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("AidSeekerHistory");
    MainActivity ma = new MainActivity();

    int sequence = 0, determiner = 0; //4/3/2023
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_seeker_history);

        historyContents = (TextView) findViewById(R.id.contents);
        sequenceOftype = (TextView) findViewById(R.id.seq);



        historyContents.setMovementMethod(new ScrollingMovementMethod());

        gettingData();

        //4/3/2023
        sequenceOftype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sequence++;
                if(sequence == 0)
                {
                    sequenceOftype.setText("C R I M E   > > >");
                }
                else if(sequence == 1)
                {
                    sequenceOftype.setText("F I R E   > > >");
                }
                else if(sequence == 2)
                {
                    sequenceOftype.setText("H E A L T H   > > >");
                }
                else
                {
                    sequenceOftype.setText("A L L   > > >");
                }
                try {
                    gettingData();
                }
                catch (Exception e)
                {
                    Toast.makeText(AidSeekerHistory.this,"Woah, Slow down!",Toast.LENGTH_SHORT).show();
                    gettingData();
                }
            }
        });
        //---


    }
    public void gettingData()
    {
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                historyContents.setText("");

                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String uid = ds.getKey();

                    dr.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists())
                                {
                                    DataSnapshot snaps = task.getResult();
                                    /*if(String.valueOf(snaps.child("seekeruid").getValue()).equals(ma.userid)) {
                                        String dt = String.valueOf(snaps.child("timedate").getValue()).substring(4,16);
                                        String et = String.valueOf(snaps.child("emergencytype").getValue());
                                        String pn = String.valueOf(snaps.child("providername").getValue());

                                        historyContents.append("   "+dt + "                  "+et+"                  "+pn+"\n\n");

                                    }*/ // original

                                    //4/3/2023
                                    determiner++;
                                    String emtype = String.valueOf(snaps.child("emergencytype").getValue());
                                    if(sequence == 0)
                                    {
                                        if(emtype.equals("All"))
                                        {
                                            if(String.valueOf(snaps.child("seekeruid").getValue()).equals(ma.userid)) {
                                                String dt = String.valueOf(snaps.child("timedate").getValue()).substring(4,16);
                                                String et = String.valueOf(snaps.child("emergencytype").getValue());
                                                String pn = String.valueOf(snaps.child("providername").getValue());

                                                historyContents.append("   "+dt + "                  "+et+"                       "+pn+"\n\n");

                                            }
                                        }
                                    }
                                    else if(sequence == 1)
                                    {
                                        if(emtype.equals("Crime"))
                                        {
                                            if(String.valueOf(snaps.child("seekeruid").getValue()).equals(ma.userid)) {
                                                String dt = String.valueOf(snaps.child("timedate").getValue()).substring(4,16);
                                                String et = String.valueOf(snaps.child("emergencytype").getValue());
                                                String pn = String.valueOf(snaps.child("providername").getValue());

                                                historyContents.append("   "+dt + "               "+et+"                  "+pn+"\n\n");

                                                
                                            }
                                        }
                                    }
                                    else if(sequence == 2)
                                    {
                                        if(emtype.equals("Fire"))
                                        {
                                            if(String.valueOf(snaps.child("seekeruid").getValue()).equals(ma.userid)) {
                                                String dt = String.valueOf(snaps.child("timedate").getValue()).substring(4,16);
                                                String et = String.valueOf(snaps.child("emergencytype").getValue());
                                                String pn = String.valueOf(snaps.child("providername").getValue());

                                                historyContents.append("   "+dt + "                  "+et+"                  "+pn+"\n\n");

                                            }
                                        }
                                    }
                                    else
                                    {
                                        sequence = -1;
                                        if(emtype.equals("Health"))
                                        {
                                            if(String.valueOf(snaps.child("seekeruid").getValue()).equals(ma.userid)) {
                                                String dt = String.valueOf(snaps.child("timedate").getValue()).substring(4,16);
                                                String et = String.valueOf(snaps.child("emergencytype").getValue());
                                                String pn = String.valueOf(snaps.child("providername").getValue());

                                                historyContents.append("   "+dt + "               "+et+"                  "+pn+"\n\n");

                                            }
                                        }

                                    }
                                    //---

                                }
                                else
                                {
                                    Toast.makeText(AidSeekerHistory.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AidSeekerHistory.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



        });

    }
}