package msmartds.in.network.model.stateDistrict;

import org.jetbrains.annotations.NotNull;

public class DistrictModel {
    private String district;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @NotNull
    @Override
    public String toString() {
        return district;
    }
}
