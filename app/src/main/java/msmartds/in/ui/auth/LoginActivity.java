package msmartds.in.ui.auth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import  msmartds.in.R;
import  msmartds.in.network.AppMethods;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.network.model.MainResponse2;
import  msmartds.in.network.model.auth.ForgotRequest;
import  msmartds.in.network.model.auth.LoginRequest;
import  msmartds.in.network.model.auth.LoginResponse;
import  msmartds.in.network.model.auth.UserData;
import  msmartds.in.ui.home.DashBoardActivity;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.Keys;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends BaseActivity {
    private EditText email, password;
    private String Emailtext, Passwordtext;
    private TextView forgetPassword;
    private Button login, btnSignUp, btnSubmit, btnClosed;
    private EditText editEmailID;
    private Context context;
    private CheckBox checkBox;
    private String loginCredentials = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( msmartds.in.R.layout.activity_login);

        context = LoginActivity.this;

        email = (EditText) findViewById( msmartds.in.R.id.emailid);
        password = (EditText) findViewById( msmartds.in.R.id.password);
        login = (Button) findViewById( msmartds.in.R.id.submit);
        btnSignUp = (Button) findViewById( msmartds.in.R.id.new_user);
        forgetPassword = (TextView) findViewById( msmartds.in.R.id.forget_password);
        checkBox = (CheckBox) findViewById( msmartds.in.R.id.checkBox);

        new MyLoginCredential().execute();


        login.setOnClickListener(view -> {
            Emailtext = email.getText().toString().trim();
            Passwordtext = password.getText().toString().trim();
            if (TextUtils.isEmpty(Passwordtext)) {
                L.toast(getApplicationContext(), "Please Enter Email/Mobile");
            } else if (TextUtils.isEmpty(Passwordtext)) {
                L.toast(getApplicationContext(), "Please Enter Password");
            } else {
                if (checkBox.isChecked()) {
                    loginCredentials = Emailtext + "&" + Passwordtext;
                    Log.v("create remember file ", loginCredentials);
                    Util.createSourceFile(getApplicationContext(), loginCredentials);
                }
                checkRequest();


            }
        });

        forgetPassword.setOnClickListener(v -> showAgentCustomDialog());

        //for Sign up page

        btnSignUp.setOnClickListener(v -> {
            Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(signUpIntent);
            finish();
        });

    }

    private void checkRequest() {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Login...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            LoginRequest request = new LoginRequest();
            request.setUserName(Emailtext);
            request.setPassword(Passwordtext);
            request.setUserType(AppMethods.paartnerType);
            request.setParam("login");
            RetrofitClient.getClient(getApplicationContext())
                    .login(request)
                    .enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                            pd.dismiss();
                            try {
                                if (response.isSuccessful() && response.body() != null && response.body().getData()!=null) {
                                    UserData res = response.body().getData();
                                    if ("0".equalsIgnoreCase(response.body().getStatus())) {
                                        Util.saveData(getApplicationContext(), Keys.DS_ID, res.getDistributorId());
                                        Util.saveData(getApplicationContext(), Keys.DS_NAME, res.getDistributorName());
                                        Util.saveData(getApplicationContext(), Keys.TXN_KEY, res.getTxnKey());
                                        Util.saveData(getApplicationContext(), Keys.DS_MOBILE, res.getMobileNo());
                                        Util.saveData(getApplicationContext(), Keys.DS_CLIENT_ID, res.getClientId());
                                        Util.saveData(getApplicationContext(), Keys.DS_COMPANY_NAME, res.getCompanyName());
                                        Util.saveData(getApplicationContext(), Keys.DS_INITIAL, res.getDistributorInitial());
                                        Util.saveData(getApplicationContext(), Keys.DS_LOGIN_URL, res.getLoginUrl());
                                        Util.saveData(getApplicationContext(), Keys.DS_LOGO_URL, res.getLoginUrl() + "/" + res.getInnerHeaderImage());
                                        Util.saveData(getApplicationContext(), Keys.DS_INNER_HEADER_IMAGE, res.getInnerHeaderImage());
                                        Util.saveData(getApplicationContext(), Keys.DS_POWERED_BY, res.getPoweredBy());
                                        Util.saveData(getApplicationContext(), Keys.DS_PENEL_TYPE, res.getPanelType());
                                        Util.saveData(getApplicationContext(), Keys.DS_DOMAIN_NAME, res.getDomainName());
                                        Util.saveData(getApplicationContext(), Keys.BALANCE, res.getBalance());
                                        Util.saveData(getApplicationContext(), Keys.WHITELABLE_COMPANY_NAME, res.getWhitelableCompanyName());
                                        Util.saveData(getApplicationContext(), Keys.DS_AGENT_LOGIN_URL, res.getAgentLoginUrl());
                                        Util.saveData(getApplicationContext(), Keys.DS_AGENT_CELL_EMAIL_ID, res.getAgentCellEmailId());
                                        Util.saveData(getApplicationContext(), Keys.DS_STATUS, response.body().getStatus());
                                        Util.saveData(getApplicationContext(), Keys.DS_TICKET_MESSAGE, res.getTickerMessage());
                                        Util.saveData(getApplicationContext(), Keys.DS_MESSAGE, response.body().getMessage());
                                        Util.saveData(getApplicationContext(), Keys.DS_MD_ID, res.getMdID());
                                        Util.saveData(getApplicationContext(), Keys.DS_EMAIL, Emailtext);
                                        Util.saveData(getApplicationContext(), Keys.DS_PASSWORD, Passwordtext);
                                        Util.saveData(getApplicationContext(), Keys.DS_LOGIN_CREDENTIALS, loginCredentials);
                                        Intent in = new Intent(LoginActivity.this, DashBoardActivity.class);
                                        startActivity(in);
                                        finish();
                                    } else {
                                        L.toast(getApplicationContext(), response.body().getMessage() + "");
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
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            pd.dismiss();
                            L.m2("Parser Error", t.getLocalizedMessage());
                        }
                    });
        }
    }

//=======================================================

    public void showAgentCustomDialog() {
        final Dialog d = Util.getDialog(LoginActivity.this, R.layout.forget_password_dialog);

        btnSubmit = (Button) d.findViewById( msmartds.in.R.id.txtsubmit);
        btnClosed = (Button) d.findViewById( msmartds.in.R.id.close_button);
        editEmailID = (EditText) d.findViewById( msmartds.in.R.id.emailid);

        btnSubmit.setOnClickListener(v -> {
            String emailID = editEmailID.getText().toString().trim();
            if (TextUtils.isEmpty(emailID)) {
                L.toast(getApplicationContext(), "Enter Registered Mobile/Email");
            } else {
                requestForgetPass(emailID);
                d.dismiss();


            }
        });

        btnClosed.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            d.cancel();
        });

        d.show();

    }

    //=======================
    private void requestForgetPass(String emailID) {
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            final ProgressDialogFragment pd = ProgressDialogFragment.newInstance("", "Forgot Password...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            ForgotRequest request = new ForgotRequest();
            request.setUserName(emailID);
            request.setUserType("SSZ");
            RetrofitClient.getClient(getApplicationContext())
                    .forgot(request)
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

    //Dialog for confirmation
    public void showConfirmationDialog(String msg, final boolean isSuccess) {

        final Dialog d = Util.getDialog(LoginActivity.this, R.layout.push_payment_confirmation_dialog);
        final Button btnSubmit = (Button) d.findViewById( msmartds.in.R.id.btn_push_submit);
        final Button btnClosed = (Button) d.findViewById( msmartds.in.R.id.close_push_button);
        final TextView header = (TextView) d.findViewById( msmartds.in.R.id.title);
        final TextView tvConfirmation = (TextView) d.findViewById( msmartds.in.R.id.tv_confirmation_dialog);

        header.setText("Confirmation");
        tvConfirmation.setText(msg);

        btnSubmit.setOnClickListener(v -> {
            if (isSuccess) {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

    class MyLoginCredential extends AsyncTask<Void, Void, Void> {
        private String loginDetails;

        @Override
        protected Void doInBackground(Void... voids) {
            loginDetails = Util.readSourceFile(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (loginDetails != null && !loginDetails.isEmpty()) {
                String[] cred = loginDetails.split("&");
                if (cred.length >= 2) {
                    String user = cred[0];
                    String pass = cred[1];

                    email.setText(user);
                    password.setText(pass.replaceAll("\\r|\\n", ""));

                    String response = Util.getData(getApplicationContext(), Keys.TXN_KEY);
                    if (response != null && !response.isEmpty()) {
                        Intent loginIntent = new Intent(getApplicationContext(), DashBoardActivity.class);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        L.toast(getApplicationContext(), "Data is not stored in xml");
                    }
                    checkBox.setChecked(true);
                } else {
                    Util.createSourceFile(context, "");
                }
            }
        }
    }
}
