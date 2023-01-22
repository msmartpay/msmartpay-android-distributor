package msmartds.in.network.model;

public class MainRequest {
    private String distributorId;
    private String txnkey;

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getTxnkey() {
        return txnkey;
    }

    public void setTxnkey(String txnkey) {
        this.txnkey = txnkey;
    }
}
