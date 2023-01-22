package msmartds.in.network.model.bankCollect;

public class CollectBankModel {

    private String bank_name,bank_account,bank_account_name,bnk_ifsc;

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getBank_account_name() {
        return bank_account_name;
    }

    public void setBank_account_name(String bank_account_name) {
        this.bank_account_name = bank_account_name;
    }

    public String getBnk_ifsc() {
        return bnk_ifsc;
    }

    public void setBnk_ifsc(String bnk_ifsc) {
        this.bnk_ifsc = bnk_ifsc;
    }


    @Override
    public String toString() {
        return bank_name;
    }
}
