package org.nterlearning.commerce.model;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.CommerceConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import java.util.List;

/**
 * User: Deringer
 * Date: 8/10/11
 * Time: 1:57 PM
 */
public interface ConfigurationModel {

    /**
     * Create a new Payment Processor configuration
     * @param configurationEntry The object containing the configuration data
     * @see PaymentConfig
     */
    void createPaymentConfig(PaymentConfig configurationEntry);

    /**
     * Update an existing Payment Processor configuration
     * @param configurationEntry The object containing the configuration data
     */
    void updatePaymentConfig(PaymentConfig configurationEntry);

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
    PaymentConfig getPaymentConfig(PaymentProcessor uniqueIdentifier);

    /**
     * Retrieve all Payment Processor configurations
     * @return A list of PaymentConfig objects containing the configuration data
     */
    List<PaymentConfig> getPaymentConfigs();

    /**
     * Create or update commerce configuration global data
     * @param commerceConfig The object containing the configuration data
     */
    void createOrUpdateCommerceConfig(CommerceConfig commerceConfig);

    /**
     * Retrieve global commerce configuration information
     * @return A CommerceConfig object containing the configuration data
     */
    CommerceConfig getCommerceConfig();

}
