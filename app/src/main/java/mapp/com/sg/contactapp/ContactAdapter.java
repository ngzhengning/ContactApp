package mapp.com.sg.contactapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.NameViewHolder> {

    private Context mctx;
    private List<Name> nameList;

    public ContactAdapter(Context mctx, List<Name> nameList)
    {
        this.mctx = mctx;
        this.nameList = nameList;
    }


    @NonNull
    @Override
    public ContactAdapter.NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NameViewHolder(
                LayoutInflater.from(mctx).inflate(R.layout.name_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.NameViewHolder holder, int position) {
        Name names = nameList.get(position);

        holder.mname.setText(String.format("%s ", names.getFirstName()));
        holder.mname.append(names.getLastName());
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    class NameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView mname;

            public NameViewHolder(View itemView)
            {
                super(itemView);
                mname = itemView.findViewById(R.id.nameTextView);

                itemView.setOnClickListener(this);
            }

        @Override
        public void onClick(View v) {
             // Toast.makeText(mctx,"HI",Toast.LENGTH_SHORT).show();
            Name name = nameList.get(getAdapterPosition());
            Intent intent = new Intent(mctx, UpdateContactActivity.class);
            intent.putExtra("contacts",name);
            mctx.startActivity(intent);
        }
    }

    public void setFilter(List<Name> listItem)
    {
        nameList = new ArrayList<>();
        nameList.addAll(listItem);
        notifyDataSetChanged();
    }





}
