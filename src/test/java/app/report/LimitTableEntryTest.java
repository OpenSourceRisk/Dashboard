
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

package app.report;

import app.util.JsonTransformer;
import app.util.StaticFilesTest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by quaternion on 17/09/2016.
 */
public class LimitTableEntryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFilesTest.class);

    @Mock
    private LimitTableEntry entry;

    @Before
    public void setUp() {
        this.entry = Mockito.mock(LimitTableEntry.class);
    }

    @Test
    public void testExists_whenFileExists_thenReturnTrue() throws IOException {
        //when
        when(entry.getTrade()).thenReturn("trade_12345");
        when(entry.getCreditRating()).thenReturn("AAA");
        when(entry.getCounterParty()).thenReturn("CUST_A");
        when(entry.getNettingSet()).thenReturn("CSA_1");
        //when(entry.getBreach()).thenReturn(Boolean.FALSE);

        //assertEquals(entry.getBreach(), Boolean.FALSE);
        assertEquals(entry.getCreditRating(), "AAA");

        //then
//        verify(entry).getBreach();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    @Ignore
    public void createFromString(){
        JSONObject jsonEntry = new JSONObject();
        try {
            jsonEntry.put("trade", "trade_12345");
            jsonEntry.put("nettingSet", "CSA_1");
            jsonEntry.put("counterParty","CUST_A");
            jsonEntry.put("creditRating","AAA");
            jsonEntry.put("breach","true");
            jsonEntry.put("metric","CE");

            JsonTransformer transformer = new JsonTransformer();

            try {
                String data_ = transformer.render(entry);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //LimitTableEntry entry = new LimitTableEntry(jsonEntry);
    }

}
