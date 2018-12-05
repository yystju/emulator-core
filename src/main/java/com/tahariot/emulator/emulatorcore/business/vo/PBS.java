package com.tahariot.emulator.emulatorcore.business.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("pbs")
public class PBS {
    @JsonProperty("cap")
    private long capacity;
    @JsonProperty("avg")
    private long avgStorage;

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getAvgStorage() {
        return avgStorage;
    }

    public void setAvgStorage(long avgStorage) {
        this.avgStorage = avgStorage;
    }
}
