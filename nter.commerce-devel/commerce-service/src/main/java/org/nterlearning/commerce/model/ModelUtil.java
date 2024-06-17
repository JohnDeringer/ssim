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

package org.nterlearning.commerce.model;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 6/7/12
 */
public class ModelUtil {

    public static Map<String, BigDecimal> sortByValue(Map<String, BigDecimal> map) {
        List<Map.Entry<String, BigDecimal>> list =
                 new LinkedList<Map.Entry<String, BigDecimal>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, BigDecimal>>() {
            public int compare(Map.Entry<String, BigDecimal> entry,
                               Map.Entry<String, BigDecimal> entry1) {

                BigDecimal value =
                        entry.getValue() != null ? entry.getValue() : new BigDecimal(0);
                BigDecimal value1 =
                        entry1.getValue() != null ? entry1.getValue() : new BigDecimal(0);

                return (value1.compareTo(value));
            }
        });

        Map<String, BigDecimal> result = new LinkedHashMap<String, BigDecimal>();

        Iterator<Map.Entry<String, BigDecimal>> listIterator = list.iterator();
        while (listIterator.hasNext()) {
            Map.Entry<String, BigDecimal> entry = listIterator.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static Map<String, BigDecimal> sort(Map<String, BigDecimal> map) {
        // Get a list of the entries in the map
        List<Map.Entry<String, BigDecimal>> list =
                new Vector<Map.Entry<String, BigDecimal>>(map.entrySet());

        // Sort the list using an annonymous inner class implementing Comparator for the compare method
        java.util.Collections.sort(list, new Comparator<Map.Entry<String, BigDecimal>>(){
            public int compare(
                    Map.Entry<String, BigDecimal> entry,
                    Map.Entry<String, BigDecimal> entry1) {
                // Return 0 for a match, -1 for less than and +1 for more then
                return (entry.getValue().equals(entry1.getValue()) ? 0 :
                        (entry.getValue().compareTo(entry1.getValue()) > 0 ? 1 : -1));
            }
        });

        // Clear the map
        map.clear();

        // Copy back the entries now in order
        for (Map.Entry<String, BigDecimal> entry: list){
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }
}
