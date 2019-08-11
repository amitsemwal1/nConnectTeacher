package com.nconnect.teacher.services;


public class DownloadNotificationService/* extends IntentService*/ {
/*

    public DownloadNotificationService() {
        super("Service");
    }

    private static final String TAG = DownloadNotificationService.class.getSimpleName();
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private String fileUrl = "";
    private String fileName = "";
    String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        fileUrl = intent.getStringExtra("file_url");
        Log.e(TAG, "file url : " + fileUrl);
        fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1, fileUrl.length());
        String notifName = fileName;
        fileName = timestamp + "_" + fileName;
        Log.e("TAG", "filename : " + fileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription("no sound");
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        notificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Downloading " + notifName)
                .setDefaults(0)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
        initRetrofit();

    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://testnrynconnect.pappaya.education/")
                .build();
        RetrofitClient retrofitInterface = retrofit.create(RetrofitClient.class);
        Call<ResponseBody> request = retrofitInterface.downloadImage(fileUrl);
        try {
            downloadImage(request.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadImage(ResponseBody body) throws IOException {
        //Extract file name from URL
        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        OutputStream outputStream = new FileOutputStream(outputFile);
        long total = 0;
        boolean downloadComplete = false;
        //int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));

        while ((count = inputStream.read(data)) != -1) {

            total += count;
            int progress = (int) ((double) (total * 100) / (double) fileSize);


            updateNotification(progress);
            outputStream.write(data, 0, count);
            downloadComplete = true;
        }
        onDownloadComplete(downloadComplete);
        outputStream.flush();
        outputStream.close();
        inputStream.close();

    }

    private void updateNotification(int currentProgress) {


        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendProgressUpdate(boolean downloadComplete) {

        Intent intent = new Intent(Constants.PROGRESS_UPDATE);
        intent.putExtra("downloadComplete", downloadComplete);
        LocalBroadcastManager.getInstance(DownloadNotificationService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete(boolean downloadComplete) {
        sendProgressUpdate(downloadComplete);
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Download Complete");
        notificationBuilder.setSmallIcon(R.drawable.ncp_downloaded);
        notificationManager.notify(0, notificationBuilder.build());
        Toast.makeText(this, "Download completed", Toast.LENGTH_SHORT).show();
        notificationManager.cancel(0);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }
*/

}
