
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

import java.util.List;

public class Exposure {

    private String name;
    private List<String> dates;
    private List<Double> epes;
    private List<Double> pfes;
    private List<Double> enes;

    public Exposure(String name, List<String> dates, List<Double> epes, List<Double> pfes, List<Double> enes) {
        this.name = name;
        this.dates = dates;
        this.epes = epes;
        this.pfes = pfes;
        this.enes = enes;
    }

    public String getName() {
        return name;
    }

    public List<String> getDates() {
        return dates;
    }

    public List<Double> getEpes() {
        return epes;
    }

    public List<Double> getPfes() {
        return pfes;
    }

    public List<Double> getEnes() {
        return enes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public void setEpes(List<Double> epes) {
        this.epes = epes;
    }

    public void setPfes(List<Double> pfes) {
        this.pfes = pfes;
    }

    public void setEnes(List<Double> enes) {
        this.enes = enes;
    }

    public void dump() {
        System.out.println("name=" + name);
        System.out.print("dates=");
        for (String date : dates)
            System.out.print(" " + date);
        System.out.println();
        System.out.print("epes=");
        for (Double epe : epes)
            System.out.print(" " + epe);
        System.out.println();
        System.out.print("pfes=");
        for (Double pfe : pfes)
            System.out.print(" " + pfe);
        System.out.println();
        System.out.print("enes=");
        for (Double ene : enes)
            System.out.print(" " + ene);
        System.out.println();
    }
}

