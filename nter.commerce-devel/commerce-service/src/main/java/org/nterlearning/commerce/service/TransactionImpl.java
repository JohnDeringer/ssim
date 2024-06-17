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
package org.nterlearning.commerce.service;

import java.math.BigDecimal;

import java.util.*;

import org.nterlearning.commerce.model.TransactionModel;
import org.nterlearning.commerce.service.paypal.PayPalValidation;
import org.nterlearning.entitlement.client.Action;
import org.nterlearning.entitlement.client.Entitlement;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.*;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0_wsdl.TransactionInterface;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0_wsdl.ValidationError;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/11/12
 */
public class TransactionImpl
        implements TransactionInterface, TransactionRestInterface {

    private Logger logger = LoggerFactory.getLogger(TransactionImpl.class);
    private TransactionModel transactionModel;
    private Entitlement entitlement;
    private WSValidator wsValidator;
    private String commerceRealm;
    private PayPalValidation payPalValidation;

    public TransactionImpl() {
    }

    @Override
    public GetTransactionByIdResponse getTransactionById(
            GetTransactionById request) {

        String authorizingSubject = wsValidator.getUid();

        return getTransactionById(authorizingSubject, request.getTransactionId());
    }

    @Override
    public GetChildTransactionsByIdResponse getChildTransactionsById(
            GetChildTransactionsById request) {
        GetChildTransactionsByIdResponse response =
                new GetChildTransactionsByIdResponse();
        List<PaymentTransaction> paymentTransactions =
                new ArrayList<PaymentTransaction>();

        String transactionId = request.getTransactionId();

        List<PaymentTransaction> transactions =
                transactionModel.getChildTransactionsById(
                        transactionId);

        String authorizingSubject = wsValidator.getUid();

        for (PaymentTransaction transaction : transactions) {
            boolean isAuthorized = false;
            String cpId = transaction.getCourseProviderId();
            String nterId = transaction.getNterId();

            if (cpId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, cpId, Action.READ);
            }

            if (!isAuthorized && nterId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, nterId, Action.READ);
            }

            if (!isAuthorized) {
                logger.info(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to retrieve a transaction for institution(s) [" +
                    (cpId != null ? cpId + "]" : "") +
                    (cpId != null && nterId != null ? " or [" : "") +
                    (nterId != null ? nterId + "]" : ""));
            } else {
                paymentTransactions.add(transaction);
            }
        }

        response.getPaymentTransaction().addAll(paymentTransactions);

        return response;
    }

    @Override
    public GetTransactionsByStudentIdResponse getTransactionsByStudentId(
            GetTransactionsByStudentId request) {

        GetTransactionsByStudentIdResponse response =
                new GetTransactionsByStudentIdResponse();
        List<PaymentTransaction> paymentTransactions =
                new ArrayList<PaymentTransaction>();

        String studentId = request.getStudentId();
        Date fromDate = ServiceUtil.toDate(request.getFromDate());
        Date toDate = ServiceUtil.toDate(request.getToDate());

        List<PaymentTransaction> transactions =
                transactionModel.getTransactionsByStudentId(
                        studentId, fromDate, toDate);

        String authorizingSubject = wsValidator.getUid();

        for (PaymentTransaction transaction : transactions) {
            boolean isAuthorized = false;
            String cpId = transaction.getCourseProviderId();
            String nterId = transaction.getNterId();

            if (cpId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, cpId, Action.READ);
            }

            if (!isAuthorized && nterId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, nterId, Action.READ);
            }

            if (!isAuthorized) {
                logger.info(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to retrieve a transaction for institution(s) [" +
                    (cpId != null ? cpId + "]" : "") +
                    (cpId != null && nterId != null ? " or [" : "") +
                    (nterId != null ? nterId + "]" : ""));
            } else {
                paymentTransactions.add(transaction);
            }
        }

        response.getPaymentTransaction().addAll(paymentTransactions);

        return response;
    }

    @Override
    public GetTransactionsByInstitutionResponse getTransactionsByInstitution(
            GetTransactionsByInstitution request) {

        GetTransactionsByInstitutionResponse response =
                new GetTransactionsByInstitutionResponse();

        String institutionName = request.getInstitution();
        Date fromDate = ServiceUtil.toDate(request.getFromDate());
        Date toDate = ServiceUtil.toDate(request.getToDate());

        String authorizingSubject = wsValidator.getUid();

        if (!entitlement.isAuthorized(
                    commerceRealm, authorizingSubject,
                    institutionName, Action.READ)) {

            throw new RuntimeException(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to retrieve a transaction for institution [" +
                    institutionName + "]");
        }

        List<PaymentTransaction> transactions =
                transactionModel.getTransactionsByInstitution(
                        institutionName, fromDate, toDate);

        response.getPaymentTransaction().addAll(transactions);

        return response;
    }

    @Override
    public GetTransactionSummaryByInstitutionResponse getTransactionSummaryByInstitution(
            GetTransactionSummaryByInstitution request) {
        GetTransactionSummaryByInstitutionResponse response =
                new GetTransactionSummaryByInstitutionResponse();

        String institutionName = request.getInstitution();
        Date fromDate = ServiceUtil.toDate(request.getFromDate());
        Date toDate = ServiceUtil.toDate(request.getToDate());

        String authorizingSubject = wsValidator.getUid();

        if (!entitlement.isAuthorized(
                    commerceRealm, authorizingSubject,
                    institutionName, Action.READ)) {

            throw new RuntimeException(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to retrieve a transaction for institution [" +
                    institutionName + "]");
        }

        List<PaymentTransaction> transactions =
                transactionModel.getTransactionItemSummaryByInstitution(
                        institutionName, fromDate, toDate);

        response.getPaymentTransaction().addAll(transactions);

        return response;
    }

    @Override
    public GetTransactionsByCourseIdResponse getTransactionsByCourseId(
            GetTransactionsByCourseId request) {
        GetTransactionsByCourseIdResponse response =
                new GetTransactionsByCourseIdResponse();
        List<PaymentTransaction> paymentTransactions =
                new ArrayList<PaymentTransaction>();

        String courseId = request.getCourseId();
        Date fromDate = ServiceUtil.toDate(request.getFromDate());
        Date toDate = ServiceUtil.toDate(request.getToDate());

        List<PaymentTransaction> transactions =
                transactionModel.getTransactionsByCourseId(
                        courseId, fromDate, toDate);

        String authorizingSubject = wsValidator.getUid();

        for (PaymentTransaction transaction : transactions) {
            boolean isAuthorized = false;
            String cpId = transaction.getCourseProviderId();
            String nterId = transaction.getNterId();

            if (cpId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, cpId, Action.READ);
            }

            if (!isAuthorized && nterId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, nterId, Action.READ);
            }

            if (!isAuthorized) {
                logger.info(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to retrieve a transaction for institution(s) [" +
                    (cpId != null ? cpId + "]" : "") +
                    (cpId != null && nterId != null ? " or [" : "") +
                    (nterId != null ? nterId + "]" : ""));
            } else {
                paymentTransactions.add(transaction);
            }
        }

        response.getPaymentTransaction().addAll(paymentTransactions);

        return response;
    }

    @Override
    public GetTransactionsByTransactionTypeResponse getTransactionsByTransactionType(
            GetTransactionsByTransactionType request) {
        GetTransactionsByTransactionTypeResponse response =
                new GetTransactionsByTransactionTypeResponse();

        String institutionName = request.getInstitution();
        TransactionType transactionType = request.getTransactionType();
        Date fromDate = ServiceUtil.toDate(request.getFromDate());
        Date toDate = ServiceUtil.toDate(request.getToDate());

        if (institutionName == null) {
            throw new RuntimeException("Invalid InstitutionName [" + institutionName + "]");
        }

        if (transactionType == null) {
            throw new RuntimeException(
                "Invalid TransactionType, valid transactionTypes are [" +
                    ServiceUtil.getPrintableTransactionTypes() + "]");
        }

        String authorizingSubject = wsValidator.getUid();

        if (!entitlement.isAuthorized(
                    commerceRealm, authorizingSubject,
                    institutionName, Action.READ)) {

            throw new RuntimeException(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to retrieve a transaction for institution [" +
                    institutionName + "]");
        }

        List<PaymentTransaction> transactions =
                transactionModel.getTransactionsByTransactionType(
                        institutionName, transactionType, fromDate, toDate);

        response.getPaymentTransaction().addAll(transactions);

        return response;
    }

    @Override
    public CreatePaymentTransactionResponse createPaymentTransaction(
            CreatePaymentTransaction request) {

        PaymentTransaction paymentTransaction = request.getPaymentTransaction();

        // User must be authorized for non-null institutions
        String cpId = paymentTransaction.getCourseProviderId();
        String nterId = paymentTransaction.getNterId();
        String authorizingSubject = wsValidator.getUid();
        boolean isAuthorized;

        if (cpId != null) {
            isAuthorized =
                entitlement.isAuthorized(
                    commerceRealm, authorizingSubject, cpId, Action.ADMIN);

            if (!isAuthorized) {
                throw new RuntimeException(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to create a transaction for institution [" +
                    cpId + "]");
            }
        }

        if (nterId != null) {
            isAuthorized =
                entitlement.isAuthorized(
                    commerceRealm, authorizingSubject, nterId, Action.ADMIN);

            if (!isAuthorized) {
                throw new RuntimeException(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to create a transaction for institution [" +
                    nterId + "]");
            }
        }

        // Create the transaction
        transactionModel.createPaymentTransaction(paymentTransaction);

        CreatePaymentTransactionResponse response =
                new CreatePaymentTransactionResponse();
        response.setStatus(RequestStatus.SUCCESS);

        return response;
    }

    @Override
    public GetPaymentStatusResponse getPaymentStatus(GetPaymentStatus request)
        throws ValidationError {

        String institution = request.getInstitution();
        String courseId = request.getCourseId();
        BigDecimal price = request.getPrice();
        String studentId = request.getStudentId();

        return getPaymentStatus(institution, studentId, courseId, price);
    }

    @Override
    public GetTransactionsResponse getTransactions(GetTransactions request) {
        GetTransactionsResponse response = new GetTransactionsResponse();
        List<PaymentTransaction> paymentTransactions =
                new ArrayList<PaymentTransaction>();

        Date fromDate = ServiceUtil.toDate(request.getFromDate());
        Date toDate = ServiceUtil.toDate(request.getToDate());

        List<PaymentTransaction> transactions =
                transactionModel.getTransactions(fromDate, toDate);

        String authorizingSubject = wsValidator.getUid();

        for (PaymentTransaction transaction : transactions) {
            boolean isAuthorized = false;
            String cpId = transaction.getCourseProviderId();
            String nterId = transaction.getNterId();

            if (cpId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, cpId, Action.READ);
            }

            if (!isAuthorized && nterId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, nterId, Action.READ);
            }

            if (!isAuthorized) {
                logger.info(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to retrieve a transaction for institution(s) [" +
                    (cpId != null ? cpId + "]" : "") +
                    (cpId != null && nterId != null ? " or [" : "") +
                    (nterId != null ? nterId + "]" : ""));
            } else {
                paymentTransactions.add(transaction);
            }
        }

        response.getPaymentTransaction().addAll(paymentTransactions);

        return response;
    }

    @Override
    public GetTransactionTypesResponse getTransactionTypes(GetTransactionTypes parameters) {
        GetTransactionTypesResponse response = new GetTransactionTypesResponse();

        List<TransactionType> transactionTypes =
                Arrays.asList(TransactionType.values());

        response.getTransactionType().addAll(transactionTypes);

        return response;
    }

    /** REST SUPPORT **/

    @Override
    public GetTransactionByIdResponse getTransactionById(
            String authorizingSubject, String transactionId) {
        GetTransactionByIdResponse response = new GetTransactionByIdResponse();

        PaymentTransaction paymentTransaction =
            transactionModel.getTransactionById(transactionId);

        if (paymentTransaction != null) {
            boolean isAuthorized = false;
            String cpId = paymentTransaction.getCourseProviderId();
            String nterId = paymentTransaction.getNterId();

            if (cpId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, cpId, Action.READ);
            }

            if (!isAuthorized && nterId != null) {
                isAuthorized =
                    entitlement.isAuthorized(
                        commerceRealm, authorizingSubject, nterId, Action.READ);
            }

            if (!isAuthorized) {
                throw new RuntimeException(
                    "User [" + wsValidator.getUser() +
                    "] does not have authorization to retrieve a transaction for institution(s) [" +
                    (cpId != null ? cpId + "]" : "") +
                    (cpId != null && nterId != null ? " or [" : "") +
                    (nterId != null ? nterId + "]" : ""));
            } else {
                response.setPaymentTransaction(paymentTransaction);
            }
        }

        return response;
    }

    /**
     * Returns the payment status for a student (purchaser) and a course
     * @param institution The name of the content provider as specified in the service registry.
     * @param studentId The unique identifier of the student/user.
     * @param courseId The IRI of the course as specified in the Course Feed.
     * @param price The price of the course as specified in the Course Feed.
     * @return PaymentStatus wrapped in a GetPaymentStatusResponse document.
     * @throws ValidationError is thrown when the information provided by the payment processor does not match our data.
     */
    @Override
    public GetPaymentStatusResponse getPaymentStatus(
            String institution, String studentId, String courseId, BigDecimal price)
        throws ValidationError {

        GetPaymentStatusResponse response = new GetPaymentStatusResponse();
        PaymentStatus paymentStatus =
                transactionModel.getPaymentStatus(
                        institution, studentId, courseId, price);

        response.setStatus(paymentStatus);

        return response;
    }

    /**
     * Receives transaction notifications from Payment Processor
     * @param map MultivaluedMap containing the name/value pairs from HTTP Post
     * @return An Http Response
     */
    @Override
    public Response notification(MultivaluedMap<String, String> map) {

        try {
            PaymentProcessor paymentProcessor = PaymentProcessor.PAY_PAL;

            String validationResult =
                    payPalValidation.verifyNotificationWithPayPal(map);

            // Retrieve query-string parameters
            String transactionId = map.getFirst("txn_id");
			String transactionParentId = map.getFirst("parent_txn_id");

            logger.info("Received Transaction notification [" + transactionId + "]");
            logger.info("Transaction [" + transactionId +
                    "] received validation result [" + validationResult + "] from PayPal");
            if (transactionId == null) {
                logger.error("Invalid transaction - 'null' transactionID");
                Response.status(Response.Status.BAD_REQUEST).build();
            }

            String itemName = map.getFirst("item_name");
            String itemNumber = map.getFirst("item_number");
            String quantity = map.getFirst("quantity");

            String paymentDate = map.getFirst("payment_date");

            String paymentStatus = map.getFirst("payment_status");
            String paymentType = map.getFirst("payment_type");
            String transactionType = map.getFirst("txn_type");
            String mcCurrency = map.getFirst("mc_currency");
            String transactionSubject =
                    map.getFirst("transaction_subject");
            String reasonCode = map.getFirst("reason_code");

            String mcGross = map.getFirst("mc_gross");
            String paymentGross = map.getFirst("payment_gross");
            String tax = map.getFirst("tax");
            String mcFee = map.getFirst("mc_fee");
            String paymentFee = map.getFirst("payment_fee");
            String handlingAmount = map.getFirst("handling_amount");
            String shipping = map.getFirst("shipping");

            String receiverEmail = map.getFirst("receiver_email");
            String payerEmail = map.getFirst("payer_email");

            String userDefinedValues = map.getFirst("custom");

            String payerId = map.getFirst("payer_id");
            String payerStatus = map.getFirst("payer_status");
            String receiverId = map.getFirst("receiver_id");

            String protectionEligibility =
                    map.getFirst("protection_eligibility");
            String notifyVersion = map.getFirst("notify_version");
            String verifySign = map.getFirst("verify_sign");
            String testIpn = map.getFirst("test_ipn");

            // Create new PaymentTransaction
            PaymentTransaction paymentTransaction = new PaymentTransaction();

            paymentTransaction.setPaymentProcessor(paymentProcessor);
            paymentTransaction.setTransactionId(transactionId);
			paymentTransaction.setTransactionParentId(transactionParentId);
            paymentTransaction.setItemName(itemName);
            paymentTransaction.setItemNumber(itemNumber);
            // Quantity
            if (quantity != null && !quantity.isEmpty()) {
                try {
                    paymentTransaction.setQuantity(new Integer(quantity));
                } catch (NumberFormatException e) {
                    logger.error(
                        "Error setting quantity [" + quantity +
                        "] for transaction [" + transactionId +
                        "] defaulting to '1'");
                    paymentTransaction.setQuantity(1);
                }
            }
            // PaymentDate
            try {
                paymentTransaction.setPaymentDate(
                        ServiceUtil.toXMLGregorianCalendar(paymentDate));
            } catch (Exception e) {
                logger.error("Error setting PaymentDate [" + paymentDate +
                       "] for transaction [" + transactionId +
                        "] defaulting to today");
                paymentTransaction.setPaymentDate(
                        ServiceUtil.toXMLGregorianCalendar(new Date()));
            }
            // Payer Status
            paymentTransaction.setPayerStatus(payerStatus);
            // Payment Status
            PaymentStatus payStatus = PaymentStatus.UNKNOWN;
            try {
                payStatus = PaymentStatus.fromValue(paymentStatus);
            } catch (Exception e) {
                logger.error("Error setting PaymentStatus [" + paymentStatus +
                        "] for transaction [" + transactionId +
                        "] defaulting to [" + PaymentStatus.UNKNOWN + "]");
            }
            paymentTransaction.setPaymentStatus(payStatus);
            // Payment Type
            PaymentType paymentTypeEnum = PaymentType.OTHER;
            try {
                paymentTypeEnum = PaymentType.fromValue(paymentType);
            } catch (Exception e) {
                logger.error("Error setting PaymentType [" + paymentType +
                        "] for transaction [" + transactionId +
                        "] defaulting to [" + PaymentType.OTHER + "]");
            }
            paymentTransaction.setPaymentType(paymentTypeEnum);
            // TransactionType
            TransactionType transactionTypeEnum;
            if (transactionType != null && !transactionType.isEmpty()) {
                try {
                    transactionTypeEnum = TransactionType.fromValue(transactionType);
                } catch (Exception e) {
                    transactionTypeEnum = TransactionType.NEW_CASE;
                    logger.error(
                            "Error setting TransactionType [" + transactionType +
                            "] for transaction [" + transactionId +
                            "] defaulting to [" + transactionTypeEnum + "]");
                }
            } else {
                if (payStatus != null && payStatus == PaymentStatus.REFUNDED) {
                    transactionTypeEnum = TransactionType.ADJUSTMENT;
                } else {
                    transactionTypeEnum = TransactionType.NEW_CASE;
                }
                logger.info(
                            "TransactionType [" + transactionType +
                            "] for transaction [" + transactionId +
                            "] defaulting to [" + transactionTypeEnum + "]");
            }
            paymentTransaction.setTransactionType(transactionTypeEnum);
            // Reason Code
            if (reasonCode != null && !reasonCode.isEmpty()) {
                ReasonCode reasonCodeEnum = ReasonCode.OTHER;
                try {
                    reasonCodeEnum = ReasonCode.fromValue(reasonCode);
                } catch (Exception e) {
                    logger.error(
                            "Error setting ReasonCode [" + reasonCode +
                            "] for transaction [" + transactionId +
                            "] defaulting to [" + ReasonCode.OTHER + "]", e);
                }
                paymentTransaction.setReasonCode(reasonCodeEnum);
            }

            // Currency Type
            paymentTransaction.setCurrencyType(mcCurrency);
            paymentTransaction.setTransactionSubject(transactionSubject);
            // Payment Gross
            if (mcGross != null && !mcGross.isEmpty()) {
                try {
                    paymentTransaction.setPaymentGross(new BigDecimal(mcGross));
                } catch (Exception e) {
                    logger.error("Error setting PaymentGross [" + mcGross +
                            "] for transaction [" + transactionId +
                            "] defaulting to '0'");
                    paymentTransaction.setPaymentGross(new BigDecimal(0));
                }
            }
            if (paymentGross != null && !paymentGross.isEmpty()) {
                try {
                    paymentTransaction
                            .setPaymentGross(new BigDecimal(paymentGross));
                } catch (Exception e) {
                    logger.error("Error setting PaymentGross [" + paymentGross +
                            "] for transaction [" + transactionId +
                            "] defaulting to '0'");
                    paymentTransaction.setPaymentGross(new BigDecimal(0));
                }
            }
            // Tax
            if (tax != null && !tax.isEmpty()) {
                try {
                    paymentTransaction.setTax(new BigDecimal(tax));
                } catch (Exception e) {
                    logger.error("Error setting Tax [" + tax +
                            "] for transaction [" + transactionId +
                            "] defaulting to '0'");
                    paymentTransaction.setTax(new BigDecimal(0));
                }
            }
            // Payment Fee
            if (mcFee != null && !mcFee.isEmpty()) {
                try {
                    paymentTransaction.setPaymentFee(new BigDecimal(mcFee));
                } catch (Exception e) {
                    logger.error("Error setting Payment Fee [" + mcFee +
                            "] for transaction [" + transactionId +
                            "] defaulting to '0'");
                    paymentTransaction.setPaymentFee(new BigDecimal(0));
                }
            }
            if (paymentFee != null && !paymentFee.isEmpty()) {
                try {
                    paymentTransaction.setPaymentFee(new BigDecimal(paymentFee));
                } catch (Exception e) {
                    logger.error(
                            "Error setting Payment Fee [" + paymentFee +
                            "] for transaction [" + transactionId +
                            "] defaulting to '0'");
                    paymentTransaction.setPaymentFee(new BigDecimal(0));
                }
            }
            // Handling amount
            if (handlingAmount != null && !handlingAmount.isEmpty()) {
                try {
                    paymentTransaction.setHandlingAmount(new BigDecimal(
                            handlingAmount));
                } catch (Exception e) {
                    logger.error(
                            "Error setting HandlingAmount [" + handlingAmount +
                            "] for transaction [" + transactionId +
                            "] defaulting to '0'");
                    paymentTransaction.setHandlingAmount(new BigDecimal(0));
                }
            }
            // Shipping
            if (shipping != null && !shipping.isEmpty()) {
                try {
                    paymentTransaction.setShipping(new BigDecimal(shipping));
                } catch (Exception e) {
                    logger.error("Error setting Shipping [" + shipping +
                            "] for transaction [" + transactionId +
                            "] defaulting to '0'");
                    paymentTransaction.setShipping(new BigDecimal(0));
                }
            }

            paymentTransaction.setReceiverEmail(receiverEmail);
            paymentTransaction.setPayerEmail(payerEmail);

            // User defined values
            Map<String, String> userDefinedValueMap =
                    ServiceUtil.getCustomValues(userDefinedValues);
            // StudentId
            String studentId = userDefinedValueMap.get("studentId");
            if (studentId != null) {
                paymentTransaction.setStudentId(studentId);
            } else {
                if (paymentTransaction.getTransactionType() != TransactionType.DISBURSEMENT) {
                    logger.error("Transaction [" + transactionId +
                        "] has an invalid StudentId [" + studentId + "]");
                }
            }
            // NterId
            String nterId = userDefinedValueMap.get("nterId");
            if (nterId != null) {
                paymentTransaction.setNterId(nterId);
            } else {
                logger.error("Invalid NTER_ID [" + nterId +
                        "] see transaction [" + transactionId + "]");
            }
            // ContentProviderId
            String cpId = userDefinedValueMap.get("cpId");
            if (cpId != null) {
                paymentTransaction.setCourseProviderId(cpId);
            } else {
                logger.error("Invalid ContentProviderId [" + cpId +
                        "] see transaction [" + transactionId + "]");
            }
            // PaymentProcessor
            String paymentProcessorId = userDefinedValueMap.get("payment_processor");
            if (paymentProcessorId != null && !paymentProcessorId.isEmpty()) {
                try {
                    paymentProcessor = PaymentProcessor.fromValue(paymentProcessorId);
                } catch (Exception e) {
                    paymentProcessor = PaymentProcessor.OTHER;
                    logger.error(
                            "Error setting PaymentProcessor [" + paymentProcessorId +
                            "] for transaction [" + transactionId +
                            "] defaulting to [" + paymentProcessor + "]", e);

                }
                paymentTransaction.setPaymentProcessor(paymentProcessor);
            }

            paymentTransaction.setPayerId(payerId);
            paymentTransaction.setPayerStatus(payerStatus);
            paymentTransaction.setReceiverId(receiverId);

            paymentTransaction.setProtectionEligibility(protectionEligibility);
            if (notifyVersion != null && !notifyVersion.isEmpty()) {
                try {
                    paymentTransaction.setNotifyVersion(new BigDecimal(
                            notifyVersion));
                } catch (Exception e) {
                    logger.error("Error setting notify version [" +
                            notifyVersion + "]");
                }
            }
            paymentTransaction.setVerifySign(verifySign);
            if (testIpn != null && !testIpn.isEmpty()) {
                paymentTransaction.setTestIpn(testIpn);
            }

            ValidationStatus validationStatus = ValidationStatus.INVALID;
            try {
                validationStatus = ValidationStatus.fromValue(validationResult);
            } catch (Exception e) {
                logger.error(
                            "Error setting ValidationStatus [" + validationResult +
                            "] for transaction [" + transactionId +
                            "] defaulting to [" + ValidationStatus.INVALID + "]", e);
            }
            paymentTransaction.setValidationStatus(validationStatus);

            // Persist new PaymentTransaction
            transactionModel.createPaymentTransaction(paymentTransaction);

            // check transaction validation
            if (validationStatus != ValidationStatus.VERIFIED) {
                logger.error("Received a validation result of " + validationStatus +
                "] from Payment Processor with transaction Id [" + transactionId + "]");
            }

        } catch (Exception e) {
            logger.error("Error processing data received from Payment Processor",
                    e);
        }

        return Response.status(Response.Status.OK).build();
    }

    // Spring dependency injection
    public void setTransactionModel(TransactionModel transactionModel) {
        this.transactionModel = transactionModel;
    }

    public void setCommerceRealm(String commerceRealm) {
        this.commerceRealm = commerceRealm;
    }

    public void setWsValidator(WSValidator wsValidator) {
        this.wsValidator = wsValidator;
    }

    public void setEntitlement(Entitlement entitlement) {
        this.entitlement = entitlement;
    }

    public void setPayPalValidation(PayPalValidation payPalValidation) {
        this.payPalValidation = payPalValidation;
    }


}
