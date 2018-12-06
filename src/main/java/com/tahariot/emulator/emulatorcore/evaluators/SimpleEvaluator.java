package com.tahariot.emulator.emulatorcore.evaluators;

import com.tahariot.emulator.emulatorcore.utils.CommonUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SimpleEvaluator {
    public List<String> names() {
        return Arrays.asList("vc.equal.norm2", "vin.offset.vairance", "vin.offset.min", "vin.offset.max", "seq.continuity.norm2");
    }
    public <T> double[] evaluate(List<T> v1, List<T> v2, Comparator<T> comparator, Comparator<T> continuityComparator) {
        int len = Math.min(v1.size(), v2.size());

        double[] diff = CommonUtil.featureDiff(v1, v2, comparator, len);

        int[] offsetDiffi = CommonUtil.offsetDiffi(v1, v2, comparator, len);
        double[] offsetDiff = CommonUtil.i2d(offsetDiffi);

        double[] continuity = CommonUtil.continuity(v2, continuityComparator, len);

        double norm2 = CommonUtil.norm2(diff);

        double continuityNorm2 = CommonUtil.norm2(continuity);

        double variance = CommonUtil.variance(offsetDiff);

//        TreeMap<Integer, List<Integer>> statistics = CommonUtil.groupby(offsetDiffi);
//        int maxCount = statistics.values().stream().map(s -> s.size()).max(Comparator.naturalOrder()).get();
//        int pos = statistics.entrySet().stream().filter(f -> { f.getValue().size() == maxCount}).findFirst().get();

        return new double[]{norm2, variance, CommonUtil.min(offsetDiff), CommonUtil.max(offsetDiff), continuityNorm2};
    }
}
