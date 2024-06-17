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
package org.nterlearning.commerce.controller;

import org.jetbrains.annotations.NotNull;

import org.nterlearning.commerce.managed.BeanUtil;
import org.nterlearning.commerce.managed.EntitlementUtil;
import org.nterlearning.commerce.model.TransactionModel;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.TransactionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/9/12
 */
@ManagedBean(name = "transactions")
public class TransactionsController implements Serializable {

	@ManagedProperty("#{transactionModel}")
	private TransactionModel transactionAPI;

    @ManagedProperty("#{entitlementUtil}")
	private EntitlementUtil entitlementUtil;

	@ManagedProperty("#{param.institutionName}")
	private String institutionName;

    @ManagedProperty("#{param.transactionId}")
	private String transactionId;

    private List<PaymentTransaction> paymentTransactions;
	private Date startDate;
	private Date endDate;

    private static final String DATA_CACHE_NAME = TransactionsController.class.getName();
    private static final String INSTITUTION_NAME = "institutionName" + DATA_CACHE_NAME;
    private static final String START_DATE = "startDate" + DATA_CACHE_NAME;
    private static final String END_DATE = "endDate" + DATA_CACHE_NAME;

    private static final String REDIRECT_URL = "/transactions.xhtml?institutionName=";

    private Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    public TransactionsController() {

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

	@PostConstruct
	private void init() {
        logger.debug("init.institutionName [" + institutionName + "]");
	}

    @SuppressWarnings("unchecked")
    public List<PaymentTransaction> getTransactions() {
        if (paymentTransactions.isEmpty()) {
            // Is the data already cached?
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
            String institutionName = getInstitutionName();
            Date startDate = getStartDate();
            Date endDate = getEndDate();
            paymentTransactions =
                    getTransactionsByInstitution(
                            institutionName, startDate, endDate);
        }

        return paymentTransactions;
    }

    @NotNull
    private List<PaymentTransaction> getTransactionsByInstitution(
            @NotNull String institution, @NotNull Date startDate, @NotNull Date endDate) {

        List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();

        for (PaymentTransaction transaction: getTransactionAPI().
                getTransactionsByInstitution(institution, startDate, endDate)) {
            String nterId = transaction.getNterId();
            String cpId = transaction.getCourseProviderId();
            if (entitlementUtil.hasReadAccess(nterId) ||
                    entitlementUtil.hasReadAccess(cpId)) {
                paymentTransactions.add(transaction);
            }
        }

        // Cache the transaction data
        BeanUtil.setAttributeInSession(DATA_CACHE_NAME, paymentTransactions);

        return paymentTransactions;
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

        // Clear transaction session data
        BeanUtil.setAttributeInSession(DATA_CACHE_NAME, null);

        // Refresh page
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
                    (String)BeanUtil.getAttributeFromSession(INSTITUTION_NAME);
        }
		return institutionName;
	}
	public void setInstitutionName(String institutionName) {
        if (institutionName != null && !institutionName.isEmpty()) {
            // Is there any cached data?
            String cachedInstitutionName =
                    (String)BeanUtil.getAttributeFromSession(INSTITUTION_NAME);
            // If the institutionName has changed flush the data cache
            if (cachedInstitutionName != null &&
                    !cachedInstitutionName.equals(institutionName)) {
                BeanUtil.setAttributeInSession(DATA_CACHE_NAME, null);
            }

            // Update institutionName
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

    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getRevenue(PaymentTransaction transaction) {
        BigDecimal revenue = new BigDecimal(0);
        String nterId = transaction.getNterId() != null ?
                transaction.getNterId() : "";
        String cpId = transaction.getCourseProviderId() != null ?
                transaction.getCourseProviderId() : "";

        // Institution is NTER portal
        if (nterId.equals(getInstitutionName())) {
            // Institution is both NTER portal & Content Provider
            if (nterId.equals(cpId)) {
                BigDecimal paymentGross = (transaction.getPaymentGross() != null ? transaction.getPaymentGross() : revenue);
                BigDecimal adminFee = (transaction.getAdminFee() != null ? transaction.getAdminFee() : revenue);
                BigDecimal paymentFee = (transaction.getPaymentFee() != null ? transaction.getPaymentFee() : revenue);

                if (transaction.getTransactionType() == TransactionType.DISBURSEMENT) {
                    revenue = paymentGross.multiply(new BigDecimal(-1));
                } else {
                    if (transaction.getPaymentStatus() == PaymentStatus.REFUNDED) {
                        revenue =
                            paymentGross.subtract(paymentFee);
                    } else {
                        revenue =
                            paymentGross.subtract(adminFee).subtract(paymentFee);
                    }
                }
            } else {
                // Institution is NTER portal only
                if (transaction.getTransactionType() == TransactionType.DISBURSEMENT) {
                    BigDecimal paymentGross = (transaction.getPaymentGross() != null ? transaction.getPaymentGross() : revenue);
                    revenue = paymentGross.multiply(new BigDecimal(-1));
                } else {
                    revenue = transaction.getReferrerFee();
                }
            }
            // Institution is a Content Provider
        } else if (cpId.equals(getInstitutionName())) {
            BigDecimal paymentGross = (transaction.getPaymentGross() != null ? transaction.getPaymentGross() : revenue);
            BigDecimal referrerFee = (transaction.getReferrerFee() != null ? transaction.getReferrerFee() : revenue);
            BigDecimal adminFee = (transaction.getAdminFee() != null ? transaction.getAdminFee() : revenue);
            BigDecimal paymentFee = (transaction.getPaymentFee() != null ? transaction.getPaymentFee() : revenue);

            if (transaction.getTransactionType() == TransactionType.DISBURSEMENT) {
                revenue = paymentGross.multiply(new BigDecimal(-1));
            } else {
                if (transaction.getPaymentStatus() == PaymentStatus.REFUNDED) {
                    revenue =
                        paymentGross.subtract(paymentFee);
                } else {
                    revenue =
                        paymentGross.subtract(referrerFee).subtract(adminFee).subtract(paymentFee);
                }
            }
        }

        return revenue;
    }

    public String getInstitutionName(PaymentTransaction transaction) {
        String nterId =
                transaction.getNterId() != null ? transaction.getNterId() : "";
        String cpId =
                transaction.getCourseProviderId() != null ? transaction.getCourseProviderId() : "";

        return (!nterId.equals(getInstitutionName()) ? nterId : cpId);
    }

    public boolean isAdmin(PaymentTransaction transaction) {
        boolean isAdmin = false;
        String institution = getInstitutionName(transaction);
        if (institution != null) {
            isAdmin = entitlementUtil.isAdmin(institution);
        }
        return isAdmin;
    }

    public boolean hasReadAccess(PaymentTransaction transaction) {
        boolean hasReadAccess = false;
        String institution = getInstitutionName(transaction);
        if (institution != null) {
            hasReadAccess = entitlementUtil.hasReadAccess(institution);
        }
        return hasReadAccess;
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
