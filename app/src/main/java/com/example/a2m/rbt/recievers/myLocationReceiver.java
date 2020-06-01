package com.example.a2m.rbt.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.example.a2m.rbt.Utilities.common;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.paperdb.Paper;

public class myLocationReceiver extends BroadcastReceiver {
    public static final String ACTION ="UPDATE_LOCATION";
    DatabaseReference publicLocation;
    String uid;

    public myLocationReceiver()
    {
        publicLocation= FirebaseDatabase.getInstance().getReference(common.PUBLIC_LOCATION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Paper.init(context);
        uid=Paper.book().read(common.USER_UID_SAVE_KEY);
        if(intent!=null)
        {
            final  String action=intent.getAction();
            if(action.equals(ACTION))
            {
                LocationResult result = LocationResult.extractResult(intent);
                if(result!=null)
                {
                    Location location=result.getLastLocation();
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                    {
                        publicLocation.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(location);
                    }
                    else
                    {
                        publicLocation.child(uid).setValue(location);
                    }
                }
            }
        }

    }
}
