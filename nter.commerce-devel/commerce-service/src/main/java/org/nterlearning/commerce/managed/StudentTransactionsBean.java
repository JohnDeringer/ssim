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

import org.jetbrains.annotations.NotNull;

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
@ManagedBean(name = "studentTransactions")
public class StudentTransactionsBean implements Serializable {

    @ManagedProperty("#{transactionModel}")
    private TransactionModel transactionAPI;

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{param.studentId}")
    private String studentId;

    private Date startDate;
    private Date endDate;

    private List<PaymentTransaction> paymentTransactions;

    private static final String STUDENT_ID = "studentId";
    private static final String DATA_CACHE_NAME = StudentTransactionsBean.class.getName();
    private static final String START_DATE = "startDate" + DATA_CACHE_NAME;
    private static final String END_DATE = "endDate" + DATA_CACHE_NAME;

    private static final String REDIRECT_URL = "/studentTransactions.xhtml?studentId=";

    private Logger logger = LoggerFactory.getLogger(StudentTransactionsBean.class);

    public StudentTransactionsBean() {
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
            // Is the transaction data cashed?
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
            String studentId = getStudentId();
            Date startDate = getStartDate();
            Date endDate = getEndDate();
            paymentTransactions =
                    getTransactionsByStudentId(studentId, startDate, endDate);
        }

        return paymentTransactions;
    }

    @NotNull
    private List<PaymentTransaction> getTransactionsByStudentId(
            @NotNull String studentId, @NotNull Date startDate, @NotNull Date endDate) {

        logger.debug("getTransactionsByStudentId.studentId [" + studentId + "]");

        List<PaymentTransaction> studentTransactions = new ArrayList<PaymentTransaction>();

        List<PaymentTransaction> transactions = getTransactionAPI().
                getTransactionsByStudentId(studentId, startDate, endDate);
        for (PaymentTransaction paymentTransaction : transactions) {
            if (entitlementUtil.hasReadAccess(paymentTransaction.getCourseProviderId()) ||
                    entitlementUtil.hasReadAccess(paymentTransaction.getNterId())) {
                studentTransactions.add(paymentTransaction);
            }
        }

        // Cache the data
        BeanUtil.setAttributeInSession(DATA_CACHE_NAME, studentTransactions);

        return studentTransactions;
    }

    public void dateUpdate() {
        String studentId = getStudentId();
        Date startDate = getStartDate();
        Date endDate = BeanUtil.getEndOfDay(getEndDate());

        if (startDate != null) {
            BeanUtil.setAttributeInSession(START_DATE, startDate);
        }

        BeanUtil.setAttributeInSession(END_DATE, endDate);


        if (studentId != null && !studentId.isEmpty()) {
            BeanUtil.setAttributeInSession(STUDENT_ID, studentId);
        }

        // Flush transaction data from cache
        BeanUtil.setAttributeInSession(DATA_CACHE_NAME, null);

        // Refresh page
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String contextPath = facesContext.getExternalContext().getRequestContextPath();
            String redirectURL =
                    contextPath + REDIRECT_URL + studentId;

            facesContext.getExternalContext().redirect(redirectURL);
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

    public String getStudentId() {
        if (studentId == null) {
            studentId = (String) BeanUtil.getAttributeFromSession(STUDENT_ID);
        }
        return studentId;
    }

    public void setStudentId(String studentId) {
        logger.debug("setStudentId [" + studentId + "]");
        if (studentId != null && !studentId.isEmpty()) {
            // Is there any cached data?
            String cachedStudentId =
                    (String)BeanUtil.getAttributeFromSession(STUDENT_ID);
            // If the studentId has changed flush the data cache
            if (cachedStudentId != null && !cachedStudentId.equals(studentId)) {
                BeanUtil.setAttributeInSession(DATA_CACHE_NAME, null);
            }

            // Update studentId
            this.studentId = studentId;
            BeanUtil.setAttributeInSession(STUDENT_ID, studentId);
        }
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
