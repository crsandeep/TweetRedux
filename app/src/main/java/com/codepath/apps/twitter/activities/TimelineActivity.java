package com.codepath.apps.twitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.fragments.ComposeFragment;
import com.codepath.apps.twitter.fragments.HomeTimelineFragment;
import com.codepath.apps.twitter.fragments.TweetsListFragment;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;

    private static String screenName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, new HomeTimelineFragment());
        ft.commit();

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
    }

    private void setupProfileImage() {
        client.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Utils.profileImageUrl = response.getString("profile_image_url").replace("_normal", "_bigger");
                    screenName = response.getString("screen_name");
                    Glide.with(getApplicationContext()).load(Utils.profileImageUrl).bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 60, 0)).into(ivProfilePhoto);
                    TweetsListFragment.setScreenName(screenName);
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
}
