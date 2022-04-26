package com.bistro.model;

import java.io.Serializable;

public class UserLikeModel implements Serializable {

    private String key;
    private String date;

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
