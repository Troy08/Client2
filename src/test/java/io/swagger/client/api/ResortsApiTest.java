/*
 * Ski Data API for NEU Seattle distributed systems course
 * An API for an emulation of skier managment system for RFID tagged lift tickets. Basis for CS6650 Assignments for 2019
 *
 * OpenAPI spec version: 1.14
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.api;

import io.swagger.client.model.ResortIDSeasonsBody;
import io.swagger.client.model.ResortsList;
import io.swagger.client.model.ResponseMsg;
import io.swagger.client.model.SeasonsList;
import org.junit.Test;
import org.junit.Ignore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * API tests for ResortsApi
 */
@Ignore
public class ResortsApiTest {

    private final ResortsApi api = new ResortsApi();

    /**
     * Add a new season for a resort
     *
     * 
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void addSeasonTest() throws Exception {
        ResortIDSeasonsBody body = null;
        Integer resortID = null;
        api.addSeason(body, resortID);

        // TODO: test validations
    }
    /**
     * get a list of seasons for the specified resort
     *
     * 
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getResortSeasonsTest() throws Exception {
        Integer resortID = null;
        SeasonsList response = api.getResortSeasons(resortID);

        // TODO: test validations
    }
    /**
     * get a list of ski resorts in the database
     *
     * 
     *
     * @throws Exception
     *          if the Api call fails
     */
    @Test
    public void getResortsTest() throws Exception {
        ResortsList response = api.getResorts();

        // TODO: test validations
    }
}
