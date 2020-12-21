package msmartds.in;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.location.GPSTrackerPresenter;
import msmartds.in.utility.Keys;
import msmartds.in.utility.Mysingleton;
import msmartds.in.utility.Util;

/**
 * Created by Smartkinda on 6/19/2017.
 */

public class PushMoneyActivity extends BaseActivity implements GPSTrackerPresenter.LocationListener {

    private SharedPreferences sharedPreferences;
    private String distributorID, key;
    private Button btnPushMoney, btnClose;
    private TextView tviewAgentId, tviewAgentName, tviewAgencyName, tviewAgentBalance;
    private EditText editAmount, editRemark;
    private String dataAgentID, dataFirmName, dataBalance;
    private String url = HttpURL.PushMoneyURL;
    private String url1 = HttpURL.DetailAgentURL;
    private ProgressDialog pd;
    private JSONObject jsonObject;

    private GPSTrackerPresenter gpsTrackerPresenter = null;
    private boolean isTxnClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.push_money_activity);

        Toolbar toolbar = findViewById(msmartds.in.R.id.toolbar);
        toolbar.setTitle("Push Balance");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        gpsTrackerPresenter = new GPSTrackerPresenter(this, this, GPSTrackerPresenter.GPS_IS_ON__OR_OFF_CODE);
        dataAgentID = getIntent().getStringExtra("AgentID");
        dataFirmName = getIntent().getStringExtra("FirmName");
        dataBalance = getIntent().getStringExtra("Balance");

        System.out.println("intent_data-->" + dataAgentID + ":" + dataFirmName + ":" + dataBalance);

        sharedPreferences = getApplication().getSharedPreferences("Details", Context.MODE_PRIVATE);
        distributorID = sharedPreferences.getString("distributorId", null);
        key = sharedPreferences.getString("txnKey", null);

        btnPushMoney = (Button) findViewById(msmartds.in.R.id.txtsubmit);
        btnClose = (Button) findViewById(msmartds.in.R.id.close_button);
        tviewAgentId = (TextView) findViewById(msmartds.in.R.id.tv_agent_id);
        tviewAgentName = (TextView) findViewById(msmartds.in.R.id.tv_agent_name);
        tviewAgencyName = (TextView) findViewById(msmartds.in.R.id.tv_agency_name);
        tviewAgentBalance = (TextView) findViewById(msmartds.in.R.id.tv_avail_balance);
        editAmount = (EditText) findViewById(msmartds.in.R.id.edit_amount);
        editRemark = (EditText) findViewById(msmartds.in.R.id.edit_remark);

        tviewAgentId.setText(dataAgentID);
        tviewAgencyName.setText(dataFirmName);
        tviewAgentBalance.setText(dataBalance);

        viewAgentDetails();

        btnPushMoney.setOnClickListener(v -> {
            if (TextUtils.isEmpty(editAmount.getText().toString().trim())) {
                editAmount.requestFocus();
                Toast.makeText(PushMoneyActivity.this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
            } else if (editAmount.equals("")) {
                Toast.makeText(PushMoneyActivity.this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            } else {
                if (!isTxnClick) {
                    isTxnClick = true;
                    gpsTrackerPresenter.checkGpsOnOrNot(GPSTrackerPresenter.GPS_IS_ON__OR_OFF_CODE);
                }
            }
        });
    }

    private void pushMoneyRequest() {
        final String Amount = editAmount.getText().toString().trim();
        final String Remark = editRemark.getText().toString().trim();

        pd = ProgressDialog.show(PushMoneyActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        JsonObjectRequest jsonrequest = null;
        try {

            JSONObject jsonReq = new JSONObject()
                    .put("distributorId", distributorID)
                    .put("txnkey", key)
                    .put("agentId", dataAgentID)
                    .put("TransaferAmount", Amount)
                    .put("PaymentRemark", Remark)
                    .put("latitude", Util.LoadPrefData(getApplicationContext(), Keys.LATITUDE))
                    .put("longitude", Util.LoadPrefData(getApplicationContext(), Keys.LONGITUDE));

            jsonrequest = new JsonObjectRequest(Request.Method.POST, url, jsonReq,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject object) {
                            jsonObject = new JSONObject();
                            jsonObject = object;
                            pd.dismiss();
                            Log.d("data Request-->", distributorID + ":" + key + ":" + dataAgentID + ":" + Remark.toString());
                            System.out.println("Object----1>" + object.toString());
                            try {
                                if (object.getString("status").equalsIgnoreCase("0")) {
                                    Log.d("url-called", url);
                                    Log.d("url data", object.toString());
                                    showConfirmationDialog();
                                } else {
                                    showConfirmationDialog();
                                }
                            } catch (JSONException e) {
                                pd.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(PushMoneyActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            pd.dismiss();
            e.printStackTrace();
        }
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(PushMoneyActivity.this).addToRequsetque(jsonrequest);
    }
    //=====================================================

    public void showConfirmationDialog() {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(PushMoneyActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        d.setCancelable(false);

        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        d.setContentView(msmartds.in.R.layout.push_payment_confirmation_dialog);

        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Button btnSubmit = (Button) d.findViewById(msmartds.in.R.id.btn_push_submit);
        final Button btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_push_button);
        final TextView tvConfirmation = (TextView) d.findViewById(msmartds.in.R.id.tv_confirmation_dialog);
        try {
            //        Toast.makeText(PushMoneyActivity.this, "--->"+jsonObject.getString("message").toString(), Toast.LENGTH_SHORT).show();

            tvConfirmation.setText(jsonObject.getString("message").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnSubmit.setOnClickListener(v -> {
            try {
                if (jsonObject.getString("status").equalsIgnoreCase("0")) {
                    Intent intent = new Intent(PushMoneyActivity.this, DashBoardActivity.class);
                    startActivity(intent);
                    PushMoneyActivity.this.finish();
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

    private void viewAgentDetails() {

        JsonObjectRequest jsonrequest = null;
        try {
            jsonrequest = new JsonObjectRequest(Request.Method.POST, url1,
                    new JSONObject()
                            .put("distributorId", distributorID)
                            .put("txnkey", key)
                            .put("agentId", dataAgentID)
                            .put("param", "singleAgentDetail"),

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject object) {
                            jsonObject = new JSONObject();
                            jsonObject = object;

                            Log.d("data Request-->", distributorID + ":" + key + ":" + dataAgentID);
                            System.out.println("Object----1>" + object.toString());
                            try {
                                if (object.getString("status").equalsIgnoreCase("0")) {
                                    Log.d("url-called", url1);
                                    Log.d("url data", object.toString());
                                    tviewAgentName.setText(object.getString("agentName"));
                                } else {
                                    //          showConfirmationDialog();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(PushMoneyActivity.this).addToRequsetque(jsonrequest);
    }

    //--------------------------------------------GPS Tracker--------------------------------------------------------------

    @Override
    public void onLocationFound(Location location) {
        gpsTrackerPresenter.stopLocationUpdates();
        if (isTxnClick) {
            isTxnClick = false;
            pushMoneyRequest();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
