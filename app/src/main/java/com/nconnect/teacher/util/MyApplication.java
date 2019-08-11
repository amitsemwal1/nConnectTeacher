package com.nconnect.teacher.util;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

public class MyApplication extends Application {

    private static Context context;
    private HttpProxyCacheServer proxy;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

}