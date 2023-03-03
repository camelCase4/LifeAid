package com.example.mobileapp_lifeaid;

public class ProviderHistory {
    public String timedate, seekername, aidORsupport, seeker_uid;

    public ProviderHistory(){}

    public ProviderHistory(String timedate, String seekername, String aidORsupport, String seeker_uid)
    {
        this.timedate = timedate;
        this.seekername = seekername;
        this.aidORsupport = aidORsupport;
        this.seeker_uid = seeker_uid;
    }
}
