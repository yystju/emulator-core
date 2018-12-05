package com.tahariot.emulator.emulatorcore.rasterizers;

import com.tahariot.emulator.emulatorcore.api.Rasterizer;

import java.util.ArrayList;
import java.util.List;

public class PossibilityBasedNeighbourShuffler<T> extends AbstractShuffler<T> {
    private double possibility = 0.0;

    public PossibilityBasedNeighbourShuffler(double possibility, Rasterizer<T> inner) {
        super(inner);
        this.possibility = possibility;
    }

    @Override
    public List<T> shuffle(List<T> orig) {
        List<T> ret = new ArrayList<>();


        while(orig.size() > 0) {
            double r = Math.random();

            T d = orig.remove(0);

            if(r < possibility) {
                ret.add(ret.size() > 1 ? ret.size() - 1 : ret.size(), d);
            } else {
                ret.add(d);
            }
        }

        return ret;
    }
}
