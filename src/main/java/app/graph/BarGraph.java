
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// A container for all of the inputs necessary to render a bar graph in echarts.
// We might add more properties later.
public class BarGraph {

    private List<String> yAxisLabels;
    private List<Double> yAxisValues;
    private List<Double> yAxisLimits;
    private String titleText;
    private String subTitleText;
    private String seriesName;
    private String seriesType;

    // Convert a string from "20160301" to "01-MAR-2016"
    private static String convertDate(String date) {
        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate d = LocalDate.parse(date, f1);
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return d.format(f2);
    }

    public static BarGraph makeBarGraph(List<String> yAxisLabels, List<Double> yAxisValues, List<Double> yAxisLimits, String metric, String date) {
        String titleText = metric.toUpperCase();
        String subTitleText = Labels.getLabel(metric);
        String seriesName = convertDate(date);
        String seriesType = "bar";
        return new BarGraph(yAxisLabels, yAxisValues, yAxisLimits, titleText, subTitleText, seriesName, seriesType);
    }

    public BarGraph(List<String> yAxisLabels, List<Double> yAxisValues, List<Double> yAxisLimits) {
        this.yAxisLabels = yAxisLabels;
        this.yAxisValues = yAxisValues;
        this.yAxisLimits = yAxisLimits;
    }

    public BarGraph(List<String> yAxisLabels, List<Double> yAxisValues, List<Double> yAxisLimits, String titleText, String subTitleText, String seriesName, String seriesType) {
        this.yAxisLabels = yAxisLabels;
        this.yAxisValues = yAxisValues;
        this.yAxisLimits = yAxisLimits;
        this.titleText = titleText;
        this.subTitleText = subTitleText;
        this.seriesName = seriesName;
        this.seriesType = seriesType;
    }

    @Override
    public String toString() {
        return "BarGraph{" +
                "yAxisLabels=" + yAxisLabels +
                ", yAxisValues=" + yAxisValues +
                ", yAxisLimits=" + yAxisLimits +
                ", titleText='" + titleText + '\'' +
                ", subTitleText='" + subTitleText + '\'' +
                ", seriesName='" + seriesName + '\'' +
                ", seriesType='" + seriesType + '\'' +
                '}';
    }

    public List<String> getYAxisLabels() {
        return yAxisLabels;
    }

    public List<Double> getYAxisLimits() {
        return yAxisLimits;
    }

    public void setYAxisLimits(List<Double> yAxisLimits) {
        this.yAxisLimits = yAxisLimits;
    }

    public List<Double> getYAxisValues() {
        return yAxisValues;
    }

    public void setYAxisValues(List<Double> yAxisValues) {
        this.yAxisValues = yAxisValues;
    }

    public void setYAxisLabels(List<String> yAxisLabels) {
        this.yAxisLabels = yAxisLabels;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getSubTitleText() {
        return subTitleText;
    }

    public void setSubTitleText(String subTitleText) {
        this.subTitleText = subTitleText;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BarGraph barGraph = (BarGraph) o;

        if (!yAxisLabels.equals(barGraph.yAxisLabels)) return false;
        if (!yAxisValues.equals(barGraph.yAxisValues)) return false;
        if (!titleText.equals(barGraph.titleText)) return false;
        if (subTitleText != null ? !subTitleText.equals(barGraph.subTitleText) : barGraph.subTitleText != null)
            return false;
        if (seriesName != null ? !seriesName.equals(barGraph.seriesName) : barGraph.seriesName != null) return false;
        return seriesType.equals(barGraph.seriesType);

    }

    @Override
    public int hashCode() {
        int result = yAxisLabels.hashCode();
        result = 31 * result + yAxisValues.hashCode();
        result = 31 * result + titleText.hashCode();
        result = 31 * result + (subTitleText != null ? subTitleText.hashCode() : 0);
        result = 31 * result + (seriesName != null ? seriesName.hashCode() : 0);
        result = 31 * result + seriesType.hashCode();
        return result;
    }
}

