package msmartds.in.network.model.auth;

import  msmartds.in.network.model.BaseResponse;

public class BalanceResponse extends BaseResponse {

    private Balance data;
    public Balance getData() {
        return data;
    }

    public void setData(Balance data) {
        this.data = data;
    }



}
