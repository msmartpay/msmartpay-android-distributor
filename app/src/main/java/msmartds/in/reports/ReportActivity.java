package msmartds.in.reports;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import msmartds.in.R;
import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.utility.L;
import msmartds.in.utility.Mysingleton;

/**
 * Created by Smartkinda on 6/30/2017.
 */

public class ReportActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String distributorID, txnKey;
    private HorizontalScrollView horizontalScrollView;
    private ListView transReportList;
    private ProgressDialog pd;
    private String url_report = HttpURL.SummaryReport;
    private String url_report_by_date = HttpURL.AccountStatementByDate;
    private ArrayList<ReportsModel> agentLists;
    private TextView tv_to, tv_from, tv_all, tv_in, tv_out;
    private LinearLayout ll_search;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private int i = 0;
    private String toDate = "", fromDate = "";
    private ReportAdaptorClass adaptor;


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

        sharedPreferences = getApplicationContext().getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        distributorID = sharedPreferences.getString("distributorId", null);
        txnKey = sharedPreferences.getString("txnKey", null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Reports");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


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
                Toast.makeText(ReportActivity.this, "Select From Date", Toast.LENGTH_LONG).show();
            } else if (toDate.equalsIgnoreCase("")) {
                Toast.makeText(ReportActivity.this, "Select From Date", Toast.LENGTH_LONG).show();
            } else {
                try {
                    ll_search.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    summaryReportRequestByDate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        tv_all.setOnClickListener(v -> {
            if (adaptor != null) {
                adaptor.getFilter().filter("");
                tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                Toast.makeText(ReportActivity.this, "Data is not available !", Toast.LENGTH_SHORT).show();
            }

        });

        tv_in.setOnClickListener(v -> {
            if (adaptor != null) {
                adaptor.getFilter().filter("In");
                tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                Toast.makeText(ReportActivity.this, "Data is not available !", Toast.LENGTH_SHORT).show();
            }

        });
        tv_out.setOnClickListener(v -> {
            if (adaptor != null) {
                adaptor.getFilter().filter("Out");
                tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                Toast.makeText(ReportActivity.this, "Data is not available !", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!toDate.equalsIgnoreCase("") && !fromDate.equalsIgnoreCase("")) {
               /* tv_from.setText("From \n" +fromDate);
                tv_to.setText("To \n" +toDate);*/
                summaryReportRequestByDate();
            } else
                summaryReportRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void summaryReportRequest() throws JSONException {
        pd = ProgressDialog.show(ReportActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        JSONObject reqObj = new JSONObject()
                .put("distributorId", distributorID)
                .put("txnkey", txnKey)
                .put("param", "AgentDetails");
        L.m2("Url--1>", url_report);
        L.m2("Req--1>", reqObj.toString());
        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, url_report,
                reqObj,
                object -> {
                    pd.dismiss();
                    try {
                        L.m2("Url--1>", url_report);
                        L.m2("Response--1>", object.toString());
                        if (object.getString("message").equalsIgnoreCase("Success")) {
                            JSONArray jsonArray = object.getJSONArray("data");
                            JSONObject jsonObject;
                            agentLists = new ArrayList<>();
                            Log.d("Array lenght ", jsonArray.length() + "");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                if (agentLists != null)
                                    agentLists.clear();
                                ReportsModel reportsModel = new ReportsModel();
                                reportsModel.setTransactionNo(jsonObject.getString("TransactionNo"));
                                reportsModel.setDateOfTransaction(jsonObject.getString("DateOfTransaction"));
                                reportsModel.setTimeOfTransaction(jsonObject.getString("TimeOfTransaction"));
                                reportsModel.setService(jsonObject.getString("Service"));
                                reportsModel.setCommission(jsonObject.getDouble("Commission"));
                                reportsModel.setNetTransactionAmount(jsonObject.getDouble("NetTransactionAmount"));
                                reportsModel.setUpdatedBalanceAmount(jsonObject.getDouble("UpdatedBalanceAmount"));
                                reportsModel.setTransactionStatus(jsonObject.getString("TransactionStatus"));
                                reportsModel.setRemarks(jsonObject.getString("Remarks"));
                                reportsModel.setActionOnBalanceAmount(jsonObject.getString("ActionOnBalanceAmount"));
                                reportsModel.setCharge(jsonObject.getDouble("charge"));
                                reportsModel.setPreviousBalanceAmount(jsonObject.getDouble("PreviousBalanceAmount"));
                                reportsModel.setFinalBalanceAmount(jsonObject.getDouble("FinalBalanceAmount"));
                                reportsModel.setIdno(jsonObject.getString("IDNO"));
                                agentLists.add(reportsModel);
                            }
                            if (agentLists != null && agentLists.size() > 0) {
                                adaptor = new ReportAdaptorClass(ReportActivity.this, agentLists);
                                transReportList.setAdapter(adaptor);
                            } else {
                                Toast.makeText(ReportActivity.this, "Data not available !", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(ReportActivity.this, object.getString("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            pd.dismiss();
            Toast.makeText(ReportActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
        });
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);

    }

    private void summaryReportRequestByDate() throws JSONException {
        pd = ProgressDialog.show(ReportActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        JSONObject reqObj = new JSONObject()
                .put("distributorId", distributorID)
                .put("txnkey", txnKey)
                .put("fromDate", fromDate)
                .put("toDate", toDate);

        L.m2("Url--1>", url_report_by_date);
        L.m2("Request--1>", reqObj.toString());

        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, url_report_by_date, reqObj,
                object -> {
                    pd.dismiss();
                    try {
                        L.m2("Url--1>", url_report_by_date);
                        L.m2("Response--1>", object.toString());
                        if (object.getString("message").equalsIgnoreCase("Success")) {
                            JSONArray jsonArray = object.getJSONArray("data");
                            JSONObject jsonObject;
                            agentLists = new ArrayList<>();
                            Log.d("Array lenght ", jsonArray.length() + "");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                ReportsModel reportsModel = new ReportsModel();
                                reportsModel.setTransactionNo(jsonObject.getString("TransactionNo"));
                                reportsModel.setDateOfTransaction(jsonObject.getString("DateOfTransaction"));
                                reportsModel.setTimeOfTransaction(jsonObject.getString("TimeOfTransaction"));
                                reportsModel.setService(jsonObject.getString("Service"));
                                reportsModel.setCommission(jsonObject.getDouble("Commission"));
                                reportsModel.setNetTransactionAmount(jsonObject.getDouble("NetTransactionAmount"));
                                reportsModel.setUpdatedBalanceAmount(jsonObject.getDouble("UpdatedBalanceAmount"));
                                reportsModel.setTransactionStatus(jsonObject.getString("TransactionStatus"));
                                reportsModel.setRemarks(jsonObject.getString("Remarks"));
                                reportsModel.setActionOnBalanceAmount(jsonObject.getString("ActionOnBalanceAmount"));
                                reportsModel.setCharge(jsonObject.getDouble("charge"));
                                reportsModel.setPreviousBalanceAmount(jsonObject.getDouble("PreviousBalanceAmount"));
                                reportsModel.setFinalBalanceAmount(jsonObject.getDouble("FinalBalanceAmount"));
                                reportsModel.setIdno(jsonObject.getString("IDNO"));
                                agentLists.add(reportsModel);
                            }
                            adaptor = new ReportAdaptorClass(ReportActivity.this, agentLists);
                            transReportList.setAdapter(adaptor);
                        } else {
                            Toast.makeText(ReportActivity.this, object.getString("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            pd.dismiss();
            Toast.makeText(ReportActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
        });
        getSocketTimeOut(jsonrequest);
        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setDateTimeField(final TextView myview) {

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(ReportActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (i == 0) {
                    fromDate = dateFormatter.format(newDate.getTime());
                    myview.setText("From \n" + fromDate);

                } else {
                    toDate = dateFormatter.format(newDate.getTime());
                    myview.setText("To \n" + toDate);
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.show();

    }
}
