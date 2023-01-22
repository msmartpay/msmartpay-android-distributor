package msmartds.in.ui.report.agent;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import  msmartds.in.R;
import  msmartds.in.network.model.report.agent.AgentReportModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by SSZPL on 5/18/2022.
 */

public class AgentReportAdaptorClass extends ArrayAdapter<AgentReportModel> implements Filterable {

    private Context contextData;
    private ArrayList<AgentReportModel> arrayListData;
    private ArrayList<AgentReportModel> arrayListDataOrignal;
    private TextView agent_id,ac,acc_op, amt, status, txnId, date,tv_current_amt;private ItemFilter mFilter = new ItemFilter();
    private ItemFilter2 mFilter2 = new ItemFilter2();

    AgentReportAdaptorClass(Context context, ArrayList<AgentReportModel> arrayList) {
        super(context, R.layout.agent_report_list_view, arrayList);
        contextData = context;
        arrayListData = arrayList;
        arrayListDataOrignal = arrayListData;
    }

    @Override
    public int getCount() {
        if(arrayListData!=null)
            return arrayListData.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) contextData.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.agent_report_list_view, parent, false);

        agent_id  =view.findViewById(R.id.agent_id);
        ac  =view.findViewById(R.id.ac);
        acc_op  =view.findViewById(R.id.acc_op);
        amt = view.findViewById(R.id.amt);
        status = view.findViewById(R.id.status);
        txnId =  view.findViewById(R.id.txn_id);
        date =  view.findViewById(R.id.date);
        tv_current_amt = view.findViewById(R.id.tv_current_amt);

        AgentReportModel historyModel=arrayListData.get(position);

        agent_id.setText("Agent: "+historyModel.getAgentId());
        ac.setText( historyModel.getService());
        acc_op.setText( historyModel.getMobileNo() +" "+historyModel.getOperator());
        status.setText(historyModel.getStatus());
        tv_current_amt.setText("Bal: \u20B9" + historyModel.getAgentFbal().trim());

        txnId.setText(historyModel.getAgenttranNo());
        date.setText(historyModel.getDot()+"\n"+historyModel.getTot());

        String actionType = (arrayListData.get(position).getAction()).trim();

        int colorGreen = Color.parseColor("#106d02");
        int colorRed = Color.parseColor("#cc0000");
        if (actionType.equalsIgnoreCase("credit")) {
            amt.setText("+ \u20B9 " + historyModel.getReqAmount().trim());
            amt.setTextColor(colorGreen);
        }else if (actionType.equalsIgnoreCase("debit")) {
            amt.setText("- \u20B9 " + historyModel.getReqAmount().trim());
            amt.setTextColor(colorRed);
        }
        return view;
    }

    public Filter getFilter() {
        return mFilter;
    }

    public Filter getFilter2() {
        return mFilter2;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<AgentReportModel> list = arrayListDataOrignal;

            int count = list.size();
            final ArrayList<AgentReportModel> nlist = new ArrayList<>(count);

            for (AgentReportModel model : list) {
                if (filterString.equalsIgnoreCase("in")) {
                    if (model.getAction().equalsIgnoreCase("Credit"))
                        nlist.add(model);
                } else if (filterString.equalsIgnoreCase("out")) {
                    if (model.getAction().equalsIgnoreCase("Debit"))
                        nlist.add(model);
                } else {
                    nlist.add(model);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayListData = (ArrayList<AgentReportModel>) results.values;
            notifyDataSetChanged();
        }

    }

    private class ItemFilter2 extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            List<AgentReportModel> list = arrayListDataOrignal;

            int count = list.size();
            ArrayList<AgentReportModel> nlist = new ArrayList<>(count);

            for (AgentReportModel model : list) {
                String remark=model.getRemark()==null?"":model.getRemark();
                if (remark.toLowerCase().contains(filterString) || model.getService().toLowerCase().contains(filterString)
                || model.agentId.contains(filterString)) {
                    nlist.add(model);
                }
            }
            if (nlist.size() == 0)
                nlist = arrayListDataOrignal;

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayListData = (ArrayList<AgentReportModel>) results.values;
            notifyDataSetChanged();
        }

    }
}
