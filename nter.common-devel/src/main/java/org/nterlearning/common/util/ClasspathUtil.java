/*
 * National Training and Education Resource (NTER)
 * Copyright (C) 2012 SRI International
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 */

package org.nterlearning.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClasspathUtil {
    private static Logger log = LoggerFactory.getLogger(ClasspathUtil.class);

    private static ClasspathUtil cu = new ClasspathUtil();

    private ClasspathUtil() {

    }

    public static ClasspathUtil getInstance() {
        return cu;
    }

    public Properties getConfigProperties(String propSpec) {
        Properties retVal = null;

        try {

            InputStream is = getClass().getClassLoader().getResourceAsStream(propSpec);

            if (is != null) {
                log.info("Found " + propSpec + " on Classpath");
                retVal = new Properties();
                retVal.load(is);
            }
        } catch (IOException e) {
            // just fail silently and return
        }

        return retVal;
    }

}
