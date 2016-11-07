package com.codepath.apps.twitter.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.fragments.UserTimelineFragment;
import com.codepath.apps.twitter.models.User;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    User user;
    String screenName;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.ivProfilePic) ImageView ivProfileImage;
    @BindView(R.id.ivBackgroundImage) ImageView ivBackgroundImage;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvTagline) TextView tvTagline;
    @BindView(R.id.tvFollowers) TextView tvFollowers;
    @BindView(R.id.tvFollowing) TextView tvFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        if(user == null) {
            screenName = getIntent().getStringExtra("screenName");
            client = TwitterApplication.getRestClient();
            if(getIntent().getBooleanExtra("fromSpan", false)) {
                client.getUserInfo(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        user = new Gson().fromJson(response.toString(), User.class);
                        populateProfileHeader(user);
                        setToolbarTitle();
                    }
                }, screenName);
            } else {
                client.getUserInfo(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        user = new Gson().fromJson(response.toString(), User.class);
                        populateProfileHeader(user);
                        setToolbarTitle();
                    }
                });
            }
        } else {
            screenName = user.getScreenName();
            populateProfileHeader(user);
            setToolbarTitle();
        }

        if(savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.getInstance(screenName);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }

    private void setToolbarTitle() {
        String title = user.getName() + " " +  Utils.formattedLikesAndRetweets(user.getStatusesCount()) + " Tweets";
        collapsingToolbar.setTitle(title);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateProfileHeader(User user) {
        tvName.setText(user.getName());
        tvScreenName.setText("@" + user.getScreenName());
        tvTagline.setText(user.getDescription());
        String htmlText = "<b>" + Utils.formattedLikesAndRetweets(user.getFollowersCount()) + "</b> FOLLOWERS";
        tvFollowers.setText(Html.fromHtml(htmlText));
        htmlText = "<b>" + Utils.formattedLikesAndRetweets(user.getFriendsCount()) + "</b> FOLLOWING";
        tvFollowing.setText(Html.fromHtml(htmlText));

        tvFollowers.setOnClickListener(v1 -> {
            Intent intent = new Intent(this, FollowActivity.class);
            intent.putExtra("user", Parcels.wrap(user));
            intent.putExtra("isFollower", true);
            startActivity(intent);
        });

        tvFollowing.setOnClickListener(v1 -> {
            Intent intent = new Intent(this, FollowActivity.class);
            intent.putExtra("user", Parcels.wrap(user));
            intent.putExtra("isFollower", false);
            startActivity(intent);
        });

        Glide.with(this).load(user.getProfileImageUrl().replace("_normal", "_bigger")).bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 10, 0)).into(ivProfileImage);
        ivProfileImage.setOnClickListener(v -> Utils.showFullScreenImageForUrl(user.getProfileImageUrl().replace("_normal", ""), this));
        ivBackgroundImage.setImageResource(android.R.color.transparent);
        if(!TextUtils.isEmpty(user.getProfileBannerUrl())) {
            Picasso.with(this).load(user.getProfileBannerUrl()).placeholder(R.drawable.placeholder).into(ivBackgroundImage, new Callback() {
                @Override public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) ivBackgroundImage.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(palette -> applyPalette(palette));
                    ivBackgroundImage.setOnClickListener(v -> Utils.showFullScreenImageForUrl(user.getProfileBannerUrl(), ProfileActivity.this));
                }
                @Override public void onError() {
                    ivBackgroundImage.setImageResource(R.drawable.placeholder);
                }
            });
        } else {
            ivBackgroundImage.getLayoutParams().height = 350;
            ivBackgroundImage.setBackgroundColor(Color.parseColor("#AAB8C2"));
        }
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        collapsingToolbar.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbar.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        supportStartPostponedEnterTransition();
    }
}
