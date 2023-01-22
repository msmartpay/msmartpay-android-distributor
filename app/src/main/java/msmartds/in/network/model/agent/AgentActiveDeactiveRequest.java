package msmartds.in.network.model.agent;

public class AgentActiveDeactiveRequest {

    private String param;
    private String agentFullId;
    private String checkChangeStatus;
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getAgentFullId() {
        return agentFullId;
    }

    public void setAgentFullId(String agentFullId) {
        this.agentFullId = agentFullId;
    }

    public String getCheckChangeStatus() {
        return checkChangeStatus;
    }

    public void setCheckChangeStatus(String checkChangeStatus) {
        this.checkChangeStatus = checkChangeStatus;
    }
}
