package msmartds.in.itemManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter {

    Context mcontext;
    ArrayList<ItemDetails> menuDetails;

    public ItemAdapter(Context mcontext, ArrayList<ItemDetails> menuDetails) {
        this.mcontext = mcontext;
        this.menuDetails = menuDetails;
    }


    @Override
    public int getCount() {
        return menuDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return menuDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(msmartds.in.R.layout.menu_list, null);
        } else {
            view = convertView;
        }
        ImageView img = (ImageView) view.findViewById(msmartds.in.R.id.details_image);
//        img.setVisibility(View.GONE);
        String cimage=menuDetails.get(position).itemImage;
        if(cimage.equals("") || null==cimage){
            img.setImageResource(msmartds.in.R.drawable.noimage);
        }
        TextView title = (TextView) view.findViewById(msmartds.in.R.id.textView9);
        TextView desc = (TextView) view.findViewById(msmartds.in.R.id.textView10);
        title.setTextColor(mcontext.getResources().getColor(msmartds.in.R.color.colorPrimary));
        title.setTextSize(16);
        title.setText(menuDetails.get(position).name);
        desc.setTextColor(mcontext.getResources().getColor(msmartds.in.R.color.colorPrimary));
        desc.setText(menuDetails.get(position).longDescription);
        TextView newprice = (TextView) view.findViewById(msmartds.in.R.id.textView11);
        newprice.setVisibility(View.GONE);

//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)title.getLayoutParams();
//        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)desc.getLayoutParams();
//        params.setMargins(0, 0, 0, 0); //substitute parameters for left, top, right, bottom
//        params2.setMargins(0, 0, 0, 0); //substitute parameters for left, top, right, bottom
//        title.setLayoutParams(params);
//        desc.setLayoutParams(params2);
        TextView discount = (TextView) view.findViewById(msmartds.in.R.id.discount);
        discount.setVisibility(View.GONE);

        return view;
    }


}
