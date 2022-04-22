package com.example.leafdex.fragments.parsers;

public class Post {

    public String imageURL, comName, desc, userID, dateTime;
    public Integer price, qty;

    public Post() {

    }

    public Post(String imageURL, String comName, String desc, String userID, String dateTime, Integer price, Integer qty) {
        this.imageURL = imageURL;
        this.comName = comName;
        this.desc = desc;
        this.userID = userID;
        this.dateTime = dateTime;
        this.price = price;
        this.qty = qty;
    }
}
