package com.codepath.apps.twitter.network;

import android.content.Context;
import android.text.TextUtils;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "pZ4DPc1KhL6sMRh3xVdFIKQLR";       // Change this
	public static final String REST_CONSUMER_SECRET = "beX5feUrpWn7zpMCcRxzvSmDlHtXij9yl0w8L52D6tsCApnBga"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler, String max_id) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		if(TextUtils.isEmpty(max_id)) {
			params.put("since_id", 1);
		}
		if(!TextUtils.isEmpty(max_id)) {
			params.put("max_id", max_id);
		}
        getClient().get(apiUrl, params, handler);
	}

	public void composeTweet(AsyncHttpResponseHandler  handler, String tweetBody){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetBody);
		getClient().post(apiUrl, params, handler);
	}

	public void deleteTweet(AsyncHttpResponseHandler  handler, String tweetId){
		String apiUrl = getApiUrl("statuses/destroy/" + tweetId + ".json");
		getClient().post(apiUrl, handler);
	}

	public void verifyCredentials(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, handler);
	}
}