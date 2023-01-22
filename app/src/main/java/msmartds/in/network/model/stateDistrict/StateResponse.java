package msmartds.in.network.model.stateDistrict;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import  msmartds.in.network.model.BaseResponse;

import java.util.List;

public class StateResponse extends BaseResponse {
    @SerializedName("data")
    @Expose
    private List<StateModel> StateList;

    public List<StateModel> getStateList() {
        return StateList;
    }

    public void setStateList(List<StateModel> stateList) {
        StateList = stateList;
    }


}
