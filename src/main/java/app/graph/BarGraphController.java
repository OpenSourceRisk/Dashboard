
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

package app.graph;

import app.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.Application.barGraphDao;
import static app.util.JsonUtil.dataToJson;

public class BarGraphController {

    public static Route fetchBarGraphLinks = (Request request, Response response) -> {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("links", barGraphDao.getBarGraphLinks());
            return ViewUtil.render(request, model, "/velocity/graph/links.vm");
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.toString());
            return ViewUtil.render(request, model, "/velocity/graph/error.vm");
        }
    };

    public static Route fetchBarGraph = (Request request, Response response) -> {
        try {
            String date = request.params("date");
            String hierarchy = request.params("hierarchy");
            String metric = request.params("metric");
            List<String> ret = new ArrayList<String>();
            response.status(200);
            response.type("application/json");
            return dataToJson(barGraphDao.getBarGraph(date, hierarchy, metric));
        } catch (Exception e) {
            response.status(404);
            response.body(e.toString());
            response.type("application/json");
            return dataToJson(e.toString());
        }
    };

    public static Route fetchBarGraphTreeLinks = (Request request, Response response) -> {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("links", barGraphDao.getBarGraphTreeLinks());
            return ViewUtil.render(request, model, "/velocity/graph/links.vm");
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.toString());
            return ViewUtil.render(request, model, "/velocity/graph/error.vm");
        }
    };

    public static Route fetchBarGraphTree = (Request request, Response response) -> {
        try {
            String date = request.params("date");
            String hierarchy = request.params("hierarchy");
            String item = request.params("item");
            String metric = request.params("metric");
            response.status(200);
            response.type("application/json");
            return dataToJson(barGraphDao.getBarGraphTree(date, hierarchy, item, metric));
        } catch (Exception e) {
            response.status(404);
            response.body(e.toString());
            response.type("application/json");
            return dataToJson(e.toString());
        }
    };
}

