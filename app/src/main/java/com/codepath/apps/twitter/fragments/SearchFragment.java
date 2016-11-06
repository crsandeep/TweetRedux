package com.codepath.apps.twitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.network.TwitterClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class SearchFragment extends TweetsListFragment {
    private TwitterClient client;

    public static SearchFragment getInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    public void populateTimeline(String type, long count) {

        client.searchTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                boolean clear = false;
                if (type.equals("since_id")) {
                    clear = true;
                }
                try {
                    JSONArray tweetArray = response.getJSONArray("statuses");
                    addAll(Arrays.asList(new Gson().fromJson(tweetArray.toString(), com.codepath.apps.twitter.models.Tweet[].class)), clear);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, getArguments().getString("query"), count);
    }
}