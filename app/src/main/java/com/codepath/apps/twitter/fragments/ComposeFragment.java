package com.codepath.apps.twitter.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.activities.TimelineActivity;
import com.codepath.apps.twitter.network.TwitterClient;
import com.codepath.apps.twitter.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ComposeFragment extends DialogFragment {

    private EditText etTweetText;
    private TextView tvCharCount;
    private Button btnPost;
    private TwitterClient client;

    public static int TWEET_CHAR_LIMIT = 140;

    public ComposeFragment() {}

    public static ComposeFragment newInstance(String title) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("Compose tweet", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etTweetText = (EditText) view.findViewById(R.id.etTweetText);
        tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);
        btnPost = (Button) view.findViewById(R.id.btnPost);
        ImageView ivComposeProfile = (ImageView) view.findViewById(R.id.ivComposeProfile);
        ImageView ivComposeCancel = (ImageView) view.findViewById(R.id.ivComposeCancel);

        ivComposeCancel.setOnClickListener(v -> {
            InputMethodManager imm =(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Intent intent = new Intent(getActivity(), TimelineActivity.class);
            startActivity(intent);
        });

        if(!TextUtils.isEmpty(Utils.profileImageUrl)) {
            Glide.with(getContext()).load(Utils.profileImageUrl).bitmapTransform(new RoundedCornersTransformation(getContext(), 10, 0)).into(ivComposeProfile);
        }

        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        etTweetText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        etTweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                tvCharCount.setText(String.valueOf(TWEET_CHAR_LIMIT - s.length()));
                if(s.length() > 0 && s.length() <= TWEET_CHAR_LIMIT){
                    btnPost.setEnabled(true);
                    btnPost.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }else{
                    btnPost.setEnabled(false);
                    btnPost.getBackground().setAlpha(128);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Bundle bundle = this.getArguments();
        String replyId = "";
        if (bundle != null) {
            etTweetText.append(bundle.getString("screenName", ""));
            replyId = bundle.getString("replyId", "");
            String receivedBody = bundle.getString("body", "");
            String titleOfPage = bundle.getString("titleOfPage", "");

            if(!TextUtils.isEmpty(titleOfPage)) {
                etTweetText.append(titleOfPage + " ");
            }

            if(!TextUtils.isEmpty(receivedBody)) {
                etTweetText.append(receivedBody);
            }
        }

        final String idToReply = replyId;

        client = TwitterApplication.getRestClient();

        btnPost.setOnClickListener(arg0 -> {

            boolean isReply = false;

            if(!TextUtils.isEmpty(idToReply)) {
                isReply = true;
            }

            client.composeTweet(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    InputMethodManager imm =(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Intent intent = new Intent(getActivity(), TimelineActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    dismiss();
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            }, etTweetText.getText().toString(), isReply, idToReply);
        });
    }
}
