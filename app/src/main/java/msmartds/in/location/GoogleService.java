package msmartds.in.location;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.utility.Keys;
import msmartds.in.utility.L;
import msmartds.in.utility.Mysingleton;
import msmartds.in.utility.Util;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class GoogleService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    boolean status;

    public static final String TAG = "GoogleService";
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    boolean isPassiveEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private LocationRequest mLocationRequest = new LocationRequest();
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 30 * 2 * 1000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    // APICall call;
    public static GoogleService instance = null;
    public String u_id;
    private GoogleApiClient mLocationClient;


    public GoogleService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // startLocationUpdates();

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);

        mLocationClient.connect();

       // fn_getlocation();
        getLocationFn();


        return START_STICKY;
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_LOCATION_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        locationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // GPS location can be null if GPS is switched off
                    if (location != null) {
                        onLocationChanged(location);
                    }
                })
                .addOnFailureListener(e -> {
                    L.m2("MapDemoActivity", "Error trying to get last GPS location");
                    e.printStackTrace();
                });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //call = new APICall(this);
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), notify_interval, notify_interval);
        intent = new Intent(str_receiver);

    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(() -> {
                getLocationFn();
              //  fn_getlocation();
            });

        }

    }

    private void fn_getlocation() {

        assert locationManager != null;
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (!isGPSEnable && !isNetworkEnable) {

        } else {
            location = null;
            if (isNetworkEnable) {
                L.m2(TAG, "isNetworkEnable");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, /*3000*/notify_interval, /*10*/200, this);
                if (locationManager != null) {
                    try {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            if (isGPSEnable) {
                L.m2(TAG, "isGPSEnable");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, /*3000*/notify_interval, /*10*/200, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            if (location != null) {
                saveLocationToServer(location);
            }
        }

    }

    private void getLocationFn() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {

            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            assert locationManager != null;
            Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            location = null;
            if (locationGps != null) {

                L.m2(TAG, "isGPSEnable");
                location = locationGps;
               /* latitude = locationGps.getLatitude();
                longitude = locationGps.getLongitude();
                saveLocationToServer(locationGps);*/
            } else if (locationNetwork != null) {
                L.m2(TAG, "isNetworkEnable");
                location = locationNetwork;
                /*saveLocationToServer(locationNetwork);
                latitude = locationNetwork.getLatitude();
                longitude = locationNetwork.getLongitude();*/
            } else if (locationPassive != null) {
                L.m2(TAG, "isPassiveEnable");
                location = locationPassive;
               /* saveLocationToServer(locationPassive);
                latitude = locationPassive.getLatitude();
                longitude = locationPassive.getLongitude();*/
            }

            if (location!=null){
                saveLocationToServer(location);
            }

            //Thats All Run Your App
        }

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
//        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        L.m2(TAG, "Connected to Google API");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        L.m2(TAG, "Location changed");

        if (location != null) {
            L.m2(TAG, "== location != null");
            L.m2("Location", location.getLatitude() + " , " + location.getLongitude());
            //fn_getlocation();
            saveLocationToServer(location);
        } else {
            L.m2("Location", null + "" + null);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.SavePrefBoolean(getApplicationContext(), Keys.START_LOCATION_SERVICE, false);
    }
}
