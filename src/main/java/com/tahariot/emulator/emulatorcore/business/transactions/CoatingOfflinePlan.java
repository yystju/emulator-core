package com.tahariot.emulator.emulatorcore.business.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonRootName("cplan")
public class CoatingOfflinePlan {
    public CoatingOfflinePlan() {
    }

    public CoatingOfflinePlan(long seq, String VIN) {
        this.seq = seq;
        this.VIN = VIN;
    }

    @JsonProperty("seq")
    private long seq;

    @JsonProperty("vin")
    private String VIN;

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
