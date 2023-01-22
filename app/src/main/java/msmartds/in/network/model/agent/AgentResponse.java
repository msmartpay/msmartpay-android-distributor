package msmartds.in.network.model.agent;

import  msmartds.in.network.model.BaseResponse;

public class AgentResponse extends BaseResponse {

    AgentData data;
    public AgentData getData() {
        return data;
    }

    public void setData(AgentData data) {
        this.data = data;
    }
}
