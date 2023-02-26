package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Admin_AidProvider_Validation extends AppCompatActivity {

    Button b,b2,b3;
    TextView tv_fullname,tv_email,tv_address,tv_contact,tv_gender,tv_age,tv_queue;
    ImageView iv;



    int index = 0;
    String holder=""; //for the id to be splitted
    String[] ids;
    String[] temp; // the holder for all the keys

    boolean checker = false;
    //checkpoint
    List<String> fullnames = new ArrayList<>();
    List<String> emails = new ArrayList<>();
    List<String> addresses = new ArrayList<>();
    List<String> contacts = new ArrayList<>();
    List<String> genders = new ArrayList<>();
    List<String> ages = new ArrayList<>();
    List<String> ID_proofs = new ArrayList<>();//new ------
    //--------


    FirebaseDatabase fd = FirebaseDatabase.getInstance();
    DatabaseReference dr = fd.getReference().child("Aid-Provider");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_aid_provider_validation);
        b = (Button) findViewById(R.id.validatebutton);
        b3 = (Button) findViewById(R.id.validatefetch);
        b2 = (Button) findViewById(R.id.denybutton);
        iv = (ImageView) findViewById(R.id.imageView35);//new ----

        tv_fullname = (TextView) findViewById(R.id.tv_asName);
        tv_email = (TextView) findViewById(R.id.tv_asEmail);
        tv_address = (TextView) findViewById(R.id.tv_asAddress);
        tv_contact = (TextView) findViewById(R.id.tv_asNum);
        tv_gender = (TextView) findViewById(R.id.tv_asGender);
        tv_age = (TextView) findViewById(R.id.tv_asAge);
        tv_queue = (TextView) findViewById(R.id.tv_nameDisplay);




        //
        String[] dummy = displayUID();// dummy ni kay for some reason it needs to be predefined ambot ngano, litse spent 7 hrs fixing this bug shesh
        fullnames.clear();
        emails.clear();
        addresses.clear();
        contacts.clear();
        genders.clear();
        ages.clear();
        ID_proofs.clear();
        //





        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = displayUID();


                if(fullnames.size() != 0) {
                    tv_queue.setText(displayText(fullnames.toArray(new String[0])));
                    b3.setEnabled(false);
                    tv_fullname.setText(fullnames.get(index));
                    tv_email.setText(emails.get(index));
                    tv_address.setText(addresses.get(index));
                    tv_contact.setText(contacts.get(index));
                    tv_gender.setText(genders.get(index));
                    tv_age.setText(ages.get(index));
                    imageDisplayer();//new ---
                    checker = true;
                }
                else
                {
                    Toast.makeText(Admin_AidProvider_Validation.this,"No Applicants!",Toast.LENGTH_SHORT).show();
                    b3.setEnabled(false);

                }
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checker) {
                    HashMap hm = new HashMap();
                    hm.put("admin_approved", true);

                    dr.child(temp[index]).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Admin_AidProvider_Validation.this, "Verified!", Toast.LENGTH_SHORT).show();
                                if (index != temp.length-1) {
                                    index++;

                                    tv_fullname.setText(fullnames.get(index));
                                    tv_email.setText(emails.get(index));
                                    tv_address.setText(addresses.get(index));
                                    tv_contact.setText(contacts.get(index));
                                    tv_gender.setText(genders.get(index));
                                    tv_age.setText(ages.get(index));
                                    imageDisplayer();
                                } else {
                                    Toast.makeText(Admin_AidProvider_Validation.this, "No more Applicants!", Toast.LENGTH_SHORT).show();
                                    /*
                                    b.setEnabled(false);
                                    b2.setBackgroundTintList(null);
                                    b2.setEnabled(false);*/
                                    checker = false;
                                    tv_fullname.setText("------------------------");
                                    tv_email.setText("------------------------");
                                    tv_address.setText("------------------------");
                                    tv_contact.setText("------------------------");
                                    tv_gender.setText("------------------------");
                                    tv_age.setText("------------------------");
                                    tv_queue.setText("-----------");
                                    iv.setImageResource(R.drawable.mtfolder);
                                }


                            } else {
                                Toast.makeText(Admin_AidProvider_Validation.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(Admin_AidProvider_Validation.this,"No Applicants To Verifiy!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker) {
                    dr.child(temp[index]).setValue(null);//---deleting
                    Toast.makeText(Admin_AidProvider_Validation.this, "Denied!", Toast.LENGTH_SHORT).show();
                    if (index != temp.length - 1) {
                        index++;

                        tv_fullname.setText(fullnames.get(index));
                        tv_email.setText(emails.get(index));
                        tv_address.setText(addresses.get(index));
                        tv_contact.setText(contacts.get(index));
                        tv_gender.setText(genders.get(index));
                        tv_age.setText(ages.get(index));
                        imageDisplayer();
                    } else {
                        Toast.makeText(Admin_AidProvider_Validation.this, "No more Applicants!", Toast.LENGTH_SHORT).show();
                        /*b.setEnabled(false);
                        b2.setBackgroundTintList(null);
                        b2.setEnabled(false);*/
                        tv_fullname.setText("------------------------");
                        tv_email.setText("------------------------");
                        tv_address.setText("------------------------");
                        tv_contact.setText("------------------------");
                        tv_gender.setText("------------------------");
                        tv_age.setText("------------------------");
                        tv_queue.setText("-----------");
                        checker = false;
                        iv.setImageResource(R.drawable.mtfolder);
                    }
                }
                else
                {
                    Toast.makeText(Admin_AidProvider_Validation.this,"No Applicants To Deny!",Toast.LENGTH_SHORT).show();
                }

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

                                    String temp_n = String.valueOf(snaps.child("admin_approved").getValue());
                                    String temp_fname = String.valueOf(snaps.child("fname").getValue());
                                    String temp_lname = String.valueOf(snaps.child("lname").getValue());
                                    String temp_email = String.valueOf(snaps.child("email").getValue());
                                    String temp_address = String.valueOf(snaps.child("address").getValue());
                                    String temp_contact = String.valueOf(snaps.child("phonenum").getValue());
                                    String temp_gender = String.valueOf(snaps.child("gender").getValue());
                                    String temp_age = String.valueOf(snaps.child("age").getValue());
                                    String temp_ID = String.valueOf(snaps.child("imageURL").getValue());//new ---

                                    if(temp_n.equals("false"))
                                    {
                                        holder += key+" ";
                                        fullnames.add(temp_fname+" "+temp_lname);
                                        emails.add(temp_email);
                                        addresses.add(temp_address);
                                        contacts.add(temp_contact);
                                        genders.add(temp_gender);
                                        ages.add(temp_age);
                                        ID_proofs.add(temp_ID);

                                    }

                                }
                                else
                                {
                                    Toast.makeText(Admin_AidProvider_Validation.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Admin_AidProvider_Validation.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
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
    public String displayText(String[] names)
    {
        String show = "";
        int remaining = 0;
        for(int i = 1; i < names.length; i++)
        {
            if(i <= 5) {
                show += Integer.toString(i) + ". " + names[i].split(" ")[0] + "\n";
            }
            else
            {
                remaining++;
            }

        }


        return remaining == 0? show:show +"And "+Integer.toString(remaining)+" more....";
    }

    //checkpoint 2/17/2023
    public void imageDisplayer()
    {
        Picasso.get().load(ID_proofs.get(index)).into(iv);
    }
    //---------
}