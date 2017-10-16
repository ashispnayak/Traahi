package com.invincix.khanam;

/**
 * Created by Ashis on 10/15/2017.
 */

public class SafetyTipsFeed {
    private String Name;
    private String url;
    private String Description;

    public SafetyTipsFeed() {
    }

    public SafetyTipsFeed(String name, String url, String description) {
        Name = name;
        this.url = url;
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
