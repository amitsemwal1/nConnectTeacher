package com.nconnect.teacher.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import com.nconnect.teacher.R;
import com.nconnect.teacher.helper.FileOpen;
import com.nconnect.teacher.helper.FullScreenPreview;
import com.nconnect.teacher.model.stories.Attachment;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.MyApplication;
import com.nconnect.teacher.util.Utils;

public class AttachmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private static final String TAG = AttachmentAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    List<Attachment> data = Collections.emptyList();
    int code = 0;
    int seekPosition = 0;

    public AttachmentAdapter(Context context, List<Attachment> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.attachment_items, parent, false);
        MyHolderAttachment holder = new MyHolderAttachment(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolderAttachment myHolder = (MyHolderAttachment) holder;
        Attachment attachment = data.get(position);

        if (attachment.getContentype().equalsIgnoreCase(Constants.IMAGE)) {
//            Log.e(TAG, "type : " + attachment.getContentype());
            myHolder.containerImage.setVisibility(View.VISIBLE);
            myHolder.containerVideo.setVisibility(View.GONE);
            myHolder.containerVIdeoPArent.setVisibility(View.GONE);
            myHolder.containerDocument.setVisibility(View.GONE);
            if (attachment.getImagePath().startsWith(Constants.S3BASE_URL)) {
                Glide.with(context).load(attachment.getImagePath()).apply(new RequestOptions().placeholder(R.drawable.ncp_placeholder)
                ).into(myHolder.ivUploadImage);
            } else {
                Glide.with(context).load(new File(attachment.getImagePath())).apply(new RequestOptions().placeholder(R.drawable.ncp_placeholder)
                ).into(myHolder.ivUploadImage);
            }
        } else if (attachment.getContentype().equalsIgnoreCase(Constants.VIDEO)) {
//            Log.e(TAG, "type : " + attachment.getContentype());
            myHolder.containerImage.setVisibility(View.GONE);
            myHolder.containerVideo.setVisibility(View.VISIBLE);
            myHolder.containerVIdeoPArent.setVisibility(View.VISIBLE);
            myHolder.containerDocument.setVisibility(View.GONE);
            myHolder.videoView.WIFI_TIP_DIALOG_SHOWED = true;
            HttpProxyCacheServer proxy = MyApplication.getProxy(context);
            String url = proxy.getProxyUrl(attachment.getImagePath());
            myHolder.videoView.fullscreenButton.setVisibility(View.GONE);
            myHolder.videoView.setUp(attachment.getImagePath(), "", Jzvd.SCREEN_WINDOW_NORMAL);
            myHolder.videoView.startButton.performClick();
        } else if (attachment.getContentype().equalsIgnoreCase(Constants.DOCUMENT)) {
            myHolder.containerImage.setVisibility(View.GONE);
            myHolder.containerVideo.setVisibility(View.GONE);
            myHolder.containerVIdeoPArent.setVisibility(View.GONE);
            myHolder.containerDocument.setVisibility(View.VISIBLE);
//            Log.e(TAG, "attachecment imagepath :" + attachment.getImagePath());
            if (attachment.getImagePath().endsWith("xls")) {
                Picasso.get().load(R.drawable.ncp_xls).error(R.drawable.ncp_xls).into(myHolder.ivUploadedDocument);
            } else if (attachment.getImagePath().endsWith("pdf")) {
                Picasso.get().load(R.drawable.ncp_pdf).error(R.drawable.ncp_pdf).into(myHolder.ivUploadedDocument);
            } else {
                Picasso.get().load(R.drawable.ncp_word).error(R.drawable.ncp_word).into(myHolder.ivUploadedDocument);
            }
            myHolder.tvDocumentName.setText(attachment.getImagePath().replace("/", ""));
            myHolder.tvDocumentType.setText(attachment.getContentype() + " Document");
            myHolder.tvDocumentSize.setVisibility(View.GONE);

        } else {
            Toast.makeText(context, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
        }

        myHolder.containerVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("code", "video");
                bundle.putString("image", attachment.getImagePath());
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                FullScreenPreview newFragment = FullScreenPreview.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(transaction, "videopreview");
            }
        });

        myHolder.ivCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                Utils.showToast(context, "item removed");
                notifyDataSetChanged();
            }
        });

        myHolder.ivCloseVIdeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                Utils.showToast(context, "item removed");
                notifyDataSetChanged();
            }
        });
        myHolder.ivCloseDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                Utils.showToast(context, "item removed");
                notifyDataSetChanged();
            }
        });
        myHolder.ivUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("code", "attach");
                bundle.putString("image", data.get(position).getImagePath());
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                FullScreenPreview newFragment = FullScreenPreview.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(transaction, "imagepreview");
            }
        });
        myHolder.containerVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("code", "video");
                bundle.putString("image", data.get(position).getImagePath());
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                FullScreenPreview newFragment = FullScreenPreview.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(transaction, "videopreview");
            }
        });
        myHolder.ivUploadedDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOpen.openFile(context, data.get(position).getUri());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyHolderAttachment extends RecyclerView.ViewHolder {
        ImageView ivCloseImage, ivCloseDocument, ivUploadImage, ivUploadedDocument;
        TextView tvDocumentName, tvDocumentType, tvDocumentSize;
        RelativeLayout containerDocument;
        RelativeLayout containerImage;
        FrameLayout containerVideo;
        RelativeLayout containerVIdeoPArent;
        JzvdStd videoView;
        ImageView ivCloseVIdeo;

        public MyHolderAttachment(View itemView) {
            super(itemView);
            ivCloseImage = (ImageView) itemView.findViewById(R.id.attachment_close_contentImage);
            ivCloseDocument = (ImageView) itemView.findViewById(R.id.attachment_close_document);
            ivUploadImage = (ImageView) itemView.findViewById(R.id.attachment_contentImage);
            ivUploadedDocument = (ImageView) itemView.findViewById(R.id.attachment_document_image);
            tvDocumentName = (TextView) itemView.findViewById(R.id.attachment_document_name);
            tvDocumentType = (TextView) itemView.findViewById(R.id.attachment_document_type);
            tvDocumentSize = (TextView) itemView.findViewById(R.id.attachment_document_size);
            containerDocument = (RelativeLayout) itemView.findViewById(R.id.attachment_document_container);
            containerImage = (RelativeLayout) itemView.findViewById(R.id.attachment_contentImageContainer);
            videoView = (JzvdStd) itemView.findViewById(R.id.attachment_contentVideo);
            containerVideo = (FrameLayout) itemView.findViewById(R.id.attachment_videoContainer);
            containerVIdeoPArent = (RelativeLayout) itemView.findViewById(R.id.videoContainerParent);
            ivCloseVIdeo = (ImageView) itemView.findViewById(R.id.attachment_close_contentVideo);
        }

    }

}

