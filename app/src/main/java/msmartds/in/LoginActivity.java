package msmartds.in;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import msmartds.in.utility.Keys;
import msmartds.in.utility.Mysingleton;

public class LoginActivity extends BaseActivity {
    private EditText email, password;
    private String Logo, Name, Email, BrandID, Mobile, Address, Key;
    private String Emailtext, Passwordtext;
    private TextView forgetPassword;
    private Button login, btnSignUp, btnSubmit, btnClosed;
    private EditText editEmailID;
    private String url = HttpURL.LoginURL;
    private String forgetPassUrl = HttpURL.ForgetPassURL;
    private ProgressDialog pd;
    private JSONObject jsonObject;
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefsEditor;
    private Context context;
    private CheckBox checkBox;
    private AppDatabase appDatabase;

    public static final int MY_IGNORE_OPTIMIZATION_REQUEST = 111;
    PowerManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.activity_login);

        context = LoginActivity.this;
        appDatabase = DatabaseClient.getInstance(context).getAppDatabase();
        myPrefs = getSharedPreferences("Details", MODE_PRIVATE);
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
                pd = ProgressDialog.show(LoginActivity.this, "", "Loading. Please wait...", true);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();

                try {
                    checkRequest();
                } catch (JSONException e) {
                    System.out.println("Login_Exception-->" + e.toString());
                    e.printStackTrace();
                }

            }
        });

        forgetPassword.setOnClickListener(v -> showAgentCustomDialog());

        //for Sign up page

        btnSignUp.setOnClickListener(v -> {
            Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
            finish();
        });

        enableBackgroundService();
    }

    @SuppressLint("BatteryLife")
    private void enableBackgroundService(){
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isIgnoringBatteryOptimizations = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            assert pm != null;
            isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());

            if (!isIgnoringBatteryOptimizations) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, MY_IGNORE_OPTIMIZATION_REQUEST);

            }else {
                // Save File data and User Save
                new  GetCredentialTask().execute();
            }
        }else {
            // Save File data and User Save
            new  GetCredentialTask().execute();
        }

    }

    private void checkRequest() throws JSONException {

        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject()
                        .put("userName", Emailtext)
                        .put("password", Passwordtext)
                        .put("userType", "SSZ")
                        .put("param", "login"),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        pd.dismiss();
                        System.out.println("Object---->" + object.toString());
                        try {
                            if (object.getString("message").equalsIgnoreCase("Login Success")) {
                                Log.d("url-called", url);
                                Log.d("url data", object.toString());
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
                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(LoginActivity.this).addToRequsetque(jsonrequest);
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        btnClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                d.cancel();
            }
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
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (jsonObject.getString("status").equalsIgnoreCase("0")) {
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
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
            }
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

            List<Credentials> credentialsList =  appDatabase.appDBDao().getAll();
            if(credentialsList!=null && credentialsList.size()>0){
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
    class GetCredentialTask extends AsyncTask<Object,Object,Object>{
        String user="",pass="";
        boolean flag =false;

        @Override
        protected Object doInBackground(Object... objects) {
            List<Credentials> credentialsList =  appDatabase.appDBDao().getAll();
            if(credentialsList!=null && credentialsList.size()>0){
                user = credentialsList.get(0).getUser();
                pass = credentialsList.get(0).getPassword();
                 if (myPrefs != null) {
                     String response = myPrefs.getString("emailID", null);
                     // Toast.makeText(getActivity(), "xml not null", Toast.LENGTH_LONG).show();
                    if (response != null && response.length() > 0) {
                        flag=true;
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

            if(flag){
                Intent loginIntent = new Intent(getApplicationContext(), DashBoardActivity.class);
                startActivity(loginIntent);
                finish();
            }

        }
    }
}
