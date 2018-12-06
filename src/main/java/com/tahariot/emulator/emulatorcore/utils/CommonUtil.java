package com.tahariot.emulator.emulatorcore.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class CommonUtil {
    public static <T> double[] continuity(List<T> vec, Comparator<T> comparator, int len) {
        int range = Math.min(vec.size(), (len >= 0 ? len : Integer.MAX_VALUE));

        double[] ret = new double[range];

        for(int i = 0; i < range; ++i) {
            T e1 = (i < vec.size()) ? vec.get(i) : null;
            T e2 = (i < vec.size() - 1) ? vec.get(i + 1) : null;

            ret[i] = (e1 != null && e2 != null) && comparator.compare(e1, e2) > 0 ? 1.0d : 0.0d;
        }

        return ret;
    }

    public static <T> double[] featureDiff(List<T> v1, List<T> v2, Comparator<T> comparator, int len) {
        int range = Math.min(Math.max(v1.size(), v2.size()), (len >= 0 ? len : Integer.MAX_VALUE));

        double[] ret = new double[range];

        for(int i = 0; i < range; ++i) {
            T e1 = (i < v1.size()) ? v1.get(i) : null;
            T e2 = (i < v1.size()) ? v2.get(i) : null;

            ret[i] = (double)(comparator.compare(e1, e2));
        }

        return ret;
    }

    public static <T> double[] offsetDiff(List<T> v1, List<T> v2, Comparator<T> comparator, int len) {
        int range = Math.min(Math.max(v1.size(), v2.size()), (len >= 0 ? len : Integer.MAX_VALUE));

        double[] ret = new double[range];

        for(int i = 0; i < range; ++i) {
            T e1 = (i < v1.size()) ? v1.get(i) : null;

            int idxv1 = (i < v1.size()) ? i : -1;
            int idxv2 = e1 != null ? v2.indexOf(v2.stream().filter(e -> comparator.compare(e, e1) == 0).findFirst().get()) : -1;

            ret[i] = (double)(idxv2 - idxv1);
        }

        return ret;
    }

    public static <T> int[] offsetDiffi(List<T> v1, List<T> v2, Comparator<T> comparator, int len) {
        int range = Math.min(Math.max(v1.size(), v2.size()), (len >= 0 ? len : Integer.MAX_VALUE));

        int[] ret = new int[range];

        for(int i = 0; i < range; ++i) {
            T e1 = (i < v1.size()) ? v1.get(i) : null;

            int idxv1 = (i < v1.size()) ? i : -1;
            int idxv2 = e1 != null ? v2.indexOf(v2.stream().filter(e -> comparator.compare(e, e1) == 0).findFirst().get()) : -1;

            ret[i] = (idxv2 - idxv1);
        }

        return ret;
    }

    public static TreeMap<Integer, List<Integer>> groupby(int[] data) {
        TreeMap<Integer, List<Integer>> ret = new TreeMap<>();

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

    public static double[] normalize(double[] data, double[] base) {
        int range = Math.max(data.length, base.length);

        double[] ret = new double[range];

        for(int i = 0; i< range; ++i) {
            ret[i] = ((i < data.length ? data[i] : 0.0) / (i < base.length ? base[i] : 1.0));
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

    public static long gcd(long a, long b) {
        long m = Math.max(a, b);
        long n = Math.min(a, b);

        while (m % n != 0) {
            long tmp = m % n;
            m = n;
            n = tmp;
        }

        return n;
    }

    public static int gcd(int a, int b) {
        int m = Math.max(a, b);
        int n = Math.min(a, b);

        while (m % n != 0) {
            int tmp = m % n;
            m = n;
            n = tmp;
        }

        return n;
    }

    public static long lcm(long a, long b) {
        return a * b / gcd(a, b);
    }

    public static int lcm(int a, int b) {
        return a * b / gcd(a, b);
    }

    public static double norm2(double[] vec) {
        double ret = 0.0d;

        for(int i = 0; i < vec.length; ++i) {
            ret += (vec[i] * vec[i]);
        }

        return Math.sqrt(ret);
    }

    public static double[] i2d(int[] arry) {
        double[] ret = new double[arry.length];
        for(int i = 0; i < arry.length; ++i) {
            ret[i] = arry[i];
        }
        return ret;
    }
}
