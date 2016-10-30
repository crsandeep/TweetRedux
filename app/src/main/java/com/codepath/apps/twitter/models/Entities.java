
package com.codepath.apps.twitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Entities {

    @SerializedName("media")
    @Expose
    private List<Medium> media = new ArrayList<Medium>();

    public List<Medium> getMedia() {
        return media;
    }
}
