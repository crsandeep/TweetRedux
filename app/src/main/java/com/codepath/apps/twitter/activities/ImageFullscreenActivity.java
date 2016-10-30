package com.codepath.apps.twitter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.codepath.apps.twitter.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import uk.co.senab.photoview.PhotoView;

public class ImageFullscreenActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        //imageView = (ImageView) findViewById(R.id.imageView);

        PhotoView photoView = (PhotoView) findViewById(R.id.imageView);

        Intent intent = getIntent();
        String image = intent.getStringExtra("image");

        Glide.with(this).load(image).bitmapTransform(new RoundedCornersTransformation(this, 10, 0,
                RoundedCornersTransformation.CornerType.ALL)).into(photoView);

    }
}