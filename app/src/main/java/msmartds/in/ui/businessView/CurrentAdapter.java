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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CurrentAdapter extends RecyclerView.Adapter<CurrentAdapter.MyViewHolder> {
    private Context context;

    ArrayList<BusinessViewItem> balance;

    public CurrentAdapter(Context context, ArrayList<BusinessViewItem> balance) {
        this.context = context;
        this.balance = balance;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView balance1;
        TextView recharge;

        MyViewHolder(View itemView) {
            super(itemView);
            balance1 = itemView.findViewById( msmartds.in.R.id.money);
            recharge = itemView.findViewById( msmartds.in.R.id.recharge);
        }

        @SuppressLint("SetTextI18n")
        public void onBind(BusinessViewItem ni) {
            balance1.setText("\u20B9 " + ni.getTransAmount());
            recharge.setText(ni.getServiceName());
        }
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate( msmartds.in.R.layout.recycler_service_item, parent, false);
        final Animation myAnim = AnimationUtils.loadAnimation(context,  msmartds.in.R.anim.btn_bubble);
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
   