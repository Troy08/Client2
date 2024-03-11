package com.server.client;


import java.util.Random;

public class SkiersGenerator {
    private static final Random random = new Random();

    public static SkierLiftRideEvent generateSkierLiftRideEvent() {
        SkierLiftRideEvent skierLiftRideEvent = new SkierLiftRideEvent();
        skierLiftRideEvent.setSkierID(random.nextInt(100000) + 1);
        skierLiftRideEvent.setResortID(random.nextInt(10) + 1);
        skierLiftRideEvent.setLiftID(random.nextInt(40) + 1);
        skierLiftRideEvent.setSeasonID("2024");
        skierLiftRideEvent.setDayID("1");
        skierLiftRideEvent.setTime(random.nextInt(360) + 1);
        return skierLiftRideEvent;
    }
}
