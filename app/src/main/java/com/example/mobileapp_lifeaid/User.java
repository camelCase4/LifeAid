package com.example.mobileapp_lifeaid;

public class User {

    public String email, username, password,role,fname,lname,age,phonenum,address,gender,trustedphonenum_1,trustedphonenum_2,trustedname_1,trustename_2;
    public boolean prompt_trustedContacts = true;//------------newly

    public User(){}

    public User(String email, String username, String password, String role,String fname, String lname, String age, String phonenum, String address, String gender, boolean prompt_trustedContacts,String trustedphonenum_1, String trustedphonenum_2,String trustedname_1,String trustename_2)
    {
        this.email = email;
        this.username = username;
        this.password = password;
        this.fname = fname;
        this.lname = lname;
        this.age = age;
        this.phonenum = phonenum;
        this.address = address;
        this.gender = gender;
        this.role = role;
        this.prompt_trustedContacts = prompt_trustedContacts;//----newly
        this.trustedphonenum_1 = trustedphonenum_1;
        this.trustedphonenum_2 = trustedphonenum_2;
        this.trustedname_1 = trustedname_1;
        this.trustename_2 = trustename_2;
        //--
    }
}
