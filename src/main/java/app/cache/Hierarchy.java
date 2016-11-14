
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
import app.report.LimitTableEntry;

// The Hierarchy class contains all of the nodes for a given level in the tree.
public class Hierarchy {

    private String name_;
    private Map<String, Node> nodes_;
    private Set<String> dates_;
    private Map<String, ProcessingDateHierarchy> processingDates_;

    public Hierarchy(String name, Set<String> dates) {
        name_ = name;
        nodes_ = new TreeMap<String, Node>();
        dates_ = dates;

        processingDates_ = new TreeMap<String, ProcessingDateHierarchy>();
        for (String d : dates_)
            processingDates_.put(d, new ProcessingDateHierarchy(d, name_, nodes_));
    }

    public void putNode(String s, Node n) {
        if (!nodes_.containsKey(s))
            nodes_.put(s, n);
    }

    private Node getNode(String item) throws DashBoardException {
        if (nodes_.containsKey(item)) {
            return nodes_.get(item);
        } else {
            throw new DashBoardException("Hierarchy '" + name_ + "' does not contain item '" + item + "'");
        }
    }

    public Collection<Node> getNodes() {
        return nodes_.values();
    }

    private ProcessingDateHierarchy getProcessingDate(String date) throws DashBoardException {
        if (processingDates_.containsKey(date)) {
            return processingDates_.get(date);
        } else {
            throw new DashBoardException("invalid date: " + date);
        }
    }

    // Return the children of the given node.
    public Set<String> getNodeChildren(String item) throws DashBoardException {
        return getNode(item).getChildren();
    }

    // Return the path to the given node.
    public List<NodeDTO> getNodeParentStack(String item) throws DashBoardException {
        return getNode(item).getParentStack();
    }

    // LOAD: The methods below are called at startup to copy data from disk into memory.

    public void setLimits(String item, Double npv, Double ce, Double eepe,
            Double totalexposure, Double colva, Double cva, Double dva, Double fca,
            Double fba, Double fva, Double im) throws DashBoardException {
        getNode(item).setLimits(npv, ce, eepe, totalexposure, colva, cva, dva, fca, fba, fva, im);
    }

    public void putMetric(String item, String date, String metric, Double value) throws DashBoardException {
        getNode(item).putMetric(date, metric, value);
    }

    public void putXvaItem(String date, String id, String metric, Double value) throws DashBoardException {
        getNode(id).getParent().putXvaItem(date, id, metric, value);
    }

    public void putXva(String date, String s, Xva x) throws DashBoardException {
        getProcessingDate(date).putXva(s, x);
    }

    public void totalExposureSetNPV(String date, String item, Double npv) throws DashBoardException {
        getNode(item).totalExposureSetNPV(date, npv);
    }

    public void totalExposureSetCE(String date, String item, Double ce) throws DashBoardException {
        getNode(item).totalExposureSetCE(date, ce);
    }

    public void totalExposureSetEEPE(String date, String item, Double eepe) throws DashBoardException {
        getNode(item).totalExposureSetEEPE(date, eepe);
    }

    public void setExposure(String date, String id, Exposure e) throws DashBoardException {
        getNode(id).setExposure(date, e);
    }

    // HIERARCHY DATA: The getters below retreive the various data structures from the hierarchy for display in the client.

    public BarGraph getBarGraph(String date, String metric) throws DashBoardException {
        return getProcessingDate(date).getBarGraph(metric);
    }

    public Xva getXva(String date, String metric) throws DashBoardException {
        return getProcessingDate(date).getXva(metric);
    }

    // TREE DATA: The getters below retreive data from the children of a selected node for display in the client.

    public BarGraph getBarGraphTree(String date, String item, String metric) throws DashBoardException {
        return getNode(item).getBarGraph(date, metric);
    }

    public Xva getXvaTree(String date, String item, String metric) throws DashBoardException {
        return getNode(item).getXva(date, metric);
    }

    public Exposure getExposureTree(String date, String item) throws DashBoardException {
        return getNode(item).getExposure(date);
    }

    public TotalExposure getTotalExposureTree(String item) throws DashBoardException {
        return getNode(item).getTotalExposure();
    }

    public Consumption getRiskGaugeTree(String date, String item, String metric) throws DashBoardException {
        return getNode(item).getRiskGauge(date, metric);
    }

    // ENDPOINTS: The getters below return all possible values for rest endpoints in this hierarchy.

    public Set<String> getNodeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (Node n : nodes_.values())
            ret.add(n.getNodeLink());
        return ret;
    }

    public Set<String> getNodeLinks2() {
        Set<String> ret = new TreeSet<String>();
        for (Node n : nodes_.values())
            ret.add(n.getNodeLink2());
        return ret;
    }

    public Set<String> getNodeLinks3() {
        Set<String> ret = new TreeSet<String>();
        for (Node n : nodes_.values())
            ret.add(n.getNodeLink3());
        return ret;
    }

    public Set<String> getBarGraphLinks() {
        Set<String> ret = new TreeSet<String>();
        for (ProcessingDateHierarchy d : processingDates_.values())
            ret.addAll(d.getBarGraphLinks());
        return ret;
    }

    public Set<String> getBarGraphTreeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (Node n : nodes_.values())
            ret.addAll(n.getBarGraphTreeLinks());
        return ret;
    }

    public Set<String> getXvaLinks() {
        Set<String> ret = new TreeSet<String>();
        for (ProcessingDateHierarchy d : processingDates_.values())
            ret.addAll(d.getXvaLinks());
        return ret;
    }

    public Set<String> getXvaTreeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (Node n : nodes_.values())
            ret.addAll(n.getXvaTreeLinks());
        return ret;
    }

    public Set<String> getExposureTreeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (Node n : nodes_.values())
            ret.addAll(n.getExposureTreeLinks());
        return ret;
    }

    public Set<String> getTotalExposureTreeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (Node n : nodes_.values())
            ret.add(n.getTotalExposureTreeLink());
        return ret;
    }

    public Set<String> getRiskGaugeTreeLinks() {
        Set<String> ret = new TreeSet<String>();
        for (Node n : nodes_.values())
            ret.addAll(n.getRiskGaugeTreeLinks());
        return ret;
    }

    public void generateLimitTable(Map<String, List<LimitTableEntry> > totalPortfolio, List<LimitTableEntry> limitBreaches) {
        for (Node n : nodes_.values())
            n.generateLimitTable(totalPortfolio, limitBreaches);
    }
}

