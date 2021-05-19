package msmartds.in;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.utility.L;
import msmartds.in.utility.Mysingleton;

/**
 * Created by Smartkinda on 6/19/2017.
 */

public class DetailAgentActivity extends BaseActivity {

    private Menu menu;
    private SharedPreferences sharedPreferences;
    private String distributorID, key, mobileNo, agentListData;
    private Button btnUpdate, btnClosed;
    private TextView tviewDetailAgentId, tviewDetailAgentMobile, tviewDetailEmail, tviewDetailAgentGender, tviewDetailDOB;
    private EditText editDetailAgencyName, editDetailAgentName, editDetailAgentAddress, editDetailPinCode, editDetailCityName, editDetailPAN, editPAN;
    private Spinner spinnerCompanyType, spinnerState, spinnerDistrict;
    private String[] CompanyFirm = {"Select Type", "Non-Registered Entity", "Proprietorship", "Partnership", "Private Limited", "Limited", "Society", "NGO", "Trust"};
    private ArrayList<String> State;
    private ArrayList<String> District;
    private String dataAgentID, AgentStatus, dataBalance, PAN, companyTypeData, stateData, districtData;
    private String state_url = HttpURL.StateURL;
    private String district_url = HttpURL.DistrictByStateURL;
    private TextView agentStatusData;
    private String url = HttpURL.DetailAgentURL;
    private String update_url = HttpURL.UpdateAgentURL;
    private String change_Status_url = HttpURL.ActiveDeactiveURL;
    private ProgressDialog pd;
    private JSONObject jsonObject;
    private ArrayAdapter StateAdaptor;
    private ArrayAdapter DistrictAdaptor;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog chddDatePickerDialog;
    private ArrayAdapter CompanyTypeAdaptor;
    private String stringState, stringDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.detail_agent_dialog);

        Toolbar toolbar = (Toolbar) findViewById(msmartds.in.R.id.toolbar);
        toolbar.setTitle("Agent Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScrollView scrollview = (ScrollView) findViewById(msmartds.in.R.id.scrollView);
        scrollview.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollview.setFocusable(true);
        scrollview.setFocusableInTouchMode(true);
        scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                v.requestFocusFromTouch();
                return false;
            }
        });


        sharedPreferences = getApplication().getSharedPreferences("Details", Context.MODE_PRIVATE);
        distributorID = sharedPreferences.getString("distributorId", null);
        key = sharedPreferences.getString("txnKey", null);
        mobileNo = sharedPreferences.getString("MobileNo", null);

        dataAgentID = getIntent().getStringExtra("AgentID");
        AgentStatus = getIntent().getStringExtra("status");

        btnUpdate = (Button) findViewById(msmartds.in.R.id.txtsubmit);
        btnClosed = (Button) findViewById(msmartds.in.R.id.close_button);

        tviewDetailAgentId = (TextView) findViewById(msmartds.in.R.id.tv_detail_agent_id);
        tviewDetailAgentMobile = (TextView) findViewById(msmartds.in.R.id.tv_mobile);
        tviewDetailAgentGender = (TextView) findViewById(msmartds.in.R.id.tv_gender);
        tviewDetailEmail = (TextView) findViewById(msmartds.in.R.id.tv_emailid);
        tviewDetailDOB = (TextView) findViewById(msmartds.in.R.id.tv_dob);

        editDetailAgencyName = (EditText) findViewById(msmartds.in.R.id.tv_detail_agency_name);
        editDetailAgentName = (EditText) findViewById(msmartds.in.R.id.tv_detail_agent_name);
        editDetailAgentAddress = (EditText) findViewById(msmartds.in.R.id.tv_address);
        editDetailCityName = (EditText) findViewById(msmartds.in.R.id.edit_city_name);
        editDetailPinCode = (EditText) findViewById(msmartds.in.R.id.edit_pin_code);
        editDetailPAN = (EditText) findViewById(msmartds.in.R.id.edit_pan_card);

        spinnerCompanyType = (Spinner) findViewById(msmartds.in.R.id.spinner_company_type);
        spinnerState = (Spinner) findViewById(msmartds.in.R.id.spinner_state);
        spinnerDistrict = (Spinner) findViewById(msmartds.in.R.id.spinner_district);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        //Set Date Of Birth
        tviewDetailDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chddDatePickerDialog.show();
            }
        });
        findViewsById();
        setchDDDateTimeField();

        CompanyTypeAdaptor = new ArrayAdapter(this, msmartds.in.R.layout.spinner_textview_layout, CompanyFirm);
        CompanyTypeAdaptor.setDropDownViewResource(msmartds.in.R.layout.spinner_textview_layout);
        spinnerCompanyType.setAdapter(CompanyTypeAdaptor);

        viewAgentDetails();

        try {
            stateRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinnerCompanyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    companyTypeData = null;
                } else {
                    companyTypeData = parent.getItemAtPosition(position).toString();
                    //                   Toast.makeText(parent.getContext(), "Selected: " + companyTypeData, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    stateData = null;
                } else {
                    stateData = parent.getItemAtPosition(position).toString();
                    try {
                        districtRequest(stateData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //                   Toast.makeText(parent.getContext(), "Selected: " + stateData, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    districtData = null;
                } else {
                    districtData = parent.getItemAtPosition(position).toString();

                    //                   Toast.makeText(parent.getContext(), "Selected: " + districtData, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(editDetailAgentName.getText().toString().trim())) {
                    Toast.makeText(DetailAgentActivity.this, "Please Enter Agent Name !!!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editDetailAgencyName.getText().toString().trim())) {
                    Toast.makeText(DetailAgentActivity.this, "Please Enter Agency Name !!!", Toast.LENGTH_SHORT).show();
                } else if (companyTypeData == null) {
                    Toast.makeText(DetailAgentActivity.this, "Please Select Valid Option", Toast.LENGTH_LONG).show();
                } else if (tviewDetailDOB.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Please Set Your Date Of Birth", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editDetailAgentAddress.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Please Enter Address !!!", Toast.LENGTH_SHORT).show();
                } else if (stateData == null) {
                    Toast.makeText(getApplicationContext(), "Please Select Your State", Toast.LENGTH_SHORT).show();
                } else if (districtData == null) {
                    Toast.makeText(getApplicationContext(), "Please Select Your District", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editDetailCityName.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Please Enter City Name", Toast.LENGTH_SHORT).show();
                } else if (editDetailPinCode.length() <= 5) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Pin Code", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editDetailPAN.getText().toString().trim())) {
                    Toast.makeText(DetailAgentActivity.this, "Please Enter PAN Number !!!", Toast.LENGTH_SHORT).show();
                } else {
                    final String PAN = editDetailPAN.getText().toString().trim();
                    pd = ProgressDialog.show(DetailAgentActivity.this, "", "Loading. Please wait...", true);
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    try {
                        JSONObject reqObj = new JSONObject()
                                .put("distributorId", distributorID)
                                .put("txnkey", key)
                                .put("param", "updateAgent")
                                .put("agentFullId", dataAgentID)
                                .put("dateofbirth", tviewDetailDOB.getText().toString())
                                .put("Gender", tviewDetailAgentGender.getText().toString())
                                .put("CompanyType", spinnerCompanyType.getSelectedItem().toString())
                                .put("AgencyName", editDetailAgencyName.getText().toString())
                                .put("Address", editDetailAgentAddress.getText().toString())
                                .put("State", spinnerState.getSelectedItem().toString())
                                .put("District", spinnerDistrict.getSelectedItem().toString())
                                .put("City", editDetailCityName.getText().toString())
                                .put("Pincode", editDetailPinCode.getText().toString())
                                .put("PanNo", PAN);
                        L.m2("url-called", update_url);
                        L.m2("Request", reqObj.toString());
                        JsonObjectRequest jsonrequest = null;
                        jsonrequest = new JsonObjectRequest(Request.Method.POST, update_url,
                                reqObj,
                                object -> {
                                    pd.dismiss();
                                    jsonObject = new JSONObject();
                                    jsonObject = object;

                                    Log.d("data Request-->", distributorID + ":" + key + ":" + dataAgentID);
                                    System.out.println("Object----1>" + object.toString());
                                    try {
                                        if (object.getString("status").equalsIgnoreCase("0")) {
                                            Log.d("url-called", update_url);
                                            Log.d("url data", object.toString());

                                            showConfirmationDialog(object.getString("message").toString());
                                        } else {
                                            showConfirmationDialog(object.getString("message").toString());
                                        }
                                    } catch (JSONException e) {
                                        pd.dismiss();
                                        e.printStackTrace();
                                    }
                                }, error -> {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                        });
                        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
                    } catch (JSONException e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    //For DOB

    private void findViewsById() {
        tviewDetailDOB.setInputType(InputType.TYPE_NULL);
    }

    private void setchDDDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        chddDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                System.out.println("tviewDetailDOB--->" + tviewDetailDOB.toString());
                tviewDetailDOB.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    private void viewAgentDetails() {
        JsonObjectRequest jsonrequest = null;
        try {
            JSONObject reqObj = new JSONObject()
                    .put("distributorId", distributorID)
                    .put("txnkey", key)
                    .put("agentId", dataAgentID)
                    .put("param", "singleAgentDetail");
            L.m2("url-called", url);
            L.m2("Request", reqObj.toString());
            jsonrequest = new JsonObjectRequest(Request.Method.POST, url,
                    reqObj,
                    object -> {
                        jsonObject = new JSONObject();
                        jsonObject = object;
                        try {
                            L.m2("url-called", url);
                            L.m2("url data", object.toString());
                            if (object.getInt("status") == 0) {

                                tviewDetailAgentId.setText(dataAgentID);
                                tviewDetailAgentMobile.setText(object.getString("mobileNo"));
                                tviewDetailEmail.setText(object.getString("emailId"));
                                tviewDetailAgentGender.setText(object.getString("gender"));
                                editDetailAgentName.setText(object.getString("agentName"));
                                editDetailAgencyName.setText(object.getString("agencyName"));
                                tviewDetailDOB.setText(object.getString("dob"));
                                editDetailAgentAddress.setText(object.getString("address"));
                                editDetailCityName.setText(object.getString("city"));
                                editDetailPinCode.setText(object.getString("pinCode"));
                                editDetailPAN.setText(object.getString("panNo"));
                                stringState = object.getString("state");
                                stringDistrict = object.getString("district");

                                for (int i = 0; i < CompanyTypeAdaptor.getCount(); i++) {
                                    Log.d("firmType--1>", CompanyTypeAdaptor.getItem(i).toString());

                                    if (object.getString("firmType").equals(CompanyTypeAdaptor.getItem(i).toString())) {
                                        Log.d("firmType--2>", CompanyTypeAdaptor.getItem(i).toString());
                                        spinnerCompanyType.setSelection(i);
                                        break;
                                    }
                                }
                            } else {
                                showConfirmationDialog(object.getString("message").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
    }

    //=====================================================

    public void showConfirmationDialog(String msg) {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(DetailAgentActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        d.setCancelable(false);

        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        d.setContentView(msmartds.in.R.layout.push_payment_confirmation_dialog);

        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Button btnSubmit = (Button) d.findViewById(msmartds.in.R.id.btn_push_submit);
        final TextView tvTitle = (TextView) d.findViewById(msmartds.in.R.id.title);
        final Button btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_push_button);
        final TextView tvConfirmation = (TextView) d.findViewById(msmartds.in.R.id.tv_confirmation_dialog);

        tvConfirmation.setText(msg);
        tvTitle.setText("Confirmation");

        btnSubmit.setOnClickListener(v -> {
            try {
                if (jsonObject.getString("status").equalsIgnoreCase("0")) {
                    JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("agentList"));
                    JSONArray jsonArray = jsonObject.getJSONArray("agentDetails");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject js = jsonArray.getJSONObject(i);
                        if (dataAgentID.equalsIgnoreCase(js.getString("AgentId"))) {
                            if (AgentStatus.equalsIgnoreCase("Activate")) {
                                js.put("status", "Deactive");
                            } else if (AgentStatus.equalsIgnoreCase("Deactive")) {
                                js.put("status", "Activate");
                            }
                            // jsonArray.remove(i);
                            //jsonArray.put(i,js);
                        }
                    }

                    Intent intent = new Intent(DetailAgentActivity.this, AgentsStatusWiseListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("agentList", jsonObject.toString());
                    startActivity(intent);
                    DetailAgentActivity.this.finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(msmartds.in.R.menu.toorbar_action, menu);

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == msmartds.in.R.id.agent_status) {
            if (AgentStatus.equalsIgnoreCase("Activate")) {
                showChangeStatusDialog("Are You Sure, You want to Deactive this Agent !!!");
            } else if (AgentStatus.equalsIgnoreCase("Deactive")) {
                showChangeStatusDialog("Are You Sure, You want to Activate this Agent !!!");
            }
        }
        if (i == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (AgentStatus.equalsIgnoreCase("Activate")) {
            menu.findItem(msmartds.in.R.id.agent_status).setTitle("Deactivate");
        } else {
            menu.findItem(msmartds.in.R.id.agent_status).setTitle("Activate");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    //=====================================================

    public void showChangeStatusDialog(String msg) {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(DetailAgentActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        d.setCancelable(false);

        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        d.setContentView(msmartds.in.R.layout.active_deactive_confirmation_dialog);

        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Button btnSubmit = (Button) d.findViewById(msmartds.in.R.id.btn_push_submit1);
        final Button btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_push_button1);
        final TextView tvConfirmation = (TextView) d.findViewById(msmartds.in.R.id.tv_confirmation_dialog1);

        tvConfirmation.setText(msg);
        if (AgentStatus.equalsIgnoreCase("Activate")) {
            btnSubmit.setText("Deactive");
        } else if (AgentStatus.equalsIgnoreCase("Deactive")) {
            btnSubmit.setText("Active");
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeAgentRequest();
                } catch (JSONException e) {
                    System.out.println("change_agent_status_failed--->" + e);
                    e.printStackTrace();
                }
                d.cancel();
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

    private void changeAgentRequest() throws JSONException {
        pd = ProgressDialog.show(DetailAgentActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        String changeStatus = "";
        if (AgentStatus.equalsIgnoreCase("Activate")) {
            changeStatus = "Deactive";
        } else if (AgentStatus.equalsIgnoreCase("Deactive")) {
            changeStatus = "Activate";
        }

        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, change_Status_url,
                new JSONObject()
                        .put("distributorId", distributorID)
                        .put("txnkey", key)
                        .put("param", "changeStatusAgent")
                        .put("agentFullId", tviewDetailAgentId.getText().toString())
                        .put("checkChangeStatus", changeStatus),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        pd.dismiss();
                        Log.d("data Request-->", distributorID + ":" + key + ":" + tviewDetailAgentId + ":" + "changeStatusAgent");
                        System.out.println("Object----1>" + object.toString());
                        try {
                            if (object.getString("status").equalsIgnoreCase("0")) {
                                Log.d("url-called", change_Status_url);
                                Log.d("url data", object.toString());

                                showConfirmationDialog(object.getString("message").toString());

                            } else {
                                pd.dismiss();
                                showConfirmationDialog(object.getString("message").toString());
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
                Toast.makeText(DetailAgentActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
    }

    //Json for State
    private void stateRequest() throws JSONException {
        pd = ProgressDialog.show(DetailAgentActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        Log.d("Before stateRequest-->", distributorID + ":" + key);
        final JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, state_url,
                new JSONObject()
                        .put("distributorId", distributorID)
                        .put("txnkey", key),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        pd.dismiss();
                        Log.d("data Request--2>", distributorID + ":" + key);
                        System.out.println("Object----2>" + object.toString());
                        try {
                            if (object.getString("status").equalsIgnoreCase("0")) {
                                JSONArray stateJsonArray = object.getJSONArray("StateList");
                                State = new ArrayList<>();
                                for (int i = 0; i < stateJsonArray.length(); i++) {

                                    JSONObject obj = stateJsonArray.getJSONObject(i);
                                    State.add(obj.getString("state"));
                                }
                                StateAdaptor = new ArrayAdapter(DetailAgentActivity.this, msmartds.in.R.layout.spinner_textview_layout, State);
                                StateAdaptor.setDropDownViewResource(msmartds.in.R.layout.spinner_textview_layout);
                                spinnerState.setAdapter(StateAdaptor);
                                spinnerState.setSelection(StateAdaptor.getPosition(stringState));
                                Log.d("state_data--->", State.toString());
                            } else {
                                pd.dismiss();
                                showConfirmationDialog(object.getString("message").toString());
                            }
                        } catch (JSONException e) {
                            pd.dismiss();
                            e.printStackTrace();
                        }
                        // StateAdaptor.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(DetailAgentActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
    }

    //JSON for District
    private void districtRequest(String state) throws JSONException {
        pd = ProgressDialog.show(DetailAgentActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        Log.d("Before districtRequest", distributorID + ":" + key + ":" + state);
        final JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, district_url,
                new JSONObject()
                        .put("state", state)
                        .put("distributorId", distributorID)
                        .put("txnkey", key),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        pd.dismiss();
                        Log.d("data Request--2>", distributorID + ":" + key);
                        System.out.println("Object----2>" + object.toString());
                        try {
                            if (object.getString("status").equalsIgnoreCase("0")) {
                                JSONArray stateJsonArray = object.getJSONArray("districtList");
                                District = new ArrayList<>();
                                for (int i = 0; i < stateJsonArray.length(); i++) {

                                    JSONObject obj = stateJsonArray.getJSONObject(i);
                                    District.add(obj.getString("district"));
                                }
                                DistrictAdaptor = new ArrayAdapter(DetailAgentActivity.this, msmartds.in.R.layout.spinner_textview_layout, District);
                                DistrictAdaptor.setDropDownViewResource(msmartds.in.R.layout.spinner_textview_layout);
                                spinnerDistrict.setAdapter(DistrictAdaptor);
                                spinnerDistrict.setSelection(DistrictAdaptor.getPosition(stringDistrict));
                                Log.d("district_data--->", District.toString());
                            } else {
                                pd.dismiss();
                                showConfirmationDialog(object.getString("message").toString());
                            }
                        } catch (JSONException e) {
                            pd.dismiss();
                            e.printStackTrace();
                        }
                        //  StateAdaptor.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(DetailAgentActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
    }

}
