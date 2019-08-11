package com.nconnect.teacher.helper;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.nconnect.teacher.R;
import com.nconnect.teacher.util.Constants;

public class VideoPreviewDialog extends DialogFragment {

    private static final String TAG = VideoPreviewDialog.class.getSimpleName();
    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;
    private String videoPath;
    private Activity activity;

    public static VideoPreviewDialog newInstance() {
        VideoPreviewDialog dialog = new VideoPreviewDialog();
        return dialog;
    }

    public void setContext(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_video_preview, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        videoView = (VideoView) view.findViewById(R.id.dialog_video);
        videoPath = getArguments().getString(Constants.VIDEO);
        position = getArguments().getInt(Constants.POSITION);
        // Set the media controller buttons
        if (mediaController == null) {
            mediaController = new MediaController(activity);
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
                        mediaController = new MediaController(activity);
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                    }
                });
            }
        });
    }

}
