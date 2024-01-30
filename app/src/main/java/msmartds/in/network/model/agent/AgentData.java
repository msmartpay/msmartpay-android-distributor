package msmartds.in.network.model.agent;

import java.util.List;

public class AgentData {
        private String agencyName;//Single
        private String amount;//Single
        private List<AgentSingle> agentDetails;//List
        private String mobileNo;//Single
        private String emailId;//Single
        private String gender;//Single
        private String agentName;//Single
        private String dob;//Single
        private String address;//Single
        private String city;//Single
        private String pinCode;//Single
        private String panNo;//Single
        private String state;//Single
        private String district;//Single
        private String firmType;
        private String AgentId;//Single
    private AgentAutoCreditDetails agentAutoCreditDetails;

    public AgentAutoCreditDetails getAgentAutoCreditDetails() {
        return agentAutoCreditDetails;
    }

    public void setAgentAutoCreditDetails(AgentAutoCreditDetails agentAutoCreditDetails) {
        this.agentAutoCreditDetails = agentAutoCreditDetails;
    }

    public String getAgentId() {
        return AgentId;
    }

    public void setAgentId(String agentId) {
        AgentId = agentId;
    }

    public String getFirmType() {
            return firmType;
        }

        public void setFirmType(String firmType) {
            this.firmType = firmType;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPinCode() {
            return pinCode;
        }

        public void setPinCode(String pinCode) {
            this.pinCode = pinCode;
        }

        public String getPanNo() {
            return panNo;
        }

        public void setPanNo(String panNo) {
            this.panNo = panNo;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }


        public String getAgencyName() {
            return agencyName;
        }

        public void setAgencyName(String agencyName) {
            this.agencyName = agencyName;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public List<AgentSingle> getAgentDetails() {
            return agentDetails;
        }

        public void setAgentDetails(List<AgentSingle> agentDetails) {
            this.agentDetails = agentDetails;
        }
    }