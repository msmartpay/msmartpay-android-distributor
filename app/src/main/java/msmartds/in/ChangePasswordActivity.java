package msmartds.in;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import msmartds.in.URL.BaseActivity;
import msmartds.in.URL.HttpURL;
import msmartds.in.utility.Mysingleton;

/**
 * Created by Smartkinda on 6/14/2017.
 */

public class ChangePasswordActivity extends BaseActivity {

    private EditText editOldPassword, editNewPassword, editConfirmPassword;
    private String oldPassword, newPassword, confirmPassword;
    private Button btnChangePassword;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String url_change_pass = HttpURL.ChangePassword;
    private ProgressDialog pd;
    private String distributorID, key;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.change_password);

        sharedPreferences = getApplicationContext().getSharedPreferences("Details", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        distributorID = sharedPreferences.getString("distributorId", null);
        key = sharedPreferences.getString("txnKey", null);


        Toolbar toolbar = (Toolbar) findViewById(msmartds.in.R.id.toolbar);
        toolbar.setTitle("Change Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editOldPassword = (EditText) findViewById(msmartds.in.R.id.old_pass_edit);
        editNewPassword = (EditText) findViewById(msmartds.in.R.id.new_pass_edit);
        editConfirmPassword = (EditText) findViewById(msmartds.in.R.id.confirm_pass_edit);

        btnChangePassword = (Button) findViewById(msmartds.in.R.id.submit_change_password);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oldPassword = editOldPassword.getText().toString().trim();
                newPassword = editNewPassword.getText().toString().trim();
                confirmPassword = editConfirmPassword.getText().toString().trim();

                if(oldPassword.length() <=0){
                    editOldPassword.requestFocus();
                    Toast.makeText(ChangePasswordActivity.this, "Enter Old Password", Toast.LENGTH_SHORT).show();
                } else if(newPassword.length() <=0){
                    editNewPassword.requestFocus();
                    Toast.makeText(ChangePasswordActivity.this, "Enter New Password", Toast.LENGTH_SHORT).show();
                }else if(confirmPassword.length() <=0){
                    editConfirmPassword.requestFocus();
                    Toast.makeText(ChangePasswordActivity.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else{
                    try {
                        changePasswordRequest();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //JSON for ChangePassword
    private void changePasswordRequest() throws JSONException {
        pd = ProgressDialog.show(ChangePasswordActivity.this, "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, url_change_pass,
                new JSONObject()
                        .put("distributorId", distributorID)
                        .put("txnkey", key)
                        .put("OldPassword", oldPassword)
                        .put("NewPassword", newPassword)
                        .put("clientType", "TEMP")
                        .put("param", "changePassword"),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        pd.dismiss();
                        jsonObject = new JSONObject();
                        jsonObject=object;
                        System.out.println("Object_changePass---->"+object.toString());
                        try {
                            if (object.getString("status").equalsIgnoreCase("0")) {

                                showConfirmationDialog(object.getString("message").toString());
                            }
                            else {
                                pd.dismiss();
                                showConfirmationDialog(object.getString("message").toString());
                            }
                        } catch (JSONException e) {
                            pd.dismiss();
                            e.printStackTrace();
                        }
                        //  StateAdaptor.notifyDataSetChanged();
                    }
                },new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse (VolleyError error){
                pd.dismiss();
            }
        });
        getSocketTimeOut(jsonrequest);

        Mysingleton.getInstance(ChangePasswordActivity.this).addToRequsetque(jsonrequest);

    }
    //===============================

    public void showConfirmationDialog(String msg) {
        // TODO Auto-generated method stub
        final Dialog d = new Dialog(ChangePasswordActivity.this, msmartds.in.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        d.setCancelable(false);

        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        d.setContentView(msmartds.in.R.layout.push_payment_confirmation_dialog);

        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final Button btnSubmit = (Button) d.findViewById(msmartds.in.R.id.btn_push_submit);
        final TextView tvTitle = (TextView) d.findViewById(msmartds.in.R.id.title);
        final Button btnClosed = (Button) d.findViewById(msmartds.in.R.id.close_push_button);
        final TextView tvConfirmation = (TextView) d.findViewById(msmartds.in.R.id.tv_confirmation_dialog);

        tvConfirmation.setText(msg);
        tvTitle.setText("Confirmation");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(jsonObject.getString("status").equalsIgnoreCase("0")){
                        Intent intent = new Intent(ChangePasswordActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                        ChangePasswordActivity.this.finish();
                        d.dismiss();
                    }else{
                        d.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                d.cancel();
            }
        });

        btnClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                d.cancel();
            }
        });
        d.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
