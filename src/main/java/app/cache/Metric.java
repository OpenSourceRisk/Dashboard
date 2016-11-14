
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
import app.util.DashBoardException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Class Metric
// Wraps a map of doubles, representing the scalar values that can
// be attached to each Node in the Tree.
class Metric {

    private String name_;
    private Map<String, Double> data_;

    public Metric(String name) {
        name_ = name;
        data_ = new HashMap<String, Double>();
        for (String m : Labels.METRICS)
            data_.put(m, Double.NaN);
    }

    public void put(String key, Double value) throws DashBoardException {
        if (!Arrays.asList(Labels.METRICS).contains(key))
            throw new DashBoardException("invalid metric: " + key);
        data_.put(key, value);
    }

    public Double get(String key) {
        return data_.get(key);
    }
}

