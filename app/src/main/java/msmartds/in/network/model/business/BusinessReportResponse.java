package msmartds.in.network.model.business;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import  msmartds.in.network.model.BaseResponse;

import java.util.List;

public class BusinessReportResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private List<BusinessReportModel> statement;

    public List<BusinessReportModel> getStatement() {
        return statement;
    }

    public void setStatement(List<BusinessReportModel> statement) {
        this.statement = statement;
    }

}
