package com.codepath.apps.twitter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.adapters.FollowAdapter;
import com.codepath.apps.twitter.models.User;
import com.codepath.apps.twitter.network.TwitterClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class FollowActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvFollowList) RecyclerView rvFollows;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;

    private FollowAdapter followAdapter;
    private ArrayList<User> followList;
    private User user;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        ButterKnife.bind(this);

        client = TwitterApplication.getRestClient();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ivProfilePhoto.setVisibility(View.GONE);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbarTitle.setText(getIntent().getBooleanExtra("isFollower", false) ? "Followers": "Following");

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        followList = new ArrayList<>();
        followAdapter = new FollowAdapter(this, followList);
        rvFollows.setAdapter(followAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvFollows.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        rvFollows.setLayoutManager(layoutManager);

        if(getIntent().getBooleanExtra("isFollower", false)) {
            populateFollowers(null, true);
        } else {
            populateFollowing(null, true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateFollowers(String cursor, boolean clear) {

        client.getFollowers(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(clear) {
                        followList.clear();
                        followAdapter.notifyDataSetChanged();
                    }
                    followList.addAll(Arrays.asList(new Gson().fromJson(response.getJSONArray("users").toString(), User[].class)));
                    followAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, user.getScreenName(), cursor);
    }

    public void populateFollowing(String cursor, boolean clear) {

        client.getFollowing(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(clear) {
                        followList.clear();
                        followAdapter.notifyDataSetChanged();
                    }
                    followList.addAll(Arrays.asList(new Gson().fromJson(response.getJSONArray("users").toString(), User[].class)));
                    followAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, user.getScreenName(), cursor);
    }
}
