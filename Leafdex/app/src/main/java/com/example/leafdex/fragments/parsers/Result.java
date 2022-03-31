package com.example.leafdex.fragments.parsers;

import java.util.List;

public class Result {
    public double score;
    public Species species;
    public List<Image> images;
    public Gbif gbif;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Gbif getGbif() {
        return gbif;
    }

    public void setGbif(Gbif gbif) {
        this.gbif = gbif;
    }

    public Result(double score, Species species, List<Image> images, Gbif gbif) {
        this.score = score;
        this.species = species;
        this.images = images;
        this.gbif = gbif;
    }
}
