package msmartds.in;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import msmartds.in.URL.BaseActivity;
import msmartds.in.bankDetails.BankDetailsActivity;

public class DrawerActivity extends BaseActivity {
    public RelativeLayout drawerpane;
    public DrawerLayout mDrawer;
    private Button btnSubmit, btnClosed;
    private Spinner spinnerAgentType;
    private String[] AgentTypeData = {"Select Option", "All Agents", "Active Agents", "De-Active Agents"};
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String AgentWiseTypeData;
    private EditText editAgentID;
    private TextView tvDistributorName, tvDistributorid;
    private String distributorName, distributor, distributorInitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.activity_drawer_acitvity);
        drawerpane = (RelativeLayout) findViewById(msmartds.in.R.id.nav_view);
        mDrawer = (DrawerLayout) findViewById(msmartds.in.R.id.drawer_layout);

        sharedPreferences = getApplicationContext().getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        distributorName = sharedPreferences.getString("DistributorName", null);
        distributor = sharedPreferences.getString("distributorId", null);
        distributorInitial = sharedPreferences.getString("distributorInitial", null);

        tvDistributorName = (TextView) findViewById(msmartds.in.R.id.drawer_distributor_name);
        tvDistributorid = (TextView) findViewById(msmartds.in.R.id.drawer_distributor_id);

        tvDistributorName.setText(distributorName);
        tvDistributorid.setText(distributorInitial + distributor);

    }

    public void click(View v) {
        if (v.getId() == msmartds.in.R.id.active_agent_drawer) {
            showAgentCustomDialog();
            mDrawer.closeDrawers();
        }
   /*     else if (v.getId() == R.id.deactive_agent_drawer) {
            showAgentCustomDialog();
            Toast.makeText(DrawerActivity.this, "You clicked De-Active Agent", Toast.LENGTH_SHORT).show();
        }
   */
        else if (v.getId() == msmartds.in.R.id.add_agent_drawer) {
            Intent addAgent = new Intent(DrawerActivity.this, AddAgentActivity.class);
            startActivity(addAgent);
            mDrawer.closeDrawers();
        }
 /*       else if (v.getId() == R.id.deposite_drawer) {
            Toast.makeText(DrawerActivity.this, "You clicked Deposite Request", Toast.LENGTH_SHORT).show();
        }
*/
        else if (v.getId() == msmartds.in.R.id.push_money_drawer) {
            showPushBalanceCustomDialog();
            mDrawer.closeDrawers();
        } else if (v.getId() == msmartds.in.R.id.report_drawer) {
            Intent intent = new Intent(DrawerActivity.this, ReportActivity.class);
            startActivity(intent);
            mDrawer.closeDrawers();
        } else if (v.getId() == msmartds.in.R.id.bank_details_drawer) {
            Intent intent = new Intent(DrawerActivity.this, BankDetailsActivity.class);
            startActivity(intent);
            mDrawer.closeDrawers();
        } else if (v.getId() == msmartds.in.R.id.change_pass_drawer) {
            Intent in = new Intent(DrawerActivity.this, ChangePasswordActivity.class);
            startActivity(in);
            mDrawer.closeDrawers();
        } /*else if (v.getId() == msmartpayds.com.R.id.qr_code_drawer) {
            Intent in = new Intent(DrawerActivity.this, QRActivity.class);
            startActivity(in);
            mDrawer.closeDrawers();
        }*/ else if (v.getId() == msmartds.in.R.id.logout) {
            Toast.makeText(this, "Worki", Toast.LENGTH_SHORT).show();

            sharedPreferences = getApplicationContext().getSharedPreferences("Details", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Toast.makeText(this, "Thank you for using " + getString(msmartds.in.R.string.app_name_sapce) + "\n           Please visit again", Toast.LENGTH_LONG).show();
            Intent in = new Intent(this, LoginActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            this.finish();
        }
    }

    public void showAgentCustomDialog() {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(getApplicationContext(), msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        d.setCancelable(false);
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        d.setContentView(msmartds.in.R.layout.view_agent_status);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnSubmit = (Button) d.findViewById(msmartds.in.R.id.btn_submit);
        btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_button);
        spinnerAgentType = (Spinner) d.findViewById(msmartds.in.R.id.spinner);

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
                    Toast.makeText(parent.getContext(), "Selected: " + AgentWiseTypeData, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (AgentWiseTypeData == null) {
                Toast.makeText(DrawerActivity.this, "Please Select Valid Option", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(DrawerActivity.this, AgentsStatusWiseListActivity.class);
                editor.putString("spinner_data", AgentWiseTypeData);
                editor.commit();
                d.dismiss();
                startActivity(intent);
                finish();
            }
        });

        btnClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                d.cancel();
            }
        });

        d.show();

    }

    public void showPushBalanceCustomDialog() {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(DrawerActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        d.setCancelable(false);

        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        d.setContentView(msmartds.in.R.layout.push_balance_dialog);

        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnSubmit = (Button) d.findViewById(msmartds.in.R.id.btn_push_submit);
        btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_push_button);
        editAgentID = (EditText) d.findViewById(msmartds.in.R.id.edit_push_balance);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editAgentID.getText().toString().trim())) {

                    Toast.makeText(DrawerActivity.this, "Please Enter Agent Id", Toast.LENGTH_SHORT).show();
                } else {
                    String AgentID = editAgentID.getText().toString().trim();
                    Toast.makeText(DrawerActivity.this, "Succesfull : " + AgentID, Toast.LENGTH_SHORT).show();
                    d.cancel();
                }
            }
        });


    }
}
