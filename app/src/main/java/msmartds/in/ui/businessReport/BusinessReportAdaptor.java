package msmartds.in.ui.businessReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import  msmartds.in.R;
import  msmartds.in.network.model.business.BusinessReportModel;

import java.util.ArrayList;

/**
 * Created by Smartkinda on 6/18/2017.
 */

public class BusinessReportAdaptor extends ArrayAdapter<BusinessReportModel> {

    private Context context;
    private ArrayList<BusinessReportModel> arrayListData;
    TextView tv_closing,tv_comm,tv_transfer,tv_add,tv_opening,tv_date;

    BusinessReportAdaptor(Context context, ArrayList<BusinessReportModel> arrayList)
    {
        super(context,R.layout.report_list_view,arrayList);
        this.context = context;
        arrayListData = arrayList;
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
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate (R.layout.report_bbusiness_item, parent, false);


        BusinessReportModel model= arrayListData.get(position);
        tv_add = view.findViewById(R.id.tv_add);
        tv_closing = view.findViewById(R.id.tv_closing);
        tv_comm = view.findViewById(R.id.tv_comm);
        tv_transfer = view.findViewById(R.id.tv_transfer);
        tv_opening = view.findViewById(R.id.tv_opening);
        tv_date = view.findViewById(R.id.tv_date);

        if(position%2==0){
            view.setBackgroundColor(context.getResources().getColor(R.color.colorLightPrimary));
        }

        tv_add.setText(model.getAdd());
        tv_closing.setText(model.getClosing());
        tv_comm.setText(model.getComm());
        tv_date.setText(model.getDate());
        tv_transfer.setText(model.getTransfer());
        tv_opening.setText(model.getOpening());

        return view;
    }

}
