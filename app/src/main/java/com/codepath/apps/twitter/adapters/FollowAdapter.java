package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.activities.ProfileActivity;
import com.codepath.apps.twitter.models.User;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {

    private List<User> mUsers;
    private Context mContext;

    public FollowAdapter(Context context, List<User> users) {
        mUsers = users;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public FollowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_follow, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FollowAdapter.ViewHolder viewHolder, int position) {
        final User user = mUsers.get(position);
        viewHolder.tvUserName.setText(user.getName());
        viewHolder.tvScreenName.setText(user.getScreenName());
        viewHolder.tvDescription.setText(user.getDescription());
        viewHolder.ivProfileImg.setImageResource(android.R.color.transparent);

        viewHolder.rlFollowItem.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra("user", Parcels.wrap(user));
            getContext().startActivity(intent);
        });
        Glide.with(getContext()).load(user.getProfileImageUrl()).bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0,
                RoundedCornersTransformation.CornerType.ALL)).into(viewHolder.ivProfileImg);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserName, tvDescription, tvScreenName;
        public ImageView ivProfileImg;
        public RelativeLayout rlFollowItem;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            ivProfileImg = (ImageView) itemView.findViewById(R.id.ivProfileImg);
            rlFollowItem = (RelativeLayout) itemView.findViewById(R.id.rlFollowItem);
        }
    }
}
