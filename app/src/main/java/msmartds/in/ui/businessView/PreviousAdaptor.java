package msmartds.in.ui.businessView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.network.model.business.BusinessViewItem;
import  msmartds.in.util.L;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PreviousAdaptor extends RecyclerView.Adapter<PreviousAdaptor.MyViewHolder> {

    private Context contextData;
    ArrayList<BusinessViewItem> balance;
    List<BusinessViewItem> recharge;

    public PreviousAdaptor(Context context, ArrayList<BusinessViewItem> balance/* ArrayList<BusinessItem> recharge*/) {
        this.contextData = context;
        if (balance != null) {
            this.balance = balance;
        } else {
            L.toast(context, "Data is not available !!");
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView balance1;
        private TextView recharge;

        MyViewHolder(View itemView) {
            super(itemView);
            balance1 = (TextView) itemView.findViewById( msmartds.in.R.id.money);
            recharge = (TextView) itemView.findViewById( msmartds.in.R.id.recharge);
        }

        public void onBind(BusinessViewItem ni) {
            balance1.setText("\u20B9 " + ni.getTransAmount());
            recharge.setText(ni.getServiceName());
        }
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate( msmartds.in.R.layout.recycler_service_item, parent, false);

        final Animation myAnim = AnimationUtils.loadAnimation(contextData,  msmartds.in.R.anim.btn_bubble);
        itemView.startAnimation(myAnim);
        return new MyViewHolder(itemView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        holder.onBind(balance.get(position));

    }

    @Override
    public int getItemCount() {
        return balance.size();
    }
}
