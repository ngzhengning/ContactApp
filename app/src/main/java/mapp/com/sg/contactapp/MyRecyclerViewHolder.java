package mapp.com.sg.contactapp;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView mName;

    //Don't use this ViewHolder i already combine it into 1 (ViewHolder + Adapter) = ContactAdapter
    public MyRecyclerViewHolder(View itemView)
    {
        super(itemView);

        mName = itemView.findViewById(R.id.nameTextView);

    }
}
