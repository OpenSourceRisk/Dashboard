
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

import java.util.List;

public class LimitTableEntry {

    private String creditRating_;
    private String counterParty_;
    private String nettingSet_;
    private String trade_;
    private String metric_;
    Double limit_;
    private List<Consumption> consumptions_;

    public LimitTableEntry(String creditRating, String counterParty,
            String nettingSet, String trade, String metric, Double limit,
            List<Consumption> consumptions) {
        creditRating_ = creditRating;
        counterParty_ = counterParty;
        nettingSet_ = nettingSet;
        trade_ = trade;
        metric_ = metric;
        limit_ = limit;
        consumptions_ = consumptions;
    }

    public String getCreditRating() {
        return creditRating_;
    }

    public String getCounterParty() {
        return counterParty_;
    }

    public String getNettingSet() {
        return nettingSet_;
    }

    public String getTrade() {
        return trade_;
    }

    public String getMetric() {
        return metric_;
    }

    public Double getLimit() {
        return limit_;
    }

    public List<Consumption> getConsumptions() {
        return consumptions_;
    }
}

