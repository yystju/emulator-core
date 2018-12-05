package com.tahariot.emulator.emulatorcore.api;

public interface Input<Inbound, Outbound> {
    Rasterizer<Inbound> getRasterizer();
    Rule<Outbound> getOutputRule();
}
