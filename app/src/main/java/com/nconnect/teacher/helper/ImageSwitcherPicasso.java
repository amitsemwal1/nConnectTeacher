package com.nconnect.teacher.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageSwitcher;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageSwitcherPicasso implements Target {

    private ImageSwitcher mImageSwitcher;
    private Context mContext;

    public ImageSwitcherPicasso(Context context, ImageSwitcher imageSwitcher) {
        mImageSwitcher = imageSwitcher;
        mContext = context;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
        mImageSwitcher.setImageDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable drawable) {

    }

}
