
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

import java.util.HashMap;
import java.util.Map;

import app.graph.BarGraph;
import app.graph.Xva;
import app.util.DashBoardException;

// Encapsulate a day's worth of data for a list of nodes.
class ProcessingDate {

    protected String date_;
    protected String hierarchy_;
    protected Map<String, Node> children_;

    protected BarGraphs barGraphs_;
    protected Map<String, Xva> xvaCache_;

    public ProcessingDate(String date, String hierarchy, Map<String, Node> children) {

        date_ = date;
        hierarchy_ = hierarchy;
        children_ = children;

        barGraphs_ = new BarGraphs(children_, date_);
        xvaCache_ = new HashMap<String, Xva>();
    }

    public String getDate() {
        return date_;
    }

    public BarGraph getBarGraph(String metric) throws DashBoardException {
        return barGraphs_.getBarGraph(metric);
    }

    public Xva getXva(String metric) throws DashBoardException {
        if (xvaCache_.containsKey(metric)) {
            return xvaCache_.get(metric);
        } else {
            throw new DashBoardException("invalid xva metric: " + metric);
        }
    }
}

