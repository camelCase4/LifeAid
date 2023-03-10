package com.example.mobileapp_lifeaid;

public class SeekerHistory {

    public String timedate, emergencytype, providername, providerUid,seekeruid;

    public SeekerHistory(){}

    public SeekerHistory(String timedate, String emergencytype, String providername, String providerUid, String seekeruid)
    {
        this.timedate = timedate;
        this.emergencytype = emergencytype;
        this.providername = providername;
        this.providerUid = providerUid;
        this.seekeruid = seekeruid;
    }
}
