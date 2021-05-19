package msmartds.in.URL;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

/**
 * Created by Harendra on 7/17/2017.
 */

public class BaseFragment extends Fragment {

    boolean conn = false;

    public static void getSocketTimeOut(JsonObjectRequest objectRequest) {
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HttpURL.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public boolean isConnectionAvailable() {

        if (!isOnline()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isOnline() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        if (haveConnectedWifi || haveConnectedMobile) {
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
}
