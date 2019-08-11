package com.nconnect.teacher.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nconnect.teacher.R;
import com.nconnect.teacher.util.Constants;
import com.nconnect.teacher.util.Utils;


public class SplashScreen extends AppCompatActivity {

    private static final String TAG = SplashScreen.class.getSimpleName();
    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_SIGNUP = 2;
    private Bundle extras;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        extras = getIntent().getExtras();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (extras != null) {
                    String notificationType = extras.getString("model");
//                    Log.e(TAG, "Model name : " + notificationType);
                    if (Utils.replaceNull(notificationType).equalsIgnoreCase("story")) {
                        String id = extras.getString("value");
                        Intent intent = new Intent(SplashScreen.this, ViewStoryActivity.class);
                        intent.putExtra("isScreen", Constants.notification);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    } else if (Utils.replaceNull(notificationType).equalsIgnoreCase("event")) {
                        String id = extras.getString("value");
                        Intent intent = new Intent(SplashScreen.this, ViewEventsActivity.class);
                        intent.putExtra("isScreen", Constants.notification);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    } else if (Utils.replaceNull(notificationType).equalsIgnoreCase("issue")) {
                        String id = extras.getString("value");
                        Intent intent = new Intent(SplashScreen.this, ViewIssueActivity.class);
                        intent.putExtra("isScreen", Constants.notification);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    } else if (Utils.replaceNull(notificationType).equalsIgnoreCase("announcement")) {
                        String id = extras.getString("value");
                        Intent intent = new Intent(SplashScreen.this, ViewAnnouncementActivity.class);
                        intent.putExtra("isScreen", Constants.notification);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    } else if (Utils.replaceNull(notificationType).equalsIgnoreCase(Constants.CHAT)) {
                        Intent intent = new Intent(SplashScreen.this, ChatActivity.class);

                        String receiver_name = "";
                        String name = "";
                        try {
                            receiver_name = extras.getString("sender");
                            name = extras.getString("sender_name");
                            /*receiver_name = "103800092";
                            name = "Amuthavani";*/
                        } catch (Exception e) {
                        }

                        if (Utils.replaceNull(receiver_name).equalsIgnoreCase("") || Utils.replaceNull(name).equalsIgnoreCase("")) {
                            intent = new Intent(SplashScreen.this, Dashboard.class);
                        }

                        intent.putExtra("receiver_name", receiver_name);
                        intent.putExtra("name", name);
                        intent.putExtra("isSplash", true);
                        intent.putExtra("screen", "Notification");
                        startActivity(intent);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
//                int userTOken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
                                boolean islogin = sharedPreferences.getBoolean("isLogin", false);

                                if (islogin) {
                                    startActivity(new Intent(SplashScreen.this, Dashboard.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(SplashScreen.this, NcpLoginScreen.class));
                                    finish();
                                }
                            }
                        }, 2000);

                    }
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFNAME, MODE_PRIVATE);
//                int userTOken = sharedPreferences.getInt(Constants.USER_TOKEN, 0);
                            boolean islogin = sharedPreferences.getBoolean("isLogin", false);

                            if (islogin) {
                                startActivity(new Intent(SplashScreen.this, Dashboard.class));
                                finish();
                            } else {
                                startActivity(new Intent(SplashScreen.this, NcpLoginScreen.class));
                                finish();
                            }
                        }
                    }, 2000);

                }
            }
        }, 500);


    }


}