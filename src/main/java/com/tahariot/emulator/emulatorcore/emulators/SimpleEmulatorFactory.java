package com.tahariot.emulator.emulatorcore.emulators;

import com.tahariot.emulator.emulatorcore.api.Emulator;
import com.tahariot.emulator.emulatorcore.api.EmulatorFactory;
import com.tahariot.emulator.emulatorcore.api.Kernel;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SimpleEmulatorFactory<T1, T2> implements EmulatorFactory<T1, T2> {
    @Override
    public Emulator<T1, T2> newInstance(String category, Kernel<T1, T2> kernel, Map<String, Object> initVariables) {
        if("simple".equals(category)) {
            return new SimpleEmulator<T1, T2>(kernel, initVariables);
        }

        return null;
    }
}
