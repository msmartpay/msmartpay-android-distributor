package msmartds.in;

/**
 * Created by Irfan on 6/18/2017.
 */

public class ReportModel {

    public String SrNo;
    public String Date;
    public String Particulars;
    public String TxnAmount;
    public String Charges;
    public String NetAmount;
    public String Action;
    public String CurrentBal;
    public String TxnStatus;
    public String Remark;

    public ReportModel(String srNo, String date, String particulars, String txnAmount, String charges, String netAmount, String action, String currentBal, String txnStatus, String remark) {
        SrNo = srNo;
        Date = date;
        Particulars = particulars;
        TxnAmount = txnAmount;
        Charges = charges;
        NetAmount = netAmount;
        Action = action;
        CurrentBal = currentBal;
        TxnStatus = txnStatus;
        Remark = remark;
    }


}
