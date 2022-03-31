package com.example.leafdex.fragments.parsers;

import java.util.List;

public class Query {
    public String project;
    public List<String> images;
    public List<String> organs;
    public Boolean includeRelatedImages;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getOrgans() {
        return organs;
    }

    public void setOrgans(List<String> organs) {
        this.organs = organs;
    }

    public Boolean getIncludeRelatedImages() {
        return includeRelatedImages;
    }

    public void setIncludeRelatedImages(Boolean includeRelatedImages) {
        this.includeRelatedImages = includeRelatedImages;
    }

    public Query(String project, List<String> images, List<String> organs, Boolean includeRelatedImages) {
        this.project = project;
        this.images = images;
        this.organs = organs;
        this.includeRelatedImages = includeRelatedImages;
    }
}
