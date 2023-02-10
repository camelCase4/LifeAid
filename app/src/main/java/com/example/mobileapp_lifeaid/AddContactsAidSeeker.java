package com.example.mobileapp_lifeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddContactsAidSeeker extends AppCompatActivity {

    ImageView iv,nxt;
    TextView tv;
    EditText et_usern,et_usernum;
    int counter = 0;
    //checkpoint
    MainActivity ma = new MainActivity();
    String firstname,firstnumber,secondname,secondnumber;
    //--
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts_aid_seeker);

        et_usern = (EditText) findViewById(R.id.et_name);
        et_usernum = (EditText) findViewById(R.id.et_phonenum);
        iv = (ImageView) findViewById(R.id.imageView33);
        nxt = (ImageView) findViewById(R.id.img_buttonnext);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(counter >= 2) {
                    Intent intent = new Intent(AddContactsAidSeeker.this, InstructionDashboardAidSeeker.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(AddContactsAidSeeker.this, "Please provide two!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv = (TextView) findViewById(R.id.tv_registration21);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddContactsAidSeeker.this,AidSeekerMainDash.class);
                startActivity(intent);
            }
        });

        //checkpoint
        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gosignal())
                {
                    /*
                    HashMap<String,String> hm = new HashMap<>();
                    hm.put("trustedname_1",)*/

                    counter++;
                    if(counter <= 2)
                    {
                        if(counter == 1) {
                            /*
                            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
                            dr.child(ma.userid).updateChildren()*/
                            firstname = et_usern.getText().toString();
                            firstnumber = et_usernum.getText().toString();

                            et_usern.setText("");
                            et_usernum.setText("");
                            Toast.makeText(AddContactsAidSeeker.this, "Added!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            HashMap hm = new HashMap();
                            hm.put("trustedname_1",firstname);
                            hm.put("trustedphonenum_1",firstnumber);
                            hm.put("trustedname_2",et_usern.getText().toString());
                            hm.put("trustedphonenum_2",et_usernum.getText().toString());
                            hm.put("prompt_trustedContacts",false);

                            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aid-Seeker");
                            dr.child(ma.userid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(AddContactsAidSeeker.this, "Recorded!", Toast.LENGTH_SHORT).show();

                                        et_usern.setText("");
                                        et_usernum.setText("");
                                    }
                                    else
                                    {
                                        Toast.makeText(AddContactsAidSeeker.this,"Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                    else
                    {
                        Toast.makeText(AddContactsAidSeeker.this, "Maximum limit already achieved", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(AddContactsAidSeeker.this, "Please enter some details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //--
    }

    public boolean gosignal()
    {
        return !et_usern.getText().toString().equals("")&&!et_usernum.getText().toString().equals("");
    }
}