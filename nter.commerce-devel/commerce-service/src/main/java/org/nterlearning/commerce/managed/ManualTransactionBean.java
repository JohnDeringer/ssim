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

package org.nterlearning.commerce.managed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nterlearning.commerce.model.ConfigurationModel;
import org.nterlearning.commerce.model.TransactionModel;
import org.nterlearning.registry.client.ActiveStatusEnum;
import org.nterlearning.registry.client.Institution;
import org.nterlearning.registry.client.RegistryImpl;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.*;

import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/18/12
 */
@ManagedBean(name = "manualTransaction")
public class ManualTransactionBean implements Serializable {

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{transactionModel}")
    private TransactionModel transactionModel;

    @ManagedProperty("#{configurationModel}")
    private ConfigurationModel configurationModel;

    @ManagedProperty("#{registryService}")
    private RegistryImpl registryService;

    private String institutionName;
    private PaymentProcessor paymentProcessor;
    private String receiverEmail;
    private TransactionType transactionType;
    private PaymentType paymentType;
    private BigDecimal amount;

    private List<Institution> institutions;

    private static final String CURRENCY_TYPE_USD = "USD";

    private Logger logger = LoggerFactory.getLogger(ManualTransactionBean.class);

    public ManualTransactionBean() {
        institutions = new ArrayList<Institution>();
    }

    public String getInstitutionName() {
        return institutionName;
    }
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }
    public void setPaymentProcessor(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }
    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public List<Institution> getInstitutions() {
        if (institutions.isEmpty()) {
            institutions = registryService.getInstitutions(ActiveStatusEnum.ACTIVE);
        }
        return institutions;
    }

    public List<String> getInstitutionNames() {
        List<String> institutionNames = new ArrayList<String>();
        for (Institution institution : getInstitutions()) {
            institutionNames.add(institution.getName());
        }
        return institutionNames;
    }

    public List<PaymentProcessor> getPaymentProcessors() {
        return Arrays.asList(PaymentProcessor.values());
    }

    public List<TransactionType> getTransactionTypes() {
        return Arrays.asList(TransactionType.values());
    }

    public List<PaymentType> getPaymentTypes() {
        return Arrays.asList(PaymentType.values());
    }

    public void saveTransaction(ActionEvent actionEvent) {
        saveTransaction();
    }

    public void saveTransaction() {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Locale locale = facesContext.getViewRoot().getLocale();
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("messages", locale);
        FacesMessage msg = null;
        boolean valid = false;

        if (institutionName == null || institutionName.isEmpty()) {
            logger.warn("Institution name is required");

            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                        resourceBundle.getString("forms.manualTransaction"),
                        resourceBundle.getString("forms.manualTransaction.required.name"));

              FacesContext.getCurrentInstance().addMessage(null, msg);
         }

        if (msg == null) {
            msg = save(resourceBundle, locale);
        }

        if (msg == null) {
            valid = true;
            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                        resourceBundle.getString("forms.manualTransaction"),
                        resourceBundle.getString("forms.manualTransaction.required.success"));

            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        requestContext.addCallbackParam("valid", valid);
    }

    public FacesMessage save(ResourceBundle resourceBundle, Locale locale) {
        FacesMessage msg = null;
        try {
            String _institutionName = BeanUtil.unescape(institutionName);

            logger.info("Adding transaction for institution [" +
                        _institutionName + "]");

            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setPaymentProcessor(paymentProcessor);
            paymentTransaction.setTransactionId(generateTransactionId());

            String itemName = TransactionType.DISBURSEMENT.value();
            String itemNumber = TransactionType.DISBURSEMENT.value();

            paymentTransaction.setItemName(itemName);
            paymentTransaction.setItemNumber(itemNumber);

            // PaymentDate
            paymentTransaction.setPaymentDate(
                    BeanUtil.toXMLGregorianCalendar(new Date()));

            // Payment Status
            paymentTransaction.setPaymentStatus(PaymentStatus.COMPLETED);

            // Payment Type
            paymentTransaction.setPaymentType(paymentType);

            // TransactionType
            paymentTransaction.setTransactionType(transactionType);

            // Currency Type
            paymentTransaction.setCurrencyType(CURRENCY_TYPE_USD);

            // Payment Gross
            if (amount != null) {
                try {
                    if (transactionType == TransactionType.DISBURSEMENT ||
                            transactionType == TransactionType.ADJUSTMENT) {
                        amount = amount.multiply(new BigDecimal(-1));
                    }
                } catch (Exception e) {
                    logger.error("Error processing payment amount [" + amount + "]");
                }
                paymentTransaction.setPaymentGross(amount);
            }
            // ReceiverEmail if paying through PaymentProcessor
            if (receiverEmail != null) {
                paymentTransaction.setReceiverEmail(receiverEmail);
            }

            PaymentConfig paymentConfig = getPaymentConfig();

            // Seller email
            String sellerEmail = paymentConfig.getSellerEmail();
            if (sellerEmail != null) {
                paymentTransaction.setPayerEmail(sellerEmail);
            } else {
                logger.error("Invalid Seller Email from PaymentConfig [" +
                    sellerEmail + "]");
            }

            // SellerId
            paymentTransaction.setPayerId(paymentConfig.getSellerId());

            // Institution
            if (isNterInstance(institutionName)) {
                paymentTransaction.setNterId(institutionName);
            } else {
                paymentTransaction.setCourseProviderId(institutionName);
            }

            // PaymentProcessor
            paymentTransaction.setPaymentProcessor(paymentProcessor);

            // TODO: Verify sign if using PaymentProcessor?
            //paymentTransaction.setVerifySign(verifySign);

            // Persist new PaymentTransaction
            if (entitlementUtil.isAdmin(institutionName)) {
                transactionModel.createPaymentTransaction(paymentTransaction);
            } else {
                logger.warn("Unable to create disbursement transaction User does not have write permission");
            }

        } catch (Exception e) {
            logger.error("Unexpected error create disbursement transaction", e);
            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    resourceBundle.getString("forms.service.add"),
                        e.getMessage());

            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return msg;
    }

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    public TransactionModel getTransactionModel() {
        return transactionModel;
    }
    public void setTransactionModel(TransactionModel transactionModel) {
        this.transactionModel = transactionModel;
    }

    public RegistryImpl getRegistryService() {
        return registryService;
    }
    public void setRegistryService(RegistryImpl registryService) {
        this.registryService = registryService;
    }

    public ConfigurationModel getConfigurationModel() {
        return configurationModel;
    }
    public void setConfigurationModel(ConfigurationModel configurationModel) {
        this.configurationModel = configurationModel;
    }

    private PaymentConfig getPaymentConfig() {
        return configurationModel.getPaymentConfig(PaymentProcessor.PAY_PAL);
    }

    private boolean isNterInstance(String institutionName) {
        boolean isNterInstance = false;
        List<PaymentTransaction> transactions =
                transactionModel.getTransactionItemSummaryByInstitution(
                        institutionName, null, null);
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getNterId().equals(institutionName)) {
                isNterInstance = true;
                break;
            } else if (transaction.getCourseProviderId().equals(institutionName)) {
                isNterInstance = false;
                break;
            }
        }
        return isNterInstance;
    }

    private String generateTransactionId() {
        Random random = new Random();
        String transId;
        while (true) {
            long randomLong = random.nextLong();
            randomLong = randomLong > 0 ? randomLong : randomLong * -1;
            transId = "NTER:" + randomLong;

            // Ensure transactionId does not already exist
            if (transactionModel.getTransactionById(transId) == null) {
                break;
            }
        }

        return transId;
    }
}
