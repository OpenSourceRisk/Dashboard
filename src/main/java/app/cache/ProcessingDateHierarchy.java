
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

import app.graph.Labels;
import app.graph.Xva;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

// Encapsulate a day's worth of data for the nodes in a hierarchy.
class ProcessingDateHierarchy extends ProcessingDate {

    // Rest endpoints
    private Set<String> barGraphLinks_;
    private Set<String> xvaLinks_;

    public ProcessingDateHierarchy(String date, String hierarchy, Map<String, Node> children) {

        super(date, hierarchy, children);

        barGraphLinks_ = new TreeSet<String>();
        xvaLinks_ = new TreeSet<String>();

        for (String s : Labels.METRICS)
            barGraphLinks_.add("bargraph/" + date_ + "/" + hierarchy_ + "/" + s);
    }

    void putXva(String s, Xva x) {
        xvaCache_.put(s, x);
        xvaLinks_.add("xva/" + date_ + "/" + hierarchy_ + "/" + s);
    }

    public Set<String> getBarGraphLinks() {
        return barGraphLinks_;
    }

    public Set<String> getXvaLinks() {
        return xvaLinks_;
    }
}

