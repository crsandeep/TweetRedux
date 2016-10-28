package com.codepath.apps.twitter.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitter.adapters.TweetsAdapter;
import com.codepath.apps.twitter.databinding.ActivityTimelineBinding;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private ActivityTimelineBinding binding;

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;

    private static int PAGE_NUMBER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.swipeContainer.setProgressViewOffset(true, 100, 300);

        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(this, tweets);
        binding.rvTweets.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        binding.rvTweets.setAdapter(aTweets);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.rvTweets.setLayoutManager(mLayoutManager);
        client = TwitterApplication.getRestClient();
        populateTimeline();

        binding.swipeContainer.setOnRefreshListener(() -> {
            PAGE_NUMBER = 0;
            populateTimeline();
        });

        binding.rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                PAGE_NUMBER++;
                populateTimeline();
            }
        });
    }

    public void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                if(PAGE_NUMBER == 0) {
                    tweets.clear();
                }
                tweets.addAll(Tweet.fromJSONArray(json));
                aTweets.notifyDataSetChanged();
                binding.swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public void onLikeClick(View view) {
        ImageView ivLike = (ImageView) view.findViewById(R.id.ivLike);
        ivLike.setColorFilter(Color.parseColor("#E81C4F"));
    }
}
