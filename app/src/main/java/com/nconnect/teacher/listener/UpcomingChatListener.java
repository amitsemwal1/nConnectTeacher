package com.nconnect.teacher.listener;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import com.nconnect.teacher.R;
import com.nconnect.teacher.activity.ChatActivity;
import com.nconnect.teacher.adapter.ChatAdapter;
import com.nconnect.teacher.database.Database;
import com.nconnect.teacher.model.chat.ChatMessage;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.MyApplication;
import com.nconnect.teacher.util.Utils;

import static android.content.Context.MODE_PRIVATE;

public class UpcomingChatListener implements ChatMessageListener {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final String TAG = UpcomingChatListener.class.getSimpleName();
    private Context context;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    public UpcomingChatListener(Context context) {
        this.context = context;
    }

    @Override
    public void processMessage(final org.jivesoftware.smack.chat.Chat chat, final Message message) {
        if (message.getType() == Message.Type.chat && message.getBody() != null) {
            final ChatMessage chatMessage = new Gson().fromJson(message.getBody(), ChatMessage.class);
            processMessage(chatMessage);
        }
    }

    private void processMessage(final ChatMessage chatMessage) {

        context = MyApplication.getAppContext();
        boolean isActive = ChatActivity.active;

        boolean isDuplicate = false;

        Database database = new Database(context);
        SQLiteDatabase mydb = Database.getDataBase();
        if (mydb != null){
            if (!database.isTableExists(chatMessage.getReceiver(), context)) {
                database.createTable(chatMessage.getReceiver());
            }

            if (!database.isTableExists(database.HISTORYTABLENAME, context)) {
                database.createHistoryTable();
            }
            Cursor allrows = mydb.rawQuery("select * from " + database.HISTORYTABLENAME + " where " + Database.SENDER + " = '" + chatMessage.getReceiver() + "' AND " + Database.RECEIVER + " = '" + chatMessage.getSender() + "'", null);

            if (allrows.moveToFirst()) {
                do {
                    try{
                        String date = allrows.getString(allrows.getColumnIndex(Database.DATE));
                        long lastTimeMillis = Utils.getMillisWithDate(date, Constants.DATE_HOURS_MINI_FORMAT);
                        long currentDataTimeMillis = Utils.getMillisWithDate(chatMessage.getDate(), Constants.DATE_HOURS_MINI_FORMAT);

                        if (lastTimeMillis > currentDataTimeMillis){
                            isDuplicate = true;
                        }
                    }catch (Exception e){}

                }
                while (allrows.moveToNext());
            }
        }

        if (!isDuplicate){

            try{

                String date = Utils.convertLocalToUtcTime();
                chatMessage.setDate(date);

            }catch (Exception e){}

            try {
                Database.insertUpcomingMsgToDB(chatMessage, context, chatMessage.getSender_name());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!isActive) {
                String message = chatMessage.getBody();

                if (!chatMessage.getFile_url().equals("")) {
                    message = Utils.getFileNameFromUrl(chatMessage.getFile_url());
                    //message = "Attached Files";
                }

                //Notification.Builder builder = new Notification.Builder(context);
                Intent notificationIntent = new Intent(context, ChatActivity.class);
                notificationIntent.putExtra("screen", "Notification");
                notificationIntent.putExtra("receiver_name", chatMessage.getSender());
                notificationIntent.putExtra("name", chatMessage.getSender_name());
                notificationIntent.putExtra(Constants.NOTIFICATIONS, new Gson().toJson(chatMessage));

                /**Creates an explicit intent for an Activity in your app**/
                //Intent resultIntent = new Intent(context, ChatActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                        0 /* Request code */, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher_logo);
                mBuilder.setContentTitle("New Message from " + chatMessage.getSender_name())
                        .setContentText(message)
                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);

                mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    assert mNotificationManager != null;
                    mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                    mNotificationManager.createNotificationChannel(notificationChannel);
                }
                assert mNotificationManager != null;
                mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
            /*PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setSmallIcon(R.mipmap.ic_launcher_logo)
                    .setContentTitle("New Message from " + chatMessage.getReceiver_name())
                    .setContentText(message)
                    .setContentIntent(pendingIntent);
            builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
            builder.setAutoCancel(true);
            long[] pattern = {500,500,500,500,500,500,500,500,500};
            builder.setVibrate(pattern);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID);
            }
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID);
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Notification notification = builder.getNotification();
            notificationManager.notify(notifyID, notification);
            Log.e(TAG, "message json : " + new Gson().toJson(chatMessage));*/
            } else {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
                String name = sharedPreferences.getString(Constants.SENDER, "");

                if (name.equalsIgnoreCase(chatMessage.getReceiver())) {
            /*boolean isMine = true;
            if (name.equalsIgnoreCase(chatMessage.getReceiver())){
                isMine = false;
            }*/
                    chatMessage.setMine(false);

                    ChatAdapter.mChatList.add(chatMessage);
                    //Database.insertToDB(chatMessage, context);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                ChatActivity.mChatAdapter.notifyItemRangeInserted(ChatAdapter.mChatList.size(), 1);
                                //ChatActivity.mChatAdapter.notifyDataSetChanged();
                                ChatActivity.recyclerMessages.smoothScrollToPosition(ChatActivity.mChatAdapter.getItemCount());
                            } catch (Exception e) {
                            }

                        }
                    });
                }
            }
        }




//        Log.e(TAG, "message json : " + new Gson().toJson(chatMessage));


    }

}