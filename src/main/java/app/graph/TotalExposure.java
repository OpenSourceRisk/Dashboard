
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

import app.util.DashBoardException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// TotalExposure - record the exposure at a given node in the Cache,
// for all available business dates.
public class TotalExposure {

    private String name_;
    private TreeSet<String> dates_;
    private List<Double> NPVs_;
    private List<Double> CEs_;
    private List<Double> EEPEs_;
    private Double limitCE_;
    private Double limitEEPE_;

    public TotalExposure(String name, TreeSet<String> dates) {
        name_ = name;
        dates_ = dates;
        NPVs_ = initializeList();
        CEs_ = initializeList();
        EEPEs_ = initializeList();
        limitCE_ = Double.NaN;
        limitEEPE_ = Double.NaN;
    }

    private List<Double> initializeList() {
        List<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < dates_.size(); i++)
            ret.add(Double.NaN);
        return ret;
    }

    public String getName() {
        return name_;
    }

    public Set<String> getDates() {
        return dates_;
    }

    public List<Double> getNPVs() {
        return NPVs_;
    }

    public List<Double> getCEs() {
        return CEs_;
    }

    public List<Double> getEEPEs() {
        return EEPEs_;
    }

    public Double getLimitCE() {
        return limitCE_;
    }

    public Double getLimitEEPE() {
        return limitEEPE_;
    }

    public void setName(String name) {
        name_ = name;
    }

    public void setDates(TreeSet<String> dates) {
        dates_ = dates;
    }

    private int getIndex(String date) throws DashBoardException {
        if (dates_.contains(date))
            return dates_.headSet(date).size();
        else
            throw new DashBoardException("invalid date: " + date);
    }

    public void setNPV(String date, Double NPV) throws DashBoardException {
        int i = getIndex(date);
        NPVs_.set(i, NPV);
    }

    public void setCE(String date, Double CE) throws DashBoardException {
        int i = getIndex(date);
        CEs_.set(i, CE);
    }

    public void setEEPE(String date, Double EEPE) throws DashBoardException {
        int i = getIndex(date);
        EEPEs_.set(i, EEPE);
    }

    public void setLimitCE(Double limitCE) {
        limitCE_ = limitCE;
    }

    public void setLimitEEPE(Double limitEEPE) {
        limitEEPE_ = limitEEPE;
    }
}

