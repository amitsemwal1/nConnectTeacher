package com.nconnect.teacher.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.io.File;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import com.nconnect.teacher.R;
import com.nconnect.teacher.util.Constants;

public class FullScreenPreview extends DialogFragment {

    private String imageUri;
    private ImageView ivFullScreen;
    private String sessionIdValue;
    private String code;
    private JzvdStd videoFullscreen;

    public static FullScreenPreview newInstance() {
        FullScreenPreview f = new FullScreenPreview();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.image_fullscreen_preview, container, false);
        imageUri = getArguments().getString("image");
        code = getArguments().getString("code");
        ivFullScreen = (ImageView) view.findViewById(R.id.image_preview);
        videoFullscreen = (JzvdStd) view.findViewById(R.id.videoplayer);
        videoFullscreen.WIFI_TIP_DIALOG_SHOWED = true;
        videoFullscreen.fullscreenButton.setVisibility(View.GONE);
        if (code.equalsIgnoreCase("attach")) {
            ivFullScreen.setVisibility(View.VISIBLE);
            videoFullscreen.setVisibility(View.GONE);
            loadImageView();
        } else if (code.equalsIgnoreCase("events")) {
            ivFullScreen.setVisibility(View.VISIBLE);
            videoFullscreen.setVisibility(View.GONE);
            loadImageUsingGlide(imageUri, ivFullScreen);
        } else if (code.equalsIgnoreCase("video")) {
            ivFullScreen.setVisibility(View.GONE);
            videoFullscreen.setVisibility(View.VISIBLE);
            videoFullscreen.setUp(imageUri, "", Jzvd.SCREEN_WINDOW_NORMAL);
        }
        return view;
    }

    private void loadImageView() {
//        Picasso.get().load(new File(imageUri)).into(ivFullScreen);
        Glide.with(getContext())
                .load(new File(imageUri))
                .into(ivFullScreen);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    private void loadImageUsingGlide(@NonNull String path, ImageView imageView) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        if (path.isEmpty()) {
            Glide.with(this).load(R.drawable.ncp_placeholder)
                    .into(imageView);
        } else {
            GlideUrl glideUrl = new GlideUrl(path, new LazyHeaders.Builder().addHeader("Cookie", sessionIdValue).build());
            Glide.with(this).load(glideUrl).into(imageView);
        }
    }
}
