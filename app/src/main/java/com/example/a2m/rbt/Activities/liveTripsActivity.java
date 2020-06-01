package com.example.a2m.rbt.Activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.a2m.rbt.Interfaces.IFirebaseLoadDone;
import com.example.a2m.rbt.Interfaces.IRecyclerItemClickListener;
import com.example.a2m.rbt.R;
import com.example.a2m.rbt.Utilities.common;
import com.example.a2m.rbt.recievers.myLocationReceiver;
import com.example.a2m.rbt.Utilities.userForTracking;
import com.example.a2m.rbt.Adapters.userViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class liveTripsActivity extends AppCompatActivity implements IFirebaseLoadDone
{
    FirebaseRecyclerAdapter<userForTracking, userViewHolder>adapter ;
    RecyclerView recycler_Live_Trips;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    IFirebaseLoadDone firebaseLoadDone;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_trips);
        recycler_Live_Trips=(RecyclerView)findViewById(R.id.recycler_activeTrips);
        recycler_Live_Trips.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_Live_Trips.setLayoutManager(layoutManager);
        recycler_Live_Trips.addItemDecoration(new DividerItemDecoration(this,((LinearLayoutManager)layoutManager).getOrientation()));
        updateLocation();
        firebaseLoadDone=this;
        loadDriverLists();
        loadSearchData();
    }
    private void updateLocation()
    {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }
    private void buildLocationRequest()
    {
        locationRequest=new LocationRequest();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setFastestInterval(3000);
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private PendingIntent getPendingIntent()
    {
        Intent intent =new Intent(liveTripsActivity.this, myLocationReceiver.class);
        intent.setAction(myLocationReceiver.ACTION);
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void loadSearchData()
    {
        final List<String>listUseremail=new ArrayList<>();

        DatabaseReference usrList=FirebaseDatabase.getInstance()
                .getReference(common.USER_INFORMATION)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(common.ACCEPTANCE_LIST);

        usrList.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot userSnapShot : dataSnapshot.getChildren())
                {
                    userForTracking user =userSnapShot.getValue(userForTracking.class);
                    listUseremail.add(user.getUemail());
                }
                firebaseLoadDone.onFirebaseLoadLiveTripsDone(listUseremail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }

    private void loadDriverLists()
    {
        Query query= FirebaseDatabase.getInstance().getReference().child(common.USER_INFORMATION)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(common.ACCEPTANCE_LIST);
        FirebaseRecyclerOptions<userForTracking> options=new FirebaseRecyclerOptions.Builder<userForTracking>()
                .setQuery(query,userForTracking.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<userForTracking, userViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull userViewHolder holder, int position, @NonNull final userForTracking model)
            {
                    holder.live_Driver_email.setText(new StringBuilder(model.getUemail()));
                    holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener()
                    {
                        @Override
                        public void onItemClickListener(View view, int position)
                        {
                            //show tracking map
                            common.driverToBeTracked=model;
                            startActivity(new Intent(liveTripsActivity.this,googleMap.class));
                        }
                    });
            }
            @NonNull
            @Override
            public userViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.livedriverswithlivetrips,viewGroup,false);
                return new userViewHolder(itemView);
            }
        };
        adapter.startListening();
        recycler_Live_Trips.setAdapter(adapter);
    }

    @Override
    protected void onStop()
    {
        if(adapter!=null)
            adapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onFirebaseLoadLiveTripsDone(List<String> lstEmail)
    {

    }

    @Override
    public void onFirebaseLoadFailed(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
