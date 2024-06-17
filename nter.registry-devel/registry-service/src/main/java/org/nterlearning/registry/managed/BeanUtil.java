/**
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
package org.nterlearning.registry.managed;

import org.nterlearning.entitlement.client.Subject;

import java.util.Comparator;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 3/1/12
 */
public class BeanUtil {

     private static final String ESCAPE_CHAR = "`";

     public static String escape(String value) {
        String replacedValue = null;
        if (value != null) {
            replacedValue = value.replace("'", ESCAPE_CHAR);
        }

        return replacedValue;
    }

    public static String unescape(String value) {
        String replacedValue = null;
        if (value != null) {
            replacedValue = value.replace(ESCAPE_CHAR, "'");
        }
        return replacedValue;
    }

    public static class SubjectComparator implements Comparator<Subject> {
        public int compare(Subject subject1, Subject subject2){
            String email1 = subject1.getEmail();
            String email2 = subject2.getEmail();

            return email1.compareTo(email2);
        }
    }

    public static class PolicyComparator implements Comparator<Policy> {
        public int compare(Policy policy1, Policy policy2){
            String email1 = policy1.getEmail();
            String email2 = policy2.getEmail();

            return email1.compareTo(email2);
        }
    }
}
