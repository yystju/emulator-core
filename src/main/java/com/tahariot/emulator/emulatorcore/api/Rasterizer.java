package com.tahariot.emulator.emulatorcore.api;

import java.util.List;
import java.util.Map;

public interface Rasterizer<T> {
    List<T> rasterize(Map<String, Object> variables, Map<String, Object> context);
}
