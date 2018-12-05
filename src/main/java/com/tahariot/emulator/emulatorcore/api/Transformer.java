package com.tahariot.emulator.emulatorcore.api;

public interface Transformer<Inbound, Outbound> {
    Outbound transform(Inbound inbound);
}
