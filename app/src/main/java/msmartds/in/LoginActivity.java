package msmartds.in;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.db.AppDatabase;
import msmartds.in.db.Credentials;
import msmartds.in.db.DatabaseClient;
import msmartds.in.location.GPSTrackerPresenter;
import msmartds.in.utility.Keys;
import msmartds.in.utility.L;
import msmartds.in.utility.Mysingleton;
import msmartds.in.utility.Util;

public class LoginActivity extends BaseActivity implements GPSTrackerPresenter.LocationListener {
    private EditText email, password;
    private String Emailtext, Passwordtext;
    private TextView forgetPassword;
    private Button login, btnSignUp, btnSubmit, btnClosed;
    private EditText editEmailID;
    private String URL_LOGIN = HttpURL.LoginURL;
    private String forgetPassUrl = HttpURL.ForgetPassURL;
    private ProgressDialog pd;
    private JSONObject jsonObject;
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefsEditor;
    private Context context;
    private CheckBox checkBox;
    private AppDatabase appDatabase;


    private GPSTrackerPresenter gpsTrackerPresenter = null;
    private boolean isTxnClick = false;
    private boolean isLocationGet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.activity_login);

        context = LoginActivity.this;
        gpsTrackerPresenter = new GPSTrackerPresenter(this, this, GPSTrackerPresenter.GPS_IS_ON__OR_OFF_CODE);
        appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        myPrefs = Util.getMyPref(this);
        prefsEditor = myPrefs.edit();

        email = (EditText) findViewById(msmartds.in.R.id.emailid);
        password = (EditText) findViewById(msmartds.in.R.id.password);
        login = (Button) findViewById(msmartds.in.R.id.submit);
        btnSignUp = (Button) findViewById(msmartds.in.R.id.new_user);
        forgetPassword = (TextView) findViewById(msmartds.in.R.id.forget_password);
        checkBox = (CheckBox) findViewById(msmartds.in.R.id.checkBox);


        login.setOnClickListener(view -> {
            Emailtext = email.getText().toString().trim();
            Passwordtext = password.getText().toString().trim();
            if (TextUtils.isEmpty(email.getText().toString().trim()) && email.getText().toString().trim().length() < 10) {
                Toast.makeText(context, "Please Enter Correct User Id !!!", Toast.LENGTH_SHORT).show();
            } /*else if (!Service.isValidEmail(Emailtext)) {
                Toast.makeText(getApplicationContext(), "Please Enter Valid Email ID", Toast.LENGTH_SHORT).show();
            }*/ else if (TextUtils.isEmpty(password.getText().toString().trim()) && password.getText().toString().trim().length() < 6) {
                Toast.makeText(context, "Please Enter Correct Password and Password > 6 !!!", Toast.LENGTH_SHORT).show();
            } else {
                if (checkBox.isChecked()) {
                    SaveCredentialsTask task = new SaveCredentialsTask();
                    task.execute();
                }
                loginProcess();
            }
        });

        forgetPassword.setOnClickListener(v -> showAgentCustomDialog());

        //for Sign up page

        btnSignUp.setOnClickListener(v -> {
            Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
            finish();
        });

        new GetCredentialTask().execute();
    }

    private void loginProcess() {
       /* if (Util.isPowerSaveMode(LoginActivity.this)) {
            loginRequest();
        } else {*/
            if (isLocationGet) {
                loginRequest();
            } else {
                if (!isTxnClick) {
                    isTxnClick = true;
                    gpsTrackerPresenter.checkGpsOnOrNot(GPSTrackerPresenter.GPS_IS_ON__OR_OFF_CODE);
                }
            }
        //}
    }

    private void loginRequest() {
        try {
            pd = ProgressDialog.show(LoginActivity.this, "", "Loading. Please wait...", true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
            JSONObject req = new JSONObject()
                    .put("userName", Emailtext)
                    .put("password", Passwordtext)
                    .put("userType", "SSZ")
                    .put("param", "login")
                    .put("latitude", Util.LoadPrefData(getApplicationContext(), Keys.LATITUDE))
                    .put("longitude", Util.LoadPrefData(getApplicationContext(), Keys.LONGITUDE));

            L.m2("request_url", URL_LOGIN.toString());
            L.m2("request", req.toString());
            JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, URL_LOGIN,
                    req,
                    object -> {
                        pd.dismiss();
                        L.m2("response", object.toString());
                        try {
                            if (object.getString("message").equalsIgnoreCase("Login Success")) {
                                String Message = object.getString("message");

                                prefsEditor.putString("DistributorName", object.getString("DistributorName"));
                                prefsEditor.putString(Keys.DS_ID, object.getString("distributorId"));
                                prefsEditor.putString("distributorInitial", object.getString("distributorInitial"));
                                prefsEditor.putString("companyName", object.getString("companyName"));
                                prefsEditor.putString("clientId", object.getString("clientId"));
                                prefsEditor.putString("loginUrl", object.getString("loginUrl"));
                                prefsEditor.putString("innerHeaderImage", object.getString("innerHeaderImage"));
                                prefsEditor.putString("LogoURL", object.getString("loginUrl") + "/" + object.getString("innerHeaderImage"));
                                prefsEditor.putString("poweredBy", object.getString("poweredBy"));
                                prefsEditor.putString("panelType", object.getString("panelType"));
                                prefsEditor.putString("domainName", object.getString("domainName"));
                                prefsEditor.putString("balance", object.getString("balance"));
                                prefsEditor.putString("mdID", object.getString("mdID"));
                                prefsEditor.putString("WhitelableCompanyName", object.getString("WhitelableCompanyName"));
                                prefsEditor.putString("MobileNo", object.getString("MobileNo"));
                                prefsEditor.putString("agentLoginUrl", object.getString("agentLoginUrl"));
                                prefsEditor.putString("agentCellEmailId", object.getString("agentCellEmailId"));
                                prefsEditor.putString("TickerMessage", object.getString("TickerMessage"));
                                prefsEditor.putString(Keys.TXN_KEY, object.getString("txnKey"));
                                prefsEditor.putString("message", object.getString("message"));
                                prefsEditor.putString("status", object.getString("status"));
                                prefsEditor.putString("emailID", Emailtext);
                                prefsEditor.putString("password", Passwordtext);
                                prefsEditor.commit();
                                Intent in = new Intent(LoginActivity.this, DashBoardActivity.class);
                                startActivity(in);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, object.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            });
            getSocketTimeOut(jsonrequest);
            Mysingleton.getInstance(LoginActivity.this).addToRequsetque(jsonrequest);
        } catch (Exception e) {
            pd.dismiss();
            System.out.println("Login_Exception-->" + e.toString());
            e.printStackTrace();
        }
    }

//=======================================================

    public void showAgentCustomDialog() {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(LoginActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        d.setCancelable(false);
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        d.setContentView(msmartds.in.R.layout.forget_password_dialog);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnSubmit = (Button) d.findViewById(msmartds.in.R.id.txtsubmit);
        btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_button);
        editEmailID = (EditText) d.findViewById(msmartds.in.R.id.emailid);

        btnSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(editEmailID.getText().toString().trim())) {
            } else {
                String emailID = editEmailID.getText().toString().trim();
                try {
                    requestForgetPass(emailID);
                    d.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            d.cancel();
        });

        d.show();

    }

    //=======================
    private void requestForgetPass(String emailID) throws JSONException {

        pd = ProgressDialog.show(LoginActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, forgetPassUrl,
                new JSONObject()
                        .put("userName", emailID)
                        .put("userType", "SSZ"),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        pd.dismiss();
                        jsonObject = new JSONObject();
                        jsonObject = object;

                        System.out.println("Object---->" + object.toString());
                        try {
                            if (object.getString("status").equalsIgnoreCase("0")) {
                                showConfirmationDialog(object.getString("message").toString());
                            } else {
                                showConfirmationDialog(object.getString("message").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(LoginActivity.this).addToRequsetque(jsonrequest);
    }

    //Dialog for confirmation
    @SuppressLint("SetTextI18n")
    public void showConfirmationDialog(String msg) {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(LoginActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        d.setCancelable(false);

        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        d.setContentView(msmartds.in.R.layout.push_payment_confirmation_dialog);

        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Button btnSubmit = (Button) d.findViewById(msmartds.in.R.id.btn_push_submit);
        final Button btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_push_button);
        final TextView header = (TextView) d.findViewById(msmartds.in.R.id.title);
        final TextView tvConfirmation = (TextView) d.findViewById(msmartds.in.R.id.tv_confirmation_dialog);

        header.setText("Confirmation");
        tvConfirmation.setText(msg);

        btnSubmit.setOnClickListener(v -> {
            try {
                if (jsonObject.getString("status").equalsIgnoreCase("0")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    d.dismiss();
                } else {
                    d.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            d.cancel();
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            d.cancel();
        });
        d.show();
    }

    @SuppressLint("StaticFieldLeak")
    class SaveCredentialsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            List<Credentials> credentialsList = appDatabase.appDBDao().getAll();
            if (credentialsList != null && credentialsList.size() > 0) {
                appDatabase.appDBDao().delete();
            }
            //creating a task
            Credentials credentials = new Credentials();
            credentials.setUser(Emailtext);
            credentials.setPassword(Passwordtext);
            //adding to database
            appDatabase.appDBDao().insert(credentials);
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    class GetCredentialTask extends AsyncTask<Object, Object, Object> {
        String user = "", pass = "";
        boolean flag = false;

        @Override
        protected Object doInBackground(Object... objects) {
            List<Credentials> credentialsList = appDatabase.appDBDao().getAll();
            if (credentialsList != null && credentialsList.size() > 0) {
                user = credentialsList.get(0).getUser();
                pass = credentialsList.get(0).getPassword();
                if (myPrefs != null) {
                    String response = myPrefs.getString("emailID", null);
                    // Toast.makeText(getActivity(), "xml not null", Toast.LENGTH_LONG).show();
                    if (response != null && response.length() > 0) {
                        flag = true;
                    } else {
                        //  Toast.makeText(getApplicationContext(), "Data is not stored in xml", Toast.LENGTH_LONG);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            email.setText(user);
            password.setText(pass);

            if (flag) {
                Intent loginIntent = new Intent(getApplicationContext(), DashBoardActivity.class);
                startActivity(loginIntent);
                finish();
            }

        }
    }

    //--------------------------------------------GPS Tracker--------------------------------------------------------------

    @Override
    public void onLocationFound(Location location) {
        isLocationGet = true;
        gpsTrackerPresenter.stopLocationUpdates();
        if (isTxnClick) {
            isTxnClick = false;
            loginProcess();
        }
    }

    @Override
    public void locationError(String msg) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPSTrackerPresenter.GPS_IS_ON__OR_OFF_CODE && resultCode == Activity.RESULT_OK) {
            gpsTrackerPresenter.onStart();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gpsTrackerPresenter.onStart();
    }

//--------------------------------------------End GPS Tracker--------------------------------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gpsTrackerPresenter.onPause();
    }
}
