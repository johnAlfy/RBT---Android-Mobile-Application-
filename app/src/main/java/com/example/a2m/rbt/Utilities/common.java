package com.example.a2m.rbt.Utilities;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.sql.Timestamp;
import java.util.Date;

public class common
{
    public static final String USER_INFORMATION="userInformation";
    public static final String USER_UID_SAVE_KEY ="saveUId" ;
    public static final String TOKENS ="tokens" ;
    public static final String ACCEPTANCE_LIST = "userDriver" ;
    public static final String PUBLIC_LOCATION = "publicLocation";
    public static userForTracking driverToBeTracked;

    public static Date convertTimeStampToDate(long time)
    {
        return new Date(new Timestamp(time).getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDateFormatted(Date date)
    {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date);
    }
}
