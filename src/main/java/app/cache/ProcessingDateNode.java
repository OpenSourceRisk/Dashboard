
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

import java.util.*;

import app.graph.*;
import app.report.Consumption;
import app.util.DashBoardException;

// Encapsulate a day's worth of data for the children of a Node.
class ProcessingDateNode extends ProcessingDate {

    private String name_;

    // Data
    private Metric metric_;
    private Exposure exposure_;

    // Rest endpoints
    private Set<String> barGraphTreeLinks_;
    private Set<String> xvaTreeLinks_;
    private Set<String> exposureTreeLinks_;
    private Set<String> riskGaugeTreeLinks_;

    public ProcessingDateNode(String date, String hierarchy, String name, Map<String, Node> children) {

        super(date, hierarchy, children);

        name_ = name;

        metric_ = new Metric(name);

        barGraphTreeLinks_ = new TreeSet<String>();
        xvaTreeLinks_ = new TreeSet<String>();
        exposureTreeLinks_ = new TreeSet<String>();
        riskGaugeTreeLinks_ = new TreeSet<String>();

        for (String s : Labels.METRICS) {
            barGraphTreeLinks_.add("bargraph-tree/" + date_ + "/" + hierarchy_ + "/" + name_ + "/" + s);
            riskGaugeTreeLinks_.add("gauge-tree/" + date_ + "/" + hierarchy_ + "/" + name_ + "/" + s);
        }
    }

    public void putMetric(String metric, Double value) throws DashBoardException {
        metric_.put(metric, value);
    }

    public Double getMetric(String metric) {
        return metric_.get(metric);
    }

    public void putXvaItem(String id, String metric, Double value) throws DashBoardException {
        if (!children_.containsKey(id))
            throw new DashBoardException("No node for data file: " + id);
        if (!xvaCache_.containsKey(metric))
            xvaCache_.put(metric, new Xva(metric));
        xvaCache_.get(metric).put(id, value);
        xvaTreeLinks_.add("xva-tree/" + date_ + "/" + hierarchy_ + "/" + name_ + "/" + metric);
    }

    void setExposure(Exposure e) {
        exposure_ = e;
        exposureTreeLinks_.add("exposure-tree/" + date_ + "/" + hierarchy_ + "/" + name_);
    }

    public Exposure getExposure() throws DashBoardException {
        if (exposure_ == null) {
            throw new DashBoardException("no exposure for node : " + name_);
        } else {
            return exposure_;
        }
    }

    public Consumption getRiskGauge(String metric, Double limit) throws DashBoardException {
        Double value = metric_.get(metric);
        return new Consumption(date_, name_, metric, value, limit);
    }

    public Set<String> getBarGraphTreeLinks() {
        return barGraphTreeLinks_;
    }

    public Set<String> getXvaTreeLinks() {
        return xvaTreeLinks_;
    }

    public Set<String> getExposureTreeLinks() {
        return exposureTreeLinks_;
    }

    public Set<String> getRiskGaugeTreeLinks() {
        return riskGaugeTreeLinks_;
    }
}

