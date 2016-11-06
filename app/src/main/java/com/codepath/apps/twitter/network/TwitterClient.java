package com.codepath.apps.twitter.network;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "pZ4DPc1KhL6sMRh3xVdFIKQLR";
	public static final String REST_CONSUMER_SECRET = "beX5feUrpWn7zpMCcRxzvSmDlHtXij9yl0w8L52D6tsCApnBga";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler, String type, Long id) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		if (type.equals("since_id")) {
			params.put("since_id", id);
			params.put("count", 25);
		} else {
			params.put(type, id);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(AsyncHttpResponseHandler handler, String type, Long id) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		if (type.equals("since_id")) {
			params.put("since_id", id);
			params.put("count", 25);
		} else {
			params.put(type, id);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void searchTweets(JsonHttpResponseHandler handler, String query, Long id) {
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("q", query);
		params.put("result_type", "popular");
		if(id > 0) {
			params.put("max_id", id);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getDirectMessages(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("direct_messages.json");
		RequestParams params = new RequestParams();
		params.put("count", 20);
		params.put("since_id", 1);
		getClient().get(apiUrl, null, handler);
	}

	public void getUserTimeline(JsonHttpResponseHandler handler, String screenName) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, handler);
	}

	public void composeTweet(AsyncHttpResponseHandler  handler, String tweetBody, boolean isReply, String idToReply){
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetBody);
		if(isReply) {
			params.put("in_reply_to_status_id", idToReply);
		}
		getClient().post(apiUrl, params, handler);
	}

	public void deleteTweet(AsyncHttpResponseHandler  handler, String tweetId){
		String apiUrl = getApiUrl("statuses/destroy/" + tweetId + ".json");
		getClient().post(apiUrl, handler);
	}

	public void reTweet(AsyncHttpResponseHandler handler, String tweetId) {
		String apiUrl = getApiUrl("statuses/retweet/" + tweetId +".json");
		getClient().post(apiUrl, handler);
	}

	public void unReTweet(AsyncHttpResponseHandler handler, String tweetId) {
		String apiUrl = getApiUrl("statuses/unretweet/" + tweetId +".json");
		getClient().post(apiUrl, handler);
	}

	public void like(AsyncHttpResponseHandler handler, String tweetId) {
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		getClient().post(apiUrl, params, handler);
	}

	public void unlike(AsyncHttpResponseHandler handler, String tweetId) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		getClient().post(apiUrl, params, handler);
	}
}