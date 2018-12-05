package com.tahariot.emulator.emulatorcore.business.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("vehicle")
public class Vehicle {
    @JsonProperty("vin")
    private String VIN;

    @JsonProperty("vc")
    private String vehicleCode;

    @JsonProperty("vc")
    private String status;

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public String getVehicleCode() {
        return vehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        this.vehicleCode = vehicleCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
