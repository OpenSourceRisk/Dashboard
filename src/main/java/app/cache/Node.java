
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
import app.util.DashBoardException;
import app.report.Consumption;
import app.report.LimitTableEntry;

// A class to represent a node in the tree.
class Node {

    private Node parent_;
    private String hierarchy_;
    private String name_;
    private String path_;
    private Map<String, Node> children_;
    private List<NodeDTO> parentStack_;
    private TreeSet<String> dates_;
    private Map<String, ProcessingDateNode> processingDates_;
    private Metric limits_;
    private TotalExposure totalExposure_;

    public Node(Node parent, String hierarchy, String name, String path, TreeSet<String> dates) {

        parent_ = parent;
        hierarchy_ = hierarchy;
        name_ = name;
        path_ = path + "/" + name_;
        dates_ = dates;
        children_ = new TreeMap<String, Node>();
        if (parent_ == null) {
            parentStack_ = new ArrayList<>();
        } else {
            parentStack_ = new ArrayList<>(parent_.parentStack_);
            parentStack_.add(new NodeDTO(hierarchy_, name_, parentStack_.size()));
        }

        limits_ = new Metric(name_);
        totalExposure_ = new TotalExposure(name_, dates_);

        // The bar graph displays the top 5 items from a list.
        Map<String, Node> barGraphNodes;
        if (hierarchy_.equals("trade")) {
            // If this is a trade (leaf) node, just display "this" (one item) on the bar graph:
            barGraphNodes = new TreeMap<String, Node>();
            barGraphNodes.put(name, this);
        } else {
            // For netting set and above, the bar graph displays the top 5 child nodes:
            barGraphNodes = children_;
        }
        processingDates_ = new TreeMap<String, ProcessingDateNode>();
        for (String d : dates_)
            processingDates_.put(d, new ProcessingDateNode(d, hierarchy_, name_, barGraphNodes));
    }

    public Node addChild(String name, String hierarchy) {
        if (!children_.containsKey(name))
            children_.put(name, new Node(this, hierarchy, name, path_, dates_));
        return children_.get(name);
    }

    public Node getParent() {
        return parent_;
    }

    public String getName() {
        return name_;
    }

    public Set<String> getChildren() {
        return children_.keySet();
    }

    public List<NodeDTO> getParentStack() {
        return parentStack_;
    }

    public Set<String> getChildren(Deque<String> stack) throws DashBoardException {
        if (stack.isEmpty()) {
            return children_.keySet();
        } else {
            String child = stack.pop();
            if (children_.containsKey(child)) {
                return children_.get(child).getChildren(stack);
            } else {
                throw new DashBoardException("node " + path_ + " has no child " + child);
            }
        }
    }

    private ProcessingDateNode getProcessingDate(String date) throws DashBoardException {
        if (processingDates_.containsKey(date)) {
            return processingDates_.get(date);
        } else {
            throw new DashBoardException("invalid date: " + date);
        }
    }

    public void setLimits(Double npv, Double ce, Double eepe, Double totalexposure,
            Double colva, Double cva, Double dva, Double fca,
            Double fba, Double fva, Double im) throws DashBoardException {
        limits_.put("npv", npv);
        limits_.put("ce", ce);
        limits_.put("eepe", eepe);
        limits_.put("totalexposure", totalexposure);
        limits_.put("colva", colva);
        limits_.put("cva", cva);
        limits_.put("dva", dva);
        limits_.put("fca", fca);
        limits_.put("fba", fba);
        limits_.put("fva", fva);
        limits_.put("im", im);
        totalExposure_.setLimitCE(ce);
        totalExposure_.setLimitEEPE(eepe);
    }

    public Double getLimit(String metric) {
        return limits_.get(metric);
    }

    public void putMetric(String date, String metric, Double value) throws DashBoardException {
        getProcessingDate(date).putMetric(metric, value);
    }

    public Double getMetric(String date, String metric) throws DashBoardException {
        return getProcessingDate(date).getMetric(metric);
    }

    public BarGraph getBarGraph(String date, String metric) throws DashBoardException {
        return getProcessingDate(date).getBarGraph(metric);
    }

    public void putXvaItem(String date, String id, String metric, Double value) throws DashBoardException {
        getProcessingDate(date).putXvaItem(id, metric, value);
    }

    public Xva getXva(String date, String metric) throws DashBoardException {
        return getProcessingDate(date).getXva(metric);
    }

    public void setExposure(String date, Exposure e) throws DashBoardException {
        getProcessingDate(date).setExposure(e);
    }

    public Exposure getExposure(String date) throws DashBoardException {
        return getProcessingDate(date).getExposure();
    }

    public void totalExposureSetNPV(String date, Double npv) throws DashBoardException {
        totalExposure_.setNPV(date, npv);
    }

    public void totalExposureSetCE(String date, Double ce) throws DashBoardException {
        totalExposure_.setCE(date, ce);
    }

    public void totalExposureSetEEPE(String date, Double eepe) throws DashBoardException {
        totalExposure_.setEEPE(date, eepe);
    }

    public TotalExposure getTotalExposure() {
        return totalExposure_;
    }

    public Consumption getRiskGauge(String date, String metric) throws DashBoardException {
        return getProcessingDate(date).getRiskGauge(metric, limits_.get(metric));
    }

    // Endpoints: The getters below return all possible values for rest endpoints in this node.

    public String getNodeLink() {
        return "tree/" + hierarchy_ + "/" + name_;
    }

    public String getNodeLink2() {
        return "tree2" + path_;
    }

    public String getNodeLink3() {
        return "periscope/" + hierarchy_ + "/" + name_;
    }

    public Set<String> getBarGraphTreeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (ProcessingDateNode d : processingDates_.values())
            ret.addAll(d.getBarGraphTreeLinks());
        return ret;
    }

    public Set<String> getXvaTreeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (ProcessingDateNode d : processingDates_.values())
            ret.addAll(d.getXvaTreeLinks());
        return ret;
    }

    public Set<String> getExposureTreeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (ProcessingDateNode d : processingDates_.values())
            ret.addAll(d.getExposureTreeLinks());
        return ret;
    }

    public String getTotalExposureTreeLink() {
        return "totalexposure-tree/" + hierarchy_ + "/" + name_;
    }

    public Set<String> getRiskGaugeTreeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (ProcessingDateNode d : processingDates_.values())
            ret.addAll(d.getRiskGaugeTreeLinks());
        return ret;
    }

    // Populate the Limit Reports, for this node, for available metrics and dates.
    // 1) Total Portfolio: All consumption for all metrics.
    // 2) Limit Breaches: Only those cases where the metric value exceeds the limit.
    // The caller has passed in two structures, one for each report, and this function populates them.
    public void generateLimitTable(Map<String, List<LimitTableEntry> > totalPortfolio, List<LimitTableEntry> limitBreaches) {

        // Member variable parentStack_ contains the list of nodes leading from
        // the root node to this node.  The variable will have a size in between:
        // - zero, indicating that this is the root (i.e. Total) node, and
        // - four, indicating that this is a leaf (i.e. Trade) node.
        // Capture the list of node names into separate variables.

        String creditRating = "";
        String counterParty = "";
        String nettingSet = "";
        String trade = "";

        // Grab however many values there are in the stack (between zero and four):
        switch (parentStack_.size()) {
            case 4:
                trade = parentStack_.get(3).getItem();
            case 3:
                nettingSet = parentStack_.get(2).getItem();
            case 2:
                counterParty = parentStack_.get(1).getItem();
            case 1:
                creditRating = parentStack_.get(0).getItem();
        }

        // Now populate the Total Portfolio report and the Limit Breaches report.
        // Iterate through all possible metrics:
        for (String metric : Labels.METRICS) {

            // Capture this node's limit for the given metric:
            Double limit = limits_.get(metric);

            // If the limit has a value of NaN, it indicates that the limit is not relevant for this node.
            // (For example at trade level we only support CE and EEPE).
            if (Double.isNaN(limit))
                continue;

            // A list of all consumption items for this node (by metric and date):
            List<Consumption> consumptions = new ArrayList<Consumption>();
            // A list of only those consumption items where value > limit:
            List<Consumption> breaches = new ArrayList<Consumption>();

            // Iterate through the processing dates for this node:
            for (ProcessingDateNode d : processingDates_.values()) {

                // Grab the value for the given metric:
                Double value = d.getMetric(metric);

                // Create the relevant Consumption object:
                Consumption c = new Consumption(d.getDate(), name_, metric, value, limit);

                // Append this Consumption object to the list for the Total Portfolio:
                consumptions.add(c);

                // If the Consumption is a breach then also append it to the list for the Limit Breaches:
                if (c.getBreach())
                    breaches.add(c);
            }

            // Add the list of all consumption items to the relevant map entry (metric) for the Total Report:
            totalPortfolio.get(metric).add(new LimitTableEntry(creditRating, counterParty, nettingSet, trade, metric, limit, consumptions));

            // If the list of limit breaches is not empty then add it to the structure for the Limit Breaches report:
            if (!breaches.isEmpty())
                limitBreaches.add(new LimitTableEntry(creditRating, counterParty, nettingSet, trade, metric, limit, breaches));
        }
    }
}

