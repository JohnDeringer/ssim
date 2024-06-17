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

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.nterlearning.commerce.managed.RealmBean;
import org.nterlearning.entitlement.client.EntitlementService;
import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;
import org.nterlearning.xml.commerce.configuration_api_0_1_0_wsdl.ConfigurationAPI;
import org.nterlearning.xml.commerce.configuration_interface_0_1_0.*;
import org.nterlearning.xml.commerce.configuration_interface_0_1_0_wsdl.ConfigurationInterface;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.RequestStatus;
import org.nterlearning.xml.commerce.transaction_api_0_1_0_wsdl.TransactionAPI;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0.GetPaymentStatus;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0.GetPaymentStatusResponse;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0.GetTransactionById;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0.GetTransactionByIdResponse;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0_wsdl.TransactionInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/22/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:commerce-beans-test.xml")
public class CommerceServiceTest {

    private TransactionInterface transactionInterface = null;
    private ConfigurationInterface configurationInterface = null;
    @Autowired
    private EntitlementService entitlementService = null;
    @Autowired
    private IdentityService identityService = null;
    @Autowired
    private RealmBean realmBean;

    //private static final String ADMIN_USER_FROM_SSO = "amadmin";
    private static final String ACTION_URL =
        "https://www.sandbox.paypal.com/cgi-bin/webscr";
    private static final String BUTTON_URL =
        "https://www.sandbox.paypal.com/en_US/i/btn/btn_buynowCC_LG.gif";
/*
    private static final String TRANSACTION_WSDL_URL =
        "https://commerce.nterlearning.org/commerce-service-0.0.1/services/TransactionAPI?wsdl";
    private static final String CONFIGURATION_WSDL_URL =
        "https://commerce.nterlearning.org/commerce-service-0.0.1/services/ConfigurationAPI?wsdl";
    private static final String NOTIFICATION_URL =
        "http://commerce.nterlearning.org/commerce-service-0.0.1/notification";
*/
    private static final String TRANSACTION_WSDL_URL =
        "http://localhost:8080/commerce-service-1.1.0/services/TransactionAPI?wsdl";
    private static final String CONFIGURATION_WSDL_URL =
        "http://localhost:8080/commerce-service-1.1.0/services/ConfigurationAPI?wsdl";
    private static final String NOTIFICATION_URL =
        "http://localhost:8080/commerce-service-1.1.0/notification";

    @Before
    public void init() {
        URL url;
        try {
            url = new URL(TRANSACTION_WSDL_URL);
            TransactionAPI transactionAPI = new TransactionAPI(url);
            transactionInterface = transactionAPI.getTransactionAPISoap12EndPoint();
        } catch (MalformedURLException e) {}

        Assert.isTrue(transactionInterface != null,
                "Transaction interface is null");

        try {
            url = new URL(CONFIGURATION_WSDL_URL);
            ConfigurationAPI configurationAPI = new ConfigurationAPI(url);
            configurationInterface = configurationAPI.getConfigurationAPISoap12EndPoint();
        } catch (MalformedURLException e) {}

        Assert.isTrue(configurationInterface != null,
                "Configuration interface is null");

    }

    @Test
    public void refund() throws Exception {
        Random r = new Random();
        //String sellerEmail = "admin@nterlearning.org";
        String sellerEmail = "jdslo1@mailinator.com";
        String sellerId = "QS6F6B7D3R2Q8";
        String studentId = "testUser@sri.com";
        String nterId = "NTER-" + r.nextInt();
        String cpId = "NWTP-" + r.nextInt();
        String courseId = "T-" + r.nextInt();
        BigDecimal price = new BigDecimal("13.00");
        long transId = r.nextLong();
        long refundTransId = r.nextLong();

        PaymentConfig paymentConfig;

        // Create PayPal Configuration
        GetPaymentConfig paymentConfigRequest = new GetPaymentConfig();
        paymentConfigRequest.setConfigId(PaymentProcessor.PAY_PAL);
        // Check to see if PayPal config exists
        GetPaymentConfigResponse getConfigResponse =
                configurationInterface.getPaymentConfig(paymentConfigRequest);

        // If it doesn't exists create an entry
        if (getConfigResponse == null ||
                getConfigResponse.getConfigurationEntry() == null) {
            // Request wrapper
            CreatePaymentConfig createConfigRequest = new CreatePaymentConfig();
            // Admin User
            //createConfigRequest.setSubjectId(ADMIN_USER_FROM_SSO);
            // Create and populate the payload
            paymentConfig = new PaymentConfig();
            paymentConfig.setConfigId(PaymentProcessor.PAY_PAL);
            paymentConfig.setSellerId(sellerId);
            paymentConfig.setSellerPassword(sellerId + "");
            paymentConfig.setActionURL(ACTION_URL);
            paymentConfig.setActiveStatus(true);
            paymentConfig.setNotifyURL(NOTIFICATION_URL);
            paymentConfig.setButtonURL(BUTTON_URL);

            // Add payload to request wrapper
            createConfigRequest.setConfigurationEntry(paymentConfig);

            // Send the request
            CreatePaymentConfigResponse createConfigResponse =
                    configurationInterface.createPaymentConfig(createConfigRequest);

            Assert.isTrue(createConfigResponse.getStatus() ==
                    RequestStatus.SUCCESS, "Failure creating Payment Configuration");

            // Ensure that the Payment Config really exists
            getConfigResponse =
                configurationInterface.getPaymentConfig(paymentConfigRequest);
            Assert.notNull(paymentConfig, "getConfigResponse is null");
            Assert.notNull(getConfigResponse.getConfigurationEntry(),
                    "PaymentConfigResponse is null");
        } else {
            // Update Payment Config to ensure that it has the correct values
            paymentConfig = getConfigResponse.getConfigurationEntry();
            paymentConfig.setSellerId(sellerId);
            paymentConfig.setSellerPassword(sellerId + "");
            paymentConfig.setActionURL(ACTION_URL);
            paymentConfig.setActiveStatus(true);
            paymentConfig.setNotifyURL(NOTIFICATION_URL);
            paymentConfig.setButtonURL(BUTTON_URL);

            UpdatePaymentConfig updateConfig = new UpdatePaymentConfig();
            //updateConfig.setSubjectId(ADMIN_USER_FROM_SSO);
            updateConfig.setConfigurationEntry(paymentConfig);
            configurationInterface.updatePaymentConfig(updateConfig);
        }

        paymentConfig = getConfigResponse.getConfigurationEntry();
        Assert.notNull(paymentConfig, "PaymentConfig is null");
        Assert.isTrue(paymentConfig.getConfigId() == PaymentProcessor.PAY_PAL,
                "Unexpected PaymentProcessor [" +
                        paymentConfig.getConfigId() + "]");

        // Create a payment transaction and POST it to the notification Servlet
        createPaymentTransaction(paymentConfig, price.toString(), studentId, nterId,
                cpId, (transId + ""), sellerId, sellerEmail, courseId,
                PaymentStatus.COMPLETED, null);

        // Retrieve the transaction
        GetTransactionById transactionsByIdRequest = new GetTransactionById();
        // Request payload
        transactionsByIdRequest.setTransactionId(transId + "");

        // Send the transaction request
        System.out.println("Requesting transaction [" + transId + "]");
        GetTransactionByIdResponse transactionResponse =
                transactionInterface.getTransactionById(transactionsByIdRequest);

        Assert.notNull(transactionResponse, "Transaction was null");
        Assert.notNull(transactionResponse.getPaymentTransaction(),
                "Transaction collection was empty");
        Assert.isTrue(
                transactionResponse.getPaymentTransaction().getPaymentStatus() == PaymentStatus.COMPLETED,
                "Transaction collection was empty");

        // Create an entitlement to the content provider
        UserImpl user = identityService.getUserByEmail(sellerEmail);
        String uid = user.getUid();
        entitlementService.createPolicy(
                realmBean.getCommerceRealm(),
                uid,
                cpId,
                org.nterlearning.entitlement.client.Action.ADMIN);

        // Create a refund transaction
        createPaymentTransaction(paymentConfig, price.multiply(new BigDecimal("-1")).toString(), studentId, nterId,
                cpId, (refundTransId + ""), sellerId, sellerEmail, courseId,
                PaymentStatus.REFUNDED, transId + "");

        // Retrieve the transaction
        transactionsByIdRequest = new GetTransactionById();
        // Request payload
        transactionsByIdRequest.setTransactionId(refundTransId + "");

        // Send the transaction request
        System.out.println("Requesting refund transaction [" + refundTransId + "]");
        transactionResponse =
                transactionInterface.getTransactionById(transactionsByIdRequest);

        Assert.notNull(transactionResponse, "Transaction was null");
        Assert.notNull(transactionResponse.getPaymentTransaction(),
                "Transaction collection was empty");
        Assert.isTrue(
                transactionResponse.getPaymentTransaction().getPaymentStatus() == PaymentStatus.REFUNDED,
                "Transaction collection was empty");
    }

    @Test
    public void getPaymentStatus() throws Exception {
        Random r = new Random();
        String sellerEmail = "admin@nterlearning.org";
        String sellerId = "QS6F6B7D3R2Q8";
        String studentId = "testUser@sri.com";
        String nterId = "NTER-" + r.nextInt();
        String cpId = "NWTP-" + r.nextInt();
        String courseId = "T-" + r.nextInt();
        String price = "13.00";
        long transId = r.nextLong();

        PaymentConfig paymentConfig;

        // Create PayPal Configuration
        GetPaymentConfig paymentConfigRequest = new GetPaymentConfig();
        paymentConfigRequest.setConfigId(PaymentProcessor.PAY_PAL);
        // Check to see if PayPal config exists
        GetPaymentConfigResponse getConfigResponse =
                configurationInterface.getPaymentConfig(paymentConfigRequest);

        // If it doesn't exists create an entry
        if (getConfigResponse == null ||
                getConfigResponse.getConfigurationEntry() == null) {
            // Request wrapper
            CreatePaymentConfig createConfigRequest = new CreatePaymentConfig();
            // Admin User
            //createConfigRequest.setSubjectId(ADMIN_USER_FROM_SSO);
            // Create and populate the payload
            paymentConfig = new PaymentConfig();
            paymentConfig.setConfigId(PaymentProcessor.PAY_PAL);
            paymentConfig.setSellerId(sellerId);
            paymentConfig.setSellerPassword(sellerId + "");
            paymentConfig.setActionURL(ACTION_URL);
            paymentConfig.setActiveStatus(true);
            paymentConfig.setNotifyURL(NOTIFICATION_URL);
            paymentConfig.setButtonURL(BUTTON_URL);

            // Add payload to request wrapper
            createConfigRequest.setConfigurationEntry(paymentConfig);

            // Send the request
            CreatePaymentConfigResponse createConfigResponse =
                    configurationInterface.createPaymentConfig(createConfigRequest);

            Assert.isTrue(createConfigResponse.getStatus() ==
                    RequestStatus.SUCCESS, "Failure creating Payment Configuration");

            // Ensure that the Payment Config really exists
            getConfigResponse =
                configurationInterface.getPaymentConfig(paymentConfigRequest);
            Assert.notNull(paymentConfig, "PaymentConfig is null");
            Assert.notNull(getConfigResponse.getConfigurationEntry(),
                    "PaymentConfigResponse is null");
        } else {
            // Update Payment Config to ensure that it has the correct values
            paymentConfig = getConfigResponse.getConfigurationEntry();
            paymentConfig.setSellerId(sellerId);
            paymentConfig.setSellerPassword(sellerId + "");
            paymentConfig.setActionURL(ACTION_URL);
            paymentConfig.setActiveStatus(true);
            paymentConfig.setNotifyURL(NOTIFICATION_URL);
            paymentConfig.setButtonURL(BUTTON_URL);

            UpdatePaymentConfig updateConfig = new UpdatePaymentConfig();
            //updateConfig.setSubjectId(ADMIN_USER_FROM_SSO);
            updateConfig.setConfigurationEntry(paymentConfig);
            configurationInterface.updatePaymentConfig(updateConfig);
        }

        paymentConfig = getConfigResponse.getConfigurationEntry();
        Assert.notNull(paymentConfig, "PaymentConfig is null");
        Assert.isTrue(paymentConfig.getConfigId() == PaymentProcessor.PAY_PAL,
                "Unexpected PaymentProcessor [" +
                        paymentConfig.getConfigId() + "]");

        // Create a payment transaction and POST it to the notification Servlet
        createPaymentTransaction(paymentConfig, price, studentId, nterId,
                cpId, (transId + ""), sellerId, sellerEmail, courseId,
                PaymentStatus.COMPLETED, null);

        // Retrieve the transaction
        GetTransactionById transactionsByIdRequest = new GetTransactionById();
        // Set the 'Admin' user
        //transactionsByIdRequest.setSubjectId(ADMIN_USER_FROM_SSO);
        // Request payload
        transactionsByIdRequest.setTransactionId(transId + "");

        // Send the transaction request
        System.out.println("Requesting transaction [" + transId + "]");
        GetTransactionByIdResponse transactionResponse =
                transactionInterface.getTransactionById(transactionsByIdRequest);

        Assert.notNull(transactionResponse, "Transaction was null");
        Assert.notNull(transactionResponse.getPaymentTransaction(),
                "Transaction collection was empty");

        // Get the Payment Status
        GetPaymentStatus paymentStatusRequest = new GetPaymentStatus();
        paymentStatusRequest.setCourseId(courseId);
        paymentStatusRequest.setPrice(new BigDecimal(price));
        paymentStatusRequest.setInstitution(cpId);
        paymentStatusRequest.setStudentId(studentId);

        GetPaymentStatusResponse response =
                transactionInterface.getPaymentStatus(paymentStatusRequest);

        Assert.isTrue(response.getStatus() == PaymentStatus.COMPLETED);
    }

    // Send a payment transaction to the Notification Servlet
    private void createPaymentTransaction(
            PaymentConfig paymentConfig, String price, String studentId,
            String nterId, String cpId, String transId, String sellerId,
            String sellerEmail, String courseId, PaymentStatus paymentStatus,
            @Nullable String parentTransactionId) {

        System.out.println("Sending Notification to [" +
                        paymentConfig.getNotifyURL() + "]");

        HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "Test Client");
        PostMethod post = new PostMethod(paymentConfig.getNotifyURL());

        post.addParameter("mc_gross", price);
        post.addParameter("protection_eligibility","Eligible");
        post.addParameter("address_status", "confirmed");
        post.addParameter("payer_id", "J3HBEKX9ADJHL");
        post.addParameter("tax", "0.00");
        post.addParameter("address_street", "1 Main St");

        // Payment Date
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MMM dd, yyyy z");
        Date today = new Date();
        String paymentDate = dateFormat.format(today);
        post.addParameter("payment_date", paymentDate);

        post.addParameter("payment_status", paymentStatus.value());
        post.addParameter("address_zip", "95131");
        post.addParameter("first_name", "Test");
        post.addParameter("mc_fee", "0.88");
        post.addParameter("address_country_code", "US");
        post.addParameter("address_name", "TestUser");
        post.addParameter("notify_version", "2.6");

        // Custom field
        post.addParameter("custom",
                "studentId=" + studentId + "|" + "nterId=" + nterId + "|" +
                        "cpId=" + cpId);

        post.addParameter("payer_status", "verified");
        post.addParameter("address_country", "United States");
        post.addParameter("address_city", "San Jose");
        post.addParameter("quantity", "1");
        post.addParameter("verify_sign", "AtkOfCXbDm2hu0ZELryHFjY-Vb7PAUvS6nMXgysbElEn9v-1XcmSoGtf");
        post.addParameter("payer_email", studentId);
        post.addParameter("txn_id", transId + "");
        post.addParameter("payment_type", "instant");
        post.addParameter("last_name", "User");
        post.addParameter("address_state", "CA");
        post.addParameter("receiver_email", sellerEmail);
        post.addParameter("payment_fee", "0.88");
        post.addParameter("receiver_id", sellerId);
        post.addParameter("txn_type", "express_checkout");
        post.addParameter("item_name", "A Guide To " + courseId);
        post.addParameter("mc_currency", "USD");
        post.addParameter("item_number", courseId);
        post.addParameter("residence_country", "US");
        post.addParameter("test_ipn", "1");
        post.addParameter("handling_amount", "0.00");
        post.addParameter("payment_gross", price);
        post.addParameter("shipping", "0.00");
        if (parentTransactionId != null) {
            post.addParameter("parent_txn_id", parentTransactionId);
        }

        //BufferedReader br = null;
        int returnCode = 0;
        try{
            returnCode = client.executeMethod(post);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }
        Assert.isTrue(returnCode == 200, "Response from POST: " + returnCode);
    }

}
