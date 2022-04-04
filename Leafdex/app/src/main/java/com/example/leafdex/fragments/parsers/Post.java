package com.example.leafdex.fragments.parsers;

public class Post {

    public String imageURL, comName, desc, userID;

    public Post() {

    }

    public Post(String imageURL, String comName, String desc, String userID) {
        this.imageURL = imageURL;
        this.comName = comName;
        this.desc = desc;
        this.userID = userID;
    }
}
