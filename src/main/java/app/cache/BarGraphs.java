
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

package app.cache;

import app.graph.BarGraph;
import app.util.DashBoardException;

import java.util.*;

// A class to facilitate sorting a list of items
// by one of their properties (a double).
class Item implements Comparable<Item> {

    public String key_;
    public Double value_;
    public Double limit_;

    Item(String key, Double value, Double limit) {
        key_ = key;
        value_ = value;
        limit_ = limit;
    }

    public int compareTo(Item i) {
        return Double.compare(i.value_, value_);
    }
}

// Class BarGraphs
// Contains a list of Nodes.
// Each Node holds a Metric, a map of values.
// This class selects the top 5 nodes for a given metric and returns these in
// the form of a BarGraph object for consumption by echarts.
public class BarGraphs {

    private String date_;
    private Map<String, Node> nodes_;
    private HashMap<String, BarGraph> barGraphs_;

    public BarGraphs(Map<String, Node> nodes, String date) {
        nodes_ = nodes;
        date_ = date;
        barGraphs_ = new HashMap<String, BarGraph>();
    }

    // Lazy initialize the top 5 items in the list for the given metric
    // and generate the inputs necessary for the corresponding bar graph in echarts.
    public BarGraph getBarGraph(String metric) throws DashBoardException {
        if (!barGraphs_.containsKey(metric)) {
            List<Item> l = new ArrayList<Item>();
            for (Node n : nodes_.values())
                l.add(new Item(n.getName(), n.getMetric(date_, metric), n.getLimit(metric)));
            Collections.sort(l);
            List<String> yAxisLabels = new ArrayList<String>();
            List<Double> yAxisValues = new ArrayList<Double>();
            List<Double> yAxisLimits = new ArrayList<Double>();
            int n=0;
            for (Item i : l) {
                yAxisLabels.add(i.key_);
                yAxisValues.add(i.value_);
                yAxisLimits.add(i.limit_);
                if (n++>3) break;
            }
            barGraphs_.put(metric, BarGraph.makeBarGraph(yAxisLabels, yAxisValues, yAxisLimits, metric, date_));
        }
        return barGraphs_.get(metric);
    }
}

