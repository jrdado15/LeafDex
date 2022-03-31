package com.example.leafdex.fragments.parsers;

public class Date {
    public Long timestamp;
    public String string;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Date(Long timestamp, String string) {
        this.timestamp = timestamp;
        this.string = string;
    }
}
