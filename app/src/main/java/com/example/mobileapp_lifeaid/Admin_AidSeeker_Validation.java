package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Admin_AidSeeker_Validation extends AppCompatActivity {

    Button b;


    int index = 0;
    String holder=""; //for the id to be splitted
    String[] ids;


    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Aid-Seeker");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_aid_seeker_validation);

        b = (Button) findViewById(R.id.validatebutton);

        String[] dummy = displayUID();// dummy ni kay for some reason it needs to be predefined ambot ngano, litse spent 7 hrs fixing this bug shesh

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] temp = displayUID();
                Toast.makeText(Admin_AidSeeker_Validation.this,Integer.toString(temp.length),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String[] displayUID()
    {

            dr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    for (DataSnapshot ds : datasnapshot.getChildren()) {
                        String key = ds.getKey();

                        dr.child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    if(task.getResult().exists())
                                    {
                                        DataSnapshot snaps = task.getResult();

                                        String temp = String.valueOf(snaps.child("admin_approved").getValue());
                                        if(temp.equals("false"))
                                        {
                                            holder += key+" ";

                                        }

                                    }
                                    else
                                    {
                                        Toast.makeText(Admin_AidSeeker_Validation.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(Admin_AidSeeker_Validation.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            return holder.trim().split(" ");

    }
}