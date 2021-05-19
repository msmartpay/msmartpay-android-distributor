package msmartds.in.URL;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

/**
 * Created by Harendra on 4/29/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public static void getSocketTimeOutStatic(JsonObjectRequest objectRequest) {
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HttpURL.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void getSocketTimeOut(JsonObjectRequest objectRequest) {
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                HttpURL.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
