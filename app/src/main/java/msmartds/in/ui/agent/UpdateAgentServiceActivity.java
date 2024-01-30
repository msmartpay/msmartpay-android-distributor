package msmartds.in.ui.agent;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import msmartds.in.R;
import msmartds.in.network.NetworkConnection;
import msmartds.in.network.RetrofitClient;
import msmartds.in.network.model.MainResponse2;
import msmartds.in.network.model.agent.AgentRequest;
import msmartds.in.network.model.agent.AgentResponse;
import msmartds.in.network.model.agent.UpdateAgentServiceRequest;
import msmartds.in.network.model.pushMoney.UpdateAgentAutoCreditRequest;
import msmartds.in.ui.home.DashBoardActivity;
import msmartds.in.util.BaseActivity;
import msmartds.in.util.Keys;
import msmartds.in.util.L;
import msmartds.in.util.ProgressDialogFragment;
import msmartds.in.util.Util;
import retrofit2.Call;
import retrofit2.Callback;

public class UpdateAgentServiceActivity extends BaseActivity {
    private String distributorID, key,numericId;
    private Button btnSubmit;
    private TextView tviewAgentId, tviewAgentName, tviewAgencyName;
    private String dataAgentID, dataFirmName, dataBalance,serviceName,serviceStatus;
    private Spinner sp_service_name, sp_service_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.update_agent_service_activity);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar);
        toolbar.setTitle("Update Agent Service");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        distributorID = Util.getData(getApplicationContext(), Keys.DS_ID);
        key = Util.getData(getApplicationContext(), Keys.TXN_KEY);

        dataAgentID = getIntent().getStringExtra("AgentID");
        dataFirmName = getIntent().getStringExtra("FirmName");
        dataBalance = getIntent().getStringExtra("Balance");

        System.out.println("intent_data-->" + dataAgentID + ":" + dataFirmName + ":" + dataBalance);


        btnSubmit = (Button) findViewById( R.id.txtsubmit);
        tviewAgentId = (TextView) findViewById( R.id.tv_agent_id);
        tviewAgentName = (TextView) findViewById( R.id.tv_agent_name);
        tviewAgencyName = (TextView) findViewById( R.id.tv_agency_name);

        sp_service_name = findViewById(R.id.sp_service_name);
        sp_service_status = findViewById(R.id.sp_service_status);


        tviewAgentId.setText(dataAgentID);
        tviewAgencyName.setText(dataFirmName);

        viewAgentDetails();

        btnSubmit.setOnClickListener(v -> {
            serviceName = sp_service_name.getSelectedItem().toString();
            serviceStatus = sp_service_status.getSelectedItem().toString();
            updateAgentServiceStatus();
        });
    }

    private void updateAgentServiceStatus() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Push Balance...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            UpdateAgentServiceRequest request = new UpdateAgentServiceRequest();
            request.setAgentId(numericId);
            request.setServiceName(sp_service_name.getSelectedItem().toString());
            request.setStatus(sp_service_status.getSelectedItem().toString());

            RetrofitClient.getClient(getApplicationContext())
                    .updateAgentService(request)
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
                                        numericId = res.getData().getAgentId();
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
        final Dialog d = Util.getDialog(UpdateAgentServiceActivity.this, R.layout.push_payment_confirmation_dialog);

        final Button btnSubmit = (Button) d.findViewById( R.id.btn_push_submit);
        final Button btnClosed = (Button) d.findViewById( R.id.close_push_button);
        final TextView tvConfirmation = (TextView) d.findViewById( R.id.tv_confirmation_dialog);

        tvConfirmation.setText(msg);
        btnSubmit.setOnClickListener(v -> {
            if (isSuccess) {

                UpdateAgentServiceActivity.this.finish();
                d.dismiss();
            } else {
                d.dismiss();
            }
            d.cancel();
        });

        btnClosed.setOnClickListener(v -> {
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
