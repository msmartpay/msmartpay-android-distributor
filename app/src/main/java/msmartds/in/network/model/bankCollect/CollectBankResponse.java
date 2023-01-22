package msmartds.in.network.model.bankCollect;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import  msmartds.in.network.model.BaseResponse;

import java.util.List;

public class CollectBankResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private List<CollectBankModel> Bank_List;

    public List<CollectBankModel> getBank_List() {
        return Bank_List;
    }

    public void setBank_List(List<CollectBankModel> bank_List) {
        Bank_List = bank_List;
    }

}
