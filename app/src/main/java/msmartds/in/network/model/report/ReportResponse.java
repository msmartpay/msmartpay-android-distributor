package msmartds.in.network.model.report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import  msmartds.in.network.model.BaseResponse;

import java.util.List;

public class ReportResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<ReportModel> statement;

    public List<ReportModel> getStatement() {
        return statement;
    }

    public void setStatement(List<ReportModel> statement) {
        this.statement = statement;
    }
}
