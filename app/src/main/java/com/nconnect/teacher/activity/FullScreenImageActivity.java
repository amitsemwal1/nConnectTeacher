package com.nconnect.teacher.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import com.nconnect.teacher.R;

public class FullScreenImageActivity extends AppCompatActivity {

    public String image_url;
    PhotoView imageView;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        backButton = findViewById(R.id.backButton);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        image_url = intent.getStringExtra("image_url");


        //listener to handle video back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        try {

            Glide.with(this)
                    .load(image_url)
                    .into(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}