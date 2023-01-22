package msmartds.in.ui.balRequest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.R;
import  msmartds.in.network.model.balanceRequest.BalHistoryData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.MyViewHolder> {
    private List<BalHistoryData> balance;

    BalanceAdapter(List<BalHistoryData> balance) {
        this.balance = balance;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reqDate;
        TextView appDate;
        TextView status;
        TextView mode;
        TextView request_id;
        TextView amount;
        TextView remark;


        MyViewHolder(View v) {
            super(v);
            reqDate = (TextView) v.findViewById( msmartds.in.R.id.cstmdate);
            mode = (TextView) v.findViewById( msmartds.in.R.id.cstmmode);
            remark = (TextView) v.findViewById( msmartds.in.R.id.remark);
            request_id = (TextView) v.findViewById( msmartds.in.R.id.request_id);
            amount = (TextView) v.findViewById( msmartds.in.R.id.cstmamount);
            status = (TextView) v.findViewById( msmartds.in.R.id.cstmstatus);
        }

        public void onBind(BalHistoryData ni){
            status.setText(ni.getStatus());
            mode.setText("Mode : "+ni.getMod());
            remark.setText("Remark : "+ni.getRemark());
            reqDate.setText("Date : "+ni.getReqDate());
            request_id.setText(ni.getReqId());
            amount.setText("Rs. "+ni.getAmount());
        }
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_history_adapter, parent, false);
        final Animation myAnim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.btn_bubble);
        itemView.startAnimation(myAnim);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.onBind(balance.get(position));
        holder.setIsRecyclable(false);

    }

    @Override
    public int getItemCount() {
        return balance.size();
    }
}