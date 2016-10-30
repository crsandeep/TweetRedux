package com.codepath.apps.twitter.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.activities.TimelineActivity;
import com.codepath.apps.twitter.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends BottomSheetDialogFragment {

    private EditText etTweetText;
    private TextView tvCharCount;
    private Button btnPost;
    private TwitterClient client;

    public static int TWEET_CHAR_LIMIT = 140;

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance(String title) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("Compose tweet", title);
        frag.setArguments(args);
        return frag;
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
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        etTweetText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
            public void afterTextChanged(Editable s) {

            }
        });

        client = TwitterApplication.getRestClient();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                client.composeTweet(new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Intent intent = new Intent(getActivity(), TimelineActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        dismiss();
                    }
                }, etTweetText.getText().toString());
            }
        });
    }
}
