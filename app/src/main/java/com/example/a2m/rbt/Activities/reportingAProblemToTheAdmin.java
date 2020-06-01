package com.example.a2m.rbt.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.a2m.rbt.Utilities.sessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class reportingAProblemToTheAdmin extends AppCompatActivity
{
    private EditText editText ;
    private Button button ;
    Context context= reportingAProblemToTheAdmin.this;
    private sessionManager session;
    public void submitReport(View v) throws JSONException
    {
        editText=findViewById(R.id.text1);
        final String report=editText.getText().toString().trim();
        Date d=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String s= formatter.format(d);
        if(report.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please write something before submitting the report !", Toast.LENGTH_LONG).show();
        }
        else
        {
            session = new sessionManager();
            final RequestQueue queue = Volley.newRequestQueue(this);

            String url = "https://8ce70b90.ngrok.io/add_report";
            if(session.getSession().typeOfUser.toLowerCase().equals("supervisor"))
            {
                url = "https://8ce70b90.ngrok.io/add_report_supervisor";
            }
            queue.start();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url
                    , new JSONObject().put("content", report).put("Date", s).put("email", session.getSession().email)
                    , new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try
                            {
                                if (response.get("msg").equals("success"))
                                    Toast.makeText(getApplicationContext(), "Report received successfully and we are working to solve it \n wait a notification by the solution !", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getApplicationContext(), "an error occurred please resend the report again ! ", Toast.LENGTH_LONG).show();
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    , new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Toast.makeText(getApplicationContext(), "an error occurred please resend the report again ! ", Toast.LENGTH_LONG).show();
                        }
                    });
            queue.add(jsObjRequest);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writingreport);
        button =findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    submitReport(v);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
