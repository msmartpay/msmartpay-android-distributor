package msmartds.in.network;

import android.content.Context;

import  msmartds.in.util.Keys;
import  msmartds.in.util.Util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    private Context context;

    AuthenticationInterceptor(Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        String dsId = Util.getData(context, Keys.DS_ID);
        String txnKey = Util.getData(context, Keys.TXN_KEY);
       // L.m2("intercept dsId", dsId);
       // L.m2("intercept txnKey", txnKey);
        return chain.proceed(
                chain.request()
                .newBuilder()
                .header("distributorId", dsId)
                .header("txnkey", txnKey)
                .build()
        );
    }

}