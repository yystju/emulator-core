package com.tahariot.emulator.emulatorcore.rasterizers;

import com.tahariot.emulator.emulatorcore.api.Rasterizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Range<T> implements Rasterizer<T> {
    private String name;
    private T minimium;
    private T pace;
    private T maximium;
    private Pacer<T> pacer;
    private Comparator<T> comp;

    public interface Pacer<T> {
        T stepOut(T orig);
    }

    public Range(String name, T minimium, T maximium, Pacer<T> pacer, Comparator<T> comp) {
        this.name = name;
        this.minimium = minimium;
        this.maximium = maximium;
        this.pacer = pacer;
        this.comp = comp;
    }

    @Override
    public List<T> rasterize(Map<String, Object> variables, Map<String, Object> context) {
        List<T> ret = new ArrayList<>();

        T value = (T) variables.get(name);

        T start = value != null ? (comp.compare(value, minimium) >= 0 ? value : minimium) : minimium;
        T end = maximium;

        for(T i = start; comp.compare(i, end) < 0; i = pacer.stepOut(i)) {
            ret.add(i);
        }

        return ret;
    }
}
