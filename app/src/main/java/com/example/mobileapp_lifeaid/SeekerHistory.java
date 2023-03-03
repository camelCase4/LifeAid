package com.example.mobileapp_lifeaid;

public class SeekerHistory {

    public String timedate, emergencytype, providername, providerUid;

    public SeekerHistory(){}

    public SeekerHistory(String timedate, String emergencytype, String providername, String providerUid)
    {
        this.timedate = timedate;
        this.emergencytype = emergencytype;
        this.providername = providername;
        this.providerUid = providerUid;
    }
}
