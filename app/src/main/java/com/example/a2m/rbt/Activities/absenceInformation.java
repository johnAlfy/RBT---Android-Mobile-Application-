package com.example.a2m.rbt.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a2m.rbt.Adapters.AbsenceCustomListViewAdapter;
import com.example.a2m.rbt.R;
import com.example.a2m.rbt.Utilities.sessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class absenceInformation extends AppCompatActivity {

    private ListView listView ;
    private AbsenceCustomListViewAdapter adapter ;
    Context context= absenceInformation.this;
    private sessionManager session=new sessionManager();
    private void absenceList(ArrayList<String>absenceform)
    {

        ArrayList<HashMap<String ,String>>AbsenceList=new ArrayList<>();
        for(int x=0 ;x<absenceform.size();x++)
        {
            HashMap <String,String>data=new HashMap<>();
            data.put("AbsenceText",absenceform.get(x));
            AbsenceList.add(data);
        }
        listView =findViewById(R.id.list);
        adapter =new AbsenceCustomListViewAdapter(getApplicationContext() ,AbsenceList);
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
        setContentView(R.layout.activity_absence_info);


        final ArrayList<String> absence=new ArrayList<>();

        if(session.getSession() !=null)
        {
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = "https://8ce70b90.ngrok.io/check_abs";
            queue.start();

            JsonArrayRequest jsObjRequest = null;
            try {
                jsObjRequest = new JsonArrayRequest(Request.Method.POST, url
                        ,new JSONArray().put(new JSONObject().put("email",session.getSession().email))
                        ,new Response.Listener<JSONArray>()
                        {
                            @Override
                            public void onResponse(JSONArray response)
                            {
                                try
                                {
                                    if(response.length()>0)
                                    {
                                        for (int i = 0; i < response.length(); i++)
                                        {

                                            JSONObject jsonObject = response.getJSONObject(i);
                                            absence.add(jsonObject.get("student_name") + " has been recorded as absent due to date : " + jsonObject.get("date_Time"));
                                        }
                                        absenceList(absence);
                                    }
                                    else
                                    {
                                        Toast.makeText(absenceInformation.this, "There isn't any absence days :) !", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(absenceInformation.this, "AN ERROR OCCURRED , PLEASE TRY AGAIN !", Toast.LENGTH_LONG).show();
                            }
                        } );
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            queue.add(jsObjRequest);

        }
        else
        {
            Intent i = new Intent(absenceInformation.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }





    }
}
