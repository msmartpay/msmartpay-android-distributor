package msmartds.in;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import msmartds.in.utility.Mysingleton;

/**
 * Created by Smartkinda on 6/30/2017.
 */

public class ReportActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String distributorID, key;
    private HorizontalScrollView horizontalScrollView;
    private ListView transReportList;
    private ProgressDialog pd;
    private String url_report = HttpURL.SummaryReport;
    private ArrayList<ReportModel> agentLists;
    private TextView tv_to,tv_from,tv_all,tv_in,tv_out;
    private LinearLayout ll_search;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private int i=0;
    private String toDate="",fromDate="";
    private ReportAdaptorClass adaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        transReportList = (ListView) findViewById(R.id.horizontal_list_view);
        tv_to = findViewById(R.id.tv_to);
        tv_from =  findViewById(R.id.tv_from);
        ll_search =  findViewById(R.id.ll_search);
        tv_all = findViewById(R.id.tv_all);
        tv_in =  findViewById(R.id.tv_in);
        tv_out =  findViewById(R.id.tv_out);

        sharedPreferences = getApplicationContext().getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        distributorID = sharedPreferences.getString("distributorId", null);
        key = sharedPreferences.getString("txnKey", null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Reports");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tv_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=1;
                setDateTimeField(tv_to);
            }
        });

        tv_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=0;
                setDateTimeField(tv_from);
            }
        });
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromDate.equalsIgnoreCase("")){
                    Toast.makeText(ReportActivity.this,"Select From Date", Toast.LENGTH_LONG).show();
                }else if(toDate.equalsIgnoreCase("")){
                    Toast.makeText(ReportActivity.this,"Select From Date", Toast.LENGTH_LONG).show();
                }else {
                    try {
                        ll_search.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        summaryReportRequestByDate();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(adaptor!=null){
                adaptor.getFilter().filter("");
                tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }else
            {
                Toast.makeText(ReportActivity.this, "Data is not available !", Toast.LENGTH_SHORT).show();
            }

            }
        });

        tv_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adaptor!=null){
                    adaptor.getFilter().filter("In");
                    tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }else {
                    Toast.makeText(ReportActivity.this, "Data is not available !", Toast.LENGTH_SHORT).show();
                }

            }
        });
        tv_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adaptor!=null){
                    adaptor.getFilter().filter("Out");
                    tv_all.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv_in.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv_out.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }else {
                    Toast.makeText(ReportActivity.this, "Data is not available !", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(!toDate.equalsIgnoreCase("")&&!fromDate.equalsIgnoreCase("")){
               /* tv_from.setText("From \n" +fromDate);
                tv_to.setText("To \n" +toDate);*/
                summaryReportRequestByDate();
            }else
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

        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, url_report,
                new JSONObject()
                        .put("distributorId", distributorID)
                        .put("txnkey", key)
                        .put("param", "AgentDetails"),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        pd.dismiss();
                        Log.d("data Request-->", distributorID+":"+key+"AgentDetails");
                        System.out.println("Object----5>"+object.toString());
                        try {
                            if (object.getString("message").equalsIgnoreCase("Success")) {
                                Log.d("url-called", url_report);
                                Log.d("url data", object.toString());
                                JSONArray jsonArray=object.getJSONArray("statement");
                                JSONObject jsonObject;
                                agentLists= new ArrayList<>();
                                Log.d("Array lenght ",jsonArray.length()+"");

                                for(int i=0; i<jsonArray.length(); i++)
                                {
                                    jsonObject= jsonArray.getJSONObject(i);
                                    if(agentLists!=null)
                                        agentLists.clear();
                                    agentLists.add(new ReportModel(jsonObject.getString("TransactionNo"),
                                            jsonObject.getString("DateOfTransaction")+" "+jsonObject.getString("TimeOfTransaction"),
                                            jsonObject.getString("Service"),
                                            jsonObject.getString("TransactionAmount"),
                                            jsonObject.getString("charge"),
                                            jsonObject.getString("NetTransactionAmount"),
                                            jsonObject.getString("ActionOnBalanceAmount"),
                                            jsonObject.getString("FinalBalanceAmount"),
                                            jsonObject.getString("TransactionStatus"),
                                            jsonObject.getString("Remarks")));
                                }

                                if(agentLists!=null && agentLists.size()>0){
                                    adaptor=new ReportAdaptorClass(ReportActivity.this, agentLists);
                                    transReportList.setAdapter(adaptor);
                                }else{
                                    Toast.makeText(ReportActivity.this, "Data not available !", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(ReportActivity.this, object.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse (VolleyError error){
                pd.dismiss();
                Toast.makeText(ReportActivity.this, "Server Error : "+error.toString(), Toast.LENGTH_SHORT).show();
            }
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

        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, HttpURL.AccountStatementByDate,
                new JSONObject()
                        .put("distributorId", distributorID)
                        .put("txnkey", key)
                        .put("fromDate", fromDate)
                        .put("toDate",toDate),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        pd.dismiss();
                        Log.d("data Request-->", distributorID+":"+key+"AgentDetails");
                        System.out.println("Object----5>"+object.toString());
                        try {
                            if (object.getString("message").equalsIgnoreCase("Success")) {
                                Log.d("url-called", url_report);
                                Log.d("url data", object.toString());
                                JSONArray jsonArray=object.getJSONArray("statement");
                                JSONObject jsonObject;
                                agentLists= new ArrayList<>();
                                Log.d("Array lenght ",jsonArray.length()+"");

                                for(int i=0; i<jsonArray.length(); i++)
                                {
                                    jsonObject= jsonArray.getJSONObject(i);
                                    agentLists.add(new ReportModel(jsonObject.getString("TransactionNo"),jsonObject.getString("DateOfTransaction")+" "+jsonObject.getString("TimeOfTransaction"),jsonObject.getString("Service"),jsonObject.getString("TransactionAmount"),jsonObject.getString("charge"),jsonObject.getString("NetTransactionAmount"),jsonObject.getString("ActionOnBalanceAmount"),jsonObject.getString("FinalBalanceAmount"),jsonObject.getString("TransactionStatus"),jsonObject.getString("Remarks")));
                                }
                                adaptor=new ReportAdaptorClass(ReportActivity.this, agentLists);
                                transReportList.setAdapter(adaptor);
                            } else {
                                Toast.makeText(ReportActivity.this, object.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse (VolleyError error){
                pd.dismiss();
                Toast.makeText(ReportActivity.this, "Server Error : "+error.toString(), Toast.LENGTH_SHORT).show();
            }
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
                if(i==0) {
                    fromDate=dateFormatter.format(newDate.getTime());
                    myview.setText("From \n" +fromDate);

                } else {
                    toDate=dateFormatter.format(newDate.getTime());
                    myview.setText("To \n" + toDate);
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.show();

    }
}
