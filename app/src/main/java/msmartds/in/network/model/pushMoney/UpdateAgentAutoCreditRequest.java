package msmartds.in.network.model.pushMoney;

public class UpdateAgentAutoCreditRequest {
    private String agentId;
    private String minimumAmount;
    private String topupAmount;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getTopupAmount() {
        return topupAmount;
    }

    public void setTopupAmount(String topupAmount) {
        this.topupAmount = topupAmount;
    }
}
