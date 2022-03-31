package com.example.leafdex.fragments.parsers;

public class Url {
    public String o;
    public String m;
    public String s;

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public Url(String o, String m, String s) {
        this.o = o;
        this.m = m;
        this.s = s;
    }
}
