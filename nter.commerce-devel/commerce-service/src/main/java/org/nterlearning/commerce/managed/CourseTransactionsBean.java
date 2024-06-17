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

import org.nterlearning.commerce.model.TransactionModel;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/18/12
 */
@ManagedBean(name = "courseTransactions")
public class CourseTransactionsBean implements Serializable {
    @ManagedProperty("#{transactionModel}")
    private TransactionModel transactionAPI;

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{param.courseId}")
    private String courseId;
    @ManagedProperty("#{param.courseName}")
    private String courseName;

    private Date startDate;
    private Date endDate;

    private List<PaymentTransaction> paymentTransactions;

    private static final String COURSE_ID = "courseId";
    private static final String DATA_CACHE_NAME = CourseTransactionsBean.class.getName();
    private static final String START_DATE = "startDate" + DATA_CACHE_NAME;
    private static final String END_DATE = "endDate" + DATA_CACHE_NAME;

    private static final String REDIRECT_URL = "/courseTransactions.xhtml";

    private Logger logger = LoggerFactory.getLogger(CourseTransactionsBean.class);

    public CourseTransactionsBean() {
        Date startDate = getStartDate();
        Date endDate = getEndDate();

        if (startDate == null) {
            setStartDate(BeanUtil.getFirstDayOfMonth());
        }

        if (endDate == null) {
            setEndDate(new Date());
        }

        paymentTransactions = new ArrayList<PaymentTransaction>();
    }

    @SuppressWarnings("unchecked")
    public List<PaymentTransaction> getTransactions() {

        if (paymentTransactions.isEmpty()) {
            // Is the transaction data already cached?
            Object object =
                BeanUtil.getAttributeFromSession(DATA_CACHE_NAME);
            if (object != null) {
                try {
                    paymentTransactions = (ArrayList<PaymentTransaction>)object;
                } catch (Exception e) {
                    logger.error("Error retrieving transactions from session", e);
                }
            }
        }

        if (paymentTransactions.isEmpty()) {
            String courseId = getCourseId();
            Date startDate = getStartDate();
            Date endDate = getEndDate();

            for (PaymentTransaction transaction: getTransactionAPI().
                    getTransactionsByCourseId(courseId, startDate, endDate)) {
                String nterId = transaction.getNterId();
                String cpId = transaction.getCourseProviderId();
                if (entitlementUtil.hasReadAccess(nterId) ||
                        entitlementUtil.hasReadAccess(cpId)) {
                    paymentTransactions.add(transaction);
                }
            }
            // Cache the data
            BeanUtil.setAttributeInSession(
                    DATA_CACHE_NAME, paymentTransactions);
        }

        if (!paymentTransactions.isEmpty() && courseName == null) {
            courseName = paymentTransactions.get(0).getItemName();
            logger.debug("Setting courseName [" + courseName + "]");
        }

        return paymentTransactions;
    }

    public void dateUpdate() {
        String courseId = getCourseId();
        Date startDate = getStartDate();
        Date endDate = BeanUtil.getEndOfDay(getEndDate());

        if (startDate != null) {
            BeanUtil.setAttributeInSession(START_DATE, startDate);
        }

        BeanUtil.setAttributeInSession(END_DATE, endDate);

        if (courseId != null && !courseId.isEmpty()) {
            BeanUtil.setAttributeInSession(COURSE_ID, courseId);
        }

        // Clear transaction data from cache
        BeanUtil.setAttributeInSession(DATA_CACHE_NAME, null);

        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String contextPath =
                    facesContext.getExternalContext().getRequestContextPath();
            StringBuilder redirectURL = new StringBuilder(contextPath);
            redirectURL.append(REDIRECT_URL);
            redirectURL.append("?courseId=");
            redirectURL.append(courseId);
            redirectURL.append("&courseName=");
            redirectURL.append(courseName);

            facesContext.getExternalContext().redirect(redirectURL.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public Date getStartDate() {
        if (startDate == null) {
            startDate = (Date) BeanUtil.getAttributeFromSession(START_DATE);
        }
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = startDate;
            BeanUtil.setAttributeInSession(START_DATE, startDate);
        }
    }

    public Date getEndDate() {
        if (endDate == null) {
            endDate = (Date) BeanUtil.getAttributeFromSession(END_DATE);
        }
        return endDate;
    }

    public void setEndDate(Date endDate) {
        if (endDate != null) {
            this.endDate = endDate;
            BeanUtil.setAttributeInSession(END_DATE, endDate);
        }
    }

    public String getCourseId() {
        if (courseId == null) {
            logger.debug("CourseId is null, will attempt to retrieve from session");
            courseId = (String) BeanUtil.getAttributeFromSession(COURSE_ID);
        }

        return courseId;
    }

    public void setCourseId(String courseId) {
        if (courseId != null && !courseId.isEmpty()) {
            // Is there any cached data?
            String cachedCourseId =
                    (String)BeanUtil.getAttributeFromSession(COURSE_ID);
            // If the courseId has changed flush the data cache
            if (cachedCourseId != null && !cachedCourseId.equals(courseId)) {
                BeanUtil.setAttributeInSession(DATA_CACHE_NAME, null);
            }

            // Update courseId
            this.courseId = courseId;
            BeanUtil.setAttributeInSession(COURSE_ID, courseId);
        }
    }

    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public TransactionModel getTransactionAPI() {
        return transactionAPI;
    }
    public void setTransactionAPI(TransactionModel transactionAPI) {
        this.transactionAPI = transactionAPI;
    }

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }
}
