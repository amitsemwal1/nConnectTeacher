package com.nconnect.teacher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.nconnect.teacher.services.NetworkService;
import com.nconnect.teacher.services.SmackService;
import com.nconnect.teacher.util.Utils;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.e("Boot1", "C json : " + context.toString());
        Toast.makeText(context, "going to restart service", Toast.LENGTH_LONG).show();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//            Log.e("Boot1", "C json : " + context.toString());
//            Intent service = new Intent(context, ChatService.class);
           /* Intent service = new Intent(context, SmackService.class);
            context.startService(service);
            SmackService.start(context);*/

            context.startService(new Intent(context, NetworkService.class));
            Utils.chatServiceActions(context);
        }
    }
}
