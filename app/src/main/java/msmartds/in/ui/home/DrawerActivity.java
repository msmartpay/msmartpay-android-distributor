package msmartds.in.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import  msmartds.in.R;
import  msmartds.in.ui.agent.AddAgentActivity;
import  msmartds.in.ui.agent.AgentsStatusWiseListActivity;
import  msmartds.in.ui.auth.ChangePasswordActivity;
import  msmartds.in.ui.auth.LoginActivity;
import  msmartds.in.ui.bankDetails.BankDetailsActivity;
import  msmartds.in.ui.businessReport.BusinessReportActivity;
import  msmartds.in.ui.collectBanks.CollectBankActivity;
import  msmartds.in.ui.commission.CommissionActivity;
import  msmartds.in.ui.report.ReportActivity;
import  msmartds.in.util.Keys;
import  msmartds.in.util.L;
import  msmartds.in.util.Util;

public class DrawerActivity extends AppCompatActivity {
    public RelativeLayout drawerpane;
    public DrawerLayout mDrawer;
    private Button btnSubmit, btnClosed;
    private Spinner spinnerAgentType;
    private String[] AgentTypeData = {"Select Option", "All Agents", "Active Agents", "De-Active Agents"};
    private String AgentWiseTypeData;
    private EditText editAgentID;
    private TextView tvDistributorName, tvDistributorid;
    private String distributorName, distributor, distributorInitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( msmartds.in.R.layout.activity_drawer_acitvity);
        drawerpane = (RelativeLayout) findViewById( msmartds.in.R.id.nav_view);
        mDrawer = (DrawerLayout) findViewById( msmartds.in.R.id.drawer_layout);

        distributorName = Util.getData(getApplicationContext(), Keys.DS_NAME);
        distributor = Util.getData(getApplicationContext(), Keys.DS_ID);
        distributorInitial = Util.getData(getApplicationContext(), Keys.DS_INITIAL);

        tvDistributorName = (TextView) findViewById( msmartds.in.R.id.drawer_distributor_name);
        tvDistributorid = (TextView) findViewById( msmartds.in.R.id.drawer_distributor_id);

        tvDistributorName.setText(distributorName);
        tvDistributorid.setText(distributorInitial + distributor);

    }

    public void click(View v) {
        if (v.getId() == R.id.active_agent_drawer) {
            showAgentCustomDialog();
            mDrawer.closeDrawers();
        }
   /*     else if (v.getId() == R.id.deactive_agent_drawer) {
            showAgentCustomDialog();
            L.toast(DrawerActivity.this, "You clicked De-Active Agent");
        }
   */
        else if (v.getId() == R.id.add_agent_drawer) {
            Intent addAgent = new Intent(getApplicationContext(), AddAgentActivity.class);
            startActivity(addAgent);
            mDrawer.closeDrawers();
        }
 /*       else if (v.getId() == R.id.deposite_drawer) {
            L.toast(getApplicationContext(), "You clicked Deposite Request");
        }
*/
        else if (v.getId() == R.id.push_money_drawer) {
            showPushBalanceCustomDialog();
            mDrawer.closeDrawers();
        } else if (v.getId() == R.id.collect_bank_drawer) {
            Intent intent = new Intent(getApplicationContext(), CollectBankActivity.class);
            startActivity(intent);
            mDrawer.closeDrawers();
        } else if (v.getId() == R.id.report_drawer) {
            Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
            startActivity(intent);
            mDrawer.closeDrawers();
        } else if (v.getId() == R.id.report_business_drawer) {
            Intent intent = new Intent(getApplicationContext(), BusinessReportActivity.class);
            startActivity(intent);
            mDrawer.closeDrawers();
        } else if (v.getId() == R.id.bank_details_drawer) {
            Intent intent = new Intent(getApplicationContext(), BankDetailsActivity.class);
            startActivity(intent);
            mDrawer.closeDrawers();
        } else if (v.getId() == R.id.commission_drawer) {
            Intent intent = new Intent(getApplicationContext(), CommissionActivity.class);
            startActivity(intent);
            mDrawer.closeDrawers();
        } else if (v.getId() == R.id.change_pass_drawer) {
            Intent in = new Intent(getApplicationContext(), ChangePasswordActivity.class);
            startActivity(in);
            mDrawer.closeDrawers();
        } else if (v.getId() == R.id.logout) {
            //  L.toast(this, "Worki");

            Util.clearPref(getApplicationContext());
           // Util.createSourceFile(getApplicationContext(), "");
            L.toast(this, "Thank you for using " + getString( msmartds.in.R.string.app_name_sapce) + "\n           Please visit again");
            Intent in = new Intent(this, LoginActivity.class);
            startActivity(in);
            this.finish();
        }
    }

    public void showAgentCustomDialog() {
        // TODO Auto-generated method stub
        final Dialog d = Util.getDialog(getApplicationContext(), R.layout.view_agent_status);
        btnSubmit = (Button) d.findViewById( msmartds.in.R.id.btn_submit);
        btnClosed = (Button) d.findViewById( msmartds.in.R.id.close_button);
        spinnerAgentType = (Spinner) d.findViewById( msmartds.in.R.id.spinner);

        ArrayAdapter agentTypeAdaptor = new ArrayAdapter(this, android.R.layout.simple_spinner_item, AgentTypeData);
        agentTypeAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgentType.setAdapter(agentTypeAdaptor);

        spinnerAgentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    AgentWiseTypeData = null;
                } else {
                    AgentWiseTypeData = parent.getItemAtPosition(position).toString();
                    L.toast(parent.getContext(), "Selected: " + AgentWiseTypeData);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (AgentWiseTypeData == null) {
                L.toast(getApplicationContext(), "Please Select Valid Option");
            } else {
                Intent intent = new Intent(getApplicationContext(), AgentsStatusWiseListActivity.class);
                Util.saveData(getApplicationContext(), Keys.AGENT_WISE_TYPE_DATA, AgentWiseTypeData);
                d.dismiss();
                startActivity(intent);
                finish();
            }
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            d.cancel();
        });

        d.show();

    }

    public void showPushBalanceCustomDialog() {
        // TODO Auto-generated method stub
        final Dialog d = Util.getDialog(getApplicationContext(), R.layout.push_balance_dialog);

        btnSubmit = (Button) d.findViewById( msmartds.in.R.id.btn_push_submit);
        btnClosed = (Button) d.findViewById( msmartds.in.R.id.close_push_button);
        editAgentID = (EditText) d.findViewById( msmartds.in.R.id.edit_push_balance);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editAgentID.getText().toString().trim())) {

                    L.toast(getApplicationContext(), "Please Enter Agent Id");
                } else {
                    String AgentID = editAgentID.getText().toString().trim();
                    L.toast(getApplicationContext(), "Succesfull : " + AgentID);
                    d.cancel();
                }
            }
        });


    }
}
