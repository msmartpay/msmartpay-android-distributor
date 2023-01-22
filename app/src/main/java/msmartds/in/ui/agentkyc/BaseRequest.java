package msmartds.in.ui.agentkyc;

public class BaseRequest {
    private String txnkey;
    private String agentId;
    private String distributorId;
    private int kycStatus;

    public int getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(int kycStatus) {
        this.kycStatus = kycStatus;
    }



    public String getTxnkey() {
        return txnkey;
    }

    public void setTxnkey(String txnkey) {
        this.txnkey = txnkey;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

}
