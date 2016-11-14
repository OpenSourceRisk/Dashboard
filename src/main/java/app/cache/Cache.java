
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

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import app.graph.*;
import app.report.*;
import app.load.File;
import app.util.DashBoardException;
import static app.cache.Utils.*;

// A class to encapsulate the data cache.
public class Cache implements Serializable {

    // A list of the available processing dates.
    private TreeSet<String> dates_;
    // The root node of the tree.
    private Node root_;
    // A list of Hierarchy objects - one for each of the 5 levels of the tree.
    private Map<String, Hierarchy> cache_;

    // Base currency - this gets set in Load.loadNpv().
    public static String baseCurrency = null;

    // For each rest endpoint, we implement a container holding all possible values for that endpoint.
    // These containers are used to generate a web page containing links to every item in the cache.
    // This functionality is provided only for debugging purposes, it is not used by the client,
    // which accesses the rest endpoints programatically.
    private Set<String> nodeLinks_;
    private Set<String> nodeLinks2_;
    private Set<String> nodeLinks3_;
    private Set<String> barGraphLinks_;
    private Set<String> barGraphTreeLinks_;
    private Set<String> xvaLinks_;
    private Set<String> xvaTreeLinks_;
    private Set<String> exposureTreeLinks_;
    private Set<String> totalExposureTreeLinks_;
    private Set<String> riskGaugeTreeLinks_;
    private Set<String> totalPortfolioLinks_;

    // List of limits and breaches.
    private Map<String, List<LimitTableEntry> > totalPortfolio_;
    private List<LimitTableEntry> limitBreaches_;

    // Initialize the cache at startup.
    public Cache() throws DashBoardException, IOException {

        System.out.println("Initializing cache...");

        // Derive the list of processing dates.
        app.load.Scanner s = new app.load.Scanner();
        dates_ = s.scan(DATA_DIR + "(\\d{8})");
        // or to test:
        //dates_ = new TreeSet<String>();
        //dates_.add("20160628");
        //dates_.add("20160629");
        //dates_.add("20160630");

        configureMemory();

        // Instantiate one Hierarchy object for each of the 5 levels in the tree.
        cache_ = new HashMap<String, Hierarchy>();
        cache_.put("total", new Hierarchy("total", dates_));
        cache_.put("creditrating", new Hierarchy("creditrating", dates_));
        cache_.put("counterparty", new Hierarchy("counterparty", dates_));
        cache_.put("nettingset", new Hierarchy("nettingset", dates_));
        cache_.put("trade", new Hierarchy("trade", dates_));

        // Instantiate the tree structure from the input data.
        root_ = new Node(null, "total", "Total", "", dates_);
        cache_.get("total").putNode("Total", root_);
        File f = new File(DATA_DIR + "hierarchies.csv");
        for (String[] l : f.data_)
            addNode(l[0], l[1], l[2], l[3]);

        // Load the data from each processing date into the cache.
        int i = 1;
        for (String d : dates_) {
            System.out.println("Loading date " + d + " (" + i++ + " of " + dates_.size() + ")...");
            new Load(this, d);
        }
        System.out.println("Loading limits...");
        loadLimits();

        System.out.println("Generating limit table...");
        generateLimitTable();

        System.out.println("Generating links for rest endpoints...");

        // Instantiate the containers for the rest endpoints.
        nodeLinks_ = new TreeSet<String>();
        nodeLinks2_ = new TreeSet<String>();
        nodeLinks3_ = new TreeSet<String>();
        barGraphLinks_ = new TreeSet<String>();
        barGraphTreeLinks_ = new TreeSet<String>();
        xvaLinks_ = new TreeSet<String>();
        xvaTreeLinks_ = new TreeSet<String>();
        exposureTreeLinks_ = new TreeSet<String>();
        totalExposureTreeLinks_ = new TreeSet<String>();
        riskGaugeTreeLinks_ = new TreeSet<String>();
        totalPortfolioLinks_ = new TreeSet<String>();

        // Populate the containers.
        for (Hierarchy h : cache_.values()) {
            nodeLinks_.addAll(h.getNodeLinks());
            nodeLinks2_.addAll(h.getNodeLinks2());
            nodeLinks3_.addAll(h.getNodeLinks3());
            barGraphLinks_.addAll(h.getBarGraphLinks());
            barGraphTreeLinks_.addAll(h.getBarGraphTreeLinks());
            xvaLinks_.addAll(h.getXvaLinks());
            xvaTreeLinks_.addAll(h.getXvaTreeLinks());
            exposureTreeLinks_.addAll(h.getExposureTreeLinks());
            totalExposureTreeLinks_.addAll(h.getTotalExposureTreeLinks());
            riskGaugeTreeLinks_.addAll(h.getRiskGaugeTreeLinks());
        }
        for (String l : Labels.METRICS)
            totalPortfolioLinks_.add("totalportfolio/" + l);

        System.out.println("Cache initialization done.");
    }

    // Check how much RAM is available to the JRE.
    // If there is less than half a gig, then truncate the list of dates to ten.
    // For now if there is more than half a gig then load all available data.
    private final int MAX_DATES=10;
    private final int RAM_LIMIT=500000000;
    private void configureMemory() {
        if (dates_.size() <= MAX_DATES)
            return;
        long availableRAM = Runtime.getRuntime().maxMemory();
        System.out.println("RAM=" + availableRAM);
        if (availableRAM < RAM_LIMIT) {
            System.out.println("RAM<" + RAM_LIMIT + " - loading only " + MAX_DATES + " of " + dates_.size() + " available dates.");
            TreeSet<String> dates2 = new TreeSet<String>();
            int i = 1;
            for (String d : dates_) {
                dates2.add(d);
                i++;
                if (i>MAX_DATES)
                    break;
            }
            dates_ = dates2;
        } else {
            System.out.println("RAM>" + RAM_LIMIT + " - loading all " + dates_.size() + " available dates.");
        }
    }

    private void addNode(String creditrating, String counterparty, String nettingset, String trade) {

        // Add the nodes to the tree.
        Node creditratingNode = root_.addChild(creditrating, "creditrating");
        Node counterpartyNode = creditratingNode.addChild(counterparty, "counterparty");
        Node nettingsetNode = counterpartyNode.addChild(nettingset, "nettingset");
        Node tradeNode = nettingsetNode.addChild(trade, "trade");

        // Add the nodes to the hierarchies.
        cache_.get("creditrating").putNode(creditrating, creditratingNode);
        cache_.get("counterparty").putNode(counterparty, counterpartyNode);
        cache_.get("nettingset").putNode(nettingset, nettingsetNode);
        cache_.get("trade").putNode(trade, tradeNode);
    }

    // A private wrapper around access to the hierarchy list.
    private Hierarchy getHierarchy(String hierarchy) throws DashBoardException {
        if (cache_.containsKey(hierarchy)) {
            return cache_.get(hierarchy);
        } else {
            throw new DashBoardException("invalid hierarchy: " + hierarchy);
        }
    }

    private void loadLimits() throws IOException, DashBoardException {
        loadLimitsImpl("total");
        loadLimitsImpl("creditrating");
        loadLimitsImpl("counterparty");
        loadLimitsImpl("nettingset");
        loadLimitsImpl("trade");
    }

    private void loadLimitsImpl(String hierarchy) throws IOException, DashBoardException {

        // Load the limits file.

        File f = new File(DATA_DIR + "limits_" + hierarchy + ".csv");
        for (String[] fields : f.data_) {

            // Extract the relevant values from the line.
            String id = fields[0];
            Double npv = parseDouble(fields[1]);
            Double ce = parseDouble(fields[2]);
            Double eepe = parseDouble(fields[3]);
            Double totalexposure = parseDouble(fields[4]);
            Double colva = parseDouble(fields[5]);
            Double cva = parseDouble(fields[6]);
            Double dva = parseDouble(fields[7]);
            Double fca = parseDouble(fields[8]);
            Double fba = parseDouble(fields[9]);
            Double fva = parseDouble(fields[10]);
            Double im = parseDouble(fields[11]);

            // Attach the values to the relevant node in the tree.
            getHierarchy(hierarchy).setLimits(id, npv, ce, eepe, totalexposure, colva, cva, dva, fca, fba, fva, im);
        }
    }

    // LOAD: The methods below are used by class Load to copy data from disk into memory.

    public void putMetric(String hierarchy, String id, String date, String metric, Double value) throws DashBoardException {
        getHierarchy(hierarchy).putMetric(id, date, metric, value);
    }

    public void putXvaItem(String hierarchy, String date, String id, String metric, Double value) throws DashBoardException {
        getHierarchy(hierarchy).putXvaItem(date, id, metric, value);
    }

    public void putXva(String hierarchy, String date, String metric, Xva xva) throws DashBoardException {
        getHierarchy(hierarchy).putXva(date, metric, xva);
    }

    public void totalExposureSetNPV(String hierarchy, String date, String id, Double npv) throws DashBoardException {
        getHierarchy(hierarchy).totalExposureSetNPV(date, id, npv);
    }

    public void totalExposureSetCE(String hierarchy, String date, String id, Double ce) throws DashBoardException {
        getHierarchy(hierarchy).totalExposureSetCE(date, id, ce);
    }

    public void totalExposureSetEEPE(String hierarchy, String date, String id, Double eepe) throws DashBoardException {
        getHierarchy(hierarchy).totalExposureSetEEPE(date, id, eepe);
    }

    public void setExposure(String hierarchy, String date, String id, Exposure e) throws DashBoardException {
        getHierarchy(hierarchy).setExposure(date, id, e);
    }

    // Set the "Exposure Profile" (not the "Total Exposure") for the root node.
    // This serves as the Exposure Profile value for hierarchy 0 ("total")
    // and for the root node of the tree ("Total").
    public void setTotalExposure(String date, Exposure e) throws DashBoardException {
        root_.setExposure(date, e);
    }

    // LIMIT TABLE: Functionality for the limit table report.

    private void generateLimitTable() {
        totalPortfolio_ = new HashMap<String, List<LimitTableEntry> >();
        for (String metric : Labels.METRICS)
            totalPortfolio_.put(metric, new ArrayList<LimitTableEntry>());
        limitBreaches_ = new ArrayList<LimitTableEntry>();
        for (Hierarchy h : cache_.values())
            h.generateLimitTable(totalPortfolio_, limitBreaches_);
    }

    public List<LimitTableEntry> getTotalPortfolio(String metric) throws DashBoardException {
        if (totalPortfolio_.containsKey(metric)) {
            return totalPortfolio_.get(metric);
        } else {
            throw new DashBoardException("invalid metric: " + metric);
        }
    }

    public List<LimitTableEntry> getLimitBreaches() {
        return limitBreaches_;
    }

    // LINKS: Each of the getters below returns a list of all valid links for the relevant rest endpoint.

    public Set<String> getDates() {
        return dates_;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Set<String> getNodeLinks() {
        return nodeLinks_;
    }

    public Set<String> getNodeLinks2() {
        return nodeLinks2_;
    }

    public Set<String> getNodeLinks3() {
        return nodeLinks3_;
    }

    public Set<String> getBarGraphLinks() {
        return barGraphLinks_;
    }

    public Set<String> getBarGraphTreeLinks() {
        return barGraphTreeLinks_;
    }

    public Set<String> getXvaLinks() {
        return xvaLinks_;
    }

    public Set<String> getXvaTreeLinks() {
        return xvaTreeLinks_;
    }

    public Set<String> getExposureTreeLinks() {
        return exposureTreeLinks_;
    }

    public Set<String> getTotalExposureTreeLinks() {
        return totalExposureTreeLinks_;
    }

    public Set<String> getRiskGaugeTreeLinks() {
        return riskGaugeTreeLinks_;
    }

    public Set<String> getTotalPortfolioLinks() {
        return totalPortfolioLinks_;
    }

    // TREE: The getters below return data relating to the structure of the tree,
    // for use in navigating the hierarchy.

    public Set<String> getNode(String hierarchy, String item) throws DashBoardException {
        return getHierarchy(hierarchy).getNodeChildren(item);
    }

    public Set<String> getNode2(Deque<String> stack) throws DashBoardException {
        return root_.getChildren(stack);
    }

    public List<NodeDTO> getNodeParentStack(String hierarchy, String item) throws DashBoardException {
        return getHierarchy(hierarchy).getNodeParentStack(item);
    }

    // DATA: The getters below retrieve the various data structures from the tree for display in the client.

    public BarGraph getBarGraph(String date, String hierarchy, String metric) throws DashBoardException {
        return getHierarchy(hierarchy).getBarGraph(date, metric);
    }

    public BarGraph getBarGraphTree(String date, String hierarchy, String item, String metric) throws DashBoardException {
        return getHierarchy(hierarchy).getBarGraphTree(date, item, metric);
    }

    public Xva getXva(String date, String hierarchy, String metric) throws DashBoardException {
        return getHierarchy(hierarchy).getXva(date, metric);
    }

    public Xva getXvaTree(String date, String hierarchy, String item, String metric) throws DashBoardException {
        return getHierarchy(hierarchy).getXvaTree(date, item, metric);
    }

    public Exposure getExposureTree(String date, String hierarchy, String item) throws DashBoardException {
        return getHierarchy(hierarchy).getExposureTree(date, item);
    }

    public TotalExposure getTotalExposureTree(String hierarchy, String item) throws DashBoardException {
        return getHierarchy(hierarchy).getTotalExposureTree(item);
    }

    public Consumption getRiskGaugeTree(String date, String hierarchy, String item, String metric) throws DashBoardException {
        return getHierarchy(hierarchy).getRiskGaugeTree(date, item, metric);
    }
}

