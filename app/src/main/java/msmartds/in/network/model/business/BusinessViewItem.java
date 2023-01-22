package msmartds.in.network.model.business;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SSZ on 7/18/2018.
 */

public class BusinessViewItem {
    @SerializedName("Trans-Amount")
    String TransAmount;
    @SerializedName("Trans-Count")
    String TransCount;
    @SerializedName("Trans-Status")
    String TransStatus;
    @SerializedName("Service-Name")
    String ServiceName;
    @SerializedName("Month-Type")
    String MonthType;

    public String getTransAmount() {
        return TransAmount;
    }

    public void setTransAmount(String transAmount) {
        TransAmount = transAmount;
    }

    public String getTransCount() {
        return TransCount;
    }

    public void setTransCount(String transCount) {
        TransCount = transCount;
    }

    public String getTransStatus() {
        return TransStatus;
    }

    public void setTransStatus(String transStatus) {
        TransStatus = transStatus;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getMonthType() {
        return MonthType;
    }

    public void setMonthType(String monthType) {
        MonthType = monthType;
    }

}
