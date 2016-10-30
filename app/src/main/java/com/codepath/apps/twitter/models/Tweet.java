package com.codepath.apps.twitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Tweet {

    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("id_str")
    @Expose
    private String idStr;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("truncated")
    @Expose
    private Boolean truncated;
    @SerializedName("entities")
    @Expose
    private Entities entities;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("is_quote_status")
    @Expose
    private Boolean isQuoteStatus;
    @SerializedName("retweet_count")
    @Expose
    private Integer retweetCount;
    @SerializedName("favorite_count")
    @Expose
    private Integer favoriteCount;
    @SerializedName("favorited")
    @Expose
    private Boolean favorited;
    @SerializedName("retweeted")
    @Expose
    private Boolean retweeted;

    public String getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getIdStr() {
        return idStr;
    }

    public String getText() {
        return text;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public Entities getEntities() {
        return entities;
    }

    public User getUser() {
        return user;
    }

    public Boolean getQuoteStatus() {
        return isQuoteStatus;
    }

    public Integer getRetweetCount() {
        return retweetCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setQuoteStatus(Boolean quoteStatus) {
        isQuoteStatus = quoteStatus;
    }

    public void setRetweetCount(Integer retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }
}
