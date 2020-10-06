package msmartds.in;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import msmartds.in.URL.BaseActivity;
import msmartds.in.utility.Mysingleton;

/**
 * Created by Smartkinda on 6/14/2017.
 */

public class SignUpActivity  extends BaseActivity {

    private static final String TAG = "Test";
    private ProgressDialog pd;
    private Dialog dialog_status;
    private EditText firstName, lastName, emailID, mobileNO, companyName;
    private String fNameText, lNameText, emailText, mobileText, companyText;
    private String url = "http://smartkinda.com/SKMRA/resources/SKLogin/Register";
//    private String url = HttpURL.REGISTER_URL;
    private Button btnResister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.sign_up_activity);

        Toolbar toolbar = (Toolbar) findViewById(msmartds.in.R.id.toolbar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName = (EditText) findViewById(msmartds.in.R.id.fname);
        lastName = (EditText) findViewById(msmartds.in.R.id.lname);
        emailID = (EditText) findViewById(msmartds.in.R.id.email);
        mobileNO = (EditText) findViewById(msmartds.in.R.id.mobile);
        companyName = (EditText) findViewById(msmartds.in.R.id.company);
        btnResister = (Button) findViewById(msmartds.in.R.id.ok);

        btnResister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fNameText = firstName.getText().toString().trim();
                lNameText = lastName.getText().toString().trim();
                emailText = emailID.getText().toString().trim();
                mobileText = mobileNO.getText().toString().trim();
                companyText = companyName.getText().toString().trim();

                if(fNameText.length()<=0)
                {
                    Toast.makeText(getApplicationContext(), "Please Enter First Name", Toast.LENGTH_SHORT).show();
                } else if(lNameText.length()<=0){
                    Toast.makeText(getApplicationContext(), "Please Enter Last Name", Toast.LENGTH_SHORT).show();
                } else if(emailText.length()<=0){
                    Toast.makeText(getApplicationContext(), "Please Enter Email ID", Toast.LENGTH_SHORT).show();
                } else if(mobileText.length()<=0){
                    Toast.makeText(getApplicationContext(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else if(companyText.length()<=0){
                    Toast.makeText(getApplicationContext(), "Please Enter Company Name", Toast.LENGTH_SHORT).show();
                } else if (!Service.isValidEmail(emailText)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Email Id", Toast.LENGTH_SHORT).show();
                } else {


                    System.out.println("Volley_fNameText--->" + fNameText);
                    System.out.println("Volley_lNameText--->" + lNameText);
                    System.out.println("Volley_emailText--->" + emailText);
                    System.out.println("Volley_mobileText--->" + mobileText);
                    System.out.println("Volley_companyText--->" + companyText);

                    Toast.makeText(SignUpActivity.this,"All_Data--->"+fNameText+":"+lNameText+":"+emailText+":"+mobileText+":"+companyText, Toast.LENGTH_SHORT).show();

                    pd = new ProgressDialog(SignUpActivity.this);
                    pd = ProgressDialog.show(SignUpActivity.this, "", "Loading. Please wait...", true);
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                    try {
                        JSONObject postParam= new JSONObject();
                        postParam.put("firstname", fNameText);
                        postParam.put("lastname", lNameText);
                        postParam.put("email", emailText);
                        postParam.put("mobileno", mobileText);
                        postParam.put("mobileno", mobileText);

                        //                       postParam.put("dsid", HttpURL.DSID);

                        System.out.println("Volley_allData--->" + postParam.toString());

                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url,postParam
                                , new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject data) {
                                pd.dismiss();

                                try {
                                    Log.d("data received",data.toString());
                                    dialog_status = new Dialog(SignUpActivity.this);
                                    dialog_status.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog_status.setContentView(msmartds.in.R.layout.sk_maindialog);
                                    dialog_status.setCancelable(true);

                                    ImageView statusImage = (ImageView) dialog_status.findViewById(msmartds.in.R.id.statusImage);
                                    if (data.getString("response-code").equals("0")) {
                                        pd.dismiss();
                                        TextView text = (TextView) dialog_status.findViewById(msmartds.in.R.id.TextView01);
                                        text.setText((String) data.get("response-message"));
                                        statusImage.setImageResource(msmartds.in.R.drawable.trnsuccess);
                                        final Button trans_status = (Button) dialog_status.findViewById(msmartds.in.R.id.trans_status_button);
                                        trans_status.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                dialog_status.dismiss();
                                                Intent intent = new Intent();
                                                intent.setClass(SignUpActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                SignUpActivity.this.finish();
                                            }
                                        });
                                    } else if (data.getString("response-code").equals("1")) {
                                        pd.dismiss();
                                        TextView text = (TextView) dialog_status.findViewById(msmartds.in.R.id.TextView01);
                                        text.setText((String) data.get("response-message"));
                                        statusImage.setImageResource(msmartds.in.R.drawable.failed);
                                        final Button trans_status = (Button) dialog_status.findViewById(msmartds.in.R.id.trans_status_button);
                                        trans_status.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                dialog_status.dismiss();
                                                finish();
                                            }
                                        });
                                    } else {
                                        pd.dismiss();
                                        TextView text = (TextView) dialog_status.findViewById(msmartds.in.R.id.TextView01);
                                        text.setText("No Response from server. Please Contact to Support By Quoting Registered Mobile No.");
                                        statusImage.setImageResource(msmartds.in.R.drawable.failed);
                                        final Button trans_status = (Button) dialog_status.findViewById(msmartds.in.R.id.trans_status_button);
                                        trans_status.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                dialog_status.dismiss();
                                                finish();
                                            }
                                        });
                                    }

                                }catch (Exception exp2){
                                    System.out.println("Exception---2>" + exp2);
                                }
                                dialog_status.show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Log.d(TAG,"Error "+ error.getMessage());
                                Toast.makeText(SignUpActivity.this, "Something Error Occur ..!! Please Try Later "+error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        getSocketTimeOut(objectRequest);
                        Mysingleton.getInstance(getApplicationContext()).addToRequsetque(objectRequest);

                    }catch (Exception exp){
                        System.out.println("Exception--->"+exp);
                    }
                }

            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}