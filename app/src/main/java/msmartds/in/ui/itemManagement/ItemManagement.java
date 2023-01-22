package msmartds.in.ui.itemManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import  msmartds.in.R;
import  msmartds.in.network.model.item.ItemManagementModel;
import  msmartds.in.ui.itemDetails.ItemStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class ItemManagement extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private String url = "http://smartkinda.com/FoodBizAPI/resources/FBBrandAPI/GetRestaurantsItemsByBrand";
    ProgressDialog pd;
    String brandId, key;

    ArrayList<ItemManagementModel> itemsList;
    HashMap<String, String> itemAndID;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( msmartds.in.R.layout.activity_item_management);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Intent in = getIntent();
        brandId = in.getStringExtra("brandid");
        key = in.getStringExtra("key");
        list = (ListView) findViewById(R.id.itemmenu);
        pd = ProgressDialog.show(ItemManagement.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);


        try {

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject().put("brandId", brandId).put("Key", key), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject data) {
                    try {
                        Log.d("url-called", url);
                        Log.d("Url data", data.toString());
                        pd.dismiss();
                        JSONArray obj = data.getJSONArray("Items");
                        itemsList = new ArrayList<ItemManagementModel>();
                        itemAndID = new HashMap<>();
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject object = (JSONObject) obj.get(i);
                            itemAndID.put(object.getString("name"), object.getString("itemId"));
                            ItemManagementModel md = new ItemManagementModel(
                                    object.getString("itemId"),
                                    object.getString("name"),
                                    object.getString("shortDesc"),
                                    object.getString("longDesc"),
                                    object.getString("price"),
                                    object.getString("discount"),
                                    object.getString("dt"),
                                    object.getString("status"),
                                    object.getString("itemImage"));
                            itemsList.add(md);
                        }
                        list.setAdapter(new ItemAdapter(getApplicationContext(), itemsList));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    pd.dismiss();
                    Toast.makeText(ItemManagement.this, "No Responce from Server", Toast.LENGTH_SHORT).show();
                    ItemManagement.this.finish();
                }
            });
            Volley.newRequestQueue(ItemManagement.this).add(objectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        list.setOnItemClickListener(this);
    }

    @Override
    protected void onRestart() {
        try {

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject().put("brandId", brandId).put("Key", key), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject data) {
                    try {
                        Log.d("url-called", url);
                        Log.d("Url data", data.toString());
                        pd.dismiss();
                        JSONArray obj = data.getJSONArray("Items");
                        itemsList = new ArrayList<ItemManagementModel>();
                        itemAndID = new HashMap<>();
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject object = (JSONObject) obj.get(i);
                            itemAndID.put(object.getString("name"), object.getString("itemId"));
                            ItemManagementModel md = new ItemManagementModel(object.getString("itemId"), object.getString("name"), object.getString("shortDesc"), object.getString("longDesc"), object.getString("price"), object.getString("discount"), object.getString("dt"), object.getString("status"), object.getString("itemImage"));
                            itemsList.add(md);
                        }
                        list.setAdapter(new ItemAdapter(getApplicationContext(), itemsList));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    pd.dismiss();
                    Toast.makeText(ItemManagement.this, "No Responce from Server", Toast.LENGTH_SHORT).show();
                    ItemManagement.this.finish();
                }
            });
            Volley.newRequestQueue(ItemManagement.this).add(objectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onRestart();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent in = new Intent(ItemManagement.this, ItemStatus.class);
        String name = itemsList.get(i).getName();
        in.putExtra("name", name);
        in.putExtra("price", itemsList.get(i).getPrice());
        in.putExtra("desc", itemsList.get(i).getLongDesc());
        in.putExtra("status", itemsList.get(i).getStatus());
        in.putExtra("brandid", brandId);
        in.putExtra("key", key);
        in.putExtra("itemid", itemAndID.get(name).toString());
        startActivity(in);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }
}
