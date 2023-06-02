package msmartds.in.ui.report.agent;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.report.agent.AgentReportModel;
import  msmartds.in.network.model.report.agent.AgentReportRequest;
import  msmartds.in.network.model.report.agent.AgentReportResponse;
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

public class AgentReportActivity extends BaseActivity {

    private ListView transReportList;
    private ArrayList<AgentReportModel> agentLists;
    private TextView tv_to, tv_from, tv_all, tv_in, tv_out;
    private LinearLayout ll_agent_search;
    private EditText et_search;

    private DatePickerDialog fromDatePickerDialog;
    private int i = 0;
    private String toDate = "", fromDate = "";
    private AgentReportAdaptorClass adaptor;

    private Call<AgentReportResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agent_report_activity);

        transReportList = (ListView) findViewById(R.id.horizontal_list_view);
        tv_to = findViewById(R.id.tv_to);
        tv_from = findViewById(R.id.tv_from);
        ll_agent_search = findViewById(R.id.ll_agent_search);
        tv_all = findViewById(R.id.tv_all);
        tv_in = findViewById(R.id.tv_in);
        tv_out = findViewById(R.id.tv_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Agent Reports");
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
        ll_agent_search.setOnClickListener(v -> {
            if (fromDate.equalsIgnoreCase("")) {
                L.toast(AgentReportActivity.this, "Select From Date");
            } else if (toDate.equalsIgnoreCase("")) {
                L.toast(AgentReportActivity.this, "Select From Date");
            } else {

                ll_agent_search.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
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
                L.toast(AgentReportActivity.this, "Data is not available !");
            }

        });

        tv_in.setOnClickListener(v -> {
            if (adaptor != null) {
                adaptor.getFilter().filter("In");
                tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                L.toast(AgentReportActivity.this, "Data is not available !");
            }

        });
        tv_out.setOnClickListener(v -> {
            if (adaptor != null) {
                adaptor.getFilter().filter("Out");
                tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                L.toast(AgentReportActivity.this, "Data is not available !");
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
            AgentReportRequest reportRequest = new AgentReportRequest();


            call = RetrofitClient.getClient(getApplicationContext())
                    .getAgentReports(reportRequest);
            call.enqueue(new Callback<AgentReportResponse>() {
                @Override
                public void onResponse(Call<AgentReportResponse> call, retrofit2.Response<AgentReportResponse> response) {
                    pd.dismiss();
                    if (agentLists == null)
                        agentLists = new ArrayList<>();
                    else
                        agentLists.clear();
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            AgentReportResponse res = response.body();
                            if ("0".equalsIgnoreCase(res.getStatus())) {
                                if (res.getStatement() != null)
                                    agentLists = (ArrayList<AgentReportModel>) res.getStatement();
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
                    adaptor = new AgentReportAdaptorClass(AgentReportActivity.this, agentLists);
                    transReportList.setAdapter(adaptor);
                }

                @Override
                public void onFailure(Call<AgentReportResponse> call, Throwable t) {
                    pd.dismiss();
                    L.m2("Parser Error", t.getLocalizedMessage());
                }
            });
        }
    }

    private void summaryReportRequestByDate() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Reports...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            if (call != null)
                call.cancel();

            AgentReportRequest reportRequest = new AgentReportRequest();
            reportRequest.setFromDate(fromDate);
            reportRequest.setToDate(toDate);
            reportRequest.setService("All");
            reportRequest.setAgentId("All");

            call = RetrofitClient.getClient(getApplicationContext())
                    .getAgentReports(reportRequest);
            call.enqueue(new Callback<AgentReportResponse>() {
                @Override
                public void onResponse(Call<AgentReportResponse> call, retrofit2.Response<AgentReportResponse> response) {
                    pd.dismiss();
                    if (agentLists == null)
                        agentLists = new ArrayList<>();
                    else
                        agentLists.clear();
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            AgentReportResponse res = response.body();
                            if ("0".equalsIgnoreCase(res.getStatus())) {
                                if (res.getStatement() != null)
                                    agentLists = (ArrayList<AgentReportModel>) res.getStatement();
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
                    adaptor = new AgentReportAdaptorClass(AgentReportActivity.this, agentLists);
                    transReportList.setAdapter(adaptor);
                }

                @Override
                public void onFailure(Call<AgentReportResponse> call, Throwable t) {
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
        fromDatePickerDialog = new DatePickerDialog(AgentReportActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
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
