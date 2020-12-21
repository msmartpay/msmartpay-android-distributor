package msmartds.in.location;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

import msmartds.in.BuildConfig;
import msmartds.in.DashBoardActivity;
import msmartds.in.R;
import msmartds.in.utility.Keys;
import msmartds.in.utility.Util;

public class ForegroundOnlyLocationService extends Service {

    private static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    public static final String ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST = PACKAGE_NAME + ".action.FOREGROUND_ONLY_LOCATION_BROADCAST";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".extra.LOCATION";

    public static final String EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            PACKAGE_NAME + ".extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION";

    private static final int NOTIFICATION_ID = 12345678;

    private static final String NOTIFICATION_CHANNEL_ID = "while_in_use_channel_01";

    private static final String TAG = ForegroundOnlyLocationService.class.getSimpleName();

    private boolean mChangingConfiguration = false;
    private boolean serviceRunningInForeground = false;
    private final IBinder localBinde = new LocalBinder();
    private NotificationManager notificationManager;
    // TODO: Step 1.1, Review variables (no changes).
    // FusedLocationProviderClient - Main class for receiving location updates.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // LocationRequest - Requirements for the location updates, i.e., how often you should receive
    // updates, the priority, etc.
    private LocationRequest locationRequest;

    // LocationCallback - Called when FusedLocationProviderClient has a new Location.
    private LocationCallback locationCallback;

    // Used only for local storage of the last known location. Usually, this would be saved to your
    // database, but because this is a simplified sample without a full database, we only need the
    // last location to create a Notification if the user navigates away from the app.
    private Location currentLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "in onCreate()");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // TODO: Step 1.2, Review the FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // TODO: Step 1.3, Create a LocationRequest.
        locationRequest = new LocationRequest();
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(10));
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(5));
        locationRequest.setMaxWaitTime(TimeUnit.MINUTES.toMillis(1));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
    }

    private void onNewLocation(Location lastLocation) {
        if (lastLocation != null) {

            // Normally, you want to save a new location to a database. We are simplifying
            // things a bit and just saving it as a local variable, as we only need it again
            // if a Notification is created (when the user navigates away from app).
            currentLocation = lastLocation;

            Util.SavePrefData(getApplicationContext(), Keys.LATITUDE, String.valueOf(currentLocation.getLatitude()));
            Util.SavePrefData(getApplicationContext(), Keys.LONGITUDE, String.valueOf(currentLocation.getLongitude()));
            Log.e(TAG, "Location : " + currentLocation.toString());
            // Notify our Activity that a new location was added. Again, if this was a
            // production app, the Activity would be listening for changes to a database
            // with new locations, but we are simplifying things a bit to focus on just
            // learning the location side of things.
            Intent intent = new Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST);
            intent.putExtra(EXTRA_LOCATION, currentLocation);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            // Updates notification content if this service is running as a foreground
            // service.
            if (serviceRunningInForeground) {
                Util.SavePrefBoolean(getApplicationContext(), Keys.APP_IN_BACKGROUND, false);

                notificationManager.notify(NOTIFICATION_ID, generateNotification(currentLocation));
            } else {
                Util.SavePrefBoolean(getApplicationContext(), Keys.APP_IN_BACKGROUND, true);
            }
            /*//Save to server
            if (!pref.getUserId().isNullOrEmpty()) {
                SaveLocationWorker.startWorker(applicationContext)
            } else {
                if (!pref.getAppIsBackGround()) {
                    unsubscribeToLocationUpdates()
                    pref.clearPrefs()
                    val mIntent = Intent(applicationContext, LoginActivity::class.java)
                    mIntent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mIntent)
                }
            }*/
        } else {
            Log.e(TAG, "Location missing in callback.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "in onStartCommand()");
        boolean cancelLocationTrackingFromNotification = intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false);
        if (cancelLocationTrackingFromNotification) {
            unsubscribeToLocationUpdates();
            stopSelf();
        }
        return START_NOT_STICKY;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "in onConfigurationChanged()");
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        serviceRunningInForeground = false;
        mChangingConfiguration = false;
        return localBinde;
    }

    @Override
    public void onRebind(Intent intent) {

        Log.i(TAG, "in onRebind()");
        // MainActivity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        serviceRunningInForeground = false;
        Util.SavePrefBoolean(getApplicationContext(), Keys.APP_IN_BACKGROUND, true);

        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // MainActivity (client) leaves foreground, so service needs to become a foreground service
        // to maintain the 'while-in-use' label.
        // NOTE: If this method is called due to a configuration change in MainActivity,
        // we do nothing.
        if (!mChangingConfiguration && Util.LoadPrefBoolean(getApplicationContext(), Keys.KEY_FOREGROUND_ENABLED)) {
            Log.e(TAG, "Start foreground service");
            Notification notification = generateNotification(currentLocation);
            startForeground(NOTIFICATION_ID, notification);
            serviceRunningInForeground = true;
            Util.SavePrefBoolean(getApplicationContext(), Keys.APP_IN_BACKGROUND, false);

        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    /*
     * Generates a BIG_TEXT_STYLE Notification that represent latest location.
     */
    private Notification generateNotification(Location location) {
        Log.e(TAG, "generateNotification()");

        // Main steps for building a BIG_TEXT_STYLE notification:
        //      0. Get data
        //      1. Create Notification Channel for O+
        //      2. Build the BIG_TEXT_STYLE
        //      3. Set up Intent / Pending Intent for notification
        //      4. Build and issue the notification

        // 0. Get data
        String address = "No Address";
        if (location != null) {
            address = Util.getCompleteAddressString(getApplicationContext(), location.getLatitude(), location.getLongitude());
        } else {

        }

        String titleText = "Current Location";

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] arr = {};
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setVibrationPattern(arr);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(false);
            notificationChannel.setSound(null, null);
            // Adds NotificationChannel to system. Attempting to create an
            // existing notification channel with its original values performs
            // no operation, so it's safe to perform the below sequence.
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // 2. Build the BIG_TEXT_STYLE.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(address)
                .setBigContentTitle(titleText);

        // 3. Set up main Intent/Pending Intents for notification.
        Intent launchActivityIntent = new Intent(this, DashBoardActivity.class);
        Intent cancelIntent = new Intent(this, ForegroundOnlyLocationService.class);
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true);
        PendingIntent servicePendingIntent =
                PendingIntent.getService(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, launchActivityIntent, 0);

        // 4. Build and issue the notification.
        // Notification Channel Id is ignored for Android pre O (26).
        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
        return notificationCompatBuilder
                .setStyle(bigTextStyle)
                .setContentTitle(titleText)
                .setContentText(address)
                .setSmallIcon(R.drawable.msmartpayicon)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                /* .addAction(
                    R.drawable.logo_nearme, "Launch",
                    activityPendingIntent
                )
               .addAction(
                    R.drawable.ic_baseline_close_24,
                    "stop_location_updates_button_text",
                    servicePendingIntent
                )*/
                .build();
    }

    public void subscribeToLocationUpdates() {
        Log.e(TAG, "subscribeToLocationUpdates()");

        Util.SavePrefBoolean(getApplicationContext(), Keys.KEY_FOREGROUND_ENABLED, true);

        // Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        // ensure this Service can be promoted to a foreground service, i.e., the service needs to
        // be officially started (which we do here).
        startService(new Intent(getApplicationContext(), ForegroundOnlyLocationService.class));

        try {
            // TODO: Step 1.5, Subscribe to location changes.
            fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.myLooper()
            );
        } catch (SecurityException unlikely) {
            Util.SavePrefBoolean(getApplicationContext(), Keys.KEY_FOREGROUND_ENABLED, false);
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely");
        }
    }

    public void unsubscribeToLocationUpdates() {
        Log.e(TAG, "unsubscribeToLocationUpdates()");

        try {
            // TODO: Step 1.6, Unsubscribe to location changes.
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Location Callback removed.");
                            stopSelf();
                        } else {
                            Log.e(TAG, "Failed to remove Location Callback. - " + Util.getGson().toJson(task));
                        }
                    });
            Util.SavePrefBoolean(getApplicationContext(), Keys.KEY_FOREGROUND_ENABLED, false);

        } catch (SecurityException unlikely) {
            Util.SavePrefBoolean(getApplicationContext(), Keys.KEY_FOREGROUND_ENABLED, true);
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely");
        }
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public ForegroundOnlyLocationService getService() {
            return ForegroundOnlyLocationService.this;
        }
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribeToLocationUpdates();
    }*/
}
