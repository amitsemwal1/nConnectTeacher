package com.nconnect.teacher.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.nconnect.teacher.R;
import com.nconnect.teacher.helper.SlideshowDialogFragment;
import com.nconnect.teacher.util.Constants;

public class MultipleImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> images = Collections.emptyList();
    private Activity context;
    private LayoutInflater inflater;
    private String sessionIdValue = "";
    private String TAG_TYPE = "";

    public MultipleImageAdapter(Activity context, List<String> list, String TAG_TYPE) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.images = list;
        this.TAG_TYPE = TAG_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.multiple_images, parent, false);
        MyHolderImages holder = new MyHolderImages(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderImages myHolder = (MyHolderImages) holder;
        if (images.size() != 0) {
            if (images.size() == 1) {
                myHolder.containerSingleImage.setVisibility(View.VISIBLE);
                myHolder.containerDoubleImage.setVisibility(View.GONE);
                myHolder.containerTribleImage.setVisibility(View.GONE);
                myHolder.containerFourImage.setVisibility(View.GONE);
                if (images.get(0).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivFirstContainerImage);
                } else {
                    loadImageUsingGlide(images.get(0), myHolder.ivFirstContainerImage);
                }

            } else if (images.size() == 2) {
                myHolder.containerSingleImage.setVisibility(View.GONE);
                myHolder.containerDoubleImage.setVisibility(View.VISIBLE);
                myHolder.containerTribleImage.setVisibility(View.GONE);
                myHolder.containerFourImage.setVisibility(View.GONE);
                if (images.get(0).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivSecondContainerFirst);
                } else {
                    loadImageUsingGlide(images.get(0), myHolder.ivSecondContainerFirst);
                }
                if (images.get(1).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivSecondContainerSecond);
                } else {
                    loadImageUsingGlide(images.get(1), myHolder.ivSecondContainerSecond);
                }
            } else if (images.size() == 3) {
                myHolder.containerSingleImage.setVisibility(View.GONE);
                myHolder.containerDoubleImage.setVisibility(View.GONE);
                myHolder.containerTribleImage.setVisibility(View.VISIBLE);
                myHolder.containerFourImage.setVisibility(View.GONE);
                if (images.get(0).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivThirdContainerFirst);
                } else {
                    loadImageUsingGlide(images.get(0), myHolder.ivThirdContainerFirst);
                }
                if (images.get(1).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivThirdContainerSecond);
                } else {
                    loadImageUsingGlide(images.get(1), myHolder.ivThirdContainerSecond);
                }
                if (images.get(2).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivThirdContainerThird);
                } else {
                    loadImageUsingGlide(images.get(2), myHolder.ivThirdContainerThird);
                }
            } else if (images.size() == 4) {
                myHolder.containerSingleImage.setVisibility(View.GONE);
                myHolder.containerDoubleImage.setVisibility(View.GONE);
                myHolder.containerTribleImage.setVisibility(View.GONE);
                myHolder.containerFourImage.setVisibility(View.VISIBLE);
                if (images.get(0).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivFourthContainerFirst);
                } else {
                    loadImageUsingGlide(images.get(0), myHolder.ivFourthContainerFirst);
                }
                if (images.get(1).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivFourthContainerSecond);
                } else {
                    loadImageUsingGlide(images.get(1), myHolder.ivFourthContainerSecond);
                }
                if (images.get(2).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivFourthContainerThird);
                } else {
                    loadImageUsingGlide(images.get(2), myHolder.ivFourthContainerThird);
                }
                if (images.get(3).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivFourthContainerFourth);
                } else {
                    loadImageUsingGlide(images.get(3), myHolder.ivFourthContainerFourth);
                }
                myHolder.tvMulitpleImage.setText("");
            } else {
                myHolder.containerSingleImage.setVisibility(View.GONE);
                myHolder.containerDoubleImage.setVisibility(View.GONE);
                myHolder.containerTribleImage.setVisibility(View.GONE);
                myHolder.containerFourImage.setVisibility(View.VISIBLE);
                if (images.get(0).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivFourthContainerFirst);
                } else {
                    loadImageUsingGlide(images.get(0), myHolder.ivFourthContainerFirst);
                }
                if (images.get(1).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivFourthContainerSecond);
                } else {
                    loadImageUsingGlide(images.get(1), myHolder.ivFourthContainerSecond);
                }
                if (images.get(2).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivFourthContainerThird);
                } else {
                    loadImageUsingGlide(images.get(2), myHolder.ivFourthContainerThird);
                }
                if (images.get(3).endsWith("mp4")) {
                    handleVideoThumpNail(myHolder.ivFourthContainerFourth);
                } else {
                    loadImageUsingGlide(images.get(3), myHolder.ivFourthContainerFourth);
                }
                int size = images.size() - 4;
                myHolder.tvMulitpleImage.setText("+" + String.valueOf(size));
                myHolder.tvMulitpleImage.setBackgroundColor(context.getResources().getColor(R.color.multi_imagebackround));
            }

        }

    }

    private void loadImageUsingGlide(@NonNull String path, ImageView imageView) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, Context.MODE_PRIVATE);
                String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
                if (sessionId != "") {
                    sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
                }
                  if (path != null && path != "") {
                    if (TAG_TYPE.equalsIgnoreCase("previewStory")) {
                        Glide.with(context).load(new File(path)).into(imageView);
                    } else {
                        GlideUrl glideUrl = new GlideUrl(path, new LazyHeaders.Builder().addHeader("Cookie", sessionIdValue).build());
                        Glide.with(context).load(glideUrl).apply(new RequestOptions().placeholder(R.drawable.ncp_placeholder).centerCrop()).into(imageView);
                    }

                }
            }
        });
    }

    void handleVideoThumpNail(ImageView imageView) {

        try {
            //imageView.setImageBitmap(bit);
            Glide.with(context)
                    .load(R.drawable.ncp_vd_placeholder)
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.ncp_placeholder))
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
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

    public class MyHolderImages extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout containerSingleImage, containerDoubleImage, containerTribleImage, containerFourImage;
        ImageView ivFirstContainerImage;
        ImageView ivSecondContainerFirst, ivSecondContainerSecond;
        ImageView ivThirdContainerFirst, ivThirdContainerThird, ivThirdContainerSecond;
        ImageView ivFourthContainerFirst, ivFourthContainerSecond, ivFourthContainerThird, ivFourthContainerFourth;
        TextView tvMulitpleImage;

        public MyHolderImages(View itemView) {
            super(itemView);
            containerSingleImage = (LinearLayout) itemView.findViewById(R.id.singleImageContainer);
            containerDoubleImage = (LinearLayout) itemView.findViewById(R.id.twoImageContainer);
            containerTribleImage = (LinearLayout) itemView.findViewById(R.id.threeImageContainer);
            containerFourImage = (LinearLayout) itemView.findViewById(R.id.fourImageContainer);
            ivFirstContainerImage = (ImageView) itemView.findViewById(R.id.singleImage);
            ivFirstContainerImage.setOnClickListener(this);
            ivSecondContainerFirst = (ImageView) itemView.findViewById(R.id.twoImageOne);
            ivSecondContainerFirst.setOnClickListener(this);
            ivSecondContainerSecond = (ImageView) itemView.findViewById(R.id.twoImageTwo);
            ivSecondContainerSecond.setOnClickListener(this);
            ivThirdContainerFirst = (ImageView) itemView.findViewById(R.id.threeImageOne);
            ivThirdContainerFirst.setOnClickListener(this);
            ivThirdContainerSecond = (ImageView) itemView.findViewById(R.id.threeImageTwo);
            ivThirdContainerSecond.setOnClickListener(this);
            ivThirdContainerThird = (ImageView) itemView.findViewById(R.id.threeImageThree);
            ivThirdContainerThird.setOnClickListener(this);
            ivFourthContainerFirst = (ImageView) itemView.findViewById(R.id.fourImageOne);
            ivFourthContainerFirst.setOnClickListener(this);
            ivFourthContainerSecond = (ImageView) itemView.findViewById(R.id.fourImageTwo);
            ivFourthContainerSecond.setOnClickListener(this);
            ivFourthContainerThird = (ImageView) itemView.findViewById(R.id.fourImageThree);
            ivFourthContainerThird.setOnClickListener(this);
            ivFourthContainerFourth = (ImageView) itemView.findViewById(R.id.fourImageFour);
            ivFourthContainerFourth.setOnClickListener(this);
            tvMulitpleImage = (TextView) itemView.findViewById(R.id.fourImageText);

        }


        @Override
        public void onClick(View v) {
            int position = 0;
            if (v == ivSecondContainerSecond) {
                position = 1;
            } else if (v == ivThirdContainerSecond) {
                position = 1;
            } else if (v == ivThirdContainerThird) {
                position = 2;
            } else if (v == ivFourthContainerSecond) {
                position = 1;
            } else if (v == ivFourthContainerThird) {
                position = 2;
            } else if (v == ivFourthContainerFourth) {
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

