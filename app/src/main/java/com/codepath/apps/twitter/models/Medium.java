package com.codepath.apps.twitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Medium {
    @SerializedName("media_url")
    @Expose
    private String mediaUrl;

    public String getMediaUrl() {
        return mediaUrl;
    }
}
