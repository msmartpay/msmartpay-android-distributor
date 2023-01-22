package msmartds.in.ui.url;

/**
 * Created by Smartkinda on 6/3/2019.
 */

public interface HttpURL {
     String BaseURL = "http://iflynpay.com/DSMRA1.0/resources/";

     String LoginURL = BaseURL + "DSLogin/Login";
     //String LoginURL = BaseURL + "SKDMR/Login";
     String ForgetPassURL = BaseURL + "DSLogin/ForgetPass";
     String ChangePassword = BaseURL + "DSLogin/ChangePass";
     String ViewAgentURL = BaseURL + "DSCommonService/Viewagent";
     String PushMoneyURL = BaseURL + "DSCommonService/PushBalance";
     String DetailAgentURL = BaseURL + "DSCommonService/Viewagent";
     String UpdateAgentURL = BaseURL + "DSCommonService/UpdateAgent";
     String RegisterAgentURL = BaseURL + "DSCommonService/AgentRegister";
     String DistrictByStateURL = BaseURL + "DSCommonService/DistrictByState";
     String StateURL = BaseURL + "DSCommonService/SelectState";
     String ActiveDeactiveURL = BaseURL + "DSCommonService/ActiveDeactiveAgent";
     String DSDistributor = BaseURL + "DSCommonService/GetBalance";
     String SummaryReport = BaseURL + "DSCommonService/AccountStatement";
     String SummaryReportBusiness = BaseURL + "DSCommonService/BusinessReport";
     String BalanceRequest = BaseURL + "DSCommonService/WalletBalRequest";
     String BalanceRequestHistory = BaseURL + "DSCommonService/WalletBalReqDetails";
     String BusinessViewUrl = BaseURL + "DSCommonService/GetAllServiceBuisinessDone";
     String BankDetails = BaseURL + "DSCommonService/BankDetails";
     String AccountStatementByDate = BaseURL + "DSCommonService/AccountStatementByDate";

     String CollectBankDetails = BaseURL+"DSCommonService/CollectBankDetails";
     String MY_COMMISSION = BaseURL+"DSCommonService/MyCommission";

     int MY_SOCKET_TIMEOUT_MS     = 10*60*1000;
}
