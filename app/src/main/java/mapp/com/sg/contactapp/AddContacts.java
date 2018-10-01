package mapp.com.sg.contactapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class AddContacts extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText firstname;
    private EditText lastname;
    private EditText contactno;
    private Button  savebtn;
    private ArrayList<Integer> addArray = new ArrayList<>();

    //Just wanted to test recaptcha *Not Required*
    final String SITE_KEY = "6LcE2XIUAAAAALW2YN0hsuh3ILtXtks9B5tP0P3N";
    final String SITE_SECRET_KEY = "6LcE2XIUAAAAAAALnCvPBjqQ9CWTtS0DKlMHsP3A";
    String userResponseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        //GetSupportActionBar is the changes towards the APP action bar in android
        getSupportActionBar().setTitle("Add Contact");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstname = (EditText) findViewById(R.id.FnameEditText);
        lastname = (EditText) findViewById(R.id.LnameEditText);
        contactno = (EditText) findViewById(R.id.ContactEditText);
        savebtn = (Button) findViewById(R.id.saveBtn);
        getData();

        //Once save button is click it will do validate the data > check for duplicate contact no > add the data > get the data again
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstname.getText().toString().isEmpty() || firstname.getText().toString().startsWith(" ") || lastname.getText().toString().isEmpty() ||
                        lastname.getText().toString().startsWith(" ")  || contactno.getText().toString().isEmpty() || contactno.getText().toString().startsWith(" ")) {

                    Toast.makeText(AddContacts.this, "No empty field allowed.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                        if(addArray.contains(Integer.parseInt(contactno.getText().toString())))
                        {
                            Toast.makeText(AddContacts.this, "This number already exist.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //added for security not needed
                            //recaptchaClick();

                            addData();
                            firstname.getText().clear();
                            lastname.getText().clear();
                            contactno.getText().clear();
                            Toast.makeText(AddContacts.this,"Your contact details have been saved", Toast.LENGTH_SHORT).show();
                            getData();
                        }
                }
            }
        });

    }

    //retrieve the record from db so i can do the validation
    private void getData(){
        //It is suppose to be initialize at the top
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("contacts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //QueryDocumentSnapshot replaced.
                            for (DocumentSnapshot document : task.getResult()) {
                                addArray.add(document.getLong("ContactNo").intValue());
                                //Log.d(TAG, document.getId() + " => " + document.getString("ContactNo"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //Add data method to add what the user have input into the firestore
    private void addData(){
        //It is suppose to be initialize at the top
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> contacts = new HashMap<>();
        contacts.put("FirstName", firstname.getText().toString());
        contacts.put("LastName", lastname.getText().toString());
        contacts.put("ContactNo", Integer.parseInt(contactno.getText().toString()));

// Add a new document with a generated ID
        db.collection("contacts")
                .add(contacts)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        db.collection("contacts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    //Action bar back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //This is the hardware back button function + transition
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Intent i = new Intent(this,MainActivity.class);
        AddContacts.this.finish();
        startActivity(i);

    }

    //USING RECAPTCHA
    public void recaptchaClick() {
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(this,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was
                                // successful.
                                userResponseToken = response.getTokenResult();
                                if (!userResponseToken.isEmpty()) {
                                    // Validate the user response token using the
                                    // reCAPTCHA siteverify API.
                                    // new SendPostRequest().execute();
                                    sendRequest();
                                }
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.d(TAG, "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                        } else {
                            // A different, unknown type of error occurred.
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                    }
                });
    }

    public void sendRequest()  {

        String url = "https://www.google.com/recaptcha/api/siteverify";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            //Toast.makeText(MainActivity.this, obj.getString("success"), Toast.LENGTH_LONG).show();
                            if (obj.getString("success").equals("true")){
                                addData();
                                firstname.getText().clear();
                                lastname.getText().clear();
                                contactno.getText().clear();
                                Toast.makeText(AddContacts.this,"Your contact details have been saved", Toast.LENGTH_SHORT).show();
                                getData();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddContacts.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("secret", SITE_SECRET_KEY);
                params.put("response", userResponseToken);
                return params;
            }
        };
        AppController.getInstance(this).addToRequestQueue(stringRequest);

    }

}
