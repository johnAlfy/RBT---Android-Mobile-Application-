package com.example.a2m.rbt.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a2m.rbt.Adapters.DriverCustomListViewAdapter;
import com.example.a2m.rbt.R;
import com.example.a2m.rbt.Utilities.sessionManager;
import com.example.a2m.rbt.Utilities.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class driverInformation extends AppCompatActivity {

    private ListView listView ;
    private DriverCustomListViewAdapter adapter ;
    Context context= driverInformation.this;
    private sessionManager session=new sessionManager();
    public void myDriveInfoList(ArrayList<String>name , ArrayList<String>busNumber , ArrayList<String>address , ArrayList<String>mobile)
    {
        ArrayList<HashMap<String ,String>>DriverList=new ArrayList<>();
        for(int x=0 ;x<name.size();x++)
        {
            HashMap <String,String>data=new HashMap<>();
            data.put("driverName",name.get(x));
            data.put("driverAddress",address.get(x));
            data.put("busNumber",busNumber.get(x));
            data.put("driverTelephone",mobile.get(x));
            DriverList.add(data);
        }
        listView =findViewById(R.id.list);
        adapter =new DriverCustomListViewAdapter(getApplicationContext() ,DriverList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int Myposition=position ;
                String ItemClickedID =listView.getItemAtPosition(Myposition).toString();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);
        final ArrayList<String> name=new ArrayList<>();
        final ArrayList<String>busNumber=new ArrayList<>();
        final ArrayList<String>address=new ArrayList<>();
        final ArrayList<String>mobile=new ArrayList<>();
        user user=session.getSession();
        String driverUsername=user.driverUsername;

        if(session.getSession() !=null)
        {
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = "https://8ce70b90.ngrok.io/driver_info";
            queue.start();

            JsonArrayRequest jsObjRequest = null;
            try {
                jsObjRequest = new JsonArrayRequest(Request.Method.POST, url,new JSONArray().put(new JSONObject().put("email",session.getSession().email)) ,
                        new Response.Listener<JSONArray>()
                        {
                            @Override
                            public void onResponse(JSONArray response)
                            {
                                try
                                {
                                    System.out.println(response.get(0).toString());
                                    for (int i=0 ; i<response.length();i++)
                                    {
                                        JSONObject jsonObject=response.getJSONObject(i);
                                        name.add("Name : "+jsonObject.get("firstName").toString()+" "+jsonObject.get("lastName").toString());
                                        busNumber.add("Bus Number : "+jsonObject.get("bus_numbers").toString());
                                        address.add("Address : "+jsonObject.get("address").toString());
                                        mobile.add("Mobile : "+jsonObject.get("contactNumber").toString());
                                    }
                                    myDriveInfoList(name,busNumber,address,mobile);

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
            startActivity(new Intent(driverInformation.this, MainActivity.class));
        }




    }
}
