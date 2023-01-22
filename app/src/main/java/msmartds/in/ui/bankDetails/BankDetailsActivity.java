package msmartds.in.ui.bankDetails;


import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainRequest;
import  msmartds.in.network.model.bankDetails.BankDetailsItem;
import  msmartds.in.network.model.bankDetails.BankDetailsResponse;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;


public class BankDetailsActivity extends BaseActivity {

    private Context context;
    ArrayList<BankDetailsItem> bankDetails_list = null;
    RecyclerView recycler_bankdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( msmartds.in.R.layout.bank_details_activity);

        Toolbar toolbar = findViewById( msmartds.in.R.id.toolbar);
        toolbar.setTitle("Bank Details");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        context = BankDetailsActivity.this;

        recycler_bankdetails = findViewById( msmartds.in.R.id.recycler_bankdetails);

        getBankDetails();
    }

    private void getBankDetails() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Banks...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            MainRequest request = new MainRequest();
            RetrofitClient.getClient(getApplicationContext())
                    .getBankDetails(request)
                    .enqueue(new Callback<BankDetailsResponse>() {
                        @Override
                        public void onResponse(Call<BankDetailsResponse> call, retrofit2.Response<BankDetailsResponse> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    BankDetailsResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        if (res.getBankDetails() != null) {
                                            bankDetails_list = (ArrayList<BankDetailsItem>) res.getBankDetails();
                                        } else {
                                            bankDetails_list = new ArrayList<>();
                                        }
                                        BankAdapter adapterCurrent = new BankAdapter(bankDetails_list);
                                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
                                        recycler_bankdetails.setLayoutManager(layoutManager);
                                        recycler_bankdetails.setItemAnimator(new DefaultItemAnimator());
                                        recycler_bankdetails.setAdapter(adapterCurrent);
                                    } else {

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
                        public void onFailure(Call<BankDetailsResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
