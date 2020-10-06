package msmartds.in.itemManagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import msmartds.in.R;
import msmartds.in.URL.BaseActivity;
import msmartds.in.itemDetails.ItemStatus;
import msmartds.in.utility.Mysingleton;


public class ItemManagement extends BaseActivity implements AdapterView.OnItemClickListener {
    private String url = "http://smartkinda.com/FoodBizAPI/resources/FBBrandAPI/GetRestaurantsItemsByBrand";
    ProgressDialog pd;
    String brandId, key;

    ArrayList<ItemDetails> itemsList;
    HashMap<String, String> itemAndID;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.activity_item_management);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        itemsList = new ArrayList<ItemDetails>();
                        itemAndID = new HashMap<>();
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject object = (JSONObject) obj.get(i);
                            itemAndID.put(object.getString("name"), object.getString("itemId"));
                            ItemDetails md = new ItemDetails(object.getString("itemId"), object.getString("name"), object.getString("shortDesc"), object.getString("longDesc"), object.getString("price"), object.getString("discount"), object.getString("dt"), object.getString("status"), object.getString("itemImage"));
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
            getSocketTimeOut(objectRequest);
            Mysingleton.getInstance(getApplicationContext()).addToRequsetque(objectRequest);
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
                        itemsList = new ArrayList<ItemDetails>();
                        itemAndID = new HashMap<>();
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject object = (JSONObject) obj.get(i);
                            itemAndID.put(object.getString("name"), object.getString("itemId"));
                            ItemDetails md = new ItemDetails(object.getString("itemId"), object.getString("name"), object.getString("shortDesc"), object.getString("longDesc"), object.getString("price"), object.getString("discount"), object.getString("dt"), object.getString("status"), object.getString("itemImage"));
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
            getSocketTimeOut(objectRequest);
            Mysingleton.getInstance(getApplicationContext()).addToRequsetque(objectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onRestart();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent in = new Intent(ItemManagement.this, ItemStatus.class);
        String name = itemsList.get(i).name;
        in.putExtra("name", name);
        in.putExtra("price", itemsList.get(i).price);
        in.putExtra("desc", itemsList.get(i).longDescription);
        in.putExtra("status", itemsList.get(i).status);
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
