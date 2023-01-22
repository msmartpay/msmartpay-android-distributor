package msmartds.in.ui.balRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainRequest;
import  msmartds.in.network.model.balanceRequest.BalHistoryData;
import  msmartds.in.network.model.balanceRequest.BalHistoryResponse;
import  msmartds.in.util.BaseFragment;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class BalanceHistoryFragemnt extends BaseFragment {

    private RecyclerView rv_list;
    private TextView nodata;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( msmartds.in.R.layout.balance_history_fragment, container, false);

        rv_list = view.findViewById( msmartds.in.R.id.rv_list);
        nodata = (TextView) view.findViewById( msmartds.in.R.id.nodata);
        getBalanceHistory();
        return view;
    }

    private void getBalanceHistory() {
        if (NetworkConnection.isConnectionAvailable(requireActivity())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Fetching History...");
            ProgressDialogFragment.showDialog(pd, getChildFragmentManager());

            MainRequest request = new MainRequest();

            RetrofitClient.getClient(requireActivity())
                    .getBalanceHistoryWallet(request)
                    .enqueue(new Callback<BalHistoryResponse>() {
                        @Override
                        public void onResponse(Call<BalHistoryResponse> call, retrofit2.Response<BalHistoryResponse> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    BalHistoryResponse res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        Util.showView(rv_list);
                                        Util.hideView(nodata);
                                        List<BalHistoryData> list = new ArrayList<>();
                                        if (res.getData() != null)
                                            list = res.getData();
                                        rv_list.setAdapter(new BalanceAdapter(list));
                                    } else {
                                        Util.hideView(rv_list);
                                        Util.showView(nodata);
                                    }

                                } else {
                                    L.toast(requireActivity(), "No Server Response");
                                }
                            } catch (Exception e) {
                                L.toast(requireActivity(), "Parser Error : " + e.getLocalizedMessage());
                                L.m2("Parser Error", e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<BalHistoryResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }

}
