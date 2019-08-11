package com.nconnect.teacher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.nconnect.teacher.listener.SmsListener;
import com.nconnect.teacher.util.Constants;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SMS_RECEIVER";
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
//            Log.e(TAG, "Value Sender : " + sender);
            String messageBody = smsMessage.getMessageBody();
//            Log.e(TAG, "Message Body : " + messageBody);
            if (sender != null && sender != "") {
                try {
                    if (sender.endsWith(Constants.SMS_SUFFIX)) {
                        mListener.messageReceived(messageBody, sender);
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "Null");
                }
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
