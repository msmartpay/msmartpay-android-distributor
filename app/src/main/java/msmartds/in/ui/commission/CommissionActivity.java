package msmartds.in.ui.commission;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.commission.CommModel;
import  msmartds.in.network.model.commission.CommissionRequest;
import  msmartds.in.network.model.commission.CommissionResponse;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Smartkinda on 6/13/2017.
 */

public class CommissionActivity extends BaseActivity {
    private RecyclerView rv_comm;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commission_activity);
        context = CommissionActivity.this;
        Toolbar toolbar = (findViewById(R.id.toolbar));
        //toolbar.setNavigationIcon(R.drawable.activity_de);
        toolbar.setTitle("My Commission");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv_comm = findViewById(R.id.rv_comm);
        rv_comm.setLayoutManager(new LinearLayoutManager(context));

        getCommission();
    }

    private void getCommission() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Commissions...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            CommissionRequest request = new CommissionRequest();
            request.setService("DMR");

            RetrofitClient.getClient(getApplicationContext())
                    .getCommissions(request)
                    .enqueue(new Callback<CommissionResponse>() {
                        @Override
                        public void onResponse(Call<CommissionResponse> call, retrofit2.Response<CommissionResponse> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    CommissionResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        ArrayList<CommModel> list = new ArrayList<>();
                                        if (res.getMyCommission() != null)
                                            list = (ArrayList<CommModel>) res.getMyCommission();
                                        CommissionAdapter adapter = new CommissionAdapter(context, list);
                                        rv_comm.setAdapter(adapter);
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
                        public void onFailure(Call<CommissionResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}