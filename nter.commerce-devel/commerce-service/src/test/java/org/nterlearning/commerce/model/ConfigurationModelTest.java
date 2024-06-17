package org.nterlearning.commerce.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.CommerceConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

/**
 * User: Deringer
 * Date: 8/10/11
 * Time: 2:21 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/commerce-beans-test.xml")
public class ConfigurationModelTest {

    private static final String ADMIN_USER = "amadmin";

    @Autowired
    ConfigurationModel model;

    public ConfigurationModelTest() {
    }

    @Before
    public void initialize() {
        try {
            removeConfiguration(PaymentProcessor.PAY_PAL);
        } catch (Exception e) {}

        try {
            removeConfiguration(PaymentProcessor.AMAZON);
        } catch (Exception e) {}
    }

    @Test
    public void getPaymentConfiguration() throws Exception {
        PaymentProcessor paymentProcessor;
        Random r = new Random();
        int randomId = r.nextInt(10);
        if (randomId % 2 == 0) {
            paymentProcessor = PaymentProcessor.PAY_PAL;
        } else {
            paymentProcessor = PaymentProcessor.AMAZON;
        }

        // Create Configuration
        String password = "SomeSecret";
        createConfiguration(paymentProcessor, password);

        // Retrieve configuration
        PaymentConfig configurationEntry =
                model.getPaymentConfig(paymentProcessor);

        // Test entry was persisted
        Assert.notNull(configurationEntry,
                "Error creating payment configuration. Persisted config [" +
                configurationEntry.getConfigId() +
                        "] returned null");
        Assert.isTrue(configurationEntry.getConfigId() == paymentProcessor,
                "Error creating payment configuration. Persisted config [" +
                configurationEntry.getConfigId() +
                        "] does not match [" + paymentProcessor + "]");

        // Clean-up
        removeConfiguration(paymentProcessor);
	}

    @Test
    public void updatePaymentConfiguration() throws Exception {

        PaymentProcessor paymentProcessor;
        Random r = new Random();
        int randomId = r.nextInt(10);
        if (randomId % 2 == 0) {
            paymentProcessor = PaymentProcessor.PAY_PAL;
        } else {
            paymentProcessor = PaymentProcessor.AMAZON;
        }

        // Create Configuration
        String password = "SomeSecret";
        createConfiguration(paymentProcessor, password);

        // Retrieve configuration
        PaymentConfig configurationEntry =
                model.getPaymentConfig(paymentProcessor);

        // Test persisted value
        Assert.notNull(configurationEntry,
                "Error creating payment configuration. Persisted config [" +
                configurationEntry.getConfigId() +
                        "] returned null");
        Assert.isTrue(
                configurationEntry.getSellerPassword().equals(password),
                "Error creating payment configuration. Persisted config password[" +
                configurationEntry.getSellerPassword() +
                        "] does not match [" + password + "]");

        // Update configuration
        password = "SomeOtherSecret";
        configurationEntry.setSellerPassword(password);
        model.updatePaymentConfig(configurationEntry);

        // Retrieve configuration
        PaymentConfig updatedConfigurationEntry =
                model.getPaymentConfig(paymentProcessor);

        // Test updated value
        Assert.notNull(configurationEntry,
                "Error updating payment configuration. Persisted config [" +
                configurationEntry.getConfigId() +
                        "] returned null");
        Assert.isTrue(
                configurationEntry.getSellerPassword().equals(password),
                "Error updating payment configuration. Persisted config password[" +
                configurationEntry.getSellerPassword() +
                        "] does not match [" + password + "]");
    }

    @Test
    public void testCommerceConfiguration() {
        // Is there an existing payment configuration
        CommerceConfig commerceConfig = model.getCommerceConfig();

        Random random = new Random();
        BigDecimal adminFee;
        BigDecimal referrerFee;
        //RoundingMode rm = RoundingMode.
        MathContext mc = new MathContext(2);

        // Test Create
        if (commerceConfig == null) {
            // Create a new entry
            commerceConfig = new CommerceConfig();
            //adminFee = new BigDecimal(random.nextDouble(), mc);
            //referrerFee = new BigDecimal(random.nextDouble(), mc);
            adminFee = new BigDecimal("12.12");
            referrerFee = new BigDecimal("5");

            System.out.println("Insert commerce admin fee [" + adminFee + "]");
            System.out.println("Insert commerce referrer fee [" + referrerFee + "]");

            commerceConfig.setAdminFee(adminFee);
            commerceConfig.setReferrerFee(referrerFee);

            model.createOrUpdateCommerceConfig(commerceConfig);

            // Retrieve entry
            commerceConfig = model.getCommerceConfig();
            // Test values
            Assert.isTrue(commerceConfig.getAdminFee().compareTo(adminFee) == 0,
                    "Inserted AdminFee [" + commerceConfig.getAdminFee() +
                            "] does not equal target value [" + adminFee + "]");
            Assert.isTrue(commerceConfig.getReferrerFee().compareTo(referrerFee) == 0,
                    "Inserted ReferrerFee [" + commerceConfig.getReferrerFee() +
                            "] does not equal target value [" + referrerFee + "]");
        }

        // Test update
        adminFee = new BigDecimal(random.nextDouble(), mc);
        referrerFee = new BigDecimal(random.nextDouble(), mc);

        System.out.println("Update commerce admin fee [" + adminFee + "]");
        System.out.println("Update commerce referrer fee [" + referrerFee + "]");

        commerceConfig.setAdminFee(adminFee);
        commerceConfig.setReferrerFee(referrerFee);

        model.createOrUpdateCommerceConfig(commerceConfig);

        // Retrieve entry
        commerceConfig = model.getCommerceConfig();
        // Test values
        Assert.isTrue(commerceConfig.getAdminFee().compareTo(adminFee) == 0,
                    "Updated AdminFee [" + commerceConfig.getAdminFee() +
                            "] does not equal target value [" + adminFee + "]");
        Assert.isTrue(commerceConfig.getReferrerFee().compareTo(referrerFee) == 0,
                    "Updated ReferrerFee [" + commerceConfig.getReferrerFee() +
                            "] does not equal target value [" + referrerFee + "]");

    }

    private void createConfiguration(PaymentProcessor paymentProcessor,
                                     String password) {
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
        configurationEntry.setSellerPassword(password);

        model.createPaymentConfig(configurationEntry);
    }

    private void removeConfiguration(PaymentProcessor paymentProcessor) {
        model.removePaymentConfig(paymentProcessor);
    }

}
