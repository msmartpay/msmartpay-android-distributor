package msmartds.in.ui.report;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.report.ReportModel;
import  msmartds.in.network.model.report.ReportRequest;
import  msmartds.in.network.model.report.ReportResponse;
import msmartds.in.network.model.report.UpdateDSTxnRemarkRequest;
import msmartds.in.ui.agent.UpdateAgentAutoCreditActivity;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Smartkinda on 6/30/2017.
 */

public class ReportActivity extends BaseActivity {

    private ListView transReportList;
    private ArrayList<ReportModel> agentLists;
    private TextView tv_to, tv_from, tv_all, tv_in, tv_out;
    private LinearLayout ll_search;
    private EditText et_search;

    private DatePickerDialog fromDatePickerDialog;
    private int i = 0;
    private String toDate = "", fromDate = "";
    private ReportAdaptorClass adaptor;

    private Call<ReportResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        transReportList = (ListView) findViewById(R.id.horizontal_list_view);
        tv_to = findViewById(R.id.tv_to);
        tv_from = findViewById(R.id.tv_from);
        ll_search = findViewById(R.id.ll_search);
        tv_all = findViewById(R.id.tv_all);
        tv_in = findViewById(R.id.tv_in);
        tv_out = findViewById(R.id.tv_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Reports");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adaptor != null)
                    adaptor.getFilter2().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv_to.setOnClickListener(v -> {
            i = 1;
            setDateTimeField(tv_to);
        });

        tv_from.setOnClickListener(v -> {
            i = 0;
            setDateTimeField(tv_from);
        });
        ll_search.setOnClickListener(v -> {
            if (fromDate.equalsIgnoreCase("")) {
                L.toast(ReportActivity.this, "Select From Date");
            } else if (toDate.equalsIgnoreCase("")) {
                L.toast(ReportActivity.this, "Select From Date");
            } else {

                ll_search.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                et_search.setText("");
                summaryReportRequestByDate();

            }
        });

        tv_all.setOnClickListener(v -> {
            if (adaptor != null) {
                adaptor.getFilter().filter("");
                tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                L.toast(ReportActivity.this, "Data is not available !");
            }

        });

        tv_in.setOnClickListener(v -> {
            if (adaptor != null) {
                adaptor.getFilter().filter("In");
                tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                L.toast(ReportActivity.this, "Data is not available !");
            }

        });
        tv_out.setOnClickListener(v -> {
            if (adaptor != null) {
                adaptor.getFilter().filter("Out");
                tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                L.toast(ReportActivity.this, "Data is not available !");
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!toDate.equalsIgnoreCase("") && !fromDate.equalsIgnoreCase("")) {
               /* tv_from.setText("From \n" +fromDate);
                tv_to.setText("To \n" +toDate);*/
            summaryReportRequestByDate();
        } else
            summaryReportRequest();
    }

    private void summaryReportRequest() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Reports...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            if (call != null)
                call.cancel();
            ReportRequest request = new ReportRequest();
            request.setParam("AgentDetails");

            call = RetrofitClient.getClient(getApplicationContext())
                    .getReports(request);
            call.enqueue(new Callback<ReportResponse>() {
                @Override
                public void onResponse(Call<ReportResponse> call, retrofit2.Response<ReportResponse> response) {
                    pd.dismiss();
                    if (agentLists == null)
                        agentLists = new ArrayList<>();
                    else
                        agentLists.clear();
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            ReportResponse res = response.body();
                            if ("0".equalsIgnoreCase(res.getStatus())) {
                                if (res.getStatement() != null)
                                    agentLists = (ArrayList<ReportModel>) res.getStatement();
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
                    adaptor = new ReportAdaptorClass(ReportActivity.this, agentLists);
                    transReportList.setAdapter(adaptor);
                }

                @Override
                public void onFailure(Call<ReportResponse> call, Throwable t) {
                    pd.dismiss();
                    L.m2("Parser Error", t.getLocalizedMessage());
                }
            });
        }
    }

    public void updateDSTransactionRemark(String id,String remark) {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Updating remarks...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            UpdateDSTxnRemarkRequest request = new UpdateDSTxnRemarkRequest();
            request.setIdNo(id);
            request.setRemark(remark);

            call = RetrofitClient.getClient(getApplicationContext())
                    .updateDSTransactionRemark(request);
            call.enqueue(new Callback<ReportResponse>() {
                @Override
                public void onResponse(Call<ReportResponse> call, retrofit2.Response<ReportResponse> response) {
                    pd.dismiss();

                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            ReportResponse res = response.body();
                            if(res.getStatus().equalsIgnoreCase("0")){
                                showConfirmationDialog(res.getMessage(),true);
                            }else{
                                showConfirmationDialog(res.getMessage(),false);
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
                public void onFailure(Call<ReportResponse> call, Throwable t) {
                    pd.dismiss();
                    L.m2("Parser Error", t.getLocalizedMessage());
                }
            });
        }
    }
    public void showConfirmationDialog(String msg, final boolean isSuccess) {
        final Dialog d = Util.getDialog(ReportActivity.this, R.layout.confirmation_dialog);

        final Button btnSubmit = (Button) d.findViewById( R.id.btn_push_submit);
        final Button btnClosed = (Button) d.findViewById( R.id.close_push_button);
        final TextView tvConfirmation = (TextView) d.findViewById( R.id.tv_confirmation_dialog);

        tvConfirmation.setText(msg);
        btnSubmit.setOnClickListener(v -> {
            if (isSuccess) {
                summaryReportRequest();
                d.dismiss();
            } else {
                d.dismiss();
            }
        });

        btnClosed.setOnClickListener(v -> {
            d.cancel();
        });

        d.show();
    }

    private void summaryReportRequestByDate() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Reports...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            if (call != null)
                call.cancel();

            ReportRequest request = new ReportRequest();
            request.setFromDate(fromDate);
            request.setToDate(toDate);

            call = RetrofitClient.getClient(getApplicationContext())
                    .getReportByDate(request);
            call.enqueue(new Callback<ReportResponse>() {
                @Override
                public void onResponse(Call<ReportResponse> call, retrofit2.Response<ReportResponse> response) {
                    pd.dismiss();
                    if (agentLists == null)
                        agentLists = new ArrayList<>();
                    else
                        agentLists.clear();
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            ReportResponse res = response.body();
                            if ("0".equalsIgnoreCase(res.getStatus())) {
                                if (res.getStatement() != null)
                                    agentLists = (ArrayList<ReportModel>) res.getStatement();
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
                    adaptor = new ReportAdaptorClass(ReportActivity.this, agentLists);
                    transReportList.setAdapter(adaptor);
                }

                @Override
                public void onFailure(Call<ReportResponse> call, Throwable t) {
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

    private void setDateTimeField(final TextView myview) {

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(ReportActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
            if (i == 0) {
                fromDate = Util.getDate(year, monthOfYear, dayOfMonth);
                myview.setText("From \n" + fromDate);

            } else {
                toDate = Util.getDate(year, monthOfYear, dayOfMonth);
                myview.setText("To \n" + toDate);
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.show();

    }
}
