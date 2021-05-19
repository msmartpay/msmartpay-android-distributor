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
import android.util.Log;
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
 * Created by Smartkinda on 6/13/2017.
 */

public class AddAgentActivity extends BaseActivity {

    private DatePickerDialog chddDatePickerDialog;
    private Button btnAddAgent;
    private TextView DateOfBirth;
    private EditText editFirstName, editLastName, editCompanyShopName, editEmailID, editMobileNo, editAddress1, editAddress2, editCityName, editPinCode, editPAN;
    private String FirstName, LastName, CompanyShopName, EmailID, MobileNo, Address1, Address2, CityName, PinCode, PAN;
    private SimpleDateFormat dateFormatter;
    private Spinner spinnerGender, spinnerCompanyFirmType, spinnerCountry, spinnerState, spinnerDistrict;
    private String[] Gender={"Select Gender...","Male","Female"};
    private String[] CompanyFirm={"Select Firm...","Non-Resistered Entry","Proprietorship","Partnership","Private Limited","Limited","Society","NGO","Trust"};
    private String[] Country={"India"};
    private ArrayList<String> State;
    private ArrayList<String> District;
    private ScrollView scrollview;
    private String genderData, companyTypeData,countryData, stateData, districtData;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String url = HttpURL.RegisterAgentURL;
    private String state_url = HttpURL.StateURL;
    private String district_url = HttpURL.DistrictByStateURL;
    private ProgressDialog pd;
    private JSONObject jsonObject;
    private String distributorID, key, mobileNo, distributorInitial, DistributorFullID;
    private ArrayAdapter StateAdaptor;
    private ArrayAdapter DistrictAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.add_agent_activity);

        Toolbar toolbar = (Toolbar) findViewById(msmartds.in.R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.activity_de);
        toolbar.setTitle("ADD AGENT");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getApplicationContext().getSharedPreferences("Details", Context.MODE_PRIVATE);
        distributorID = sharedPreferences.getString("distributorId", null);
        key = sharedPreferences.getString("txnKey", null);
        mobileNo = sharedPreferences.getString("MobileNo", null);
        distributorInitial = sharedPreferences.getString("distributorInitial", null);
        DistributorFullID = distributorInitial+distributorID;

        scrollview = (ScrollView) findViewById(msmartds.in.R.id.scrollView);
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

        btnAddAgent = (Button) findViewById(msmartds.in.R.id.btn_add_agent);

        editFirstName = (EditText) findViewById(msmartds.in.R.id.edit_first_name);
        editLastName = (EditText) findViewById(msmartds.in.R.id.edit_last_name);
        editCompanyShopName = (EditText) findViewById(msmartds.in.R.id.edit_company_name);
        editEmailID = (EditText) findViewById(msmartds.in.R.id.edit_emailID);
        editMobileNo = (EditText) findViewById(msmartds.in.R.id.edit_mobileNo);
        editAddress1 = (EditText) findViewById(msmartds.in.R.id.edit_address1);
        editAddress2 = (EditText) findViewById(msmartds.in.R.id.edit_address2);
        editCityName = (EditText) findViewById(msmartds.in.R.id.edit_city);
        editPinCode = (EditText) findViewById(msmartds.in.R.id.edit_pincode);
        editPAN = (EditText) findViewById(msmartds.in.R.id.edit_PAN_details);

        DateOfBirth =(TextView) findViewById(msmartds.in.R.id.tview_dob);
        spinnerGender = (Spinner) findViewById(msmartds.in.R.id.spinner_gender);
        spinnerCompanyFirmType = (Spinner) findViewById(msmartds.in.R.id.spinner_company_type);
        spinnerCountry = (Spinner) findViewById(msmartds.in.R.id.spinner_country_list);
        spinnerState = (Spinner) findViewById(msmartds.in.R.id.spinner_state_list);
        spinnerDistrict = (Spinner) findViewById(msmartds.in.R.id.spinner_district_list);

        ArrayAdapter GenderAdaptor = new ArrayAdapter(this, msmartds.in.R.layout.spinner_textview_layout,Gender);
        GenderAdaptor.setDropDownViewResource(msmartds.in.R.layout.spinner_textview_layout);
        spinnerGender.setAdapter(GenderAdaptor);

        final ArrayAdapter CompanyFirmAdaptor = new ArrayAdapter(this, msmartds.in.R.layout.spinner_textview_layout,CompanyFirm);
        CompanyFirmAdaptor.setDropDownViewResource(msmartds.in.R.layout.spinner_textview_layout);
        spinnerCompanyFirmType.setAdapter(CompanyFirmAdaptor);

        ArrayAdapter CountryAdaptor = new ArrayAdapter(this, msmartds.in.R.layout.spinner_textview_layout,Country);
        CountryAdaptor.setDropDownViewResource(msmartds.in.R.layout.spinner_textview_layout);
        spinnerCountry.setAdapter(CountryAdaptor);

        try {
            stateRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        DateOfBirth.setOnClickListener(v -> chddDatePickerDialog.show());
        findViewsById();
        setchDDDateTimeField();

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    genderData = null;
                }else {
                genderData = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + genderData, Toast.LENGTH_LONG).show();
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerCompanyFirmType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    companyTypeData = null;
                }else{
                companyTypeData = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + companyTypeData, Toast.LENGTH_LONG).show();
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                countryData = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + countryData, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    stateData = null;
                }else{
                    stateData = parent.getItemAtPosition(position).toString();
                    try {
                        districtRequest(stateData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
 //                   Toast.makeText(parent.getContext(), "Selected: " + stateData, Toast.LENGTH_LONG).show();
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    districtData = null;
                }else{
                districtData = parent.getItemAtPosition(position).toString();

//                Toast.makeText(parent.getContext(), "Selected: " + districtData, Toast.LENGTH_LONG).show();
            }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Add agent coding start from here by IRFAN

        btnAddAgent.setOnClickListener(v -> {

            FirstName = editFirstName.getText().toString().trim();
            LastName = editLastName.getText().toString().trim();
            CompanyShopName = editCompanyShopName.getText().toString().trim();
            EmailID = editEmailID.getText().toString().trim();
            MobileNo = editMobileNo.getText().toString().trim();
            Address1 = editAddress1.getText().toString().trim();
            Address2 = editAddress2.getText().toString().trim();
            CityName = editCityName.getText().toString().trim();
            PinCode = editPinCode.getText().toString().trim();
            PAN = editPAN.getText().toString().trim();

            if(FirstName.length()<=0)
            {
                editFirstName.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter First Name", Toast.LENGTH_SHORT).show();
            }else if(LastName.length()<=0){
                editLastName.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter Last Name", Toast.LENGTH_SHORT).show();
            }else if(DateOfBirth.length()<=0){
                Toast.makeText(getApplicationContext(), "Please Set Your Date Of Birth", Toast.LENGTH_SHORT).show();
            }else if(genderData == null){
                Toast.makeText(AddAgentActivity.this, "Please Select Valid Option", Toast.LENGTH_LONG).show();
            } else if(companyTypeData == null){
                Toast.makeText(AddAgentActivity.this, "Please Select Valid Option", Toast.LENGTH_LONG).show();
            }else if(CompanyShopName.length()<=0) {
                editCompanyShopName.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter Company/Firm/Shop Name", Toast.LENGTH_SHORT).show();
            }else if(EmailID.length()<=0) {
                editEmailID.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter Valid Email ID", Toast.LENGTH_SHORT).show();
            }else if(MobileNo.length()<=9) {
                editMobileNo.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
            }else if(Address1.length()<=0) {
                editAddress1.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter Address Line 1", Toast.LENGTH_SHORT).show();
            }else if(Address2.length()<=0) {
                editAddress2.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter Address Line 2", Toast.LENGTH_SHORT).show();
            } else if(countryData == null){
                Toast.makeText(getApplicationContext(), "Please Select Your Country", Toast.LENGTH_SHORT).show();
            } else if(stateData == null){
                Toast.makeText(getApplicationContext(), "Please Select Your State", Toast.LENGTH_SHORT).show();
            } else if(districtData == null){
                Toast.makeText(getApplicationContext(), "Please Select Your District", Toast.LENGTH_SHORT).show();
            }else if(CityName.length()<=0) {
                editCityName.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter City Name", Toast.LENGTH_SHORT).show();
            }else if(PinCode.length()<=5) {
                editPinCode.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter Valid Pin Code", Toast.LENGTH_SHORT).show();
            }/*else if(PAN.length()<=0) {
                editPAN.requestFocus();
                Toast.makeText(getApplicationContext(), "Please Enter PAN Number (Optional)", Toast.LENGTH_SHORT).show();
            }*/else if (!Service.isValidEmail(EmailID)) {
                Toast.makeText(getApplicationContext(), "Please Enter Valid Email Id", Toast.LENGTH_SHORT).show();
            } else{

                agentResistrationDetails();
                }
        });
    }

    private void findViewsById() {
        DateOfBirth.setInputType(InputType.TYPE_NULL);
    }

    private void setchDDDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        chddDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                System.out.println("DateOfBirth--->"+DateOfBirth.toString());
                DateOfBirth.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void agentResistrationDetails() {

        pd = ProgressDialog.show(AddAgentActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        try {

        JSONObject jsonObjectReq=new JSONObject()
                .put("distributorId", distributorID)
                .put("txnkey", key)
                .put("DSFullId", DistributorFullID)
                .put("Firstname", FirstName)
                .put("Lastname", LastName)
                .put("dateofbirth", DateOfBirth.getText().toString())
                .put("Gender", genderData)
                .put("CompanyType", companyTypeData)
                .put("AgencyName", CompanyShopName)
                .put("PanNo", PAN)
                .put("Address", Address1)
                .put("State", stateData)
                .put("District", districtData)
                .put("City", CityName)
                .put("Country", countryData)
                .put("Pincode", PinCode)
                .put("EmailId", EmailID)
                .put("param", "AgentRegistration")
                .put("AthoMobile", MobileNo);

            L.m2("Url--1>", url);
            L.m2("Request--1>", jsonObjectReq.toString());


            JsonObjectRequest  jsonrequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectReq,
                    object -> {
                        pd.dismiss();
                        jsonObject = new JSONObject();
                        jsonObject=object;
                        try {
                            L.m2("Url--1>", url);
                            L.m2("Response--1>", object.toString());
                            if (object.getInt("status")==0) {
                                pd.dismiss();
                                showConfirmationDialog(object.getString("message").toString());
                            }
                            else {
                                pd.dismiss();
                                showConfirmationDialog(object.getString("message").toString());
                            }
                        } catch (JSONException e) {
                            pd.dismiss();
                            e.printStackTrace();
                        }
                    }, error -> {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Server Error : "+error.toString(), Toast.LENGTH_SHORT).show();
                    });
            getSocketTimeOut(jsonrequest);
            Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
        } catch (JSONException e) {
            pd.dismiss();
            e.printStackTrace();
        }

    }

    public void showConfirmationDialog(String msg) {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(AddAgentActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

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
                if(jsonObject.getInt("status")==0){
                    Intent intent = new Intent(AddAgentActivity.this, DashBoardActivity.class);
                    startActivity(intent);
                    AddAgentActivity.this.finish();
                    d.dismiss();
                }else{
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

    //Json for State
    private void stateRequest() throws JSONException {
        pd = ProgressDialog.show(AddAgentActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        JSONObject jsonReq = new JSONObject()
                .put("distributorId", distributorID)
                .put("txnkey", key);
        L.m2("Url--1>", state_url);
        L.m2("Request--1>", jsonReq.toString());

        final JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, state_url,
                jsonReq,
                object -> {
                    pd.dismiss();
                    try {
                        L.m2("Url--1>", state_url);
                        L.m2("Response--1>", object.toString());

                        if (object.getInt("status")==0) {
                        JSONArray stateJsonArray = object.getJSONArray("data");
                        State = new ArrayList<>();
                        for(int i=0; i<stateJsonArray.length(); i++){
                           String state=stateJsonArray.getString(i);
                            State.add(state);
                        }
                            StateAdaptor = new ArrayAdapter(AddAgentActivity.this, R.layout.spinner_textview_layout,State);
                            StateAdaptor.setDropDownViewResource(R.layout.spinner_textview_layout);
                            spinnerState.setAdapter(StateAdaptor);
                        Log.d("state_data--->",State.toString());
                        }
                        else {
                            pd.dismiss();
                            showConfirmationDialog(object.getString("message").toString());
                        }
                    } catch (JSONException e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }
                    StateAdaptor.notifyDataSetChanged();
                },new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse (VolleyError error){
                pd.dismiss();
                Toast.makeText(AddAgentActivity.this, "Server Error : "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
    }

    //JSON for District
    private void districtRequest(String state) throws JSONException {
        pd = ProgressDialog.show(AddAgentActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        JSONObject jsonReq = new JSONObject()
                .put("state", state)
                .put("distributorId", distributorID)
                .put("txnkey", key);
        L.m2("Url--1>", district_url);
        L.m2("Request--1>", jsonReq.toString());

        final JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, district_url,
                new JSONObject()
                        .put("state", state)
                        .put("distributorId", distributorID)
                        .put("txnkey", key),
                object -> {
                    pd.dismiss();
                     try {
                         L.m2("Url--1>", district_url);
                         L.m2("Response--1>", object.toString());
                         if (object.getInt("status")==0) {
                            JSONArray stateJsonArray = object.getJSONArray("data");
                            District = new ArrayList<>();
                            for(int i=0; i<stateJsonArray.length(); i++){
                                String district=stateJsonArray.getString(i);
                                District.add(district);
                            }
                            DistrictAdaptor = new ArrayAdapter(AddAgentActivity.this, R.layout.spinner_textview_layout,District);
                            DistrictAdaptor.setDropDownViewResource(R.layout.spinner_textview_layout);
                            spinnerDistrict.setAdapter(DistrictAdaptor);
                            Log.d("district_data---2>",District.toString());
                        }
                        else {
                            pd.dismiss();
                            showConfirmationDialog(object.getString("message").toString());
                        }
                    } catch (JSONException e) {
                        pd.dismiss();
                        e.printStackTrace();
                    }
                    StateAdaptor.notifyDataSetChanged();
                }, error -> {
                    pd.dismiss();
                    Toast.makeText(AddAgentActivity.this, "Server Error : "+error.toString(), Toast.LENGTH_SHORT).show();
                });
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}