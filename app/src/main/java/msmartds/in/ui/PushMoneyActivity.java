package msmartds.in.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainResponse2;
import  msmartds.in.network.model.agent.AgentRequest;
import  msmartds.in.network.model.agent.AgentResponse;
import  msmartds.in.network.model.pushMoney.PushMoneyRequest;
import  msmartds.in.ui.home.DashBoardActivity;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.Keys;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import retrofit2.Call;
import retrofit2.Callback;

public class PushMoneyActivity extends BaseActivity {
    private String distributorID, key;
    private Button btnPushMoney;
    private TextView tviewAgentId, tviewAgentName, tviewAgencyName, tviewAgentBalance;
    private Spinner sp_push;
    private EditText editAmount, editRemark;
    private String dataAgentID, dataFirmName, dataBalance;
    private String Amount = "", Remark = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( msmartds.in.R.layout.push_money_activity);

        Toolbar toolbar = (Toolbar) findViewById( msmartds.in.R.id.toolbar);
        toolbar.setTitle("Push Balance");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        distributorID = Util.getData(getApplicationContext(), Keys.DS_ID);
        key = Util.getData(getApplicationContext(), Keys.TXN_KEY);

        dataAgentID = getIntent().getStringExtra("AgentID");
        dataFirmName = getIntent().getStringExtra("FirmName");
        dataBalance = getIntent().getStringExtra("Balance");

        System.out.println("intent_data-->" + dataAgentID + ":" + dataFirmName + ":" + dataBalance);


        btnPushMoney = (Button) findViewById( msmartds.in.R.id.txtsubmit);
        tviewAgentId = (TextView) findViewById( msmartds.in.R.id.tv_agent_id);
        tviewAgentName = (TextView) findViewById( msmartds.in.R.id.tv_agent_name);
        tviewAgencyName = (TextView) findViewById( msmartds.in.R.id.tv_agency_name);
        tviewAgentBalance = (TextView) findViewById( msmartds.in.R.id.tv_avail_balance);
        editAmount = (EditText) findViewById( msmartds.in.R.id.edit_amount);
        editRemark = (EditText) findViewById( msmartds.in.R.id.edit_remark);
        sp_push = findViewById(R.id.sp_push);
        tviewAgentId.setText(dataAgentID);
        tviewAgencyName.setText(dataFirmName);
        tviewAgentBalance.setText(dataBalance);

        viewAgentDetails();

        btnPushMoney.setOnClickListener(v -> {
            Amount = editAmount.getText().toString().trim();
            Remark = editRemark.getText().toString().trim();
            if (Amount.isEmpty()) {
                editAmount.requestFocus();
                L.toast(PushMoneyActivity.this, "Please Enter Amount");
            } else {
                pushBalance();
            }
        });
    }

    private void pushBalance() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Push Balance...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            PushMoneyRequest request = new PushMoneyRequest();
            request.setAgentId(dataAgentID);
            request.setAction(sp_push.getSelectedItem().toString().equalsIgnoreCase("Push Balance") ? "Credit" : "Debit");
            request.setTransaferAmount(Amount);
            request.setPaymentRemark(Remark == null ? "" : Remark);

            RetrofitClient.getClient(getApplicationContext())
                    .pushBalance(request)
                    .enqueue(new Callback<MainResponse2>() {
                        @Override
                        public void onResponse(Call<MainResponse2> call, retrofit2.Response<MainResponse2> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    MainResponse2 res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {

                                        showConfirmationDialog(res.getMessage(), true);
                                    } else {
                                        showConfirmationDialog(res.getMessage(), false);
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
                                        tviewAgentName.setText(res.getData().getAgencyName());
                                    } else {
                                        //showConfirmationDialog(res.getMessage());
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

    public void showConfirmationDialog(String msg, final boolean isSuccess) {
        final Dialog d = Util.getDialog(PushMoneyActivity.this, R.layout.push_payment_confirmation_dialog);

        final Button btnSubmit = (Button) d.findViewById( msmartds.in.R.id.btn_push_submit);
        final Button btnClosed = (Button) d.findViewById( msmartds.in.R.id.close_push_button);
        final TextView tvConfirmation = (TextView) d.findViewById( msmartds.in.R.id.tv_confirmation_dialog);

        tvConfirmation.setText(msg);
        btnSubmit.setOnClickListener(v -> {
            if (isSuccess) {
                Intent intent = new Intent(PushMoneyActivity.this, DashBoardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                PushMoneyActivity.this.finish();
                d.dismiss();
            } else {
                d.dismiss();
            }
            d.cancel();
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            d.cancel();
        });

        d.show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
