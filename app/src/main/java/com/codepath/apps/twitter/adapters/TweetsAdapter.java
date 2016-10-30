package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.activities.ImageFullscreenActivity;
import com.codepath.apps.twitter.activities.TimelineActivity;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.utils.Utils;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private static List<com.codepath.apps.twitter.models.Tweet> mTweets;
    private static Context mContext;

    public TweetsAdapter(Context context, List<com.codepath.apps.twitter.models.Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvBody;
        TextView tvTime;
        TextView tvRetweetCount;
        TextView tvLikeCount;
        ImageView ivMedia;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);

            ivMedia.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ImageFullscreenActivity.class);
                    int position = getAdapterPosition();

                    Tweet tweet = TimelineActivity.getTweets().get(position);
                    String image = "";

                    if(tweet.getEntities().getMedia() != null && tweet.getEntities().getMedia().size() > 0) {
                        for(int i = 0; i < tweet.getEntities().getMedia().size();i++) {
                            if(tweet.getEntities().getMedia().get(i) != null) {
                                String mediaUrl = tweet.getEntities().getMedia().get(i).getMediaUrl();
                                if (mediaUrl.equals("")) {
                                    continue;
                                } else {
                                    image = mediaUrl;
                                    break;
                                }
                            }
                        }
                    }
                    intent.putExtra("image", image);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder holder, int position) {
        com.codepath.apps.twitter.models.Tweet tweet = mTweets.get(position);
        holder.tvUserName.setText(tweet.getUser().getName());
        holder.tvBody.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "helveticaroman.otf"));
        holder.tvBody.setText(tweet.getText());
        holder.ivProfileImage.setImageResource(android.R.color.transparent);
        String imageUrl = tweet.getUser().getProfileImageUrl().replace("_normal", "_bigger");
        Glide.with(getContext()).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0,
                RoundedCornersTransformation.CornerType.ALL)).into(holder.ivProfileImage);
        holder.tvTime.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
        if(tweet.getRetweetCount() != null && tweet.getRetweetCount() > 0) {
            holder.tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
        }
        if(tweet.getUser().getFavouritesCount() != null && tweet.getUser().getFavouritesCount() > 0) {
            holder.tvLikeCount.setText(Integer.toString(tweet.getUser().getFavouritesCount()));
        }
        holder.ivMedia.setImageResource(0);
        if(tweet.getEntities().getMedia() != null && tweet.getEntities().getMedia().size() > 0) {
            String image = "";

            for(int i = 0; i < tweet.getEntities().getMedia().size();i++) {
                if(tweet.getEntities().getMedia().get(i) != null) {
                    String mediaUrl = tweet.getEntities().getMedia().get(i).getMediaUrl();
                    if (mediaUrl.equals("")) {
                        continue;
                    } else {
                        image = mediaUrl;
                        break;
                    }
                }
            }

            Glide.with(getContext()).load(image).bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0,
                    RoundedCornersTransformation.CornerType.ALL)).into(holder.ivMedia);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
