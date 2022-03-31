package com.example.leafdex.fragments.parsers;

public class Image {
    public String organ;
    public String author;
    public String license;
    public Date date;
    public Url url;
    public String citation;

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public Image(String organ, String author, String license, Date date, Url url, String citation) {
        this.organ = organ;
        this.author = author;
        this.license = license;
        this.date = date;
        this.url = url;
        this.citation = citation;
    }
}
