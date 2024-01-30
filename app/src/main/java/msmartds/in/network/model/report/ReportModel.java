package msmartds.in.network.model.report;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Irfan on 6/18/2017.
 */

public class ReportModel {

    public String TransactionNo;
    public String DateOfTransaction;
    public String TimeOfTransaction;
    public String Service;
    public String TransactionAmount;
    public String charge;
    public String NetTransactionAmount;
    public String ActionOnBalanceAmount;
    public String FinalBalanceAmount;
    public String TransactionStatus;
    public String Remarks;

    @SerializedName("IDNO")
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionNo() {
        return TransactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        TransactionNo = transactionNo;
    }

    public String getDateOfTransaction() {
        return DateOfTransaction;
    }

    public void setDateOfTransaction(String dateOfTransaction) {
        DateOfTransaction = dateOfTransaction;
    }

    public String getTimeOfTransaction() {
        return TimeOfTransaction;
    }

    public void setTimeOfTransaction(String timeOfTransaction) {
        TimeOfTransaction = timeOfTransaction;
    }

    public String getService() {
        return Service;
    }

    public void setService(String service) {
        Service = service;
    }

    public String getTransactionAmount() {
        return TransactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        TransactionAmount = transactionAmount;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getNetTransactionAmount() {
        return NetTransactionAmount;
    }

    public void setNetTransactionAmount(String netTransactionAmount) {
        NetTransactionAmount = netTransactionAmount;
    }

    public String getActionOnBalanceAmount() {
        return ActionOnBalanceAmount;
    }

    public void setActionOnBalanceAmount(String actionOnBalanceAmount) {
        ActionOnBalanceAmount = actionOnBalanceAmount;
    }

    public String getFinalBalanceAmount() {
        return FinalBalanceAmount;
    }

    public void setFinalBalanceAmount(String finalBalanceAmount) {
        FinalBalanceAmount = finalBalanceAmount;
    }

    public String getTransactionStatus() {
        return TransactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        TransactionStatus = transactionStatus;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }



}
