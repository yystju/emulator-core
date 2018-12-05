package com.tahariot.emulator.emulatorcore.features;

import com.tahariot.emulator.emulatorcore.api.Feature;

import java.util.List;

public class OffsetFeature<T> implements Feature<Double> {
    private List<T> collection;

    public OffsetFeature(List<T> collection) {
        this.collection = collection;
    }

    @Override
    public Double feature(int index) {
        T value = (index < this.collection.size()) ? this.collection.get(index) : null;
        return (double) (value != null ? index : this.collection.size() + 1);
    }
}
