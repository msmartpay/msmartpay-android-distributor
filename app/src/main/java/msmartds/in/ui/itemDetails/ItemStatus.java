package msmartds.in.ui.itemDetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import  msmartds.in.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemStatus extends AppCompatActivity {
    TextView ItemName, ItemPrice, ItemDesc, activated;
    private String itemName, itemPrice, itemDesc, status, brandID, key, itemID, indicator;
    Switch onoff;
    Button update;
    ProgressDialog pd;
    private String url = "http://smartkinda.com/FoodBizAPI/resources/FBBrandAPI/ActiveDeactiveItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( msmartds.in.R.layout.activity_item_status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent in = getIntent();
        itemName = in.getStringExtra("name");
        itemPrice = in.getStringExtra("price");
        itemDesc = in.getStringExtra("desc");
        status = in.getStringExtra("status");
        Log.d("status", status);
        brandID = in.getStringExtra("brandid");
        key = in.getStringExtra("key");
        itemID = in.getStringExtra("itemid");
        setTitle(itemName);

        ItemName = (TextView) findViewById(R.id.itemname);
        ItemPrice = (TextView) findViewById(R.id.itemprice);
        ItemDesc = (TextView) findViewById(R.id.itemdesc);
        activated = (TextView) findViewById(R.id.activated);
        onoff = (Switch) findViewById(R.id.switch1);
        update = (Button) findViewById(R.id.updateitemstatus);
        ItemName.setText(itemName);
        ItemPrice.setText(itemPrice);
        ItemDesc.setText(itemDesc);
        activated.setVisibility(View.GONE);
        if (status.equalsIgnoreCase("y")) {
            onoff.setChecked(true);
            indicator = "Y";
            activated.setVisibility(View.VISIBLE);
        } else {
            onoff.setChecked(false);
            indicator = "N";
            activated.setVisibility(View.GONE);

        }

        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    indicator = "Y";
                    Log.d("indicator", indicator);
                    activated.setVisibility(View.VISIBLE);
                } else {
                    indicator = "N";
                    Log.d("indicator", indicator);
                    activated.setVisibility(View.GONE);
                }
            }

        });


        update.setOnClickListener(view -> {
            pd = ProgressDialog.show(ItemStatus.this, "", "Loading. Please wait...", true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            try {

                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                        url, new JSONObject().put("brandId", brandID)
                        .put("Key", key)
                        .put("itemId", itemID)
                        .put("status", indicator), data -> {
                            try {
                                Log.d("url-called", url);
                                Log.d("Url data", data.toString());
                                Log.d("indicator", indicator);
                                pd.dismiss();

                                Toast.makeText(ItemStatus.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                                ItemStatus.this.finish();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }, volleyError -> {
                    pd.dismiss();
                    Toast.makeText(ItemStatus.this, "No Responce from Server", Toast.LENGTH_SHORT).show();
                    ItemStatus.this.finish();
                });
                Volley.newRequestQueue(ItemStatus.this).add(objectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
