package com.nconnect.teacher.firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import com.nconnect.teacher.helper.ConnectivityInterceptor;
import com.nconnect.teacher.model.Params;
import com.nconnect.teacher.model.stories.Stories;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FcmMessagingService extends FirebaseMessagingService {

    private static final String TAG = FcmMessagingService.class.getSimpleName();
    private LocalBroadcastManager broadcastManager;
    private String sessionIdValue = "";

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> messageData = remoteMessage.getData();
//            Log.e(TAG, "map data : " + messageData.toString());
            String modelType = messageData.get("model");
            if (modelType.equalsIgnoreCase("story")) {
                String storyId = messageData.get("value");
                Intent intent = new Intent(Constants.STORIES);
                intent.putExtra("type", 1);
                intent.putExtra("story_id", storyId);
                broadcastManager.sendBroadcast(intent);
            } else if (modelType.equalsIgnoreCase("issue")) {
                String issueId = messageData.get("value");
                Intent intent = new Intent(Constants.ISSUES);
                intent.putExtra("type", 1);
                intent.putExtra("issue_id", issueId);
                broadcastManager.sendBroadcast(intent);
            } else if (modelType.equalsIgnoreCase(Constants.CHAT)) {
                Intent intent = new Intent(Constants.CHAT);
                String receiver_name = "";
                String name = "";
                try{
                    receiver_name = messageData.get("sender");
                    name = messageData.get("sender_name");
                            /*receiver_name = "103800092";
                            name = "Amuthavani";*/
                }catch (Exception e){}

                intent.putExtra("receiver_name", receiver_name);
                intent.putExtra("name", name);
                intent.putExtra("screen", "Notification");

                broadcastManager.sendBroadcast(intent);
            }

            try {
                if (!modelType.equalsIgnoreCase(Constants.CHAT)) {
                    Intent notify = new Intent(Constants.notification);
                    broadcastManager.sendBroadcast(notify);
                }
                /*Dashboard.notifyItemCount += 1;
                Dashboard.setupBadge();*/
            } catch (Exception e) {
            }
        }
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.FCM_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fcm_id_teacher", s);
        editor.commit();
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
        if (isLogin) {
            new AsyncUpdateDeviceToken(s).execute();
        } else {
//            Log.e(TAG, "You are not logged in " + s);
        }
    }

    class AsyncUpdateDeviceToken extends AsyncTask<Void, Void, Void> {
        private String deviceToken = "";

        public AsyncUpdateDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            updateDeviceToken(deviceToken);
            return null;
        }
    }

    private void updateDeviceToken(String token) {

        Params params = new Params();
        params.setDevice_token(token);
        Stories stories = new Stories();
        stories.setStoriesParams(params);
//        Log.e(TAG, "model data : " + new Gson().toJson(stories));
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Constants.SESSION_ID, "");
        if (sessionId != "") {
            sessionIdValue = Constants.SESSION_ID + "=" + sessionId;
        }
        (Utils.httpService(this).updateToken(stories, sessionId)).enqueue(new Callback<Stories>() {
            @Override
            public void onResponse(Call<Stories> call, Response<Stories> response) {
//                Log.e(TAG, "response : " + new Gson().toJson(response.body()));
                try {
                    if (response.body().getResult().getResponse().equalsIgnoreCase(Constants.SUCCESS_MESSAGE)) {
//                        Log.e(TAG, "device token updated");
                    } else {
//                        Log.e(TAG, "Cant update Device token ");
                        Toast.makeText(FcmMessagingService.this, "Cant update Device token", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
//                    Log.e(TAG, "Excpetion : " + e);
                }
            }

            @Override
            public void onFailure(Call<Stories> call, Throwable t) {
//                Log.e(TAG, "eror :  " + t);
                if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
                    Toast.makeText(FcmMessagingService.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
