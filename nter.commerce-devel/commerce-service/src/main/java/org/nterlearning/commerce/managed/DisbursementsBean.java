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
import org.nterlearning.xml.commerce.domain_objects_0_1_0.TransactionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/25/12
 */
@ManagedBean(name = "disbursements")
public class DisbursementsBean implements Serializable {

    @ManagedProperty("#{transactionModel}")
    private TransactionModel transactionAPI;

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

	@ManagedProperty("#{param.institutionName}")
    private String institutionName;

    private Date startDate;
    private Date endDate;

    private static final String DATA_CACHE_NAME = DisbursementsBean.class.getName();
    private static final String INSTITUTION_NAME = "institutionName" + DATA_CACHE_NAME;
    private static final String START_DATE = "startDate" + DATA_CACHE_NAME;
    private static final String END_DATE = "endDate" + DATA_CACHE_NAME;

    private static final String REDIRECT_URL = "/disbursements.xhtml?institutionName=";

    private Logger logger = LoggerFactory.getLogger(DisbursementsBean.class);

    public DisbursementsBean() {

        Date startDate = getStartDate();
        Date endDate = getEndDate();

        if (startDate == null) {
            setStartDate(BeanUtil.getFirstDayOfMonth());
        }

        if (endDate == null) {
            setEndDate(new Date());
        }

    }

    public List<PaymentTransaction> getTransactions() {
        String institutionName = getInstitutionName();
        Date startDate = getStartDate();
        Date endDate = getEndDate();

        // TODO: Is entitlement needed here? Menu item won't be available?
//        for (PaymentTransaction paymentTransaction : transactions) {
//            if (entitlementUtil.hasReadAccess(paymentTransaction.getCourseProviderId()) ||
//                    entitlementUtil.hasReadAccess(paymentTransaction.getNterId())) {
//                disbursementTransactions.add(paymentTransaction);
//            }
//        }

        return transactionAPI.getTransactionsByTransactionType(
                institutionName, TransactionType.DISBURSEMENT,
                    startDate, endDate);
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

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
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

    public TransactionModel getTransactionAPI() {
        return transactionAPI;
    }
    public void setTransactionAPI(TransactionModel transactionAPI) {
        this.transactionAPI = transactionAPI;
    }
}
