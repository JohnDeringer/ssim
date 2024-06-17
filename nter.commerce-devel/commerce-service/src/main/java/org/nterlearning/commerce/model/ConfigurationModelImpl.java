package org.nterlearning.commerce.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.commerce.persistence.CommerceConfigEntity;
import org.nterlearning.commerce.persistence.ConfigurationDao;
import org.nterlearning.commerce.persistence.PaymentConfigEntity;
import org.nterlearning.xml.commerce.configuration_interface_0_1_0.*;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.CommerceConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Deringer
 * Date: 8/10/11
 * Time: 1:57 PM
 */
public class ConfigurationModelImpl implements ConfigurationModel{

    ConfigurationDao configurationDao;

    private Logger logger = LoggerFactory.getLogger(ConfigurationModelImpl.class);
    private static final String XSD_CONFIGURATION = "configuration-interface_0.1.0.xsd";

    @Override
    public void createPaymentConfig(@NotNull PaymentConfig configEntry) {
        logger.info("createPaymentConfig with PaymentConfig [" +
                configEntry.getID() + "]");

        // Validate the data
        try {
            JAXBContext jc = JAXBContext.newInstance(CreatePaymentConfig.class);
            CreatePaymentConfig request = new CreatePaymentConfig();
            request.setConfigurationEntry(configEntry);

            JAXBSource source = new JAXBSource(jc, request);
            URL url =
                ConfigurationModelImpl.class.getClassLoader().getResource(
                        XSD_CONFIGURATION);
            if (url != null) {
                File file = new File(url.getFile());
                if (file.isFile()) {
                    SchemaFactory sf = SchemaFactory.newInstance(
                            XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema schema = sf.newSchema(file);

                    Validator validator = schema.newValidator();
                    validator.setErrorHandler(new DataValidationError());
                    validator.validate(source);
                } else {
                    logger.error("Cannot validate createPaymentConfig data, unable to locate schema [" +
                        file.getAbsolutePath() + "]");
                }
            } else {
                logger.error("Cannot validate createPaymentConfig data, unable to locate schema [" +
                    XSD_CONFIGURATION + "]");
            }
        } catch (JAXBException e) {
            logger.warn("Unexpected error attempting to validate createPaymentConfig data", e);
        } catch (SAXException e) {
            logger.warn("Unexpected error attempting to validate createPaymentConfig data", e);
        } catch (IOException e) {
            logger.warn("Unexpected error attempting to validate createPaymentConfig data", e);
        }

        configurationDao.createPaymentConfig(configEntry);
    }

    @Override
    public void updatePaymentConfig(@NotNull PaymentConfig configEntry) {
        logger.info("updatePaymentConfig with PaymentConfig [" +
                configEntry.getID() + "]");

        // Validate the data
        try {
            JAXBContext jc = JAXBContext.newInstance(UpdatePaymentConfig.class);
            UpdatePaymentConfig request = new UpdatePaymentConfig();
            request.setConfigurationEntry(configEntry);

            JAXBSource source = new JAXBSource(jc, request);
            URL url =
                ConfigurationModelImpl.class.getClassLoader().getResource(
                        XSD_CONFIGURATION);
            if (url != null) {
                File file = new File(url.getFile());
                if (file.isFile()) {
                    SchemaFactory sf = SchemaFactory.newInstance(
                            XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema schema = sf.newSchema(file);

                    Validator validator = schema.newValidator();
                    validator.setErrorHandler(new DataValidationError());
                    validator.validate(source);
                } else {
                    logger.error("Cannot validate updatePaymentConfig data, unable to locate schema [" +
                        file.getAbsolutePath() + "]");
                }
            } else {
                    logger.error("Cannot validate updatePaymentConfig data, unable to locate schema [" +
                        XSD_CONFIGURATION + "]");
            }
        } catch (JAXBException e) {
            logger.warn("Unexpected error attempting to validate updatePaymentConfig data", e);
        } catch (SAXException e) {
            logger.warn("Unexpected error attempting to validate updatePaymentConfig data", e);
        } catch (IOException e) {
            logger.warn("Unexpected error attempting to validate updatePaymentConfig data", e);
        }
        configurationDao.updatePaymentConfig(configEntry);
    }

    @Override
    public void removePaymentConfig(@NotNull PaymentProcessor uniqueIdentifier) {
        logger.info("removePaymentConfig with PaymentProcessor [" + uniqueIdentifier + "]");

        configurationDao.removePaymentConfig(uniqueIdentifier);
    }

    @Override
    @Nullable
    public PaymentConfig getPaymentConfig(@NotNull PaymentProcessor uniqueIdentifier) {
        PaymentConfig paymentConfig = null;
        PaymentConfigEntity paymentConfigEntity =
                configurationDao.getPaymentConfig(uniqueIdentifier);
        if (paymentConfigEntity != null) {
            paymentConfig = paymentConfigEntity.getPaymentConfig();
        }

        return paymentConfig;
    }

    @Override
    @NotNull
    public List<PaymentConfig> getPaymentConfigs() {
        List<PaymentConfig> paymentConfigList = new ArrayList<PaymentConfig>();
        for (PaymentConfigEntity paymentConfigEntity : configurationDao.getPaymentConfigs()) {
            PaymentConfig paymentConfig = paymentConfigEntity.getPaymentConfig();
            paymentConfigList.add(paymentConfig);
        }

        return paymentConfigList;
    }

    @Override
    public void createOrUpdateCommerceConfig(@NotNull CommerceConfig commerceConfig) {
        logger.info("createOrUpdateCommerceConfig with AdminFee [" +
                commerceConfig.getAdminFee() +
                "], and ReferrerFee [" + commerceConfig.getReferrerFee() + "]");

        // Validate the data
        try {
            JAXBContext jc =
                    JAXBContext.newInstance(CreateOrUpdateCommerceConfig.class);
            CreateOrUpdateCommerceConfig request =
                    new CreateOrUpdateCommerceConfig();
            request.setCommerceConfig(commerceConfig);

            JAXBSource source = new JAXBSource(jc, request);
            URL url =
                ConfigurationModelImpl.class.getClassLoader().getResource(
                        XSD_CONFIGURATION);
            if (url != null) {
                File file = new File(url.getFile());

                if (file.isFile()) {
                    SchemaFactory sf = SchemaFactory.newInstance(
                            XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema schema = sf.newSchema(file);

                    Validator validator = schema.newValidator();
                    validator.setErrorHandler(new DataValidationError());
                    validator.validate(source);
                } else {
                    logger.error("Cannot validate createOrUpdateCommerceConfig data, unable to locate schema [" +
                        file.getAbsolutePath() + "]");
                }
            } else {
                    logger.error("Cannot validate createOrUpdateCommerceConfig data, unable to locate schema [" +
                        XSD_CONFIGURATION + "]");
                }
        } catch (JAXBException e) {
            logger.warn("Unexpected error attempting to validate createOrUpdateCommerceConfig data", e);
        } catch (SAXException e) {
            logger.warn("Unexpected error attempting to validate createOrUpdateCommerceConfig data", e);
        } catch (IOException e) {
            logger.warn("Unexpected error attempting to validate createOrUpdateCommerceConfig data", e);
        }

        configurationDao.createOrUpdateCommerceConfig(commerceConfig);
    }

    @Override
    @Nullable
    public CommerceConfig getCommerceConfig() {
        CommerceConfig commerceConfig = null;

        CommerceConfigEntity storedConfig =
                configurationDao.getCommerceConfig();
        if (storedConfig != null) {
            commerceConfig = new CommerceConfig();

            commerceConfig.setID(storedConfig.getID());

            BigDecimal adminFee = storedConfig.getAdminFee();
            if (adminFee != null) {
                adminFee = adminFee.setScale(2, RoundingMode.HALF_EVEN);
                commerceConfig.setAdminFee(adminFee);
            }

            BigDecimal referrerFee = storedConfig.getReferrerFee();
            if (referrerFee != null) {
                referrerFee = referrerFee.setScale(2, RoundingMode.HALF_EVEN);
                commerceConfig.setReferrerFee(referrerFee);
            }
        }

        return commerceConfig;
    }

    /**
     * Spring dependency injection
     * @param dao ConfigurationDao
     */
    public void setConfigurationDao(ConfigurationDao dao) {
        configurationDao = dao;
    }

}
