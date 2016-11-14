
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

import java.util.HashMap;
import java.util.Map;

// This file is a central location for static labels and descriptions.
public class Labels {

    public static String[] METRICS = {
        "npv", "ce", "eepe", "totalexposure",           // npv
        "colva", "cva", "dva", "fca", "fba", "fva",     // xva
        "im"                                            // im
    };

    private static final Map<String, String> LABELS;
    static {
        LABELS = new HashMap<String, String>();
        LABELS.put("ce", "Current Exposure");
        LABELS.put("colva", "Collateral Valuation Adjustment");
        LABELS.put("cva", "Credit Valuation Adjustment");
        LABELS.put("dva", "Debit Valuation Adjustment");
        LABELS.put("eepe", "Effective Expected Positive Exposure");
        LABELS.put("fba", "Funding Benefit Adjustment");
        LABELS.put("fca", "Funding Cost Adjustment");
        LABELS.put("fva", "Funding Value Adjustment");
        LABELS.put("im", "Initial Margin");
        LABELS.put("npv", "Net Present Value");
        LABELS.put("totalexposure", "Total Exposure");
    }

    public static String getLabel(String metric) {
        return LABELS.get(metric);
    }
}

