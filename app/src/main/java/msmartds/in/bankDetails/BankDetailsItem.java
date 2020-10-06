package msmartds.in.bankDetails;

import androidx.annotation.NonNull;


/**
 * Created by SSZ on 7/18/2018.
 */

public class BankDetailsItem {

    private String CLIENT_ID,BANK_NAME,ACCOUNT_NO,IFSC_CODE,BANK_HOLDER_NAME,ADDRESS;

    public String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public void setCLIENT_ID(String CLIENT_ID) {
        this.CLIENT_ID = CLIENT_ID;
    }

    public String getBANK_NAME() {
        return BANK_NAME;
    }

    public void setBANK_NAME(String BANK_NAME) {
        this.BANK_NAME = BANK_NAME;
    }

    public String getACCOUNT_NO() {
        return ACCOUNT_NO;
    }

    public void setACCOUNT_NO(String ACCOUNT_NO) {
        this.ACCOUNT_NO = ACCOUNT_NO;
    }

    public String getIFSC_CODE() {
        return IFSC_CODE;
    }

    public void setIFSC_CODE(String IFSC_CODE) {
        this.IFSC_CODE = IFSC_CODE;
    }

    public String getBANK_HOLDER_NAME() {
        return BANK_HOLDER_NAME;
    }

    public void setBANK_HOLDER_NAME(String BANK_HOLDER_NAME) {
        this.BANK_HOLDER_NAME = BANK_HOLDER_NAME;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }


    @NonNull
    @Override
    public String toString() {
        return BANK_NAME;
    }
}
