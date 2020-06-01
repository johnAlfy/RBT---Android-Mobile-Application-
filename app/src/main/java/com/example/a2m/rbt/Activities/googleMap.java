package com.example.a2m.rbt.Activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a2m.rbt.R;
import com.example.a2m.rbt.Utilities.common;
import com.example.a2m.rbt.Utilities.myLocation;
import com.example.a2m.rbt.Utilities.sessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class googleMap extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    Context context= googleMap.this;
    private sessionManager session=new sessionManager();
    ArrayList<LatLng>points=new ArrayList<>();
    private void recievePickUpPoints()
    {
        if(session.getSession() !=null)
        {
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = "https://8ce70b90.ngrok.io/get_route_path";
            queue.start();

            JsonArrayRequest jsObjRequest = null;
            try {
                jsObjRequest = new JsonArrayRequest(Request.Method.POST, url,new JSONArray().put(new JSONObject().put("email",session.getSession().driverUsername)) ,
                        new Response.Listener<JSONArray>()
                        {
                            @Override
                            public void onResponse(JSONArray response)
                            {
                                Log.d("",response.toString());
                                try
                                {
                                    for (int x=0 ; x<response.length();x++)
                                    {
                                        JSONObject s=response.getJSONObject(x);
                                        LatLng l =new LatLng(s.getDouble("latitude"),s.getDouble("longitude"));
                                        points.add(l);
                                    }
                                    if(points.size()>0)
                                    {
                                        for (int i=0 ; i<points.size();i++)
                                        {
                                            mMap.addMarker(new MarkerOptions().position(points.get(i)).title("PICKUP POINT NUMBER "+String.valueOf(i+1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1)
                                            ).visible(true).draggable(true));
                                        }
                                    }

                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {

                            }
                        } );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            queue.add(jsObjRequest);

        }
        else
        {
            startActivity(new Intent(googleMap.this, MainActivity.class));
        }


    }

    DatabaseReference trackingUserLocation;
    private void showRealNotification(double des,String str)
    {
        Intent intent=new Intent();
        PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(),0,intent,0);
        Notification notification=new Notification.Builder(getApplicationContext())
                .setTicker("RBT")
                .setContentTitle("Your driver is almost arrived for Pick Up Point "+str)
                .setContentText(String.valueOf(des))
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent).getNotification();
        notification.flags=Notification.DEFAULT_SOUND;
        notification.flags=Notification.FLAG_AUTO_CANCEL;
        notification.flags=Notification.DEFAULT_VIBRATE;
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0,notification);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        registerEventRealtime();
    }

    private void registerEventRealtime()
    {
        trackingUserLocation= FirebaseDatabase.getInstance().getReference(common.PUBLIC_LOCATION)
                .child(common.driverToBeTracked.getuId());
        trackingUserLocation.addValueEventListener(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        recievePickUpPoints();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        trackingUserLocation.removeEventListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        trackingUserLocation.addValueEventListener(this);
    }


    Marker marker = null;
    Circle circleMarker = null;
    LatLng update=null;
    LatLng driverMarker=null;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
    {
        if(dataSnapshot.hasChildren())
        {
            if(marker!=null)
            {
                marker.remove();
            }
            if(circleMarker!=null)
            {
                circleMarker.remove();
            }
            update=driverMarker;
            myLocation location=dataSnapshot.getValue(myLocation.class);
             driverMarker=new LatLng(location.getLatitude(),location.getLongitude());
            marker=mMap.addMarker(new MarkerOptions().position(driverMarker).title(common.driverToBeTracked.getUemail())
                    .snippet(common.getDateFormatted(common.convertTimeStampToDate(location.getTime()))).visible(true).draggable(true));
            circleMarker=mMap.addCircle(new CircleOptions().center(driverMarker).radius(100.0).strokeWidth(3f).strokeColor(Color.RED)
                    .fillColor(Color.argb(70,150,50,50)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverMarker,16f));

            if(update==null)
            {
                update=driverMarker;
            }
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.add(driverMarker);lineOptions.add(update);
            lineOptions.width(8f);
            lineOptions.clickable(true);
            lineOptions.color(Color.BLUE);
            mMap.addPolyline(lineOptions);
            for(int x=0 ; x<points.size();x++)
            {
                Double destance = distance(driverMarker.latitude, driverMarker.longitude, points.get(x).latitude, points.get(x).longitude);

                if (destance < 100)
                {
                    showRealNotification(destance,String.valueOf(x+1));
                }
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError)
    {

    }
    public double distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Double(distance * meterConversion).floatValue();
    }
}
