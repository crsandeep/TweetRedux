package com.codepath.apps.twitter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.activities.DetailedViewActivity;
import com.codepath.apps.twitter.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitter.adapters.ItemClickSupport;
import com.codepath.apps.twitter.adapters.TweetsAdapter;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public abstract class TweetsListFragment extends Fragment {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;

    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    private static String screenName = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(getActivity(), tweets);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        ButterKnife.bind(this, view);

        rvTweets.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        rvTweets.setAdapter(aTweets);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mLayoutManager.scrollToPosition(0);
        rvTweets.setLayoutManager(mLayoutManager);

        if (!Utils.isNetworkAvailable(getActivity())) {
            Snackbar.make(swipeContainer, R.string.not_connected, Snackbar.LENGTH_INDEFINITE).setAction("Retry",
                    v -> {
                        getActivity().recreate();
                    }).show();
        }

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline("max_id", tweets.get(tweets.size() - 1).getId() - 1);
            }
        });

        ItemClickSupport.addTo(rvTweets).setOnItemLongClickListener(
                new ItemClickSupport.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                        final int pos = position;
                        if (!TextUtils.isEmpty(screenName)) {
                            if (screenName.equals(tweets.get(pos).getUser().getScreenName())) {
                                new MaterialDialog.Builder(getActivity())
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
                                                        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
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
                            } else {
                                Toast.makeText(getActivity(), "You cannot delete this tweet", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return true;
                    }
                }
        );

        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Intent intent = new Intent(getActivity(), DetailedViewActivity.class);
                    intent.putExtra("tweet", Parcels.wrap(tweets.get(position)));
                    startActivity(intent);
                }
        );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateTimeline("since_id", (long) 1);
        swipeContainer.setOnRefreshListener(() -> {
            populateTimeline("since_id", (long) 1);
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public static void setScreenName(String name) {
        screenName = name;
    }

    protected void addAll(List<Tweet> list, boolean clear){
        if(clear){
            tweets.clear();
            aTweets.notifyDataSetChanged();
        }
        tweets.addAll(list);
        aTweets.notifyDataSetChanged();
        rvTweets.setVisibility(View.VISIBLE);
        swipeContainer.setRefreshing(false);
    }

    protected abstract void populateTimeline(String sinceOrMaxId, long count);
}