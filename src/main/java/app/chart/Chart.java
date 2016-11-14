
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

import java.util.Map;


/**
 * Created by quaternion on 27/07/2016.
 */
public class Chart {

    private final String title;
    private final Map<String, Double> labelsAndValues;
    private String busDate;

    public Chart(final String title, final Map<String, Double> labelsAndValues) {
        this.title = title;
        this.labelsAndValues = labelsAndValues;
    }

    public String getTitle() {
        return this.title;
    }

    public Map<String, Double> getLabelAndValue() {
        return labelsAndValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chart chart = (Chart) o;

        if (!title.equals(chart.title)) return false;
        if (!labelsAndValues.equals(chart.labelsAndValues)) return false;
        return busDate.equals(chart.busDate);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + labelsAndValues.hashCode();
        result = 31 * result + busDate.hashCode();
        return result;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Chart(title=" + this.getTitle() +  ")";
    }

}

