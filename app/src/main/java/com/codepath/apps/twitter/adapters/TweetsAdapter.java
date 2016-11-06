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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.activities.ProfileActivity;
import com.codepath.apps.twitter.fragments.ComposeFragment;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private List<com.codepath.apps.twitter.models.Tweet> mTweets;
    private Context mContext;

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
        TextView tvRetweetUser;
        ImageView ivProfileImage;
        ImageView ivRetweetImage;
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
        ImageView ivShare;
        LinearLayout retweetLayout;
        LinearLayout likeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRetweetUser = (TextView) itemView.findViewById(R.id.tvRetweetUser);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            ivRetweetImage = (ImageView) itemView.findViewById(R.id.ivRetweetImage);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            ivShare = (ImageView) itemView.findViewById(R.id.ivShare);
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
        holder.tvRetweetUser.setVisibility(View.GONE);
        holder.ivMedia.setImageResource(0);
        holder.ivMedia.setVisibility(View.GONE);
        holder.ivProfileImage.setImageResource(android.R.color.transparent);
        holder.ivRetweetImage.setVisibility(View.GONE);
        if(tweet.getRetweet() != null) {
            holder.tvRetweetUser.setVisibility(View.VISIBLE);
            holder.ivRetweetImage.setVisibility(View.VISIBLE);
            holder.tvRetweetUser.setText(tweet.getUser().getName() + " Retweeted");
            holder.tvUserName.setText(tweet.getRetweet().getUser().getName());
            String screenName = "@" + tweet.getRetweet().getUser().getScreenName();
            holder.tvScreenName.setText(screenName);
            String imageUrl = tweet.getRetweet().getUser().getProfileImageUrl().replace("_normal", "_bigger");
            Glide.with(getContext()).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(holder.ivProfileImage);
            if (tweet.getRetweet().getFavoriteCount() != null && tweet.getRetweet().getFavoriteCount() > 0) {
                String likeString = Integer.toString(tweet.getRetweet().getFavoriteCount());
                if (tweet.getRetweet().getFavoriteCount() >= 1000) {
                    likeString = (tweet.getRetweet().getFavoriteCount() / 1000) + "k";
                }
                holder.tvLikeCount.setText(likeString);
            }
        } else {
            holder.tvUserName.setText(tweet.getUser().getName());
            String screenName = "@" + tweet.getUser().getScreenName();
            holder.tvScreenName.setText(screenName);
            String imageUrl = tweet.getUser().getProfileImageUrl().replace("_normal", "_bigger");
            Glide.with(getContext()).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(holder.ivProfileImage);
            if (tweet.getFavoriteCount() != null && tweet.getFavoriteCount() > 0) {
                String likeString = Integer.toString(tweet.getFavoriteCount());
                if (tweet.getFavoriteCount() >= 1000) {
                    likeString = (tweet.getFavoriteCount() / 1000) + "k";
                }
                holder.tvLikeCount.setText(likeString);
            }
        }
        holder.tvBody.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "helveticaroman.otf"));
        holder.tvBody.setText(tweet.getText());

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)|\\#(\\w+)"), Color.parseColor("#66d6ff"),
                        text -> {
                            Toast.makeText(mContext, "Clicked username: " + text,
                                    Toast.LENGTH_SHORT).show();
                        }).into(holder.tvBody);

        holder.tvTime.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
        if (tweet.getRetweetCount() != null && tweet.getRetweetCount() > 0) {
            holder.tvRetweetCount.setText(Utils.formattedLikesAndRetweets(tweet.getRetweetCount()));
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

            holder.ivMedia.setVisibility(View.VISIBLE);

            Glide.with(getContext()).load(image).bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(holder.ivMedia);
        }

        holder.ivMedia.setOnClickListener(v -> Utils.showFullScreenImage(tweet, mContext));

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
                            if(tweet.getRetweet() != null) {
                                tweet.getRetweet().setFavoriteCount(tweet.getRetweet().getFavoriteCount() - 1);
                                holder.tvLikeCount.setText(Integer.toString(tweet.getRetweet().getFavoriteCount()));
                            } else {
                                tweet.setFavoriteCount(tweet.getFavoriteCount() - 1);
                                holder.tvLikeCount.setText(Integer.toString(tweet.getFavoriteCount()));
                            }
                            DrawableCompat.setTint(holder.ivLike.getDrawable(), Color.parseColor("#AAB8C2"));
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
                            if(tweet.getRetweet() != null) {
                                tweet.getRetweet().setFavoriteCount(tweet.getRetweet().getFavoriteCount() + 1);
                                holder.tvLikeCount.setText(Integer.toString(tweet.getRetweet().getFavoriteCount()));
                            } else {
                                tweet.setFavoriteCount(tweet.getFavoriteCount() + 1);
                                holder.tvLikeCount.setText(Integer.toString(tweet.getFavoriteCount()));
                            }
                            DrawableCompat.setTint(holder.ivLike.getDrawable(), Color.parseColor("#e81c4f"));
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

        holder.ivShare.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "";
            shareBody += "@" + tweet.getUser().getScreenName() + "'s" + "Tweet: ";
            shareBody += tweet.getText();
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Tweet from " + tweet.getUser().getName() + "(" + "@" + tweet.getUser().getScreenName() + ")");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });

        holder.ivProfileImage.setOnClickListener(v -> {
            Intent i = new Intent(mContext, ProfileActivity.class);
            i.putExtra("user", Parcels.wrap(tweet.getUser()));
            mContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
