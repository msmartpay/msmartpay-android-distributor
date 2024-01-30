package msmartds.in.network.model.agent;

public class AgentAutoCreditDetails {
    private Integer minimumAmount;
    private Integer topupAmount;

    public Integer getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(Integer minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Integer getTopupAmount() {
        return topupAmount;
    }

    public void setTopupAmount(Integer topupAmount) {
        this.topupAmount = topupAmount;
    }
}
