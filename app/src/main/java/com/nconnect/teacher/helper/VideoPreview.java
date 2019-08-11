package com.nconnect.teacher.helper;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.nconnect.teacher.R;
import com.nconnect.teacher.util.Constants;

public class VideoPreview extends AppCompatActivity {

    private static final String TAG = VideoView.class.getSimpleName();
    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_preview_activity);
        intializeViews();
    }

    private void intializeViews() {
        videoView = (VideoView) findViewById(R.id.videoPreview);
        Intent intent = getIntent();
        videoPath = intent.getStringExtra(Constants.VIDEO);
        position = intent.getIntExtra(Constants.POSITION, 0);
        // Set the media controller buttons
        if (mediaController == null) {
            mediaController = new MediaController(this);
            // Set the videoView that acts as the anchor for the MediaController.
            mediaController.setAnchorView(videoView);
            // Set MediaController for VideoView
            videoView.setMediaController(mediaController);
        }
        try {
            // ID of video file.
            /*int id = this.getRawResIdByName("myvideo");
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + id));*/
            videoView.setVideoPath(videoPath);
            if (position == 0) {
                videoView.start();
            } else {
                videoView.seekTo(position);
                videoView.start();
            }
        } catch (Exception e) {
//            Log.e(TAG, "Error" + e.getMessage());
            e.printStackTrace();
        }
        videoView.requestFocus();
        // When the video file ready for playback.
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                }
                // When video Screen change size.
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        // Re-Set the videoView that acts as the anchor for the MediaController
                        mediaController = new MediaController(VideoPreview.this);
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
