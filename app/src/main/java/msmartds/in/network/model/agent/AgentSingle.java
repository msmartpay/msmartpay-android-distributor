package msmartds.in.network.model.agent;

public class AgentSingle {
    private String agencyName;
    private String agentEmailId;
    private String AgentId;

    private Long numericId;
    private String amount;
    private String mobileNo;
    private String autoCredit;

    public String getAutoCredit() {
        return autoCredit;
    }

    public void setAutoCredit(String autoCredit) {
        this.autoCredit = autoCredit;
    }

    public Long getNumericId() {
        return numericId;
    }

    public void setNumericId(Long numericId) {
        this.numericId = numericId;
    }

    private String status;

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgentEmailId() {
        return agentEmailId;
    }

    public void setAgentEmailId(String agentEmailId) {
        this.agentEmailId = agentEmailId;
    }

    public String getAgentId() {
        return AgentId;
    }

    public void setAgentId(String agentId) {
        AgentId = agentId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
