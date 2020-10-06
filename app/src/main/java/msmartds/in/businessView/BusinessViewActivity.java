package msmartds.in.businessView;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import msmartds.in.R;
import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.utility.Mysingleton;

public class BusinessViewActivity extends BaseActivity {


    private String distributorid;
    private ProgressDialog pd;

    private Context context;
    ArrayList<BusinessViewItem> currentMonth = null;

    ArrayList<BusinessViewItem> previousMonth = null;


    RecyclerView recycler_viewRechage;
    RecyclerView recycler_viewWallet;


    private TextView currTextView, prevTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.business_view_activity);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(msmartds.in.R.id.toolbar);

        toolbar.setTitle("Business View");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = BusinessViewActivity.this;
        SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("Details", MODE_PRIVATE);
        distributorid = myPrefs.getString("distributorId", "");
        currTextView = findViewById(R.id.currTextView);
        prevTextView = findViewById(R.id.prevTextView);

       /* RecyclerView */
        recycler_viewRechage = (RecyclerView) findViewById(msmartds.in.R.id.recycler_viewRechage);
       /* RecyclerView*/
        recycler_viewWallet = (RecyclerView) findViewById(msmartds.in.R.id.recycler_viewWallet);


        try {
            getViewBusiness();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void getViewBusiness() throws JSONException {
        pd = ProgressDialog.show(BusinessViewActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        try {
            JSONObject jsonObjectReq = new JSONObject()
                    .put("distributor_id", distributorid);

            Log.d("summary_Request--1>", jsonObjectReq.toString());
            String serviceBuisinessDoneURL = HttpURL.BusinessViewUrl;
            JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, serviceBuisinessDoneURL, jsonObjectReq,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject object) {
                            pd.dismiss();
                            try {
                                if (object.get("status") != null && object.get("status").equals("0")) {
                                    final JSONArray parentArray = (JSONArray) object.get("distributorBuisinessDetails");

                                    if (parentArray.length() > 0) {
                                        for (int i = 0; i < parentArray.length(); i++) {
                                            JSONObject obj = (JSONObject) parentArray.get(i);

                                            if (obj.getString("Month-Type").equals("CurrentMonth")) {
                                                Log.d("check--1", obj.getString("Month-Type"));

                                                if (obj.getString("Month-Type").equals("CurrentMonth")) {
                                                    currTextView.setVisibility(View.GONE);
                                                    BusinessViewItem businessItem = new BusinessViewItem();

                                                    if (currentMonth == null)

                                                        currentMonth = new ArrayList<>();
                                                    businessItem.setTransAmount(obj.get("Trans-Amount") + "");
                                                    businessItem.setTransCount(obj.get("Trans-Count") + "");
                                                    businessItem.setTransStatus(obj.get("Trans-Status") + "");
                                                    businessItem.setServiceName(obj.get("Service-Name") + "");
                                                    businessItem.setMonthType(obj.get("Month-Type") + "");


                                                    currentMonth.add(businessItem);

                                                } else if (obj.getString("Month-Type").equals("")) {

                                                    currTextView.setVisibility(View.VISIBLE);
                                                }


                                            } else if (obj.getString("Month-Type").equals("PreviousMonth")) {
                                                Log.d("check--2", obj.getString("Month-Type"));

                                                if (obj.getString("Month-Type").equals("PreviousMonth")) {
                                                    prevTextView.setVisibility(View.GONE);

                                                    BusinessViewItem businessItem = new BusinessViewItem();
                                                    if (previousMonth == null)

                                                        previousMonth = new ArrayList<>();
                                                    businessItem.setTransAmount(obj.get("Trans-Amount") + "");
                                                    businessItem.setTransCount(obj.get("Trans-Count") + "");
                                                    businessItem.setTransStatus(obj.get("Trans-Status") + "");
                                                    businessItem.setServiceName(obj.get("Service-Name") + "");
                                                    businessItem.setMonthType(obj.get("Month-Type") + "");

                                                    previousMonth.add(businessItem);
                                                } else if (obj.getString("Month-Type").equals("")) {
                                                    prevTextView.setVisibility(View.VISIBLE);
                                                }


                                            }

                                        }


                                        if (currentMonth != null) {
                                            currTextView.setVisibility(View.GONE);
                                            RecyclerAdapterCurrent adapterCurrent = new RecyclerAdapterCurrent(getApplicationContext(), currentMonth);
                                            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(BusinessViewActivity.this, 2);
                                            recycler_viewRechage.setLayoutManager(layoutManager);
                                            recycler_viewRechage.setItemAnimator(new DefaultItemAnimator());
                                            recycler_viewRechage.setAdapter(adapterCurrent);
                                        } else if (currentMonth == null) {
                                            currTextView.setVisibility(View.VISIBLE);
                                        }

                                        if (previousMonth != null) {
                                            prevTextView.setVisibility(View.GONE);
                                            RecyclerAdaptorPrevious adaptorPrevious1 = new RecyclerAdaptorPrevious(getApplicationContext(), previousMonth);
                                            RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(BusinessViewActivity.this, 2);
                                            recycler_viewWallet.setLayoutManager(layoutManager1);
                                            recycler_viewWallet.setItemAnimator(new DefaultItemAnimator());
                                            recycler_viewWallet.setAdapter(adaptorPrevious1);
                                        } else if (previousMonth == null) {
                                            prevTextView.setVisibility(View.VISIBLE);
                                        }


                                    }
                                } else {
                                    Toast.makeText(context, "Data is not available...", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener()

            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(BusinessViewActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            getSocketTimeOut(jsonrequest);
            Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);
        } catch (Exception exp) {
            pd.dismiss();
            exp.printStackTrace();
        }
    }


    //==============================current=====================================================
    private class RecyclerAdapterCurrent extends RecyclerView.Adapter<RecyclerAdapterCurrent.MyViewHolder> {
        private Context context;

        ArrayList<BusinessViewItem> balance;
        private View itemView;

        RecyclerAdapterCurrent(Context context, ArrayList<BusinessViewItem> balance) {
            this.context = context;
            this.balance = balance;

        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView balance1;
            TextView recharge;

            MyViewHolder(View itemView) {
                super(itemView);
                balance1 = itemView.findViewById(msmartds.in.R.id.money);
                recharge = itemView.findViewById(msmartds.in.R.id.recharge);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            itemView = LayoutInflater.from(parent.getContext()).inflate(msmartds.in.R.layout.recycler_service_item, parent, false);
            final Animation myAnim = AnimationUtils.loadAnimation(context, msmartds.in.R.anim.btn_bubble);
            itemView.startAnimation(myAnim);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            BusinessViewItem ni = balance.get(position);

            holder.balance1.setText("\u20B9 " + ni.getTransAmount());
            holder.recharge.setText(ni.getServiceName());

            holder.setIsRecyclable(false);

        }

        @Override
        public int getItemCount() {
            return balance.size();
        }
    }

    //===================================previous=============================
    public class RecyclerAdaptorPrevious extends RecyclerView.Adapter<RecyclerAdaptorPrevious.MyViewHolder> {

        private Context contextData;
        ArrayList<BusinessViewItem> balance;
        List<BusinessViewItem> recharge;
        private View itemView;

        private RecyclerAdaptorPrevious(Context context, ArrayList<BusinessViewItem> balance/* ArrayList<BusinessItem> recharge*/) {
            this.contextData = context;
            if (balance != null) {
                this.balance = balance;
            } else {
                Toast.makeText(context, "Data is not available !!", Toast.LENGTH_SHORT).show();
            }

        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView balance1;
            private TextView recharge;

            MyViewHolder(View itemView) {
                super(itemView);
                balance1 = (TextView) itemView.findViewById(msmartds.in.R.id.money);
                recharge = (TextView) itemView.findViewById(msmartds.in.R.id.recharge);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(msmartds.in.R.layout.recycler_service_item, parent, false);

            final Animation myAnim = AnimationUtils.loadAnimation(context, msmartds.in.R.anim.btn_bubble);
            itemView.startAnimation(myAnim);
            return new MyViewHolder(itemView);

        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            BusinessViewItem ni = balance.get(position);
            holder.balance1.setText("\u20B9 " + ni.getTransAmount());
            holder.recharge.setText(ni.getServiceName());

            holder.setIsRecyclable(false);

        }

        @Override
        public int getItemCount() {
            return balance.size();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {

        return super.onSupportNavigateUp();
    }


}
