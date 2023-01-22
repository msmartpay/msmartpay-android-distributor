package msmartds.in.ui.agent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.R;
import  msmartds.in.network.model.agent.AgentResponse;
import  msmartds.in.network.model.agent.AgentSingle;
import  msmartds.in.util.Keys;
import  msmartds.in.util.Util;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Smartkinda on 6/14/2017.
 */

public class AgentsStatusWiseListActivity extends AppCompatActivity {

    private String AgentWiseStatus;
    private RecyclerView rv_list;
    private EditText et_search;
    private ArrayList<AgentSingle> agentLists;
    private AgentAdaptorClass adaptorClass;
    private AgentResponse agentResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( msmartds.in.R.layout.agents_status_wise_list_activity);

        AgentWiseStatus = Util.getData(getApplicationContext(), Keys.AGENT_WISE_TYPE_DATA);

        rv_list = findViewById( msmartds.in.R.id.rv_list);
        Toolbar toolbar = (Toolbar) findViewById( msmartds.in.R.id.toolbar);

        if (AgentWiseStatus.equalsIgnoreCase("Active Agents")) {
            toolbar.setTitle("ACTIVE AGENTS");
        } else if (AgentWiseStatus.equalsIgnoreCase("De-Active Agents")) {
            toolbar.setTitle("DE-ACTIVE AGENTS");
        } else {
            toolbar.setTitle("ALL AGENTS");
        }
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        agentResponse = Util.getGson().fromJson(getIntent().getStringExtra("agentList"), AgentResponse.class);
       if(agentResponse.getData()!=null){
           agentLists = (ArrayList<AgentSingle>) agentResponse.getData().getAgentDetails();
           adaptorClass = new AgentAdaptorClass(agentLists, agentResponse);
           rv_list.setAdapter(adaptorClass);
       }


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
