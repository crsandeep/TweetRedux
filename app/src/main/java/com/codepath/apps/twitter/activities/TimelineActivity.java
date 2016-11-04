package com.codepath.apps.twitter.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitter.adapters.ItemClickSupport;
import com.codepath.apps.twitter.adapters.TweetsAdapter;
import com.codepath.apps.twitter.fragments.ComposeFragment;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private static ArrayList<com.codepath.apps.twitter.models.Tweet> tweets;
    private TweetsAdapter aTweets;

    private static boolean refresh = false;
    private static String screenName = "";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!isNetworkAvailable(this)) {
            Snackbar.make(swipeContainer, R.string.not_connected, Snackbar.LENGTH_INDEFINITE).setAction("Retry",
                    v -> {
                        this.recreate();
                    }).show();
        }

        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(this, tweets);
        rvTweets.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        rvTweets.setAdapter(aTweets);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(mLayoutManager);
        client = TwitterApplication.getRestClient();
        populateTimeline();
        setupProfileImage();

        Intent webIntent = getIntent();
        String action = webIntent.getAction();
        String type = webIntent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String titleOfPage = webIntent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = webIntent.getStringExtra(Intent.EXTRA_TEXT);

                Bundle bundle = new Bundle();
                bundle.putString("body", urlOfPage);
                bundle.putString("titleOfPage", titleOfPage);

                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance("Compose tweet");
                composeFragment.setArguments(bundle);
                composeFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
                composeFragment.show(fm, "fragment_compose");
            }
        }

        swipeContainer.setOnRefreshListener(() -> {
            refresh = true;
            populateTimeline();
        });

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline();
            }
        });

        ItemClickSupport.addTo(rvTweets).setOnItemLongClickListener(
                new ItemClickSupport.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                        final int pos = position;
                        if (screenName.equals(tweets.get(pos).getUser().getScreenName())) {
                            new MaterialDialog.Builder(TimelineActivity.this)
                                    .title("Confirm delete")
                                    .content("Are you sure?")
                                    .positiveText("Yes")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Tweet tweet = tweets.get(pos);
                                            client.deleteTweet(new JsonHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                                                    tweets.remove(tweet);
                                                    Toast.makeText(TimelineActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                    aTweets.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                                }
                                            }, tweet.getIdStr());
                                        }
                                    })
                                    .negativeText("No")
                                    .show();
                        } else{
                            Toast.makeText(TimelineActivity.this, "You cannot delete this tweet", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
        );


        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Intent intent = new Intent(TimelineActivity.this, DetailedViewActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweets.get(position)));
                    startActivity(intent);
                }
        );
    }

    public void populateTimeline() {
        String max_id = "";

        if (refresh) {
            tweets.clear();
            aTweets.notifyDataSetChanged();
        }

        if (tweets != null && tweets.size() > 0) {
            max_id = tweets.get(tweets.size() - 1).getIdStr();
        }

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                refresh = false;
                System.out.println(json);
                tweets.addAll(Arrays.asList(new Gson().fromJson(json.toString(), com.codepath.apps.twitter.models.Tweet[].class)));
                aTweets.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                System.out.println("network call failed" + statusCode);
                swipeContainer.setRefreshing(false);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, max_id);
    }

    private void setupProfileImage() {
        client.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Utils.profileImageUrl = response.getString("profile_image_url").replace("_normal", "_bigger");
                    screenName = response.getString("screen_name");
                    Glide.with(getApplicationContext()).load(Utils.profileImageUrl).bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 60, 0)).into(ivProfilePhoto);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void composeNew(View view) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance("Compose tweet");
        composeFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        composeFragment.show(fm, "fragment_compose");
    }

    public void postTweet(View view) {
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
