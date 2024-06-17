package org.nterlearning.commerce.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.nterlearning.commerce.service.ServiceUtil;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.*;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0_wsdl.ValidationError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * User: Deringer
 * Date: 8/15/11
 * Time: 4:36 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/commerce-beans-test.xml")
public class PaymentTransactionTest {
    @Autowired
    private TransactionModelImpl transactionModel = null;
    @Autowired
    private ConfigurationModelImpl configurationModel = null;
    private static final String ADMIN_USER = "amadmin";

    @Before
    public void init() {
        PaymentConfig paymentConfig =
                configurationModel.getPaymentConfig(PaymentProcessor.PAY_PAL);
        if (paymentConfig == null) {
            // Create
            paymentConfig = new PaymentConfig();
            paymentConfig.setConfigId(PaymentProcessor.PAY_PAL);
            paymentConfig.setSellerId("QS6F6B7D3R2Q8");
            paymentConfig.setActionURL("https://www.sandbox.paypal.com/cgi-bin/webscr");
            paymentConfig.setNotifyURL("http://localhost:8080/commerce-service-0.0.1/notification");
            configurationModel.createPaymentConfig(paymentConfig);
        } else {
            // Update
            paymentConfig.setSellerId("QS6F6B7D3R2Q8");
            configurationModel.updatePaymentConfig(paymentConfig);
        }
    }

    @Test
    public void testDataExporter() throws Exception {
        // create transactions
        Random r = new Random();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String txId_1 = r.nextLong() + "";
        String txId_2 = r.nextLong() + "";
        String studentId = "jd@sri.com";
        String nterId = "NTER-1";
        String cpId = "NWTP-1";
        String courseId = "12345";
        BigDecimal price = new BigDecimal(23);

        CommerceConfig commerceConfig = new CommerceConfig();
        commerceConfig.setReferrerFee(new BigDecimal("3.5"));
        commerceConfig.setAdminFee(new BigDecimal("4.5"));
        configurationModel.createOrUpdateCommerceConfig(commerceConfig);

        createPaymentTransaction(
                txId_1, studentId, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);
        createPaymentTransaction(
                txId_2, studentId, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);

        // retrieve the transactions
        List<PaymentTransaction> transactions =
                transactionModel.getTransactions(
                        cal.getTime(), cal.getTime());
        // Test
        int transCount = 0;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) ||
                    transaction.getTransactionId().equals(txId_2)) {
                transCount++;
            }
        }

        // Test
        Assert.isTrue(transCount == 2,
                "Created 2 transactions, but received [" + transactions.size() + "]");

        DataExporter exporter = new DataExporter();
        String csvData = null;
        try {
            csvData = exporter.toCommaDelimited(transactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Export:" + csvData);

        Assert.notNull(csvData, "Failed to create CSV file");
        Assert.isTrue(!csvData.isEmpty(), "Failed to create CSV file");
    }

    @Test
    public void getTransactions() throws Exception {
        // create transactions
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String txId_2 = r.nextLong() + "";
        String studentId = "jd@sri.com";
        String nterId = "NTER-1";
        String cpId = "NWTP-1";
        String courseId = "12345";
        BigDecimal price = new BigDecimal(23);

        createPaymentTransaction(
                txId_1, studentId, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);
        createPaymentTransaction(
                txId_2, studentId, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);

        // retrieve the transactions
        List<PaymentTransaction> transactions =
                transactionModel.getTransactions(
                        cal.getTime(), cal.getTime());

        // Test
        int transactionCount = 0;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) ||
                transaction.getTransactionId().equals(txId_2)) {
                transactionCount++;
            }
        }
        Assert.isTrue(transactionCount == 2,
                "Created 2 transactions, but received [" + transactionCount + "]");
    }

    @Test
    public void getTransactionsById() throws Exception {
        // create transactions
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String txId_2 = r.nextLong() + "";
        String studentId = "jd@sri.com";
        String nterId = "NTER-1";
        String cpId = "NWTP-1";
        String courseId = "12345";
        BigDecimal price = new BigDecimal(23);

        createPaymentTransaction(
                txId_1, studentId, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);
        createPaymentTransaction(
                txId_2, studentId, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);

        // retrieve the transactions
        Assert.notNull(transactionModel.getTransactionById(txId_1),
                "Null transaction response");
    }

    @Test
    public void getTransactionsByStudentId() throws Exception {
        // create transactions
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String txId_2 = r.nextLong() + "";
        String studentId_1 = "jd@sri.com";
        String studentId_2 = "jderinger@sri.com";
        String nterId = "NTER-1";
        String cpId = "NWTP-1";
        String courseId = "12345";
        BigDecimal price = new BigDecimal(23);

        createPaymentTransaction(
                txId_1, studentId_1, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);
        createPaymentTransaction(
                txId_2, studentId_2, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);

        // retrieve the transactions
        List<PaymentTransaction> transactions =
                transactionModel.getTransactionsByStudentId(
                        studentId_2, cal.getTime(), cal.getTime());
        // Test
        int transactionCount = 0;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) ||
                transaction.getTransactionId().equals(txId_2)) {
                transactionCount++;
            }
        }
        Assert.isTrue(transactionCount == 1,
                "Created 1 transactions, but received [" + transactions.size() + "]");
    }

    @Test
    public void getTransactionsByResourceId() throws Exception {
        // create transactions
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String txId_2 = r.nextLong() + "";
        String studentId_1 = "jd@sri.com";
        String studentId_2 = "jderinger@sri.com";
        String nterId = "NTER-2";
        String cpId = "NWTP-2";
        String courseId = "12345";
        BigDecimal price = new BigDecimal(23);

        createPaymentTransaction(
                txId_1, studentId_1, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);
        createPaymentTransaction(
                txId_2, studentId_2, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);

        // retrieve the transactions
        List<PaymentTransaction> transactions =
                transactionModel.getTransactionsByInstitution(
                        nterId, cal.getTime(), cal.getTime());
        // Test
        int transactionCount = 0;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) ||
                transaction.getTransactionId().equals(txId_2)) {
                transactionCount++;
            }
        }
        Assert.isTrue(transactionCount == 2,
                "Created 2 transactions, but received [" + transactions.size() + "]");

        // Case: NTER_ID & CP_ID are the same
        nterId = "NTER-NWTP";
        cpId = "NTER-NWTP";
        String studentId = "test@sri.com";
        String txId = r.nextLong() + "";

        createPaymentTransaction(
                txId, studentId, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);

        // retrieve the transactions
        transactions =
                transactionModel.getTransactionsByInstitution(
                        nterId, cal.getTime(), cal.getTime());

         // Test
        transactionCount = 0;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId)) {
                transactionCount++;
            }
        }
        Assert.isTrue(transactionCount == 1,
                "Created 1 transactions, but received [" + transactions.size() + "]");
    }

    @Test
    public void getTransactionsByCourseId() throws Exception {
        // create transactions
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String txId_2 = r.nextLong() + "";
        String studentId_1 = "jd@sri.com";
        String studentId_2 = "jderinger@sri.com";
        String nterId = "NTER-2";
        String cpId = "NWTP-2";
        String courseId = "12345-x";
        BigDecimal price = new BigDecimal(23);

        createPaymentTransaction(
                txId_1, studentId_1, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);
        createPaymentTransaction(
                txId_2, studentId_2, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);

        // retrieve the transactions
        List<PaymentTransaction> transactions =
                transactionModel.getTransactionsByCourseId(
                        courseId, cal.getTime(), cal.getTime());

        // Test
        int transactionCount = 0;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) ||
                transaction.getTransactionId().equals(txId_2)) {
                transactionCount++;
            }
        }
        Assert.isTrue(transactionCount == 2,
                "Created 2 transactions, but received [" + transactions.size() + "]");
    }

    @Test
    public void getPaymentStatus() throws Exception {
        // create transactions
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String txId_2 = r.nextLong() + "";
        String studentId_1 = "jd@sri.com";
        String studentId_2 = "jderinger@sri.com";
        String nterId = "NTER-2";
        String cpId = "NWTP-3";
        String courseId = "12345";
        BigDecimal price = new BigDecimal(23);

        // Create the transactions
        createPaymentTransaction(
                txId_1, studentId_1, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.PENDING, PaymentType.INSTANT);
        createPaymentTransaction(
                txId_2, studentId_2, nterId, cpId, cal.getTime(), courseId,
                price, PaymentStatus.COMPLETED, PaymentType.INSTANT);

        // create Payment Processor configuration
        PaymentConfig paymentConfig =
                configurationModel.getPaymentConfig(PaymentProcessor.PAY_PAL);
        if (paymentConfig == null) {
            createConfiguration(PaymentProcessor.PAY_PAL);
        }

        // retrieve the transactions
        PaymentStatus paymentStatus =
                transactionModel.getPaymentStatus(cpId, studentId_2, courseId, price);
        // Test
        Assert.isTrue(paymentStatus == PaymentStatus.COMPLETED,
                "Expected PaymentStatus [PaymentStatus.COMPLETED] but received [" +
                        paymentStatus + "]");
    }

    @Test
    public void getPaymentStatusFailure() throws Exception {
        // create transactions
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String txId_2 = r.nextLong() + "";
        String studentId_1 = "jd@sri.com";
        String studentId_2 = "jderinger@sri.com";
        String nterId = "NTER-2";
        String cpId = "NWTP-3";
        String courseId = "12345";
        BigDecimal price = new BigDecimal(23);

        // Create the transactions
        createPaymentTransaction(
                txId_1, studentId_1, nterId, cpId, cal.getTime(), courseId,
                null, PaymentStatus.PENDING, PaymentType.INSTANT);
        createPaymentTransaction(
                txId_2, studentId_2, nterId, cpId, cal.getTime(), courseId,
                new BigDecimal(0), PaymentStatus.COMPLETED, PaymentType.INSTANT);

        // create Payment Processor configuration
        PaymentConfig paymentConfig =
                configurationModel.getPaymentConfig(PaymentProcessor.PAY_PAL);
        if (paymentConfig == null) {
            createConfiguration(PaymentProcessor.PAY_PAL);
        }

        // retrieve the paymentStatus
        PaymentStatus paymentStatus = PaymentStatus.UNKNOWN;
        try {
            paymentStatus =
                transactionModel.getPaymentStatus(cpId, studentId_2, courseId, price);
        } catch (Exception e) {
            if (e.getClass().equals(ValidationError.class)) {
                System.out.println(
                        "getPaymentStatusFailure - Expected validation error");
            } else {
                throw new RuntimeException(e);
            }
        }

        // Test
        Assert.isTrue(paymentStatus != PaymentStatus.COMPLETED,
                "Expected PaymentStatus [PaymentStatus.UNKNOWN] but received [" +
                        paymentStatus + "]");
    }

    private PaymentTransaction createPaymentTransaction(
            String txId, String studentId, String nterId, String cpId,
            Date transactionDate, String courseId, BigDecimal price,
            PaymentStatus paymentStatus, PaymentType paymentType) {
        // Create new PaymentTransaction
        PaymentTransaction paymentTransaction = new PaymentTransaction();

        paymentTransaction.setPaymentProcessor(PaymentProcessor.PAY_PAL);
        paymentTransaction.setTransactionId(txId);
        paymentTransaction.setItemName("A great course");
        paymentTransaction.setItemNumber(courseId);
        paymentTransaction.setQuantity(1);
        paymentTransaction.setPaymentDate(
                ServiceUtil.toXMLGregorianCalendar(transactionDate));
        paymentTransaction.setPayerStatus("verified");
        paymentTransaction.setPaymentStatus(paymentStatus);
        paymentTransaction.setPaymentType(paymentType);
        paymentTransaction.setTransactionType(TransactionType.EXPRESS_CHECKOUT);
        paymentTransaction.setCurrencyType("USD");
        paymentTransaction.setPaymentGross(price);
        paymentTransaction.setTax(new BigDecimal(0));
        paymentTransaction.setPaymentFee(new BigDecimal(0));
        paymentTransaction.setPaymentFee(new BigDecimal(.88));
        paymentTransaction.setHandlingAmount(new BigDecimal(0));
        paymentTransaction.setShipping(new BigDecimal(0));

        // SellerId from configuration
        PaymentConfig paymentConfig =
                configurationModel.getPaymentConfig(PaymentProcessor.PAY_PAL);
        paymentTransaction.setReceiverId(paymentConfig.getSellerId());
        paymentTransaction.setReceiverEmail("gpmac_1231902686_biz@paypal.com");

        paymentTransaction.setPayerEmail("2590_per@paypal.com");

        // User defined values
        paymentTransaction.setStudentId(studentId);
        paymentTransaction.setNterId(nterId);
        paymentTransaction.setCourseProviderId(cpId);

        paymentTransaction.setPayerId("LPLWNMTBWMFAY");
        paymentTransaction.setPayerStatus("verified");

        paymentTransaction.setProtectionEligibility("Eligible");
        paymentTransaction.setNotifyVersion(new BigDecimal(2));
        paymentTransaction.setVerifySign("AtkOfCXbDm2hu0ZELryHFjY-Vb7PAUvS6nMXgysbElEn9v-1XcmSoGtf");
        paymentTransaction.setTestIpn("1");
        paymentTransaction.setValidationStatus(ValidationStatus.COMPLETED);
        paymentTransaction.setSysDate(
                ServiceUtil.toXMLGregorianCalendar(new java.util.Date()));

        // Persist new PaymentTransaction
        transactionModel.createPaymentTransaction(paymentTransaction);

        return paymentTransaction;
    }

    private void createConfiguration(PaymentProcessor paymentProcessor) {
        PaymentConfig configurationEntry = new PaymentConfig();

        String imageURL = "http://someImage";
        String notifyURL = "http://localhost:8080/commerce-service-0.0.1/notification";
        String sellerId = "gpmac_1231902686_biz@paypal.com";
        String actionURL = "http://localhost:8080";

        configurationEntry.setConfigId(paymentProcessor);
        configurationEntry.setNotifyURL(notifyURL);
        configurationEntry.setSellerId(sellerId);
        configurationEntry.setActionURL(actionURL);
        configurationEntry.setActiveStatus(true);
        configurationEntry.setApiVersion("2.6");
        configurationEntry.setSellerPassword("some-password");

        configurationModel.createPaymentConfig(configurationEntry);
    }
}
