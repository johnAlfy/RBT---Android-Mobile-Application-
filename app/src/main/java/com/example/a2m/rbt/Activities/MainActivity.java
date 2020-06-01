package com.example.a2m.rbt.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.a2m.rbt.R;
import com.example.a2m.rbt.Utilities.common;
import com.example.a2m.rbt.Utilities.sessionManager;
import com.example.a2m.rbt.Utilities.user;
import com.example.a2m.rbt.Utilities.userForTracking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
{
    sessionManager session;
    Context context = MainActivity.this;
    private Button login;
    DatabaseReference user_information;
    private FirebaseAuth mAuth;
    com.example.a2m.rbt.Utilities.user user = null;
    EditText email = null;
    EditText passWord = null;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    //"(?=\\S+$)" +           //no white spaces
                    //".{8,}" +               //at least 8 characters
                    "$");

    private boolean validateEmail(String EMAIL)
    {
        String emailInput = EMAIL.trim();
        if (emailInput.isEmpty())
        {
            Toast.makeText(MainActivity.this, "Email field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
        {
            Toast.makeText(MainActivity.this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            return true; }
    }

    private boolean validatePassword(String pass)
    {
        String passwordInput = pass.trim();

        if (passwordInput.isEmpty())
        {
            Toast.makeText(MainActivity.this, "Password field can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }
      /*  else if (!PASSWORD_PATTERN.matcher(passwordInput).matches())
        {
            Toast.makeText(MainActivity.this, "Password too weak", Toast.LENGTH_LONG).show();
            return false;
        }*/
        else
        {
            return true;
        }
    }

    private void buLoginClick(View v) throws JSONException
    {
        email = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.pass);
        if (!validateEmail(email.getText().toString()))
        {

        }
        else if (!validatePassword(passWord.getText().toString()))
        {

        }
        else
        {

            final RequestQueue queue = Volley.newRequestQueue(this);
            final String url = "https://8ce70b90.ngrok.io/log_in";
            queue.start();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                    new JSONObject().put("email", email.getText()).put("password", passWord.getText()),
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try
                            {
                                Log.d("", response.toString());
                                if (!response.has("error"))
                                {
                                    user = new user
                                    (
                                        response.get("id").toString(),
                                        response.get("firstName").toString(),
                                        response.get("lastName").toString(),
                                        response.get("email").toString(),
                                        response.get("password").toString(),
                                        response.get("contactNumber").toString(),
                                        response.get("address").toString(),
                                        response.get("dateOfBirth").toString(),
                                        response.get("Type_of_user").toString(),
                                        response.get("nationalNumber").toString(),
                                        response.get("username").toString()
                                    );



                                    if (user.typeOfUser.toLowerCase().equals("parent") || user.typeOfUser.toLowerCase().equals("driver"))
                                    {
                                        System.out.println("da5aaaal");
                                        fireBaseSignUp(user.email, user.password);
                                        fireBaseLogin(user.email, user.password,user);

                                    }
                                    else if (user.typeOfUser.toLowerCase().equals("supervisor"))
                                    {
                                        session = new sessionManager();
                                        if (session.startSession(user))
                                        {

                                            Intent i = new Intent(MainActivity.this, startSupervisor.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);

                                        }
                                    }

                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Email or password is not valid", Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "AN ERROR OCCURRED PLEASE CHECK EMAIL AND PASSWORD AND TRY TO LOG IN AGAIN !", Toast.LENGTH_LONG).show();
                        }
                    });
            queue.add(jsObjRequest);

        }
    }

    private void fireBaseSignUp(final String email, final String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {

                            System.out.println("siggned up successfully !");
                        }
                        else
                        {
                            System.out.println("siggned up failed !");
                        }

                        // ...
                    }
                });
    }

    private void fireBaseLogin(String email, String password , final user u)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            System.out.println("siggned in successfully !");
                            if(checkUserAtDataBase())
                            {
                                if(u.driverUsername!=null)
                                {
                                    addDriverToUser(u);
                                }
                                if (u.typeOfUser.toLowerCase().equals("parent"))
                                {
                                    session = new sessionManager();
                                    if (session.startSession(u))
                                    {
                                        Intent i = new Intent(MainActivity.this, parentsNotifications.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                }
                                else if (u.typeOfUser.toLowerCase().equals("driver"))
                                {
                                    session = new sessionManager();
                                    if (session.startSession(u))
                                    {
                                        Intent i = new Intent(MainActivity.this, liveTripsActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                }
                            }
                        }
                        else
                        {
                            System.out.println("siggned in failed !");
                        }
                    }
                });
    }

    private void requestLocationPermission()
    {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener()
        {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response)
            {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response)
            {
                Toast.makeText(MainActivity.this, "YOU MUST ACCEPT ACCESS LOACTION PERMISSION FIRST TO USE APP", Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
            {

            }
        }).check();

    }

    public boolean checkUserAtDataBase()
    {
        user_information = FirebaseDatabase.getInstance().getReference(common.USER_INFORMATION);
        System.out.println("da5al checkUserAtDataBase");
        user_information.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener()
        {
            userForTracking u;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.hasChildren()) //If user isnot exist
                {
                    System.out.println("dadada");
                   u= new userForTracking(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    user_information.child(u.getuId()).setValue(u);
                    System.out.println(u.getUemail()+" "+u.getuId());
                }
                else
                {
                    System.out.println("da5al Base");
                    u= dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(userForTracking.class);
                }
                Paper.book().write(common.USER_UID_SAVE_KEY, u.getuId());
                updateToken(FirebaseAuth.getInstance().getCurrentUser());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
        return true;
    }

    private void addDriverToUser(final user u)
    {
        final DatabaseReference acceptanceList = FirebaseDatabase.getInstance()
                .getReference(common.USER_INFORMATION)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(common.ACCEPTANCE_LIST);

        if (u.typeOfUser.toLowerCase().equals("driver"))
        {
            final userForTracking user = new userForTracking(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    FirebaseAuth.getInstance().getCurrentUser().getEmail());

            acceptanceList.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.getValue() == null)
                    {
                        acceptanceList.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }
        if (u.typeOfUser.toLowerCase().equals("parent"))
        {
            final DatabaseReference listOfDrivers = FirebaseDatabase.getInstance()
                    .getReference(common.USER_INFORMATION);
            Query query = listOfDrivers.orderByChild("uemail").equalTo(u.driverUsername);
            query.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                        userForTracking driver = new userForTracking();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                        {
                            driver = userSnapshot.getValue(userForTracking.class);
                        }
                        if(dataSnapshot.hasChildren())
                        {
                            acceptanceList.child(driver.getuId()).setValue(driver);
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
    }

    private void updateToken(final FirebaseUser user)
    {
        final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(common.TOKENS);
        //get token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>()
        {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult)
            {
                tokens.child(user.getUid()).setValue(instanceIdResult.getToken());
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        requestLocationPermission();
        Paper.init(this);
        user_information= FirebaseDatabase.getInstance().getReference(common.USER_INFORMATION);
      //  user u= new user("1","remon","sdsd","sa2a@gmail.com", "ssss","ss","ss", "","driver","s","sa2a@gmail.com");
       // fireBaseLogin("sa2a@gmail.com","12345678",u);
        login =findViewById(R.id.log);
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                try
                {
                    buLoginClick(v);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
          //  startActivity(new Intent(MainActivity.this,liveTripsActivity.class));
            }
        });
    }
}


