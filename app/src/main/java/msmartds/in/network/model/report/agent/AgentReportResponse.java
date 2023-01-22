package msmartds.in.network.model.report.agent;

import java.util.List;

public class AgentReportResponse {
    private String status;
    private String message;
    private List<AgentReportModel> statement;

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

    public List<AgentReportModel> getStatement() {
        return statement;
    }

    public void setStatement(List<AgentReportModel> statement) {
        this.statement = statement;
    }
}
