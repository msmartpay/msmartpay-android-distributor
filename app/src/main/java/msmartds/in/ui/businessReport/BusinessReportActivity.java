package msmartds.in.ui.businessReport;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.business.BusinessReportModel;
import  msmartds.in.network.model.business.BusinessReportRequest;
import  msmartds.in.network.model.business.BusinessReportResponse;
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

public class BusinessReportActivity extends BaseActivity {

    private ListView transReportList;
    private ArrayList<BusinessReportModel> agentLists;
    private TextView tv_to, tv_from;
    private LinearLayout ll_search;

    private DatePickerDialog fromDatePickerDialog;
    private int i = 0;
    private String toDate = "", fromDate = "";
    private BusinessReportAdaptor adaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_business_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Reports");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        transReportList = (ListView) findViewById(R.id.horizontal_list_view);
        tv_to = findViewById(R.id.tv_to);
        tv_from = findViewById(R.id.tv_from);
        ll_search = findViewById(R.id.ll_search);

        Calendar curDate = Calendar.getInstance();
        Calendar pre30Date = Calendar.getInstance();
        pre30Date.add(Calendar.DAY_OF_MONTH, -30);
        fromDate = Util.getDate(pre30Date.getTime());
        tv_from.setText("From \n" + fromDate);

        toDate = Util.getDate(curDate.getTime());
        tv_to.setText("To \n" + toDate);


        summaryReportRequest();
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
                L.toast(BusinessReportActivity.this, "Select From Date");
            } else if (toDate.equalsIgnoreCase("")) {
                L.toast(BusinessReportActivity.this, "Select From Date");
            } else {
                ll_search.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                summaryReportRequest();

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void summaryReportRequest() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Business...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            BusinessReportRequest request = new BusinessReportRequest();
            request.setFrom(fromDate);
            request.setTo(toDate);
            RetrofitClient.getClient(getApplicationContext())
                    .getBusinessReport(request)
                    .enqueue(new Callback<BusinessReportResponse>() {
                        @Override
                        public void onResponse(Call<BusinessReportResponse> call, retrofit2.Response<BusinessReportResponse> response) {
                            pd.dismiss();
                            if (agentLists == null)
                                agentLists = new ArrayList<>();
                            else
                                agentLists.clear();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    BusinessReportResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        if (res.getStatement() != null && res.getStatement().size() > 0)
                                            agentLists = (ArrayList<BusinessReportModel>) res.getStatement();

                                    } else {
                                        L.toast(getApplicationContext(), res.getMessage());
                                    }

                                } else {
                                    L.toast(getApplicationContext(), "No Server Response");
                                }

                                adaptor = new BusinessReportAdaptor(BusinessReportActivity.this, agentLists);
                                transReportList.setAdapter(adaptor);
                            } catch (Exception e) {
                                L.toast(getApplicationContext(), "Parser Error : " + e.getLocalizedMessage());
                                L.m2("Parser Error", e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<BusinessReportResponse> call, Throwable t) {
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
        fromDatePickerDialog = new DatePickerDialog(BusinessReportActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
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
