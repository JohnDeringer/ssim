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

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class InetAddressUtil {

    private static final String IPV4_REGEX = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";
    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static boolean isValidInet4Address(@NotNull String inet4Address) {

        Assert.hasText(inet4Address);

        boolean isValid = true;
        Matcher matcher = IPV4_PATTERN.matcher(inet4Address);
        if (matcher.matches()) {

            for (int index = 1; index < matcher.groupCount(); index++) {

                String ipSegmentString = matcher.group(index);
                if (ipSegmentString == null || ipSegmentString.length() <= 0) {

                    isValid = false;
                    break;

                }

                try {

                    int ipSegmentInt = Integer.parseInt(ipSegmentString);
                    isValid = ipSegmentInt >= 0 && ipSegmentInt <= 255;

                } catch (NumberFormatException e) {

                    isValid = false;
                    break;

                }

            }

        } else {

            isValid = false;

        }

        return isValid;

    }

}
