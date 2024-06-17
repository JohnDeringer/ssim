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
package org.nterlearning.commerce.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.commerce.persistence.*;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.*;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0_wsdl.ValidationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Deringer
 * Date: 8/9/11
 * Time: 1:51 PM
 */
public class TransactionModelImpl implements TransactionModel {

    private Logger logger = LoggerFactory.getLogger(TransactionModelImpl.class);
    private TransactionDao transactionDao;
    private ConfigurationDao configurationDao;
    private static final String CURRENCY_TYPE_USD = "USD";
    private static final String NTER_PAY_STATUS_CHECK = "NTER";

    @Override
    public void createPaymentTransaction(
            @NotNull PaymentTransaction paymentTransaction) {

        String transactionId = paymentTransaction.getTransactionId();
        ValidationStatus validationStatus =
                paymentTransaction.getValidationStatus();
        PaymentStatus paymentStatus = paymentTransaction.getPaymentStatus();
        BigDecimal payment = paymentTransaction.getPaymentGross();

        if (validationStatus == ValidationStatus.COMPLETED &&
                paymentStatus == PaymentStatus.COMPLETED) {
            logger.info("Calculating fees for TransactionId [" +
                transactionId + "]");
            CommerceConfigEntity commerceConfig =
                        configurationDao.getCommerceConfig();
            if (commerceConfig != null) {
                if (paymentTransaction.getTransactionType() != TransactionType.DISBURSEMENT) {
                    BigDecimal referrerRate =
                        commerceConfig.getReferrerFee().divide(new BigDecimal(100));
                    BigDecimal adminRate =
                        commerceConfig.getAdminFee().divide(new BigDecimal(100));

                    BigDecimal referrerFee = payment.multiply(referrerRate);
                    BigDecimal adminFee = payment.multiply(adminRate);

                    logger.info("Referrer Fee [" + referrerFee + "]");
                    logger.info("Admin Fee [" + adminFee + "]");

                    paymentTransaction.setReferrerFee(referrerFee);
                    paymentTransaction.setAdminFee(adminFee);
                }
            } else {
                logger.error("Commerce Configuration fees have not been set");
            }
        }

        transactionDao.createTransaction(paymentTransaction);
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactions(
            @Nullable Date fromDate, @Nullable Date toDate) {

        // Date is optional, if null, return all dates
        if (fromDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -100);
            fromDate = cal.getTime();
        }
        if (toDate == null) {
            toDate = new Date();
        }

        List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();
        for (PaymentTransactionEntity paymentTransaction :
                transactionDao.getAllTransactions(fromDate, toDate)) {
            paymentTransactions.add(paymentTransaction.getPaymentTransaction());
        }

        return paymentTransactions;
    }

    @Override
    @Nullable
    public PaymentTransaction getTransactionById(@NotNull String transactionId) {
        PaymentTransaction paymentTransaction = null;
        List<PaymentTransactionEntity> paymentTransactions =
                    transactionDao.getTransactionsById(transactionId);
        if (!paymentTransactions.isEmpty()) {
            paymentTransaction = paymentTransactions.get(0).getPaymentTransaction();
        }

        return paymentTransaction;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getChildTransactionsById(
            @NotNull String transactionId) {

        List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();
        for (PaymentTransactionEntity paymentTransaction :
                transactionDao.getChildTransactionsById(transactionId)) {
            paymentTransactions.add(paymentTransaction.getPaymentTransaction());
        }

        return paymentTransactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionsByStudentId(
            @NotNull String studentId, @Nullable Date fromDate, @Nullable Date toDate) {

        // Date is optional, if null, return all dates
        if (fromDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -100);
            fromDate = cal.getTime();
        }
        if (toDate == null) {
            toDate = new Date();
        }

        List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();
        for (PaymentTransactionEntity paymentTransaction :
                transactionDao.getTransactionsByStudentId(studentId, fromDate, toDate)) {
            paymentTransactions.add(paymentTransaction.getPaymentTransaction());
        }

        return paymentTransactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionsByTransactionType(
            String institution, TransactionType transactionType,
            Date fromDate, Date toDate) {

        // Date is optional, if null, return all dates
        if (fromDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -100);
            fromDate = cal.getTime();
        }
        if (toDate == null) {
            toDate = new Date();
        }

        List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();
        for (PaymentTransactionEntity paymentTransaction :
                transactionDao.getTransactionsByTransactionType(
                        institution, transactionType, fromDate, toDate)) {
            paymentTransactions.add(paymentTransaction.getPaymentTransaction());
        }

        return paymentTransactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionsByInstitution(
            @NotNull String institution,
            @Nullable Date fromDate, @Nullable Date toDate) {

        // Date is optional, if null, return all dates
        if (fromDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -100);
            fromDate = cal.getTime();
        }
        if (toDate == null) {
            toDate = new Date();
        }

        List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();
        for (PaymentTransactionEntity paymentTransaction :
                transactionDao.getTransactionsByInstitution(
                        institution, fromDate, toDate)) {
            paymentTransactions.add(paymentTransaction.getPaymentTransaction());
        }

        return paymentTransactions;
    }

    @Override
    @NotNull
    public Map<String, BigDecimal> getBalanceByInstitution() {
        Map<String, BigDecimal> balanceMap =
                transactionDao.getBalanceByContentProvider();
        Map<String, BigDecimal> nterMap =
                transactionDao.getBalanceByNterInstance();

        // Iterate through NTER instances and add to balanceMap
        //  Add NTER balance to NWTP balance when NTER & NWTP are the same institution
        for (String nterId : nterMap.keySet()) {
            BigDecimal nterBalance =
                    nterMap.get(nterId) != null ?
                    nterMap.get(nterId) : new BigDecimal(0);
            // Does NWTP map have a matching institution
            BigDecimal cpBalance = balanceMap.get(nterId);
            if (cpBalance != null) {
                nterBalance = nterBalance.add(cpBalance);
            }

            balanceMap.put(nterId, nterBalance);
        }

        // Order the list
        balanceMap = ModelUtil.sortByValue(balanceMap);

        return balanceMap;
    }

    @Override
    @NotNull
    public BigDecimal getTransactionBalanceByInstitution(@NotNull String institution) {
        BigDecimal balance =
                transactionDao.getTransactionBalanceByContentProvider(institution);
        balance =
                balance.add(
                        transactionDao.getTransactionBalanceByNterInstance(institution)
                );
        return balance;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionItemSummaryByInstitution(
            @NotNull String institution,
            @Nullable Date fromDate, @Nullable Date toDate) {

        // Date is optional, if null, return all dates
        if (fromDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -100);
            fromDate = cal.getTime();
        }
        if (toDate == null) {
            toDate = new Date();
        }

        List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();
        for (PaymentTransactionEntity paymentTransaction :
                transactionDao.getTransactionItemSummaryByInstitution(
                        institution, fromDate, toDate)) {
            paymentTransactions.add(paymentTransaction.getPaymentTransaction());
        }

        return paymentTransactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionRefundItemSummaryByInstitution(
            @NotNull String institution,
            @Nullable Date fromDate, @Nullable Date toDate) {

        // Date is optional, if null, return all dates
        if (fromDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -100);
            fromDate = cal.getTime();
        }
        if (toDate == null) {
            toDate = new Date();
        }

        List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();
        for (PaymentTransactionEntity paymentTransaction :
                transactionDao.getTransactionRefundItemSummaryByInstitution(
                        institution, fromDate, toDate)) {
            paymentTransactions.add(paymentTransaction.getPaymentTransaction());
        }

        return paymentTransactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionsByCourseId(
            @NotNull String courseId,
            @Nullable Date fromDate, @Nullable Date toDate) {

        // Date is optional, if null, return all dates
        if (fromDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -100);
            fromDate = cal.getTime();
        }
        if (toDate == null) {
            toDate = new Date();
        }

        List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();
        for (PaymentTransactionEntity paymentTransaction :
                transactionDao.getTransactionsByCourseId(courseId, fromDate, toDate)) {
            paymentTransactions.add(paymentTransaction.getPaymentTransaction());
        }

        return paymentTransactions;
    }

    /**
     * Verification
     *  paymentStatus=Completed
     *  txnId has not been previously processed
     *  receiverEmail is your Primary PayPal email
     *  paymentAmount/paymentCurrency are correct
     */
    @Override
    @NotNull
    public PaymentStatus getPaymentStatus(
        @NotNull String institution, @NotNull String studentId,
        @NotNull String courseId, @NotNull BigDecimal price)
            throws ValidationError {

        PaymentStatus paymentStatus = PaymentStatus.UNKNOWN;

        // Calculate amount paid
        BigDecimal amountPaid = new BigDecimal(0);
        List<PaymentTransactionEntity> transactions;
        // institution == NTER_PAY_STATUS_CHECK :
        //  An NTER instance is checking PaymentStatus
        //  Don't check institution for NTER portal
        if (institution.equalsIgnoreCase(NTER_PAY_STATUS_CHECK)) {
            transactions =
                transactionDao.getTransactionsByStudentAndCourse(studentId, courseId);
        } else {
            transactions =
                transactionDao.getTransactionsByStudentAndCourse(
                        institution, studentId, courseId);
        }

        // Loop through transaction and add amount paid
        for (PaymentTransactionEntity paymentTransaction : transactions) {
            logger.info("GetPaymentStatus found transaction [" +
                paymentTransaction.getTransactionId() + "] for student [" +
                studentId + "] and course [" + courseId + "]");

            // Add payments - check currencyType
            String currencyType = paymentTransaction.getCurrencyType();
            if (currencyType.equals(CURRENCY_TYPE_USD)) {
                BigDecimal paymentGross = paymentTransaction.getPaymentGross();
                if (paymentGross != null) {
                    logger.debug("GetPaymentStatus transaction [" +
                    paymentTransaction.getTransactionId() +
                        "] recorded payment [" +
                        paymentGross + "]");
                    amountPaid = amountPaid.add(paymentGross);
                    // Latest PaymentStatus
                    paymentStatus = paymentTransaction.getPaymentStatus();
                }
            } else {
                logger.warn("GetPaymentStatus transaction [" +
                    paymentTransaction.getTransactionId() +
                        "] has an invalid currencyType [" +
                        currencyType + "] payment will not be accepted");
            }

            // Validation - sellerId
            PaymentProcessor processor = paymentTransaction.getPaymentProcessor();
            String sellerId =
                    getPaymentProcessorSellerId(processor);
            String receiverId = paymentTransaction.getReceiverId();
            if (!receiverId.equals(sellerId)) {
                logger.error("ReceiverID [" + receiverId + "] sent by " +
                    paymentTransaction.getPaymentProcessor().value() +
                    " does not match the SellerId [" + sellerId + "] from " +
                    "Configuration. see transactionId [" +
                    paymentTransaction.getTransactionId() + "]");
                throw new ValidationError(
                    "SellerId [" + receiverId + "] from " + paymentTransaction.getPaymentProcessor().value() +
                            " does not match the sellerId [" + sellerId + "] on record");
            }

            // Check correct seller received payment
            //  Only required for NTER status check, ContentProvider transactions are already filtered
//            if (institution.equalsIgnoreCase(NTER_PAY_STATUS_CHECK)) {
//                if (!institution.equals(paymentTransaction.getCourseProviderId())) {
//                    logger.error("Content Provider ID [" + paymentTransaction.getCourseProviderId() +
//                        "] sent by " + paymentTransaction.getPaymentProcessor().value() +
//                            " does not match the institution [" +
//                        institution +
//                        "] supplied in the PaymentStatus request. see transactionId [" +
//                        paymentTransaction.getTransactionId() + "]");
//                throw new ValidationError(
//                    "ContentProviderId [" + paymentTransaction.getPaymentProcessor().value() +
//                            "] does not match the institution [" +
//                            institution + "] supplied with PaymentStatus request");
//                }
//            }
        }

        // Validation - amountPaid
        if (amountPaid.compareTo(price) < 0) {
            throw new ValidationError(
                "Amount paid [" + amountPaid +
                        "] is less than product price [" + price + "]");
        }

        return paymentStatus;
    }

    @Nullable
    private String getPaymentProcessorSellerId(@NotNull PaymentProcessor paymentProcessor) {
        String sellerId = null;
        PaymentConfigEntity entry =
                configurationDao.getPaymentConfig(paymentProcessor);
        if (entry != null) {
            sellerId = entry.getSellerId();
        } else {
            logger.error("Payment configuration has not be set for [" +
                paymentProcessor + "]");
        }

        return sellerId;
    }

    /**
     * Spring dependency injection
     * @param transactionDao TransactionDao
     */
    public void setTransactionDao(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public void setConfigurationDao(ConfigurationDao configurationDao) {
        this.configurationDao = configurationDao;
    }

}
