package org.nterlearning.commerce.persistence;

import org.nterlearning.commerce.persistence.CommerceConfigEntity;
import org.nterlearning.commerce.persistence.PaymentConfigEntity;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.CommerceConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import java.util.List;

/**
 * User: Deringer
 * Date: 6/17/11
 * Time: 3:42 PM
 */
public interface ConfigurationDao {

    /**
     * Create a new Payment Processor configuration
     * @param configEntry The object containing the configuration data
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig
     */
    void createPaymentConfig(
            PaymentConfig configEntry);

    /**
     * Update an existing Payment Processor configuration
     * @param configEntry The object containing the configuration data
     */
    void updatePaymentConfig(
            PaymentConfig configEntry);

    /**
     * Remove an existing Payment Processor configuration
     * @param uniqueIdentifier The unique identifier of the configuration data
     */
    void removePaymentConfig(PaymentProcessor uniqueIdentifier);

    /**
     * Retrieves a Payment Processor configuration data
     * @param uniqueIdentifier The unique identifier of the configuration data
     * @return A PaymentConfig object containing the configuration data
     */
    PaymentConfigEntity getPaymentConfig(PaymentProcessor uniqueIdentifier);

    /**
     * Retrieve all Payment Processor configurations
     * @return A list of PaymentConfig objects containing the configuration data
     * @see PaymentConfigEntity
     */
    List<PaymentConfigEntity> getPaymentConfigs();

    /**
     * Create or update commerce configuration global data
     * @param commerceConfig The object containing the configuration data
     */
    void createOrUpdateCommerceConfig(CommerceConfig commerceConfig);

    /**
     * Retrieve global commerce configuration information
     * @return A CommerceConfig object containing the configuration data
     */
    CommerceConfigEntity getCommerceConfig();

}
