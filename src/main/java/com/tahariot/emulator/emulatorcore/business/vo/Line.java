package com.tahariot.emulator.emulatorcore.business.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("line")
public class Line {
    @JsonProperty("code")
    private String code;

    @JsonProperty("takt")
    private long takt;

    @JsonProperty("rogm")
    private double rationOfGrainMovement;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getTakt() {
        return takt;
    }

    public void setTakt(long takt) {
        this.takt = takt;
    }

    public double getRationOfGrainMovement() {
        return rationOfGrainMovement;
    }

    public void setRationOfGrainMovement(double rationOfGrainMovement) {
        this.rationOfGrainMovement = rationOfGrainMovement;
    }
}
