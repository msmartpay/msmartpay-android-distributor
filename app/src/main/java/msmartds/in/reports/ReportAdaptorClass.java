package msmartds.in.reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import msmartds.in.R;

/**
 * Created by Smartkinda on 6/18/2017.
 */

public class ReportAdaptorClass extends ArrayAdapter<ReportsModel> implements Filterable {

    private Context contextData;
    private ArrayList<ReportsModel> arrayListData;
    private ArrayList<ReportsModel> arrayListData2;
    private TextView SrNo, Date, Particulars, TxnAmount, Charges, NetAmount, Action, CurrentBal, TxnStatus, Remark;
    private ItemFilter mFilter = new ItemFilter();

    ReportAdaptorClass(Context context, ArrayList<ReportsModel> arrayList)
    {
        super(context, R.layout.report_list_view,arrayList);
        contextData = context;
        arrayListData = arrayList;
        arrayListData2=arrayListData;
    }

    @Override
    public int getCount() {
        return arrayListData.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);

    }

    //@SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) contextData.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate (R.layout.report_list_view, parent, false);

        SrNo = (TextView) view.findViewById(R.id.tview_srno);
        Date = (TextView) view.findViewById(R.id.tview_date);
        Particulars = (TextView) view.findViewById(R.id.tview_particuler);
        //TxnAmount = (TextView) view.findViewById(R.id.tview_txn_amount);
        Charges = (TextView) view.findViewById(R.id.tview_charges);
        NetAmount = (TextView) view.findViewById(R.id.tview_net_amount);
        Action = (TextView) view.findViewById(R.id.tview_action);
        CurrentBal = (TextView) view.findViewById(R.id.tview_current_bal);
        TxnStatus = (TextView) view.findViewById(R.id.tview_txn_status);
        Remark = (TextView) view.findViewById(R.id.tview_remark);

        SrNo.setText(arrayListData.get(position).getTransactionNo());
        Date.setText(arrayListData.get(position).getDateOfTransaction()+" "+arrayListData.get(position).getTimeOfTransaction());
        //Particulars.setText(arrayListData.get(position).Particulars);
        Particulars.setText("Transaction Amount");
        //TxnAmount.setText(arrayListData.get(position).TxnAmount);
        Charges.setText("\u20B9 " + arrayListData.get(position).getCharge());
       // NetAmount.setText(arrayListData.get(position).NetAmount);
        Action.setText(arrayListData.get(position).getActionOnBalanceAmount());
        CurrentBal.setText("\u20B9 "+arrayListData.get(position).getFinalBalanceAmount());
        TxnStatus.setText(arrayListData.get(position).getTransactionStatus());
        Remark.setText(arrayListData.get(position).getRemarks());


        String actionType = (arrayListData.get(position).getActionOnBalanceAmount()).trim();

        int colorGreen = Color.parseColor("#106d02");
        int colorRed = Color.parseColor("#cc0000");
        if (actionType.equals("credit")) {
            NetAmount.setText("+ \u20B9 " + arrayListData.get(position).getNetTransactionAmount());
            NetAmount.setTextColor(colorGreen);
        }
        if (actionType.equals("debit")) {
            NetAmount.setText("- \u20B9 " + arrayListData.get(position).getNetTransactionAmount());
            NetAmount.setTextColor(colorRed);
        }


        return view;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<ReportsModel> list = arrayListData2;

            int count = list.size();
            final ArrayList<ReportsModel> nlist = new ArrayList<>(count);

            for (ReportsModel model:list) {
                if(filterString.equalsIgnoreCase("in")){
                    if(model.getActionOnBalanceAmount().equalsIgnoreCase("Credit"))
                        nlist.add(model);
                }else if(filterString.equalsIgnoreCase("out")){
                    if(model.getActionOnBalanceAmount().equalsIgnoreCase("Debit"))
                        nlist.add(model);
                }else {
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
            arrayListData = (ArrayList<ReportsModel>) results.values;
            notifyDataSetChanged();
        }

    }
}
