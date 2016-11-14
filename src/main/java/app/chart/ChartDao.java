
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

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by quaternion on 17/07/2016.
 */
public class ChartDao {

    Map<String, Double> data = new HashMap<>();

    private final List<Chart> charts = ImmutableList.of(

            new Chart("CVA Line Chart", data),
            new Chart("FVA Bar chart",data)
    );

    public Iterable<Chart> getAllCharts() {
        return charts;
    }

    public Chart getChartByTitle(String title) {
        return charts.stream().filter(b -> b.getTitle().equals(title)).findFirst().orElse(null);
    }

    public Chart getRandomChart() {
        return charts.get(new Random().nextInt(charts.size()));
    }

}
