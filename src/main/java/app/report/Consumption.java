
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

public class Consumption {

    private String date_;
    private String name_;
    private String metric_;
    private Double value_;
    private Double limit_;
    private Double consumption_;
    private Boolean breach_;

    public Consumption(String date, String name, String metric, Double value, Double limit) {
    
        date_ = date;
        name_ = name;
        metric_ = metric;
        value_ = value;
        limit_ = limit;

        if (0 == limit_) {
            consumption_ = 0.;
            breach_ = false;
        } else {
            // Values and limits are negative for FCA, FBA, FVA.
            // So calculate consumption based on the absolute value of limit and value.
            // Maybe we should throw an exception or something if limit and value
            // have different signs but that does not occur in the current data set.
            consumption_ = Math.abs(value_) / Math.abs(limit_) * 100.;
            breach_ = Math.abs(value_) > Math.abs(limit_);
        }
    }

    public String getDate() {
        return date_;
    }

    public String getName() {
        return name_;
    }

    public String getMetric() {
        return metric_;
    }

    public Double getValue() {
        return value_;
    }

    public Double getLimit() {
        return limit_;
    }

    public Double getConsumption() {
        return consumption_;
    }

    public Boolean getBreach() {
        return breach_;
    }
}

