
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

import app.util.JsonTransformer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by quaternion on 01/08/2016.
 */
public class BarGraphTest {

    private BarGraph bg;

    /*
        var ce_chartData = {
        yAxisLabels: ["CC", "CCC", "BB", "AA", "BBB"],
        title: {
            text: 'CE',
            subtext: '01-MAR-2016'
        },
        series: [{
            name: '2016-03-01',
            type: 'bar',
            data: [2.434153795E8, 2.031896731E8, 1.696079112E8, 5.808002756E7, 5.086273894E7]
        }]
    }
    var npv_chartData = {
        yAxisLabels: ["CCC", "BB", "CC", "AA", "C"],
        title: {
            text: 'NPV',
            subtext: '01-MAR-2016'
        },
        series: [{
            name: '2016-03-01',
            type: 'bar',
            data: [1.835835138E8, 1.070428432E8, 1.970644777E7, 1.592368559E7, -1.281875696E7]
        }]
    }
    var fca_chartData = {
        yAxisLabels: ["AAA", "NR", "CC", "B", "BBB"],
        title: {
            text: 'FCA',
            subtext: '01-MAR-2016'
        },
        series: [{
            name: '2016-03-01',
            type: 'bar',
            data: [1443943.9, 1421737.4, 1396364.0, 1376168.0, 1172954.6]
        }]
    }
    var fba_chartData = {
        yAxisLabels: ["B", "NR", "A", "AAA", "BB"],
        title: {
            text: 'FBA',
            subtext: '01-MAR-2016'
        },
        series: [{
            name: '2016-03-01',
            type: 'bar',
            data: [-17279.998, -40045.45, -48515.71269, -49429.49, -66222.23245]
        }]
    }
    var eepe_chartData = {
        yAxisLabels: ["CC", "CCC", "BB", "AA", "BBB"],
        title: {
            text: 'EEPE',
            subtext: '01-MAR-2016'
        },
        series: [{
            name: '2016-03-01',
            type: 'bar',
            data: [2.434153795E8, 2.031896731E8, 1.696079112E8, 5.808002756E7, 5.086273894E7]
        }]
    }
    var npv2_chartData = {
        yAxisLabels: ["CCC", "BB", "CC", "AA", "C"],
        title: {
            text: 'NPV2',
            subtext: '01-MAR-2016'
        },
        series: [{
            name: '2016-03-01',
            type: 'bar',
            data: [1.835835138E8, 1.070428432E8, 1.970644777E7, 1.592368559E7, -1.281875696E7]
        }]
    }

     */

    @Before
    public void setUp() throws Exception {
        String[] labels = new String[] {"CC", "CCC", "BB", "AA", "BBB"};
        Double[] values = new Double[] {2.33,3.32,4.13,5.98,9.58};
        Double[] limits = new Double[] {2.34,3.33,4.14,5.99,9.59};

        bg = new BarGraph(Arrays.asList(labels), Arrays.asList(values), Arrays.asList(limits));
        bg.setSeriesName("2016-03-01");
        bg.setSeriesType("bar");
        bg.setTitleText("CE");
        bg.setSubTitleText("01-MAR-2016");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getYAxisLabels() throws Exception {

    }

    @Test
    public void getYAxisValues() throws Exception {

    }

    @Test
    public void setYAxisValues() throws Exception {

    }

    @Test
    public void setYAxisLabels() throws Exception {

    }

    @Test
    public void getAsJsonTest(){
        JsonTransformer jt = new JsonTransformer();
        try {
            String pp = jt.render(bg);
            System.out.println(pp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
