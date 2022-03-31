package com.example.leafdex.fragments.parsers;

import java.util.List;

public class Root {
    public Query query;
    public String language;
    public String preferedReferential;
    public String bestMatch;
    public List<Result> results;
    public String version;
    public int remainingIdentificationRequests;

    public Query getQuery() {
        return query;
    }

    public String getLanguage() {
        return language;
    }

    public String getPreferedReferential() {
        return preferedReferential;
    }

    public String getBestMatch() {
        return bestMatch;
    }

    public List<Result> getResults() {
        return results;
    }

    public String getVersion() {
        return version;
    }

    public int getRemainingIdentificationRequests() {
        return remainingIdentificationRequests;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setPreferedReferential(String preferedReferential) {
        this.preferedReferential = preferedReferential;
    }

    public void setBestMatch(String bestMatch) {
        this.bestMatch = bestMatch;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setRemainingIdentificationRequests(int remainingIdentificationRequests) {
        this.remainingIdentificationRequests = remainingIdentificationRequests;
    }

    public Root() {

    }

    public Root(Query query, String language, String preferedReferential, String bestMatch, List<Result> results, String version, int remainingIdentificationRequests) {
        this.query = query;
        this.language = language;
        this.preferedReferential = preferedReferential;
        this.bestMatch = bestMatch;
        this.results = results;
        this.version = version;
        this.remainingIdentificationRequests = remainingIdentificationRequests;
    }
}
