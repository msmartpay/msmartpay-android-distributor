package msmartds.in;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Smartkinda on 6/14/2017.
 */

public class AgentsStatusWiseListActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String AgentWiseStatus, agentListData;
    private ListView agentList;
    private EditText et_search;
    private ArrayList<AgentModel> agentLists;
    private AgentModel agentModel;
    private JSONObject jsonObject1;
    private JSONArray jsonArray;
    private Context context;
    private AgentAdaptorClass adaptorClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.agents_status_wise_list_activity);

        context = AgentsStatusWiseListActivity.this;
        sharedPreferences = getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        AgentWiseStatus = sharedPreferences.getString("spinner_data", null);
        agentListData = getIntent().getStringExtra("agentList");

        Log.d("agentList---3>", getIntent().getStringExtra("agentList"));

        agentList = (ListView) findViewById(msmartds.in.R.id.listview);

        Toolbar toolbar = (Toolbar) findViewById(msmartds.in.R.id.toolbar);
        //    toolbar.setTitle("Agents List");
        if (AgentWiseStatus.equalsIgnoreCase("Active Agents")) {
            toolbar.setTitle("ACTIVE AGENTS");
        } else if (AgentWiseStatus.equalsIgnoreCase("De-Active Agents")) {
            toolbar.setTitle("DE-ACTIVE AGENTS");
        } else {
            toolbar.setTitle("ALL AGENTS");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            jsonObject1 = new JSONObject(agentListData.toString());
            jsonArray = jsonObject1.getJSONArray("data");
            JSONObject jsonObject;
            agentLists = new ArrayList<>();
            Log.d("Array lenght ", jsonArray.length() + "");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                agentModel = new AgentModel();

                agentModel.setAgencyName(jsonObject.getString("agencyName"));
                agentModel.setAgentEmailId(jsonObject.getString("agentEmailId"));
                agentModel.setAgentId(jsonObject.getString("AgentId"));
                agentModel.setAmount(jsonObject.getString("amount"));
                agentModel.setMobileNo(jsonObject.getString("mobileNo"));
                agentModel.setStatus(jsonObject.getString("status"));
                agentLists.add(agentModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("  .  ", agentLists.size() + "");
        adaptorClass = new AgentAdaptorClass(context, agentLists, getIntent().getStringExtra("agentList"));
        agentList.setAdapter(adaptorClass);


        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adaptorClass.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
}

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
