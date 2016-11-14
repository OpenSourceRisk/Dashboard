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

package app.data;

/**
 * Created by quaternion on 07/08/2016.
 */
public enum ChartCategory {

    // Credit Risk
    NPV("Net Present Value")
    , CE("Current Exposure")
    , EPE("Expected Positive Exposure")
    , ENE("Expected Negative Exposure")
    , PFE("Potential Future Exposure")
    , EEPE("Effective Expected Positive Exposure")
    , TOTALEXP("Total - CE + EEPE")
    , CVA("Credit Valuation Adjustment")
    , DVA("Debit Valuation Adjustment")
    , SACCR("Basel III Standardized Approach Counterparty Credit Risk")
    , EL("Expected Loss")
    , UEL("Unexpected Loss")
    // Market Risk
    , VAR("Value at Risk")
    , ES("Expected Shortfall")
    // Liquidity Risk
    , FCA("Funding Cost Adjustment")
    , FBA("Funding Benefit Adjustment")
    , FVA("Funding Value Adjustment")
    , ColVA("Collateral Valuation Adjustment")
    , MVA("Margin Valuation Adjustment")
    , IM("Initial Margin")
    , VM("Variation Margin")
    , SNCO("Stressed Net Cash Outflow")
    , RSF("Required Stable Funding");

    private String value;

    private ChartCategory(String s) {
        this.value = s;
    }

    public String value() {
        return this.value;
    }
}
