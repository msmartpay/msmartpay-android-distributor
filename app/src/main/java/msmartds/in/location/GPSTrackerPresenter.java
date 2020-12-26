package msmartds.in.location;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import msmartds.in.utility.Keys;
import msmartds.in.utility.Util;


public class GPSTrackerPresenter {
    private static final String TAG = GPSTrackerPresenter.class.getSimpleName();
    private static final int UPDATE_INTERVAL = 600000; // 10 min
    private static final int FASTEST_INTERVAL = 10000; // 5 min
    private static final int DISPLACEMENT = 10; // 1 km
    public static final int RUN_TIME_PERMISSION_CODE = 999;
    public static final int GPS_IS_ON__OR_OFF_CODE = 9911;

    private LocationCallback mLocationCallback = null;
    private FusedLocationProviderClient mFusedLocationClient = null;
    private LocationRequest mLocationRequest = null;
    private LocationManager locationManager = null;
    private Activity mActivity = null;
    private LocationListener mListener = null;
    private int requestCode = 0;

    public GPSTrackerPresenter(Activity mActivity, LocationListener mListener, int requestCode) {
        this.mActivity = mActivity;
        this.mListener = mListener;
        this.requestCode = requestCode;
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
           getLastLocationSuccess();
            createLocationRequest();
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    for (Location location : locationResult.getLocations()) {
                        Util.SavePrefData(mActivity, Keys.LATITUDE, location.getLatitude() + "");
                        Util.SavePrefData(mActivity, Keys.LONGITUDE, location.getLongitude() + "");
                        mListener.onLocationFound(location);
                    }
                }
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Here, we assigned required minimum and maximum duration that explained above
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    //In the start method, we chacking permissions. It'will invoke a permision required pop if required permission not given
    public void onStart() {
        if (!checkRequiredLocationPermission(true)) {
            getLastLocation();
        } else {
            startLocationUpdates();
        }
    }

    public void checkGpsOnOrNot(int requestCode) {
        if (isGpsOnOrNot()) {
            onStart();
        } else {
            openInfoGPSSettingsDialog(requestCode);
        }
    }

    public boolean isGpsOnOrNot() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public void onPause() {
        stopLocationUpdates();
    }

    private Boolean checkRequiredLocationPermission(boolean isRequestPermissions) {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (isRequestPermissions) {
                // mListener.checkRequiredLocationPermission();
                checkPermissions();
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check Gps setting IN/OFF state. If GPS OFF it'll show on popup.
     */
    private void checkGpsSetting(int requestCode) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(mActivity).checkLocationSettings(builder.build());

        task.addOnCompleteListener(result -> {
            try {
                result.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    // Location settings are not satisfied. But could be fixed by showing the
                    // user a dialog.
                    // Cast to a resolvable exception.
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ((ResolvableApiException) exception).startResolutionForResult(mActivity, requestCode);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    // Location settings are not satisfied. But could be fixed by showing the
                    // user a dialog.
                    // Cast to a resolvable exception.
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Empty
                        break;
                }
            }
        });
    }

    private void openInfoGPSSettingsDialog(int requestCode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Location Permission")
                .setMessage("MSmartPay needs your location information before any transaction to detect suspicious activity and keep your account safe")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    checkGpsSetting(requestCode);
                    dialog.dismiss();
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLastLocationSuccess() {
        if (checkRequiredLocationPermission(false))
            mFusedLocationClient.getLastLocation().addOnSuccessListener(mActivity, location -> {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    Util.SavePrefData(mActivity, Keys.LATITUDE, location.getLatitude() + "");
                    Util.SavePrefData(mActivity, Keys.LONGITUDE, location.getLongitude() + "");
                    mListener.onLocationFound(location);
                }
            });
    }
    private void getLastLocation() {
        if (checkRequiredLocationPermission(false))
        mFusedLocationClient.getLastLocation().addOnCompleteListener(mActivity, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Util.SavePrefData(mActivity, Keys.LATITUDE, task.getResult().getLatitude() + "");
                Util.SavePrefData(mActivity, Keys.LONGITUDE, task.getResult().getLongitude() + "");
                mListener.onLocationFound(task.getResult());
            } else {
                mListener.locationError("No Location Detected : " + task.getException());
                Log.e("", "no_location_detected", task.getException());
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void checkPermissions() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            //list.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        Dexter.withContext(mActivity)
                .withPermissions(list)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Log.e(TAG, "areAllPermissionsGranted");
                            onStart();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            Log.e(TAG, "isAnyPermissionPermanentlyDenied");
                            Util.openSettingsDialog(mActivity, GPS_IS_ON__OR_OFF_CODE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(dexterError ->
                Log.e(TAG, dexterError.name()))
                .onSameThread()
                .check();
    }

    public interface LocationListener {
        void onLocationFound(Location location);

        void locationError(String msg);

        //void checkRequiredLocationPermission();
    }
}
