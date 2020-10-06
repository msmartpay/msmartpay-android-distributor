package msmartds.in.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.format.Formatter;

import androidx.appcompat.app.AlertDialog;
import androidx.work.WorkInfo;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import msmartds.in.URL.HttpURL;

public class Util {
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    public static boolean isWorkScheduled(List<WorkInfo> workInfos) {
        boolean running = false;
        if (workInfos == null || workInfos.size() == 0) return false;
        for (WorkInfo workStatus : workInfos) {
            running = workStatus.getState() == WorkInfo.State.RUNNING | workStatus.getState() == WorkInfo.State.ENQUEUED;
        }
        return running;
    }
    public static void  getSocketTimeOut(JsonObjectRequest objectRequest){
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HttpURL.MY_SOCKET_TIMEOUT_MS,
                /*DefaultRetryPolicy.DEFAULT_MAX_RETRIES*/0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    public static Gson gson1;


    public static Gson getGson(){
        if(gson1==null)
            gson1 = new Gson();
        return gson1;
    }
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private static SharedPreferences getMyPref(Context context){
        if(pref==null) {
            pref = context.getSharedPreferences("Details", Context.MODE_PRIVATE);
        }
        return pref;
    }
    public static void SavePrefData(Context context, String key, String value){
        if(editor==null)
            editor=getMyPref(context).edit();
        editor.putString(key,value);
        editor.apply();
    }
    public static String LoadPrefData(Context context, String key){
        return getMyPref(context).getString(key,"");
    }
    public static void SavePrefBoolean(Context context, String key, Boolean value){
        if(editor==null)
            editor=getMyPref(context).edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public static Boolean LoadPrefBoolean(Context context, String key){
        return getMyPref(context).getBoolean(key,false);
    }
    public static void clearMyPref(Context context){
        if(editor==null)
            editor=getMyPref(context).edit();
        editor.clear().apply();
    }


    public static String getIpAddress(Context context){
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }

        if(haveConnectedWifi){
            return getWifiIPAddress(context);
        }
        if (haveConnectedMobile)
            return getMobileIPAddress();

        return null;
    }
    public static String getWifiIPAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiMgr != null;
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return  Formatter.formatIpAddress(ip);
    }
    public static String getMobileIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static String getCompleteAddressString(Context context,double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public static void onGPS(Context context) {

        final AlertDialog.Builder builder= new AlertDialog.Builder(context);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", (dialog, which) -> context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).setNegativeButton("NO", (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
