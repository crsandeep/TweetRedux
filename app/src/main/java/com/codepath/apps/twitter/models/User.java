package com.codepath.apps.twitter.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String name;
    private Long uid;
    private String screenName;
    private String favouritesCount;
    private String profileImageUrl;

    public String getName() {
        return name;
    }

    public Long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getFavouritesCount() {
        return favouritesCount;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.favouritesCount = jsonObject.getString("favourites_count");
            user.profileImageUrl = jsonObject.getString("profile_image_url").replace("_normal", "_bigger");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

}
