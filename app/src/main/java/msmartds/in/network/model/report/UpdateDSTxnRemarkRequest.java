package msmartds.in.network.model.report;

import com.google.gson.annotations.SerializedName;

public class UpdateDSTxnRemarkRequest {

    @SerializedName("id_no")
    private String idNo;
    private String remark;

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
