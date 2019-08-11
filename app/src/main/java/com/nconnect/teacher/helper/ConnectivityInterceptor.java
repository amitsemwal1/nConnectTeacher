package com.nconnect.teacher.helper;

import android.content.Context;

import java.io.IOException;

import com.nconnect.teacher.util.Utils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectivityInterceptor implements Interceptor {

    private Context mContext;

    public ConnectivityInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!Utils.isNetworkAvailable(mContext)) {
            throw new NoConnectivityException();
        }
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

    public class NoConnectivityException extends IOException {

        @Override
        public String getMessage() {
            return "No connectivity exception";
        }

    }
}

