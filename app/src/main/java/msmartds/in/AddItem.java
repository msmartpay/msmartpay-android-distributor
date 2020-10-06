package msmartds.in;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

import msmartds.in.URL.BaseActivity;
import msmartds.in.utility.Mysingleton;

public class AddItem extends BaseActivity {
    Spinner spinner, customlist;

    TextView itemsdesc, itemldesc, itemprice, itemname, itemdiscount, itemdeltime;
    Button additem;
    LinearLayout namingList;
    ArrayAdapter adapter, adapter2;
    private String key = "", brandID;
    private String allCategoriesUrl = "http://smartkinda.com/FoodBizAPI/resources/FBBrandAPI/GetAllCategory";
    private String allCategoriesByList = "http://smartkinda.com/FoodBizAPI/resources/FBBrandAPI/GetAllItemByCategory";
    private ProgressDialog pd;
    private ArrayList allCategories, customCategories;
    private HashMap<String, String> itemIds;
    private String addItemUrl = "http://smartkinda.com/FoodBizAPI/resources/FBBrandAPI/AddItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.activity_add_item);
        setTitle("Add Items");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences myPrefs = AddItem.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        key = myPrefs.getString("key", null);
        brandID = myPrefs.getString("brandID", null);
        spinner = (Spinner) findViewById(msmartds.in.R.id.catagoryitemlist);
        namingList = (LinearLayout) findViewById(msmartds.in.R.id.naminglist);
        namingList.setVisibility(View.GONE);
        customlist = (Spinner) findViewById(msmartds.in.R.id.customlist);
        customlist.setVisibility(View.GONE);
// adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, data);

        additem = (Button) findViewById(msmartds.in.R.id.additembutton);
        itemname = (TextView) findViewById(msmartds.in.R.id.itemname);

        itemdiscount = (TextView) findViewById(msmartds.in.R.id.itemdiscount);
        itemdeltime = (TextView) findViewById(msmartds.in.R.id.itemdeltime);
        itemsdesc = (TextView) findViewById(msmartds.in.R.id.itemsdesc);
        itemldesc = (TextView) findViewById(msmartds.in.R.id.itemldesc);
        itemprice = (TextView) findViewById(msmartds.in.R.id.itemprice);
        itemIds = new HashMap<String, String>();
        pd = ProgressDialog.show(this, "", "Loading.Please wait...", true, false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        try {
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, allCategoriesUrl, new JSONObject().put("Key", key).put("brandId", brandID), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject responce) {
                    try {
                        pd.dismiss();
                        Log.d("url", allCategoriesUrl);
                        Log.d("url-responce", responce.toString());

                        if (responce.getString("message").equalsIgnoreCase("Success")) {
                            allCategories = new ArrayList();
                            JSONArray parentArray = responce.getJSONArray("Category");
                            for (int i = 0; i < parentArray.length(); i++) {
                                JSONObject object = parentArray.getJSONObject(i);
                                allCategories.add(object.getString("categoryName") + "");
                                itemIds.put(object.getString("categoryName") + "", object.getString("categoryId") + "");
                            }

                            adapter = new ArrayAdapter(AddItem.this, android.R.layout.simple_spinner_item, allCategories);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        } else {
                            Toast.makeText(AddItem.this, "Some Error Occur..!! Please Try Later", Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(AddItem.this, "Some Error Occur..!! Please Try Later", Toast.LENGTH_SHORT).show();
                }
            });
            getSocketTimeOut(objectRequest);
            Mysingleton.getInstance(getApplicationContext()).addToRequsetque(objectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spinner.getItemAtPosition(position).toString().equalsIgnoreCase("other")) {
                    itemname.setVisibility(View.VISIBLE);
                } else {
                    itemname.setVisibility(View.GONE);
                    pd = ProgressDialog.show(AddItem.this, "", "Loading.Please wait...", true, false);
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                    try {
                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, allCategoriesByList, new JSONObject().put("Key", key).put("brandId", brandID).put("CategoryId", itemIds.get(spinner.getItemAtPosition(position).toString())), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    pd.dismiss();
                                    Log.d("url", allCategoriesByList);
                                    Log.d("url-responce", response.toString());
                                    if (response.getString("message").equalsIgnoreCase("Success")) {

                                        customCategories = new ArrayList();
                                        JSONArray parentArray = response.getJSONArray("items");

                                        if (parentArray.length() <= 0) {
                                            customlist.setVisibility(View.GONE);
                                        } else {
                                            for (int i = 0; i < parentArray.length(); i++) {
                                                JSONObject object = parentArray.getJSONObject(i);

                                                customCategories.add(object.getString("name") + "");
                                                itemIds.put(object.getString("name") + "", object.getString("itemId") + "");
                                            }
                                            customlist.setVisibility(View.VISIBLE);
                                            adapter2 = new ArrayAdapter(AddItem.this, android.R.layout.simple_spinner_item, customCategories);
                                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            customlist.setAdapter(adapter2);
                                            namingList.setVisibility(View.VISIBLE);

                                        }


                                    } else {
                                        Toast.makeText(AddItem.this, "Some Error Occur..!! Please Try Later", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(AddItem.this, "Some Error Occur..!! Please Try Later", Toast.LENGTH_SHORT).show();
                            }
                        });
                        getSocketTimeOut(objectRequest);
                        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(objectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = ProgressDialog.show(AddItem.this, "", "Loading.Please wait...", true, false);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                try {

                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, addItemUrl, new JSONObject().put("Key", key).put("brandId", brandID).put("categoryName", spinner.getSelectedItem().toString()).put("categoryDesc",customlist.getSelectedItem().toString()).put("itemName", itemname.getText().toString()+"").put("shortDesc", itemsdesc.getText().toString())
                            .put("longDesc", itemldesc.getText().toString()).put("price", itemprice.getText().toString()).put("discount", itemdiscount.getText().toString()).put("deliveryTime", itemdeltime.getText().toString()), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                pd.dismiss();
                                Log.d("url", addItemUrl);
                                Log.d("url-responce", response.toString());
                                Toast.makeText(AddItem.this, response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            Toast.makeText(AddItem.this, "Some Error Occur..!! Please Try Later", Toast.LENGTH_SHORT).show();
                        }
                    });
                    getSocketTimeOut(objectRequest);
                    Mysingleton.getInstance(getApplicationContext()).addToRequsetque(objectRequest);



                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }
}
