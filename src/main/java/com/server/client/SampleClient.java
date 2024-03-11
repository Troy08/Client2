package com.server.client;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;


public class SampleClient {
    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://localhost:8080/Asigment1ski_war/");
        SkiersApi skiersApi = new SkiersApi(apiClient);
        int count = 0;
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            SkierLiftRideEvent skierLiftRideEvent = SkiersGenerator.generateSkierLiftRideEvent();
            try {
                LiftRide liftRide = new LiftRide()
                        .time(skierLiftRideEvent.getTime())
                        .liftID(skierLiftRideEvent.getLiftID());
                ApiResponse<Void> voidApiResponse =
                        skiersApi.writeNewLiftRideWithHttpInfo(liftRide, skierLiftRideEvent.getResortID(),
                                skierLiftRideEvent.getSeasonID(), skierLiftRideEvent.getDayID(), skierLiftRideEvent.getSkierID());
                if (voidApiResponse.getStatusCode() == 201) {
                    count++;
                }
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
        long endTime = System.nanoTime();
        /*
          local result:
          total send :1000
          total time :16131 ms
          average latency :16 ms
         */
        System.out.println("total send :" + count);
        System.out.println("total time :" + ((endTime - startTime) / 1000000) + " ms");
        System.out.println("average latency :" + ((endTime - startTime) / 1000000 / 1000) + " ms");
    }
}
