package com.nconnect.teacher.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.helper.SlideshowDialogFragment;
import com.nconnect.teacher.model.MultipleImageVideo;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

public class MultiImageVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MultiImageVideoAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;
    List<MultipleImageVideo> images = Collections.emptyList();
    private String sessionIdValue = "";
    private String TAG_TYPE = "";

    public MultiImageVideoAdapter(Context context, List<MultipleImageVideo> list, String TAG_TYPE) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.images = list;
        this.TAG_TYPE = TAG_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.multiple_images_item, parent, false);
        MyHolderImageVideo holder = new MyHolderImageVideo(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        setViews(position, holder);
    }

    private void setViews(int position, RecyclerView.ViewHolder holder) {
        MyHolderImageVideo myHolder = (MyHolderImageVideo) holder;
        if (images.size() != 0) {
            if (!images.get(position).getType().isEmpty()) {
                if (images.size() == 1) {
                    myHolder.containerSingleImage.setVisibility(View.VISIBLE);
                    myHolder.containerDoubleImage.setVisibility(View.GONE);
                    myHolder.containerTribleImage.setVisibility(View.GONE);
                    myHolder.containerFourImage.setVisibility(View.GONE);
                    if (images.get(0).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivFirstContainerImage.setVisibility(View.VISIBLE);
                        myHolder.vvFirstContainerVideo.setVisibility(View.GONE);
                        myHolder.conOneVideo.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(0).getPath(), myHolder.ivFirstContainerImage);
                    } else {
                        myHolder.conOneVideo.setVisibility(View.VISIBLE);
                        myHolder.ivFirstContainerImage.setVisibility(View.GONE);
                        myHolder.vvFirstContainerVideo.setVisibility(View.VISIBLE);
                        myHolder.vvFirstContainerVideo.setVideoURI(Uri.parse(images.get(0).getPath()));
                        myHolder.vvFirstContainerVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvFirstContainerVideo.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                } else if (images.size() == 2) {
                    myHolder.containerSingleImage.setVisibility(View.GONE);
                    myHolder.containerDoubleImage.setVisibility(View.VISIBLE);
                    myHolder.containerTribleImage.setVisibility(View.GONE);
                    myHolder.containerFourImage.setVisibility(View.GONE);

                    if (images.get(0).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivSecondContainerFirst.setVisibility(View.VISIBLE);
                        myHolder.vvSecondContainerFirst.setVisibility(View.GONE);
                        myHolder.conTwoVIdeoOne.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(0).getPath(), myHolder.ivSecondContainerFirst);
                    } else {
                        myHolder.conTwoVIdeoOne.setVisibility(View.VISIBLE);
                        myHolder.ivSecondContainerFirst.setVisibility(View.GONE);
                        myHolder.vvSecondContainerFirst.setVisibility(View.VISIBLE);
                        myHolder.vvSecondContainerFirst.setVideoURI(Uri.parse(images.get(0).getPath()));
                        myHolder.vvSecondContainerFirst.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvSecondContainerFirst.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }

                    if (images.get(1).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivSecondContainerSecond.setVisibility(View.VISIBLE);
                        myHolder.vvSecondContainerSecond.setVisibility(View.GONE);
                        myHolder.conTwoVIdeoTwo.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(1).getPath(), myHolder.ivSecondContainerSecond);
                    } else {
                        myHolder.conTwoVIdeoTwo.setVisibility(View.VISIBLE);
                        myHolder.ivSecondContainerSecond.setVisibility(View.GONE);
                        myHolder.vvSecondContainerSecond.setVisibility(View.VISIBLE);
                        myHolder.vvSecondContainerSecond.setVideoURI(Uri.parse(images.get(1).getPath()));
                        myHolder.vvSecondContainerSecond.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvSecondContainerFirst.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }

                } else if (images.size() == 3) {
                    myHolder.containerSingleImage.setVisibility(View.GONE);
                    myHolder.containerDoubleImage.setVisibility(View.GONE);
                    myHolder.containerTribleImage.setVisibility(View.VISIBLE);
                    myHolder.containerFourImage.setVisibility(View.GONE);

                    if (images.get(0).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivThirdContainerFirst.setVisibility(View.VISIBLE);
                        myHolder.vvThirdContainerFirst.setVisibility(View.GONE);
                        myHolder.conThreeVideoOne.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(0).getPath(), myHolder.ivThirdContainerFirst);
                    } else {
                        myHolder.conThreeVideoOne.setVisibility(View.VISIBLE);
                        myHolder.ivThirdContainerFirst.setVisibility(View.GONE);
                        myHolder.vvThirdContainerFirst.setVisibility(View.VISIBLE);
                        myHolder.vvThirdContainerFirst.setVideoURI(Uri.parse(images.get(0).getPath()));
                        myHolder.vvThirdContainerFirst.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvThirdContainerFirst.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                    if (images.get(1).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.conThreeVideoTwo.setVisibility(View.GONE);
                        myHolder.ivThirdContainerSecond.setVisibility(View.VISIBLE);
                        myHolder.vvThirdContainerSecond.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(1).getPath(), myHolder.ivThirdContainerSecond);
                    } else {
                        myHolder.conThreeVideoTwo.setVisibility(View.VISIBLE);
                        myHolder.ivThirdContainerSecond.setVisibility(View.GONE);
                        myHolder.vvThirdContainerSecond.setVisibility(View.VISIBLE);
                        myHolder.vvThirdContainerSecond.setVideoURI(Uri.parse(images.get(1).getPath()));
                        myHolder.vvThirdContainerSecond.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvThirdContainerSecond.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }

                    if (images.get(2).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivThirdContainerThird.setVisibility(View.VISIBLE);
                        myHolder.conThreeVideoThree.setVisibility(View.GONE);
                        myHolder.vvThirdContainerThird.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(2).getPath(), myHolder.ivThirdContainerThird);
                    } else {
                        myHolder.conThreeVideoThree.setVisibility(View.VISIBLE);
                        myHolder.ivThirdContainerThird.setVisibility(View.GONE);
                        myHolder.vvThirdContainerThird.setVisibility(View.VISIBLE);
                        myHolder.vvThirdContainerThird.setVideoURI(Uri.parse(images.get(2).getPath()));
                        myHolder.vvThirdContainerThird.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvThirdContainerThird.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }

                } else if (images.size() == 4) {
                    myHolder.containerSingleImage.setVisibility(View.GONE);
                    myHolder.containerDoubleImage.setVisibility(View.GONE);
                    myHolder.containerTribleImage.setVisibility(View.GONE);
                    myHolder.containerFourImage.setVisibility(View.VISIBLE);
                    if (images.get(0).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivFourthContainerFirst.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerFirst.setVisibility(View.GONE);
                        myHolder.conFourVideOne.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(0).getPath(), myHolder.ivFourthContainerFirst);
                    } else {
                        myHolder.conFourVideOne.setVisibility(View.VISIBLE);
                        myHolder.ivFourthContainerFirst.setVisibility(View.GONE);
                        myHolder.vvFourthContainerFirst.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerFirst.setVideoURI(Uri.parse(images.get(0).getPath()));
                        myHolder.vvFourthContainerFirst.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvFourthContainerFirst.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                    if (images.get(1).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivFourthContainerSecond.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerSecond.setVisibility(View.GONE);
                        myHolder.conFourVideoTwo.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(1).getPath(), myHolder.ivFourthContainerSecond);
                    } else {
                        myHolder.conFourVideoTwo.setVisibility(View.VISIBLE);
                        myHolder.ivFourthContainerSecond.setVisibility(View.GONE);
                        myHolder.vvFourthContainerSecond.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerSecond.setVideoURI(Uri.parse(images.get(1).getPath()));
                        myHolder.vvFourthContainerSecond.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvFourthContainerSecond.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                    if (images.get(2).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.conFourVideoThree.setVisibility(View.GONE);
                        myHolder.ivFourthContainerThird.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerThird.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(2).getPath(), myHolder.ivFourthContainerThird);
                    } else {
                        myHolder.conFourVideoThree.setVisibility(View.VISIBLE);
                        myHolder.ivFourthContainerThird.setVisibility(View.GONE);
                        myHolder.vvFourthContainerThird.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerThird.setVideoURI(Uri.parse(images.get(2).getPath()));
                        myHolder.vvFourthContainerThird.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvFourthContainerThird.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                    if (images.get(3).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivFourthContainerFourth.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerFourth.setVisibility(View.GONE);
                        myHolder.conFourVideoFour.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(3).getPath(), myHolder.ivFourthContainerFourth);
                    } else {
                        myHolder.conFourVideoFour.setVisibility(View.VISIBLE);
                        myHolder.ivFourthContainerFourth.setVisibility(View.GONE);
                        myHolder.vvFourthContainerFourth.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerFourth.setVideoURI(Uri.parse(images.get(3).getPath()));

                        myHolder.vvFourthContainerFourth.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvFourthContainerFourth.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                    myHolder.tvMulitpleImage.setText("");
                } else {
                    myHolder.containerSingleImage.setVisibility(View.GONE);
                    myHolder.containerDoubleImage.setVisibility(View.GONE);
                    myHolder.containerTribleImage.setVisibility(View.GONE);
                    myHolder.containerFourImage.setVisibility(View.VISIBLE);
                    if (images.get(0).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivFourthContainerFirst.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerFirst.setVisibility(View.GONE);
                        myHolder.conFourVideOne.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(0).getPath(), myHolder.ivFourthContainerFirst);

                    } else {
                        myHolder.conFourVideOne.setVisibility(View.VISIBLE);
                        myHolder.ivFourthContainerFirst.setVisibility(View.GONE);
                        myHolder.vvFourthContainerFirst.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerFirst.setVideoURI(Uri.parse(images.get(0).getPath()));
                        myHolder.vvFourthContainerFirst.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvFourthContainerFirst.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                    if (images.get(1).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivFourthContainerSecond.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerSecond.setVisibility(View.GONE);
                        myHolder.conFourVideoTwo.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(1).getPath(), myHolder.ivFourthContainerSecond);
                    } else {
                        myHolder.conFourVideoTwo.setVisibility(View.VISIBLE);
                        myHolder.ivFourthContainerSecond.setVisibility(View.GONE);
                        myHolder.vvFourthContainerSecond.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerSecond.setVideoURI(Uri.parse(images.get(1).getPath()));
                        myHolder.vvFourthContainerSecond.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvFourthContainerSecond.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                    if (images.get(2).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.conFourVideoThree.setVisibility(View.GONE);
                        myHolder.ivFourthContainerThird.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerThird.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(2).getPath(), myHolder.ivFourthContainerThird);
                    } else {
                        myHolder.conFourVideoThree.setVisibility(View.VISIBLE);
                        myHolder.ivFourthContainerThird.setVisibility(View.GONE);
                        myHolder.vvFourthContainerThird.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerThird.setVideoURI(Uri.parse(images.get(2).getPath()));
                        myHolder.vvFourthContainerThird.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvFourthContainerThird.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                    if (images.get(3).getType().equalsIgnoreCase(Constants.IMAGE)) {
                        myHolder.ivFourthContainerFourth.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerFourth.setVisibility(View.GONE);
                        myHolder.conFourVideoFour.setVisibility(View.GONE);
                        loadProfileImageusingPicasso(images.get(3).getPath(), myHolder.ivFourthContainerFourth);
                    } else {
                        myHolder.conFourVideoFour.setVisibility(View.VISIBLE);
                        myHolder.ivFourthContainerFourth.setVisibility(View.GONE);
                        myHolder.vvFourthContainerFourth.setVisibility(View.VISIBLE);
                        myHolder.vvFourthContainerFourth.setVideoURI(Uri.parse(images.get(3).getPath()));

                        myHolder.vvFourthContainerFourth.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                myHolder.vvFourthContainerFourth.start();
                                Utils.enableSound(0, mp, context);
                            }
                        });
                    }
                    int size = images.size() - 4;
                    myHolder.tvMulitpleImage.setText("+" + String.valueOf(size));
                    myHolder.tvMulitpleImage.setBackgroundColor(context.getResources().getColor(R.color.multi_imagebackround));
                }
            }
        }
    }

    private void loadProfileImageusingPicasso(String path, ImageView imageView) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");

        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        GlideUrl glideUrl = new GlideUrl(path, new LazyHeaders.Builder().addHeader("Cookie", sessionIdValue).build());
        Log.e("TAg", "path : " + path);
        if (path.isEmpty()) {
            Picasso.get().load(R.drawable.ic_placeholder_noimage).error(R.drawable.ic_placeholder_noimage)
                    .fit().centerCrop().into(imageView);
        } else {
            if (TAG_TYPE.equalsIgnoreCase("previewStory")) {
                Picasso.get().load(new File(path)).fit().centerCrop().error(R.drawable.ic_placeholder_noimage).into(imageView);
            } else {
                Glide.with(context).load(glideUrl).into(imageView);
            }
        }

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    class MyHolderImageVideo extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout containerSingleImage, containerDoubleImage, containerTribleImage, containerFourImage;
        ImageView ivFirstContainerImage;
        VideoView vvFirstContainerVideo;
        ImageView ivSecondContainerFirst, ivSecondContainerSecond;
        VideoView vvSecondContainerFirst, vvSecondContainerSecond;
        ImageView ivThirdContainerFirst, ivThirdContainerThird, ivThirdContainerSecond;
        VideoView vvThirdContainerFirst, vvThirdContainerThird, vvThirdContainerSecond;
        ImageView ivFourthContainerFirst, ivFourthContainerSecond, ivFourthContainerThird, ivFourthContainerFourth;
        VideoView vvFourthContainerFirst, vvFourthContainerSecond, vvFourthContainerThird, vvFourthContainerFourth;
        RelativeLayout conFourVideOne, conFourVideoTwo, conFourVideoThree, conFourVideoFour;
        RelativeLayout conThreeVideoOne, conThreeVideoTwo, conThreeVideoThree;
        RelativeLayout conOneVideo;
        RelativeLayout conTwoVIdeoOne, conTwoVIdeoTwo;
        TextView tvMulitpleImage;

        public MyHolderImageVideo(View itemView) {
            super(itemView);
            containerSingleImage = (LinearLayout) itemView.findViewById(R.id.singleImageContainer);
            containerDoubleImage = (LinearLayout) itemView.findViewById(R.id.twoImageContainer);
            containerTribleImage = (LinearLayout) itemView.findViewById(R.id.threeImageContainer);
            containerFourImage = (LinearLayout) itemView.findViewById(R.id.fourImageContainer);
            ivFirstContainerImage = (ImageView) itemView.findViewById(R.id.singleImage);
            vvFirstContainerVideo = (VideoView) itemView.findViewById(R.id.singleVideo);
            ivSecondContainerFirst = (ImageView) itemView.findViewById(R.id.twoImageOne);
            ivSecondContainerSecond = (ImageView) itemView.findViewById(R.id.twoImageTwo);
            vvSecondContainerFirst = (VideoView) itemView.findViewById(R.id.twoVideoOne);
            vvSecondContainerSecond = (VideoView) itemView.findViewById(R.id.twoVideoTwo);
            ivThirdContainerFirst = (ImageView) itemView.findViewById(R.id.threeImageOne);
            ivThirdContainerSecond = (ImageView) itemView.findViewById(R.id.threeImageTwo);
            ivThirdContainerThird = (ImageView) itemView.findViewById(R.id.threeImageThree);
            vvThirdContainerFirst = (VideoView) itemView.findViewById(R.id.threeVideoOne);
            vvThirdContainerSecond = (VideoView) itemView.findViewById(R.id.threeVideoTwo);
            vvThirdContainerThird = (VideoView) itemView.findViewById(R.id.threeVideoThree);
            ivFourthContainerFirst = (ImageView) itemView.findViewById(R.id.fourImageOne);
            ivFourthContainerSecond = (ImageView) itemView.findViewById(R.id.fourImageTwo);
            ivFourthContainerThird = (ImageView) itemView.findViewById(R.id.fourImageThree);
            ivFourthContainerFourth = (ImageView) itemView.findViewById(R.id.fourImageFour);
            vvFourthContainerFirst = (VideoView) itemView.findViewById(R.id.fourVideoOne);
            vvFourthContainerSecond = (VideoView) itemView.findViewById(R.id.fourVideoTwo);
            vvFourthContainerThird = (VideoView) itemView.findViewById(R.id.fourVideoThree);
            vvFourthContainerFourth = (VideoView) itemView.findViewById(R.id.fourVideoFour);
            conFourVideOne = (RelativeLayout) itemView.findViewById(R.id.fourVideoOneContainer);
            conFourVideoTwo = (RelativeLayout) itemView.findViewById(R.id.fourVideoTwoContainer);
            conFourVideoThree = (RelativeLayout) itemView.findViewById(R.id.fourVideoThreeContainer);
            conFourVideoFour = (RelativeLayout) itemView.findViewById(R.id.fourVideoFourContainer);
            conThreeVideoOne = (RelativeLayout) itemView.findViewById(R.id.threeVideoOneContainer);
            conThreeVideoTwo = (RelativeLayout) itemView.findViewById(R.id.threeVideoTwoContainer);
            conThreeVideoThree = (RelativeLayout) itemView.findViewById(R.id.threeVideoThreeCotainer);
            tvMulitpleImage = (TextView) itemView.findViewById(R.id.fourImageText);
            conFourVideOne = (RelativeLayout) itemView.findViewById(R.id.fourVideoOneContainer);
            conFourVideoTwo = (RelativeLayout) itemView.findViewById(R.id.fourVideoTwoContainer);
            conFourVideoThree = (RelativeLayout) itemView.findViewById(R.id.fourVideoThreeContainer);
            conFourVideoFour = (RelativeLayout) itemView.findViewById(R.id.fourVideoFourContainer);
            conThreeVideoOne = (RelativeLayout) itemView.findViewById(R.id.threeVideoOneContainer);
            conThreeVideoTwo = (RelativeLayout) itemView.findViewById(R.id.threeVideoTwoContainer);
            conThreeVideoThree = (RelativeLayout) itemView.findViewById(R.id.threeVideoThreeCotainer);
            tvMulitpleImage = (TextView) itemView.findViewById(R.id.fourImageText);
            conOneVideo = (RelativeLayout) itemView.findViewById(R.id.singleVideoLayout);
            conTwoVIdeoOne = (RelativeLayout) itemView.findViewById(R.id.twoVideoOneLayout);
            conTwoVIdeoTwo = (RelativeLayout) itemView.findViewById(R.id.twoVideoTwoLayout);
            containerSingleImage.setOnClickListener(this);
            ivSecondContainerFirst.setOnClickListener(this);
            vvSecondContainerFirst.setOnClickListener(this);
            ivSecondContainerSecond.setOnClickListener(this);
            vvSecondContainerSecond.setOnClickListener(this);
            ivThirdContainerFirst.setOnClickListener(this);
            vvThirdContainerThird.setOnClickListener(this);
            ivThirdContainerSecond.setOnClickListener(this);
            vvThirdContainerSecond.setOnClickListener(this);
            ivThirdContainerThird.setOnClickListener(this);
            vvThirdContainerThird.setOnClickListener(this);
            ivFourthContainerFirst.setOnClickListener(this);
            vvFourthContainerFirst.setOnClickListener(this);
            ivFourthContainerSecond.setOnClickListener(this);
            vvFourthContainerSecond.setOnClickListener(this);
            ivFourthContainerThird.setOnClickListener(this);
            vvThirdContainerThird.setOnClickListener(this);
            ivFourthContainerFourth.setOnClickListener(this);
            vvFourthContainerFourth.setOnClickListener(this);
            conOneVideo.setOnClickListener(this);
            conTwoVIdeoTwo.setOnClickListener(this);
            conTwoVIdeoOne.setOnClickListener(this);
            conThreeVideoOne.setOnClickListener(this);
            conThreeVideoTwo.setOnClickListener(this);
            conThreeVideoThree.setOnClickListener(this);
            conFourVideOne.setOnClickListener(this);
            conFourVideoTwo.setOnClickListener(this);
            conFourVideoThree.setOnClickListener(this);
            conFourVideoFour.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = 0;
            if (v == ivSecondContainerSecond || v == conTwoVIdeoTwo) {
                position = 1;
            } else if (v == ivThirdContainerSecond || v == conThreeVideoTwo) {
                position = 1;
            } else if (v == ivThirdContainerThird || v == conThreeVideoThree) {
                position = 2;
            } else if (v == ivFourthContainerSecond || v == conFourVideoTwo) {
                position = 1;
            } else if (v == ivFourthContainerThird || v == conFourVideoThree) {
                position = 2;
            } else if (v == ivFourthContainerFourth || v == conFourVideoFour) {
                position = 3;
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("images", (Serializable) images);
            bundle.putInt("position", position);
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
            newFragment.setArguments(bundle);
            newFragment.show(transaction, "slideshow");
        }
    }

}
