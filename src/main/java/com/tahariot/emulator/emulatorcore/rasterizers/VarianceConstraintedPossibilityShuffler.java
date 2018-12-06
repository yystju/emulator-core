package com.tahariot.emulator.emulatorcore.rasterizers;

import com.tahariot.emulator.emulatorcore.api.Rasterizer;
import com.tahariot.emulator.emulatorcore.api.Transformer;
import com.tahariot.emulator.emulatorcore.transformers.VarianceTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class VarianceConstraintedPossibilityShuffler<T> extends AbstractShuffler<T> {
    private static Logger logger = LoggerFactory.getLogger(VarianceConstraintedPossibilityShuffler.class);

    private double shuffleRatio;
    private long window;

    public VarianceConstraintedPossibilityShuffler(long window, double shuffleRatio, Rasterizer<T> inner) {
        super(inner);
        this.window = window;
        this.shuffleRatio = shuffleRatio;
    }

    @Override
    public List<T> shuffle(List<T> orig) {
        List<T> ret = null;

        double ratio = Double.MAX_VALUE;

        int maxTimes = 1000;

        Transformer<List<T>, Double> transformer = getTransformer(orig);

        do {
            ret = new ArrayList<>();

            List<T> cloned = new ArrayList<>();

            cloned.addAll(orig);

            while(cloned.size() > 0) {
                long range = orig.size();

                if(window > 0) {
                    range = Math.min(window, range);
                }

                double pace = 1.0f / ((double)range);

                double r = Math.random();

                int idx = ((int)(r / pace));

                if(idx < cloned.size()) {
                    ret.add(cloned.remove(idx));
                }
            }

            ratio = transformer.transform(ret);

            logger.debug("orig ({}) : {}", orig.size(), orig);
            logger.debug("ret ({}) : {}", ret.size(), ret);
            logger.debug("ratio : {}, shuffleRatio : {}", ratio, this.shuffleRatio);

            --maxTimes;

            if(maxTimes <= 0) {
                throw new RuntimeException("Attempted too many times for finding a result...");
            }
        } while(ratio > this.shuffleRatio);

        return ret;
    }

    private Transformer<List<T>, Double> getTransformer(List<T> orig) {
        return new VarianceTransformer<>(orig);
    }
}
