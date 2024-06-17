/*
 * National Training and Education Resource (NTER)
 * Copyright (C) 2012  SRI International
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.nterlearning.commerce.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hyperjaxb3.xml.bind.annotation.adapters.XMLGregorianCalendarAsDate;
import org.jvnet.hyperjaxb3.xml.bind.annotation.adapters.XmlAdapterUtils;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/18/12
 */
public class ServiceUtil {

    private static Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

//    private static DatatypeFactory df = null;
//    static {
//        try {
//            df = DatatypeFactory.newInstance();
//        } catch (DatatypeConfigurationException dce) {
//            throw new IllegalStateException(
//                    "Exception while obtaining DatatypeFactory instance", dce);
//        }
//    }

    /**
     * Converts a String Date into an instance of XMLGregorianCalendar
     *
     * @param theDate
     *            Instance of java.lang.String in the format
     *            ("HH:mm:ss MMM dd, yyyy z")
     * @return XMLGregorianCalendar instance whose value is based upon the value
     *         in the date parameter. If the date parameter is null then this
     *         method will simply return null.
     * @throws java.text.ParseException When unable to parse string
     */
//    public static java.util.Date asJavaUtilDate(String theDate)
//            throws ParseException {
//        if (theDate == null) {
//            return null;
//        } else {
//            DateFormat dateFormat = new SimpleDateFormat(
//                    "HH:mm:ss MMM dd, yyyy z");
//            return dateFormat.parse(theDate);
//        }
//    }

    /**
     * Converts a String Date into an instance of XMLGregorianCalendar
     *
     * @param theDate
     *            Instance of java.lang.String in the format
     *            ("HH:mm:ss MMM dd, yyyy z")
     * @return XMLGregorianCalendar instance whose value is based upon the value
     *         in the date parameter. If the date parameter is null then this
     *         method will simply return null.
     * @throws ParseException When unable to parse string
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(String theDate)
            throws ParseException {
        if (theDate == null) {
            return null;
        } else {
            DateFormat dateFormat = new SimpleDateFormat(
                    "HH:mm:ss MMM dd, yyyy z");
            Date date = dateFormat.parse(theDate);
            return toXMLGregorianCalendar(date);
        }
    }

    /**
     * Converts a java.util.Date into an instance of XMLGregorianCalendar
     *
     * @param date
     *            Instance of java.util.Date or a null reference
     * @return XMLGregorianCalendar instance whose value is based upon the value
     *         in the date parameter. If the date parameter is null then this
     *         method will simply return null.
     */
    @Nullable
    public static XMLGregorianCalendar toXMLGregorianCalendar(@Nullable Date date) {
        if (date == null) {
            return null;
        }
        return XmlAdapterUtils.marshall(XMLGregorianCalendarAsDate.class, date);
    }

//    @NotNull
//    public static XMLGregorianCalendar asXMLGregorianCalendar(
//            @NotNull java.util.Date date) {
//        return XmlAdapterUtils.marshall(XMLGregorianCalendarAsDate.class, date);
//    }

    @Nullable
    public static Date toDate(@Nullable XMLGregorianCalendar cal) {
        if (cal == null) {
            return null;
        }
        return XmlAdapterUtils.unmarshall(XMLGregorianCalendarAsDate.class, cal);
    }

    /**
     * Converts a String Date into an instance of XMLGregorianCalendar
     *
     * @param strDate
     *            Instance of java.lang.String in the format
     *            ("HH:mm:ss MMM dd, yyyy z")
     * @return java.util.Date instance whose value is based upon the value
     *         in the strDate parameter. If the date parameter is null then this
     *         method will simply return null.
     */
    public static Date toDate(String strDate) {
        Date date = null;
        if (strDate != null && !strDate.isEmpty()) {
            try {
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.parse(strDate);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Error parsing Date, format must be yyyy-MM-dd", e);
            }
        }
        return date;
    }

    public static Map<String, String> getCustomValues(String userDefinedValues) {
        Map<String, String> valueMap = new HashMap<String, String>();
        String valueLineItems[] = userDefinedValues.split("\\|");
        for (String valueLineItem : valueLineItems) {
            String valueItem[] = valueLineItem.split("\\=");
            if (valueItem != null && valueItem.length == 2) {
                valueMap.put(valueItem[0], valueItem[1]);
            } else {
                logger.error("Incorrect format of custom field [" +
                        userDefinedValues + "] custom field must be in the format: studentId=studentUniqueIdentifier|nterId=portalId|cpId=contentProviderId");
            }
        }
        return valueMap;
    }

    public static String getPrintableTransactionTypes() {
        StringBuilder sb = new StringBuilder();
        TransactionType[] types = TransactionType.values();
        for (int i = 0; i < types.length; i++) {
            sb.append(types[i].value());
            if (i < types.length - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    // TODO: verify uniqueness with DB?
    public static String getTransactionId() {
        Random r = new Random();
        long tId = r.nextLong();
        if (tId < 1) {
            tId *= -1;
        }

        return "NTER:" + tId;
    }

}
