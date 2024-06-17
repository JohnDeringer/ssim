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
import org.jetbrains.annotations.Nullable;

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
 *         Date: 4/19/12
 */
@ManagedBean(name = "salesSummary")
public class SalesSummaryController implements Serializable {

    @ManagedProperty("#{transactionModel}")
    private TransactionModel transactionAPI;

    @ManagedProperty("#{param.institutionName}")
    private String institutionName;

    private Date startDate;
    private Date endDate;

    private List<PaymentTransaction> refundTransactions;

    private static final String DATA_CACHE_NAME = SalesSummaryController.class.getName();
    private static final String INSTITUTION_NAME = "institutionName" + DATA_CACHE_NAME;
    private static final String START_DATE = "startDate" + DATA_CACHE_NAME;
    private static final String END_DATE = "endDate" + DATA_CACHE_NAME;

    private static final String REDIRECT_URL = "/salesSummary.xhtml?institutionName=";

    private Logger logger = LoggerFactory.getLogger(SalesSummaryController.class);

    public SalesSummaryController() {

        Date startDate = getStartDate();
        Date endDate = getEndDate();

        if (startDate == null) {
            setStartDate(BeanUtil.getFirstDayOfMonth());
        }

        if (endDate == null) {
            setEndDate(new Date());
        }
    }

    public List<TransactionSummary> getTransactions() {
        List<TransactionSummary> transactionSummaryList = new ArrayList<TransactionSummary>();

        String institutionName = getInstitutionName();
        Date startDate = getStartDate();
        Date endDate = getEndDate();

        List<PaymentTransaction> transactions =
                getTransactionsByInstitution(institutionName, startDate, endDate);

        // Retrieve refund data
        initRefundTransactions(institutionName, startDate, endDate);

        for (PaymentTransaction transaction : transactions) {
            String courseId = transaction.getItemNumber();
            int salesQty =
                    transaction.getQuantity() != null ?
                            transaction.getQuantity() : 0;

            // Refund transaction summary
            PaymentTransaction refundSummary =
                    getRefundSummaryByCourse(courseId);
            int refundQty = 0;
            if (refundSummary != null) {
                try {
                    refundQty = refundSummary.getQuantity();
                } catch (Exception e) {
                    logger.error("Error retrieving transaction summary refund quantity");
                }
            }

            TransactionSummary transactionSummary = new TransactionSummary();

            logger.debug("institutionName [" + institutionName + "]");

            TransactionUtil.InstitutionType institutionType =
                    TransactionUtil.getInstitutionType(
                            transaction, institutionName);

            switch(institutionType) {
                // Institution is both the NTER portal and content/course provider
                case BOTH:
                    transactionSummary.setCourseId(courseId);
                    transactionSummary.setCourseName(transaction.getItemName());
                    transactionSummary.setPrice(
                            TransactionUtil.getPrice(transaction));
                    transactionSummary.setSales(salesQty);
                    transactionSummary.setReturns(refundQty);
                    transactionSummary.setGrossRevenue(
                            transaction.getPaymentGross());
                    transactionSummary.setFees(
                            TransactionUtil.getFees(
                                    transaction, refundSummary,
                                    institutionType));
                    transactionSummary.setNetRevenue(
                            TransactionUtil.getNetRevenue(
                                    transaction, refundSummary,
                                    institutionType));
                    break;

                case CONTENT_PROVIDER:
                    transactionSummary.setCourseId(courseId);
                    transactionSummary.setCourseName(transaction.getItemName());
                    transactionSummary.setPrice(
                            TransactionUtil.getPrice(transaction));
                    transactionSummary.setSales(salesQty);
                    transactionSummary.setReturns(refundQty);
                    transactionSummary.setGrossRevenue(
                            transaction.getPaymentGross());
                    transactionSummary.setFees(
                            TransactionUtil.getFees(
                                    transaction, refundSummary,
                                    institutionType));
                    transactionSummary.setNetRevenue(
                            TransactionUtil.getNetRevenue(
                                    transaction, refundSummary,
                                    institutionType));
                    break;

                case NTER:
                    transactionSummary.setCourseId(courseId);
                    transactionSummary.setCourseName(transaction.getItemName());
                    transactionSummary.setPrice(
                            TransactionUtil.getPrice(transaction));
                    transactionSummary.setSales(salesQty);
                    transactionSummary.setReturns(refundQty);
                    transactionSummary.setGrossRevenue(
                            transaction.getReferrerFee());
                    transactionSummary.setNetRevenue(
                            TransactionUtil.getNetRevenue(
                                    transaction, refundSummary,
                                    institutionType));
                    break;

                default:
                    throw new RuntimeException(
                            "Invalid InstitutionType [" + institutionType + "]");
            }

            transactionSummaryList.add(transactionSummary);
        }

        return transactionSummaryList;
    }

    public void dateUpdate() {
        String institutionName = getInstitutionName();
        Date startDate = getStartDate();
        Date endDate = BeanUtil.getEndOfDay(getEndDate());

        if (startDate != null) {
            BeanUtil.setAttributeInSession(START_DATE, startDate);
        }

        BeanUtil.setAttributeInSession(END_DATE, endDate);

        if (institutionName != null && !institutionName.isEmpty()) {
            BeanUtil.setAttributeInSession(INSTITUTION_NAME, institutionName);
        }

        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String contextPath = facesContext.getExternalContext().getRequestContextPath();
            String redirectURL =
                    contextPath + REDIRECT_URL + institutionName;

            facesContext.getExternalContext().redirect(redirectURL);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public String getInstitutionName() {
        if (institutionName == null || institutionName.isEmpty()) {
            institutionName =
                    (String) BeanUtil.getAttributeFromSession(INSTITUTION_NAME);
        }
        return institutionName;
    }
    public void setInstitutionName(String institutionName) {
        if (institutionName != null && !institutionName.isEmpty()) {
            this.institutionName = institutionName;
            BeanUtil.setAttributeInSession(INSTITUTION_NAME, institutionName);
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

    @NotNull
    public List<PaymentTransaction> getRefundTransactions(
            @NotNull String institution,
            @Nullable Date startDate, @Nullable Date endDate) {
        return getTransactionAPI().getTransactionRefundItemSummaryByInstitution(
                institution, startDate, endDate);
    }

    private void initRefundTransactions(
            @NotNull String institutionName,
            @Nullable Date startDate, @Nullable Date endDate) {

        if (refundTransactions == null) {
            refundTransactions =
                    getRefundTransactions(institutionName, startDate, endDate);
        }
    }

    @Nullable
    private PaymentTransaction getRefundSummaryByCourse(@NotNull String courseId) {
        PaymentTransaction paymentTransaction = null;
        if (refundTransactions != null) {
            for (PaymentTransaction transaction : refundTransactions) {
                if (transaction.getItemNumber().equals(courseId)) {
                    paymentTransaction = transaction;
                    break;
                }
            }
        } else {
            throw new RuntimeException(
                    "Refund transactions have not been initialized");
        }

        return paymentTransaction;
    }

    @NotNull
    private List<PaymentTransaction> getTransactionsByInstitution(
            @NotNull String institution, @NotNull Date startDate, @NotNull Date endDate) {

        return getTransactionAPI().
                getTransactionItemSummaryByInstitution(institution, startDate, endDate);
    }

    public TransactionModel getTransactionAPI() {
        return transactionAPI;
    }
    public void setTransactionAPI(TransactionModel transactionAPI) {
        this.transactionAPI = transactionAPI;
    }
}
