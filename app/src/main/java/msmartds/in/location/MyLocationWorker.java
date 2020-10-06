package msmartds.in.location;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.utility.Keys;
import msmartds.in.utility.Mysingleton;
import msmartds.in.utility.Util;


public class MyLocationWorker extends Worker {

	private static final String DEFAULT_START_TIME = "08:00";
	private static final String DEFAULT_END_TIME = "19:00";

	private static final String TAG = "MyLocationWorker";

	/**
	 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
	 */
	private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

	/**
	 * The fastest rate for active location updates. Updates will never be more frequent
	 * than this value.
	 */
	private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
			UPDATE_INTERVAL_IN_MILLISECONDS / 2;
	/**
	 * The current location.
	 */
	private Location mLocation;

	/**
	 * Provides access to the Fused Location Provider API.
	 */
	private FusedLocationProviderClient mFusedLocationClient;

	private Context mContext;
	/**
	 * Callback for changes in location.
	 */
	private LocationCallback mLocationCallback;

	public MyLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
		mContext = context;
	}

	@NonNull
	@Override
	public Result doWork() {
			/*Log.d(TAG, "doWork: Done");

		Log.d(TAG, "onStartJob: STARTING JOB..");

		DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		String formattedDate = dateFormat.format(date);

		Date currentDate = dateFormat.parse(formattedDate);
			Date startDate = dateFormat.parse(DEFAULT_START_TIME);
			Date endDate = dateFormat.parse(DEFAULT_END_TIME);

			if (currentDate.after(startDate) && currentDate.before(endDate)) {
			*/
		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
		mLocationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				super.onLocationResult(locationResult);
			}
		};

		LocationRequest mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		try {
			mFusedLocationClient
					.getLastLocation()
					.addOnCompleteListener(task -> {
						if (task.isSuccessful() && task.getResult() != null) {
							mLocation = task.getResult();
							saveLocationToServer();

							/*// Create the NotificationChannel, but only on API 26+ because
							// the NotificationChannel class is new and not in the support library
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
								CharSequence name = mContext.getString(R.string.app_name);
								String description = mContext.getString(R.string.app_name);
								int importance = NotificationManager.IMPORTANCE_DEFAULT;
								NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.app_name), name, importance);
								channel.setDescription(description);
								// RegisterFragment the channel with the system; you can't change the importance
								// or other notification behaviors after this
								NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
								notificationManager.createNotificationChannel(channel);
							}

							NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.app_name))
									.setSmallIcon(android.R.drawable.ic_menu_mylocation)
									.setContentTitle("New Location Update")
									.setContentText("You are at " + getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude()))
									.setPriority(NotificationCompat.PRIORITY_DEFAULT)
									.setStyle(new NotificationCompat.BigTextStyle().bigText("You are at " + getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude())));

							NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

							// notificationId is a unique int for each notification that you must define
							notificationManager.notify(1001, builder.build());
*/
							mFusedLocationClient.removeLocationUpdates(mLocationCallback);
						} else {
							Log.w(TAG, "Failed to get location.");
						}
					});
		} catch (SecurityException unlikely) {
			Log.e(TAG, "Lost location permission." + unlikely);
		}

		try {
			mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
		} catch (SecurityException unlikely) {
			//Utils.setRequestingLocationUpdates(this, false);
			Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
		}
			/*} else {
				Log.d(TAG, "Time up to get location. Your time is : " + DEFAULT_START_TIME + " to " + DEFAULT_END_TIME);
			}*/

		return Result.success();
	}
	private void saveLocationToServer() {
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

	public static void startLocationWorker(Context context){
		// START Worker
		PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyLocationWorker.class, 15, TimeUnit.SECONDS)
				.addTag(TAG)
				.build();
		WorkManager.getInstance(context).enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);

	}

	public static void cancelLocationWorker(Context context){
		WorkManager.getInstance(context).cancelAllWorkByTag(TAG);

	}

	public static List<WorkInfo> getWorkInfoList(Context context) throws ExecutionException, InterruptedException {
		return WorkManager.getInstance(context).getWorkInfosByTag(TAG).get();
	}
}