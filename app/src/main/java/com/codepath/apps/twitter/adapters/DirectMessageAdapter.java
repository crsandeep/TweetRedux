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

import butterknife.BindView;
import butterknife.ButterKnife;
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
        return new ViewHolder(tweetView);

    }

    @Override
    public void onBindViewHolder(DirectMessageAdapter.ViewHolder viewHolder, int position) {
        final DirectMessage message = mMessages.get(position);
        User otherUser;
        String text = "";
        if(!message.getRecipientUser().getScreenName().equals(Utils.screenName)){
            text = "Me: ";
            otherUser = message.getRecipientUser();
        }else{
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

        @BindView(R.id.tvUserName) TextView tvUserName;
        @BindView(R.id.tvText) TextView tvText;
        @BindView(R.id.tvScreenName) TextView tvScreenName;
        @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
        @BindView(R.id.ivProfileImg) ImageView ivProfileImg;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}