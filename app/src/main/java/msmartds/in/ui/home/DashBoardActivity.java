package msmartds.in.ui.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainRequest;
import  msmartds.in.network.model.agent.AgentRequest;
import  msmartds.in.network.model.agent.AgentResponse;
import  msmartds.in.network.model.auth.BalanceResponse;
import msmartds.in.ui.agent.PushMoneyActivity;
import  msmartds.in.ui.agent.AddAgentActivity;
import  msmartds.in.ui.agent.AgentsStatusWiseListActivity;
import  msmartds.in.ui.balRequest.BalanceRequest;
import  msmartds.in.ui.businessView.BusinessViewActivity;
import  msmartds.in.ui.report.ReportActivity;
import  msmartds.in.ui.report.agent.AgentReportActivity;
import  msmartds.in.util.Keys;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import retrofit2.Call;
import retrofit2.Callback;

public class DashBoardActivity extends DrawerActivity {

    ImageView navgiation;
    private LinearLayout home_view_agent, btnDeActiveAgent, home_add_agent, btnDepositeRequest, home_push_balance, home_report,agent_report, home_balance_request, home_business;
    private Button btnSubmit, btnClosed;
    private TextView tViewDistributorBal, CopyRightText, PoweredByText;
    private Spinner spinnerAgentType;
    private String[] AgentTypeData = {"Select Option", "All Agents", "Active Agents", "De-Active Agents"};

    private String AgentWiseTypeData;
    private EditText editAgentID;
    private String agentStatus, distributorBal, CopyRight, PoweredBy;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dashboard, null, false);

        mDrawer.addView(view, 0);
        navgiation = (ImageView) findViewById(R.id.openbar);

        navgiation.setOnClickListener(view1 -> mDrawer.openDrawer(drawerpane));


        //All Buttons ID
        home_view_agent =  findViewById(R.id.home_view_agent);
        home_add_agent =  findViewById(R.id.home_add_agent);
        home_push_balance =  findViewById(R.id.home_push_balance);
        home_balance_request =  findViewById(R.id.home_balance_request);
        home_report =  findViewById(R.id.home_report);
        agent_report =  findViewById(R.id.agent_report);
        home_business = findViewById(R.id.home_business);
        CopyRightText =  findViewById(R.id.copy_rights);
        //PoweredByText = findViewById(R.id.powered_by);


        distributorBal = Util.getData(getApplicationContext(), Keys.BALANCE);
        CopyRight = Util.getData(getApplicationContext(), Keys.WHITELABLE_COMPANY_NAME);
        PoweredBy = Util.getData(getApplicationContext(), Keys.DS_POWERED_BY);

        tViewDistributorBal = (TextView) findViewById(R.id.distributor_balance);
        tViewDistributorBal.setText("\u20B9" + distributorBal);
        L.m2("Dashboard","CopyRight : "+CopyRight+", PoweredBy: "+PoweredBy);
        CopyRightText.setText("PoweredBy: "+CopyRight);
        //PoweredByText.setText(PoweredBy);

        home_view_agent.setOnClickListener(v -> showAgentCustomDialog());

        home_add_agent.setOnClickListener(v -> {
            Intent addAgentIntent = new Intent(getApplicationContext(), AddAgentActivity.class);
            startActivity(addAgentIntent);
        });

        home_push_balance.setOnClickListener(v -> showPushBalanceCustomDialog());

        home_report.setOnClickListener(v -> {
            Intent reportIntent = new Intent(getApplicationContext(), ReportActivity.class);
            startActivity(reportIntent);
        });

        agent_report.setOnClickListener(v -> {
            Intent reportIntent = new Intent(getApplicationContext(), AgentReportActivity.class);
            startActivity(reportIntent);
        });

        home_balance_request.setOnClickListener(v -> {
            Intent reportIntent = new Intent(getApplicationContext(), BalanceRequest.class);
            startActivity(reportIntent);
        });

        home_business.setOnClickListener(v -> {
            Intent home_business = new Intent(getApplicationContext(), BusinessViewActivity.class);
            startActivity(home_business);
        });

    }


    public void showAgentCustomDialog() {
        final Dialog d = Util.getDialog(DashBoardActivity.this, R.layout.view_agent_status);
        btnSubmit = (Button) d.findViewById(R.id.btn_submit);
        btnClosed = (Button) d.findViewById(R.id.close_button);
        spinnerAgentType = (Spinner) d.findViewById(R.id.spinner);

        ArrayAdapter agentTypeAdaptor = new ArrayAdapter(this, R.layout.spinner_textview_layout, AgentTypeData);
        agentTypeAdaptor.setDropDownViewResource(R.layout.spinner_textview_layout);
        spinnerAgentType.setAdapter(agentTypeAdaptor);

        spinnerAgentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    AgentWiseTypeData = null;
                } else {
                    i = position;
                    AgentWiseTypeData = parent.getItemAtPosition(position).toString();
//                    L.toast(parent.getContext(), "Selected: " + AgentWiseTypeData);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (AgentWiseTypeData == null) {
                L.toast(DashBoardActivity.this, "Please select valid option !");
            } else {

                if (i == 1) {
                    agentStatus = "All";
                } else if (i == 2) {
                    agentStatus = "Activate";
                } else if (i == 3) {
                    agentStatus = "Deactive";
                }
                agentStatusRequest();
                d.dismiss();
            }
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method status
            d.cancel();
        });

        d.show();

    }

    public void showPushBalanceCustomDialog() {
        // TODO Auto-generated method stub
        @SuppressLint("PrivateResource") final Dialog d = Util.getDialog(DashBoardActivity.this, R.layout.push_balance_dialog);
        btnSubmit = (Button) d.findViewById(R.id.btn_push_submit);
        btnClosed = (Button) d.findViewById(R.id.close_push_button);
        editAgentID = (EditText) d.findViewById(R.id.edit_push_balance);

        btnSubmit.setOnClickListener(v -> {
            final String AgentID = editAgentID.getText().toString().trim();
            if (TextUtils.isEmpty(AgentID)) {
                L.toast(DashBoardActivity.this, "Please enter agent id");
            } else {
                d.cancel();
                if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
                    final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Agent...");
                    ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

                    AgentRequest request = new AgentRequest();
                    request.setParam("singleAgentDetail");
                    request.setAgentId(AgentID);

                    RetrofitClient.getClient(getApplicationContext())
                            .getSingleAndListAgent(request)
                            .enqueue(new Callback<AgentResponse>() {
                                @Override
                                public void onResponse(Call<AgentResponse> call, retrofit2.Response<AgentResponse> response) {
                                    pd.dismiss();
                                    try {
                                        if (response.isSuccessful() && response.body() != null) {
                                            AgentResponse res = response.body();
                                            if ("0".equalsIgnoreCase(res.getStatus()) &&  res.getData()!=null) {
                                                Intent intent = new Intent(getApplicationContext(), PushMoneyActivity.class);
                                                intent.putExtra("AgentID", AgentID);
                                                intent.putExtra("FirmName", res.getData().getAgencyName());
                                                intent.putExtra("Balance",  res.getData().getAmount());
                                                startActivity(intent);
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
                                }

                                @Override
                                public void onFailure(Call<AgentResponse> call, Throwable t) {
                                    pd.dismiss();
                                    L.m2("Parser Error", t.getLocalizedMessage());
                                }
                            });
                }
            }
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            d.cancel();
        });

        d.show();
    }


    private void agentStatusRequest() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Agent Details...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            AgentRequest request = new AgentRequest();
            request.setParam("AgentDetails");
            request.setStatus(agentStatus);

            RetrofitClient.getClient(getApplicationContext())
                    .getSingleAndListAgent(request)
                    .enqueue(new Callback<AgentResponse>() {
                        @Override
                        public void onResponse(Call<AgentResponse> call, retrofit2.Response<AgentResponse> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    AgentResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        Util.saveData(getApplicationContext(), Keys.AGENT_WISE_TYPE_DATA, AgentWiseTypeData);
                                        if (res.getData().getAgentDetails() != null) {
                                            Intent intent = new Intent(DashBoardActivity.this, AgentsStatusWiseListActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("agentList", Util.getStringFromModel(res));
                                            startActivity(intent);
                                        } else {
                                            L.toast(getApplicationContext(), "No Data");
                                        }
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
                        }

                        @Override
                        public void onFailure(Call<AgentResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }

    private void updateDistributerBal() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Balance...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            MainRequest request = new MainRequest();


            RetrofitClient.getClient(getApplicationContext())
                    .getBalance(request)
                    .enqueue(new Callback<BalanceResponse>() {
                        @Override
                        public void onResponse(Call<BalanceResponse> call, retrofit2.Response<BalanceResponse> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    BalanceResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus()) && res.getData()!=null) {
                                        Util.saveData(getApplicationContext(), Keys.BALANCE, "Rs. " + res.getData().getBalance());
                                        tViewDistributorBal.setText("Rs. " + res.getData().getBalance());

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
                        }

                        @Override
                        public void onFailure(Call<BalanceResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDistributerBal();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.warning_message_red)
                    .setTitle("Closing application")
                    .setMessage("Are you sure you want to exit ?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        }


    }
}