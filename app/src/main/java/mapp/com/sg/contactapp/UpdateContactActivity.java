package mapp.com.sg.contactapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateContactActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fnameEditText;
    private EditText lnameEditText;
    private EditText contactEditText;
    private Name name;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Integer> addArray = new ArrayList<>();
    private ArrayList<Integer> sArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_contact);

        getSupportActionBar().setTitle("Update Contact");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Retrieve the SPECIFIC RECORD*******
        getData();
        name= (Name) getIntent().getSerializableExtra("contacts");

        fnameEditText = findViewById(R.id.EditText_fname);
        lnameEditText = findViewById(R.id.EditText_lname);
        contactEditText = findViewById(R.id.EditText_contact);

        fnameEditText.setText(name.getFirstName());
        lnameEditText.setText(name.getLastName());
        contactEditText.setText(String.valueOf(name.getContactNo()));
        findViewById(R.id.updatebtn).setOnClickListener(this);
    }

    //Once updateContact is called > validate the data > check for duplicate > update the data > get data back again
    private void updateContact(){
        if(fnameEditText.getText().toString().isEmpty() || fnameEditText.getText().toString().startsWith(" ") || lnameEditText.getText().toString().isEmpty() ||
                lnameEditText.getText().toString().startsWith(" ")  || contactEditText.getText().toString().isEmpty() || contactEditText.getText().toString().startsWith(" ")) {

            Toast.makeText(UpdateContactActivity.this, "No empty field allowed.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(addArray.contains(Integer.parseInt(contactEditText.getText().toString())))
            {
               Toast.makeText(UpdateContactActivity.this, "This number already exist.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                updateData();
                getData();
            }
        }
    }

    //UpdateData
    private void updateData() {
        //Suppose to be initialize at the top
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //name.getID() will retrieve the record ID so that it can update to that specific record in firestore
        db.collection("contacts").document(name.getId())
                .update(
                        "FirstName", fnameEditText.getText().toString(),
                        "LastName", lnameEditText.getText().toString(),
                        "ContactNo", Integer.parseInt(contactEditText.getText().toString())
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateContactActivity.this, "Your contact details have been updated", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //RETRIEVE A SPECIFIC RECORD FROM FIRESTORE
    private void getData(){
        //AGAIN TO BE INITIALIZE AT THE TOP
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //this will get the record from firestore
        db.collection("contacts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //QueryDocumentSnapshot replaced.
                            //this addArray is for validation i will retrieve all contactNo WHERE != current ID of the record you are editing
                            for (DocumentSnapshot document : task.getResult()) {
                                if (!(document.getId().equals(name.getId()))) {
                                    addArray.add(document.getLong("ContactNo").intValue());
                                    Log.d(TAG, document.getId() + " => " + document.getLong("ContactNo").intValue());
                                }
                                else {
                                    //Don't really need an ARRAYList to store 1 contact number record but i just copy and paste
                                    sArray.add(document.getLong("ContactNo").intValue());
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    //deleteContact function
    private void deleteContact(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("contacts").document(name.getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(UpdateContactActivity.this,"Contact have been successfully deleted",Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(UpdateContactActivity.this,MainActivity.class));
                        }
                    }
                });
    }

    //Very extra stuff but once you press the call button it will launch the phone activity with the number automatically key In
    private void callContact(){
        //Action_Dial doesn't require any permission(Manifest file). but i just add the permission for fun..
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + sArray.toString()));
        startActivity(intent);
    }

    //using switch case to give the button function
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.updatebtn:
                updateContact();
                break;
            case R.id.deletebtn:
                //Can use a method to do this but i just copy and paste
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure about this?");
                builder.setMessage("Delete Contact");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteContact();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
                break;
            case R.id.callbtn:
                callContact();
                break;

        }
    }

    //Android hardware back button function
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                //this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Intent i = new Intent(this,MainActivity.class);
        UpdateContactActivity.this.finish();
        startActivity(i);

    }
}
