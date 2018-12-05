package com.tahariot.emulator.emulatorcore.rasterizers;

import com.tahariot.emulator.emulatorcore.api.Rasterizer;

import java.util.ArrayList;
import java.util.List;

public class EvenPossibilityShuffler<T> extends AbstractShuffler<T> {
    private long window;

    public EvenPossibilityShuffler(Rasterizer<T> inner) {
        this(-1, inner);
    }

    public EvenPossibilityShuffler(long window, Rasterizer<T> inner) {
        super(inner);
        this.window = window;
    }

    @Override
    public List<T> shuffle(List<T> orig) {
        List<T> ret = new ArrayList<>();

        while(orig.size() > 0) {
            long range = orig.size();

            if(window > 0) {
                range = Math.min(window, range);
            }

            double pace = 1.0f / ((double)range);

            double r = Math.random();

            int idx = ((int)(r / pace));

            if(idx < orig.size()) {
                ret.add(orig.remove(idx));
            }
        }

        return ret;
    }
}
