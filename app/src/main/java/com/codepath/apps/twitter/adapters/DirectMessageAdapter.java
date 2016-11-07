package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.DirectMessage;
import com.codepath.apps.twitter.models.User;
import com.codepath.apps.twitter.utils.Utils;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DirectMessageAdapter extends RecyclerView.Adapter<DirectMessageAdapter.ViewHolder> {

    private List<DirectMessage> mMessages;
    private Context mContext;
    public DirectMessageAdapter(Context context, List<DirectMessage> messages) {
        mMessages = messages;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public DirectMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_message, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(DirectMessageAdapter.ViewHolder viewHolder, int position) {
        final DirectMessage message = mMessages.get(position);
        User otherUser;
        String text;
        if(!message.getRecipientUser().getScreenName().equals(Utils.screenName)){
            text = new String("Me: ");
            otherUser = message.getRecipientUser();
        }else{
            text = new String();
            otherUser = message.getSenderUser();
        }

        TextView tvUserName = viewHolder.tvUserName;
        TextView tvScreenName = viewHolder.tvScreenName;
        TextView tvRelativeTime = viewHolder.tvRelativeTime;

        TextView tvText = viewHolder.tvText;
        ImageView ivProfileImg = viewHolder.ivProfileImg;

        tvUserName.setText(otherUser.getName());
        tvScreenName.setText(otherUser.getScreenName());
        tvRelativeTime.setText(message.getRelativeDate());
        tvText.setText(text + message.getText());
        ivProfileImg.setImageResource(android.R.color.transparent);

        Glide.with(getContext()).load(otherUser.getProfileImageUrl()).bitmapTransform(new RoundedCornersTransformation(mContext, 10, 0,
                RoundedCornersTransformation.CornerType.ALL)).into(ivProfileImg);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserName, tvText, tvScreenName, tvRelativeTime;
        public ImageView ivProfileImg;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTime);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);

            tvText = (TextView) itemView.findViewById(R.id.tvText);
            ivProfileImg = (ImageView) itemView.findViewById(R.id.ivProfileImg);
        }
    }

}