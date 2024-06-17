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
import org.nterlearning.commerce.service.paypal.NVPService;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.TransactionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/18/12
 */
@ManagedBean(name = "transactionDetails")
public class TransactionDetailsBean implements Serializable {

    @ManagedProperty("#{param.institutionName}")
    private String institutionName;

    @ManagedProperty("#{param.transactionId}")
	private String transactionId;

    @ManagedProperty("#{transactionModel}")
    private TransactionModel transactionModel;

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{nvpService}")
	private NVPService nvpService;

    PaymentTransaction paymentTransaction;

    private String pageTitle;
    private static final String PAGE_TITLE_KEY = "title.transaction";

    private static final String REDIRECT_URL = "/transactionDetails.xhtml?transactionId=";

    private Logger logger = LoggerFactory.getLogger(TransactionModel.class);


    public PaymentTransaction getTransaction() {
        if (transactionId != null) {
            logger.debug("Retrieving transaction for [" + transactionId + "]");
            paymentTransaction = transactionModel.getTransactionById(transactionId);
        }
        return paymentTransaction;
    }

    public List<PaymentTransaction> getChildTransactions() {
        List<PaymentTransaction> childTransactions =
                transactionModel.getChildTransactionsById(transactionId);

        if (paymentTransaction != null &&
                paymentTransaction.getTransactionParentId() != null) {
            childTransactions.add(
                    transactionModel.getTransactionById(
                            paymentTransaction.getTransactionParentId()
                    )
            );
        }

        return childTransactions;
    }

    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getPageTitle() {
        Object[] args = new Object[]{transactionId};
        pageTitle = BeanUtil.getMessageAsString(PAGE_TITLE_KEY, args);
        return pageTitle;
    }
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public boolean hasReadAccess(String institutionName) {
        return entitlementUtil.hasReadAccess(institutionName);
    }

    public boolean isAdmin(PaymentTransaction transaction) {
        boolean isAdmin = false;
        String nterId =
            transaction.getNterId() != null ? transaction.getNterId() : null;
        if (nterId != null) {
            isAdmin = entitlementUtil.isAdmin(nterId);
        }

        String cpId =
            transaction.getCourseProviderId() != null ? transaction.getCourseProviderId() : null;

        if (!isAdmin && cpId != null) {
            isAdmin = entitlementUtil.isAdmin(cpId);
        }

        return isAdmin;
    }

    public boolean isRefunded() {
        // Check the session for refund state. Payment Processor may not have yet sent
        //  refund transaction - asynchronous notification
        Boolean sessionRefundState =
                (Boolean)BeanUtil.getAttributeFromSession(transactionId);
        if (sessionRefundState != null && sessionRefundState == Boolean.TRUE) {
            return true;
        }

        boolean isRefunded = false;

        // Is this a refund or disbursement transaction?
        PaymentTransaction transaction =
                transactionModel.getTransactionById(transactionId);
        if (transaction.getPaymentStatus() == PaymentStatus.REFUNDED ||
                transaction.getTransactionType() == TransactionType.DISBURSEMENT) {
            isRefunded = true;
        }

        // Check child transactions, has this already been refunded?
        if (!isRefunded) {
            List<PaymentTransaction> refundTransactions =
                    transactionModel.getChildTransactionsById(transactionId);

            BigDecimal amount = new BigDecimal(0);
            for (PaymentTransaction refundTransaction : refundTransactions) {
                BigDecimal paymentGross =
                        refundTransaction.getPaymentGross() != null ?
                                refundTransaction.getPaymentGross() : new BigDecimal(0);
                amount = amount.add(paymentGross);
            }

            isRefunded = amount.compareTo(new BigDecimal(0)) != 0;
        }

        return isRefunded;
    }

    public void refund(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (transactionId != null) {
            PaymentTransaction transaction =
                    transactionModel.getTransactionById(transactionId);

            if (isAdmin(transaction)) {
                String note = null;
                String merchant = BeanUtil.getLocalizedString("merchant.name");
                String response =
                    nvpService.refundTransactionCode(
                        merchant, transactionId, note);

                if (response.equals(NVPService.RESPONSE_ACK_FAILURE) ||
                    response.equals(NVPService.RESPONSE_ACK_FAILURE_W)) {
                        logger.error(
                            "Received unexpected refund response from PaymentProcessor [" +
                            response + "] for transaction [" + transactionId + "]");

                    String shortMessage =
                            nvpService.getShortErrorMessage() != null ?
                            nvpService.getShortErrorMessage() + ". " :
                            "";
                    String longMessage =
                            nvpService.getLongErrorMessage() != null ?
                            nvpService.getLongErrorMessage() + "." :
                            "";
                    FacesMessage msg =
                        new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                                shortMessage,
                                longMessage);

                    facesContext.addMessage("messages", msg);
                } else {
                    // Set refund state in http session to disable form refund button
                    BeanUtil.setAttributeInSession(transactionId, Boolean.TRUE);

                    try {
                        // Forward to: transactionDetails.xhtml
                        String contextPath = facesContext.getExternalContext().getRequestContextPath();
                        String redirectURL = contextPath + REDIRECT_URL + transactionId;

                        facesContext.getExternalContext().redirect(redirectURL);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            } else {
                logger.warn("User does not have authorization to refund transaction[" +
                    transactionId + "]");
            }
        } else {
            logger.error("Unable to refund transaction using key [" +
                    transactionId + "]");
        }
    }

    public TransactionModel getTransactionModel() {
        return transactionModel;
    }
    public void setTransactionModel(TransactionModel transactionModel) {
        this.transactionModel = transactionModel;
    }

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    public NVPService getNvpService() {
        return nvpService;
    }
    public void setNvpService(NVPService nvpService) {
        this.nvpService = nvpService;
    }
}
