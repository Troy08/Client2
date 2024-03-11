package com.server.client;


import com.opencsv.CSVWriter;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SkiersClient {
    private static final AtomicInteger successCounter = new AtomicInteger(0);
    private static final AtomicInteger failCounter = new AtomicInteger(0);
    private static final String CSV_FILE = "./requests_log.csv";


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>(10000);
        Thread producerThread = new Thread(new EventProducer(eventQueue));
        producerThread.start();
        List<Recorder> recorders = Collections.synchronizedList(new ArrayList<Recorder>());
        // create 32 threads that each send 1000 POST requests and terminate.
        runTaskInMultiThread(32, eventQueue, recorders);
        // run other requests
        runTaskInMultiThread(168, eventQueue, recorders);

        long endTime = System.currentTimeMillis();
        System.out.println("number of successful requests sent: " + successCounter.get());
        System.out.println("number of unsuccessful requests: " + failCounter.get());
        System.out.printf("the total run time (wall time): %d ms\n", endTime - startTime);
        System.out.println("the total throughput in requests per second: "
                + (successCounter.get() / ((double) (endTime - startTime) / 1000)));
        // write to csv file
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE));
            String[] header = {"start time", "request type", "latency", "response code"};
            writer.writeNext(header);
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            synchronized (recorders) {
                for (Recorder recorder : recorders) {
                    String[] temp = new String[4];
                    temp[0] = ft.format(new Date(recorder.getStartTime())) + "\t";
                    temp[1] = recorder.getRequestType();
                    temp[2] = recorder.getLatency() + "\t";
                    temp[3] = String.valueOf(recorder.getResponseCode());
                    writer.writeNext(temp);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // calculate other data
        long sum = 0;
        for (Recorder recorder : recorders) {
            sum += recorder.getLatency();
        }
        recorders.sort((r1, r2) -> Math.toIntExact(r1.getLatency() - r2.getLatency()));
        int index = (int) Math.ceil(99 / 100.0 * recorders.size()) - 1;
        long p99Latency = recorders.get(index).getLatency();

        long minLatency = recorders.get(0).getLatency();
        long maxLatency = recorders.get(recorders.size() - 1).getLatency();
        long medianLatency = recorders.get(recorders.size() / 2).getLatency();
        double meanLatency = (double) sum / recorders.size();

        System.out.println("mean response time (millisecs): " + meanLatency);
        System.out.println("median response time (millisecs): " + medianLatency);
        System.out.println("throughput (requests/second): " + (successCounter.get() / ((double) (endTime - startTime) / 1000)));
        System.out.println("p99 (99th percentile) response time: " + p99Latency);
        System.out.println("min response time (milliseconds): " + minLatency);
        System.out.println("max response time (milliseconds): " + maxLatency);
    }


    private static void runTaskInMultiThread(int taskNum, BlockingQueue<SkierLiftRideEvent> eventQueue, List<Recorder> recorders) {
        ExecutorService executor = Executors.newFixedThreadPool(taskNum);
        for (int i = 0; i < taskNum; i++) {
            executor.submit(new PostTask(eventQueue, recorders, i % 2 == 0));
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("ExecutorService did not terminate.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    static class PostTask implements Runnable {
        private final BlockingQueue<SkierLiftRideEvent> eventQueue;
        private final List<Recorder> recorders;
        private final boolean loadFlag;

        PostTask(BlockingQueue<SkierLiftRideEvent> eventQueue, List<Recorder> recorders, boolean loadFlag) {
            this.eventQueue = eventQueue;
            this.recorders = recorders;
            this.loadFlag = loadFlag;
        }

        @Override
        public void run() {
            ApiClient apiClient = new ApiClient();
//            apiClient.setBasePath("http://localhost:8080/Asigment1ski_war/");
            if (loadFlag) {
                apiClient.setBasePath("http://localhost:8080/Asigment1ski_war/");
            } else {
                apiClient.setBasePath("http://localhost:8081/Asigment1ski_war/");
            }
            SkiersApi skiersApi = new SkiersApi(apiClient);

            for (int i = 0; i < 1000; i++) {
                try {
                    SkierLiftRideEvent skierLiftRideEvent = eventQueue.take();
                    LiftRide liftRide = new LiftRide()
                            .time(skierLiftRideEvent.getTime())
                            .liftID(skierLiftRideEvent.getLiftID());
                    boolean requestSuccessful = false;
                    int attempt = 0;
                    while (attempt < 5 && !requestSuccessful) {
                        long startTime = System.currentTimeMillis();
                        ApiResponse<Void> voidApiResponse =
                                skiersApi.writeNewLiftRideWithHttpInfo(liftRide, skierLiftRideEvent.getResortID(),
                                        skierLiftRideEvent.getSeasonID(), skierLiftRideEvent.getDayID(), skierLiftRideEvent.getSkierID());
                        if (voidApiResponse.getStatusCode() == 201) {
                            successCounter.incrementAndGet();
                            requestSuccessful = true;
                        } else {
                            attempt++;
                        }
                        long endTime = System.currentTimeMillis();
                        long latency = endTime - startTime;
                        recorders.add(new Recorder(startTime, "POST", latency, voidApiResponse.getStatusCode()));
                    }

                } catch (InterruptedException e) {
                    failCounter.incrementAndGet();
                    Thread.currentThread().interrupt();
                    break;
                } catch (ApiException e) {
                    failCounter.incrementAndGet();
                    throw new RuntimeException(e);
                }
            }
        }
    }
}


