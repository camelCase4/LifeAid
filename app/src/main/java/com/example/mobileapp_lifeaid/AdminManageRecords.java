package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminManageRecords extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    int role_holder = 0;
    public static String chosenRole = "";
    TextView conts,search,totalCount;

    ImageView menu;

    public static String user_id ="";
    public static String specifiedID = "";

    int choosing = 0;//5/1/2023
    String checkSpecific = "";//5/1/2023
    int countOfUsers = 0;//5/1/2023

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_records);

        conts = (TextView) findViewById(R.id.contents);
        search = (TextView) findViewById(R.id.srch);
        totalCount = (TextView) findViewById(R.id.countTotal);


        conts.setMovementMethod(new ScrollingMovementMethod());//3/29/2023

        //4/16/2023
        menu = (ImageView) findViewById(R.id.imageView18);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminManageRecords.this,MenuForAdmins.class);
                startActivity(intent);
            }
        });
        //---


        Spinner sp = findViewById(R.id.sp_drop);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.LifeAidUsers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);
        sp.setSelection(role_holder);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                specificSearch();
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String temp = adapterView.getItemAtPosition(i).toString();


        if(temp.equals("Aid-Provider"))
        {
            totalCount.setText("0 In Total");
            choosing = 0;//5/1/2023
            countOfUsers = 0;//5/1/2023
            chosenRole = "Aid-Provider";
            gettingUsers(chosenRole);
        }
        else if(temp.equals("Aid-Seeker"))
        {
            totalCount.setText("0 In Total");
            choosing = 0;//5/1/2023
            countOfUsers = 0;//5/1/2023
            chosenRole = "Aid-Seeker";
            gettingUsers(chosenRole);
        }
        else if(temp.equals("Nurses"))
        {
            totalCount.setText("0 In Total");
            choosing = 1;
            countOfUsers = 0;//5/1/2023
            chosenRole = "Aid-Provider";
            gettingUsers(chosenRole);
        }
        else if(temp.equals("Policemen"))
        {
            totalCount.setText("0 In Total");
            choosing = 2;
            countOfUsers = 0;//5/1/2023
            chosenRole = "Aid-Provider";
            gettingUsers(chosenRole);
        }
        else if(temp.equals("Firemen"))
        {
            totalCount.setText("0 In Total");
            choosing = 3;
            countOfUsers = 0;//5/1/2023
            chosenRole = "Aid-Provider";
            gettingUsers(chosenRole);
        }
        else if(temp.equals("Doctors"))
        {
            totalCount.setText("0 In Total");
            choosing = 4;
            countOfUsers = 0;//5/1/2023
            chosenRole = "Aid-Provider";
            gettingUsers(chosenRole);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void gettingUsers(String cr)
    {
        //5/1/2023
        if(choosing == 0)
        {
            checkSpecific = "fire police doctor nurse all";
        }
        else if(choosing == 1)
        {
            checkSpecific = "nurse";
        }
        else if(choosing == 2)
        {
            checkSpecific = "police";
        }
        else if(choosing == 3)
        {
            checkSpecific = "fire";
        }
        else if(choosing == 4)
        {
            checkSpecific = "doctor";
        }
        //----
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference(cr);
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                conts.setText("");
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
                                    /*String tempproviderUID = uid;
                                    String con_num = String.valueOf(snaps.child("phonenum").getValue());



                                    //String temp = con_num +"             "+tempproviderUID+"\n\n"; original
                                    //4/2/2023
                                    String temp = con_num +"             "+tempproviderUID;
                                    //---
                                    SpannableString ss = new SpannableString(temp);
                                    ClickableSpan clickableSpan = new ClickableSpan() {

                                        @Override
                                        public void onClick(View textView) {
                                            //3/29/2023

                                            Intent intent = new Intent(AdminManageRecords.this,AdminManageRecordsSecondPage.class);
                                            intent.putExtra("UserUid", tempproviderUID);
                                            startActivity(intent);

                                            //---
                                        }
                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            super.updateDrawState(ds);
                                            ds.setUnderlineText(false);
                                        }
                                    };
                                    ss.setSpan(clickableSpan, 23, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                                    conts.append(ss);
                                    conts.append("\n\n");
                                    conts.setMovementMethod(LinkMovementMethod.getInstance());
                                    conts.setHighlightColor(Color.TRANSPARENT);
                                    */

                                    //5/1/2023
                                   if(checkSpecific.contains(String.valueOf(snaps.child("job").getValue()).toLowerCase()))
                                    {
                                        countOfUsers++;
                                        totalCount.setText(countOfUsers+"  In Total");
                                        String tempproviderUID = uid;
                                        String con_num = String.valueOf(snaps.child("phonenum").getValue());



                                        //String temp = con_num +"             "+tempproviderUID+"\n\n"; original
                                        //4/2/2023
                                        String temp = con_num +"             "+tempproviderUID;
                                        //---
                                        SpannableString ss = new SpannableString(temp);
                                        ClickableSpan clickableSpan = new ClickableSpan() {

                                            @Override
                                            public void onClick(View textView) {
                                                //3/29/2023

                                                Intent intent = new Intent(AdminManageRecords.this,AdminManageRecordsSecondPage.class);
                                                intent.putExtra("UserUid", tempproviderUID);
                                                startActivity(intent);

                                                //---
                                            }
                                            @Override
                                            public void updateDrawState(TextPaint ds) {
                                                super.updateDrawState(ds);
                                                ds.setUnderlineText(false);
                                            }
                                        };
                                        ss.setSpan(clickableSpan, 23, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                                        conts.append(ss);
                                        conts.append("\n\n");
                                        conts.setMovementMethod(LinkMovementMethod.getInstance());
                                        conts.setHighlightColor(Color.TRANSPARENT);

                                   }
                                    //----

                                }
                                else
                                {
                                    Toast.makeText(AdminManageRecords.this, "Failed to read!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(AdminManageRecords.this, "Task was not successful!", Toast.LENGTH_SHORT).show();
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
    //3/28/2023
    public void specificSearch()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(AdminManageRecords.this);
        alert.setMessage("Enter a user UID");


        alert.setView(edittext);

        alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                specifiedID = edittext.getText().toString();
                //Intent intent = new Intent(AdminManageRecords.this,AdminManageRecordsSecondPage.class);
                //startActivity(intent);
                gettingSpecificProvider(specifiedID);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }
    //--

    //3/29/2023
    public void gettingSpecificProvider(String userid)
    {
        DatabaseReference drprov = FirebaseDatabase.getInstance().getReference("Aid-Provider");
        drprov.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if(datasnapshot.child(userid).exists()) {
                    drprov.child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    DataSnapshot snaps = task.getResult();
                                    chosenRole = "Aid-Provider";
                                    Intent intent = new Intent(AdminManageRecords.this, AdminManageRecordsSecondPage.class);
                                    intent.putExtra("UserUid", userid);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                }
                else
                {
                    gettingSpecifiedSeeker(userid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void gettingSpecifiedSeeker(String userid)
    {
        DatabaseReference drseek = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
        drseek.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if(datasnapshot.child(userid).exists()) {
                    drseek.child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    DataSnapshot snaps = task.getResult();
                                    chosenRole = "Aid-Seeker";
                                    Intent intent = new Intent(AdminManageRecords.this, AdminManageRecordsSecondPage.class);
                                    intent.putExtra("UserUid", userid);
                                    startActivity(intent);

                                }
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(AdminManageRecords.this, "UID input does not exist!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //--
}