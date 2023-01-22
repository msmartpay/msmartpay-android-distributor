package msmartds.in.util;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import  msmartds.in.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Gson gson;
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static SimpleDateFormat fullDateFormatter = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss 'GMT'z", Locale.ENGLISH);
    private static SimpleDateFormat fullDateFormatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);


    public static boolean isValidEmail(String enteredEmail) {
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(enteredEmail);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getFullDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        return fullDateFormatter.format(newDate.getTime());
    }

    public static String getFullDate() {
        Calendar newDate = Calendar.getInstance();
        return fullDateFormatter1.format(newDate.getTime());
    }

    public static String getDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        return getDate(newDate.getTime());
    }

    public static String getDate(Long date) {
        return dateFormatter.format(date);
    }

    public static String getDate(Date date) {
        return dateFormatter.format(date);
    }

    public static Gson getGson() {
        if (gson == null) gson = new GsonBuilder()
                .setLenient()
                .create();
        ;
        return gson;
    }

    public static <T> List<T> getListFromJson(String jsonArray, Class<T> clazz) {
        Type typeOfT = TypeToken.getParameterized(List.class, clazz).getType();

        return getGson().fromJson(jsonArray, typeOfT);
    }

    public static String getStringFromModel(Object obj) {
        return getGson().toJson(obj);
    }


    @SuppressLint("SetTextI18n")
    public static String getAmountWithSymbol(Context ctx, String balance) {
        return ctx.getResources().getString(R.string.rupee_symbol) + " " + getAmount(balance);
    }

    @SuppressLint("SetTextI18n")
    public static Double getAmount(String balance) {
        if (balance == null || balance.isEmpty() || balance.equalsIgnoreCase("na")) {
            return 0.00;
        } else {
            return (int) Math.round(Double.parseDouble(balance) * 100) / (double) 100;
        }
    }

    private static SharedPreferences getPref(Context ctx) {
        if (sharedPreferences == null)
            sharedPreferences = ctx.getSharedPreferences(Keys.PREF_NAME, MODE_PRIVATE);
        return sharedPreferences;
    }

    public static void saveData(Context ctx, String key, String value) {
        if (editor == null)
            editor = getPref(ctx).edit();
        editor.putString(key, value).apply();
    }

    public static String getData(Context ctx, String key) {
        return getPref(ctx).getString(key, "");
    }

    public static void clearPref(Context ctx) {
        getPref(ctx).edit().clear().apply();
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getIpAddress(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }

        if (haveConnectedWifi) {
            return getWifiIPAddress(context);
        }
        if (haveConnectedMobile)
            return getMobileIPAddress();

        return null;
    }

    public static String getWifiIPAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ip);
    }

    public static String getMobileIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
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

    public static void showView(View v) {
        v.setVisibility(View.VISIBLE);
    }

    public static void hideView(View v) {
        v.setVisibility(View.GONE);
    }

    public static void invisibleView(View v) {
        v.setVisibility(View.INVISIBLE);
    }

    public static void createSourceFile(Context ctx, String data) {
        String FILE_NAME = Keys.FILE_NAME;
        ;
        FileWriter writer = null;
        File cDir;
        File tempFile;

        try {
            System.out.println("Source File creation start");
            //** Getting reference to btn_save of the layout activity_main *//*
            cDir = ctx.getCacheDir();

            //** Getting a reference to temporary file, if created earlier *//*
            tempFile = new File(cDir.getPath() + "/" + FILE_NAME);
            writer = new FileWriter(tempFile);
            writer.write(data);
            //** Closing the writer object *//*
            writer.close();

            System.out.println("Source File created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readSourceFile(Context ctx) {
        Log.v("Read file called", "");
        String FILE_NAME = Keys.FILE_NAME;
        String strLine = "";
        StringBuilder text = new StringBuilder();
        File cDir;
        File tempFile;
        try {
            //** Getting reference to btn_save of the layout activity_main *//*
            cDir = ctx.getCacheDir();

            //** Getting a reference to temporary file, if created earlier *//*
            tempFile = new File(cDir.getPath() + "/" + FILE_NAME);
            if (!tempFile.exists()) {
                return "";
            } else {
                FileReader fReader = new FileReader(tempFile);
                BufferedReader bReader = new BufferedReader(fReader);

                //** Reading the contents of the file , line by line *//*
                while ((strLine = bReader.readLine()) != null) {
                    text.append(strLine + "\n");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return text.toString();
    }

    public static void showKeyBoard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyBoard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static Dialog getDialog(Context context, int resourceLayout) {
        @SuppressLint("PrivateResource")
        Dialog d = new Dialog(context, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        d.setCancelable(false);
        Objects.requireNonNull(d.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        d.setContentView(resourceLayout);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return d;
    }

    public static void setScrollView(ScrollView scrollview) {
        if (scrollview != null) {
            scrollview.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            scrollview.setFocusable(true);
            scrollview.setFocusableInTouchMode(true);
            scrollview.setOnTouchListener((v, event) -> {
                // TODO Auto-generated method stub
                v.requestFocusFromTouch();
                return false;
            });
        }
    }
}
