
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

import app.graph.Exposure;
import app.graph.Xva;
import app.load.File;
import app.util.DashBoardException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.cache.Cache.baseCurrency;
import static app.cache.Utils.*;

// Copy data from disk to memory.
// This class is functionally a part of class Cache, it has been separated out for clarity.
// Class Load calls certain setters in class Cache in order to write the data to memory.
public class Load {

    private Cache cache_;
    private String date_;

    public Load(Cache cache, String date) throws DashBoardException, IOException {

        cache_ = cache;
        date_ = date;

        // Total

        loadNpv("total");
        loadXvaTotal();
        loadExposureTotal();

        // Creditrating

        loadNpv("creditrating");
        //loadIM("creditrating");
        loadXva("creditrating");
        loadExposure("creditrating");

        // Counterparty

        loadNpv("counterparty");
        //loadIM("counterparty");
        loadXva("counterparty");
        loadExposure("counterparty");

        // Nettingset

        loadNpv("nettingset");
        //loadIM("nettingset");
        loadXvaNettingset();
        loadExposure("nettingset");

        // Trade

        loadNpv("trade");
        loadExposure("trade");
    }

    private void loadNpv(String hierarchy) throws IOException, DashBoardException {

        // Load the NPV file.

        File f = new File(DATA_DIR + date_ + "/npv_" + hierarchy + ".csv");
        for (String[] fields : f.data_) {

            // Extract the relevant values from the line.
            String id = fields[0];
            Double npv = parseDouble(fields[6]);
            Double ce = parseDouble(fields[8]);

            // Attach the values to the relevant node in the tree.
            cache_.putMetric(hierarchy, id, date_, "npv", npv);
            cache_.putMetric(hierarchy, id, date_, "ce", ce);

            // Accumulate the total exposure.
            cache_.totalExposureSetNPV(hierarchy, date_, id, npv);
            cache_.totalExposureSetCE(hierarchy, date_, id, ce);

            // If this is the total NPV file, then set the base ccy (a public
            // static field in the Cache class).  This function gets called for
            // every processing date but we only need to extract the value once
            // so only do it if baseCurrency has not already been initialized.
            if (baseCurrency == null && hierarchy.equals("total"))
                baseCurrency = fields[7];
        }
    }

//    private void loadIM(String hierarchy) throws DashBoardException, IOException {
//
//        // Load the IM file.
//
//        File f = new File(DATA_DIR + date_ + "/SIMM_Report_" + hierarchy + ".csv");
//
//        for (String[] fields : f.data_) {
//
//            if (fields[1].equals("All") && fields[2].equals("All") && fields[3].equals("All")) {
//
//                String id = fields[0];
//                Double im = parseDouble(fields[4]);
//
//                // Attach the value to the relevant node in the tree.
//                cache_.putMetric(hierarchy, id, date_, "im", im);
//            }
//        }
//    }

    private void loadXva(String hierarchy) throws DashBoardException, IOException {

        // Load the XVA file, accumulating data for both bar and donut graphs.

        // Declare the structures to accumulate data for the donut graphs.
        Xva xvaCol = new Xva("colva");
        Xva xvaCva = new Xva("cva");
        Xva xvaDva = new Xva("dva");
        Xva xvaFca = new Xva("fca");
        Xva xvaFba = new Xva("fba");
        Xva xvaFva = new Xva("fva");

        File f = new File(DATA_DIR + date_ + "/xva_" + hierarchy + ".csv");

        for (String[] fields : f.data_) {

            // Extract the relevant values from the line.
            String id = fields[1];
            Double col = parseDouble(fields[6]);
            Double cva = parseDouble(fields[2]);
            Double dva = parseDouble(fields[3]);
            Double fca = parseDouble(fields[5]);
            Double fba = parseDouble(fields[4]);
            Double fva = fca + fba;
            Double eepe = parseDouble(fields[12]);

            // Accumulate the total exposure.
            cache_.totalExposureSetEEPE(hierarchy, date_, id, eepe);

            // Accumulate and cache the data for the bar graphs.
            cache_.putMetric(hierarchy, id, date_, "colva", col);
            cache_.putMetric(hierarchy, id, date_, "cva", cva);
            cache_.putMetric(hierarchy, id, date_, "dva", dva);
            cache_.putMetric(hierarchy, id, date_, "fca", fca);
            cache_.putMetric(hierarchy, id, date_, "fba", fba);
            cache_.putMetric(hierarchy, id, date_, "fva", fva);
            cache_.putMetric(hierarchy, id, date_, "eepe", eepe);

            // Accumulate the Hierarchy-level data for the donut graphs.
            xvaCol.put(id, col);
            xvaCva.put(id, cva);
            xvaDva.put(id, dva);
            xvaFca.put(id, fca);
            xvaFba.put(id, fba);
            xvaFva.put(id, fva);

            // Cache the Node-level data for the donut graphs.
            cache_.putXvaItem(hierarchy, date_, id, "colva", col);
            cache_.putXvaItem(hierarchy, date_, id, "cva", cva);
            cache_.putXvaItem(hierarchy, date_, id, "dva", dva);
            cache_.putXvaItem(hierarchy, date_, id, "fca", fca);
            cache_.putXvaItem(hierarchy, date_, id, "fba", fba);
            cache_.putXvaItem(hierarchy, date_, id, "fva", fva);
        }

        // Cache the Hierarchy-level data for the donut graphs
        cache_.putXva(hierarchy, date_, "colva", xvaCol);
        cache_.putXva(hierarchy, date_, "cva", xvaCva);
        cache_.putXva(hierarchy, date_, "dva", xvaDva);
        cache_.putXva(hierarchy, date_, "fca", xvaFca);
        cache_.putXva(hierarchy, date_, "fba", xvaFba);
        cache_.putXva(hierarchy, date_, "fva", xvaFva);
    }

    private void loadXvaTotal() throws DashBoardException, IOException {

        // Extract EEPE from the XVA file for the total hierarchy (i.e. the root node).

        String path = DATA_DIR + date_ + "/xva_total.csv";
        File f = new File(path);
        if (1 != f.data_.size())
            throw new DashBoardException("error loading file '" + path + "' " +
                "- expected 1 data row, detected " + f.data_.size() + ".");

        String[] fields = f.data_.get(0);
        String id = fields[1];  // "Total"

        Double col = parseDouble(fields[6]);
        Double cva = parseDouble(fields[2]);
        Double dva = parseDouble(fields[3]);
        Double fca = parseDouble(fields[5]);
        Double fba = parseDouble(fields[4]);
        Double fva = fca + fba;
        Double eepe = parseDouble(fields[12]);

        cache_.putMetric("total", id, date_, "colva", col);
        cache_.putMetric("total", id, date_, "cva", cva);
        cache_.putMetric("total", id, date_, "dva", dva);
        cache_.putMetric("total", id, date_, "fca", fca);
        cache_.putMetric("total", id, date_, "fba", fba);
        cache_.putMetric("total", id, date_, "fva", fva);
        cache_.putMetric("total", id, date_, "eepe", eepe);

        cache_.totalExposureSetEEPE("total", date_, id, eepe);
    }

    // File xva_nettingset.csv looks something like this:
    //
    //      #TradeId,NettingSetId,CVA,...
    //      ,CSA_1,2679.26,...
    //      Trade_9765,CSA_1,449.812,...
    //      Trade_7634,CSA_1,19.2981,...
    //
    // You get one row where the trade ID is null, followed by a series of rows where the Trade ID is populated.
    // The row with the null trade ID contains data for the netting set.
    // The rows where the Trade ID is populated contain trade-level data, at present the only value we need there is EEPE.

    private void loadXvaNettingset() throws DashBoardException, IOException {

        // Load the xva file, accumulating data for both bar and donut graphs.

        // Declare the structures to accumulate data for the donut graphs.
        Xva xvaCol = new Xva("colva");
        Xva xvaCva = new Xva("cva");
        Xva xvaDva = new Xva("dva");
        Xva xvaFca = new Xva("fca");
        Xva xvaFba = new Xva("fba");
        Xva xvaFva = new Xva("fva");

        File f = new File(DATA_DIR + date_ + "/xva_nettingset.csv");

        for (String[] fields : f.data_) {

            if (fields[0] == null || fields[0].isEmpty()) {

                // Nettingset-level data.

                // Extract the relevant values from the line.
                String id = fields[1];  // The nettingset name is in column 1.
                Double col = parseDouble(fields[6]);
                Double cva = parseDouble(fields[2]);
                Double dva = parseDouble(fields[3]);
                Double fca = parseDouble(fields[5]);
                Double fba = parseDouble(fields[4]);
                Double fva = fca + fba;
                Double eepe = parseDouble(fields[12]);

                // Accumulate the total exposure.
                cache_.totalExposureSetEEPE("nettingset", date_, id, eepe);

                // Accumulate and cache the data for the bar graphs.
                cache_.putMetric("nettingset", id, date_, "colva", col);
                cache_.putMetric("nettingset", id, date_, "cva", cva);
                cache_.putMetric("nettingset", id, date_, "dva", dva);
                cache_.putMetric("nettingset", id, date_, "fca", fca);
                cache_.putMetric("nettingset", id, date_, "fba", fba);
                cache_.putMetric("nettingset", id, date_, "fva", fva);
                cache_.putMetric("nettingset", id, date_, "eepe", eepe);

                // Accumulate the Hierarchy-level data for the donut graphs.
                xvaCol.put(id, col);
                xvaCva.put(id, cva);
                xvaDva.put(id, dva);
                xvaFca.put(id, fca);
                xvaFba.put(id, fba);
                xvaFva.put(id, fva);

                // Cache the Node-level data for the donut graphs.
                cache_.putXvaItem("nettingset", date_, id, "colva", col);
                cache_.putXvaItem("nettingset", date_, id, "cva", cva);
                cache_.putXvaItem("nettingset", date_, id, "dva", dva);
                cache_.putXvaItem("nettingset", date_, id, "fca", fca);
                cache_.putXvaItem("nettingset", date_, id, "fba", fba);
                cache_.putXvaItem("nettingset", date_, id, "fva", fva);

            } else {

                // Trade-level data.  At present the only value we need is EEPE.

                String id = fields[0];  // The Trade ID is in column 0.
                Double eepe = parseDouble(fields[12]);
                cache_.putMetric("trade", id, date_, "eepe", eepe);
                cache_.totalExposureSetEEPE("trade", date_, id, eepe);
            }
        }

        // Cache the Hierarchy-level data for the donut graphs
        cache_.putXva("nettingset", date_, "colva", xvaCol);
        cache_.putXva("nettingset", date_, "cva", xvaCva);
        cache_.putXva("nettingset", date_, "dva", xvaDva);
        cache_.putXva("nettingset", date_, "fca", xvaFca);
        cache_.putXva("nettingset", date_, "fba", xvaFba);
        cache_.putXva("nettingset", date_, "fva", xvaFva);
    }

    private void loadExposure(String hierarchy) throws DashBoardException, IOException {

        // Load the Exposure file.

        app.load.Scanner s = new app.load.Scanner();
        Map<String, String> m = s.scan2(DATA_DIR + date_ + "/exposure_" + hierarchy + "_(\\w+).csv");

        for (Map.Entry<String, String> e : m.entrySet()) {

            // Declare some containers to accumulate the data.
            List<String> dates = new ArrayList<String>();
            List<Double> epes = new ArrayList<Double>();
            List<Double> pfes = new ArrayList<Double>();
            List<Double> enes = new ArrayList<Double>();

            File f = new File(e.getValue());
            for (String[] fields : f.data_) {

                // Extract the data from the fields into the containers.
                dates.add(convertDate(fields[1]));
                epes.add(parseDouble(fields[3]));
                if (hierarchy.equals("trade"))
                    pfes.add(parseDouble(fields[7]));
                else
                    pfes.add(parseDouble(fields[5]));
                enes.add(parseDouble(fields[4])*-1);    // reverse the sign
            }

            // Cache the data.
            cache_.setExposure(hierarchy, date_, e.getKey(), new Exposure(e.getKey(), dates, epes, pfes, enes));
        }
    }

    private void loadExposureTotal() throws DashBoardException, IOException {

        // Load the Exposure file for the total hierarchy (i.e. the root node).

        // Declare some containers to accumulate the data.
        List<String> dates = new ArrayList<String>();
        List<Double> epes = new ArrayList<Double>();
        List<Double> pfes = new ArrayList<Double>();
        List<Double> enes = new ArrayList<Double>();

        File f = new File(DATA_DIR + date_ + "/exposure_total.csv");
        for (String[] fields : f.data_) {

            // Extract the data from the fields into the containers.
            dates.add(convertDate(fields[1]));
            epes.add(parseDouble(fields[3]));
            pfes.add(parseDouble(fields[5]));
            enes.add(parseDouble(fields[4])*-1);        // reverse the sign
        }

        // Cache the data.
        cache_.setTotalExposure(date_, new Exposure(date_, dates, epes, pfes, enes));
    }
};

