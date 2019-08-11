package com.nconnect.teacher.services;

public class ChatService/* extends Service */ {
/*

    private static final String TAG = ChatService.class.getSimpleName();
    private static final String DOMAIN = "openfire.pappaya.education";
    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    public static boolean ServerchatCreated = false;

    public static void start(Context context) {
        Intent intent = new Intent(context, ChatService.class);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new LocalBinder<ChatService>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "pappaya_teacher";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("")
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .build();

            startForeground(1, notification);
        }*/
       /* SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String USERNAME = sharedPreferences.getString(Constants.SENDER, "");
        String PASSWORD = sharedPreferences.getString(Constants.PASSWORD, "");
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        xmpp = MyXMPP.getInstance(ChatService.this, DOMAIN, USERNAME, PASSWORD);
        xmpp.connect(USERNAME);


    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        xmpp.connection.disconnect();
        //start(getApplicationContext());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        try{

            Presence presence = new Presence(Presence.Type.unavailable);
            MyXMPP.connection.sendPacket(presence);

            MyXMPP.connection.disconnect();
            //Toast.makeText(getApplicationContext(), "Chatservice Closed", Toast.LENGTH_SHORT).show();

            stopSelf();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isNetworkConnected() {
        return cm.getActiveNetworkInfo() != null;
    }
*/
}