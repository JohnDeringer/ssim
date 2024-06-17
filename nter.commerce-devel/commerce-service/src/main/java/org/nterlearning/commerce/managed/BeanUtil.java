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
package org.nterlearning.commerce.managed;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.datatype.XMLGregorianCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.jvnet.hyperjaxb3.xml.bind.annotation.adapters.XMLGregorianCalendarAsDate;
import org.jvnet.hyperjaxb3.xml.bind.annotation.adapters.XmlAdapterUtils;

import org.nterlearning.entitlement.client.Subject;

import org.primefaces.util.MessageFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/9/12
 */
public class BeanUtil {

    private static final String ESCAPE_CHAR = "`";
    private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);

    @NotNull
    public static String escape(@NotNull String value) {
        return value.replace("'", ESCAPE_CHAR);
    }

    @NotNull
    public static String unescape(@NotNull String value) {
        return value.replace(ESCAPE_CHAR, "'");
    }

    public static class SubjectComparator implements Comparator<Subject> {
        public int compare(Subject subject1, Subject subject2){
            String email1 = subject1.getEmail();
            String email2 = subject2.getEmail();

            return email1.compareTo(email2);
        }
    }

    @NotNull
    public static Date getFirstDayOfMonth() {
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);

        return calendar.getTime();
    }

    @NotNull
    public static Date getLastDayOfMonth() {
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndOfDay(calendar);

        return calendar.getTime();
    }

    @NotNull
    public static Date getEndOfDay(@NotNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        setTimeToEndOfDay(calendar);

        return calendar.getTime();
    }

    @NotNull
    public static Date getDate(@NotNull String dateString) {
        // Fri Apr 01 00:00:00 PDT 2011
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            logger.error("Exception parsing Date String [" + dateString + "]");
        }
        return date;
    }

    @Nullable
    public static Object getAttributeFromRequest(@NotNull String attribute) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request =
                (HttpServletRequest)context.getExternalContext().getRequest();

        return request.getAttribute(attribute);
    }

    @Nullable
    public static Object getParameterFromRequest(@NotNull String attribute) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request =
                (HttpServletRequest)context.getExternalContext().getRequest();

        return request.getParameter(attribute);
    }

    @Nullable
    public static Object getAttributeFromSession(@NotNull String attribute) {
        Object value = null;
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request =
                (HttpServletRequest)context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            value = httpSession.getAttribute(attribute);
        } else {
            logger.warn("Unable to getAttributeFromSession, HttpSession is 'Null'");
        }

        return value;
    }

    public static void setAttributeInSession(
            @NotNull String name, @Nullable Object value) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request =
                (HttpServletRequest)context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            httpSession.setAttribute(name, value);
        } else {
            logger.warn("Unable to setAttributeInSession, HttpSession is 'Null'");
        }
    }

    public static String getMessageAsString(String msgKey, Object[] args) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Locale locale = facesContext.getViewRoot().getLocale();
        FacesMessage msg =
                    MessageFactory.getMessage(locale,
                            msgKey, args);
        return msg.getSummary();
    }

    public static String getLocalizedString(String key) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Locale locale = facesContext.getViewRoot().getLocale();
        ResourceBundle resourceBundle =
        ResourceBundle.getBundle("messages", locale);

        return resourceBundle.getString(key);
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(@NotNull Date date) {
        return XmlAdapterUtils.marshall(XMLGregorianCalendarAsDate.class, date);
    }

    @NotNull
    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(@NotNull Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndOfDay(@NotNull Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }


}
