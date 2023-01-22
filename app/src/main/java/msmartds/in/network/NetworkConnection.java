package msmartds.in.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import  msmartds.in.util.L;

public class NetworkConnection {

    public static boolean isConnectionAvailable(Context context) {
        boolean flag = false;
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        if (netInfo != null) {
            if (netInfo.isConnected())
                flag = true;
            else {
                L.toast(context.getApplicationContext(), "No internet available");
            }
        } else {
            L.toast(context.getApplicationContext(), "No internet available");
        }
        return flag;
    }
    public static boolean isConnectionAvailable2(Context context) {
        boolean flag = false;
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();
        if (netInfo != null) {
            if (netInfo.isConnected())
                flag = true;
            else {
                //   showToast(context.getApplicationContext(), "No internet available", true);
            }
        } else {
            // showToast(context.getApplicationContext(), "No internet available", true);
        }
        return flag;
    }
}
