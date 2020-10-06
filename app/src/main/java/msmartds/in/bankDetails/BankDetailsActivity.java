package msmartds.in.bankDetails;


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

import androidx.appcompat.widget.Toolbar;
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

import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.utility.Mysingleton;


public class BankDetailsActivity extends BaseActivity {

    private Context context;


    //=====================String instance=========================
    private String txnKey, distributorId;

    //====================Used String instance for url=============
    private String BUSINESS_DETAILS_URL = HttpURL.BankDetails;

    //=======================Shared preference instance============
    SharedPreferences myPrefs;

    //====================Classes instance ========================
    private ProgressDialog pd;

    ArrayList<BankDetailsItem> bankDetails_list = null;

    RecyclerView recycler_bankdetails;


    //=============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.bank_details_activity);


        //===============toolbar code==================================================

      Toolbar toolbar =  findViewById(msmartds.in.R.id.toolbar);

        toolbar.setTitle("Bank Details");
        setSupportActionBar(toolbar);


        //=====================arrow backbuttton press==================================
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        context = BankDetailsActivity.this;
        myPrefs = getApplicationContext().getSharedPreferences("Details", MODE_PRIVATE);
        txnKey = myPrefs.getString("txnKey", "");
        distributorId = myPrefs.getString("distributorId", "");

        recycler_bankdetails = findViewById(msmartds.in.R.id.recycler_bankdetails);

            getBankDetails();


    }

    //===============Methods area==========================================================

    private void getBankDetails() {

        pd = ProgressDialog.show(BankDetailsActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        try {
            JSONObject jsonObjectReq = new JSONObject()
                    .put("txnkey", txnKey)
                    .put("distributorId", distributorId);

            Log.d("summary_Request--1>", jsonObjectReq.toString());

            JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, BUSINESS_DETAILS_URL, jsonObjectReq,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject object) {
                            pd.dismiss();
                            try {
                                if (object.get("status") != null && object.get("status").equals("0")) {
                                    final JSONArray parentArray = (JSONArray) object.get("BankDetails");

                                    if (parentArray.length() > 0) {
                                        for (int i = 0; i < parentArray.length(); i++) {
                                            JSONObject obj = (JSONObject) parentArray.get(i);

                                                BankDetailsItem bankDetailsItem = new BankDetailsItem();

                                                if (bankDetails_list == null)

                                                    bankDetails_list = new ArrayList<>();

                                                bankDetailsItem.setCLIENT_ID(obj.get("CLIENT_ID") + "");
                                                bankDetailsItem.setBANK_NAME(obj.get("BANK_NAME") + "");
                                                bankDetailsItem.setACCOUNT_NO(obj.get("ACCOUNT_NO") + "");
                                                bankDetailsItem.setIFSC_CODE(obj.get("IFSC_CODE") + "");
                                                bankDetailsItem.setBANK_HOLDER_NAME(obj.get("BANK_HOLDER_NAME") + "");
                                                bankDetailsItem.setADDRESS(obj.get("ADDRESS") + "");

                                                    bankDetails_list.add(bankDetailsItem);

                                        }
                                        BankDetailsActivity.RecyclerAdapterBank adapterCurrent = new BankDetailsActivity.RecyclerAdapterBank(getApplicationContext(), bankDetails_list);
                                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
                                        recycler_bankdetails.setLayoutManager(layoutManager);
                                        recycler_bankdetails.setItemAnimator(new DefaultItemAnimator());
                                        recycler_bankdetails.setAdapter(adapterCurrent);


                                    }
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
                    Toast.makeText(BankDetailsActivity.this, "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            getSocketTimeOut(jsonrequest);
            Mysingleton.getInstance(getApplicationContext()).addToRequsetque(jsonrequest);

        } catch (Exception exp) {
            pd.dismiss();
            exp.printStackTrace();
        }

    }


    private class RecyclerAdapterBank extends RecyclerView.Adapter<RecyclerAdapterBank.MyViewHolder> {
        private Context context;

        ArrayList<BankDetailsItem> balance;
        private View itemView;

        RecyclerAdapterBank(Context context, ArrayList<BankDetailsItem> balance) {
            this.context = context;
            this.balance = balance;

        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_text_bank, ac_holderName, ac_no, ifsc_code, address;


            MyViewHolder(View itemView) {
                super(itemView);
                tv_text_bank = itemView.findViewById(msmartds.in.R.id.tv_text_bank);
                ac_holderName = itemView.findViewById(msmartds.in.R.id.ac_holderName);
                ac_no = itemView.findViewById(msmartds.in.R.id.ac_no);
                ifsc_code = itemView.findViewById(msmartds.in.R.id.ifsc_code);
                address = itemView.findViewById(msmartds.in.R.id.address);
            }
        }

        @Override
        public BankDetailsActivity.RecyclerAdapterBank.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            itemView = LayoutInflater.from(parent.getContext()).inflate(msmartds.in.R.layout.bank_service_item, parent, false);
            final Animation myAnim = AnimationUtils.loadAnimation(context, msmartds.in.R.anim.btn_bubble);
            itemView.startAnimation(myAnim);
            return new BankDetailsActivity.RecyclerAdapterBank.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BankDetailsActivity.RecyclerAdapterBank.MyViewHolder holder, int position) {

            BankDetailsItem ni = balance.get(position);

            holder.tv_text_bank.setText(ni.getBANK_NAME());
            holder.ac_holderName.setText(ni.getBANK_HOLDER_NAME());
            holder.ac_no.setText(ni.getACCOUNT_NO());
            holder.ifsc_code.setText(ni.getIFSC_CODE());
            holder.address.setText(ni.getADDRESS());

            holder.setIsRecyclable(false);

        }

        @Override
        public int getItemCount() {
            return balance.size();
        }
    }

    //======================================================================================


    //=====================arrow backbuttton press override method=========================
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
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
