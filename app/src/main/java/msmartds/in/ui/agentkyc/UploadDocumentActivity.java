package msmartds.in.ui.agentkyc;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import  msmartds.in.R;
import  msmartds.in.network.AppMethods;
import  msmartds.in.network.NetworkConnection;
import  msmartds.in.network.RetrofitClient;
import  msmartds.in.util.BaseActivity;
import  msmartds.in.util.ImageUtils;
import  msmartds.in.util.Keys;
import  msmartds.in.util.L;
import  msmartds.in.util.ProgressDialogFragment;
import  msmartds.in.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadDocumentActivity extends BaseActivity implements View.OnClickListener {
    private ProgressDialogFragment pd;
    private List<String> data = new ArrayList<>();
    private SmartMaterialSpinner sp_type;
    private Button btn_select_doc;
    private static final int CHOOSE_IMAGE_CODE = 2001;
    private String fileName, file, filePath,fileType,agentId="";
    private ImageView iv_file;
    private Button btn_kyc_upload,btn_kyc_status_update;
    private RecyclerView rv_document;
    private  DocumentAdapter adapter;
    DocumentDataResponseContainer container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);
        Toolbar toolbar = (Toolbar) findViewById( msmartds.in.R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.activity_de);
        toolbar.setTitle("Upload Documents");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        sp_type = findViewById(R.id.sp_kyc_document);
        btn_select_doc = findViewById(R.id.btn_kyc_select);
        iv_file = findViewById(R.id.iv_file);
        btn_kyc_upload = findViewById(R.id.btn_kyc_upload);
        btn_kyc_status_update = findViewById(R.id.btn_kyc_status_update);
        rv_document = findViewById(R.id.rv_kyc);
        btn_select_doc.setOnClickListener(this);
        btn_kyc_upload.setOnClickListener(this);

        Intent in=getIntent();
        agentId=in.getStringExtra("AgentID");

        fetchDocumentType();
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fileType = data.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_kyc_status_update.setOnClickListener(v -> {

            activateAgentKYC();
        });

    }



    private void fetchDocumentType(){
        pd = ProgressDialogFragment.newInstance("Loading. Please wait...", "Fetching Documents...");
        ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
        BaseRequest request = new BaseRequest();
        request.setAgentId(agentId);
        request.setTxnkey(Util.getData(getApplicationContext(), Keys.TXN_KEY));
        request.setDistributorId(Util.getData(getApplicationContext(), Keys.DS_ID));

        RetrofitClient.getClient(this)
                .fetchDocumentType(request).enqueue(new Callback<DocumentTypeResponseModel>() {
            @Override
            public void onResponse(Call<DocumentTypeResponseModel> call, Response<DocumentTypeResponseModel> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    DocumentTypeResponseModel res = response.body();
                    if (res.getStatus() != null && res.getStatus().equals("0")) {
                        data = res.getData();
                        sp_type.setItem(data);
                        fetchKycData();

                    }else {
                        L.toast(UploadDocumentActivity.this, res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<DocumentTypeResponseModel> call, Throwable t) {
                pd.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_kyc_select){
            requestMyPermissions();
        }
        if(view.getId()==R.id.btn_kyc_upload){
            if(fileType==null || fileType.isEmpty()){
                L.toast(UploadDocumentActivity.this,"Please select document type");
            } else if(file==null){
                L.toast(UploadDocumentActivity.this,"Please select File");

            }else{
                uploadFile();
            }
        }

    }

    private void requestMyPermissions() {
        L.m2("permissions", "Clicked");
        Dexter.withContext(UploadDocumentActivity.this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            L.m2("permissions", "Granted");
                            showChoosingFile();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            L.m2("permissions", "Denied");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .withErrorListener(dexterError -> L.m2("permissions", "Error" + dexterError.name()))
                .check();
    }


    private void showChoosingFile() {
        //fileName = "" + System.currentTimeMillis();
        //Intent intent = ImageUtils.getPickImageChooserIntent(getApplicationContext(), fileName, true, false);
        //startActivityForResult(Intent.createChooser(intent, "Choose Image"), CHOOSE_IMAGE_CODE);
        ImagePicker.with(UploadDocumentActivity.this)
                .galleryOnly()
                .crop()
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                        1080,
                        1080
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .start(CHOOSE_IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_IMAGE_CODE) {

                runOnUiThread(() -> {

                    Uri uri = data.getData();
                    String filePath= ImageUtils.getRealPathFromURI(UploadDocumentActivity.this,uri);

                        file = filePath;
                        iv_file.setImageURI(uri);
                    iv_file.setVisibility(View.VISIBLE);

                });
            }
        }
    }

    private void uploadFile(){
        if (NetworkConnection.isConnectionAvailable(getApplicationContext())) {
            pd = ProgressDialogFragment.newInstance("Loading. Please wait...", "Uploading File...");
            ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
            List<MultipartBody.Part> partList = new ArrayList<>();
            partList.add(prepareFilePart("file", file));
            RetrofitClient.getClient(getApplicationContext()).kycUploadFile(partList).enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    pd.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        FileUploadResponse res =  response.body();
                        fileName = res.getFileName();
                        filePath = AppMethods.KYC_BASE_URL+res.getDownloadUri();
                        sendDocumentData();

                    }else{
                        L.toast(getApplicationContext(), "Please Try Again");
                    }
                    iv_file.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                   pd.dismiss();
                    L.toast(getApplicationContext(), t.getMessage());
                }
            });
        }
    }

    private void sendDocumentData(){
        pd = ProgressDialogFragment.newInstance("Loading. Please wait...", "Uploading File...");
        ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
        DocumentDataRequestContainer request = new DocumentDataRequestContainer();
        request.setAgentId(agentId);
        request.setDistributorId(Util.getData(this, Keys.DS_ID));
        request.setTxnkey(Util.getData(this,Keys.TXN_KEY));
        DocumentData data = new DocumentData();
        request.setData(data);
        data.setDocumentType(fileType);
        data.setFilePath(filePath);
        RetrofitClient.getClient(this).kycUploadDoc(request).enqueue(new Callback<DocumentDataResponseContainer>() {
            @Override
            public void onResponse(Call<DocumentDataResponseContainer> call, Response<DocumentDataResponseContainer> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    DocumentDataResponseContainer res = response.body();
                    if(res.getStatus()!=null && res.getStatus().equals("0")){
                        if(res.getDocuments()!=null)
                        adapter.refreshAdapter(res.getDocuments());
                    }else{
                        L.toast(UploadDocumentActivity.this,res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<DocumentDataResponseContainer> call, Throwable t) {
                pd.dismiss();
                L.toast(UploadDocumentActivity.this,"Please Try Again");
            }
        });

    }

    private void activateAgentKYC(){
        pd = ProgressDialogFragment.newInstance("Loading. Please wait...", "Fetching Documents...");
        ProgressDialogFragment.showDialog(pd, getSupportFragmentManager());
        BaseRequest request = new BaseRequest();
        request.setAgentId(agentId);
        request.setTxnkey(Util.getData(getApplicationContext(), Keys.TXN_KEY));
        request.setDistributorId(Util.getData(getApplicationContext(), Keys.DS_ID));
        request.setKycStatus(1);

        RetrofitClient.getClient(this)
                .updateAgentKycStatus(request).enqueue(new Callback<DocumentTypeResponseModel>() {
            @Override
            public void onResponse(Call<DocumentTypeResponseModel> call, Response<DocumentTypeResponseModel> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    DocumentTypeResponseModel res = response.body();
                    showConfirmationDialog(res.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DocumentTypeResponseModel> call, Throwable t) {
                pd.dismiss();
            }
        });
    }

    private void fetchKycData(){
        BaseRequest request = new BaseRequest();
        request.setAgentId(agentId);
        request.setDistributorId(Util.getData(this, Keys.DS_ID));
        request.setTxnkey(Util.getData(this,Keys.TXN_KEY));
        RetrofitClient.getClient(this).fetchDocumentData(request).enqueue(new Callback<DocumentDataResponseContainer>() {
            @Override
            public void onResponse(Call<DocumentDataResponseContainer> call, Response<DocumentDataResponseContainer> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    DocumentDataResponseContainer res = response.body();

                    if(res.getStatus()!=null && res.getStatus().equals("0")){
                        if(res.getDocuments()!=null) {
                            adapter = new DocumentAdapter(res.getDocuments(),UploadDocumentActivity.this);
                            rv_document.setAdapter(adapter);
                            rv_document.setLayoutManager(new LinearLayoutManager(UploadDocumentActivity.this));
                        }
                        L.toast(UploadDocumentActivity.this,res.getMessage());
                    }else{
                        L.toast(UploadDocumentActivity.this,res.getMessage());
                    }

                }
            }


            @Override
            public void onFailure(Call<DocumentDataResponseContainer> call, Throwable t) {
                L.toast(UploadDocumentActivity.this,t.getMessage());
            }
        });
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, String filePath) {

        File file = new File(filePath);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/*"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showConfirmationDialog(String msg) {
        // TODO Auto-generated method stub
        final Dialog d = Util.getDialog(UploadDocumentActivity.this, R.layout.push_payment_confirmation_dialog);

        final Button btnSubmit = (Button) d.findViewById(R.id.btn_push_submit);
        final TextView tvTitle = (TextView) d.findViewById(R.id.title);
        final Button btnClosed = (Button) d.findViewById(R.id.close_push_button);
        final TextView tvConfirmation = (TextView) d.findViewById(R.id.tv_confirmation_dialog);

        tvConfirmation.setText(msg);
        tvTitle.setText("Confirmation");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
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

}