package com.example.a2m.rbt.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a2m.rbt.R;
import com.example.a2m.rbt.Utilities.sessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class supervisorAttendance extends AppCompatActivity {

    private ListView listView ;
    private Button sendAtendance;
    private sessionManager session = null ;
    Context context = supervisorAttendance.this;
    private void attendanceView(final ArrayList<String> studentsList)
    {
        final ArrayList<String> copy =new ArrayList<>();
        for(int x=0;x<studentsList.size();x++)
        {
            copy.add(x,studentsList.get(x));
        }
        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<>(getApplicationContext() , android.R.layout.simple_list_item_1
                , android.R.id.text1,studentsList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int mypos =position ;
                int idOfItem = (int) listView.getItemIdAtPosition(mypos);
                if (!studentsList.get(idOfItem).contains("recorded as absent"))
                {
                    Toast.makeText(getApplicationContext(), studentsList.get(idOfItem), Toast.LENGTH_SHORT).show();
                    studentsList.set(idOfItem ,studentsList.get(idOfItem)+"          recorded as absent");
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1, studentsList);
                    listView.setAdapter(arrayAdapter);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),copy.get(idOfItem), Toast.LENGTH_SHORT).show();
                    studentsList.set(idOfItem ,copy.get(idOfItem));
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1, studentsList);
                    listView.setAdapter(arrayAdapter);

                }
                for (String s :studentsList)
                {
                    Log.d("",s);
                }
            }
        });
    }
    public void sendAttendance(View view , ArrayList<String> listOfStudents ,ArrayList<String>studentsIdsList) throws JSONException
    {
        JSONArray sentArray =new JSONArray();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date todaysDate =new Date();
        if(studentsIdsList.size()>0&& listOfStudents.size()>0)
        {

            for (int x = 0; x < listOfStudents.size(); x++)
            {
                JSONObject object = new JSONObject();

                if (listOfStudents.get(x).contains("recorded as absent"))
                {
                    object.put("id", studentsIdsList.get(x)).put("name", listOfStudents.get(x).split(" ")[0]).put("absent", true)
                            .put("date", formatter.format(todaysDate));
                }
                else
                {
                    object.put("id", studentsIdsList.get(x)).put("name", listOfStudents.get(x)).put("absent", false)
                            .put("date", formatter.format(todaysDate));
                }
                sentArray.put(object);
            }
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://8ce70b90.ngrok.io/take_attendance";
            queue.start();
            JsonArrayRequest jsObjRequest = null;
            jsObjRequest = new JsonArrayRequest(Request.Method.POST, url, sentArray,
                    new Response.Listener<JSONArray>()
                    {

                        @Override
                        public void onResponse(JSONArray response)
                        {
                            System.out.println(response.toString());
                            try
                            {
                                if (response.getJSONObject(0).get("msg").equals("success"))
                                {
                                    Toast.makeText(supervisorAttendance.this, "ABSENCE RECORDED SUCCESSFULLY ! ", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(context, "AN ERROR OCCURRED PLEASE TRY AGAIN ! ", Toast.LENGTH_LONG).show();
                        }
                    });
            queue.add(jsObjRequest);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_attendance);
        session=new sessionManager();
        listView=findViewById(R.id.list);
        sendAtendance=findViewById(R.id.sendAttendance);
        final ArrayList<String> studentsList =new ArrayList<>();
        final ArrayList<String> studentsIdsList =new ArrayList<>();
    //    studentsList.add("john");
    //    studentsList.add("ramez");
    //    studentsList.add("kero");
        if(session.getSession() !=null)
        {
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = "https://7b4b8dd0.ngrok.io/get_students_related";
            queue.start();

            JsonArrayRequest jsObjRequest = null;
            try
            {
                jsObjRequest = new JsonArrayRequest(Request.Method.POST, url,new JSONArray().put(new JSONObject().put("email",session.getSession().email)),
                    new Response.Listener<JSONArray>()
                    {
                        @Override
                        public void onResponse(JSONArray response)
                        {
                            Log.d("attendance heree",response.toString());
                            for(int x=0 ; x<response.length();x++)
                            {
                                try
                                {
                                    JSONObject object =response.getJSONObject(x);
                                    studentsList.add(object.get("name").toString());
                                    studentsIdsList.add(object.get("id").toString());
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                            attendanceView(studentsList);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Toast.makeText(context , "AN ERROR OCCURRED PLEASE TRY AGAIN ! ", Toast.LENGTH_LONG).show();
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
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }



        sendAtendance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    sendAttendance(v,studentsList,studentsIdsList);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });


    }
}
