package mapp.com.sg.contactapp;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Name implements Serializable {

    @Exclude private String id;
    private String LastName, FirstName;
    private int ContactNo;

    public Name(String lastName, String firstName, int contactNo) {
        this.LastName = lastName;
        this.FirstName = firstName;
        this.ContactNo = contactNo;
    }
    public Name() {
    }

    public int getContactNo() {
        return ContactNo;
    }

    public void setContactNo(int contactNo) {
        ContactNo = contactNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFirstName() {
        return FirstName;
    }



    public void setFirstName(String firstName) {
        FirstName = firstName;
    }
}


