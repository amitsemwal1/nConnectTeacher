package com.nconnect.teacher.firebase;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.nconnect.teacher.util.Constants;

public class FirebaseIdService extends FirebaseInstanceIdService {

    private static final String TAG = FirebaseIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        //now we will have the token
        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.e(TAG, "token : " + token);
        /*if (token != null && token != "") {
            tokenListener.onTokenReceived(token);
        }*/
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.FCM_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fcm_id_teacher", token);
        editor.commit();
    }


}
