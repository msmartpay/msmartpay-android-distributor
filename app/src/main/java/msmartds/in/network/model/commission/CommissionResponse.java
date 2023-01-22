package msmartds.in.network.model.commission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import  msmartds.in.network.model.BaseResponse;

import java.util.List;

public class CommissionResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<CommModel> MyCommission;

    public List<CommModel> getMyCommission() {
        return MyCommission;
    }

    public void setMyCommission(List<CommModel> myCommission) {
        MyCommission = myCommission;
    }

}
