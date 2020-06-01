package com.example.a2m.rbt.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class sessionManager {
    public  SharedPreferences sharedPreference;
    @SuppressLint("RestrictedApi")
    public Context context ;
    @SuppressLint("RestrictedApi")
    public sessionManager() {
        context = getApplicationContext();
}

    public boolean startSession(user u )
    {
       sharedPreference=context.getSharedPreferences("currentUser",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.clear();
        editor.commit();
        editor.putString("id", u.id);
        editor.putString("firstName", u.firstName);
        editor.putString("lastName", u.lastName);
        editor.putString("password", u.password);
        editor.putString("email", u.email);
        editor.putString("contactNumber",u.contactNumber);
        editor.putString("address", u.address);
        editor.putString("dateOfBirth", u.dateOfBirth);
        editor.putString("Type_of_user", u.typeOfUser);
        editor.putString("nationalNumber", u.nationalNumber);
        editor.putString("driverUsername",u.driverUsername);
        editor.commit();
        return true;
    }

    public boolean endSession( )
    {
        sharedPreference=context.getSharedPreferences("currentUser",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.clear();
        editor.commit();
        return true;
    }

    public user getSession()
    {
        SharedPreferences prefs = context.getSharedPreferences("currentUser", MODE_PRIVATE);
        user user =new user(
                prefs.getString("id",null),
                prefs.getString("firstName",null),
                prefs.getString("lastName",null),
                prefs.getString("email",null),
                prefs.getString("password",null),
                prefs.getString("contactNumber",null),
                prefs.getString("address",null),
                prefs.getString("dateOfBirth",null),
                prefs.getString("Type_of_user",null),
                prefs.getString("nationalNumber",null),
                prefs.getString("driverUsername",null));
        if (prefs.getString("email",null)==null)
            return null;
        else
            return user;
    }

}
