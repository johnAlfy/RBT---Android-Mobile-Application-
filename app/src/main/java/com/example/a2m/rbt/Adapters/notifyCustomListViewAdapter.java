package com.example.a2m.rbt.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a2m.rbt.R;

import java.util.ArrayList;
import java.util.HashMap;

public class notifyCustomListViewAdapter extends BaseAdapter {



    private Context cox ;
    private ArrayList<HashMap<String ,String>> DATA ;
    private static LayoutInflater inflater = null;
    public notifyCustomListViewAdapter(Context context , ArrayList<HashMap<String ,String>>d)
    {
        cox=context ;
        DATA=d;
        inflater=(LayoutInflater)context.getSystemService(Context .LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return DATA.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView ;
        if(convertView==null)
        {
            view = inflater .inflate(R.layout.notify,null);
            TextView AbsenceText=view.findViewById(R.id.notificationText);
            TextView DateText=view.findViewById(R.id.notificationDate);
            HashMap<String,String>STdata=new HashMap<>() ;
            STdata=DATA.get(position);
            AbsenceText .setText(STdata.get("AbsenceText"));
            DateText.setText(STdata.get("DateText"));


        }

        return view;
    }


}
