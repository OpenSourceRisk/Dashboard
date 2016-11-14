
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

package app.chart;

import app.util.JsonTransformer;
import app.util.Path;

import static spark.Spark.get;

/**
 * Created by quaternion on 22/07/2016.
 */
public class ChartResource {

    private final ChartDao todoService;

    public ChartResource(ChartDao todoService) {
        this.todoService = todoService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        get(Path.API_CONTEXT + "/charts/:id", "application/json", (request, response)

                -> todoService.getChartByTitle(request.params(":id")), new JsonTransformer());

        get(Path.API_CONTEXT + "/charts", "application/json", (request, response)

                -> todoService.getAllCharts(), new JsonTransformer());

    }
}
