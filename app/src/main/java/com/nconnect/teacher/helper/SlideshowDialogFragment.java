package com.nconnect.teacher.helper;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.gson.Gson;
import com.jsibbold.zoomage.ZoomageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import com.nconnect.teacher.R;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.MyApplication;

public class SlideshowDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<String> images;
    //    private ArrayList<MultipleImageVideo> imageVideos;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount;
    private int selectedPosition = 0;
    private ImageView ivDownloadImage, ivOnBackPressed;
    private boolean writtenToDisk = false;
    private String fileName = "";

    public static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
//        imageVideos = new ArrayList<>();
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        ivDownloadImage = (ImageView) v.findViewById(R.id.downloadImage);
        ivDownloadImage.setOnClickListener(this);
        ivOnBackPressed = (ImageView) v.findViewById(R.id.navigationArrow);
        ivOnBackPressed.setOnClickListener(this);
        images = (ArrayList<String>) getArguments().getSerializable("images");
//        imageVideos = (ArrayList<MultipleImageVideo>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");
        myViewPagerAdapter = new MyViewPagerAdapter(getContext());
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);
        requestMultiplePermissions();
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Permission granted");
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                            Toast.makeText(getActivity(), "Please accept this permission to use this application", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onClick(View v) {

        if (v == ivDownloadImage) {
            try {
                if (!images.get(selectedPosition).endsWith("mp4")) {
                    Toast.makeText(getActivity(), "Downloading...", Toast.LENGTH_LONG).show();
                    /*Intent intent = new Intent(getActivity(), DownloadNotificationService.class);
                    intent.putExtra("file_url", images.get(selectedPosition));
                    getActivity().startService(intent);*/
                    new DownloadManager(images.get(selectedPosition), getActivity(), Constants.IMAGE);
                } else {
                    Toast.makeText(getActivity(), "Downloading...", Toast.LENGTH_LONG).show();
                    /*Intent intent = new Intent(getActivity(), DownloadNotificationService.class);
                    intent.putExtra("file_url", images.get(selectedPosition));
                    getActivity().startService(intent);*/
                    new DownloadManager(images.get(selectedPosition), getActivity(), Constants.VIDEO);
                }
            } catch (Exception e) {
//                Log.e(TAG, "exception : " + e);
                Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        } else if (v == ivOnBackPressed) {
            dismiss();
            getActivity().getSupportFragmentManager().beginTransaction().detach(SlideshowDialogFragment.this).commit();
        }

    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        private ZoomageView imageViewPreview;
        private Context context;
        private JzvdStd videoView;
        private SimpleExoPlayerView exoPlayerView;
        private SimpleExoPlayer exoPlayer;


        public MyViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
//            Log.e(TAG, " image url list : " + new Gson().toJson(images));
            imageViewPreview = (ZoomageView) view.findViewById(R.id.image_preview);
            videoView = (JzvdStd) view.findViewById(R.id.videoplayer);
            videoView.WIFI_TIP_DIALOG_SHOWED = true;
            videoView.fullscreenButton.setVisibility(View.GONE);
            exoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.exo_player_view);
            if (!images.get(position).endsWith("mp4")) {
                exoPlayerView.setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
                imageViewPreview.setVisibility(View.VISIBLE);
                String image = images.get(position);
                Glide.with(context).load(image).into(imageViewPreview);
            } else {
                imageViewPreview.setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
                exoPlayerView.setVisibility(View.VISIBLE);
                try {
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                    DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(images.get(position)), dataSourceFactory, extractorsFactory, null, null);
                    exoPlayerView.setPlayer(exoPlayer);
                    exoPlayer.setPlayWhenReady(true);
                    exoPlayer.prepare(mediaSource);
                } catch (Exception e) {
//                    Log.e("MainAcvtivity", " exoplayer error " + e.toString());
                }
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getContext(), getTheme()) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                if (Jzvd.backPress()) {
                    return;
                }
                if (images.get(selectedPosition).endsWith("mp4")) {
                    myViewPagerAdapter.exoPlayer.stop(true);
                    myViewPagerAdapter.exoPlayer.release();
                }
                super.onBackPressed();
                getActivity().getSupportFragmentManager().beginTransaction().detach(SlideshowDialogFragment.this).commit();
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
        if (images.get(selectedPosition).endsWith("mp4")) {
            myViewPagerAdapter.exoPlayer.stop(true);
            myViewPagerAdapter.exoPlayer.release();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (images.get(selectedPosition).endsWith("mp4")) {
            myViewPagerAdapter.exoPlayer.stop(true);
            myViewPagerAdapter.exoPlayer.release();
        }
    }
}
