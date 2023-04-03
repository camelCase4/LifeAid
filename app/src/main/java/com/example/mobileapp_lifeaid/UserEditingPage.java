package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

public class UserEditingPage extends AppCompatActivity {

    EditText address,fname,lname,age,gender,job,number,name1,name2,name1pn,name2pn;
    Button update;

    MainActivity ma = new MainActivity();

    String whatR = "";

    String[] valPartners = {"fname","lname","address","age","gender","phonenum","job","trustedname_1","trustedname_2","trustedphonenum_1","trustedphonenum_2"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_editing_page);

        address = (EditText) findViewById(R.id.et_address);
        fname = (EditText) findViewById(R.id.et_fname);
        lname = (EditText) findViewById(R.id.et_lname);
        age = (EditText) findViewById(R.id.et_age);
        gender = (EditText) findViewById(R.id.et_gender);
        job = (EditText) findViewById(R.id.et_job);
        number = (EditText) findViewById(R.id.et_pn);
        name1 = (EditText) findViewById(R.id.et_name1);
        name2 = (EditText) findViewById(R.id.et_name2);
        name1pn = (EditText) findViewById(R.id.et_name1num);
        name2pn = (EditText) findViewById(R.id.et_name2num);

        EditText[] vals = {fname,lname,address,age,gender,number,job,name1,name2,name1pn,name2pn};

        update = (Button) findViewById(R.id.delbutton);

        if(ma.userrole.equals("AidProvider"))
        {
            gettingData("Aid-Provider");
            whatR = "Aid-Provider";

        }
        else if(ma.userrole.equals("AidSeeker"))
        {
            gettingData("Aid-Seeker");
            whatR = "Aid-Seeker";

        }
        else
        {
            gettingData("Admin");
            whatR = "Admin";
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatingData(whatR,vals);
            }
        });
    }
    public void updatingData(String r, EditText[] vals)
    {
        HashMap hm = new HashMap();


        for(EditText i: vals)
        {
            if(i.getText().toString().length() != 0)
            {
                int x = Arrays.asList(vals).indexOf(i);
                hm.put(valPartners[x],i.getText().toString());
            }
        }


        try {


            DatabaseReference dr = FirebaseDatabase.getInstance().getReference(r);
            dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(UserEditingPage.this, "Updated!", Toast.LENGTH_SHORT).show();
                    if (r.equals("Aid-Provider")) {
                        Intent intent = new Intent(UserEditingPage.this, MenuButtonForProviders.class);
                        startActivity(intent);
                    } else if (r.equals("Aid-Seeker")) {
                        Intent intent = new Intent(UserEditingPage.this, MenuButtonForSeekers.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(UserEditingPage.this, MenuForAdmins.class);
                        startActivity(intent);
                    }

                }
            });
        }
        catch (Exception e)
        {

        }
    }

    public void gettingData(String role)
    {
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference(role);

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                dr.child(ma.userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                DataSnapshot snaps = task.getResult();

                                String etfname = String.valueOf(snaps.child("fname").getValue());
                                String etlname = String.valueOf(snaps.child("lname").getValue());
                                String etaddress = String.valueOf(snaps.child("address").getValue());
                                String etage = String.valueOf(snaps.child("age").getValue());
                                String etgender = String.valueOf(snaps.child("gender").getValue());
                                String etjob = String.valueOf(snaps.child("job").getValue());
                                String etphonenum = String.valueOf(snaps.child("phonenum").getValue());
                                String etname1 = String.valueOf(snaps.child("trustedname_1").getValue());
                                String etname2 = String.valueOf(snaps.child("trustedname_2").getValue());
                                String etnum1 = String.valueOf(snaps.child("trustedphonenum_1").getValue());
                                String etnum2 = String.valueOf(snaps.child("trustedphonenum_2").getValue());


                                address.setHint("Address: "+etaddress);
                                fname.setHint("Firstname: "+etfname);
                                lname.setHint("Lastname: "+etlname);
                                age.setHint("Age: "+etage);
                                gender.setHint("Gender: "+etgender);
                                job.setHint("Occupation: "+etjob);
                                number.setHint("Phone: "+etphonenum);
                                name1.setHint("Contact1: "+etname1);
                                name2.setHint("Contact2: "+etname2);
                                name1pn.setHint("ContactNum1: "+etnum1);
                                name2pn.setHint("ContactNum2: "+etnum2);

                                if(ma.userrole.equals("AidProvider"))
                                {
                                    name1.setHint("Not Applicable");
                                    name2.setHint("Not Applicable");
                                    name1pn.setHint("Not Applicable");
                                    name2pn.setHint("Not Applicable");

                                    name1.setEnabled(false);
                                    name2.setEnabled(false);
                                    name1pn.setEnabled(false);
                                    name2pn.setEnabled(false);
                                }
                                else if(ma.userrole.equals("AidSeeker"))
                                {
                                    job.setHint("Not Applicable");
                                    job.setEnabled(false);

                                }
                                else
                                {
                                    job.setHint("Not Applicable");
                                    name1.setHint("Not Applicable");
                                    name2.setHint("Not Applicable");
                                    name1pn.setHint("Not Applicable");
                                    name2pn.setHint("Not Applicable");
                                    job.setEnabled(false);
                                    name1.setEnabled(false);
                                    name2.setEnabled(false);
                                    name1pn.setEnabled(false);
                                    name2pn.setEnabled(false);
                                }
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
}