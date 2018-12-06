package com.tahariot.emulator.emulatorcore.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class CommonUtil {
    public static <T> double offsetVariance(List<T> v1, List<T> v2) {
        return offsetVariance(v1, v2, -1);
    }
    public static <T> double offsetVariance(List<T> v1, List<T> v2, int len) {
        return variance(offsetDiff(v1, v2, len));
    }

    public static <T> double[] positiveBiasDiff(List<T> v1, List<T> v2, int len) {
        int range = Math.min(Math.max(v1.size(), v2.size()), (len >= 0 ? len : Integer.MAX_VALUE));

        double[] ret = new double[range];

        for(int i = 0; i < range; ++i) {
            T e1 = (i < v1.size()) ? v1.get(i) : null;

            int idxv1 = (i < v1.size()) ? i : -1;
            int idxv2 = e1 != null ? v2.indexOf(e1) : -1;

            ret[i] = (double)(idxv2 - idxv1 > 0 ? idxv2 - idxv1 : 0);
        }

        return ret;
    }

    public static <T> double[] negativeBiasDiff(List<T> v1, List<T> v2, int len) {
        int range = Math.min(Math.max(v1.size(), v2.size()), (len >= 0 ? len : Integer.MAX_VALUE));

        double[] ret = new double[range];

        for(int i = 0; i < range; ++i) {
            T e1 = (i < v1.size()) ? v1.get(i) : null;

            int idxv1 = (i < v1.size()) ? i : -1;
            int idxv2 = e1 != null ? v2.indexOf(e1) : -1;

            ret[i] = (double)(idxv2 - idxv1 < 0 ? idxv2 - idxv1 : 0);
        }

        return ret;
    }

    public static <T> double[] offsetDiff(List<T> v1, List<T> v2, int len) {
        int range = Math.min(Math.max(v1.size(), v2.size()), (len >= 0 ? len : Integer.MAX_VALUE));

        double[] ret = new double[range];

        for(int i = 0; i < range; ++i) {
            T e1 = (i < v1.size()) ? v1.get(i) : null;

            int idxv1 = (i < v1.size()) ? i : -1;
            int idxv2 = e1 != null ? v2.indexOf(e1) : -1;

            ret[i] = (double)(idxv2 - idxv1);
        }

        return ret;
    }

    public static <T> int[] offsetDiffi(List<T> v1, List<T> v2, int len) {
        int range = Math.min(Math.max(v1.size(), v2.size()), (len >= 0 ? len : Integer.MAX_VALUE));

        int[] ret = new int[range];

        for(int i = 0; i < range; ++i) {
            T e1 = (i < v1.size()) ? v1.get(i) : null;

            int idxv1 = (i < v1.size()) ? i : -1;
            int idxv2 = e1 != null ? v2.indexOf(e1) : -1;

            ret[i] = (idxv2 - idxv1);
        }

        return ret;
    }

    public static Map<Integer, List<Integer>> groupby(int[] data) {
        Map<Integer, List<Integer>> ret = new TreeMap<>();

        for(int i = 0; i< data.length; ++i) {
            List<Integer> list = null;

            if(ret.containsKey(data[i])) {
                list = ret.get(data[i]);
            } else {
                list = new ArrayList<>();
                ret.put(data[i], list);
            }

            list.add(i);
        }

        return ret;
    }

    public static Map<Integer, Integer> count(int[] data) {
        Map<Integer, Integer> ret = new TreeMap<>();

        for(int i = 0; i< data.length; ++i) {
            int cnt = 0;

            if(ret.containsKey(data[i])) {
                cnt = ret.get(data[i]);
            }

            ret.put(data[i], ++cnt);
        }

        return ret;
    }

    public static double min(double[] data) {
        double ret = 0.0f;

        for(int i = 0; i< data.length; ++i) {
            if(ret > data[i]) {
                ret = data[i];
            }
        }

        return ret;
    }

    public static double max(double[] data) {
        double ret = 0.0f;

        for(int i = 0; i< data.length; ++i) {
            if(ret < data[i]) {
                ret = data[i];
            }
        }

        return ret;
    }

    public static double variance(double[] vec) {
        if(vec.length <= 1) {
            return 0.0f;
        }

        double m = 0.0;

        for(int i = 0; i < vec.length; ++i) {
            m += vec[i];
        }

        double s = 0.0;

        for(int i = 0; i < vec.length; ++i) {
            s += (vec[i] - m) * (vec[i] - m);
        }

        return (s / (vec.length - 1));
    }

    public static interface Selector<T> {
        boolean select(T t);
    }
    public static double[] filter(double[] orig, Selector<Double> selector) {
        Object[] selected = Arrays.asList(ArrayUtils.toObject(orig)).stream().filter(d -> selector.select(d)).toArray();

        double[] ret = new double[selected.length];

        for(int i = 0; i < selected.length; ++i) {
            ret[i] = (Double)selected[i];
        }

        return ret;
    }
}
