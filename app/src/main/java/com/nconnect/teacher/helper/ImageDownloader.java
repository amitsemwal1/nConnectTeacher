package com.nconnect.teacher.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.nconnect.teacher.util.Constants;

public class ImageDownloader implements Target {

    private Context context;
    private String uriPath;
    private static final String TAG = ImageDownloader.class.getSimpleName();

    public ImageDownloader(Context context, String uriPath) {
        this.context = context;
        this.uriPath = uriPath;
    }

    @Override
    public void onPrepareLoad(Drawable arg0) {
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.GALLERY_DIRECTORY_NAME + "/" + "images" + "/");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
           /* if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "Oops! Failed create " + Constants.GALLERY_DIRECTORY_NAME + " directory");
                return;
            }*/
            mediaStorageDir.mkdir();
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        try {
            mediaFile.createNewFile();
            FileOutputStream ostream = new FileOutputStream(mediaFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
            Toast.makeText(context, "image saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception : " + e);
            Toast.makeText(context, "Failed to save image plese try again later", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        Log.e(TAG, "Exception : " + e);
        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
    }
}
