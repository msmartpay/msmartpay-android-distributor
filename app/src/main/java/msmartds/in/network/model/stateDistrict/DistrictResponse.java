package msmartds.in.network.model.stateDistrict;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import  msmartds.in.network.model.BaseResponse;

import java.util.List;

public class DistrictResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<DistrictModel> districtList;

    public List<DistrictModel> getDistrictList() {
        return districtList;
    }

    public void setDistrictList(List<DistrictModel> districtList) {
        this.districtList = districtList;
    }

}
