package msmartds.in.ui.bankDetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.R;
import  msmartds.in.network.model.bankDetails.BankDetailsItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.MyViewHolder> {
    private ArrayList<BankDetailsItem> balance;

    BankAdapter(ArrayList<BankDetailsItem> balance) {
        this.balance = balance;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_text_bank, ac_holderName, ac_no, ifsc_code, address;


        MyViewHolder(View itemView) {
            super(itemView);
            tv_text_bank = itemView.findViewById(R.id.tv_text_bank);
            ac_holderName = itemView.findViewById(R.id.ac_holderName);
            ac_no = itemView.findViewById(R.id.ac_no);
            ifsc_code = itemView.findViewById(R.id.ifsc_code);
            address = itemView.findViewById(R.id.address);
        }

        public void onBind(BankDetailsItem ni){
            tv_text_bank.setText(ni.getBANK_NAME());
            ac_holderName.setText(ni.getBANK_HOLDER_NAME());
            ac_no.setText(ni.getACCOUNT_NO());
            ifsc_code.setText(ni.getIFSC_CODE());
            address.setText(ni.getADDRESS());

        }
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_service_item, parent, false);
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