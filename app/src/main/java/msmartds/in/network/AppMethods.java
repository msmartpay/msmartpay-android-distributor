package msmartds.in.network;

import  msmartds.in.network.model.MainRequest;
import  msmartds.in.network.model.MainResponse2;
import  msmartds.in.network.model.agent.AgentActiveDeactiveRequest;
import  msmartds.in.network.model.agent.AgentRegisterRequest;
import  msmartds.in.network.model.agent.AgentRequest;
import  msmartds.in.network.model.agent.AgentResponse;
import  msmartds.in.network.model.agent.UpdateAgentRequest;
import  msmartds.in.network.model.auth.BalanceResponse;
import  msmartds.in.network.model.auth.ChangePasswordRequest;
import  msmartds.in.network.model.auth.ForgotRequest;
import  msmartds.in.network.model.auth.LoginRequest;
import  msmartds.in.network.model.auth.LoginResponse;
import  msmartds.in.network.model.balanceRequest.BalHistoryResponse;
import  msmartds.in.network.model.balanceRequest.BalRequest;
import  msmartds.in.network.model.bankCollect.CollectBankResponse;
import  msmartds.in.network.model.bankDetails.BankDetailsResponse;
import  msmartds.in.network.model.business.BusinessReportRequest;
import  msmartds.in.network.model.business.BusinessReportResponse;
import  msmartds.in.network.model.business.BusinessViewRequest;
import  msmartds.in.network.model.business.BusinessViewResponse;
import  msmartds.in.network.model.commission.CommissionRequest;
import  msmartds.in.network.model.commission.CommissionResponse;
import  msmartds.in.network.model.pushMoney.PushMoneyRequest;
import  msmartds.in.network.model.report.ReportRequest;
import  msmartds.in.network.model.report.ReportResponse;
import  msmartds.in.network.model.report.agent.AgentReportRequest;
import  msmartds.in.network.model.report.agent.AgentReportResponse;
import  msmartds.in.network.model.stateDistrict.DistrictRequest;
import  msmartds.in.network.model.stateDistrict.DistrictResponse;
import  msmartds.in.network.model.stateDistrict.StateResponse;
import  msmartds.in.ui.agentkyc.BaseRequest;
import  msmartds.in.ui.agentkyc.DocumentDataRequestContainer;
import  msmartds.in.ui.agentkyc.DocumentDataResponseContainer;
import  msmartds.in.ui.agentkyc.DocumentTypeResponseModel;
import  msmartds.in.ui.agentkyc.FileUploadResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppMethods {

    String paartnerType="MNDPL";
    String Domain = "https://android.msmartpay.in/";
    String BASE_URL = Domain + "DSMRA1.0/resources/";
    String DS_CS = "DSCommonService/";
    String USER = "user/";
    String REPORT = "report/";
    String AGENT = "agent/";

    String KYC_BASE_URL = Domain+"FileServer";

    @POST("DSLogin/Login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("DSLogin/ForgetPass")
    Call<MainResponse2> forgot(@Body ForgotRequest request);

    @POST(USER + "ChangePass")
    Call<MainResponse2> changePassword(@Body ChangePasswordRequest request);

    @POST(USER + "GetBalance")
    Call<BalanceResponse> getBalance(@Body MainRequest request);

    @POST(REPORT + "WalletBalRequest")
    Call<MainResponse2> balanceRequestWallet(@Body BalRequest request);

    @POST(REPORT + "WalletBalReqDetails")
    Call<BalHistoryResponse> getBalanceHistoryWallet(@Body MainRequest request);

    @POST(REPORT + "BankDetails")
    Call<BankDetailsResponse> getBankDetails(@Body MainRequest request);

    @POST(REPORT + "BusinessReport")
    Call<BusinessReportResponse> getBusinessReport(@Body BusinessReportRequest request);

    @POST(REPORT + "GetAllServiceBuisinessDone")
    Call<BusinessViewResponse> getAllServiceBusinessDone(@Body BusinessViewRequest request);

    @POST(REPORT + "CollectBankDetails")
    Call<CollectBankResponse> getCollectBankDetails(@Body MainRequest request);

    @POST(REPORT + "MyCommission")
    Call<CommissionResponse> getCommissions(@Body CommissionRequest request);

    //
    @POST(AGENT + "AgentRegister")
    Call<MainResponse2> agentRegister(@Body AgentRegisterRequest request);

    @POST(AGENT + "ActiveDeactiveAgent")
    Call<MainResponse2> activeDeactiveAgent(@Body AgentActiveDeactiveRequest request);

    //singleAgentDetail or AgentDetails in param key
    @POST(AGENT + "Viewagent")
    Call<AgentResponse> getSingleAndListAgent(@Body AgentRequest request);

    @POST(AGENT + "UpdateAgent")
    Call<MainResponse2> updateAgent(@Body UpdateAgentRequest request);

    @POST(AGENT + "PushBalance")
    Call<MainResponse2> pushBalance(@Body PushMoneyRequest request);


    @POST("util/SelectState")
    Call<StateResponse> getStates(@Body MainRequest request);

    @POST( "util/DistrictByState")
    Call<DistrictResponse> getDistrictsByState(@Body DistrictRequest request);

    @POST(REPORT + "AccountStatement")
    Call<ReportResponse> getReports(@Body ReportRequest request);

    @POST(REPORT + "AccountStatementByDate")
    Call<ReportResponse> getReportByDate(@Body ReportRequest request);


    @POST(REPORT + "AgentReport")
    Call<AgentReportResponse> getAgentReports(@Body AgentReportRequest request);

    @POST(REPORT + "AgentReportByDate")
    Call<AgentReportResponse> getAgentReportsByDate(@Body AgentReportRequest request);

    /****** KYC Documents *********/
    @POST(DS_CS + "fetchKycDocumentType")
    Call<DocumentTypeResponseModel> fetchDocumentType(@Body BaseRequest baseRequest);

    @Multipart
    @POST(KYC_BASE_URL+"/uploadFile")
    Call<FileUploadResponse> kycUploadFile(@Part List<MultipartBody.Part> files);

    @POST(DS_CS + "uploadKycDocument")
    Call<DocumentDataResponseContainer> kycUploadDoc(@Body DocumentDataRequestContainer requestContainer);
    @POST(DS_CS + "fetchKycDocuments")
    Call<DocumentDataResponseContainer> fetchDocumentData(@Body BaseRequest baseRequest);

    @POST(DS_CS + "updateAgentKycStatus")
    Call<DocumentTypeResponseModel> updateAgentKycStatus(@Body BaseRequest baseRequest);

}
