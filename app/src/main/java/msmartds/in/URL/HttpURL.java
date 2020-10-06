package msmartds.in.URL;

/**
 * Created by Smartkinda on 6/12/2017.
 */

public interface HttpURL {


    String BaseURL = "https://msmartpay.in/DSMRA/resources/";

    String LoginURL = BaseURL + "DSLogin/Login";
    // String LoginURL = BaseURL + "SKDMR/Login";
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
    String BalanceRequest = BaseURL + "DSCommonService/WalletBalRequest";
    String BalanceRequestHistory = BaseURL + "DSCommonService/WalletBalReqDetails";
    String BusinessViewUrl = BaseURL + "DSCommonService/GetAllServiceBuisinessDone";
    String BankDetails = BaseURL + "DSCommonService/BankDetails";
    String CollectionBanks = BaseURL + "DSCommonService/CollectionBanks";
    //String BankDetails = BaseURL + "SKDMR/BankDetails";
    String AccountStatementByDate = BaseURL + "DSCommonService/AccountStatementByDate";
    String LOCATION =  BaseURL +"location/save";

    int MY_SOCKET_TIMEOUT_MS = 10 * 60 * 1000;
}
