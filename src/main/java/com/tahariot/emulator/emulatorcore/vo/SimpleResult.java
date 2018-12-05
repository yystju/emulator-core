package com.tahariot.emulator.emulatorcore.vo;

import com.tahariot.emulator.emulatorcore.api.History;
import com.tahariot.emulator.emulatorcore.api.Result;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleResult<Inbound, Outbound> implements Result<Inbound, Outbound> {
    private List<Outbound> data = new ArrayList<>();
    private List<History<Inbound, Outbound>> historyList = new ArrayList<>();

    @Override
    public void debug(long takt, Outbound datium, String message) {
        this.historyList.add(new SimpleResultHistory<Inbound, Outbound>(History.Level.DEBUG, takt, datium, message));
    }

    @Override
    public void info(long takt, Outbound datium, String message) {
        this.historyList.add(new SimpleResultHistory<Inbound, Outbound>(History.Level.INFOR, takt, datium, message));
    }

    @Override
    public void warn(long takt, Outbound datium, String message) {
        this.historyList.add(new SimpleResultHistory<Inbound, Outbound>(History.Level.WARNN, takt, datium, message));
    }

    @Override
    public void error(long takt, Outbound datium, String message) {
        this.historyList.add(new SimpleResultHistory<Inbound, Outbound>(History.Level.ERROR, takt, datium, message));
    }

    @Override
    public void error(long takt, Outbound datium, String message, Throwable thr) {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        thr.printStackTrace(new PrintStream(outs));
        this.historyList.add(new SimpleResultHistory<Inbound, Outbound>(History.Level.ERROR, takt, datium, String.format("%s\n%s", message, outs.toString())));
    }

    public void addDatium(long takt, Outbound datium) {
        this.data.add(datium);
        debug(takt, datium, "Added result...");
    }

    @Override
    public List<Outbound> data() {
        return data;
    }

    @Override
    public List<History<Inbound, Outbound>> history() {
        return historyList;
    }

    private class SimpleResultHistory<Inbound, Outbound> implements History<Inbound, Outbound> {
        private long takt;
        private Outbound datium;
        private Date timestamp;
        private String message;
        private Level level;

        public SimpleResultHistory(Level level, long takt, Outbound datium, String message) {
            this.level = level;
            this.takt = takt;
            this.datium = datium;
            this.timestamp = new Date();
            this.message = message;
        }

        @Override
        public Level level() {
            return this.level;
        }

        public String toString() {
            return String.format("[%s :: %d - %s] %s"
                    , (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")).format(this.timestamp)
                    , takt
                    , this.datium != null ? this.datium.toString() : ""
                    , message);
        }
    }
}
