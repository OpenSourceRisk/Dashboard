
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

import app.util.DashBoardException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Utils {

    public static final String DATA_DIR = "data/v6/";

    // Attempt to convert the given string into a date using the given pattern.
    // If the attempt succeeds, return the date.
    // If the attempt fails, return null.
    private static LocalDate convertDateImpl(String s, String p) {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern(p));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // ORE data files use two different formats for date:
    //    M/D/YYYY      <- M and D may be 1 or 2 digits
    //    YYYY-MM-DD
    // This function accepts as input a string which is assumed to be in one of the
    // two above formats.
    // Attempt to convert the string into a LocalDate, and then to convert that
    // LocalDate back into a string with the format YYYYMMDD.
    // If the attempt fails, throw an exception.
    public static String convertDate(String s) throws DashBoardException {
        LocalDate d = null;
        // Try to convert the input string into a LocalDate using format "M/d/y".
        if ((d = convertDateImpl(s, "M/d/y")) != null) {
            // Convert the LocalDate back into a string with format "yyyyMMdd" and return.
            return d.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // Try to convert the input string into a LocalDate using format "yyyy-MM-dd".
        } else if ((d = convertDateImpl(s, "yyyy-MM-dd")) != null) {
            // Convert the LocalDate back into a string with format "yyyyMMdd" and return.
            return d.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // Give up.
        } else {
            throw new DashBoardException("invalid date : " + s);
        }
    }

    public static Double parseDouble(String s) {
        if (s.equalsIgnoreCase("n/a"))
            return Double.NaN;
        else
            return Double.parseDouble(s);
    }
};

