package msmartds.in.network.model.business;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import  msmartds.in.network.model.BaseResponse;

import java.util.List;

public class BusinessViewResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private List<BusinessViewItem> distributorBuisinessDetails;

    public List<BusinessViewItem> getDistributorBuisinessDetails() {
        return distributorBuisinessDetails;
    }

    public void setDistributorBuisinessDetails(List<BusinessViewItem> distributorBuisinessDetails) {
        this.distributorBuisinessDetails = distributorBuisinessDetails;
    }
}
