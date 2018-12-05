package com.tahariot.emulator.emulatorcore.api;

import java.util.List;

public interface Result<Inbound, Outbound> {
    void debug(long takt, Outbound datium, String message);
    void info(long takt, Outbound datium, String message);
    void warn(long takt, Outbound datium, String message);
    void error(long takt, Outbound datium, String message);
    void error(long takt, Outbound datium, String message, Throwable thr);

    List<Outbound> data();
    List<History<Inbound, Outbound>> history();
}
