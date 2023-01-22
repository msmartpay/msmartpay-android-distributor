package msmartds.in.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import  msmartds.in.BuildConfig;


public class L {

    private static final String TAG = "@-Test";

    public static void m(String str) {
        if (BuildConfig.DEBUG)
            Log.i(TAG, str);
    }

    public static void m2(String tag, String str) {
        if (BuildConfig.DEBUG)
            Log.e(tag, str);
    }

    public static void toast(Context context, String message) {
       Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastL(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
