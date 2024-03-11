package com.server.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class EventProducer implements Runnable {
    private final BlockingQueue<SkierLiftRideEvent> eventQueue;
    private static final AtomicInteger totalPosts = new AtomicInteger(0);

    public EventProducer(BlockingQueue<SkierLiftRideEvent> eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public void run() {
        while (true) {
            SkierLiftRideEvent event = SkiersGenerator.generateSkierLiftRideEvent();
            try {
                eventQueue.put(event);
                int eventCount = totalPosts.incrementAndGet();
                if (eventCount >= 200_000) {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
