package msmartds.in;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.balRequest.BalanceRequest;
import msmartds.in.businessView.BusinessViewActivity;
import msmartds.in.location.GPSTrackerPresenter;
import msmartds.in.reports.ReportActivity;
import msmartds.in.utility.L;
import msmartds.in.utility.MyAppUpdateManager;
import msmartds.in.utility.Mysingleton;

public class DashBoardActivity extends DrawerActivity implements GPSTrackerPresenter.LocationListener {

    private Menu menu;
    private ListView list;
    private String Logo, Name, Email, BrandID, Mobile, Address, Key;
    private ImageView navgiation;
    private Button btnActiveAgent, btnDeActiveAgent, btnAddAgent, btnDepositeRequest, btnPushBalance, btnReport, home_balance_request, business;
    private Button btnSubmit, btnClosed;
    private TextView tViewDistributorBal, CopyRightText, PoweredByText;
    private Spinner spinnerAgentType;
    private String[] AgentTypeData = {"Select Option", "All Agents", "Active Agents", "De-Active Agents"};
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String AgentWiseTypeData;
    private EditText editAgentID;
    private ProgressDialog pd;
    private String url = HttpURL.ViewAgentURL;
    private String url_single_push = HttpURL.ViewAgentURL;
    private String url_dsBalance = HttpURL.DSDistributor;
    private String distributorID, key, agentStatus, distributorBal, EmailText, PasswordText, agentStatusMessege, CopyRight, PoweredBy;
    private int i;

    private GPSTrackerPresenter gpsTrackerPresenter = null;
    private boolean isTxnClick = false;
    // Declare the UpdateManager
    private MyAppUpdateManager mUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(msmartds.in.R.layout.dashboard, null, false);

        mDrawer.addView(view, 0);
        navgiation = (ImageView) findViewById(msmartds.in.R.id.openbar);
        navgiation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(drawerpane);
            }
        });

        //All Buttons ID
        btnActiveAgent = (Button) findViewById(R.id.home_view_agent);
        btnDeActiveAgent = (Button) findViewById(R.id.home_deactive_agent);
        btnAddAgent = (Button) findViewById(msmartds.in.R.id.home_add_agent);
        btnDepositeRequest = (Button) findViewById(msmartds.in.R.id.home_deposite);
        btnPushBalance = (Button) findViewById(msmartds.in.R.id.home_push_balance);
        home_balance_request = (Button) findViewById(msmartds.in.R.id.home_balance_request);
        btnReport = (Button) findViewById(msmartds.in.R.id.home_report);
        business = findViewById(msmartds.in.R.id.business);
        CopyRightText = (TextView) findViewById(msmartds.in.R.id.copy_rights);
        PoweredByText = (TextView) findViewById(msmartds.in.R.id.powered_by);

        sharedPreferences = getApplicationContext().getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        distributorID = sharedPreferences.getString("distributorId", null);
        key = sharedPreferences.getString("txnKey", null);
        distributorBal = sharedPreferences.getString("balance", null);
        CopyRight = sharedPreferences.getString("WhitelableCompanyName", null);
        PoweredBy = sharedPreferences.getString("poweredBy", null);
        agentStatusMessege = sharedPreferences.getString("message", null);
        EmailText = sharedPreferences.getString("emailID", null);
        PasswordText = sharedPreferences.getString("password", null);

        tViewDistributorBal = (TextView) findViewById(msmartds.in.R.id.distributor_balance);
        tViewDistributorBal.setText("\u20B9" + distributorBal);
        CopyRightText.setText(CopyRight);
        PoweredByText.setText(PoweredBy);

        btnActiveAgent.setOnClickListener(v -> showAgentCustomDialog());

        btnDeActiveAgent.setOnClickListener(v -> showAgentCustomDialog());

        btnAddAgent.setOnClickListener(v -> {
            Intent addAgentIntent = new Intent(DashBoardActivity.this, AddAgentActivity.class);
            startActivity(addAgentIntent);
        });

        btnPushBalance.setOnClickListener(v -> showPushBalanceCustomDialog());

        btnReport.setOnClickListener(v -> {
            Intent reportIntent = new Intent(DashBoardActivity.this, ReportActivity.class);
            startActivity(reportIntent);
        });

        home_balance_request.setOnClickListener(view1 -> {
            Intent reportIntent = new Intent(DashBoardActivity.this, BalanceRequest.class);
            startActivity(reportIntent);
        });

        business.setOnClickListener(v -> {
            Intent business = new Intent(DashBoardActivity.this, BusinessViewActivity.class);
            startActivity(business);
        });

        SharedPreferences myPrefs = DashBoardActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Name = myPrefs.getString("name", null);
        Email = myPrefs.getString("email", null);
        Address = myPrefs.getString("address", null);
        Mobile = myPrefs.getString("mobile", null);
        BrandID = myPrefs.getString("brandID", null);
        Key = myPrefs.getString("key", null);
        Logo = myPrefs.getString("logo", null);
        gpsTrackerPresenter = new GPSTrackerPresenter(this, this, GPSTrackerPresenter.GPS_IS_ON__OR_OFF_CODE);
        initializeAppUpdateManager();
    }


    public void showAgentCustomDialog() {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(DashBoardActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        d.setCancelable(false);

        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        d.setContentView(msmartds.in.R.layout.view_agent_status);

        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnSubmit = (Button) d.findViewById(msmartds.in.R.id.btn_submit);
        btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_button);
        spinnerAgentType = (Spinner) d.findViewById(msmartds.in.R.id.spinner);

        ArrayAdapter agentTypeAdaptor = new ArrayAdapter(this, msmartds.in.R.layout.spinner_textview_layout, AgentTypeData);
        agentTypeAdaptor.setDropDownViewResource(msmartds.in.R.layout.spinner_textview_layout);
        spinnerAgentType.setAdapter(agentTypeAdaptor);

        spinnerAgentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    AgentWiseTypeData = null;
                } else {
                    i = position;
                    AgentWiseTypeData = parent.getItemAtPosition(position).toString();
//                    Toast.makeText(parent.getContext(), "Selected: " + AgentWiseTypeData, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (AgentWiseTypeData == null) {
                Toast.makeText(DashBoardActivity.this, "Please select valid option !", Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (i == 1) {
                        agentStatus = "All";
                    } else if (i == 2) {
                        agentStatus = "Activate";
                    } else if (i == 3) {
                        agentStatus = "Deactive";
                    }
                    agentStatusRequest();
                    d.dismiss();
                } catch (JSONException e) {
                    System.out.println("agentList_Exception-->" + e.toString());
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

    public void showPushBalanceCustomDialog() {
        final Dialog d = new Dialog(DashBoardActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        d.setCancelable(false);
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        d.setContentView(msmartds.in.R.layout.push_balance_dialog);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnSubmit = (Button) d.findViewById(msmartds.in.R.id.btn_push_submit);
        btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_push_button);
        editAgentID = (EditText) d.findViewById(msmartds.in.R.id.edit_push_balance);

        btnSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(editAgentID.getText().toString().trim())) {
                Toast.makeText(DashBoardActivity.this, "Please enter agent id", Toast.LENGTH_SHORT).show();

            } else {
                final String AgentID = editAgentID.getText().toString().trim();
                d.cancel();

                pd = ProgressDialog.show(DashBoardActivity.this, "", "Loading. Please wait...", true);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();
                JsonObjectRequest jsonrequest = null;
                try {
                    JSONObject reqObj=new JSONObject()
                            .put("distributorId", distributorID)
                            .put("txnkey", key)
                            .put("param", "singleAgentDetail")
                            .put("agentId", AgentID);
                    L.m2("Url-1",url_single_push);
                    L.m2("Request-1",reqObj.toString());
                    jsonrequest = new JsonObjectRequest(Request.Method.POST, url_single_push,
                            reqObj,
                            object -> {
                                pd.dismiss();
                                L.m2("Url-1",url_single_push);
                                L.m2("Response-1",object.toString());
                                try {
                                    if (object.getInt("status")==0) {
                                        JSONObject jsonData = object.getJSONObject("data");
                                        Intent intent = new Intent(DashBoardActivity.this, PushMoneyActivity.class);
                                        intent.putExtra("AgentID", AgentID);
                                        intent.putExtra("FirmName", jsonData.getString("agencyName"));
                                        intent.putExtra("Balance", jsonData.getString("amount"));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(DashBoardActivity.this, object.getString("message").toString(), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }, error -> {
                        pd.dismiss();
                        Toast.makeText(DashBoardActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                    });
                } catch (JSONException e) {
                    pd.dismiss();
                    e.printStackTrace();
                }
                getSocketTimeOut(jsonrequest);
                Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);


            }
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            d.cancel();
        });

        d.show();
    }


    private void agentStatusRequest() throws JSONException {
        pd = ProgressDialog.show(DashBoardActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        JSONObject reqObj = new JSONObject()
                .put("distributorId", distributorID)
                .put("txnkey", key)
                .put("param", "AgentDetails")
                .put("Status", agentStatus);
        L.m2("Url-->", url);
        L.m2("data Request-->", reqObj.toString());
        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, url,
                reqObj,
                object -> {
                    pd.dismiss();

                    try {
                        L.m2("Url-->", url);
                        L.m2("data Response-->", object.toString());
                        if (object.getInt("status") == 0) {
                            editor.putString("spinner_data", AgentWiseTypeData);
                            editor.commit();
                            Intent intent = new Intent(DashBoardActivity.this, AgentsStatusWiseListActivity.class);
                            intent.putExtra("agentList", object.toString());
                            startActivity(intent);
                        } else {
                            Toast.makeText(DashBoardActivity.this, object.getString("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        L.m2("error", Objects.requireNonNull(e.getMessage()));
                    }
                }, error -> {
                    pd.dismiss();
                    Toast.makeText(DashBoardActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                });
        BaseActivity.getSocketTimeOutStatic(jsonrequest);
        Mysingleton.getInstance(DashBoardActivity.this).addToRequsetque(jsonrequest);
    }

    private void updateDistributerBal() {

        try {
            JSONObject reqObj = new JSONObject();
            reqObj.put("distributorId", distributorID);
            reqObj.put("txnkey", key);
            L.m2("url-", url_dsBalance);
            L.m2("url-req", reqObj.toString());
            JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, url_dsBalance,
                    reqObj,
                    object -> {
                        try {
                            L.m2("url res", object.toString());
                            if (object.getInt("status") == 0) {
                                JSONObject jsonData = object.getJSONObject("data");
                                editor.putString("balance", jsonData.getString("balance"));
                                editor.commit();
                                tViewDistributorBal.setText("Rs. " + jsonData.getString("balance"));
                            } else {
                                Toast.makeText(DashBoardActivity.this, object.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            L.m2("error", Objects.requireNonNull(e.getMessage()));
                        }
                    }, error -> {
                Toast.makeText(DashBoardActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            });
            BaseActivity.getSocketTimeOutStatic(jsonrequest);
            Mysingleton.getInstance(DashBoardActivity.this).addToRequsetque(jsonrequest);
        } catch (JSONException e) {
            L.m2("error", Objects.requireNonNull(e.getMessage()));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDistributerBal();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(msmartds.in.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(msmartds.in.R.drawable.warning_message_red)
                    .setTitle("Closing application")
                    .setMessage("Are you sure you want to exit ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }


    }

    //--------------------------------------------In-APP-Update-------------------------------------------------------------
    private void initializeAppUpdateManager() {
        // Initialize the Update Manager with the Activity and the Update Mode
        mUpdateManager = MyAppUpdateManager.Builder(this);

        // Callback from UpdateInfoListener
        // You can get the available version code of the apk in Google Play
        // Number of days passed since the user was notified of an update through the Google Play
        mUpdateManager.addUpdateInfoListener(new MyAppUpdateManager.UpdateInfoListener() {
            @Override
            public void onReceiveVersionCode(final int code) {
                L.m2("onReceiveVersionCode", String.valueOf(code));
                //txtAvailableVersion.setText(String.valueOf(code));
            }

            @Override
            public void onReceiveStalenessDays(final int days) {
                L.m2("onReceiveStalenessDays", String.valueOf(days));
                //txtStalenessDays.setText(String.valueOf(days));
            }
        });

        // Callback from Flexible Update Progress
        // This is only available for Flexible mode
        // Find more from https://developer.android.com/guide/playcore/in-app-updates#monitor_flexible
        mUpdateManager.addFlexibleUpdateDownloadListener((bytesDownloaded, totalBytes) -> {
            L.m2("addFlexibleUpdateDownload", "Downloading: " + bytesDownloaded + " / " + totalBytes);
        });
        callImmediateUpdate(tViewDistributorBal);
        //callFlexibleUpdate(tViewDistributorBal);
    }

    public void callFlexibleUpdate(View view) {
        // Start a Flexible Update
        mUpdateManager.mode(MyAppUpdateManager.FLEXIBLE).start();
        //txtFlexibleUpdateProgress.setVisibility(View.VISIBLE);
    }

    public void callImmediateUpdate(View view) {
        // Start a Immediate Update
        mUpdateManager.mode(MyAppUpdateManager.IMMEDIATE).start();
    }
    //--------------------------------------------GPS Tracker--------------------------------------------------------------

    @Override
    public void onLocationFound(Location location) {
        gpsTrackerPresenter.stopLocationUpdates();
        if (isTxnClick) {
            isTxnClick = false;
        }
    }

    @Override
    public void locationError(String msg) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPSTrackerPresenter.GPS_IS_ON__OR_OFF_CODE && resultCode == Activity.RESULT_OK) {
            gpsTrackerPresenter.checkGpsOnOrNot(GPSTrackerPresenter.GPS_IS_ON__OR_OFF_CODE);
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