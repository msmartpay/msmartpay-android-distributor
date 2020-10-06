package msmartds.in.location;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.utility.Keys;
import msmartds.in.utility.L;
import msmartds.in.utility.Mysingleton;
import msmartds.in.utility.Util;

public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();


    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            L.m2(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        L.m2(TAG, "Connected to Google API");
    }

    @Override
    public void onConnectionSuspended(int i) {
        L.m2(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        L.m2(TAG, "Failed to connect to Google API");

    }

    @Override
    public void onLocationChanged(Location location) {
        L.m2(TAG, "Location changed");


        if (location != null) {
            L.m2(TAG, "== location != null");

            //Send result to activities
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            saveLocationToServer(location);
        }
    }

    private void sendMessageToUI(String lat, String lng) {

        L.m2(TAG, "Sending info...");
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private void saveLocationToServer(Location mLocation) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonData = new JSONObject();
            jsonObject.put("distributorId", Util.LoadPrefData(getApplicationContext(), Keys.DS_ID));
            jsonObject.put("txnkey",Util.LoadPrefData(getApplicationContext(), Keys.TXN_KEY));
            jsonData.put("latitude",mLocation.getLatitude()+"");
            jsonData.put("longitude",mLocation.getLongitude()+"");
            jsonData.put("location",Util.getCompleteAddressString(getApplicationContext(),mLocation.getLatitude(), mLocation.getLongitude()));
            jsonObject.put("data",jsonData);
            Log.d("Req. save Location", jsonObject.toString());
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, HttpURL.LOCATION,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject data) {
                    Log.d("Res. save Location", data.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.toString());
                }
            });
            BaseActivity.getSocketTimeOutStatic(objectRequest);
            Mysingleton.getInstance(getApplicationContext()).addToRequsetque(objectRequest);
        } catch (Exception e) {
            Log.d("data failuer", e.toString());
            e.printStackTrace();
        }

    }

}
