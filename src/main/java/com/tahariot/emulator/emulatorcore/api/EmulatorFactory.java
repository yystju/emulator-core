package com.tahariot.emulator.emulatorcore.api;

import java.util.Map;

public interface EmulatorFactory<T1, T2> {
    Emulator<T1, T2> newInstance(String category, Kernel<T1, T2> kernel, Map<String, Object> initVariables);
}
