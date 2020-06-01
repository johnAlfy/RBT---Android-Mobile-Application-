package com.example.a2m.rbt.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.a2m.rbt.Interfaces.IRecyclerItemClickListener;
import com.example.a2m.rbt.R;

public class userViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView live_Driver_email;
    IRecyclerItemClickListener iRecyclerItemClickListener;

    public void setiRecyclerItemClickListener(IRecyclerItemClickListener iRecyclerItemClickListener) {
        this.iRecyclerItemClickListener = iRecyclerItemClickListener;
    }

    public userViewHolder(@NonNull View itemView) {
        super(itemView);
        live_Driver_email=(TextView)itemView.findViewById(R.id.txt_Driver_email);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        iRecyclerItemClickListener.onItemClickListener(v,getAdapterPosition());
    }
}
