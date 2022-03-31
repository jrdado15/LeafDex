package com.example.leafdex.fragments.parsers;

public class Family {
    public String scientificNameWithoutAuthor;
    public String scientificNameAuthorship;
    public String scientificName;

    public String getScientificNameWithoutAuthor() {
        return scientificNameWithoutAuthor;
    }

    public void setScientificNameWithoutAuthor(String scientificNameWithoutAuthor) {
        this.scientificNameWithoutAuthor = scientificNameWithoutAuthor;
    }

    public String getScientificNameAuthorship() {
        return scientificNameAuthorship;
    }

    public void setScientificNameAuthorship(String scientificNameAuthorship) {
        this.scientificNameAuthorship = scientificNameAuthorship;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public Family(String scientificNameWithoutAuthor, String scientificNameAuthorship, String scientificName) {
        this.scientificNameWithoutAuthor = scientificNameWithoutAuthor;
        this.scientificNameAuthorship = scientificNameAuthorship;
        this.scientificName = scientificName;
    }
}
