package com.example.leafdex;

public class User {

    public String fname, lname, email, contact, sex, birthdate, imageURL;

    public User() {

    }

    public User(String fname, String lname, String email, String contact, String sex, String birthdate, String imageURL) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.contact = contact;
        this.sex = sex;
        this.birthdate = birthdate;
        this.imageURL = imageURL;
    }
}
