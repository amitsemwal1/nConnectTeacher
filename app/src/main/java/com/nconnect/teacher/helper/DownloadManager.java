package com.nconnect.teacher.helper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadManager {
    private static final String TAG = DownloadManager.class.getSimpleName();
    private String url = "";
    private Activity context = null;
    private long downloadId = 0;
    private String type = "";

    public DownloadManager(String url, Activity context, String type) {
        this.context = context;
        this.url = url;
        this.type = type;
        initReceiver();
        startDownload();
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long broadCastDownloadId = intent.getLongExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if (broadCastDownloadId == downloadId) {
                    if (getDownloadStatus() == android.app.DownloadManager.STATUS_SUCCESSFUL) {
                        Toast.makeText(context, "File Downloaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Can't download please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, intentFilter);
    }

    private void startDownload() {
        Uri uri = Uri.parse(url);
        android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(uri);
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
        fileName = timestamp + "_" + fileName;
        String downloadPath = "", dirType = "";
        if (type.equalsIgnoreCase(Constants.DOCUMENT)) {
            downloadPath = Utils.getDocDir();
            dirType = Constants.GALLERY_DIRECTORY_NAME + "/" + "documents" + "/";
        } else if (type.equalsIgnoreCase(Constants.IMAGE)) {
            downloadPath = Utils.getImgDir();
            dirType = Constants.GALLERY_DIRECTORY_NAME + "/" + "images" + "/";
        } else if (type.equalsIgnoreCase(Constants.VIDEO)) {
            downloadPath = Utils.getVidDir();
            dirType = Constants.GALLERY_DIRECTORY_NAME + "/" + "videos" + "/";
        }
        File downloadedDir = new File(downloadPath);
        if (!downloadedDir.exists()) {
            downloadedDir.mkdirs();
        }
        request.setTitle("Downloading " + fileName);
        request.setDestinationInExternalPublicDir(dirType, fileName);
        android.app.DownloadManager downloadManager = (android.app.DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);
    }

    private int getDownloadStatus() {

        android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
        query.setFilterById(downloadId);

        android.app.DownloadManager manager = (android.app.DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = manager.query(query);

        if (cursor.moveToFirst()) {
            //column for download  status
            int columnIndex = cursor.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            //column for reason code if the download failed or paused
            int columnReason = cursor.getColumnIndex(android.app.DownloadManager.COLUMN_REASON);
            int reason = cursor.getInt(columnReason);
            //get the download filename
//            int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
//            String filename = cursor.getString(filenameIndex);

            String statusText = "";
            String reasonText = "";

            switch (status) {
                case android.app.DownloadManager.STATUS_FAILED:
                    statusText = "STATUS_FAILED";
                    switch (reason) {
                        case android.app.DownloadManager.ERROR_CANNOT_RESUME:
                            reasonText = "ERROR_CANNOT_RESUME";
                            break;
                        case android.app.DownloadManager.ERROR_DEVICE_NOT_FOUND:
                            reasonText = "ERROR_DEVICE_NOT_FOUND";
                            break;
                        case android.app.DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            reasonText = "ERROR_FILE_ALREADY_EXISTS";
                            break;
                        case android.app.DownloadManager.ERROR_FILE_ERROR:
                            reasonText = "ERROR_FILE_ERROR";
                            break;
                        case android.app.DownloadManager.ERROR_HTTP_DATA_ERROR:
                            reasonText = "ERROR_HTTP_DATA_ERROR";
                            break;
                        case android.app.DownloadManager.ERROR_INSUFFICIENT_SPACE:
                            reasonText = "ERROR_INSUFFICIENT_SPACE";
                            break;
                        case android.app.DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                            reasonText = "ERROR_TOO_MANY_REDIRECTS";
                            break;
                        case android.app.DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                            reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                            break;
                        case android.app.DownloadManager.ERROR_UNKNOWN:
                            reasonText = "ERROR_UNKNOWN";
                            break;
                    }
                    break;
                case android.app.DownloadManager.STATUS_PAUSED:
                    statusText = "STATUS_PAUSED";
                    switch (reason) {
                        case android.app.DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                            reasonText = "PAUSED_QUEUED_FOR_WIFI";
                            break;
                        case android.app.DownloadManager.PAUSED_UNKNOWN:
                            reasonText = "PAUSED_UNKNOWN";
                            break;
                        case android.app.DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                            reasonText = "PAUSED_WAITING_FOR_NETWORK";
                            break;
                        case android.app.DownloadManager.PAUSED_WAITING_TO_RETRY:
                            reasonText = "PAUSED_WAITING_TO_RETRY";
                            break;
                    }
                    break;
                case android.app.DownloadManager.STATUS_PENDING:
                    statusText = "STATUS_PENDING";
                    break;
                case android.app.DownloadManager.STATUS_RUNNING:
                    statusText = "STATUS_RUNNING";
                    break;
                case android.app.DownloadManager.STATUS_SUCCESSFUL:
                    statusText = "STATUS_SUCCESSFUL";
//                    reasonText = "Filename:\n" + filename;
                    break;
            }
//            Log.e(TAG, "status text : " + statusText + "\n" + "reason name : " + reasonText);
            return status;
        }

        return android.app.DownloadManager.ERROR_UNKNOWN;
    }

    public void cancelDownload() {
        android.app.DownloadManager manager = (android.app.DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        manager.remove(downloadId);
    }

}
