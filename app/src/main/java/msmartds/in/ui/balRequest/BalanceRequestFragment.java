package msmartds.in.ui.balRequest;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.android.material.textfield.TextInputLayout;
import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainRequest;
import  msmartds.in.network.model.MainResponse2;
import  msmartds.in.network.model.balanceRequest.BalRequest;
import  msmartds.in.network.model.bankCollect.CollectBankModel;
import  msmartds.in.network.model.bankCollect.CollectBankResponse;
import  msmartds.in.ui.home.DashBoardActivity;
import  msmartds.in.util.BaseFragment;
import  msmartds.in.util.Keys;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceRequestFragment extends BaseFragment {

    private SmartMaterialSpinner sp_bank, sp_mode_type;
    private EditText et_amount, et_refId, et_remarks, et_date;
    private TextInputLayout til_refId;
    private Button btn_submit;

    private DatePickerDialog fromDatePickerDialog;
    private String refId, bankName = "", amount, ddate, payment_mode = "";
    private String  mdID;
    private List<CollectBankModel> bankList;
    private List<String> modeLIst;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bal_request_fragment, container, false);

        initViews(view);

        mdID = Util.getData(requireActivity(), Keys.DS_MD_ID);
        setDateTimeField();
        modeLIst = Arrays.asList(getResources().getStringArray(R.array.mode_type));
        sp_mode_type.setItem(modeLIst);
        getBankDetails();

        et_date.setOnClickListener(v -> {
            if (v == et_date) {
                fromDatePickerDialog.show();
            }
        });
        sp_bank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > -1) {
                    bankName = bankList.get(i).getBank_name();
                    L.m2("bankName", bankName);
                } else {
                    bankName = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp_mode_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > -1) {
                    payment_mode = modeLIst.get(i);
                    L.m2("payment_mode", payment_mode);
                    if ("cash".equalsIgnoreCase(payment_mode)) {
                        Util.hideView(til_refId);
                    } else {
                        Util.showView(til_refId);
                    }
                } else {
                    payment_mode = "";
                    Util.showView(til_refId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn_submit.setOnClickListener(view1 -> balanceRequest());
        return view;
    }

    private void initViews(View view) {
        Util.setScrollView(view.findViewById(R.id.scrollView));

        et_remarks = view.findViewById(R.id.et_remarks);
        et_amount = view.findViewById(R.id.et_amount);
        et_refId = view.findViewById(R.id.et_refId);
        et_date = view.findViewById(R.id.et_date);
        sp_bank = view.findViewById(R.id.sp_bank);
        sp_mode_type = view.findViewById(R.id.sp_mode_type);
        til_refId = view.findViewById(R.id.til_refId);
        btn_submit = view.findViewById(R.id.btn_submit);

    }

    private void balanceRequest() {
        String remarkss = et_remarks.getText().toString();
        remarkss = remarkss.replaceAll(" ", "_");
        amount = et_amount.getText().toString();
        ddate = et_date.getText().toString();
        refId = et_refId.getText().toString();
        refId.replaceAll(" ", "_");

        if (bankName.isEmpty()) {
            L.toast(requireActivity(), "Select Bank");
        } else if (payment_mode.isEmpty()) {
            L.toast(requireActivity(), "Select Payment Mode");
        } else if (amount.isEmpty() || Util.getAmount(amount) < 100) {
            L.toast(requireActivity(), "Enter valid amount");
        } else if (ddate.isEmpty()) {
            L.toast(requireActivity(), "Select Deposit Date");
        } else if (!"cash".equalsIgnoreCase(payment_mode) && (refId != null && refId.isEmpty())) {
            L.toast(requireActivity(), "Enter Valid Transaction Reference Id");
        } else {
            if (NetworkConnection.isConnectionAvailable(requireActivity())) {
                final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Balance Requesting...");
                ProgressDialogFragment.showDialog(pd, getChildFragmentManager());

                BalRequest request = new BalRequest();
                request.setMdID(mdID);
                request.setBankName(bankName);
                request.setRefId(refId);
                request.setAmount(amount);
                request.setDepositDate(ddate);
                request.setRemark(remarkss);
                request.setMode(payment_mode);
                RetrofitClient.getClient(requireActivity())
                        .balanceRequestWallet(request)
                        .enqueue(new Callback<MainResponse2>() {
                            @Override
                            public void onResponse(Call<MainResponse2> call, retrofit2.Response<MainResponse2> response) {
                                pd.dismiss();
                                try {
                                    if (response.isSuccessful() && response.body() != null) {
                                        MainResponse2 res = response.body();
                                        if ("0".equalsIgnoreCase(res.getStatus())) {
                                            new AlertDialog.Builder(requireActivity())
                                                    .setTitle("Status")
                                                    .setIcon(R.drawable.trnsuccess)
                                                    .setMessage(res.getMessage())
                                                    .setPositiveButton("Yes", (dialog, which) -> {
                                                        Intent intent = new Intent(requireActivity(), DashBoardActivity.class);
                                                        startActivity(intent);
                                                        requireActivity().finish();
                                                    })
                                                    .show();
                                        } else {
                                            new AlertDialog.Builder(requireActivity())
                                                    .setTitle("Status")
                                                    .setIcon(R.drawable.failed)
                                                    .setMessage(res.getMessage() + "")
                                                    .setPositiveButton("Ok", (dialog, which) -> {
                                                        Intent intent = new Intent(requireActivity(), DashBoardActivity.class);
                                                        startActivity(intent);
                                                        requireActivity().finish();
                                                    })
                                                    .show();
                                        }

                                    } else {
                                        L.toast(requireActivity(), "No Server Response");
                                    }
                                } catch (Exception e) {
                                    L.toast(requireActivity(), "Parser Error : " + e.getLocalizedMessage());
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


    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                et_date.setText(Util.getDate(year, monthOfYear, dayOfMonth));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }


    private void getBankDetails() {
        if (NetworkConnection.isConnectionAvailable(requireActivity())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Banks...");
            ProgressDialogFragment.showDialog(pd, getChildFragmentManager());

            MainRequest request = new MainRequest();

            RetrofitClient.getClient(requireActivity())
                    .getCollectBankDetails(request)
                    .enqueue(new Callback<CollectBankResponse>() {
                        @Override
                        public void onResponse(Call<CollectBankResponse> call, retrofit2.Response<CollectBankResponse> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    CollectBankResponse res = response.body();
                                    if (bankList == null)
                                        bankList = new ArrayList<>();
                                    else
                                        bankList.clear();
                                    if ("0".equalsIgnoreCase(res.getStatus()) && res.getBank_List() != null) {
                                        bankList = (ArrayList<CollectBankModel>) res.getBank_List();
                                    }
                                    sp_bank.setItem(bankList);
                                } else {
                                    L.toast(requireActivity(), "No Server Response");
                                }
                            } catch (Exception e) {
                                L.toast(requireActivity(), "Parser Error : " + e.getLocalizedMessage());
                                L.m2("Parser Error", e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<CollectBankResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }

    }


}
