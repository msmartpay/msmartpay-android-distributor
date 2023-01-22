package msmartds.in.network.model.commission;

public class CommModel {
    private String StartRange,EndRange,Service,Operator,CommType,Comm;

    public String getStartRange() {
        return StartRange;
    }

    public void setStartRange(String startRange) {
        StartRange = startRange;
    }

    public String getEndRange() {
        return EndRange;
    }

    public void setEndRange(String endRange) {
        EndRange = endRange;
    }

    public String getService() {
        return Service;
    }

    public void setService(String service) {
        Service = service;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }

    public String getCommType() {
        return CommType;
    }

    public void setCommType(String commType) {
        CommType = commType;
    }

    public String getComm() {
        return Comm;
    }

    public void setComm(String comm) {
        Comm = comm;
    }
}
