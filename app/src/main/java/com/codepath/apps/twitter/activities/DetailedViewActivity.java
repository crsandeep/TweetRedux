package com.codepath.apps.twitter.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.databinding.ActivityDetailedViewBinding;
import com.codepath.apps.twitter.fragments.ComposeFragment;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailedViewActivity extends AppCompatActivity {

    private ActivityDetailedViewBinding binding;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detailed_view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        client = TwitterApplication.getRestClient();

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        binding.ivProfileImage.setImageResource(android.R.color.transparent);
        binding.tvLikeCount.setText("");

        if(tweet.getRetweet() != null) {
            binding.tvUserName.setText(tweet.getRetweet().getUser().getName());
            String screenName = "@" + tweet.getRetweet().getUser().getScreenName();
            binding.tvScreenName.setText(screenName);
            String imageUrl = tweet.getRetweet().getUser().getProfileImageUrl().replace("_normal", "_bigger");
            Glide.with(this).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(this, 10, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(binding.ivProfileImage);
            if (tweet.getRetweet().getFavoriteCount() != null && tweet.getRetweet().getFavoriteCount() > 0) {
                String likeString = Integer.toString(tweet.getRetweet().getFavoriteCount());
                if (tweet.getRetweet().getFavoriteCount() >= 1000) {
                    likeString = (tweet.getRetweet().getFavoriteCount() / 1000) + "k";
                }
                binding.tvLikeCount.setText(likeString);
            }
        } else {
            binding.tvUserName.setText(tweet.getUser().getName());
            String screenName = "@" + tweet.getUser().getScreenName();
            binding.tvScreenName.setText(screenName);
            String imageUrl = tweet.getUser().getProfileImageUrl().replace("_normal", "_bigger");
            Glide.with(this).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(this, 10, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(binding.ivProfileImage);
            if (tweet.getFavoriteCount() != null && tweet.getFavoriteCount() > 0) {
                String likeString = Integer.toString(tweet.getFavoriteCount());
                if (tweet.getFavoriteCount() >= 1000) {
                    likeString = (tweet.getFavoriteCount() / 1000) + "k";
                }
                binding.tvLikeCount.setText(likeString);
            }
        }

        binding.ivProfileImage.setOnClickListener(v1 -> {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("user", Parcels.wrap(tweet.getUser()));
            startActivity(i);
        });

        binding.tvBody.setTypeface(Typeface.createFromAsset(getAssets(), "helveticaroman.otf"));
        binding.tvBody.setText(tweet.getText());
        Utils.addSpanListener(this, binding.tvBody);

        String relativeTime = Utils.getRelativeTimeAgo(tweet.getCreatedAt()) + " ago";
        binding.tvTime.setText(relativeTime);
        binding.tvRetweetCount.setText("");
        if(tweet.getRetweetCount() != null && tweet.getRetweetCount() > 0) {
            binding.tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
        }

        if(tweet.getRetweeted()) {
            DrawableCompat.setTint(binding.ivRetweet.getDrawable(), Color.parseColor("#19cf86"));
        } else {
            DrawableCompat.setTint(binding.ivRetweet.getDrawable(), Color.parseColor("#AAB8C2"));
        }

        if(tweet.getFavorited()) {
            DrawableCompat.setTint(binding.ivLike.getDrawable(), Color.parseColor("#e81c4f"));
        } else {
            DrawableCompat.setTint(binding.ivLike.getDrawable(), Color.parseColor("#AAB8C2"));
        }

        binding.ivMedia.setImageResource(0);
        if(tweet.getEntities().getMedia() != null && tweet.getEntities().getMedia().size() > 0) {
            String image = "";

            for(int i = 0; i < tweet.getEntities().getMedia().size();i++) {
                if(tweet.getEntities().getMedia().get(i) != null) {
                    String mediaUrl = tweet.getEntities().getMedia().get(i).getMediaUrl();
                    if (!TextUtils.isEmpty(mediaUrl)) {
                        image = mediaUrl;
                        break;
                    }
                }
            }

            Glide.with(this).load(image).bitmapTransform(new RoundedCornersTransformation(this, 10, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(binding.ivMedia);
        }

        binding.ivMedia.setOnClickListener(v -> Utils.showFullScreenImage(tweet, this));

        binding.ivReply.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("screenName", "@" + tweet.getUser().getScreenName() + " ");
            bundle.putString("replyId", tweet.getIdStr());

            FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
            ComposeFragment composeFragment = ComposeFragment.newInstance("Compose tweet");
            composeFragment.setArguments(bundle);
            composeFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
            composeFragment.show(fm, "fragment_compose");
        });

        binding.ivRetweet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(tweet.getRetweeted()) {
                    client.unReTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            DrawableCompat.setTint(binding.ivRetweet.getDrawable(), Color.parseColor("#AAB8C2"));
                            tweet.setRetweeted(false);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    }, tweet.getIdStr());
                } else {
                    client.reTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            DrawableCompat.setTint(binding.ivRetweet.getDrawable(), Color.parseColor("#19cf86"));
                            tweet.setRetweeted(true);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    }, tweet.getIdStr());
                }
            }
        });


        binding.ivLike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(tweet.getFavorited()) {
                    client.unlike(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            DrawableCompat.setTint(binding.ivLike.getDrawable(), Color.parseColor("#AAB8C2"));
                            tweet.setFavorited(false);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    }, tweet.getIdStr());
                } else {
                    client.like(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            DrawableCompat.setTint(binding.ivLike.getDrawable(), Color.parseColor("#e81c4f"));
                            tweet.setFavorited(true);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    }, tweet.getIdStr());
                }
            }
        });

        binding.ivShare.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "";
            shareBody += "@" + tweet.getUser().getScreenName() + "'s" + "Tweet: ";
            shareBody += tweet.getText();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Tweet from " + tweet.getUser().getName() + "(" + "@" + tweet.getUser().getScreenName() + ")");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
