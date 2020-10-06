package msmartds.in.collectBanks;

public class CollectBankModel {
    private String bank_name;
    private String bank_account;
    private String bank_account_name;
    private String bnk_ifsc;
    private String actual_bank_name;
    public String getActual_bank_name() {
        return actual_bank_name;
    }

    public void setActual_bank_name(String actual_bank_name) {
        this.actual_bank_name = actual_bank_name;
    }



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
