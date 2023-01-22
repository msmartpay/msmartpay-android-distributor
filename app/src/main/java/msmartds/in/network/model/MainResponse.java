package msmartds.in.network.model;

import com.google.gson.annotations.SerializedName;

public class MainResponse {
    @SerializedName("response-code")
    private String responseCode;
    @SerializedName("response-message")
    private String responseMessage;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
