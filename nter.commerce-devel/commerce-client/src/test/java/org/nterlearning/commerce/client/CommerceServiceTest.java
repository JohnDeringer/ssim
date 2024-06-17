package org.nterlearning.commerce.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.nterlearning.commerce.configuration.client.CommerceConfig;
import org.nterlearning.commerce.configuration.client.PaymentConfig;
import org.nterlearning.commerce.configuration.client.ClientPaymentConfig;
import org.nterlearning.commerce.configuration.client.PaymentProcessor;
import org.nterlearning.commerce.transaction.client.*;

import org.nterlearning.registry.client.ActiveStatusEnum;
import org.nterlearning.registry.client.Contact;
import org.nterlearning.registry.client.Institution;
import org.nterlearning.registry.client.RegistryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/19/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:commerce-client-beans-test.xml")
public class CommerceServiceTest {

    @Autowired
    ConfigurationClient configurationInterface;
    @Autowired
    TransactionClient transactionInterface;
    @Autowired
    RegistryImpl registryService;

    private static final String ACTION_URL =
        "https://www.sandbox.paypal.com/cgi-bin/webscr";
    private static final String BUTTON_URL =
        "https://www.sandbox.paypal.com/en_US/i/btn/btn_buynowCC_LG.gif";
    private static final String NOTIFICATION_URL =
        "https://localhost:9443/commerce-service-1.1.0-SNAPSHOT/services/rest/transactions/notification";

    @Test
    public void getClientPaymentConfig() {
        ClientPaymentConfig paymentConfig =
                configurationInterface.getClientPaymentConfig(PaymentProcessor.PAY_PAL);
        Assert.notNull(paymentConfig, "ClientPaymentConfig is null");

        Assert.notNull(paymentConfig.getConfigId(), "ConfigId is null");
        Assert.notNull(paymentConfig.getActionURL(), "ActionURL is null");
        Assert.notNull(paymentConfig.getButtonURL(), "ButtonURL is null");
        Assert.notNull(paymentConfig.getNotifyURL(), "NotifyURL is null");
        Assert.notNull(paymentConfig.getSellerId(), "SellerId is null");
    }

    @Test
    public void createPaymentConfig() {
        // Retrieve new PaymentConfig
        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.AMAZON);
        Assert.notNull(paymentConfig, "PaymentConfig was not created");
    }

    @Test
    public void updatePaymentConfig() {
        PaymentConfig paymentConfig;
        try {
            // Create configuration
            createConfiguration(PaymentProcessor.AMAZON);

            // Retrieve new PaymentConfig
            paymentConfig = configurationInterface.getPaymentConfig(PaymentProcessor.AMAZON);
            Assert.notNull(paymentConfig, "PaymentConfig was not created");

            // Update PaymentConfig
            paymentConfig.setApiPassword("setApiPassword");
            paymentConfig.setApiSignature("setApiSignature");
            paymentConfig.setButtonURL("setButtonURL");
            paymentConfig.setSellerEmail("setSellerEmail");
            paymentConfig.setActionURL("setActionURL");
            paymentConfig.setActiveStatus(false);
            paymentConfig.setApiUsername("setApiUsername");
            paymentConfig.setApiVersion("setApiVersion");
            paymentConfig.setNotifyURL("setNotifyURL");
            paymentConfig.setSellerPassword("setSellerPassword");

            configurationInterface.updatePaymentConfig(paymentConfig);

            // Retrieve new PaymentConfig
            PaymentConfig updatedPaymentConfig =
                    configurationInterface.getPaymentConfig(PaymentProcessor.AMAZON);
            Assert.notNull(paymentConfig, "PaymentConfig was not found");

            Assert.isTrue(updatedPaymentConfig.isActiveStatus() == paymentConfig.isActiveStatus());
            Assert.isTrue(updatedPaymentConfig.getApiPassword().equals(paymentConfig.getApiPassword()));
            Assert.isTrue(updatedPaymentConfig.getApiSignature().equals(paymentConfig.getApiSignature()));
            Assert.isTrue(updatedPaymentConfig.getApiUsername().equals(paymentConfig.getApiUsername()));
            Assert.isTrue(updatedPaymentConfig.getApiVersion().equals(paymentConfig.getApiVersion()));
            Assert.isTrue(updatedPaymentConfig.getActionURL().equals(paymentConfig.getActionURL()));
            Assert.isTrue(updatedPaymentConfig.getApiPassword().equals(paymentConfig.getApiPassword()));
            Assert.isTrue(updatedPaymentConfig.getButtonURL().equals(paymentConfig.getButtonURL()));
            Assert.isTrue(updatedPaymentConfig.getConfigId() == paymentConfig.getConfigId());
            Assert.isTrue(updatedPaymentConfig.getNotifyURL().equals(paymentConfig.getNotifyURL()));
            Assert.isTrue(updatedPaymentConfig.getSellerEmail().equals(paymentConfig.getSellerEmail()),
                    "Expected email [" + paymentConfig.getSellerEmail() +
                            "] but received [" + updatedPaymentConfig.getSellerEmail() + "]");
            Assert.isTrue(updatedPaymentConfig.getSellerId().equals(paymentConfig.getSellerId()));
            Assert.isTrue(updatedPaymentConfig.getSellerPassword().equals(paymentConfig.getSellerPassword()));
        } finally {
            // Clean-up
            configurationInterface.removePaymentConfig(PaymentProcessor.AMAZON);

            // Retrieve removed PaymentConfig
            paymentConfig =
                    configurationInterface.getPaymentConfig(PaymentProcessor.AMAZON);
            Assert.isNull(paymentConfig, "PaymentConfig was not removed");
        }
    }

    @Test
    public void getPaymentConfigs() {
        try {
            // Create configuration
            createConfiguration(PaymentProcessor.AMAZON);
            // Create configuration
            createConfiguration(PaymentProcessor.OTHER);

            List<PaymentConfig> paymentConfigs =
                    configurationInterface.getPaymentConfigs();

            Assert.notNull(paymentConfigs);
            Assert.isTrue(!paymentConfigs.isEmpty());
            Assert.isTrue(paymentConfigs.size() > 1);

            int match = 0;
            for (PaymentConfig paymentConfig : paymentConfigs) {
                if (paymentConfig.getConfigId() == PaymentProcessor.AMAZON ||
                        paymentConfig.getConfigId() == PaymentProcessor.OTHER) {
                    match++;
                }
            }
            Assert.isTrue(match == 2);
        } finally {
            // Clean-up
            configurationInterface.removePaymentConfig(PaymentProcessor.AMAZON);

            // Retrieve removed PaymentConfig
            PaymentConfig paymentConfig =
                    configurationInterface.getPaymentConfig(PaymentProcessor.AMAZON);
            Assert.isNull(paymentConfig, "PaymentConfig was not removed");

            configurationInterface.removePaymentConfig(PaymentProcessor.OTHER);

            // Retrieve removed PaymentConfig
            paymentConfig =
                    configurationInterface.getPaymentConfig(PaymentProcessor.OTHER);
            Assert.isNull(paymentConfig, "PaymentConfig was not removed");
        }
    }

    @Test
    public void createOrUpdateCommerceConfig() {
        BigDecimal adminFee = new BigDecimal(5.10).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal referrerFee = new BigDecimal(5.60).setScale(2, RoundingMode.HALF_EVEN);

        CommerceConfig commerceConfig = new CommerceConfig();
        commerceConfig.setAdminFee(adminFee);
        commerceConfig.setReferrerFee(referrerFee);

        configurationInterface.createOrUpdateCommerceConfig(commerceConfig);

        CommerceConfig newCommerceConfig =
                configurationInterface.getCommerceConfig();
        Assert.notNull(newCommerceConfig);
        Assert.isTrue(newCommerceConfig.getAdminFee().equals(adminFee),
                "Expected [" + adminFee +
                        "] but received [" + newCommerceConfig.getAdminFee() + "]");
        Assert.isTrue(newCommerceConfig.getReferrerFee().equals(referrerFee),
                "Expected [" + referrerFee +
                        "] but received [" + newCommerceConfig.getReferrerFee() + "]");
    }

    private String nterId;
    private String cpId;

    @Before
    public void init() {
        nterId = getRandomName("NTER");
        cpId = getRandomName("NWTP");

        // Create registry NTER institution
        Institution institution = new Institution();
        institution.setName(nterId);
        institution.setActiveStatus(ActiveStatusEnum.ACTIVE);
        Contact contact = new Contact();
        contact.setPersonName(getRandomName("contactName"));
        contact.setEmail(transactionInterface.getUser());
        institution.setContactInfo(contact);

        System.out.println("Creating institution [" + nterId + "]");
        registryService.createInstitution(institution);

        // Create registry NWTP institution
        institution = new Institution();
        institution.setName(cpId);
        institution.setActiveStatus(ActiveStatusEnum.ACTIVE);
        contact = new Contact();
        contact.setPersonName(getRandomName("contactName"));
        contact.setEmail(transactionInterface.getUser());
        institution.setContactInfo(contact);

        System.out.println("Creating institution [" + cpId + "]");
        registryService.createInstitution(institution);
    }

    @Test
    public void createPaymentTransaction() {
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String parentTxId = null;
        String studentId_1 = "jd@sri.com";
        String courseId = "12345";
        String price = "23";

        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        PaymentTransaction createTransaction =
            createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price,
                    studentId_1, nterId, cpId, courseId,
                    TransactionType.EXPRESS_CHECKOUT, PaymentStatus.COMPLETED);

        Assert.notNull(createTransaction);

        PaymentTransaction getTransaction =
                transactionInterface.getTransactionById(txId_1);
        Assert.notNull(getTransaction);

        // Admin Fee
        BigDecimal adminFeeRate = configurationInterface.getCommerceConfig().getAdminFee();
        Assert.notNull(adminFeeRate);
        adminFeeRate = adminFeeRate.divide(new BigDecimal(100));
        BigDecimal adminFee = new BigDecimal(price).multiply(adminFeeRate);

        // Referrer Fee
        BigDecimal referrerFeeRate = configurationInterface.getCommerceConfig().getReferrerFee();
        Assert.notNull(referrerFeeRate);
        referrerFeeRate = referrerFeeRate.divide(new BigDecimal(100));
        BigDecimal referrerFee = new BigDecimal(price).multiply(referrerFeeRate);

        Assert.isTrue(adminFee.equals(getTransaction.getAdminFee().setScale(3, RoundingMode.HALF_EVEN)),
            "Expected [" + adminFee + "] received [" + getTransaction.getAdminFee().setScale(3, RoundingMode.HALF_EVEN) + "]");
        Assert.isTrue(referrerFee.equals(getTransaction.getReferrerFee().setScale(3, RoundingMode.HALF_EVEN)),
                    "Expected [" + adminFee + "] received [" + getTransaction.getReferrerFee().setScale(3, RoundingMode.HALF_EVEN) + "]");

        Assert.isTrue(createTransaction.getCourseProviderId().equals(getTransaction.getCourseProviderId()));
        Assert.isTrue(createTransaction.getCurrencyType().equals(getTransaction.getCurrencyType()));
         Assert.isTrue(createTransaction.getItemName().equals(getTransaction.getItemName()));
        Assert.isTrue(createTransaction.getItemNumber().equals(getTransaction.getItemNumber()));
        Assert.isTrue(createTransaction.getNterId().equals(getTransaction.getNterId()));
        Assert.isTrue(createTransaction.getPayerEmail().equals(getTransaction.getPayerEmail()));
        Assert.isTrue(createTransaction.getPayerId().equals(getTransaction.getPayerId()));
        Assert.isTrue(createTransaction.getPayerStatus().equals(getTransaction.getPayerStatus()));
        //Assert.isTrue(createTransaction.getPaymentDate().equals(getTransaction.getPaymentDate()),
        //    "Expected [" + createTransaction.getPaymentDate() + "] received [" + getTransaction.getPaymentDate() + "]");
        Assert.isTrue(createTransaction.getPaymentFee().setScale(2, RoundingMode.HALF_EVEN).equals(getTransaction.getPaymentFee().setScale(2, RoundingMode.HALF_EVEN)),
                "Expected [" + createTransaction.getPaymentFee() + "] received [" + getTransaction.getPaymentFee().setScale(2, RoundingMode.HALF_EVEN) + "]");
        Assert.isTrue(createTransaction.getPaymentGross().setScale(2, RoundingMode.HALF_EVEN).equals(getTransaction.getPaymentGross().setScale(2, RoundingMode.HALF_EVEN)),
                "Expected [" + createTransaction.getPaymentGross() + "] received [" + getTransaction.getPaymentGross().setScale(2, RoundingMode.HALF_EVEN) + "]");
        Assert.isTrue(createTransaction.getPaymentProcessor() == getTransaction.getPaymentProcessor());
        Assert.isTrue(createTransaction.getPaymentStatus() == getTransaction.getPaymentStatus());
        Assert.isTrue(createTransaction.getPaymentType() == getTransaction.getPaymentType());
        Assert.isTrue(createTransaction.getReceiverEmail().equals(getTransaction.getReceiverEmail()));
        Assert.isTrue(createTransaction.getReceiverId().equals(getTransaction.getReceiverId()));
        Assert.isTrue(createTransaction.getStudentId().equals(getTransaction.getStudentId()));
        Assert.isTrue(createTransaction.getTransactionType() == getTransaction.getTransactionType());

    }

    @Test
    public void getTransactions() {
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String parentTxId = null;
        String studentId_1 = "jd@sri.com";
        String courseId = "123456";
        String price = "24";

        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        PaymentTransaction createTransaction =
            createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId_1,
                    nterId, cpId, courseId,
                    TransactionType.EXPRESS_CHECKOUT, PaymentStatus.COMPLETED);

        PaymentTransaction getTransaction =
                transactionInterface.getTransactionById(txId_1);
        Assert.notNull(getTransaction);
        Assert.isTrue(createTransaction.getTransactionId().equals(
                getTransaction.getTransactionId()));

        // Test method
        List<PaymentTransaction> transactions =
                transactionInterface.getTransactions(new Date(), new Date());
        Assert.isTrue(!transactions.isEmpty());
        boolean found = false;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find transaction [" + txId_1 + "]");
    }

    @Test
    public void getChildTransactionsById() {
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String parentTxId = null;
        String studentId_1 = "jd@sri.com";
        String courseId = "x12345";
        String price = "13";

        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        // Create normal transaction
        PaymentTransaction createTransaction =
            createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId_1,
                    nterId, cpId, courseId,
                    TransactionType.EXPRESS_CHECKOUT, PaymentStatus.COMPLETED);

        PaymentTransaction getTransaction =
                transactionInterface.getTransactionById(txId_1);
        Assert.notNull(getTransaction);
        Assert.isTrue(createTransaction.getTransactionId().equals(
                getTransaction.getTransactionId()));

        // Create child transaction
        parentTxId = txId_1;
        txId_1 = r.nextLong() + "";
        createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId_1,
                    nterId, cpId, courseId,
                TransactionType.ADJUSTMENT, PaymentStatus.COMPLETED);

        List<PaymentTransaction> childTransactions =
                transactionInterface.getChildTransactionsById(parentTxId);
        boolean found = false;
        for (PaymentTransaction transaction : childTransactions) {
            if (transaction.getTransactionParentId().equals(parentTxId)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find transaction [" + parentTxId + "]");
    }

    @Test
    public void getTransactionsByStudentId() {
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String parentTxId = null;
        String studentId_1 = "jderinger@sri.com";
        String courseId = "y12345";
        String price = "53";

        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        PaymentTransaction createTransaction =
            createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId_1,
                    nterId, cpId, courseId,
                    TransactionType.EXPRESS_CHECKOUT, PaymentStatus.COMPLETED);

        PaymentTransaction getTransaction =
                transactionInterface.getTransactionById(txId_1);
        Assert.notNull(getTransaction);
        Assert.isTrue(createTransaction.getTransactionId().equals(
                getTransaction.getTransactionId()));

        // Test method
        List<PaymentTransaction> transactions =
                transactionInterface.getTransactionsByStudentId(
                        studentId_1, new Date(), new Date());
        Assert.isTrue(!transactions.isEmpty());
        boolean found = false;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) &&
                    transaction.getStudentId().equals(studentId_1)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find transaction [" + txId_1 +
                "] by studentId [" + studentId_1 + "]");
    }

    @Test
    public void getTransactionsByCourseId() {
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String parentTxId = null;
        String studentId_1 = "john.deringer@sri.com";
        String courseId = "666";
        String price = "230.99";

        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        PaymentTransaction createTransaction =
            createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId_1,
                    nterId, cpId, courseId,
                    TransactionType.EXPRESS_CHECKOUT, PaymentStatus.COMPLETED);

        PaymentTransaction getTransaction =
                transactionInterface.getTransactionById(txId_1);
        Assert.notNull(getTransaction);
        Assert.isTrue(createTransaction.getTransactionId().equals(
                getTransaction.getTransactionId()));

        // Test method
        List<PaymentTransaction> transactions =
                transactionInterface.getTransactionsByCourseId(
                        courseId, new Date(), new Date());
        Assert.isTrue(!transactions.isEmpty());
        boolean found = false;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) &&
                    transaction.getItemNumber().equals(courseId)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find transaction [" + txId_1 +
                "] by courseId [" + courseId + "]");
    }

    @Test
    public void getTransactionsByTransactionType() {
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String parentTxId = null;
        String studentId_1 = "jd@sri.com";
        String courseId = "459882ll";
        String price = "63.89";
        TransactionType transactionType = TransactionType.EXPRESS_CHECKOUT;

        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        PaymentTransaction createTransaction =
            createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId_1,
                    nterId, cpId, courseId, transactionType, PaymentStatus.COMPLETED);

        PaymentTransaction getTransaction =
                transactionInterface.getTransactionById(txId_1);
        Assert.notNull(getTransaction);
        Assert.isTrue(createTransaction.getTransactionId().equals(
                getTransaction.getTransactionId()));

        // Test method
        List<PaymentTransaction> transactions =
                transactionInterface.getTransactionsByTransactionType(
                        nterId, transactionType, new Date(), new Date());
        Assert.isTrue(!transactions.isEmpty());
        boolean found = false;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) &&
                    transaction.getTransactionType() == transactionType &&
                    transaction.getNterId().equals(nterId)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find transaction [" + txId_1 +
                "] by transactionType [" + transactionType +
                "] and institution [" + nterId + "]");

        // Test method - 2
        transactions =
                transactionInterface.getTransactionsByTransactionType(
                        cpId, transactionType, new Date(), new Date());
        Assert.isTrue(!transactions.isEmpty());
        found = false;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) &&
                    transaction.getTransactionType() == transactionType &&
                    transaction.getCourseProviderId().equals(cpId)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find transaction [" + txId_1 +
                "] by transactionType [" + transactionType +
                "] and institution [" + cpId + "]");
    }

    @Test
    public void getTransactionsByInstitution() {
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String parentTxId = null;
        String studentId_1 = "jd@sri.com";
        String courseId = "89jkf744";
        String price = "19.99";
        TransactionType transactionType = TransactionType.EXPRESS_CHECKOUT;

        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        PaymentTransaction createTransaction =
            createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId_1,
                    nterId, cpId, courseId, transactionType, PaymentStatus.COMPLETED);

        PaymentTransaction getTransaction =
                transactionInterface.getTransactionById(txId_1);
        Assert.notNull(getTransaction);
        Assert.isTrue(createTransaction.getTransactionId().equals(
                getTransaction.getTransactionId()));

        // Test method
        List<PaymentTransaction> transactions =
                transactionInterface.getTransactionsByInstitution(
                        nterId, new Date(), new Date());
        Assert.isTrue(!transactions.isEmpty());
        boolean found = false;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) &&
                    transaction.getNterId().equals(nterId)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find transaction [" + txId_1 +
                "] by Institution [" + nterId + "]");

        // Test method - 2
        transactions =
                transactionInterface.getTransactionsByInstitution(
                        cpId, new Date(), new Date());
        Assert.isTrue(!transactions.isEmpty());
        found = false;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getTransactionId().equals(txId_1) &&
                    transaction.getCourseProviderId().equals(cpId)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find transaction [" + txId_1 +
                "] by Institution [" + cpId + "]");
    }

    @Test
    public void getTransactionSummaryByInstitution() {
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String parentTxId = null;
        String studentId_1 = "jack.frost@sri.com";
        String courseId = "43453345";
        String price = "45.45";
        TransactionType transactionType = TransactionType.EXPRESS_CHECKOUT;

        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        PaymentTransaction createTransaction =
            createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId_1,
                    nterId, cpId, courseId, transactionType, PaymentStatus.COMPLETED);

        PaymentTransaction getTransaction =
                transactionInterface.getTransactionById(txId_1);
        Assert.notNull(getTransaction);
        Assert.isTrue(createTransaction.getTransactionId().equals(
                getTransaction.getTransactionId()));

        // Test method
        List<PaymentTransaction> transactions =
                transactionInterface.getTransactionSummaryByInstitution(
                        nterId, new Date(), new Date());
        Assert.isTrue(!transactions.isEmpty());
        boolean found = false;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getItemNumber().equals(courseId) &&
                    transaction.getNterId().equals(nterId)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find courseId [" + courseId +
                "] by Institution [" + nterId + "]");

        // Test method - 2
        transactions =
                transactionInterface.getTransactionSummaryByInstitution(
                        cpId, new Date(), new Date());
        Assert.isTrue(!transactions.isEmpty());
        found = false;
        for (PaymentTransaction transaction : transactions) {
            if (transaction.getItemNumber().equals(courseId) &&
                    transaction.getCourseProviderId().equals(cpId)) {
                found = true;
                break;
            }
        }
        Assert.isTrue(found, "Unable to find courseId [" + courseId +
                "] by Institution [" + cpId + "]");
    }

    @Test
    public void getPaymentStatus() {
        Random r = new Random();
        String txId_1 = r.nextLong() + "";
        String parentTxId = null;
        String studentId = "jd@sri.com";
        String courseId = "12345";
        String price = "23";
        TransactionType transactionType = TransactionType.EXPRESS_CHECKOUT;

        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        PaymentTransaction createTransaction =
            createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId,
                    nterId, cpId, courseId, transactionType, PaymentStatus.COMPLETED);

        PaymentTransaction getTransaction =
                transactionInterface.getTransactionById(txId_1);
        Assert.notNull(getTransaction);
        Assert.isTrue(createTransaction.getTransactionId().equals(
                getTransaction.getTransactionId()));

        // Test method - NTER
        PaymentStatus paymentStatus =
                transactionInterface.getPaymentStatus(
                        "NTER", studentId, courseId, new BigDecimal(price)
                );
        Assert.isTrue(paymentStatus == PaymentStatus.COMPLETED);

        // Test method - NWTP
        paymentStatus =
                transactionInterface.getPaymentStatus(
                        cpId, studentId, courseId, new BigDecimal(price)
                );
        Assert.isTrue(paymentStatus == PaymentStatus.COMPLETED);

        // Refund
        price = "-23";
        createPaymentTransaction(
                paymentConfig, txId_1, parentTxId, price, studentId,
                    nterId, cpId, courseId, TransactionType.ADJUSTMENT,
                    PaymentStatus.REFUNDED);

        // Test method - NTER
        paymentStatus =
                transactionInterface.getPaymentStatus(
                        "NTER", studentId, courseId, new BigDecimal(price)
                );
        Assert.isTrue(paymentStatus == PaymentStatus.REFUNDED);

        // Test method - NWTP
        paymentStatus =
                transactionInterface.getPaymentStatus(
                        cpId, studentId, courseId, new BigDecimal(price)
                );
        Assert.isTrue(paymentStatus == PaymentStatus.REFUNDED);
    }

    //@Test
    public void getPaymentStatusRest() throws Exception {
        Random r = new Random();
        String sellerEmail = "admin@nterlearning.org";
        String sellerId = "QS6F6B7D3R2Q8";
        String studentId = "testUser@sri.com";
        String courseId = "T-" + r.nextInt();
        String price = "13.00";
        long transId = r.nextLong();

        // Check to see if PayPal config exists
        PaymentConfig paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);

        // If it doesn't exists create an entry
        if (paymentConfig == null) {

            // Create and populate the payload
            paymentConfig = new PaymentConfig();
            paymentConfig.setConfigId(PaymentProcessor.PAY_PAL);
            paymentConfig.setSellerId(sellerId);
            paymentConfig.setSellerPassword(sellerId + "");
            paymentConfig.setActionURL(ACTION_URL);
            paymentConfig.setActiveStatus(true);
            paymentConfig.setNotifyURL(NOTIFICATION_URL);
            paymentConfig.setButtonURL(BUTTON_URL);

            // Send the request
            configurationInterface.createPaymentConfig(paymentConfig);

            // Ensure that the Payment Config really exists
            paymentConfig =
                configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);
            Assert.notNull(paymentConfig);

        } else {
            // Update Payment Config to ensure that it has the correct values
            paymentConfig.setSellerId(sellerId);
            paymentConfig.setSellerPassword(sellerId + "");
            paymentConfig.setActionURL(ACTION_URL);
            paymentConfig.setActiveStatus(true);
            paymentConfig.setNotifyURL(NOTIFICATION_URL);
            paymentConfig.setButtonURL(BUTTON_URL);

            configurationInterface.updatePaymentConfig(paymentConfig);
        }

        // Ensure that the Payment Config really exists
        paymentConfig =
            configurationInterface.getPaymentConfig(PaymentProcessor.PAY_PAL);
        Assert.notNull(paymentConfig);
        // Create a payment transaction and POST it to the notification Servlet
        createPaymentTransactionRest(paymentConfig, price, studentId, nterId,
                cpId, (transId + ""), sellerId, sellerEmail, courseId);

        // Retrieve the transaction
        GetTransactionById transactionsByIdRequest = new GetTransactionById();
        // Request payload
        transactionsByIdRequest.setTransactionId(transId + "");

        // Send the transaction request
        System.out.println("Requesting transaction [" + transId + "]");
        PaymentTransaction transactionResponse =
                transactionInterface.getTransactionById(transId + "");

        Assert.notNull(transactionResponse);

        // Get the Payment Status
        PaymentStatus paymentStatus =
                transactionInterface.getPaymentStatus(
                        cpId, studentId, courseId, new BigDecimal(price));

        Assert.notNull(paymentStatus);
        Assert.isTrue(paymentStatus == PaymentStatus.COMPLETED);
    }

    private PaymentTransaction createPaymentTransaction(
            PaymentConfig paymentConfig, String txId, String parentTxId,
            String price, String studentId,
            String nterId, String cpId,
            String courseId,
            TransactionType transactionType, PaymentStatus paymentStatus) {

        PaymentTransaction paymentTransaction = new PaymentTransaction();

        org.nterlearning.commerce.transaction.client.PaymentProcessor paymentProcessor =
            org.nterlearning.commerce.transaction.client.PaymentProcessor.fromValue(
                    paymentConfig.getConfigId().value());

        paymentTransaction.setPaymentProcessor(paymentProcessor);
        paymentTransaction.setTransactionId(txId);
        paymentTransaction.setItemName("A great course");
        paymentTransaction.setItemNumber(courseId);
        paymentTransaction.setQuantity(1);
        paymentTransaction.setPaymentDate(new Date());
        paymentTransaction.setPayerStatus("verified");
        paymentTransaction.setPaymentStatus(paymentStatus);
        paymentTransaction.setPaymentType(PaymentType.INSTANT);
        paymentTransaction.setTransactionType(transactionType);
        paymentTransaction.setCurrencyType("USD");
        paymentTransaction.setPaymentGross(new BigDecimal(price));
        paymentTransaction.setTax(new BigDecimal(0));
        paymentTransaction.setPaymentFee(new BigDecimal(0));
        paymentTransaction.setPaymentFee(new BigDecimal(.88));
        //paymentTransaction.setAdminFee(new BigDecimal(.99));
        paymentTransaction.setHandlingAmount(new BigDecimal(0));
        paymentTransaction.setShipping(new BigDecimal(0));

        paymentTransaction.setReceiverId(paymentConfig.getSellerId());
        paymentTransaction.setReceiverEmail(paymentConfig.getSellerEmail());

        paymentTransaction.setPayerEmail("2590_per@paypal.com");

        if (parentTxId != null) {
            paymentTransaction.setTransactionParentId(parentTxId);
        }
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
        paymentTransaction.setSysDate(new Date());

        // Persist new PaymentTransaction
        transactionInterface.createPaymentTransaction(paymentTransaction);

        return paymentTransaction;
    }

    // Send a payment transaction to the Notification Servlet
    private void createPaymentTransactionRest(
            PaymentConfig paymentConfig, String price, String studentId,
            String nterId, String cpId, String transId, String sellerId,
            String sellerEmail, String courseId) {

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

        post.addParameter("payment_status", "Completed");
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
        post.addParameter("item_name", "A Guide To Everything");
        post.addParameter("mc_currency", "USD");
        post.addParameter("item_number", courseId);
        post.addParameter("residence_country", "US");
        post.addParameter("test_ipn", "1");
        post.addParameter("handling_amount", "0.00");
        post.addParameter("payment_gross", price);
        post.addParameter("shipping", "0.00");

        int returnCode = 0;
        try{
            returnCode = client.executeMethod(post);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        Assert.isTrue(returnCode == 200, "Response from POST: " + returnCode);
    }

    private PaymentConfig createConfiguration(
            PaymentProcessor paymentProcessor) {
        PaymentConfig configurationEntry = new PaymentConfig();

        String actionURL = "http://localhost:8080";
        String imageURL = "http://someImage";
        String notifyURL = "http://localhost:8080/commerce-service-0.0.1/notification";
        String sellerId = "gpmac_1231902686_biz@paypal.com";

        configurationEntry.setActionURL(actionURL);
        configurationEntry.setActiveStatus(true);
        configurationEntry.setApiVersion("2.6");
        configurationEntry.setButtonURL(imageURL);
        configurationEntry.setConfigId(paymentProcessor);
        configurationEntry.setNotifyURL(notifyURL);
        configurationEntry.setSellerId(sellerId);
        configurationEntry.setSellerPassword("Seller-password");
        configurationEntry.setApiUsername("APi-user");
        configurationEntry.setApiPassword("API-password");
        configurationEntry.setApiSignature("API-signature");
        configurationEntry.setSellerEmail("Seller@email.com");

        configurationInterface.createPaymentConfig(configurationEntry);

        return configurationEntry;
    }

    private String getRandomName(String prefix) {
        Random random = new Random();
        int randomInt = random.nextInt();
        randomInt = randomInt > 0 ? randomInt : randomInt * -1;
        return prefix + ":" + randomInt;
    }

}
