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

import org.nterlearning.commerce.configuration.client.ClientPaymentConfig;
import org.nterlearning.commerce.configuration.client.CommerceConfig;
import org.nterlearning.commerce.configuration.client.PaymentConfig;
import org.nterlearning.commerce.configuration.client.PaymentProcessor;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/11/12
 */
public interface ConfigurationClient {

    /**
     * Create a new Payment Processor configuration
     *
     * @param configurationEntry The object containing the configuration data
     * @see PaymentConfig
     */
    void createPaymentConfig(PaymentConfig configurationEntry);

    /**
     * Update an existing Payment Processor configuration
     *
     * @param configurationEntry The object containing the configuration data
     */
    void updatePaymentConfig(PaymentConfig configurationEntry);

    /**
     * Remove an existing Payment Processor configuration
     *
     * @param uniqueIdentifier The unique identifier of the configuration data
     */
    void removePaymentConfig(PaymentProcessor uniqueIdentifier);

    /**
     * Retrieves a Payment Processor configuration data
     *
     * @param uniqueIdentifier The unique identifier of the configuration data
     * @return A PaymentConfig object containing the configuration data
     */
    PaymentConfig getPaymentConfig(PaymentProcessor uniqueIdentifier);

    /**
     * Retrieves a Payment Processor configuration data
     *
     * @param uniqueIdentifier The unique identifier of the configuration data
     * @return A PaymentConfig object containing the configuration data
     */
    ClientPaymentConfig getClientPaymentConfig(PaymentProcessor uniqueIdentifier);

    /**
     * Retrieve all Payment Processor configurations
     *
     * @return A list of PaymentConfig objects containing the configuration data
     */
    List<PaymentConfig> getPaymentConfigs();

    /**
     * Create or update commerce configuration global data
     *
     * @param commerceConfig The object containing the configuration data
     */
    void createOrUpdateCommerceConfig(CommerceConfig commerceConfig);

    /**
     * Retrieve global commerce configuration information
     *
     * @return A CommerceConfig object containing the configuration data
     */
    CommerceConfig getCommerceConfig();

    /**
     * Returns the user used in the SOAP header
     * @return A String containing the WS-Security username
     */
    String getUser();
}
