package msmartds.in.ui.agent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainRequest;
import  msmartds.in.network.model.MainResponse2;
import  msmartds.in.network.model.agent.AgentActiveDeactiveRequest;
import  msmartds.in.network.model.agent.AgentRequest;
import  msmartds.in.network.model.agent.AgentResponse;
import  msmartds.in.network.model.agent.AgentSingle;
import  msmartds.in.network.model.agent.UpdateAgentRequest;
import  msmartds.in.network.model.stateDistrict.DistrictModel;
import  msmartds.in.network.model.stateDistrict.DistrictRequest;
import  msmartds.in.network.model.stateDistrict.DistrictResponse;
import  msmartds.in.network.model.stateDistrict.StateModel;
import  msmartds.in.network.model.stateDistrict.StateResponse;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Smartkinda on 6/19/2017.
 */

public class DetailAgentActivity extends BaseActivity {

    private Button btnUpdate;
    private TextView tviewDetailAgentId, tviewDetailAgentMobile, tviewDetailEmail, tviewDetailAgentGender;
    private EditText edit_dob,editDetailAgencyName, editDetailAgentName, editDetailAgentAddress, editDetailPinCode, editDetailCityName, editDetailPAN, editPAN;
    private Spinner spinnerCompanyType, spinnerState, spinnerDistrict;
    private String[] CompanyFirm = {"Select Type", "Non-Registered Entity", "Proprietorship", "Partnership", "Private Limited", "Limited", "Society", "NGO", "Trust"};
    private String dataAgentID, AgentStatus, dataBalance, PAN, companyTypeData, stateData, districtData;
    private ArrayList<StateModel> stateList;
    private ArrayList<DistrictModel> districtList;
    private ArrayAdapter<StateModel> StateAdaptor;
    private ArrayAdapter<DistrictModel> DistrictAdaptor;

    private DatePickerDialog chddDatePickerDialog;
    private ArrayAdapter CompanyTypeAdaptor;
    private String stringState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_agent_dialog);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Agent Details");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Util.setScrollView(findViewById(R.id.scrollView));


        dataAgentID = getIntent().getStringExtra("AgentID");
        AgentStatus = getIntent().getStringExtra("status");

        btnUpdate = (Button) findViewById(R.id.btn_update);

        tviewDetailAgentId = (TextView) findViewById(R.id.tv_detail_agent_id);
        tviewDetailAgentMobile = (TextView) findViewById(R.id.tv_mobile);
        tviewDetailAgentGender = (TextView) findViewById(R.id.tv_gender);
        tviewDetailEmail = (TextView) findViewById(R.id.tv_emailid);
        edit_dob =  findViewById(R.id.edit_dob);

        editDetailAgencyName = (EditText) findViewById(R.id.tv_detail_agency_name);
        editDetailAgentName = (EditText) findViewById(R.id.tv_detail_agent_name);
        editDetailAgentAddress = (EditText) findViewById(R.id.tv_address);
        editDetailCityName = (EditText) findViewById(R.id.edit_city_name);
        editDetailPinCode = (EditText) findViewById(R.id.edit_pin_code);
        editDetailPAN = (EditText) findViewById(R.id.edit_pan_card);

        spinnerCompanyType = (Spinner) findViewById(R.id.spinner_company_type);
        spinnerState = (Spinner) findViewById(R.id.spinner_state);
        spinnerDistrict = (Spinner) findViewById(R.id.spinner_district);


        //Set Date Of Birth
        edit_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chddDatePickerDialog.show();
            }
        });
        findViewsById();
        setchDDDateTimeField();

        CompanyTypeAdaptor = new ArrayAdapter(this, R.layout.spinner_textview_layout, CompanyFirm);
        CompanyTypeAdaptor.setDropDownViewResource(R.layout.spinner_textview_layout);
        spinnerCompanyType.setAdapter(CompanyTypeAdaptor);

        viewAgentDetails();

        stateRequest();

        spinnerCompanyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    companyTypeData = null;
                } else {
                    companyTypeData = parent.getItemAtPosition(position).toString();
                    //                   L.toast(parent.getContext(), "Selected: " + companyTypeData);
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
                    stateData = stateList.get(position).getState();
                    districtRequest(stateData);

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
                    districtData = districtList.get(position).getDistrict();

                    //                   L.toast(parent.getContext(), "Selected: " + districtData);
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
                    L.toast(DetailAgentActivity.this, "Please Enter Agent Name !!!");
                } else if (TextUtils.isEmpty(editDetailAgencyName.getText().toString().trim())) {
                    L.toast(DetailAgentActivity.this, "Please Enter Agency Name !!!");
                } else if (companyTypeData == null) {
                    L.toast(DetailAgentActivity.this, "Please Select Valid Option");
                } else if (edit_dob.length() <= 0) {
                    L.toast(getApplicationContext(), "Please Set Your Date Of Birth");
                } else if (TextUtils.isEmpty(editDetailAgentAddress.getText().toString().trim())) {
                    L.toast(getApplicationContext(), "Please Enter Address !!!");
                } else if (stateData == null) {
                    L.toast(getApplicationContext(), "Please Select Your State");
                } else if (districtData == null) {
                    L.toast(getApplicationContext(), "Please Select Your District");
                } else if (TextUtils.isEmpty(editDetailCityName.getText().toString().trim())) {
                    L.toast(getApplicationContext(), "Please Enter City Name");
                } else if (editDetailPinCode.length() <= 5) {
                    L.toast(getApplicationContext(), "Please Enter Valid Pin Code");
                } else if (TextUtils.isEmpty(editDetailPAN.getText().toString().trim())) {
                    L.toast(DetailAgentActivity.this, "Please Enter PAN Number !!!");
                } else {
                    final String PAN = editDetailPAN.getText().toString().trim();

                    if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
                        final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Updating Agent...");
                        ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
                        UpdateAgentRequest request = new UpdateAgentRequest();

                        request.setAgentFullId(dataAgentID);
                        request.setParam("updateAgent");
                        request.setDateofbirth(edit_dob.getText().toString());
                        request.setGender(tviewDetailAgentGender.getText().toString());
                        request.setCompanyType(spinnerCompanyType.getSelectedItem().toString());
                        request.setAgencyName(editDetailAgencyName.getText().toString());
                        request.setAddress(editDetailAgentAddress.getText().toString());
                        request.setState(stringState);
                        request.setDistrict(districtData);
                        request.setPanNo(PAN);
                        request.setCity(editDetailCityName.getText()+"");
                        request.setPincode(editDetailPinCode.getText().toString());
                        RetrofitClient.getClient(getApplicationContext())
                                .updateAgent(request)
                                .enqueue(new Callback<MainResponse2>() {
                                    @Override
                                    public void onResponse(Call<MainResponse2> call, retrofit2.Response<MainResponse2> response) {
                                        pd.dismiss();
                                        try {
                                            if (response.isSuccessful() && response.body() != null) {
                                                MainResponse2 res = response.body();
                                                if ("0".equalsIgnoreCase(res.getStatus())) {
                                                    showConfirmationDialog(res.getMessage());
                                                } else {
                                                    showConfirmationDialog(res.getMessage());
                                                }
                                            } else {
                                                L.toast(getApplicationContext(), "No Server Response");
                                            }
                                        } catch (Exception e) {
                                            L.toast(getApplicationContext(), "Parser Error : " + e.getLocalizedMessage());
                                            L.m2("Parser Error", e.getLocalizedMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MainResponse2> call, Throwable t) {
                                        pd.dismiss();
                                        L.m2("Parser Error", t.getLocalizedMessage());
                                    }
                                });
                    }
                }
            }
        });

    }

    //For DOB

    private void findViewsById() {
        edit_dob.setInputType(InputType.TYPE_NULL);
    }

    private void setchDDDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        chddDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                System.out.println("edit_dob--->" + edit_dob.toString());
                edit_dob.setText(Util.getDate(year, monthOfYear, dayOfMonth));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    private void viewAgentDetails() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "View Agent Details...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            AgentRequest request = new AgentRequest();
            request.setAgentId(dataAgentID);
            request.setParam("singleAgentDetail");

            RetrofitClient.getClient(getApplicationContext())
                    .getSingleAndListAgent(request)
                    .enqueue(new Callback<AgentResponse>() {
                        @Override
                        public void onResponse(Call<AgentResponse> call, retrofit2.Response<AgentResponse> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    AgentResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus()) && res.getData()!=null) {
                                        tviewDetailAgentId.setText(dataAgentID);
                                        tviewDetailAgentMobile.setText( res.getData().getMobileNo());
                                        tviewDetailEmail.setText( res.getData().getEmailId());
                                        tviewDetailAgentGender.setText( res.getData().getGender());
                                        editDetailAgentName.setText( res.getData().getAgentName());
                                        editDetailAgencyName.setText( res.getData().getAgencyName());
                                        edit_dob.setText( res.getData().getDob());
                                        editDetailAgentAddress.setText( res.getData().getAddress());
                                        editDetailCityName.setText( res.getData().getCity());
                                        editDetailPinCode.setText( res.getData().getPinCode());
                                        editDetailPAN.setText( res.getData().getPanNo());
                                        stringState =  res.getData().getState();
                                        for (int i = 0; i < CompanyTypeAdaptor.getCount(); i++) {
                                            Log.d("firmType--1>", CompanyTypeAdaptor.getItem(i).toString());

                                            if ( res.getData().getFirmType().equals(CompanyTypeAdaptor.getItem(i).toString())) {
                                                Log.d("firmType--2>", CompanyTypeAdaptor.getItem(i).toString());
                                                spinnerCompanyType.setSelection(i);
                                                break;
                                            }
                                        }
                                    } else {
                                        showConfirmationDialog(res.getMessage());
                                    }
                                } else {
                                    L.toast(getApplicationContext(), "No Server Response");
                                }
                            } catch (Exception e) {
                                L.toast(getApplicationContext(), "Parser Error : " + e.getLocalizedMessage());
                                L.m2("Parser Error", e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<AgentResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }

    //=====================================================

    public void showConfirmationDialog(String msg) {
        // TODO Auto-generated method stub
        final Dialog d = Util.getDialog(DetailAgentActivity.this, R.layout.push_payment_confirmation_dialog);

        final Button btnSubmit = (Button) d.findViewById(R.id.btn_push_submit);
        final TextView tvTitle = (TextView) d.findViewById(R.id.title);
        final Button btnClosed = (Button) d.findViewById(R.id.close_push_button);
        final TextView tvConfirmation = (TextView) d.findViewById(R.id.tv_confirmation_dialog);

        tvConfirmation.setText(msg);
        tvTitle.setText("Confirmation");

        btnSubmit.setOnClickListener(v -> {
            AgentResponse agentResponse = Util.getGson().fromJson(getIntent().getStringExtra("agentList"), AgentResponse.class);
            for (AgentSingle model : agentResponse.getData().getAgentDetails()) {
                if (dataAgentID.equalsIgnoreCase(model.getAgentId())) {
                    if (AgentStatus.equalsIgnoreCase("Activate")) {
                        model.setStatus("Deactive");
                    } else if (AgentStatus.equalsIgnoreCase("Deactive")) {
                        model.setStatus("Activate");
                    }
                }
            }

            Intent intent = new Intent(DetailAgentActivity.this, AgentsStatusWiseListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("agentList", Util.getStringFromModel(agentResponse));
            startActivity(intent);
            DetailAgentActivity.this.finish();
            d.dismiss();

            d.cancel();
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            d.cancel();
        });
        d.show();
    }

    //=====================================================

    public void showChangeStatusDialog(String msg) {
        // TODO Auto-generated method stub
        final Dialog d = Util.getDialog(DetailAgentActivity.this, R.layout.active_deactive_confirmation_dialog);

        final Button btnSubmit = (Button) d.findViewById(R.id.btn_push_submit1);
        final Button btnClosed = (Button) d.findViewById(R.id.close_push_button1);
        final TextView tvConfirmation = (TextView) d.findViewById(R.id.tv_confirmation_dialog1);

        tvConfirmation.setText(msg);
        if (AgentStatus.equalsIgnoreCase("Activate")) {
            btnSubmit.setText("Deactive");
        } else if (AgentStatus.equalsIgnoreCase("Deactive")) {
            btnSubmit.setText("Active");
        }

        btnSubmit.setOnClickListener(v -> {
            changeAgentRequest();
            d.cancel();
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            d.cancel();
        });
        d.show();
    }

    private void changeAgentRequest() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Change Agent Status...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            String shangeStatus = "";
            if (AgentStatus.equalsIgnoreCase("Activate")) {
                shangeStatus = "Deactive";
            } else if (AgentStatus.equalsIgnoreCase("Deactive")) {
                shangeStatus = "Activate";
            }
            AgentActiveDeactiveRequest request = new AgentActiveDeactiveRequest();
            request.setAgentFullId(tviewDetailAgentId.getText().toString());
            request.setCheckChangeStatus(shangeStatus);
            request.setParam("changeStatusAgent");

            RetrofitClient.getClient(getApplicationContext())
                    .activeDeactiveAgent(request)
                    .enqueue(new Callback<MainResponse2>() {
                        @Override
                        public void onResponse(Call<MainResponse2> call, retrofit2.Response<MainResponse2> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    MainResponse2 res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        showConfirmationDialog(res.getMessage());
                                    } else {
                                        showConfirmationDialog(res.getMessage());
                                    }
                                } else {
                                    L.toast(getApplicationContext(), "No Server Response");
                                }
                            } catch (Exception e) {
                                L.toast(getApplicationContext(), "Parser Error : " + e.getLocalizedMessage());
                                L.m2("Parser Error", e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<MainResponse2> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }

    //Json for State
    private void stateRequest() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching State...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            MainRequest request = new MainRequest();
            RetrofitClient.getClient(getApplicationContext())
                    .getStates(request)
                    .enqueue(new Callback<StateResponse>() {
                        @Override
                        public void onResponse(Call<StateResponse> call, retrofit2.Response<StateResponse> response) {
                            pd.dismiss();
                            if (stateList == null)
                                stateList = new ArrayList<>();
                            else
                                stateList.clear();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    StateResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        if (res.getStateList() != null) {
                                            stateList = (ArrayList<StateModel>) res.getStateList();
                                        }
                                    } else {
                                        L.toast(getApplicationContext(), res.getMessage());
                                    }
                                } else {
                                    L.toast(getApplicationContext(), "No Server Response");
                                }
                            } catch (Exception e) {
                                L.toast(getApplicationContext(), "Parser Error : " + e.getLocalizedMessage());
                                L.m2("Parser Error", e.getLocalizedMessage());
                            }
                            StateAdaptor = new ArrayAdapter(getApplicationContext(), R.layout.spinner_textview_layout, stateList);
                            StateAdaptor.setDropDownViewResource(R.layout.spinner_textview_layout);
                            spinnerState.setAdapter(StateAdaptor);
                            //if (stateList.size()>0)
                            //spinnerState.setSelection(StateAdaptor.getPosition(stringState));
                        }

                        @Override
                        public void onFailure(Call<StateResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }

    //JSON for District
    private void districtRequest(String state) {

        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Districts...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            DistrictRequest request = new DistrictRequest();
            request.setState(state);

            RetrofitClient.getClient(getApplicationContext())
                    .getDistrictsByState(request)
                    .enqueue(new Callback<DistrictResponse>() {
                        @Override
                        public void onResponse(Call<DistrictResponse> call, retrofit2.Response<DistrictResponse> response) {
                            pd.dismiss();
                            if (districtList == null)
                                districtList = new ArrayList<>();
                            else
                                districtList.clear();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    DistrictResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        if (res.getDistrictList() != null) {
                                            districtList = (ArrayList<DistrictModel>) res.getDistrictList();
                                        }
                                    } else {
                                        L.toast(getApplicationContext(), res.getMessage());
                                    }
                                } else {
                                    L.toast(getApplicationContext(), "No Server Response");
                                }
                            } catch (Exception e) {
                                L.toast(getApplicationContext(), "Parser Error : " + e.getLocalizedMessage());
                                L.m2("Parser Error", e.getLocalizedMessage());
                            }
                            DistrictAdaptor = new ArrayAdapter(getApplicationContext(), R.layout.spinner_textview_layout, districtList);
                            DistrictAdaptor.setDropDownViewResource(R.layout.spinner_textview_layout);
                            spinnerDistrict.setAdapter(DistrictAdaptor);
                            if (districtList.size() > 0 && districtData != null) {
                                //  spinnerDistrict.setSelection(DistrictAdaptor.getPosition(districtData));
                            }
                        }

                        @Override
                        public void onFailure(Call<DistrictResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toorbar_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.agent_status) {
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
            menu.findItem(R.id.agent_status).setTitle("Deactivate");
        } else {
            menu.findItem(R.id.agent_status).setTitle("Activate");
        }
        return super.onPrepareOptionsMenu(menu);
    }


}
