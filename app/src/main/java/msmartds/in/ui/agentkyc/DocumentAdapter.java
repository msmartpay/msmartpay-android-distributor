package msmartds.in.ui.agentkyc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import  msmartds.in.R;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {
    private List<DocumentDataResponse> list;
    private Context context;
    public  class ViewHolder extends RecyclerView.ViewHolder {
             TextView tv_doc_type;
              TextView tv_doc_name;
             TextView tv_doc_status;
             ImageView iv_download;
             int position=0;
        public ViewHolder(View view) {
            super(view);
            tv_doc_name = view.findViewById(R.id.tv_doc_name);
            tv_doc_type = view.findViewById(R.id.tv_doc_type);
            tv_doc_status = view.findViewById(R.id.tv_doc_status);
            iv_download = view.findViewById(R.id.iv_download);
            iv_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentDataResponse res = list.get(position);
                    Uri uri = Uri.parse(res.getDocument_file_name());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                }
            });

            // Define click listener for the ViewHolder's View

        }
    }
    public DocumentAdapter(List<DocumentDataResponse> list,Context context){
        this.list = list;
        this.context = context;
    }
    public void refreshAdapter(List<DocumentDataResponse> list){
        this.list =new ArrayList(list);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kyc_document_item, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.position = position;
        DocumentDataResponse data = list.get(position);
        holder.tv_doc_name.setText(data.getDocument_file_name());
        holder.tv_doc_status.setText(data.getDocument_status());
        holder.tv_doc_type.setText(data.getDocument_type());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
