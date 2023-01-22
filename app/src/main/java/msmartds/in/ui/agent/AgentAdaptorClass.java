package msmartds.in.ui.agent;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.R;
import  msmartds.in.network.model.agent.AgentResponse;
import  msmartds.in.network.model.agent.AgentSingle;
import  msmartds.in.ui.PushMoneyActivity;
import  msmartds.in.util.Util;

import java.util.ArrayList;

public class AgentAdaptorClass extends RecyclerView.Adapter<AgentAdaptorClass.MyViewHolder> implements Filterable {
    
    private ArrayList<AgentSingle> arrayListData;
    private ArrayList<AgentSingle> arrayListDataOrignal;
    private AgentResponse agentResponse;


   public AgentAdaptorClass(ArrayList<AgentSingle> arrayList, AgentResponse agentResponse) {
        arrayListData = arrayList;
        arrayListDataOrignal = arrayList;
        this.agentResponse = agentResponse;
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.agents_view_status, parent, false);
        final Animation myAnim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.btn_bubble);
        itemView.startAnimation(myAnim);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.onBind(arrayListData.get(position));
    }

    @Override
    public int getItemCount() {
        return  arrayListData.size();
    }
    
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);

    }
    
    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView AgentID, FirmName, btnStatus, Balance,tv_mobile,tv_name;
        private Button btnPush, btnDetail;
        public MyViewHolder(View view){
            super(view);
            AgentID = (TextView) view.findViewById( msmartds.in.R.id.tv_agent_id);
            FirmName = (TextView) view.findViewById( msmartds.in.R.id.tv_firm_name);
            Balance = (TextView) view.findViewById( msmartds.in.R.id.tv_balance);
            btnPush = (Button) view.findViewById( msmartds.in.R.id.btn_push);
            btnDetail = (Button) view.findViewById( msmartds.in.R.id.btn_details);
            btnStatus = (TextView) view.findViewById( msmartds.in.R.id.btn_status);

            tv_mobile = (TextView) view.findViewById( msmartds.in.R.id.tv_mobile);
            tv_name = (TextView) view.findViewById( msmartds.in.R.id.tv_name);


        }

        public void onBind(AgentSingle agentSingle) {
            AgentID.setText(agentSingle.getAgentId());
            FirmName.setText(agentSingle.getAgencyName());
            Balance.setText(agentSingle.getAmount());
            tv_mobile.setText("Mob: "+agentSingle.getMobileNo());
            tv_name.setText(agentSingle.getAgentEmailId());


            if(agentSingle.getStatus().equalsIgnoreCase("Activate")){
                btnStatus.setText(agentSingle.getStatus());
                btnStatus.setTextColor(Color.parseColor("#32CD32"));
            }else{
                btnStatus.setText(agentSingle.getStatus());
                btnStatus.setTextColor(Color.parseColor("#FF0000"));
            }


            btnPush.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), PushMoneyActivity.class);
                intent.putExtra("AgentID",agentSingle.getAgentId());
                intent.putExtra("FirmName",agentSingle.getAgencyName());
                intent.putExtra("Balance",agentSingle.getAmount());
                v.getContext().startActivity(intent);
            });

            btnDetail.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), DetailAgentActivity.class);
                intent.putExtra("AgentID",agentSingle.getAgentId());
                intent.putExtra("status",agentSingle.getStatus());
                intent.putExtra("agentList", Util.getStringFromModel(agentResponse));
                v.getContext().startActivity(intent);
            });
        }
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
                    ArrayList<AgentSingle> filteredList = new ArrayList<>();
                    for (AgentSingle row : arrayListDataOrignal) {

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
                arrayListData = (ArrayList<AgentSingle>) results.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

   
}
