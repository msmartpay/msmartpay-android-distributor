package msmartds.in.balRequest;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;
import msmartds.in.DashBoardActivity;
import msmartds.in.R;
import msmartds.in.URL.BaseFragment;
import msmartds.in.URL.HttpURL;
import msmartds.in.collectBanks.CollectBankModel;
import msmartds.in.utility.L;
import msmartds.in.utility.Mysingleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceRequestFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout icic_qr_code, upi_qr_code;
    private Communication comm;
    private MaterialSpinner bank_details, neftdetails;
    private EditText damount, b_refid, remarks, fromDateEtxt;
    private Button brequest;
    private ProgressDialog pd;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private String refId, bankName, amount, ddate;
    private String selectedType;
    private String BalanceRequestUrl = HttpURL.BalanceRequest;
    private String agentID, distributorId, txnKey = "", mdID;
    private SharedPreferences sharedPreferences;
    private ArrayList<CollectBankModel> bankList = null;
    private CollectBankModel bankDetailsItem;
    private TextInputLayout til_id_balance_request_refId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        comm = (Communication) context;

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bal_request_fragment, container, false);

        sharedPreferences = getActivity().getSharedPreferences("Details", MODE_PRIVATE);
        distributorId = sharedPreferences.getString("distributorId", null);
        mdID = sharedPreferences.getString("mdID", null);
        txnKey = sharedPreferences.getString("txnKey", null);

        Log.d("Balance--", distributorId + ", " + mdID + ", " + txnKey);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        findViewsById(view);
        setDateTimeField();

        remarks = (EditText) view.findViewById(R.id.id_balance_request_remarks);
        damount = (EditText) view.findViewById(R.id.id_balance_request_amount);
        b_refid = (EditText) view.findViewById(R.id.id_balance_request_refId);
        til_id_balance_request_refId = view.findViewById(R.id.til_id_balance_request_refId);
        bank_details = view.findViewById(R.id.id_balancerequest_bank);
        neftdetails = view.findViewById(R.id.id_request_type);
        icic_qr_code = view.findViewById(R.id.icic_qr_code);
        upi_qr_code = view.findViewById(R.id.upi_qr_code);


        brequest = (Button) view.findViewById(R.id.id_balance_request_submit);
        brequest.setOnClickListener(this);


        ScrollView scrollview = (ScrollView) view.findViewById(R.id.scrollView);
        scrollview.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollview.setFocusable(true);
        scrollview.setFocusableInTouchMode(true);
        scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                v.requestFocusFromTouch();
                return false;
            }
        });
        if (isConnectionAvailable()) {
            getBankDetails();
        } else {
            Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

        neftdetails.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == -1) {
                    selectedType = null;
                    til_id_balance_request_refId.setVisibility(View.GONE);
                } else {
                    selectedType = neftdetails.getSelectedItem().toString();
                    if (selectedType.equalsIgnoreCase("cash")) {
                        til_id_balance_request_refId.setVisibility(View.GONE);
                    } else {
                        til_id_balance_request_refId.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bank_details.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == -1) {
                    bankDetailsItem = null;
                } else {
                    bankDetailsItem = bankList.get(position);
                    if (bankDetailsItem != null && bankDetailsItem.getBank_name().contains("ICICI Bank QR CODE")) {
                        icic_qr_code.setVisibility(View.VISIBLE);
                        //upi_qr_code.setVisibility(View.GONE);
                    }/*else if(bankDetailsItem!=null && bankDetailsItem.getBank_name().contains("BHIM UPI")){
                        upi_qr_code.setVisibility(View.VISIBLE);
                        icic_qr_code.setVisibility(View.GONE);
                    }*/ else {
                        //upi_qr_code.setVisibility(View.GONE);
                        icic_qr_code.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    private void getBankDetails() {
        pd = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        try {
            String url = HttpURL.CollectionBanks;
            JSONObject jsonObjectReq = new JSONObject()
                    .put("txnkey", txnKey)
                    .put("distributorId", distributorId);

            L.m2("Url--1>", url);
            L.m2("Request--1>", jsonObjectReq.toString());

            JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.POST, url, jsonObjectReq,

                    object -> {
                        pd.dismiss();
                        try {
                            if (object != null) {
                                L.m2("Url--1>", url);
                                L.m2("Response--1>", object.toString());

                                if (object.getInt("status") == 0) {
                                    final JSONArray parentArray = (JSONArray) object.get("data");
                                    bankList = new ArrayList<>();
                                    if (parentArray.length() > 0) {
                                        for (int i = 0; i < parentArray.length(); i++) {
                                            JSONObject obj = (JSONObject) parentArray.get(i);

                                            CollectBankModel bankDetailsItem = new CollectBankModel();

                                            bankDetailsItem.setActual_bank_name(obj.get("bank_name") + "");
                                            bankDetailsItem.setBank_name(obj.get("bank_name") + " " + obj.get("bank_account") + " " + obj.get("bnk_ifsc") + "");
                                            bankDetailsItem.setBank_account(obj.get("bank_account") + "");
                                            bankDetailsItem.setBank_account_name(obj.get("bank_account_name") + "");
                                            bankDetailsItem.setBnk_ifsc(obj.get("bnk_ifsc") + "");

                                            bankList.add(bankDetailsItem);

                                        }
                                        ArrayAdapter<CollectBankModel> adapter = new ArrayAdapter<CollectBankModel>(getActivity(), R.layout.spinner_textview_layout, bankList);
                                        adapter.setDropDownViewResource(R.layout.spinner_textview_layout);
                                        bank_details.setAdapter(adapter);


                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                pd.dismiss();
                Toast.makeText(getActivity(), "Server Error : " + error.toString(), Toast.LENGTH_SHORT).show();
            });
            getSocketTimeOut(jsonrequest);
            Mysingleton.getInstance(getActivity()).addToRequsetque(jsonrequest);
        } catch (Exception exp) {
            pd.dismiss();
            exp.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_balance_request_submit) {
            if (isConnectionAvailable()) {
                balanceRequest();
            } else {
                Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void balanceRequest() {
        String remarkss = remarks.getText().toString();

        amount = damount.getText().toString();
        ddate = fromDateEtxt.getText().toString();
        refId = b_refid.getText().toString();

        //refId.replaceAll(" ", "_");

        if (ddate.length() <= 0) {
            Toast.makeText(getActivity().getApplicationContext(), "Select Deposit Date. ", Toast.LENGTH_SHORT).show();
        } else if (amount.length() <= 0 && Double.parseDouble(amount) < 100) {
            Toast.makeText(getActivity().getApplicationContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
        } else if ("cash".equalsIgnoreCase(selectedType) && (refId != null && refId.length() <= 0)) {
            Toast.makeText(getActivity(), "Enter Valid Transaction Reference Id", Toast.LENGTH_LONG).show();
        } else {
            pd = new ProgressDialog(getActivity());
            pd = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            try {
                JSONObject jsonObjectData = new JSONObject()
                        .put("distributorId", distributorId)
                        .put("txnkey", txnKey)
                        .put("mdID", mdID)
                        .put("bankName", bankDetailsItem.getActual_bank_name())
                        .put("refId", refId)
                        .put("amount", amount)
                        .put("depositDate", ddate)
                        .put("remark", remarkss)
                        .put("mode", selectedType);

                Log.d("url--BalanceRequest", BalanceRequestUrl);
                Log.d("Req--BalanceRequest>", jsonObjectData.toString());
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, BalanceRequestUrl, jsonObjectData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject data) {

                        Log.d("data", data.toString());
                        try {
                            if ((data.getString("status") != null && data.getString("status").equals("0"))) {
                                pd.dismiss();
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Status")
                                        .setIcon(R.drawable.trnsuccess)
                                        .setMessage(data.get("message") + "")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getActivity(), DashBoardActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        })
                                        .show();

                            } else {
                                pd.dismiss();
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Status")
                                        .setIcon(R.drawable.failed)
                                        .setMessage(data.get("message") + "")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getActivity(), DashBoardActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        })
                                        .show();


                            }
                        } catch (JSONException e) {
                            pd.dismiss();
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                    }
                });
                getSocketTimeOut(objectRequest);
                Mysingleton.getInstance(getActivity()).addToRequsetque(objectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void findViewsById(View v) {
        fromDateEtxt = (EditText) v.findViewById(R.id.id_balance_request_ddate);
        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.requestFocus();

    }

    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == fromDateEtxt) {
                    fromDatePickerDialog.show();
                }
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


    }


    public interface Communication {

    }

}
