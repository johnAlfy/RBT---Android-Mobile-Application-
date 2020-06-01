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

public class STCustomListViewAdapter extends BaseAdapter {
    private Context cox ;
    private ArrayList<HashMap<String ,String>>DATA ;
    private static LayoutInflater inflater = null;
    public STCustomListViewAdapter(Context context , ArrayList<HashMap<String ,String>>d)
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
            view = inflater .inflate(R.layout.st_list_row,null);
            TextView studentName=view.findViewById(R.id.STname);
            TextView studentAddress=view.findViewById(R.id.STaddress);
            TextView studentGrade=view.findViewById(R.id.STgrade);
            TextView studentAge=view.findViewById(R.id.STage);
            ImageView studentImage =view.findViewById(R.id.STimage);
            HashMap<String,String>STdata=new HashMap<>() ;
            STdata=DATA.get(position);
            studentName .setText(STdata.get("studentName"));
            studentAddress .setText(STdata.get("studentAddress"));
            studentGrade.setText(STdata.get("studentGrade"));
            studentAge .setText(STdata.get("studentAge"));
            //studentImage.setImageDrawable();

        }

        return view;
    }
}
