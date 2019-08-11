package com.nconnect.teacher.OttoEvents;

public class AndroidBusProvider {
    private static final AndroidBus BUS = new AndroidBus();

    public static AndroidBus getInstance() {
        return BUS;
    }

    private AndroidBusProvider() {
    }
}