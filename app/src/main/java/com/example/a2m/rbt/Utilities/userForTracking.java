package com.example.a2m.rbt.Utilities;

import java.util.HashMap;

public class userForTracking {
    private String uId ,Uemail;
    private HashMap <String,userForTracking> driversToBeTracked ;

    public userForTracking()
    {

    }

    public userForTracking(String uId, String uemail) {
        this.uId = uId;
        this.Uemail = uemail;
        this.driversToBeTracked=new HashMap<>();
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUemail() {
        return Uemail;
    }

    public void setUemail(String uemail) {
        this.Uemail = uemail;
    }

    public HashMap<String, userForTracking> getDriversToBeTracked() {
        return driversToBeTracked;
    }

    public void setDriversToBeTracked(HashMap<String, userForTracking> driversToBeTracked) {
        this.driversToBeTracked = driversToBeTracked;
    }
}
