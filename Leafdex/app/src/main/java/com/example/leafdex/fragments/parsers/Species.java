package com.example.leafdex.fragments.parsers;

import java.util.List;

public class Species {
    public String scientificNameWithoutAuthor;
    public String scientificNameAuthorship;
    public Genus genus;
    public Family family;
    public List<String> commonNames;
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

    public Genus getGenus() {
        return genus;
    }

    public void setGenus(Genus genus) {
        this.genus = genus;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public List<String> getCommonNames() {
        return commonNames;
    }

    public void setCommonNames(List<String> commonNames) {
        this.commonNames = commonNames;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public Species(String scientificNameWithoutAuthor, String scientificNameAuthorship, Genus genus, Family family, List<String> commonNames, String scientificName) {
        this.scientificNameWithoutAuthor = scientificNameWithoutAuthor;
        this.scientificNameAuthorship = scientificNameAuthorship;
        this.genus = genus;
        this.family = family;
        this.commonNames = commonNames;
        this.scientificName = scientificName;
    }
}
