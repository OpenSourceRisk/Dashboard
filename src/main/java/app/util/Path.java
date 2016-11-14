
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

package app.util;


public class Path {

    public static final String API_CONTEXT = "/api";

    public static class Web {
        public static final String INDEX = "/index/";
        public static final String LOGIN = "/login/";
        public static final String LOGOUT = "/logout/";
        public static final String CHARTS = "/charts/";
        public static final String ONE_CHART = "/charts/:isbn/";
    }

    public static class Template {
        public final static String INDEX = "/velocity/index/index.vm";
        public final static String LOGIN = "/velocity/login/login.vm";
        public final static String CHARTS_ALL = "/velocity/chart/all.vm";
        public static final String CHARTS_ONE = "/velocity/chart/one.vm";
        public static final String NOT_FOUND = "/velocity/notFound.vm";
    }

}
