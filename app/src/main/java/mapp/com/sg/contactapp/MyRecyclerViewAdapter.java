package mapp.com.sg.contactapp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;


import java.util.ArrayList;


public class MyRecyclerViewAdapter  extends RecyclerView.Adapter<MyRecyclerViewHolder>{

    //Don't use this adapter i already combine it into 1 (ViewHolder + Adapter) = ContactAdapter
    MainActivity mainActivity;
    ArrayList<Name> nameArrayList;

    public MyRecyclerViewAdapter(MainActivity mainActivity, ArrayList<Name> nameArrayList) {
        this.mainActivity = mainActivity;
        this.nameArrayList = nameArrayList;
    }

    @NonNull
    @Override
    public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mainActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.single_row,parent,false);

        return new MyRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewHolder holder, int position) {
        holder.mName.setText(nameArrayList.get(position).getFirstName());
        holder.mName.append(nameArrayList.get(position).getLastName());

    }

    @Override
    public int getItemCount() {
        return nameArrayList.size();
    }

}
