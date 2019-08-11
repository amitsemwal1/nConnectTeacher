package com.nconnect.teacher.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nconnect.teacher.util.Constants;


public class DownloadImage extends AsyncTask<String, String, String> {


    private ProgressDialog progressDialog;
    private String fileName;
    private String folder;
    private boolean isDownloaded;
    private static final String TAG = DownloadVideo.class.getSimpleName();
    private Context context;
    private int BUFFERSIZE = 8192;

    public DownloadImage(Context context) {
        this.context = context;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
 /*   @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(context, R.style.dialog_background);
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }
*/
    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            // getting file length
            int lengthOfFile = connection.getContentLength();
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), BUFFERSIZE);
            String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            //Extract file name from URL
            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
            //Append timestamp to file name
            fileName = timestamp + "_" + fileName;
            //External directory path to save file
            folder = Environment.getExternalStorageDirectory() + File.separator + Constants.GALLERY_DIRECTORY_NAME + "/" + "images" + "/";
//            folder = Environment.getExternalStorageDirectory() + File.separator + "nconnect/";

            File directory = new File(folder);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // Output stream to write file
            OutputStream output = new FileOutputStream(folder + fileName);
            byte data[] = new byte[1024 * 1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
                // writing data to file
                output.write(data, 0, count);
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();
            return "Downloaded at: " + folder + fileName;
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
        return "Something went wrong";
    }

    /**
     * Updating progress bar
     */
   /* protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        this.progressDialog.setIndeterminate(false);
        this.progressDialog.setMax(100);
        this.progressDialog.setProgress(Integer.parseInt(progress[0]));
    }

*/
    @Override
    protected void onPostExecute(String message) {
        // dismiss the dialog after the file was downloaded
//        this.progressDialog.dismiss();
        // Display File path after downloading
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
/*
 */