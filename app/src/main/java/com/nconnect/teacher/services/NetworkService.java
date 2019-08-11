package com.nconnect.teacher.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.nconnect.teacher.services.SmackService.disConnectAll;

public class NetworkService extends Service {

    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static boolean isConnection = true;
    NotificationManager manager;
    BroadcastReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                    //check internet connection
                    if (!ConnectionHelper.isConnectedOrConnecting(context)) {
                        if (context != null) {
                            boolean show = false;
                            if (ConnectionHelper.lastNoConnectionTs == -1) {//first time
                                show = true;
                                ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                            } else {
                                if (System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                                    show = true;
                                    ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                                }
                            }

                            if (show && ConnectionHelper.isOnline) {
                                ConnectionHelper.isOnline = false;
                                isConnection = false;
                                Log.i("NETWORK123", "Connection lost");
                                //manager.cancelAll();

                                //method to disconnect all connection when it goes to offline
                                disConnectAll();
                            }
                        }
                    } else {
                        Log.i("NETWORK123", "Connected");
                        // Perform your actions here
                        ConnectionHelper.isOnline = true;

                        if (!isConnection) {
                            isConnection = true;

                            /*MyXMPP xmpp = MyXMPP.getInstance();
                            xmpp.initialiseConnection();*/
                            SmackService.start(context);
                            //xmpp.connect(MainActivity.user1);

                            //event to refresh the list adapter
                            //AndroidBusProvider.getInstance().post(new RestartServiceEvent(true));
                            /*Intent pushIntent = new Intent(context, MyService.class);
                            context.startService(pushIntent);*/
                        }
                        /*try{

                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PAPPAYA_PREF, MODE_PRIVATE);
                            String table_name = sharedPreferences.getString(Constants.USERNAME, "");

                            Database database = new Database(getApplicationContext());
                            if (!database.isTableExists(table_name, getApplicationContext())) {
                                database.createTable(table_name);
                            }
                            if (database.isTableExists(table_name, getApplicationContext())) {
                                //process offline messages
                                database.getMessagesFromOffline(table_name, getApplicationContext());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }*/
                    }
                }
            }
        };


        registerReceiver(receiver, filter);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}