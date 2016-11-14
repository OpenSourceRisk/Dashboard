
/*
 Copyright (C) 2016 Quaternion Risk Management Ltd
 All rights reserved.

 This file is part of ORE, a free-software/open-source library
 for transparent pricing and risk analysis - http://opensourcerisk.org

 ORE is free software: you can redistribute it and/or modify it
 under the terms of the Modified BSD License.  You should have received a
 copy of the license along with this program.
 The license is also available online at <http://opensourcerisk.org>

 This program is distributed on the basis that it will form a useful
 contribution to risk analytics and model standardisation, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE. See the license for more details.
*/

package app;

import app.cache.Cache;
import app.graph.*;
import app.report.LimitTableController;
import app.report.LimitTableDao;
import app.util.DashBoardException;
import app.util.JsonUtil;
import app.util.Path;

import static spark.Spark.*;

public class Application {

    // Declare dependencies
    public static CacheDao cacheDao;
    public static BarGraphDao barGraphDao;
    public static XvaDao xvaDao;
    public static ExposureDao exposureDao;
    public static TotalExposureDao totalExposureDao;
    public static RiskGaugeDao riskGaugeDao;
    public static LimitTableDao limitTableDao;
    public static Cache cache;

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    public static void main(String[] args) {

        cacheDao = new CacheDao();
        barGraphDao = new BarGraphDao();
        xvaDao = new XvaDao();
        exposureDao = new ExposureDao();
        totalExposureDao = new TotalExposureDao();
        riskGaugeDao = new RiskGaugeDao();
        limitTableDao = new LimitTableDao();

        // Configure Spark
        port(getHerokuAssignedPort());

        staticFileLocation("/public");

        // Set up before-filters (called before each get/post)
//        before("*", Filters.handleLocaleChange);

        // Set up routes

        // these are HTML links (no JSON)
        get(Path.API_CONTEXT + "/tree", CacheController.fetchNodeLinks);
        get(Path.API_CONTEXT + "/tree2", CacheController.fetchNodeLinks2);
        get(Path.API_CONTEXT + "/periscope", CacheController.fetchNodeLinks3);
        get(Path.API_CONTEXT + "/bargraph", BarGraphController.fetchBarGraphLinks);
        get(Path.API_CONTEXT + "/bargraph-tree", BarGraphController.fetchBarGraphTreeLinks);
        get(Path.API_CONTEXT + "/xva", XvaController.fetchXvaLinks);
        get(Path.API_CONTEXT + "/xva-tree", XvaController.fetchXvaTreeLinks);
        get(Path.API_CONTEXT + "/totalexposure-tree", TotalExposureController.fetchTotalExposureTreeLinks);
        get(Path.API_CONTEXT + "/exposure-tree", ExposureController.fetchExposureTreeLinks);
        get(Path.API_CONTEXT + "/gauge-tree", RiskGaugeController.fetchRiskGaugeTreeLinks);
        get(Path.API_CONTEXT + "/totalportfolio", LimitTableController.fetchTotalPortfolioLinks);

        // drives the date dropdown
        get(Path.API_CONTEXT + "/dates", CacheController.fetchDates);

        // base currency
        get(Path.API_CONTEXT + "/baseccy", CacheController.fetchBaseCurrency);

        //top level summaries
        get(Path.API_CONTEXT + "/tree2/Total", CacheController.fetchNode2);
        get(Path.API_CONTEXT + "/tree2/Total/:creditrating", CacheController.fetchNode2);
        get(Path.API_CONTEXT + "/tree2/Total/:creditrating/:counterparty", CacheController.fetchNode2);
        get(Path.API_CONTEXT + "/tree2/Total/:creditrating/:counterparty/:nettingset", CacheController.fetchNode2);
        get(Path.API_CONTEXT + "/tree2/Total/:creditrating/:counterparty/:nettingset/:trade", CacheController.fetchNode2);

        get(Path.API_CONTEXT + "/tree/:hierarchy/:item", CacheController.fetchNode);
        get(Path.API_CONTEXT + "/bargraph/:date/:hierarchy/:metric", BarGraphController.fetchBarGraph);
        get(Path.API_CONTEXT + "/bargraph-tree/:date/:hierarchy/:item/:metric", BarGraphController.fetchBarGraphTree);
        get(Path.API_CONTEXT + "/xva/:date/:hierarchy/:metric", XvaController.fetchXva);
        get(Path.API_CONTEXT + "/xva-tree/:date/:hierarchy/:item/:metric", XvaController.fetchXvaTree);
        get(Path.API_CONTEXT + "/exposure-tree/:date/:hierarchy/:item", ExposureController.fetchExposureTree);
        get(Path.API_CONTEXT + "/totalexposure-tree/:hierarchy/:item", TotalExposureController.fetchTotalExposureTree);
        get(Path.API_CONTEXT + "/gauge-tree/:date/:hierarchy/:item/:metric", RiskGaugeController.fetchRiskGaugeTree);

        get(Path.API_CONTEXT + "/periscope/:hierarchy/:item", CacheController.fetchNodeParentStack);
        get(Path.API_CONTEXT + "/totalportfolio/:metric", LimitTableController.fetchTotalPortfolio);
        get(Path.API_CONTEXT + "/limitbreaches", LimitTableController.fetchLimitBreaches);

        exception(DashBoardException.class, (exception, request, response) -> {
            response.status(404);
            String ret = JsonUtil.dataToJson(exception.getMessage());
            response.body(ret);
        });

//        enableDebugScreen(); // go to /debug
//        enableRouteOverview(); // go to /debug/routeoverview

        //Set up after-filters (called after each get/post)
        // compress the responses to save bandwidth
//        after("*", Filters.addGzipHeader);

        // only required for websockets
        // init();

        try {
            cache = new Cache();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}

