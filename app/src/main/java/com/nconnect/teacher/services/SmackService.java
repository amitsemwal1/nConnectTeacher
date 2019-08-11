package com.nconnect.teacher.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.xmpp.MyXMPP;


public class SmackService extends Service {


    private static final String DOMAIN = "openfire.pappaya.education";
    private static final String TAG = SmackService.class.getSimpleName();
    public static boolean mActive = false;
    public static Thread mThread;
    public static MyXMPP xmppConnection;
    public static XMPPTCPConnection mConnection;
    private Handler mHandler;

    public SmackService() {
    }

    public static void start(Context context) {

        mActive = false;
        if (mThread != null && mThread.isAlive()) {
            mThread.interrupt();
            mThread = null;
            try {
                xmppConnection.disconnect();
                xmppConnection.initialiseConnection();
                mConnection = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(context, SmackService.class);
        context.startService(intent);
    }

    //method to disconnect all connection when it goes to offline
    public static void disConnectAll() {

        mActive = false;
        if (mThread != null && mThread.isAlive()) {
            mThread.interrupt();
            mThread = null;

        }
        try {
            xmppConnection.disconnect();
            mConnection = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mConnection = MyXMPP.getConnection();

        try{
            if (mConnection == null){
                xmppConnection.init();
            }
        }catch (Exception e){}
        if (isConnectingToInternet()) {
            mActive = false;
            start();
        }
      //  return Service.START_NOT_STICKY;
        return Service.START_REDELIVER_INTENT;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(getApplicationContext(), "Service closed", Toast.LENGTH_LONG).show();
        stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder<SmackService>(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void start() {
        if (!mActive) {
            mActive = true;

            // Create ConnectionThread Loop
            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        mHandler = new Handler();
                        initConnection();
                        Looper.loop();
                    }
                });
                mThread.start();
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        try{

            Presence presence = new Presence(Presence.Type.unavailable);
            MyXMPP.connection.sendPacket(presence);
            MyXMPP.connection.disconnect();
            //Toast.makeText(getApplicationContext(), "SmackService Closed", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

        stopSelf();
    }

    public void stop() {
        mActive = false;
    }

    private void initConnection() {
        //Toast.makeText(SmackService.this, "Smack service initConnection() ", Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String USERNAME = sharedPreferences.getString(Constants.SENDER, "");
        String PASSWORD = sharedPreferences.getString(Constants.PASSWORD, "");

        xmppConnection.disconnect();
        mConnection = null;

        if (mConnection == null) {

            xmppConnection = MyXMPP.getSmackInstance(SmackService.this, DOMAIN, USERNAME, PASSWORD);
            xmppConnection.connect(USERNAME);
        } else {
//            Log.e(TAG, "Error while connecting ");
            xmppConnection.disconnect();
            xmppConnection.initialiseConnection();
        }

        /*xmppConnection = MyXMPP.getSmackInstance(SmackService.this, DOMAIN, USERNAME, PASSWORD);
        xmppConnection.connect(USERNAME);*/
        /*if (mConnection == null) {

            xmppConnection = MyXMPP.getSmackInstance(SmackService.this, DOMAIN, USERNAME, PASSWORD);
            xmppConnection.connect(USERNAME);
        } else {
            Log.e(TAG, "Error while connecting ");
            xmppConnection.disconnect();
            xmppConnection.initialiseConnection();
        }*/
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
            }
        }
        return false;
    }
}