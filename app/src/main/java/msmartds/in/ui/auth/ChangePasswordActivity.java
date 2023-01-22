package msmartds.in.ui.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import  msmartds.in.R;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainResponse2;
import  msmartds.in.network.model.auth.ChangePasswordRequest;
import  msmartds.in.ui.home.DashBoardActivity;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Smartkinda on 6/14/2017.
 */

public class ChangePasswordActivity extends BaseActivity {

    private EditText editOldPassword, editNewPassword, editConfirmPassword;
    private String oldPassword, newPassword, confirmPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Change Password");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        editOldPassword = (EditText) findViewById(R.id.old_pass_edit);
        editNewPassword = (EditText) findViewById(R.id.new_pass_edit);
        editConfirmPassword = (EditText) findViewById(R.id.confirm_pass_edit);

        btnChangePassword = (Button) findViewById(R.id.submit_change_password);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oldPassword = editOldPassword.getText().toString().trim();
                newPassword = editNewPassword.getText().toString().trim();
                confirmPassword = editConfirmPassword.getText().toString().trim();

                if (oldPassword.length() <= 0) {
                    editOldPassword.requestFocus();
                    L.toast(ChangePasswordActivity.this, "Enter Old Password");
                } else if (newPassword.length() <= 0) {
                    editNewPassword.requestFocus();
                    L.toast(ChangePasswordActivity.this, "Enter New Password");
                } else if (confirmPassword.length() <= 0) {
                    editConfirmPassword.requestFocus();
                    L.toast(ChangePasswordActivity.this, "Enter Confirm Password");
                } else {
                    changePasswordRequest();

                }
            }
        });
    }

    //JSON for ChangePassword
    private void changePasswordRequest() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Changing Password...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword(oldPassword);
            request.setNewPassword(newPassword);
            request.setClientType("TEMP");
            request.setParam("changePassword");

            RetrofitClient.getClient(getApplicationContext())
                    .changePassword(request)
                    .enqueue(new Callback<MainResponse2>() {
                        @Override
                        public void onResponse(Call<MainResponse2> call, retrofit2.Response<MainResponse2> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    MainResponse2 res = response.body();
                                    if ("0".equalsIgnoreCase(res.getStatus())) {
                                        showConfirmationDialog(res.getMessage(), true);
                                    } else {
                                        showConfirmationDialog(res.getMessage(), false);
                                    }
                                } else {
                                    L.toast(getApplicationContext(), "No Server Response");
                                }
                            } catch (Exception e) {
                                L.toast(getApplicationContext(), "Parser Error : " + e.getLocalizedMessage());
                                L.m2("Parser Error", e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<MainResponse2> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }
    //===============================

    public void showConfirmationDialog(String msg, final boolean isSuccess) {
        // TODO Auto-generated method stub
 final Dialog d = Util.getDialog(ChangePasswordActivity.this,R.layout.push_payment_confirmation_dialog);

        final Button btnSubmit = (Button) d.findViewById( msmartds.in.R.id.btn_push_submit);
        final TextView tvTitle = (TextView) d.findViewById( msmartds.in.R.id.title);
        final Button btnClosed = (Button) d.findViewById( msmartds.in.R.id.close_push_button);
        final TextView tvConfirmation = (TextView) d.findViewById( msmartds.in.R.id.tv_confirmation_dialog);

        tvConfirmation.setText(msg);
        tvTitle.setText("Confirmation");

        btnSubmit.setOnClickListener(v -> {

            if (isSuccess) {
                Intent intent = new Intent(ChangePasswordActivity.this, DashBoardActivity.class);
                startActivity(intent);
                ChangePasswordActivity.this.finish();
                d.dismiss();
            } else {
                d.dismiss();
            }

            d.cancel();
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            d.cancel();
        });
        d.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
