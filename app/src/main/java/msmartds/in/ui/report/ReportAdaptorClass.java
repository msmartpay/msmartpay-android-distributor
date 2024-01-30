package msmartds.in.ui.report;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import  msmartds.in.R;
import  msmartds.in.network.model.report.ReportModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smartkinda on 6/18/2017.
 */

public class ReportAdaptorClass extends ArrayAdapter<ReportModel> implements Filterable {

    private Context contextData;
    private ArrayList<ReportModel> arrayListData;
    private ArrayList<ReportModel> arrayListDataOrignal;
    private TextView SrNo, Date, Particulars, TxnAmount, Charges, NetAmount, Action, CurrentBal, TxnStatus, Remark, tview_txn_service;
    private ItemFilter mFilter = new ItemFilter();
    private ItemFilter2 mFilter2 = new ItemFilter2();
    private Button btn_paid, btn_unpaid;

    ReportAdaptorClass(Context context, ArrayList<ReportModel> arrayList) {
        super(context, R.layout.report_list_view, arrayList);
        contextData = context;
        arrayListData = arrayList;
        arrayListDataOrignal = arrayListData;
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
        view = layoutInflater.inflate(R.layout.report_list_view, parent, false);

        btn_paid = view.findViewById(R.id.btn_paid);
        btn_unpaid = view.findViewById(R.id.btn_unpaid);

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
        tview_txn_service = (TextView) view.findViewById(R.id.tview_txn_service);
        tview_txn_service.setText(arrayListData.get(position).getService());
        SrNo.setText(arrayListData.get(position).getTransactionNo());
        Date.setText(arrayListData.get(position).getDateOfTransaction() + " " + arrayListData.get(position).getTimeOfTransaction());
        //Particulars.setText(arrayListData.get(position).Particulars);
        Particulars.setText("Transaction Amount");
        //TxnAmount.setText(arrayListData.get(position).TxnAmount);
        Charges.setText("\u20B9 " + arrayListData.get(position).getTransactionAmount());
        // NetAmount.setText(arrayListData.get(position).NetAmount);
        Action.setText(arrayListData.get(position).getActionOnBalanceAmount());
        CurrentBal.setText("\u20B9 " + arrayListData.get(position).getFinalBalanceAmount());
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

        btn_paid.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contextData instanceof ReportActivity) {
                    ((ReportActivity) contextData).updateDSTransactionRemark(arrayListData.get(position).getId(),"Paid");
                    notifyDataSetChanged();
                }
            }
        });
        btn_unpaid.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contextData instanceof ReportActivity) {
                    ((ReportActivity) contextData).updateDSTransactionRemark(arrayListData.get(position).getId(),"Due");
                    notifyDataSetChanged();
                }
            }
        });


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

            final List<ReportModel> list = arrayListDataOrignal;

            int count = list.size();
            final ArrayList<ReportModel> nlist = new ArrayList<>(count);

            for (ReportModel model : list) {
                if (filterString.equalsIgnoreCase("in")) {
                    if (model.getActionOnBalanceAmount().equalsIgnoreCase("Credit"))
                        nlist.add(model);
                } else if (filterString.equalsIgnoreCase("out")) {
                    if (model.getActionOnBalanceAmount().equalsIgnoreCase("Debit"))
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
            arrayListData = (ArrayList<ReportModel>) results.values;
            notifyDataSetChanged();
        }

    }

    private class ItemFilter2 extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            List<ReportModel> list = arrayListDataOrignal;

            int count = list.size();
            ArrayList<ReportModel> nlist = new ArrayList<>(count);

            for (ReportModel model : list) {
                if (model.getRemarks().toLowerCase().contains(filterString) || model.getService().toLowerCase().contains(filterString)) {
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
            arrayListData = (ArrayList<ReportModel>) results.values;
            notifyDataSetChanged();
        }

    }
}
