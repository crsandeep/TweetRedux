package com.codepath.apps.twitter.network;

import android.content.Context;
import android.text.TextUtils;

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

	public void getHomeTimeline(AsyncHttpResponseHandler handler, Long id) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		if(id > 0) {
			params.put("max_id", id);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(AsyncHttpResponseHandler handler, Long id) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		if(id > 0) {
			params.put("max_id", id);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getUserTimeline(JsonHttpResponseHandler handler, String screenName, Long id) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		if(id > 0) {
			params.put("max_id", id);
		}
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}

	public void searchTweets(JsonHttpResponseHandler handler, String query, Long id) {
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("q", query);
		params.put("result_type", "recent");
		if(id > 0) {
			params.put("max_id", id);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getDirectMessages(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("direct_messages.json");
		RequestParams params = new RequestParams();
		getClient().get(apiUrl, params, handler);
	}

    public void sendDirectMessages(JsonHttpResponseHandler handler, String screen_name, String text) {
        String apiUrl = getApiUrl("direct_messages/new.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screen_name);
        params.put("text", text);
        getClient().post(apiUrl, params, handler);
    }

	public void getUserInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler, String screenName) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
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

	public void getFollowers(JsonHttpResponseHandler handler, String screenName, String cursor) {
		String apiUrl = getApiUrl("followers/list.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		params.put("count", 200);
        if(!TextUtils.isEmpty(cursor)) {
            params.put("cursor", cursor);
        }
		getClient().get(apiUrl, params, handler);
	}

	public void getFollowing(JsonHttpResponseHandler handler, String screenName, String cursor) {
		String apiUrl = getApiUrl("friends/list.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		params.put("count", 200);
        if(!TextUtils.isEmpty(cursor)) {
            params.put("cursor", cursor);
        }
		getClient().get(apiUrl, params, handler);
	}
}