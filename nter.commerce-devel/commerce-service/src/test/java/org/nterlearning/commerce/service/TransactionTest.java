package org.nterlearning.commerce.service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import org.jetbrains.annotations.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.nterlearning.commerce.model.ConfigurationModelImpl;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/23/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:commerce-beans-test.xml")
public class TransactionTest {

    @Autowired
    ConfigurationModelImpl configurationModel;

    @Test
    public void createTransaction() {
        PaymentConfig paymentConfig =
                configurationModel.getPaymentConfig(PaymentProcessor.PAY_PAL);
        Assert.notNull(paymentConfig, "PaymentConfig is Null");

        String price = "22.00";
        BigDecimal amount = new BigDecimal(price);
        String studentId = "jdslo@sri.com";
        String nterId = "NTER-1";
        String cpId = "NWTP-1";
        String transId = getNewTransId();
        String sellerId = paymentConfig.getSellerId();
        String sellerEmail = "gpmac_1231902686_biz@paypal.com";
        String courseId = "123456";
        String courseName = "Advanced Rocket Science";
        String parentTransId = null;
        String paymentFee = amount.multiply(new BigDecimal(".03")).toString();
        PaymentProcessor paymentProcessor = PaymentProcessor.PAY_PAL;
        String reasonCode = null;

        // Normal Transaction
        createPaymentTransaction(
                paymentConfig, price, studentId,
                nterId, cpId, transId, sellerId,
                sellerEmail, courseId, courseName, PaymentStatus.COMPLETED,
                TransactionType.EXPRESS_CHECKOUT, parentTransId,
                paymentFee, paymentProcessor.value(), reasonCode);

        // Refund:
        // paymentStatus = Refunded
        String refundTransId = getNewTransId();
        String refundAmount = "-22.00";
        parentTransId = transId;
        paymentFee = null;
        reasonCode = ReasonCode.REFUND.value();

        createPaymentTransaction(
                paymentConfig, refundAmount, studentId,
                nterId, cpId, refundTransId, sellerId,
                sellerEmail, courseId, courseName, PaymentStatus.REFUNDED,
                TransactionType.ADJUSTMENT, parentTransId,
                paymentFee, paymentProcessor.value(), reasonCode);

        // NTER-Disbursement:
        // transactionType = nter_disbursement
        String disbursementTransId = getNewTransId();
        String disbursementAmount = "-12.00";
        studentId = null;
        courseId = "DISBURSEMENT";
        courseName = "DISBURSEMENT";
        parentTransId = null;
        paymentFee = null;
        nterId = "NTER-1";
        cpId = null;
        paymentProcessor = PaymentProcessor.OTHER;
        reasonCode = null;

        createPaymentTransaction(
                paymentConfig, disbursementAmount, studentId,
                nterId, cpId, disbursementTransId, sellerId,
                sellerEmail, courseId, courseName, PaymentStatus.COMPLETED,
                TransactionType.DISBURSEMENT, parentTransId,
                paymentFee, paymentProcessor.value(), reasonCode);

        // NWTP-Disbursement:
        // transactionType = nter_disbursement
        disbursementTransId = getNewTransId();
        disbursementAmount = "-122.00";
        studentId = null;
        courseId = "DISBURSEMENT";
        courseName = "DISBURSEMENT";
        parentTransId = null;
        paymentFee = null;
        nterId = null;
        cpId = "NWTP-1";

        createPaymentTransaction(
                paymentConfig, disbursementAmount, studentId,
                nterId, cpId, disbursementTransId, sellerId,
                sellerEmail, courseId, courseName, PaymentStatus.COMPLETED,
                TransactionType.DISBURSEMENT, parentTransId,
                paymentFee, paymentProcessor.value(), reasonCode);
    }

    private String getNewTransId() {
        Random r = new Random();
        long txId = r.nextLong();
        if (txId < 0) {
            txId *= -1;
        }
        return txId + "";
    }

    // Send a payment transaction to the Notification Servlet
    private void createPaymentTransaction(
            PaymentConfig paymentConfig, String price,
            @Nullable String studentId,
            String nterId, String cpId,
            String transId, String sellerId,
            String sellerEmail, String courseId,
            String courseName, PaymentStatus paymentStatus,
            TransactionType transactionType,
            @Nullable String parentTransactionId,
            String paymentFee, String paymentProcessor, String reasonCode) {

        System.out.println("Sending Notification to [" +
                paymentConfig.getNotifyURL() + "]");

        HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "Test Client");
        PostMethod post = new PostMethod(paymentConfig.getNotifyURL());

        post.addParameter("mc_gross", price);
        post.addParameter("protection_eligibility", "Eligible");
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
        if (paymentFee != null) {
            post.addParameter("mc_fee", paymentFee);
        }
        post.addParameter("address_country_code", "US");
        post.addParameter("address_name", "TestUser");
        post.addParameter("notify_version", "2.6");

        // Custom field
        StringBuilder sb = new StringBuilder();
        if (studentId != null) {
            sb.append("studentId=");
            sb.append(studentId);
            sb.append("|");
        }
        if (nterId != null) {
            sb.append("nterId=");
            sb.append(nterId);
            sb.append("|");
        }
        if (cpId != null) {
            sb.append("cpId=");
            sb.append(cpId);
            sb.append("|");
        }
        if (paymentProcessor != null) {
            sb.append("payment_processor=");
            sb.append(paymentProcessor);
        }
        post.addParameter("custom", sb.toString());

        post.addParameter("payer_status", "verified");
        post.addParameter("address_country", "United States");
        post.addParameter("address_city", "San Jose");
        post.addParameter("quantity", "1");
        post.addParameter("verify_sign", "AtkOfCXbDm2hu0ZELryHFjY-Vb7PAUvS6nMXgysbElEn9v-1XcmSoGtf");
        if (studentId != null) {
            post.addParameter("payer_email", studentId);
        }
        post.addParameter("txn_id", transId + "");
        post.addParameter("payment_type", "instant");
        post.addParameter("last_name", "User");
        post.addParameter("address_state", "CA");
        post.addParameter("receiver_email", sellerEmail);
        if (paymentFee != null) {
            post.addParameter("payment_fee", paymentFee);
        }
        post.addParameter("receiver_id", sellerId);
        post.addParameter("txn_type", transactionType.value());
        post.addParameter("item_name", courseName);
        post.addParameter("mc_currency", "USD");
        post.addParameter("item_number", courseId);
        post.addParameter("residence_country", "US");
        post.addParameter("test_ipn", "1");
        //post.addParameter("handling_amount", "0.00");
        post.addParameter("payment_gross", price);
        if (reasonCode != null) {
            post.addParameter("reason_code", reasonCode);
        }
        if (parentTransactionId != null) {
            post.addParameter("parent_txn_id", parentTransactionId);
        }

        //BufferedReader br = null;
        int returnCode = 0;
        try {
            returnCode = client.executeMethod(post);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        Assert.isTrue(returnCode == 200, "Response from POST: " + returnCode);
    }


//    // Send a payment transaction to the Notification Servlet
//    private void createRefundTransaction(
//            PaymentConfig paymentConfig, String price, String studentId,
//            String nterId, String cpId, String transId, String sellerId,
//            String sellerEmail, String courseId) {
//
//        System.out.println("Sending Notification to [" +
//                paymentConfig.getNotifyURL() + "]");
//
//        HttpClient client = new HttpClient();
//        client.getParams().setParameter("http.useragent", "Test Client");
//        PostMethod post = new PostMethod(paymentConfig.getNotifyURL());
//
//        post.addParameter("mc_gross", price);
//        post.addParameter("protection_eligibility", "Eligible");
//        post.addParameter("address_status", "confirmed");
//        post.addParameter("payer_id", "J3HBEKX9ADJHL");
//        post.addParameter("tax", "0.00");
//        post.addParameter("address_street", "1 Main St");
//
//        // Payment Date
//        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MMM dd, yyyy z");
//        Date today = new Date();
//        String paymentDate = dateFormat.format(today);
//        post.addParameter("payment_date", paymentDate);
//
//        post.addParameter("payment_status", "Completed");
//        post.addParameter("address_zip", "95131");
//        post.addParameter("first_name", "Test");
//        post.addParameter("mc_fee", "0.88");
//        post.addParameter("address_country_code", "US");
//        post.addParameter("address_name", "TestUser");
//        post.addParameter("notify_version", "2.6");
//
//        // Custom field
//        post.addParameter("custom",
//                "studentId=" + studentId + "|" + "nterId=" + nterId + "|" +
//                        "cpId=" + cpId);
//
//        post.addParameter("payer_status", "verified");
//        post.addParameter("address_country", "United States");
//        post.addParameter("address_city", "San Jose");
//        post.addParameter("quantity", "1");
//        post.addParameter("verify_sign", "AtkOfCXbDm2hu0ZELryHFjY-Vb7PAUvS6nMXgysbElEn9v-1XcmSoGtf");
//        post.addParameter("payer_email", studentId);
//        post.addParameter("txn_id", transId + "");
//        post.addParameter("payment_type", "instant");
//        post.addParameter("last_name", "User");
//        post.addParameter("address_state", "CA");
//        post.addParameter("receiver_email", sellerEmail);
//        post.addParameter("payment_fee", "0.88");
//        post.addParameter("receiver_id", sellerId);
//        post.addParameter("txn_type", "express_checkout");
//        post.addParameter("item_name", "A Guide To Everything");
//        post.addParameter("mc_currency", "USD");
//        post.addParameter("item_number", courseId);
//        post.addParameter("residence_country", "US");
//        post.addParameter("test_ipn", "1");
//        post.addParameter("handling_amount", "0.00");
//        post.addParameter("payment_gross", price);
//        post.addParameter("shipping", "0.00");
//
//        //BufferedReader br = null;
//        int returnCode = 0;
//        try {
//            returnCode = client.executeMethod(post);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            post.releaseConnection();
////            if (br != null) {
////                try {
////                    br.close();
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
//        }
//        Assert.isTrue(returnCode == 200, "Response from POST: " + returnCode);
//    }
//
//    // Send a payment transaction to the Notification Servlet
//    private void createDisbursementTransaction(
//            PaymentConfig paymentConfig, String price, String studentId,
//            String nterId, String cpId, String transId, String sellerId,
//            String sellerEmail, String courseId) {
//
//        System.out.println("Sending Notification to [" +
//                paymentConfig.getNotifyURL() + "]");
//
//        HttpClient client = new HttpClient();
//        client.getParams().setParameter("http.useragent", "Test Client");
//        PostMethod post = new PostMethod(paymentConfig.getNotifyURL());
//
//        post.addParameter("mc_gross", price);
//        post.addParameter("protection_eligibility", "Eligible");
//        post.addParameter("address_status", "confirmed");
//        post.addParameter("payer_id", "J3HBEKX9ADJHL");
//        post.addParameter("tax", "0.00");
//        post.addParameter("address_street", "1 Main St");
//
//        // Payment Date
//        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MMM dd, yyyy z");
//        Date today = new Date();
//        String paymentDate = dateFormat.format(today);
//        post.addParameter("payment_date", paymentDate);
//
//        post.addParameter("payment_status", "Completed");
//        post.addParameter("address_zip", "95131");
//        post.addParameter("first_name", "Test");
//        post.addParameter("mc_fee", "0.88");
//        post.addParameter("address_country_code", "US");
//        post.addParameter("address_name", "TestUser");
//        post.addParameter("notify_version", "2.6");
//
//        // Custom field
//        post.addParameter("custom",
//                "studentId=" + studentId + "|" + "nterId=" + nterId + "|" +
//                        "cpId=" + cpId);
//
//        post.addParameter("payer_status", "verified");
//        post.addParameter("address_country", "United States");
//        post.addParameter("address_city", "San Jose");
//        post.addParameter("quantity", "1");
//        post.addParameter("verify_sign", "AtkOfCXbDm2hu0ZELryHFjY-Vb7PAUvS6nMXgysbElEn9v-1XcmSoGtf");
//        post.addParameter("payer_email", studentId);
//        post.addParameter("txn_id", transId + "");
//        post.addParameter("payment_type", "instant");
//        post.addParameter("last_name", "User");
//        post.addParameter("address_state", "CA");
//        post.addParameter("receiver_email", sellerEmail);
//        post.addParameter("payment_fee", "0.88");
//        post.addParameter("receiver_id", sellerId);
//        post.addParameter("txn_type", "express_checkout");
//        post.addParameter("item_name", "A Guide To Everything");
//        post.addParameter("mc_currency", "USD");
//        post.addParameter("item_number", courseId);
//        post.addParameter("residence_country", "US");
//        post.addParameter("test_ipn", "1");
//        post.addParameter("handling_amount", "0.00");
//        post.addParameter("payment_gross", price);
//        post.addParameter("shipping", "0.00");
//
//        int returnCode = 0;
//        try {
//            returnCode = client.executeMethod(post);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            post.releaseConnection();
//        }
//        Assert.isTrue(returnCode == 200, "Response from POST: " + returnCode);
//    }

}
