package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

    TextView historyContents;


    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("AidSeekerHistory");
    MainActivity ma = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_seeker_history);

        historyContents = (TextView) findViewById(R.id.contents);



        historyContents.setMovementMethod(new ScrollingMovementMethod());

        gettingData();
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
                                    if(String.valueOf(snaps.child("seekeruid").getValue()).equals(ma.userid)) {
                                        String dt = String.valueOf(snaps.child("timedate").getValue()).substring(4,16);
                                        String et = String.valueOf(snaps.child("emergencytype").getValue());
                                        String pn = String.valueOf(snaps.child("providername").getValue());

                                        historyContents.append("   "+dt + "                  "+et+"                  "+pn+"\n\n");

                                    }

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