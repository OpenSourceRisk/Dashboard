
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static app.cache.Utils.DATA_DIR;

/*  class FileList

    This class encapsulates the file dirlist.txt, which lists all of the data files
    that are packaged into the project's jar file.
*/
public class FileList {

    private static final List<String> fileNames_;
    static {

        fileNames_ = new ArrayList<String>();
        String fileName = "";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                FileList.class.getClassLoader().getResourceAsStream(DATA_DIR + "dirlist.txt")))) {
            while ((fileName = br.readLine()) != null)
                fileNames_.add(fileName);
        } catch(final Exception e){
            throw new RuntimeException("Failed to initialize file list.", e);
        }
    }

    public static boolean fileExists(String fileName) {
        return fileNames_.contains(fileName);
    }

    public static List<String> getFileList() {
        return fileNames_;
    }
}

