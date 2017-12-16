package com.invincix.khanam;

/**
 * Created by Ashis on 10/26/2017.
 */

public class MunicipalityFeed {
    public String Description;
    public String Url;
    public String Name;
    public String Uid;
    public MunicipalityFeed(){

    }

    public MunicipalityFeed(String name, String desc, String url, String uid){
        this.Description=desc;
        this.Url=url;
        this.Name=name;
        this.Uid=uid;


    }


    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        this.Url = url;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

}
