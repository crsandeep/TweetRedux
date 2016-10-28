package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.utils.Utils;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context mContext;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
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

        public ViewHolder(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
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
        Tweet tweet = mTweets.get(position);
        //holder.tvUserName.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "helvetica.otf"));
        holder.tvUserName.setText(tweet.getUser().getName());
        holder.tvBody.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "helveticaroman.otf"));
        holder.tvBody.setText(tweet.getBody());
        holder.ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).bitmapTransform(new RoundedCornersTransformation(mContext, 30, 0,
                RoundedCornersTransformation.CornerType.ALL)).into(holder.ivProfileImage);
        holder.tvTime.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
        holder.tvRetweetCount.setText(tweet.getRetweetCount());
        if(tweet.getUser().getFavouritesCount() != null && Integer.parseInt(tweet.getUser().getFavouritesCount()) > 0) {
            holder.tvLikeCount.setText(tweet.getUser().getFavouritesCount());
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
