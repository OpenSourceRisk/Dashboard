
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

/**
 * Created by quaternion on 30/08/2016.
 * This class is intended to provide the JSON representation
 * of a route to a node. This will be used for the breadcrumb navigator
 * typical JSON
 * 1 item (from a counterparty query, has 1 parent
 * [ {hierarchy: 'creditrating', 'level' : 0, item: 'CCC'}]
 *
 * 2 items from a netting set query
 * [ {hierarchy: 'creditrating', 'level' : 0, item: 'AAA'},
 *  {hierarchy: 'counterparty', 'level' : 1, item: 'CUST_Z'}]
 *
 */
public class NodeDTO {

    private String hierarchy;
    private String item;
    private Integer level;

    public NodeDTO(String hierarchy, String item, Integer level) {
        this.hierarchy = hierarchy;
        this.item = item;
        this.level = level;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
