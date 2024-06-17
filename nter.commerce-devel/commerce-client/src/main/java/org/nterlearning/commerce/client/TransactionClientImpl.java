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

package org.nterlearning.commerce.client;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.commerce.transaction.client.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/11/12
 */
public class TransactionClientImpl implements TransactionClient {

    private static TransactionInterface transactionInterface;
    private String wsdlLocation = null;
    private CommerceClientCallback commerceClientCallback;
    private String user = null;
    private Logger logger = LoggerFactory.getLogger(TransactionClientImpl.class);

    public TransactionClientImpl(String user,
                              String password,
                              String wsdlLocation) {
        commerceClientCallback = new CommerceClientCallback();
        this.user = user;
        commerceClientCallback.setUsername(user);
        commerceClientCallback.setPassword(password);

        this.wsdlLocation = wsdlLocation;
    }

    @Override
    public void createPaymentTransaction(
            @NotNull PaymentTransaction paymentTransaction) {
        if (getTransactionInterface() != null) {

            CreatePaymentTransaction request = new CreatePaymentTransaction();
            request.setPaymentTransaction(paymentTransaction);

            CreatePaymentTransactionResponse response =
                    transactionInterface.createPaymentTransaction(request);
            if (response.getStatus() != RequestStatus.SUCCESS) {
                logger.error("Unexpected response from createPaymentTransaction [" +
                    response.getStatus()+ ']');
            }
        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactions(
            @Nullable Date fromDate, @Nullable Date toDate) {
        List<PaymentTransaction> transactions = new ArrayList<PaymentTransaction>();
        if (getTransactionInterface() != null) {

            GetTransactions request = new GetTransactions();

            if (fromDate != null) {
                request.setFromDate(fromDate);
            }

            if (toDate != null) {
                request.setToDate(toDate);
            }

            GetTransactionsResponse response =
                    transactionInterface.getTransactions(request);

            transactions = response.getPaymentTransaction();

        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }

        return transactions;
    }

    @Override
    @Nullable
    public PaymentTransaction getTransactionById(@NotNull String transactionId) {
        PaymentTransaction transaction = null;
        if (getTransactionInterface() != null) {

            GetTransactionById request = new GetTransactionById();
            request.setTransactionId(transactionId);

            GetTransactionByIdResponse response =
                    transactionInterface.getTransactionById(request);

            transaction = response.getPaymentTransaction();

        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }

        return transaction;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getChildTransactionsById(
            @NotNull String transactionId) {
        List<PaymentTransaction> transactions = new ArrayList<PaymentTransaction>();
        if (getTransactionInterface() != null) {

            GetChildTransactionsById request = new GetChildTransactionsById();
            request.setTransactionId(transactionId);

            GetChildTransactionsByIdResponse response =
                    transactionInterface.getChildTransactionsById(request);

            transactions = response.getPaymentTransaction();

        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }

        return transactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionsByStudentId(
            @NotNull String studentId, @Nullable Date fromDate, @Nullable Date toDate) {
        List<PaymentTransaction> transactions = new ArrayList<PaymentTransaction>();
        if (getTransactionInterface() != null) {

            GetTransactionsByStudentId request = new GetTransactionsByStudentId();
            request.setStudentId(studentId);

            if (fromDate != null) {
                request.setFromDate(fromDate);
            }

            if (toDate != null) {
                request.setToDate(toDate);
            }

            GetTransactionsByStudentIdResponse response =
                    transactionInterface.getTransactionsByStudentId(request);

            transactions = response.getPaymentTransaction();

        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }

        return transactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionsByCourseId(
            @NotNull String courseId, @Nullable Date fromDate, @Nullable Date toDate) {
        List<PaymentTransaction> transactions = new ArrayList<PaymentTransaction>();
        if (getTransactionInterface() != null) {

            GetTransactionsByCourseId request = new GetTransactionsByCourseId();
            request.setCourseId(courseId);

            if (fromDate != null) {
                request.setFromDate(fromDate);
            }

            if (toDate != null) {
                request.setToDate(toDate);
            }

            GetTransactionsByCourseIdResponse response =
                    transactionInterface.getTransactionsByCourseId(request);

            transactions = response.getPaymentTransaction();

        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }

        return transactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionsByTransactionType(
            @NotNull String institution, @NotNull TransactionType transactionType,
            @Nullable Date fromDate, @Nullable Date toDate) {
        List<PaymentTransaction> transactions = new ArrayList<PaymentTransaction>();
        if (getTransactionInterface() != null) {

            GetTransactionsByTransactionType request =
                    new GetTransactionsByTransactionType();
            request.setInstitution(institution);
            request.setTransactionType(transactionType);

            if (fromDate != null) {
                request.setFromDate(fromDate);
            }

            if (toDate != null) {
                request.setToDate(toDate);
            }

            GetTransactionsByTransactionTypeResponse response =
                    transactionInterface.getTransactionsByTransactionType(request);

            transactions = response.getPaymentTransaction();

        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }

        return transactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionsByInstitution(
            @NotNull String institution,
            @Nullable Date fromDate, @Nullable Date toDate) {
        List<PaymentTransaction> transactions = new ArrayList<PaymentTransaction>();
        if (getTransactionInterface() != null) {

            GetTransactionsByInstitution request =
                    new GetTransactionsByInstitution();
            request.setInstitution(institution);

            if (fromDate != null) {
                request.setFromDate(fromDate);
            }

            if (toDate != null) {
                request.setToDate(toDate);
            }

            GetTransactionsByInstitutionResponse response =
                    transactionInterface.getTransactionsByInstitution(request);

            transactions = response.getPaymentTransaction();

        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }

        return transactions;
    }

    @Override
    @NotNull
    public List<PaymentTransaction> getTransactionSummaryByInstitution(
            @NotNull String institution,
            @Nullable Date fromDate, @Nullable Date toDate) {
        List<PaymentTransaction> transactions = new ArrayList<PaymentTransaction>();
        if (getTransactionInterface() != null) {

            GetTransactionSummaryByInstitution request =
                    new GetTransactionSummaryByInstitution();
            request.setInstitution(institution);

            if (fromDate != null) {
                request.setFromDate(fromDate);
            }

            if (toDate != null) {
                request.setToDate(toDate);
            }

            GetTransactionSummaryByInstitutionResponse response =
                    transactionInterface.getTransactionSummaryByInstitution(request);

            transactions = response.getPaymentTransaction();

        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }

        return transactions;
    }

    @Override
    @NotNull
    public PaymentStatus getPaymentStatus(
            @NotNull String institution, @NotNull String studentId,
            @NotNull String courseId, @NotNull BigDecimal price) {
        PaymentStatus paymentStatus = PaymentStatus.UNKNOWN;
        if (getTransactionInterface() != null) {

            GetPaymentStatus request = new GetPaymentStatus();
            request.setInstitution(institution);
            request.setStudentId(studentId);
            request.setCourseId(courseId);
            request.setPrice(price);

            GetPaymentStatusResponse response;
            try {
                response = transactionInterface.getPaymentStatus(request);
                paymentStatus = response.getStatus();
            } catch (ValidationError_Exception e) {
                logger.warn("Received validation error when requesting PaymentStatus", e);
            }

        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                transactionInterface + "]");
        }

        return paymentStatus;
    }

    @Override
    @Nullable
    public String getUser() {
        return this.user;
    }

    private TransactionInterface getTransactionInterface() {
        if (transactionInterface == null) {
            if (wsdlLocation != null && !wsdlLocation.isEmpty()) {
                logger.info("Attempting to contact the Entitlement service using [" +
                        wsdlLocation + "]");
                try {
                    URL uRL = new URL(wsdlLocation);
                    TransactionAPI transactionAPI = new TransactionAPI(uRL);
                    transactionInterface =
                            transactionAPI.getTransactionAPISoap12EndPoint();

                    // WS-Security header
                    Map<String, Object> ctx = new HashMap<String, Object>();

                    ctx.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
                    ctx.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
                    ctx.put(WSHandlerConstants.USER,
                            commerceClientCallback.getUsername());
                    ctx.put(WSHandlerConstants.PW_CALLBACK_REF,
                             commerceClientCallback);

                    Client client = ClientProxy.getClient(transactionInterface);
                    HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

                    HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
                    httpClientPolicy.setConnectionTimeout(10000);
                    httpClientPolicy.setReceiveTimeout(10000);
                    httpClientPolicy.setAutoRedirect(true);
                    httpClientPolicy.setMaxRetransmits(0);

                    httpConduit.setClient(httpClientPolicy);

                    Endpoint cxfEndpoint = client.getEndpoint();
                    cxfEndpoint.getOutInterceptors().add(new WSS4JOutInterceptor(ctx));

                    if (logger.isDebugEnabled()) {
                        cxfEndpoint.getInInterceptors().add(new LoggingInInterceptor());
                        cxfEndpoint.getOutInterceptors().add(new LoggingOutInterceptor());
                    }

                } catch (Exception e) {
                    logger.error(
                        "Unexpected exception connecting to Commerce transactionAPI using wsdl [" +
                            wsdlLocation + "]", e);
                }
            } else {
                logger.warn("Invalid Commerce configuration wsdl url [" +
                        wsdlLocation + "], Commerce transactionAPI will not be accessible");
            }
        }
        return transactionInterface;
    }

}
