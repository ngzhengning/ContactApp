package mapp.com.sg.contactapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.NameList;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    //RecyclerView recyclerView;
    //ArrayList<Name> nameArrayList;
    //MyRecyclerViewAdapter adapter;

    //Initialization of the recyclerView + adapter to display the names
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private ArrayList<Name> nameList;
    private FirebaseFirestore db;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //nameArrayList = new ArrayList<>();
        //setUpRecyclerView();
        //getData();

        //This get data method is use to retrieve record from the firestore + display it at the recyclerview
        getData();
    }

    private void getData(){
        recyclerView = findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nameList = new ArrayList<>();
        adapter = new ContactAdapter(this, nameList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        db.collection("contacts").orderBy("FirstName", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {

                                Name p = d.toObject(Name.class);
                                p.setId(d.getId());
                                nameList.add(p);
                                Log.d(TAG,p.getId());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        Drawable add = menu.findItem(R.id.action_add).getIcon();
        add.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        //SEARCH FILTER
        final MenuItem itemMenu = menu.findItem(R.id.action_search);
        searchView = (SearchView) itemMenu.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               if(!searchView.isIconified())
               {
                   searchView.setIconified(true);
               }
               itemMenu.collapseActionView();
               return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Name> filtermodelist = filter(nameList,newText);
                adapter.setFilter(filtermodelist);
                return true;
            }
        });
        Drawable search = menu.findItem(R.id.action_search).getIcon();
        search.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);;
        return super.onCreateOptionsMenu(menu);
    }
    private List<Name>filter(List<Name> pl,String query)
    {
        query = query.toLowerCase();
        final List<Name> filterModeList = new ArrayList<>();
        for(Name tt:pl)
        {
            final String text = tt.getFirstName().toLowerCase();
            if(text.startsWith(query))
            {
                filterModeList.add(tt);
            }
        }
        return filterModeList;
    }

    //suppose to add the search here also but you can try it
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the + sign selection
        switch (item.getItemId()) {
            case R.id.action_add:
                openAddContacts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openAddContacts(){
        Intent intent = new Intent(this, AddContacts.class);
        MainActivity.this.finish();
        startActivity(intent);
    }


    /*private void getData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("contacts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //QueryDocumentSnapshot replaced.
                            for (DocumentSnapshot document : task.getResult()) {
                                Name names = new Name(document.getString("FirstName"),
                                        document.getString("LastName"));
                            nameArrayList.add(names);
                            }

                            adapter = new MyRecyclerViewAdapter(MainActivity.this,nameArrayList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }*/
}
