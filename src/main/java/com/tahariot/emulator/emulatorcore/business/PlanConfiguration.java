package com.tahariot.emulator.emulatorcore.business;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.tahariot.emulator.emulatorcore.business.transactions.AssemblyOnlinePlan;
import com.tahariot.emulator.emulatorcore.business.transactions.CoatingOfflinePlan;
import com.tahariot.emulator.emulatorcore.business.vo.Line;
import com.tahariot.emulator.emulatorcore.business.vo.PBS;

import java.util.List;

@JsonRootName("config")
public class PlanConfiguration {
    @JsonProperty("name")
    private String name;

    @JsonProperty("line")
    private Line line;

    @JsonProperty("pbs")
    private PBS pbs;

    @JsonProperty("coating")
    private List<CoatingOfflinePlan> coatingOfflinePlans;

    @JsonProperty("assembly")
    private List<AssemblyOnlinePlan> assemblyOnlinePlans;

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public PBS getPbs() {
        return pbs;
    }

    public void setPbs(PBS pbs) {
        this.pbs = pbs;
    }

    public List<CoatingOfflinePlan> getCoatingOfflinePlans() {
        return coatingOfflinePlans;
    }

    public void setCoatingOfflinePlans(List<CoatingOfflinePlan> coatingOfflinePlans) {
        this.coatingOfflinePlans = coatingOfflinePlans;
    }

    public List<AssemblyOnlinePlan> getAssemblyOnlinePlans() {
        return assemblyOnlinePlans;
    }

    public void setAssemblyOnlinePlans(List<AssemblyOnlinePlan> assemblyOnlinePlans) {
        this.assemblyOnlinePlans = assemblyOnlinePlans;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
