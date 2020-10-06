package msmartds.in;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Smartkinda on 6/18/2017.
 */

public class AgentAdaptorClass extends ArrayAdapter<AgentModel> {

    private Context contextData;
    private ArrayList<AgentModel> arrayListData;
    private ArrayList<AgentModel> arrayListDataOrignal;
    private TextView AgentID, FirmName, btnStatus, Balance;
    private Button btnPush, btnDetail;
    private String ListData;

    AgentAdaptorClass(Context context, ArrayList<AgentModel> arrayList, String data)
    {
        super(context, msmartds.in.R.layout.agents_view_status,arrayList);
        contextData = context;
        arrayListData = arrayList;
        arrayListDataOrignal = arrayList;
        ListData=data;
    }

    @Override
    public int getCount() {
        return arrayListData.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) contextData.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate (msmartds.in.R.layout.agents_view_status, parent, false);

        AgentID = (TextView) view.findViewById(msmartds.in.R.id.tv_agent_id);
        FirmName = (TextView) view.findViewById(msmartds.in.R.id.tv_firm_name);
        Balance = (TextView) view.findViewById(msmartds.in.R.id.tv_balance);
        btnPush = (Button) view.findViewById(msmartds.in.R.id.btn_push);
        btnDetail = (Button) view.findViewById(msmartds.in.R.id.btn_details);
        btnStatus = (TextView) view.findViewById(msmartds.in.R.id.btn_status);

        AgentID.setText(arrayListData.get(position).getAgentId());
        FirmName.setText(arrayListData.get(position).getAgencyName());
        Balance.setText(arrayListData.get(position).getAmount());


        if(arrayListData.get(position).getStatus().equalsIgnoreCase("Activate")){
            btnStatus.setText(arrayListData.get(position).getStatus());
            btnStatus.setTextColor(Color.parseColor("#32CD32"));
        }else{
                btnStatus.setText(arrayListData.get(position).getStatus());
                btnStatus.setTextColor(Color.parseColor("#FF0000"));
        }


       btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contextData, PushMoneyActivity.class);
                    intent.putExtra("AgentID",arrayListData.get(position).getAgentId());
                    intent.putExtra("FirmName",arrayListData.get(position).getAgencyName());
                    intent.putExtra("Balance",arrayListData.get(position).getAmount());
                contextData.startActivity(intent);
            }
        });

       btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(contextData, DetailAgentActivity.class);
                intent.putExtra("AgentID",arrayListData.get(position).getAgentId());
                intent.putExtra("status",arrayListData.get(position).getStatus());
                intent.putExtra("agentList", ListData);
                contextData.startActivity(intent);
            }
        });

        return view;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    arrayListData = arrayListDataOrignal;
                } else {
                    ArrayList<AgentModel> filteredList = new ArrayList<>();
                    for (AgentModel row : arrayListDataOrignal) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getAgentId().toLowerCase().contains(charString.toLowerCase())
                                || row.getAgencyName().toLowerCase().contains(charString.toLowerCase())
                                || row.getMobileNo().contains(charString)) {
                            filteredList.add(row);
                        }
                    }

                    arrayListData = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = arrayListData;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayListData = (ArrayList<AgentModel>) results.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
}
