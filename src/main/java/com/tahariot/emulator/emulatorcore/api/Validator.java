package com.tahariot.emulator.emulatorcore.api;

public interface Validator<Data, Index> {
    boolean valid(Data data, Index index);
}
