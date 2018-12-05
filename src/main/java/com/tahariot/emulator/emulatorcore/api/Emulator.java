package com.tahariot.emulator.emulatorcore.api;

import java.util.Map;

public interface Emulator<Inbound, Outbound> {
     Result<Inbound, Outbound> emulate(Input<Inbound, Outbound> input, Map<String, Object> context);
}
