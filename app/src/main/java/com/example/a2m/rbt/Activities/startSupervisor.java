package com.example.a2m.rbt.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a2m.rbt.R;
import com.example.a2m.rbt.Adapters.notifyCustomListViewAdapter;
import com.example.a2m.rbt.Utilities.sessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class startSupervisor extends AppCompatActivity {
    private ListView listView ;
    private notifyCustomListViewAdapter adapter ;
    private Button backMainMenue ;
    private Button menu;
    private LinearLayout linearLayout ;
    private LinearLayout linearLayout2 ;
    private Button takeAttendance ;
    private Button showStudentsInformation ;
    private Button sendReport ;
    private ImageView message ;
    private TextView newText ;
    private Button logout;

    private sessionManager session=new sessionManager();
    Context context= startSupervisor.this;

    public void notificationList(ArrayList<String>Notificationform,ArrayList<String>dates)
    {

        ArrayList<HashMap<String ,String>>NotifictaionList=new ArrayList<>();
        for(int x=0 ;x<Notificationform.size();x++)
        {
            HashMap <String,String>data=new HashMap<>();
            data.put("AbsenceText",Notificationform.get(x));
            data.put("DateText",dates.get(x));
            NotifictaionList.add(data);
        }
        listView =findViewById(R.id.list);
        adapter =new notifyCustomListViewAdapter(getApplicationContext() ,NotifictaionList);
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
        setContentView(R.layout.activity_start_supervisor);
        newText =findViewById(R.id.newText);

        ///////////////////////////////////////////////////////////

        final ArrayList<String> notificationForm=new ArrayList<>();
        final ArrayList<String> datesForm=new ArrayList<>();

        if(session.getSession() !=null)
        {
            RequestQueue queue = Volley.newRequestQueue(this);
            final String url = "https://8ce70b90.ngrok.io/show_notification_supervisor";
            queue.start();

            JsonArrayRequest jsObjRequest = null;
            try
            {
                jsObjRequest = new JsonArrayRequest(Request.Method.POST, url,new JSONArray().put(new JSONObject().put("email",session.getSession().email)),
                        new Response.Listener<JSONArray>()
                        {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onResponse(JSONArray response)
                            {
                                try
                                {
                                    Log.d("",response.toString());
                                    JSONObject reportsAnswers=response.getJSONObject(response.length()-1);
                                    response.remove(response.length()-1);
                                    if(reportsAnswers.get("reportsAnswers").equals(true))
                                    {
                                        message =(ImageView) findViewById(R.id.messageButton);
                                        message.setColorFilter(Color.RED);
                                        newText.setText("new msgs");
                                    }
                                    for (int i=0 ; i<response.length();i++)
                                    {
                                        JSONObject jsonObject=response.getJSONObject(i);
                                        notificationForm.add(jsonObject.get("content").toString());
                                        datesForm.add(jsonObject.get("dateTime").toString());
                                    }
                                    notificationList(notificationForm,datesForm);
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
                                Toast.makeText(getApplicationContext(), "An Error Occurred Please Try Again ! ", Toast.LENGTH_LONG).show();
                            }
                        } );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            queue.add(jsObjRequest);
        }
        else
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }




        ///////////////////////////////////////////////////////////
        message =(ImageView) findViewById(R.id.messageButton);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), adminResponceOnParentsMsgs.class));
            }
        });
        ///////////////////////////////////////////////////////////

        backMainMenue=findViewById(R.id.backButton);
        backMainMenue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext() , startSupervisor.class));

            }
        });

        /////////////////////////////////////////////////////////

        menu =findViewById(R.id.menuButton);
        linearLayout=findViewById(R.id.mainContent);
        linearLayout2=findViewById(R.id.mainMenu);
        menu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                linearLayout.animate().translationX(780);
                linearLayout2.animate().translationX(0);
            }
        });

        /////////////////////////////////////////////////////////////////

        takeAttendance =findViewById(R.id.takeAttendance);
        takeAttendance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext() , supervisorAttendance.class));
            }
        });

        /////////////////////////////////////////////////////////////////

        showStudentsInformation =findViewById(R.id.showStudentsInformation);
        showStudentsInformation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext() , childrenInformation.class));
            }
        });

        /////////////////////////////////////////////////////////////////////////////////

        sendReport =findViewById(R.id.sendReport);
        sendReport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext() , reportingAProblemToTheAdmin.class));
            }
        });

        //////////////////////////////////////////////////////////////////////////////////

        logout=findViewById(R.id.LogOut);
        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (session.endSession())
                {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        });

    }
}
