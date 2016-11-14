
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

package app.report;

import java.util.*;

import spark.Request;
import spark.Response;
import spark.Route;

import app.util.ViewUtil;
import static app.Application.limitTableDao;
import static app.util.JsonUtil.dataToJson;

/**
 * Created by quaternion on 16/09/2016.
 */
public class LimitTableController {

    public static Route fetchTotalPortfolioLinks = (Request request, Response response) -> {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("links", limitTableDao.getTotalPortfolioLinks());
            return ViewUtil.render(request, model, "/velocity/graph/links.vm");
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.toString());
            return ViewUtil.render(request, model, "/velocity/graph/error.vm");
        }
    };

    public static Route fetchTotalPortfolio = (Request request, Response response) -> {
        try {
            String metric = request.params("metric");
            response.status(200);
            response.type("application/json");
            return dataToJson(limitTableDao.getTotalPortfolio(metric));
        } catch (Exception e) {
            response.status(404);
            response.body(e.toString());
            response.type("application/json");
            return dataToJson(e.toString());
        }
    };

    public static Route fetchLimitBreaches = (Request request, Response response) -> {
        try {
            response.status(200);
            response.type("application/json");
            return dataToJson(limitTableDao.getLimitBreaches());
        } catch (Exception e) {
            response.status(404);
            response.body(e.toString());
            response.type("application/json");
            return dataToJson(e.toString());
        }
    };
}

