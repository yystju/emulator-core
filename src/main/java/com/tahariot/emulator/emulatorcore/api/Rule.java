package com.tahariot.emulator.emulatorcore.api;

import java.util.Map;

public interface Rule<Inbound> {
    boolean valid(Inbound value, Map<String, Object> context);
}
