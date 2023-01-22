package msmartds.in.network.model.pushMoney;

public class PushMoneyRequest {
    private String agentId;
    private String action;
    private String TransaferAmount;
    private String PaymentRemark;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTransaferAmount() {
        return TransaferAmount;
    }

    public void setTransaferAmount(String transaferAmount) {
        TransaferAmount = transaferAmount;
    }

    public String getPaymentRemark() {
        return PaymentRemark;
    }

    public void setPaymentRemark(String paymentRemark) {
        PaymentRemark = paymentRemark;
    }
}
