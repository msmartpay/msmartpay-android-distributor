package msmartds.in.network.model.auth;

import  msmartds.in.network.model.BaseResponse;

//
public class LoginResponse extends BaseResponse {

    private UserData data;
    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

 }
