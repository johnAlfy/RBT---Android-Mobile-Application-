package com.example.a2m.rbt.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.a2m.rbt.Adapters.ReportCusromListViewAdapter;
import com.example.a2m.rbt.Utilities.sessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class adminResponceOnParentsMsgs extends AppCompatActivity {

    private ListView listView ;
    private ReportCusromListViewAdapter adapter ;
    private Button Dbutton ;
    private sessionManager session=new sessionManager();
    Context context= adminResponceOnParentsMsgs.this;
    ArrayList<HashMap<String ,String>>reportList=new ArrayList<>();
    public/* ArrayList<HashMap<String ,String>>*/void resportsResponse(ArrayList<String> reportform)
    {

        for(int x=0 ;x<reportform.size();x++)
        {
            HashMap <String,String>data=new HashMap<>();
            data.put("ReportText",reportform.get(x));
            reportList.add(data);
        }
        listView =findViewById(R.id.list);
        adapter =new ReportCusromListViewAdapter(getApplicationContext() ,reportList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int Myposition=position ;
                String ItemClickedID =listView.getItemAtPosition(Myposition).toString();
            }
        });
       // return reportList;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);



        final ArrayList<String> reportform=new ArrayList<>();

        if(session.getSession() !=null)
        {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://8ce70b90.ngrok.io/review_answer";
            if(session.getSession().typeOfUser.toLowerCase().equals("supervisor"))
            {
                url = "https://8ce70b90.ngrok.io/review_answer_supervisor";
            }
            queue.start();

            JsonArrayRequest jsObjRequest = null;
            try
            {
                jsObjRequest = new JsonArrayRequest(Request.Method.POST, url
                , new JSONArray().put(new JSONObject().put("email",session.getSession().email))
                , new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {

                        if(response.length()!=0)
                        {
                            for (int i = 0; i < response.length(); i++)
                            {
                                try
                                {
                                    JSONObject obj = response.getJSONObject(i);
                                    reportform.add(obj.get("answer").toString());
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            resportsResponse(reportform);
                        }
                        else
                        {
                            Toast.makeText(adminResponceOnParentsMsgs.this, "There isnot any new msgs yet !", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                , new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                }
                );
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            queue.add(jsObjRequest);
        }
        else
        {
            Intent i = new Intent(adminResponceOnParentsMsgs.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }



        Dbutton=findViewById(R.id.delete);
        final RequestQueue queue = Volley.newRequestQueue(this);
        Dbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reportList.clear();
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

                String url = "https://8ce70b90.ngrok.io/delete_repo";
                if(session.getSession().typeOfUser.toLowerCase().equals("supervisor"))
                {
                    url = "https://8ce70b90.ngrok.io/delete_repo_supervisor";
                }
                queue.start();
                JsonArrayRequest jsObjRequest = null;
                try
                {
                    jsObjRequest = new JsonArrayRequest(Request.Method.POST, url
                    , new JSONArray().put(new JSONObject().put("email",session.getSession().email))
                    , new Response.Listener<JSONArray>()
                    {
                        @Override
                        public void onResponse(JSONArray response)
                        {

                            if(response.length()!=0)
                            {
                                Toast.makeText(adminResponceOnParentsMsgs.this, "Messages deleted successfully !", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Intent i = new Intent(adminResponceOnParentsMsgs.this,MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }
                    }
                            , new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {

                        }
                    }
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                queue.add(jsObjRequest);
            }
        });

    }
}
