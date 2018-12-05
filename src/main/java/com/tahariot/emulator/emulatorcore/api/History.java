package com.tahariot.emulator.emulatorcore.api;

public interface History<Inbound, Outbound> {
    enum Level {
        DEBUG, INFOR, WARNN, ERROR
    }

    Level level();
    String toString();
}
