
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

import app.util.DashBoardException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// The CSV files are packaged in the jar file.
// This class loads a CSV file and stores its contents
// in a buffer.

// The data fields are not enclosed in quotes and there are no embedded commas,
// all commas are delimiters.

public class File {

    private String path_;
    private String[] headers_;
    public List<String[]> data_;

    public File(String path) throws DashBoardException, IOException {

        if (!FileList.fileExists(path))
            throw new DashBoardException("nonexistent file : " + path);

        path_ = path;
        data_ = new ArrayList<String[]>();

        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(path)))) {

            line = br.readLine();
            if (null == line) {
                throw new DashBoardException("empty file : " + path);
            }

            if (line.length() < 1) {
                throw new DashBoardException("null header : " + path);
            }

            headers_ = line.split(cvsSplitBy);

            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] ss = line.split(cvsSplitBy);
                // Some of the files contains line comprising only comma delimiters, no data.
                // line.split() returns zero in that case and the test below ignores such lines.
                if (ss.length > 0) {
                    // Make sure that the number of fields in this line equals the number of column headers.
                    if (ss.length != headers_.length)
                        throw new DashBoardException("invalid line length - file='" + path_ + "' - " + 
                            "header row has " + headers_.length + " fields, " + 
                            "line #" + i + " has " + ss.length + " fields.");
                    data_.add(ss);
                }
                i++;
            }
        }
    }

    private void dumpLine(String[] ss) {
        System.out.print("|");
        for (String s: ss) {
            System.out.print(s + "|");
        }
        System.out.println();
    }

    public void dump() {
        System.out.println("path=" + path_ + " size=" + (data_.size()-1));
        dumpLine(headers_);
        for (int i=0; i<3 && i<data_.size(); i++) {
            dumpLine(data_.get(i));
        }
    }
}

