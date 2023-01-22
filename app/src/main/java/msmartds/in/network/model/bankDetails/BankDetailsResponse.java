package msmartds.in.network.model.bankDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import  msmartds.in.network.model.BaseResponse;

import java.util.List;

public class BankDetailsResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<BankDetailsItem> BankDetails;

    public List<BankDetailsItem> getBankDetails() {
        return BankDetails;
    }

    public void setBankDetails(List<BankDetailsItem> bankDetails) {
        BankDetails = bankDetails;
    }

}
