package msmartds.in.ui.collectBanks;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainRequest;
import  msmartds.in.network.model.bankCollect.CollectBankModel;
import  msmartds.in.network.model.bankCollect.CollectBankResponse;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class CollectBankActivity extends BaseActivity {
    private Toolbar toolbar;
    private RecyclerView rv_list_bank;
    private Context context;
    private ArrayList<CollectBankModel> bankList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_collect_activity);
        toolbar = findViewById(R.id.toolbar);
        rv_list_bank = findViewById(R.id.rv_list_bank);
        toolbar.setTitle("Bank Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = CollectBankActivity.this;

        getBankDetails();
    }

    private void getBankDetails() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Banks...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            MainRequest request = new MainRequest();

            RetrofitClient.getClient(getApplicationContext())
                    .getCollectBankDetails(request)
                    .enqueue(new Callback<CollectBankResponse>() {
                        @Override
                        public void onResponse(Call<CollectBankResponse> call, retrofit2.Response<CollectBankResponse> response) {
                            pd.dismiss();
                            if (bankList == null)
                                bankList = new ArrayList<>();
                            else
                                bankList.clear();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    CollectBankResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        if (res.getBank_List() != null)
                                            bankList = (ArrayList<CollectBankModel>) res.getBank_List();

                                        CollectBankAdapter adapterCurrent = new CollectBankAdapter(getApplicationContext(), bankList);
                                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
                                        rv_list_bank.setLayoutManager(layoutManager);
                                        rv_list_bank.setItemAnimator(new DefaultItemAnimator());
                                        rv_list_bank.setAdapter(adapterCurrent);
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
                        public void onFailure(Call<CollectBankResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
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
