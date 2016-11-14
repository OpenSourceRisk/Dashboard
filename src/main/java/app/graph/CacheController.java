
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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static app.Application.cacheDao;
import static app.util.JsonUtil.dataToJson;

public class CacheController {

    public static Route fetchDates = (Request request, Response response) -> {
        try {
            response.status(200);
            response.type("application/json");
            return dataToJson(cacheDao.getDates());
        } catch (Exception e) {
            response.status(404);
            response.body(e.toString());
            response.type("application/json");
            return dataToJson(e.toString());
        }
    };

    public static Route fetchBaseCurrency = (Request request, Response response) -> {
        try {
            response.status(200);
            response.type("application/json");
            return dataToJson(cacheDao.getBaseCurrency());
        } catch (Exception e) {
            response.status(404);
            response.body(e.toString());
            response.type("application/json");
            return dataToJson(e.toString());
        }
    };

    public static Route fetchNodeLinks = (Request request, Response response) -> {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("links", cacheDao.getNodeLinks());
            return ViewUtil.render(request, model, "/velocity/graph/links.vm");
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.toString());
            return ViewUtil.render(request, model, "/velocity/graph/error.vm");
        }
    };

    public static Route fetchNodeLinks2 = (Request request, Response response) -> {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("links", cacheDao.getNodeLinks2());
            return ViewUtil.render(request, model, "/velocity/graph/links.vm");
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.toString());
            return ViewUtil.render(request, model, "/velocity/graph/error.vm");
        }
    };

    public static Route fetchNodeLinks3 = (Request request, Response response) -> {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("links", cacheDao.getNodeLinks3());
            return ViewUtil.render(request, model, "/velocity/graph/links.vm");
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.toString());
            return ViewUtil.render(request, model, "/velocity/graph/error.vm");
        }
    };

    public static Route fetchNode = (Request request, Response response) -> {
        try {
            String hierarchy = request.params("hierarchy");
            String item = request.params("item");
            response.status(200);
            response.type("application/json");
            return dataToJson(cacheDao.getNode(hierarchy, item));
        } catch (Exception e) {
            response.status(404);
            response.body(e.toString());
            response.type("application/json");
            return dataToJson(e.toString());
        }
    };

    public static Route fetchNode2 = (Request request, Response response) -> {
        try {

            String creditrating = request.params("creditrating");
            String counterparty = request.params("counterparty");
            String nettingset = request.params("nettingset");
            String trade = request.params("trade");

            Deque<String> stack = new ArrayDeque<>();
            if (creditrating != null && !creditrating.isEmpty()) {
                if (counterparty != null && !counterparty.isEmpty()) {
                    if (nettingset != null && !nettingset.isEmpty()) {
                        if (trade != null && !trade.isEmpty()) {
                            stack.push(trade);
                        }
                        stack.push(nettingset);
                    }
                    stack.push(counterparty);
                }
                stack.push(creditrating);
            }

            response.status(200);
            response.type("application/json");
            return dataToJson(cacheDao.getNode2(stack));
        } catch (Exception e) {
            response.status(404);
            response.body(e.toString());
            response.type("application/json");
            return dataToJson(e.toString());
        }
    };

    public static Route fetchNodeParentStack = (Request request, Response response) -> {
        try {
            // provide the parent stack of this node (ie the route that was taken)
            // eg creditrating/CCC
            // nettingset/CSA_12
            // etc
            String hierarchy = request.params("hierarchy");
            String item = request.params("item");

            response.status(200);
            response.type("application/json");
            return dataToJson(cacheDao.getNodeParentStack(hierarchy, item));
        } catch (Exception e) {
            response.status(404);
            response.body(e.toString());
            response.type("application/json");
            return dataToJson(e.toString());
        }
    };
}

