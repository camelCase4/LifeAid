package com.example.mobileapp_lifeaid;

public class AdminAndProviderAid {
    public String whatRole,lati,longi,message,partner_uid,job,id;

    public AdminAndProviderAid(){}

    public AdminAndProviderAid(String whatRole, String lati, String longi, String message, String partner_uid, String job,String id)
    {
        this.whatRole = whatRole;
        this.lati = lati;
        this.longi = longi;
        this.message = message;
        this.partner_uid = partner_uid;
        this.job = job;
        this.id = id;
    }

}
