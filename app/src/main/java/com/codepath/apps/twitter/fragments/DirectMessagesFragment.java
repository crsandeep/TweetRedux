package com.codepath.apps.twitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.TwitterApplication;
import com.codepath.apps.twitter.adapters.DirectMessageAdapter;
import com.codepath.apps.twitter.models.DirectMessage;
import com.codepath.apps.twitter.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class DirectMessagesFragment extends Fragment {

    @BindView(R.id.rvMessages) RecyclerView rvMessages;

    private DirectMessageAdapter messageAdapter;
    private ArrayList<DirectMessage> messageList;

    TwitterClient client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_direct_messages, container, false);
        ButterKnife.bind(this, v);
        client = TwitterApplication.getRestClient();
        messageList = new ArrayList<>();
        messageAdapter = new DirectMessageAdapter(getContext(), messageList);
        rvMessages.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvMessages.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        rvMessages.setLayoutManager(layoutManager);
        populateDirectMessages();
        return v;
    }

    private void populateDirectMessages() {
        client.getDirectMessages(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                messageList.clear();
                messageList.addAll(DirectMessage.fromJsonArray(response));
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }
}
