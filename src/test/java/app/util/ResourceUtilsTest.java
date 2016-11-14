
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import spark.utils.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ResourceUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetFile_whenURLProtocolIsNotFile_thenThrowFileNotFoundException() throws
            MalformedURLException,
            FileNotFoundException {
        thrown.expect(FileNotFoundException.class);
        thrown.expectMessage("My File Path cannot be resolved to absolute file path " +
                "because it does not reside in the file system: http://example.com/");

        URL url = new URL("http://example.com/");
        ResourceUtils.getFile(url, "My File Path");
    }

    @Test
    public void testGetFile_whenURLProtocolIsFile_thenReturnFileObject() throws
            MalformedURLException,
            FileNotFoundException,
            URISyntaxException {
        //given
        URL url = new URL("file://public/file.txt");
        File file = ResourceUtils.getFile(url, "Some description");

        //then
        assertEquals("Should be equals because URL protocol is file", file, new File(ResourceUtils.toURI(url).getSchemeSpecificPart()));
    }

}
