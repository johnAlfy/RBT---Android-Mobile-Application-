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
import com.example.a2m.rbt.R;
import com.example.a2m.rbt.Adapters.STCustomListViewAdapter;
import com.example.a2m.rbt.Utilities.sessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class childrenInformation extends AppCompatActivity
{
    private ListView listView ;
    private STCustomListViewAdapter adapter ;
    Context context= childrenInformation.this;
    private sessionManager session=new sessionManager();

    public void myChildrenInfoList(ArrayList<String>name , ArrayList<String>age , ArrayList<String>address , ArrayList<String>grade)
    {
        ArrayList<HashMap<String ,String>>StudentList=new ArrayList<>();
        for(int x=0 ;x<name.size();x++)
        {
            HashMap <String,String>data=new HashMap<>();
            data.put("studentName",name.get(x));
            data.put("studentAddress",address.get(x));
            data.put("studentAge",age.get(x));
            data.put("studentGrade",grade.get(x));
            StudentList.add(data);
        }
        listView =findViewById(R.id.list);
        adapter =new STCustomListViewAdapter(getApplicationContext() ,StudentList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int Myposition=position ;
                String ItemClickedID =listView.getItemAtPosition(Myposition).toString();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_childern_info);
        final ArrayList<String> studentsNames=new ArrayList<>();
        final ArrayList<String> studentsAges=new ArrayList<>();
        final ArrayList<String> studentsAddresses=new ArrayList<>();
        final ArrayList<String> studentsGrades=new ArrayList<>();

        if(session.getSession() !=null)
        {
            RequestQueue queue = Volley.newRequestQueue(this);
             String url = "https://8ce70b90.ngrok.io/child_info";
            if(session.getSession().typeOfUser.toLowerCase().equals("supervisor"))
            {

                url = "https://8ce70b90.ngrok.io/get_students_related";
            }
            queue.start();

            JsonArrayRequest jsObjRequest = null;
            try
            {
                jsObjRequest = new JsonArrayRequest(Request.Method.POST, url,new JSONArray().put(new JSONObject()
                        .put("email",session.getSession().email)) ,
                        new Response.Listener<JSONArray>()
                        {
                            @Override
                            public void onResponse(JSONArray response)
                            {
                                try
                                {
                                    System.out.println(response.toString());
                                    for (int i=0 ; i<response.length();i++)
                                    {
                                        JSONObject jsonObject=response.getJSONObject(i);
                                        studentsNames.add(jsonObject.get("name").toString());
                                        studentsAges.add("Age : "+jsonObject.get("age").toString());
                                        studentsAddresses.add("Address : "+jsonObject.get("address").toString());
                                        studentsGrades.add("Grade : "+jsonObject.get("level").toString());
                                    }
                                    myChildrenInfoList(studentsNames,studentsAges,studentsAddresses,studentsGrades);

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
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            queue.add(jsObjRequest);
        }
        else
        {
            Intent i = new Intent(childrenInformation.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }
}
