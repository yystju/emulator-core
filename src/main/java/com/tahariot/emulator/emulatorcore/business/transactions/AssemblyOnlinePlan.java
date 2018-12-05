package com.tahariot.emulator.emulatorcore.business.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonRootName("aplan")
public class AssemblyOnlinePlan {
    public AssemblyOnlinePlan() {
    }

    public AssemblyOnlinePlan(long seq, String VIN, String vehicleCode) {
        this.seq = seq;
        this.VIN = VIN;
        this.vehicleCode = vehicleCode;
        this.status = "0";
    }

    @JsonProperty("seq")
    private long seq;

    @JsonProperty("vin")
    private String VIN;

    @JsonProperty("status")
    private String status;

    @JsonProperty("vc")
    private String vehicleCode;

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleCode() {
        return vehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        this.vehicleCode = vehicleCode;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
