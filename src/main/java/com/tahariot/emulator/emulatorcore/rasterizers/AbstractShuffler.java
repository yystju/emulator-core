package com.tahariot.emulator.emulatorcore.rasterizers;

import com.tahariot.emulator.emulatorcore.api.Rasterizer;

import java.util.List;
import java.util.Map;

public abstract class AbstractShuffler<T> implements Rasterizer<T> {
    private Rasterizer<T> inner;

    public AbstractShuffler(Rasterizer<T> inner) {
        this.inner = inner;
    }

    public abstract List<T> shuffle(List<T> orig);

    @Override
    public List<T> rasterize(Map<String, Object> variables, Map<String, Object> context) {
        return shuffle(inner.rasterize(variables, context));
    }
}
