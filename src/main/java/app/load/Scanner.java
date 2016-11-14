
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

package app.load;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {
    /*
    This function returns a map containing the results of a directory
    listing using a regex.

    For example, if you search for:

        "data/v3/20160301/exposure_counterparty_(\\w+).csv"

    And if that matches:

        data/v3/20160301/exposure_counterparty_CUST_A.csv
        data/v3/20160301/exposure_counterparty_CUST_B.csv

    Then you will get back a TreeMap object where:
    
        key=CUST_A val=data/v3/20160301/exposure_counterparty_CUST_A.csv
        key=CUST_B val=data/v3/20160301/exposure_counterparty_CUST_B.csv
    */
    public Map<String, String> scan2(String path) throws IOException {

        Map<String, String> ret = new TreeMap<String, String>();

        List<String> fileNames = FileList.getFileList();
        for (String line : fileNames) {
            Pattern p = Pattern.compile(path);
            Matcher m = p.matcher(line);
            if (m.matches())
                ret.put(m.group(1), line);
        }
        return ret;
    }

    /*
    This function is similar to the one above but it returns only the patterns
    matched, not the full paths.
    Can be used to retrieve the list of directories matching a given pattern.
    */
    public TreeSet<String> scan(String path) throws IOException {

        TreeSet<String> ret = new TreeSet<String>();

        List<String> fileNames = FileList.getFileList();
        for (String line : fileNames) {
            Pattern p = Pattern.compile(path);
            Matcher m = p.matcher(line);
            if (m.matches())
                ret.add(m.group(1));
        }
        return ret;
    }
}

