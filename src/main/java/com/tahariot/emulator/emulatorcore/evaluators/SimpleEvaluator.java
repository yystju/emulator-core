package com.tahariot.emulator.emulatorcore.evaluators;

import com.tahariot.emulator.emulatorcore.utils.CommonUtil;

public class SimpleEvaluator {
    public double[] evaluate(double[] diff) {
        double variance = CommonUtil.variance(diff);

        double min = CommonUtil.min(diff);
        double max = CommonUtil.max(diff);

        double[] nomove = CommonUtil.filter(diff, d -> d == 0.0);

        double nomoveVariance = CommonUtil.variance(nomove);
        double nomoveMin = CommonUtil.min(nomove);
        double nomoveMax = CommonUtil.max(nomove);

        double[] positive = CommonUtil.filter(diff, d -> d > 0.0);

        double positiveVariance = CommonUtil.variance(positive);
//        double positiveMin = CommonUtil.min(positive);
        double positiveMax = CommonUtil.max(positive);

        double[] negative = CommonUtil.filter(diff, d -> d < 0.0);

        double negativeVariance = CommonUtil.variance(negative);
        double negativeMin = CommonUtil.min(negative);
//        double negativeMax = CommonUtil.max(negative);

        return new double[]{variance, min, max, nomoveVariance, nomoveMin, nomoveMax, positiveVariance, positiveMax, negativeVariance, negativeMin};
    }
}
