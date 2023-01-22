package msmartds.in.ui.agentkyc;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DocumentDataResponseContainer {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("documents")
    private ArrayList<DocumentDataResponse> documents;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<DocumentDataResponse> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<DocumentDataResponse> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "DocumentDataResponseContainer{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
