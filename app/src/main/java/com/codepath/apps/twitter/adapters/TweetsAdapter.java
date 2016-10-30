package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.activities.ImageFullscreenActivity;
import com.codepath.apps.twitter.fragments.ComposeFragment;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private static List<com.codepath.apps.twitter.models.Tweet> mTweets;
    private static Context mContext;

    TwitterClient client;

    public TweetsAdapter(Context context, List<com.codepath.apps.twitter.models.Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
        client = TwitterApplication.getRestClient();
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvBody;
        TextView tvTime;
        TextView tvRetweetCount;
        TextView tvLikeCount;
        ImageView ivMedia;
        ImageView ivRetweet;
        ImageView ivLike;
        ImageView ivReply;
        LinearLayout retweetLayout;
        LinearLayout likeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            retweetLayout = (LinearLayout) itemView.findViewById(R.id.retweetLayout);
            likeLayout = (LinearLayout) itemView.findViewById(R.id.likeLayout);
        }
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder holder, int position) {
        com.codepath.apps.twitter.models.Tweet tweet = mTweets.get(position);
        holder.tvUserName.setText(tweet.getUser().getName());
        String screenName = "@" + tweet.getUser().getScreenName();
        holder.tvScreenName.setText(screenName);
        holder.tvBody.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "helveticaroman.otf"));
        holder.tvBody.setText(tweet.getText());
        holder.ivProfileImage.setImageResource(android.R.color.transparent);
        String imageUrl = tweet.getUser().getProfileImageUrl().replace("_normal", "_bigger");
        Glide.with(getContext()).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0,
                RoundedCornersTransformation.CornerType.ALL)).into(holder.ivProfileImage);
        holder.tvTime.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
        if (tweet.getRetweetCount() != null && tweet.getRetweetCount() > 0) {
            String retweetString = Integer.toString(tweet.getRetweetCount());
            if (tweet.getRetweetCount() >= 1000) {
                retweetString = (tweet.getRetweetCount() / 1000) + "k";
            }
            holder.tvRetweetCount.setText(retweetString);
        }
        if (tweet.getFavoriteCount() != null && tweet.getFavoriteCount() > 0) {
            String likeString = Integer.toString(tweet.getFavoriteCount());
            if (tweet.getFavoriteCount() >= 1000) {
                likeString = (tweet.getFavoriteCount() / 1000) + "k";
            }
            holder.tvLikeCount.setText(likeString);
        }
        if (tweet.getRetweeted()) {
            holder.tvRetweetCount.setTextColor(Color.parseColor("#19cf86"));
            DrawableCompat.setTint(holder.ivRetweet.getDrawable(), Color.parseColor("#19cf86"));
        } else {
            holder.tvRetweetCount.setTextColor(Color.parseColor("#AAB8C2"));
            DrawableCompat.setTint(holder.ivRetweet.getDrawable(), Color.parseColor("#AAB8C2"));
        }

        if (tweet.getFavorited()) {
            holder.tvLikeCount.setTextColor(Color.parseColor("#e81c4f"));
            DrawableCompat.setTint(holder.ivLike.getDrawable(), Color.parseColor("#e81c4f"));
        } else {
            holder.tvLikeCount.setTextColor(Color.parseColor("#AAB8C2"));
            DrawableCompat.setTint(holder.ivLike.getDrawable(), Color.parseColor("#AAB8C2"));
        }

        holder.ivMedia.setImageResource(0);
        if (tweet.getEntities().getMedia() != null && tweet.getEntities().getMedia().size() > 0) {
            String image = "";

            for (int i = 0; i < tweet.getEntities().getMedia().size(); i++) {
                if (tweet.getEntities().getMedia().get(i) != null) {
                    String mediaUrl = tweet.getEntities().getMedia().get(i).getMediaUrl();
                    if (!TextUtils.isEmpty(mediaUrl)) {
                        image = mediaUrl;
                        break;
                    }
                }
            }

            Glide.with(getContext()).load(image).bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(holder.ivMedia);
        }

        holder.ivMedia.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ImageFullscreenActivity.class);
            String image = "";

            if (tweet.getEntities().getMedia() != null && tweet.getEntities().getMedia().size() > 0) {
                for (int i = 0; i < tweet.getEntities().getMedia().size(); i++) {
                    if (tweet.getEntities().getMedia().get(i) != null) {
                        String mediaUrl = tweet.getEntities().getMedia().get(i).getMediaUrl();
                        if (!TextUtils.isEmpty(mediaUrl)) {
                            image = mediaUrl;
                            break;
                        }
                    }
                }
            }
            intent.putExtra("image", image);
            mContext.startActivity(intent);
        });

        holder.ivReply.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("screenName", "@" + tweet.getUser().getScreenName() + " ");
            bundle.putString("replyId", tweet.getIdStr());

            FragmentTransaction fm = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
            ComposeFragment composeFragment = ComposeFragment.newInstance("Compose tweet");
            composeFragment.setArguments(bundle);
            composeFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
            composeFragment.show(fm, "fragment_compose");
        });

        holder.retweetLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (tweet.getRetweeted()) {
                    client.unReTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            tweet.setRetweetCount(tweet.getRetweetCount() - 1);
                            DrawableCompat.setTint(holder.ivRetweet.getDrawable(), Color.parseColor("#AAB8C2"));
                            holder.tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
                            holder.tvRetweetCount.setTextColor(Color.parseColor("#AAB8C2"));
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
                            tweet.setRetweetCount(tweet.getRetweetCount() + 1);
                            DrawableCompat.setTint(holder.ivRetweet.getDrawable(), Color.parseColor("#19cf86"));
                            holder.tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
                            holder.tvRetweetCount.setTextColor(Color.parseColor("#19cf86"));
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


        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (tweet.getFavorited()) {
                    client.unlike(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            tweet.setFavoriteCount(tweet.getFavoriteCount() - 1);
                            DrawableCompat.setTint(holder.ivLike.getDrawable(), Color.parseColor("#AAB8C2"));
                            holder.tvLikeCount.setText(Integer.toString(tweet.getFavoriteCount()));
                            holder.tvLikeCount.setTextColor(Color.parseColor("#AAB8C2"));
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
                            tweet.setFavoriteCount(tweet.getFavoriteCount() + 1);
                            DrawableCompat.setTint(holder.ivLike.getDrawable(), Color.parseColor("#e81c4f"));
                            holder.tvLikeCount.setText(Integer.toString(tweet.getFavoriteCount()));
                            holder.tvLikeCount.setTextColor(Color.parseColor("#e81c4f"));
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
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
