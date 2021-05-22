package msmartds.in.reports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportsModel {

@SerializedName("TransactionNo")
@Expose
private String transactionNo;
@SerializedName("DateOfTransaction")
@Expose
private String dateOfTransaction;
@SerializedName("TimeOfTransaction")
@Expose
private String timeOfTransaction;
@SerializedName("Service")
@Expose
private String service;
@SerializedName("TransactionAmount")
@Expose
private double transactionAmount;
@SerializedName("Commission")
@Expose
private double commission;
@SerializedName("NetTransactionAmount")
@Expose
private double netTransactionAmount;
@SerializedName("UpdatedBalanceAmount")
@Expose
private double updatedBalanceAmount;
@SerializedName("TransactionStatus")
@Expose
private String transactionStatus;
@SerializedName("Remarks")
@Expose
private String remarks;
@SerializedName("ActionOnBalanceAmount")
@Expose
private String actionOnBalanceAmount;
@SerializedName("charge")
@Expose
private double charge;
@SerializedName("PreviousBalanceAmount")
@Expose
private double previousBalanceAmount;
@SerializedName("FinalBalanceAmount")
@Expose
private double finalBalanceAmount;
@SerializedName("IDNO")
@Expose
private String idno;

public String getTransactionNo() {
return transactionNo;
}

public void setTransactionNo(String transactionNo) {
this.transactionNo = transactionNo;
}

public String getDateOfTransaction() {
return dateOfTransaction;
}

public void setDateOfTransaction(String dateOfTransaction) {
this.dateOfTransaction = dateOfTransaction;
}

public String getTimeOfTransaction() {
return timeOfTransaction;
}

public void setTimeOfTransaction(String timeOfTransaction) {
this.timeOfTransaction = timeOfTransaction;
}

public String getService() {
return service;
}

public void setService(String service) {
this.service = service;
}

public double getTransactionAmount() {
return transactionAmount;
}

public void setTransactionAmount(double transactionAmount) {
this.transactionAmount = transactionAmount;
}

public double getCommission() {
return commission;
}

public void setCommission(double commission) {
this.commission = commission;
}

public double getNetTransactionAmount() {
return netTransactionAmount;
}

public void setNetTransactionAmount(double netTransactionAmount) {
this.netTransactionAmount = netTransactionAmount;
}

public double getUpdatedBalanceAmount() {
return updatedBalanceAmount;
}

public void setUpdatedBalanceAmount(double updatedBalanceAmount) {
this.updatedBalanceAmount = updatedBalanceAmount;
}

public String getTransactionStatus() {
return transactionStatus;
}

public void setTransactionStatus(String transactionStatus) {
this.transactionStatus = transactionStatus;
}

public String getRemarks() {
return remarks;
}

public void setRemarks(String remarks) {
this.remarks = remarks;
}

public String getActionOnBalanceAmount() {
return actionOnBalanceAmount;
}

public void setActionOnBalanceAmount(String actionOnBalanceAmount) {
this.actionOnBalanceAmount = actionOnBalanceAmount;
}

public double getCharge() {
return charge;
}

public void setCharge(double charge) {
this.charge = charge;
}

public double getPreviousBalanceAmount() {
return previousBalanceAmount;
}

public void setPreviousBalanceAmount(double previousBalanceAmount) {
this.previousBalanceAmount = previousBalanceAmount;
}

public double getFinalBalanceAmount() {
return finalBalanceAmount;
}

public void setFinalBalanceAmount(double finalBalanceAmount) {
this.finalBalanceAmount = finalBalanceAmount;
}

public String getIdno() {
return idno;
}

public void setIdno(String idno) {
this.idno = idno;
}

}