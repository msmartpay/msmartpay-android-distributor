package msmartds.in.network.model.balanceRequest;

import  msmartds.in.network.model.BaseResponse;

import java.util.List;

public class BalHistoryResponse extends BaseResponse {

    private List<BalHistoryData> data;
    public List<BalHistoryData> getData() {
        return data;
    }

    public void setData(List<BalHistoryData> data) {
        this.data = data;
    }
}
