package msmartds.in.ui.businessView;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.business.BusinessViewItem;
import  msmartds.in.network.model.business.BusinessViewRequest;
import  msmartds.in.network.model.business.BusinessViewResponse;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class BusinessViewActivity extends BaseActivity {

    ArrayList<BusinessViewItem> currentMonth = null;

    ArrayList<BusinessViewItem> previousMonth = null;


    RecyclerView recycler_viewRechage;
    RecyclerView recycler_viewWallet;


    private TextView currTextView, prevTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( msmartds.in.R.layout.business_view_activity);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById( msmartds.in.R.id.toolbar);

        toolbar.setTitle("Business View");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currTextView = findViewById(R.id.currTextView);
        prevTextView = findViewById(R.id.prevTextView);

        /* RecyclerView */
        recycler_viewRechage = (RecyclerView) findViewById( msmartds.in.R.id.recycler_viewRechage);
        /* RecyclerView*/
        recycler_viewWallet = (RecyclerView) findViewById( msmartds.in.R.id.recycler_viewWallet);


        getViewBusiness();


    }

    public void getViewBusiness() {

        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching Business...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());

            BusinessViewRequest request = new BusinessViewRequest();


            RetrofitClient.getClient(getApplicationContext())
                    .getAllServiceBusinessDone(request)
                    .enqueue(new Callback<BusinessViewResponse>() {
                        @Override
                        public void onResponse(Call<BusinessViewResponse> call, retrofit2.Response<BusinessViewResponse> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    BusinessViewResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        if (res.getDistributorBuisinessDetails() != null)
                                            for (BusinessViewItem model : res.getDistributorBuisinessDetails()) {
                                                if ("CurrentMonth".equalsIgnoreCase(model.getMonthType())) {
                                                    if ("CurrentMonth".equalsIgnoreCase(model.getMonthType())) {
                                                        currTextView.setVisibility(View.GONE);
                                                        currentMonth = new ArrayList<>();
                                                        currentMonth.add(model);
                                                    } else if ("".equalsIgnoreCase(model.getMonthType())) {
                                                        currTextView.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                                if ("PreviousMonth".equalsIgnoreCase(model.getMonthType())) {
                                                    if ("PreviousMonth".equalsIgnoreCase(model.getMonthType())) {
                                                        previousMonth = new ArrayList<>();
                                                        prevTextView.setVisibility(View.GONE);
                                                        previousMonth.add(model);
                                                    } else if ("".equalsIgnoreCase(model.getMonthType())) {
                                                        prevTextView.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }
                                    } else {
                                        L.toast(getApplicationContext(), res.getMessage());
                                    }
                                    if (currentMonth != null) {
                                        currTextView.setVisibility(View.GONE);
                                        CurrentAdapter adapterCurrent = new CurrentAdapter(getApplicationContext(), currentMonth);
                                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(BusinessViewActivity.this, 2);
                                        recycler_viewRechage.setLayoutManager(layoutManager);
                                        recycler_viewRechage.setItemAnimator(new DefaultItemAnimator());
                                        recycler_viewRechage.setAdapter(adapterCurrent);
                                    } else if (currentMonth == null) {
                                        currTextView.setVisibility(View.VISIBLE);
                                    }

                                    if (previousMonth != null) {
                                        prevTextView.setVisibility(View.GONE);
                                        PreviousAdaptor adaptorPrevious1 = new PreviousAdaptor(getApplicationContext(), previousMonth);
                                        RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(BusinessViewActivity.this, 2);
                                        recycler_viewWallet.setLayoutManager(layoutManager1);
                                        recycler_viewWallet.setItemAnimator(new DefaultItemAnimator());
                                        recycler_viewWallet.setAdapter(adaptorPrevious1);
                                    } else if (previousMonth == null) {
                                        prevTextView.setVisibility(View.VISIBLE);
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
                        public void onFailure(Call<BusinessViewResponse> call, Throwable t) {
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
