package msmartds.in.businessView;

/**
 * Created by SSZ on 7/18/2018.
 */

public class BusinessViewItem {

    String TransAmount,TransCount,TransStatus,ServiceName,MonthType;

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
