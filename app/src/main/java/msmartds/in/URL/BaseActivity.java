package msmartds.in.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Harendra on 4/29/2017.
 */

public class BaseActivity extends AppCompatActivity {

    boolean conn = false;
    Builder alertDialog;
    ProgressDialog progressDialog;
    public AlertDialog dialog = null;

    public void showToast(String txt) {
        // Inflate the Layout
        Toast toast = Toast.makeText(BaseActivity.this, txt, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void  getSocketTimeOut(JsonObjectRequest objectRequest){
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HttpURL.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public static void  getSocketTimeOutStatic(JsonObjectRequest objectRequest){
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HttpURL.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
    //Check Internet Connection
    public boolean isConnectionAvailable() {

        if (isOnline() == false) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isOnline() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(BaseActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        if (haveConnectedWifi == true || haveConnectedMobile == true) {
            Log.e("Log-Wifi", String.valueOf(haveConnectedWifi));
            Log.e("Log-Mobile", String.valueOf(haveConnectedMobile));
            conn = true;
        } else {
            Log.e("Log-Wifi", String.valueOf(haveConnectedWifi));
            Log.e("Log-Mobile", String.valueOf(haveConnectedMobile));
            conn = false;
        }

        return conn;
    }
    // ProgressDialog progressDialog; I have declared earlier.
    public void showPDialog(String msg) {
        progressDialog = new ProgressDialog(BaseActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.dialog_animation));
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissPDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public String LoadPref(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        String data = sharedPreferences.getString(key, "");
        return data;
    }

    public void SavePref(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public static void createSourceFile(Context ctx, String data, String fileName) {
        String FILE_NAME = fileName;
        FileWriter writer = null;
        File cDir;
        File tempFile;

        try {
            System.out.println("Source File creation start");
            //** Getting reference to btn_save of the layout activity_main *//*
           // cDir =ctx.getExternalCacheDir();

            //** Getting a reference to temporary file, if created earlier *//*
            tempFile = new File(/*cDir.getPath()*/ getDiskCacheDir(ctx)+ "/" + FILE_NAME);
            writer = new FileWriter(tempFile);
            writer.write(data);
            //** Closing the writer object *//*
            writer.close();

            System.out.println("Source File created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
    public static String readSourceFile(Context ctx, String fileName) {
        Log.v("Read file called", "");
        String FILE_NAME = fileName;
        String strLine = "";
        StringBuilder text = new StringBuilder();
        File cDir;
        File tempFile;
        try {
            //** Getting reference to btn_save of the layout activity_main *//*
           // cDir = ctx.getExternalCacheDir();

            //** Getting a reference to temporary file, if created earlier *//*
            tempFile = new File(/*cDir.getPath()*/getDiskCacheDir(ctx) + "/" + FILE_NAME);
            if (!tempFile.exists()) {
                return null;
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
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return text.toString();
    }

    public void showKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }
    public void hideKeyBoard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
