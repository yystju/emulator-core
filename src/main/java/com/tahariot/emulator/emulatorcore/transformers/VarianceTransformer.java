package com.tahariot.emulator.emulatorcore.transformers;

import com.tahariot.emulator.emulatorcore.api.Feature;
import com.tahariot.emulator.emulatorcore.api.Transformer;
import com.tahariot.emulator.emulatorcore.features.OffsetFeature;

import java.util.List;

public class VarianceTransformer<T> implements Transformer<List<T>, Double> {
    private List<T> orig;
    public VarianceTransformer(List<T> orig) {
        this.orig = orig;
    }

    @Override
    public Double transform(List<T> ts) {
        return calcRatio(this.orig, ts);
    }

    private double calcRatio(List<T> orig, List<T> candidate) {
        int range = Math.max(orig.size(), candidate.size());

        if(range == 0) {
            return 0.0;
        }

        Feature<Double> origFeature = getFeature(orig);
        Feature<Double> candidateFeature = getFeature(candidate);

        double d = 0.0;
        double s = 0.0;

        for(int i = 0; i < range; ++i) {
            T value = (i < orig.size()) ? orig.get(i) : null;
            int candidateIndex = (value != null) ? candidate.indexOf(value) : -1;

            double origValue = origFeature.feature(i);
            double candidateValue = candidateFeature.feature(candidateIndex);

            s += (candidateValue - origValue);
            d += (candidateValue - origValue) * (candidateValue - origValue);
        }

        return (Math.sqrt(d - range * s) / (range - 1));
    }

    private Feature<Double> getFeature(List<T> orig) {
        return new OffsetFeature<>(orig);
    }
}
