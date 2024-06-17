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

package org.nterlearning.commerce.form;

import org.jetbrains.annotations.NotNull;

import org.nterlearning.commerce.managed.AccountingBean;
import org.nterlearning.commerce.managed.BeanUtil;
import org.nterlearning.commerce.managed.EntitlementUtil;
import org.nterlearning.commerce.model.TransactionModel;
import org.nterlearning.commerce.service.paypal.NVPService;
import org.nterlearning.commerce.service.paypal.PaymentItem;

import org.nterlearning.registry.client.Contact;
import org.nterlearning.registry.client.Institution;
import org.nterlearning.registry.client.RegistryImpl;

import org.primefaces.context.RequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 6/5/12
 */
@ManagedBean(name = "paymentForm")
public class PaymentForm implements Serializable {

    @ManagedProperty("#{nvpService}")
	private NVPService nvpService;

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{registryService}")
    private RegistryImpl registryService;

    @ManagedProperty("#{transactionModel}")
    private TransactionModel transactionModel;

    @ManagedProperty("#{param['institutionName']}")
    private String institutionName;

    private String email;
    private BigDecimal amount;
    private String note;

    private Logger logger = LoggerFactory.getLogger(PaymentForm.class);

    @PostConstruct
    public void init() {
        if (institutionName != null) {
            Institution institution =
                    registryService.getInstitutionByName(institutionName);
            if (institution != null) {
                Contact contact = institution.getContactInfo();
                if (contact != null) {
                    email = contact.getEmail();
                } else {
                    logger.error("Invalid ContactInfo [" + contact +
                            "] for Institution [" + institutionName + "]");
                }
            } else {
                logger.warn("Unable to retrieve institution [" + institutionName + "]");
            }

            amount =
                    transactionModel.getTransactionBalanceByInstitution(
                            institutionName
                    );
            if (amount != null) {
                amount = amount.setScale(2, RoundingMode.HALF_UP);
            } else {
                amount = new BigDecimal(0);
            }
        }
    }

    public String getInstitutionName() {
        return institutionName;
    }
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public void save(ActionEvent actionEvent) {
        if (entitlementUtil.isGlobalAdmin()) {
            // TODO: ?query DB to ensure payment is due?

            RequestContext context = RequestContext.getCurrentInstance();
            FacesMessage msg = null;
            boolean valid = false;

            FacesContext facesContext = FacesContext.getCurrentInstance();
            Locale locale = facesContext.getViewRoot().getLocale();
            ResourceBundle resourceBundle =
                ResourceBundle.getBundle("messages", locale);

            String merchant = BeanUtil.getLocalizedString("merchant.name");

            String receiverEmail = getEmail();
            // Test for null
            if (receiverEmail == null || receiverEmail.isEmpty()) {
                logger.warn("Email is required [" + receiverEmail + "]");

                msg =
                    new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                            "",
                            resourceBundle.getString("forms.payment.email.required"));

                facesContext.addMessage(null, msg);
            } else {
                // Test for
                Pattern p =
                    Pattern.compile(
                            resourceBundle.getString("forms.payment.email.regex")
                    );
                Matcher m = p.matcher(receiverEmail);
                boolean match = m.matches();

                if (!match) {
                    logger.warn("Receiver email is invalid [" + receiverEmail + "]");
                    msg =
                        new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                                "",
                                resourceBundle.getString("forms.payment.email.invalid"));

                    facesContext.addMessage(null, msg);
                }
            }

            BigDecimal amount = getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                logger.warn("Amount is required [" + amount + "]");

                msg =
                    new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                            "",
                            resourceBundle.getString("forms.payment.amount.required"));

                facesContext.addMessage(null, msg);
            }

            if (msg == null) {
                valid = true;

                String note = getNote();
                PaymentItem paymentItem =
                        new PaymentItem(receiverEmail, amount, note);

                String subject = BeanUtil.getLocalizedString("label.payment.subject");

                String response = nvpService.payment(merchant, subject, paymentItem);
                if (response.equals(NVPService.RESPONSE_ACK_FAILURE) ||
                            response.equals(NVPService.RESPONSE_ACK_FAILURE_W)) {

                    valid = false;
                    logger.error("Received unexpected payment response from PaymentProcessor [" +
                        response + "] for institution [" + institutionName +
                            "] using receiver email [" + receiverEmail + "]");

                    String shortMessage =
                            nvpService.getShortErrorMessage() != null ?
                            nvpService.getShortErrorMessage() + ". " :
                            "";
                    String longMessage =
                            nvpService.getLongErrorMessage() != null ?
                            nvpService.getLongErrorMessage() + "." :
                            "";
                    msg =
                        new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                                shortMessage,
                                longMessage);

                    facesContext.addMessage(null, msg);
                }
            }

            if (valid) {
                // Remove item from list
                removeAccountingItem(institutionName);

                msg =
                    new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                            "",
                            resourceBundle.getString("forms.payment.success"));

                facesContext.addMessage(null, msg);
            }

            // Return validation flag back to client
            context.addCallbackParam("valid", valid);
        } else {
            logger.warn("User [" + entitlementUtil.getEmail() +
                    "] does not have authorization to call this operation");
        }
    }

    private void removeAccountingItem(@NotNull String institutionName) {
        // Retrieve institution balances from http session
        @SuppressWarnings("unchecked")
        List<AccountingBean.AccountingItem> accountingItems =
            (ArrayList<AccountingBean.AccountingItem>)
                    BeanUtil.getAttributeFromSession(
                            AccountingBean.AccountingItem.class.getCanonicalName()
                    );
        if (accountingItems != null) {
            // Search for accounting by institution name
            AccountingBean.AccountingItem accountingItem =
                                    new AccountingBean.AccountingItem();
            accountingItem.setInstitutionName(institutionName);
            int itemIndex = accountingItems.indexOf(accountingItem);
            // If found remove accounting item
            if (itemIndex > -1) {
                accountingItem = accountingItems.remove(itemIndex);
                accountingItem = null;
                // Update http session
                BeanUtil.setAttributeInSession(
                        AccountingBean.AccountingItem.class.getCanonicalName(),
                        accountingItems);
            }
        }
    }

    public String escape(String value) {
        return BeanUtil.escape(value);
    }

    public NVPService getNvpService() {
        return nvpService;
    }
    public void setNvpService(NVPService nvpService) {
        this.nvpService = nvpService;
    }

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    public RegistryImpl getRegistryService() {
        return registryService;
    }
    public void setRegistryService(RegistryImpl registryService) {
        this.registryService = registryService;
    }

    public TransactionModel getTransactionModel() {
        return transactionModel;
    }
    public void setTransactionModel(TransactionModel transactionModel) {
        this.transactionModel = transactionModel;
    }
}
