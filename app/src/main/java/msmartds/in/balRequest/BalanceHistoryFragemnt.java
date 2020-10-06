package msmartds.in.balRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import msmartds.in.URL.BaseFragment;
import msmartds.in.URL.HttpURL;
import msmartds.in.utility.Mysingleton;

import static android.content.Context.MODE_PRIVATE;

public class BalanceHistoryFragemnt extends BaseFragment {

    Communicator2 comm;
    private ListView brlist;
    private String BalanceRequestHistoryUrl = HttpURL.BalanceRequestHistory;
    private HashMap<String, ArrayList> map;
    private ArrayList<String> reqDate;
    private ArrayList<String> appDate;
    private ArrayList<String> remark;
    private ArrayList<String> status;
    private ArrayList<String> mode;
    private ArrayList<String> request_id;
    private ArrayList<String> amount;
    private TextView nodata;
    private SharedPreferences sharedPreferences;
    private String distributorId, txnKey = "";
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        comm = (Communicator2) context;

    }


    public interface Communicator2 {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(msmartds.in.R.layout.balance_history_fragment, container, false);

        context = getActivity();
        sharedPreferences = context.getSharedPreferences("Details", MODE_PRIVATE);
        distributorId = sharedPreferences.getString("distributorId", null);
        txnKey = sharedPreferences.getString("txnKey", null);
        brlist = (ListView) view.findViewById(msmartds.in.R.id.brlist);
        nodata = (TextView) view.findViewById(msmartds.in.R.id.nodata);

        try {

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, BalanceRequestHistoryUrl,
                    new JSONObject()
                            .put("distributorId", distributorId)
                            .put("txnkey", txnKey), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject data) {

                    Log.d("url called", BalanceRequestHistoryUrl);
                    Log.d("Data", data.toString());
                    try {
                        if (data.getString("status").equalsIgnoreCase("0")) {
                            nodata.setVisibility(View.GONE);
                            reqDate = new ArrayList<>();
                            appDate = new ArrayList<>();
                            mode = new ArrayList<>();
                            status = new ArrayList<>();
                            amount = new ArrayList<>();
                            request_id = new ArrayList<>();
                            remark = new ArrayList<>();
                            map = new HashMap<>();

                            JSONArray parentary = data.getJSONArray("data");
                            for (int i = 0; i < parentary.length(); i++) {
                                JSONObject obj = (JSONObject) parentary.get(i);
                                reqDate.add(obj.get("reqDate").toString());
                                appDate.add(obj.get("appDate").toString());
                                mode.add(obj.get("mod").toString());
                                amount.add(obj.get("amount").toString());
                                status.add(obj.get("status").toString());
                                request_id.add(obj.get("ReqId").toString());
                                remark.add(obj.get("remark").toString());
                            }
                            map.put("reqDate", reqDate);
                            map.put("appDate", appDate);
                            map.put("mode", mode);
                            map.put("amount", amount);
                            map.put("status", status);
                            map.put("request_id", request_id);
                            map.put("remark", remark);
                            brlist.setAdapter(new CustomBalanceAdapter(context, map));
                        } else {
                            brlist.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            getSocketTimeOut(objectRequest);
            Mysingleton.getInstance(context).addToRequsetque(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    //History Adaptor
    public class CustomBalanceAdapter extends BaseAdapter {
        Context context;
        HashMap<String, ArrayList> map;
        ArrayList<String> reqDate;
        ArrayList<String> appDate;
        ArrayList<String> status;
        ArrayList<String> mode;
        ArrayList<String> request_id;
        ArrayList<String> amount;
        ArrayList<String> remark;

        public CustomBalanceAdapter(Context context, HashMap<String, ArrayList> map) {

            this.context = context;
            this.reqDate = map.get("reqDate");
            this.status = map.get("status");
            this.mode = map.get("mode");
            this.request_id = map.get("request_id");
            this.appDate = map.get("appDate");
            this.amount = map.get("amount");
            this.remark = map.get("remark");
        }

        @Override
        public int getCount() {
            return reqDate.size();
        }

        @Override
        public Object getItem(int position) {
            return reqDate.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        class ViewHolder {
            TextView reqDate;
            TextView appDate;
            TextView status;
            TextView mode;
            TextView request_id;
            TextView amount;
            TextView remark;

            public ViewHolder(View v) {
                reqDate = (TextView) v.findViewById(msmartds.in.R.id.cstmdate);
                mode = (TextView) v.findViewById(msmartds.in.R.id.cstmmode);
                remark = (TextView) v.findViewById(msmartds.in.R.id.remark);
                request_id = (TextView) v.findViewById(msmartds.in.R.id.request_id);
                amount = (TextView) v.findViewById(msmartds.in.R.id.cstmamount);
                status = (TextView) v.findViewById(msmartds.in.R.id.cstmstatus);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            View row = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(msmartds.in.R.layout.balance_history_adapter, parent, false);
                holder = new ViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            holder.status.setText(status.get(position));
            holder.mode.setText("Mode : "+mode.get(position) + "");
            holder.remark.setText("Remark : "+remark.get(position));
            holder.reqDate.setText("Date : "+reqDate.get(position));
            holder.request_id.setText(request_id.get(position));
            holder.amount.setText("Rs. "+amount.get(position));
            return row;
        }
    }

}
