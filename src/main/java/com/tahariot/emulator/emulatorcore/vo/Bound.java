package com.tahariot.emulator.emulatorcore.vo;

public class Bound<T> {
    private T min;
    private T max;

    public Bound(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public T getLowerBound() {
        return min;
    }

    public T getUpperBound() {
        return max;
    }
}
