package com.codepath.apps.twitter.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.codepath.apps.twitter.activities.ImageFullscreenActivity;
import com.codepath.apps.twitter.activities.ProfileActivity;
import com.codepath.apps.twitter.activities.SearchActivity;
import com.codepath.apps.twitter.adapters.PatternEditableBuilder;
import com.codepath.apps.twitter.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utils {

    public static String profileImageUrl = "";

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = getTimeString(dateMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    public static String getTimeString(long timeInMillis) {

        Date date = new Date(timeInMillis);

        StringBuffer dateStr = new StringBuffer();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar now = Calendar.getInstance();

        int days = daysBetween(calendar.getTime(), now.getTime());
        int minutes = hoursBetween(calendar.getTime(), now.getTime());
        int hours = minutes / 60;
        if (days == 0) {

            int second = minuteBetween(calendar.getTime(), now.getTime());
            if (minutes > 60) {

                if (hours >= 1 && hours <= 24) {
                    dateStr.append(hours).append("h");
                }

            } else {

                if (second <= 1) {
                    dateStr.append("Now");
                } else if (second > 1 && second <= 60) {
                    dateStr.append(second).append("s");
                } else if (second >= 60 && minutes <= 60) {
                    dateStr.append(minutes).append("m");
                }
            }
        } else if (hours > 24 && days <= 31) {
            dateStr.append(days).append("d");
        } else {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yy");
            dateStr = new StringBuffer(format.format(calendar.getTime()));
        }

        return dateStr.toString();
    }

    public static int minuteBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.SECOND_IN_MILLIS);
    }

    public static int hoursBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MINUTE_IN_MILLIS);
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.DAY_IN_MILLIS);
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void showFullScreenImage(Tweet tweet, Context mContext) {
        Intent intent = new Intent(mContext, ImageFullscreenActivity.class);
        String image = "";

        if (tweet.getEntities().getMedia() != null && tweet.getEntities().getMedia().size() > 0) {
            for (int i = 0; i < tweet.getEntities().getMedia().size(); i++) {
                if (tweet.getEntities().getMedia().get(i) != null) {
                    String mediaUrl = tweet.getEntities().getMedia().get(i).getMediaUrl();
                    if (!TextUtils.isEmpty(mediaUrl)) {
                        image = mediaUrl;
                        break;
                    }
                }
            }
        }
        intent.putExtra("image", image);
        mContext.startActivity(intent);
    }

    public static void showFullScreenImageForUrl(String url, Context mContext) {
        Intent intent = new Intent(mContext, ImageFullscreenActivity.class);
        intent.putExtra("image", url);
        mContext.startActivity(intent);
    }

    public static String formattedLikesAndRetweets(int count) {
        String formattedLikesAndRetweets = Integer.toString(count);
        if (count >= 1000) {
            formattedLikesAndRetweets = (count / 1000) + "K";
        }
        return formattedLikesAndRetweets;
    }

    public static void addSpanListener(Context context, TextView view) {
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.parseColor("#66d6ff"),
                        text -> {
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.putExtra("fromSpan", true);
                            intent.putExtra("screenName", text.substring(1));
                            context.startActivity(intent);
                        }).into(view);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), Color.parseColor("#66d6ff"),
                        text -> {
                            Intent intent = new Intent(context, SearchActivity.class);
                            intent.putExtra("query", text);
                            context.startActivity(intent);
                        }).into(view);
    }
}
