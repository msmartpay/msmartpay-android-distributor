package msmartds.in.ui.auth;

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

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import  msmartds.in.R;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.L;
import  msmartds.in.util.Util;

import org.json.JSONObject;

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
        setContentView( msmartds.in.R.layout.sign_up_activity);

        Toolbar toolbar = (Toolbar) findViewById( msmartds.in.R.id.toolbar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstName = (EditText) findViewById( msmartds.in.R.id.fname);
        lastName = (EditText) findViewById( msmartds.in.R.id.lname);
        emailID = (EditText) findViewById( msmartds.in.R.id.email);
        mobileNO = (EditText) findViewById( msmartds.in.R.id.mobile);
        companyName = (EditText) findViewById( msmartds.in.R.id.company);
        btnResister = (Button) findViewById( msmartds.in.R.id.ok);

        btnResister.setOnClickListener(v -> {
            fNameText = firstName.getText().toString().trim();
            lNameText = lastName.getText().toString().trim();
            emailText = emailID.getText().toString().trim();
            mobileText = mobileNO.getText().toString().trim();
            companyText = companyName.getText().toString().trim();

            if(fNameText.length()<=0)
            {
                L.toast(getApplicationContext(), "Please Enter First Name");
            } else if(lNameText.length()<=0){
                L.toast(getApplicationContext(), "Please Enter Last Name");
            } else if(emailText.length()<=0){
                L.toast(getApplicationContext(), "Please Enter Email ID");
            } else if(mobileText.length()<=0){
                L.toast(getApplicationContext(), "Please Enter Mobile Number");
            } else if(companyText.length()<=0){
                L.toast(getApplicationContext(), "Please Enter Company Name");
            } else if (!Util.isValidEmail(emailText)) {
                L.toast(getApplicationContext(), "Please Enter Valid Email Id");
            } else {
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
                                dialog_status.setContentView(R.layout.sk_maindialog);
                                dialog_status.setCancelable(true);

                                ImageView statusImage = (ImageView) dialog_status.findViewById(R.id.statusImage);
                                if (data.getString("response-code").equals("0")) {
                                    pd.dismiss();
                                    TextView text = (TextView) dialog_status.findViewById(R.id.TextView01);
                                    text.setText((String) data.get("response-message"));
                                    statusImage.setImageResource(R.drawable.trnsuccess);
                                    final Button trans_status = (Button) dialog_status.findViewById(R.id.trans_status_button);
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
                                    TextView text = (TextView) dialog_status.findViewById(R.id.TextView01);
                                    text.setText((String) data.get("response-message"));
                                    statusImage.setImageResource(R.drawable.failed);
                                    final Button trans_status = (Button) dialog_status.findViewById(R.id.trans_status_button);
                                    trans_status.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            dialog_status.dismiss();
                                            finish();
                                        }
                                    });
                                } else {
                                    pd.dismiss();
                                    TextView text = (TextView) dialog_status.findViewById(R.id.TextView01);
                                    text.setText("No Response from server. Please Contact to Support By Quoting Registered Mobile No.");
                                    statusImage.setImageResource(R.drawable.failed);
                                    final Button trans_status = (Button) dialog_status.findViewById(R.id.trans_status_button);
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
                            L.toast(SignUpActivity.this, "Something Error Occur ..!! Please Try Later "+error.toString());
                        }
                    });
                   // getSocketTimeOut(objectRequest);
                    Volley.newRequestQueue(SignUpActivity.this).add(objectRequest);

                }catch (Exception exp){
                    System.out.println("Exception--->"+exp);
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