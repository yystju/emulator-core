package com.tahariot.emulator.emulatorcore.api;

import java.util.List;
import java.util.Map;

public interface Kernel<Inbound, Outbound> {
     Outbound action(long takt, List<Inbound> data, Result<Inbound, Outbound> result, Map<String, Object> context);
}
