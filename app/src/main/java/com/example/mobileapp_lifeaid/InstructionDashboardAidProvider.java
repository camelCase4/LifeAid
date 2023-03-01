package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class InstructionDashboardAidProvider extends AppCompatActivity {


    MainActivity ma = new MainActivity();
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_dashboard_aid_provider);

        Toast.makeText(InstructionDashboardAidProvider.this, "Read well, this page will only prompt once.",Toast.LENGTH_SHORT).show();

        imageView = (ImageView) findViewById(R.id.img_next);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InstructionDashboardAidProvider.this,AidProviderMainDash.class);
                startActivity(intent);
            }
        });

        //checkpoint 3/1/2023
        HashMap hm = new HashMap();
        hm.put("prompt_trustedContacts",false);


        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {

                }

            }
        });
        //----
    }
}