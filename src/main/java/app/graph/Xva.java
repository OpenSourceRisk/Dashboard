
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

import java.util.ArrayList;
import java.util.List;

public class Xva {

    private String name;
    private List<String> labels;
    private List<XvaItem> data;
    private Double sum;

    public Xva(String name) {
        this.name = name;
        labels = new ArrayList<String>();
        data = new ArrayList<XvaItem>();
        sum = 0.0;
    }

    public Xva(String name, List<String> labels, List<XvaItem> data, Double sum) {
        this.name = name;
        this.labels = labels;
        this.data = data;
        this.sum = sum;
    }

    public void put(String label, Double value) {
        labels.add(label);
        data.add(new XvaItem(label, value));
        sum += value;
    }

    public String getName() {
        return name;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<XvaItem> getData() {
        return data;
    }

    public Double getSum() {
        return sum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void setData(List<XvaItem> data) {
        this.data = data;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public void dump() {
        System.out.println("name=" + name);
        System.out.print("labels=");
        for (String label : labels)
            System.out.print(" " + label);
        System.out.println();
        System.out.print("data=");
        for (XvaItem item : data)
            item.dump();
        System.out.println();
    }
}

