package msmartds.in.ui.agent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainRequest;
import  msmartds.in.network.model.MainResponse2;
import  msmartds.in.network.model.agent.AgentRegisterRequest;
import  msmartds.in.network.model.stateDistrict.DistrictModel;
import  msmartds.in.network.model.stateDistrict.DistrictRequest;
import  msmartds.in.network.model.stateDistrict.DistrictResponse;
import  msmartds.in.network.model.stateDistrict.StateModel;
import  msmartds.in.network.model.stateDistrict.StateResponse;
import  msmartds.in.ui.home.DashBoardActivity;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.Keys;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Smartkinda on 6/13/2017.
 */

public class AddAgentActivity extends BaseActivity {

    private DatePickerDialog chddDatePickerDialog;
    private Button btn_add_agent;
    private EditText edit_first_name,edit_middle_name, edit_last_name, edit_dob, edit_company_name, edit_emailID, edit_mobileNo, edit_address1, edit_address2, edit_city, edit_pincode,
            office_address1, office_address2, office_city, office_pincode,edit_PAN_details;
    private String FirstName,middleName, LastName, CompanyShopName, EmailID, MobileNo, Address1, Address2, CityName, PinCode,officeAddress1, officeAddress2, officeCityName, officePinCode, PAN;

    private SmartMaterialSpinner sp_gender, sp_company_type, sp_country, sp_state, sp_district,sp_office_country,sp_office_state, sp_office_district;
    private List<String> genderList, companeyFirmList, countryList,officeCountryList;
    private String genderData, companyTypeData, countryData, stateData, districtData,officeCountryData, officeStateData, officeDistrictData;
    private String distributorID, key, mobileNo, distributorInitial, DistributorFullID;
    private ArrayList<StateModel> stateList,officeStateList;
    private ArrayList<DistrictModel> districtList,officeDistrictList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_agent_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.activity_de);
        toolbar.setTitle("ADD AGENT");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        distributorID = Util.getData(getApplicationContext(), Keys.DS_ID);
        key = Util.getData(getApplicationContext(), Keys.TXN_KEY);
        mobileNo = Util.getData(getApplicationContext(), Keys.DS_MOBILE);

        distributorInitial = Util.getData(getApplicationContext(), Keys.DS_INITIAL);
        DistributorFullID = distributorInitial + distributorID;

        initViews();

        genderList = Arrays.asList(getResources().getStringArray(R.array.gender_type));
        sp_gender.setItem(genderList);

        companeyFirmList = Arrays.asList(getResources().getStringArray(R.array.company_firm_type));
        sp_company_type.setItem(companeyFirmList);

        countryList = Arrays.asList(getResources().getStringArray(R.array.country));
        sp_country.setItem(countryList);

        officeCountryList=Arrays.asList(getResources().getStringArray(R.array.country));
        sp_office_country.setItem(officeCountryList);

        stateRequest();


        edit_dob.setOnClickListener(v -> chddDatePickerDialog.show());
        setchDDDateTimeField();

        sp_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    genderData = genderList.get(position);
                } else {
                    genderData = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_company_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    companyTypeData = companeyFirmList.get(position);
                } else {
                    companyTypeData = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    countryData = countryList.get(position);
                } else {
                    countryData = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_office_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    officeCountryData = officeCountryList.get(position);
                } else {
                    officeCountryList = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    stateData = stateList.get(position).getState();
                    districtRequest(stateData,"personal");
                } else {
                    stateData = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_office_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    officeStateData = officeStateList.get(position).getState();
                    districtRequest(officeStateData,"office");
                } else {
                    officeStateData = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    districtData = districtList.get(position).getDistrict();
                } else {
                    districtData = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_office_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > -1) {
                    officeDistrictData = officeDistrictList.get(position).getDistrict();
                } else {
                    officeDistrictData = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Add agent coding start from here by IRFAN

        btn_add_agent.setOnClickListener(v -> {

            FirstName = edit_first_name.getText().toString().trim();
            middleName = edit_middle_name.getText().toString().trim();
            LastName = edit_last_name.getText().toString().trim();
            CompanyShopName = edit_company_name.getText().toString().trim();
            EmailID = edit_emailID.getText().toString().trim();
            MobileNo = edit_mobileNo.getText().toString().trim();
            Address1 = edit_address1.getText().toString().trim();
            Address2 = edit_address2.getText().toString().trim();
            CityName = edit_city.getText().toString().trim();
            PinCode = edit_pincode.getText().toString().trim();

            officeAddress1 = office_address1.getText().toString().trim();
            officeAddress2 = office_address2.getText().toString().trim();
            officeCityName = office_city.getText().toString().trim();
            officePinCode = office_pincode.getText().toString().trim();


            PAN = edit_PAN_details.getText().toString().trim();

            if (FirstName.length() <= 0) {
                edit_first_name.requestFocus();
                L.toast(getApplicationContext(), "Please Enter First Name");
            } else if (LastName.length() <= 0) {
                edit_last_name.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Last Name");
            } else if (edit_dob.length() <= 0) {
                L.toast(getApplicationContext(), "Please Set Your Date Of Birth");
            } else if (genderData == null) {
                L.toast(AddAgentActivity.this, "Please Select Valid Option");
            } else if (companyTypeData == null) {
                L.toast(AddAgentActivity.this, "Please Select Valid Option");
            } else if (CompanyShopName.length() <= 0) {
                edit_company_name.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Company/Firm/Shop Name");
            } else if (EmailID.length() <= 0) {
                edit_emailID.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Valid Email ID");
            } else if (MobileNo.length() <= 9) {
                edit_mobileNo.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Valid Mobile Number");
            } else if (Address1.length() <= 0) {
                edit_address1.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Address Line 1");
            } else if (Address2.length() <= 0) {
                edit_address2.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Address Line 2");
            } else if (countryData == null) {
                L.toast(getApplicationContext(), "Please Select Your Country");
            } else if (stateData == null) {
                L.toast(getApplicationContext(), "Please Select Your State");
            } else if (districtData == null) {
                L.toast(getApplicationContext(), "Please Select Your District");
            } else if (CityName.length() <= 0) {
                edit_city.requestFocus();
                L.toast(getApplicationContext(), "Please Enter City Name");
            } else if (PinCode.length() < 6) {
                edit_pincode.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Valid Pin Code");
            }else if (officeAddress1.length() <= 0) {
                edit_address1.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Address Line 1");
            } else if (officeAddress2.length() <= 0) {
                edit_address2.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Address Line 2");
            } else if (officeCountryData == null) {
                L.toast(getApplicationContext(), "Please Select Your Country");
            } else if (officeStateData == null) {
                L.toast(getApplicationContext(), "Please Select Your State");
            } else if (officeDistrictData == null) {
                L.toast(getApplicationContext(), "Please Select Your District");
            } else if (officeCityName.length() <= 0) {
                edit_city.requestFocus();
                L.toast(getApplicationContext(), "Please Enter City Name");
            } else if (officePinCode.length() < 6) {
                edit_pincode.requestFocus();
                L.toast(getApplicationContext(), "Please Enter Valid Pin Code");
            }else if(PAN.length()<10) {
                edit_PAN_details.requestFocus();
                L.toast(getApplicationContext(), "Please Enter PAN Number");
            }else if (!Util.isValidEmail(EmailID)) {
                L.toast(getApplicationContext(), "Please Enter Valid Email Id");
            } else {

                agentResistrationDetails();
            }
        });
    }

    private void initViews() {
        Util.setScrollView(findViewById(R.id.scrollView));
        btn_add_agent = findViewById(R.id.btn_add_agent);

        edit_first_name = findViewById(R.id.edit_first_name);
        edit_middle_name = findViewById(R.id.edit_middle_name);
        edit_last_name = findViewById(R.id.edit_last_name);
        edit_company_name = findViewById(R.id.edit_company_name);
        edit_emailID = findViewById(R.id.edit_emailID);
        edit_mobileNo = findViewById(R.id.edit_mobileNo);
        edit_address1 = findViewById(R.id.edit_address1);
        edit_address2 = findViewById(R.id.edit_address2);
        edit_city = findViewById(R.id.edit_city);
        edit_pincode = findViewById(R.id.edit_pincode);
        sp_country = findViewById(R.id.sp_country);
        sp_state = findViewById(R.id.sp_state);
        sp_district = findViewById(R.id.sp_district);

        office_address1 = findViewById(R.id.office_address1);
        office_address2 = findViewById(R.id.office_address2);
        office_city = findViewById(R.id.edit_city);
        office_pincode = findViewById(R.id.office_pincode);
        sp_office_country = findViewById(R.id.sp_office_country);
        sp_office_state = findViewById(R.id.sp_office_state);
        sp_office_district = findViewById(R.id.sp_office_district);

        edit_PAN_details = findViewById(R.id.edit_PAN_details);
        edit_dob = findViewById(R.id.edit_dob);
        sp_gender = findViewById(R.id.sp_gender);
        sp_company_type = findViewById(R.id.sp_company_type);

    }

    private void setchDDDateTimeField() {

        Calendar newCalendar = Calendar.getInstance();
        chddDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edit_dob.setText(Util.getDate(year, monthOfYear, dayOfMonth));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void agentResistrationDetails() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Districts...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            AgentRegisterRequest request = new AgentRegisterRequest();
            request.setFirstname(FirstName);
            request.setMiddleName(middleName);
            request.setLastname(LastName);
            request.setDateofbirth(edit_dob.getText().toString());
            request.setGender(genderData);
            request.setCompanyType(companyTypeData);
            request.setAgencyName(CompanyShopName);
            request.setDSFullId(DistributorFullID);
            request.setPanNo(PAN);
            request.setState(stateData);
            request.setDistrict(districtData);
            request.setCity(CityName);
            request.setCountry(countryData);
            request.setPincode(PinCode);
            request.setAddress(Address1);
            request.setAddress2(Address2);

            request.setOfficeState(officeStateData);
            request.setOfficeDistrict(officeDistrictData);
            request.setOfficeCity(officeCityName);
            request.setOfficeCountry(officeCountryData);
            request.setOfficePincode(officePinCode);
            request.setOfficeAddress(officeAddress1);
            request.setOfficeAddress2(officeAddress2);

            request.setEmailId(EmailID);
            request.setParam("AgentRegistration");
            request.setAthoMobile(MobileNo);

            RetrofitClient.getClient(getApplicationContext())
                    .agentRegister(request)
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

    public void showConfirmationDialog(String msg) {
        // TODO Auto-generated method stub
        final Dialog d = Util.getDialog(AddAgentActivity.this, R.layout.push_payment_confirmation_dialog);

        final Button btnSubmit = (Button) d.findViewById(R.id.btn_push_submit);
        final Button btnClosed = (Button) d.findViewById(R.id.close_push_button);
        final TextView header = (TextView) d.findViewById(R.id.title);
        final TextView tvConfirmation = (TextView) d.findViewById(R.id.tv_confirmation_dialog);

        header.setText("Confirmation");
        tvConfirmation.setText(msg);

        btnSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(AddAgentActivity.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            AddAgentActivity.this.finish();
            d.dismiss();


            d.cancel();
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            d.cancel();
        });
        d.show();
    }

    //Json for State
    private void stateRequest() {

        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching State...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            MainRequest request = new MainRequest();
            request.setDistributorId(distributorID);
            request.setTxnkey(key);

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

                            if (officeStateList == null)
                                officeStateList=new ArrayList<>();
                            else
                                officeStateList.clear();

                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    StateResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        if (res.getStateList() != null) {
                                            stateList = (ArrayList<StateModel>) res.getStateList();
                                            officeStateList=stateList;

                                            sp_state.setItem(stateList);
                                            sp_office_state.setItem(officeStateList);
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
    private void districtRequest(String state,String caller) {

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
                            if("personal".equalsIgnoreCase(caller)) {
                                if (districtList == null)
                                    districtList = new ArrayList<>();
                                else
                                    districtList.clear();
                            }else{
                                if (officeDistrictList == null)
                                    officeDistrictList = new ArrayList<>();
                                else
                                    officeDistrictList.clear();
                            }
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    DistrictResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        if (res.getDistrictList() != null) {
                                            if("personal".equalsIgnoreCase(caller)) {
                                                districtList = (ArrayList<DistrictModel>) res.getDistrictList();
                                                sp_district.setItem(districtList);
                                            }else {
                                                officeDistrictList = (ArrayList<DistrictModel>) res.getDistrictList();
                                                sp_office_district.setItem(officeDistrictList);
                                            }
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}