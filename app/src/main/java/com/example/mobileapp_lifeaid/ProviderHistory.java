package com.example.mobileapp_lifeaid;

public class ProviderHistory {
    public String timedate, seekername, aidORsupport, seeker_uid,provider_id,feedback,locationPlace;

    public ProviderHistory(){}

    public ProviderHistory(String timedate, String seekername, String aidORsupport, String seeker_uid,String provider_id,String feedback,String locationPlace)
    {
        this.timedate = timedate;
        this.seekername = seekername;
        this.aidORsupport = aidORsupport;
        this.seeker_uid = seeker_uid;
        this.provider_id = provider_id;
        //3/22/2023
        this.feedback = feedback;
        this.locationPlace = locationPlace;
        //-----

    }
}
