package com.codepath.apps.twitter.models;

import com.codepath.apps.twitter.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DirectMessage {

    User recipientUser;
    User senderUser;
    String text;
    String relativeDate;

    public String getRelativeDate() {
        return relativeDate;
    }

    public void setRelativeDate(String relativeDate) {
        this.relativeDate = relativeDate;
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(User recipientUser) {
        this.recipientUser = recipientUser;
    }

    public User getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static DirectMessage fromJson(JSONObject jsonObject){

        DirectMessage message = new DirectMessage();
        try {
            message.text = jsonObject.getString("text");
            message.relativeDate = Utils.getRelativeTimeAgo(jsonObject.getString("created_at"));
            message.recipientUser = new Gson().fromJson(jsonObject.getJSONObject("recipient").toString(), com.codepath.apps.twitter.models.User.class);
            message.senderUser = new Gson().fromJson(jsonObject.getJSONObject("sender").toString(), com.codepath.apps.twitter.models.User.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static ArrayList<DirectMessage> fromJsonArray(JSONArray jsonArray){
        ArrayList<DirectMessage> result = new ArrayList<>();
        for(int i=0; i< jsonArray.length(); i++){
            try {
                DirectMessage message = fromJson(jsonArray.getJSONObject(i));
                result.add(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
