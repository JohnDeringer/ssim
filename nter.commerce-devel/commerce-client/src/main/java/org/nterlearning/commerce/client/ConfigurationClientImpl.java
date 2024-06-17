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

import org.nterlearning.commerce.configuration.client.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/11/12
 */
public class ConfigurationClientImpl implements ConfigurationClient {

    private static ConfigurationInterface configurationInterface;
    private String wsdlLocation = null;
    private CommerceClientCallback commerceClientCallback;
    private String user = null;
    private Logger logger = LoggerFactory.getLogger(ConfigurationClientImpl.class);

    public ConfigurationClientImpl(String user,
                              String password,
                              String wsdlLocation) {
        commerceClientCallback = new CommerceClientCallback();
        this.user = user;
        commerceClientCallback.setUsername(this.user);
        commerceClientCallback.setPassword(password);

        this.wsdlLocation = wsdlLocation;
    }

    @Override
    public void createPaymentConfig(@NotNull PaymentConfig configurationEntry) {
        if (getConfigurationInterface() != null) {
            CreatePaymentConfig request = new CreatePaymentConfig();
            request.setConfigurationEntry(configurationEntry);

            CreatePaymentConfigResponse response =
                    configurationInterface.createPaymentConfig(request);
            if (response.getStatus() != RequestStatus.SUCCESS) {
                logger.error("Unexpected response from createPaymentConfig [" +
                    response.getStatus()+ ']');
            }
        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                configurationInterface + "]");
        }
    }

    @Override
    public void updatePaymentConfig(@NotNull PaymentConfig configurationEntry) {
        if (getConfigurationInterface() != null) {
            UpdatePaymentConfig request = new UpdatePaymentConfig();
            request.setConfigurationEntry(configurationEntry);

            UpdatePaymentConfigResponse response =
                    configurationInterface.updatePaymentConfig(request);
            if (response.getStatus() != RequestStatus.SUCCESS) {
                logger.error("Unexpected response from updatePaymentConfig [" +
                    response.getStatus()+ ']');
            }
        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                configurationInterface + "]");
        }
    }

    @Override
    public void createOrUpdateCommerceConfig(@NotNull CommerceConfig commerceConfig) {
        if (getConfigurationInterface() != null) {
            CreateOrUpdateCommerceConfig request =
                    new CreateOrUpdateCommerceConfig();
            request.setCommerceConfig(commerceConfig);

            CreateOrUpdateCommerceConfigResponse response =
                    configurationInterface.createOrUpdateCommerceConfig(request);
            if (response.getStatus() != RequestStatus.SUCCESS) {
                logger.error("Unexpected response from createOrUpdateCommerceConfig [" +
                    response.getStatus()+ ']');
            }
        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                configurationInterface + "]");
        }
    }

    @Override
    public void removePaymentConfig(@NotNull PaymentProcessor uniqueIdentifier) {
        if (getConfigurationInterface() != null) {
            RemovePaymentConfig request = new RemovePaymentConfig();
            request.setConfigId(uniqueIdentifier);

            RemovePaymentConfigResponse response =
                    configurationInterface.removePaymentConfig(request);
            if (response.getStatus() != RequestStatus.SUCCESS) {
                logger.error("Unexpected response from removePaymentConfig [" +
                    response.getStatus()+ ']');
            }
        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                configurationInterface + "]");
        }
    }

    @Override
    @Nullable
    public PaymentConfig getPaymentConfig(
            @NotNull PaymentProcessor uniqueIdentifier) {
        PaymentConfig paymentConfig = null;
        if (getConfigurationInterface() != null) {
            GetPaymentConfig request = new GetPaymentConfig();
            request.setConfigId(uniqueIdentifier);

            GetPaymentConfigResponse response =
                    configurationInterface.getPaymentConfig(request);

            paymentConfig = response.getConfigurationEntry();
        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                configurationInterface + "]");
        }
        return paymentConfig;
    }

    @Override
    public ClientPaymentConfig getClientPaymentConfig(
            @NotNull PaymentProcessor uniqueIdentifier) {
        ClientPaymentConfig paymentConfig = null;
        if (getConfigurationInterface() != null) {
            GetClientPaymentConfig request = new GetClientPaymentConfig();
            request.setConfigId(uniqueIdentifier);

            GetClientPaymentConfigResponse response =
                    configurationInterface.getClientPaymentConfig(request);

            paymentConfig = response.getConfigurationEntry();
        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                configurationInterface + "]");
        }
        return paymentConfig;
    }

    @Override
    @NotNull
    public List<PaymentConfig> getPaymentConfigs() {
        List<PaymentConfig> paymentConfigs = new ArrayList<PaymentConfig>();
        if (getConfigurationInterface() != null) {
            GetPaymentConfigs request = new GetPaymentConfigs();

            GetPaymentConfigsResponse response =
                    configurationInterface.getPaymentConfigs(request);

            paymentConfigs = response.getConfigurationEntry();
        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                configurationInterface + "]");
        }
        return paymentConfigs;
    }

    @Override
    @Nullable
    public CommerceConfig getCommerceConfig() {
        CommerceConfig commerceConfig = null;
        if (getConfigurationInterface() != null) {
            GetCommerceConfig request = new GetCommerceConfig();

            GetCommerceConfigResponse response =
                    configurationInterface.getCommerceConfig(request);

            commerceConfig = response.getCommerceConfig();
        } else {
            logger.warn("Invalid reference to commerce transactionAPI [" +
                configurationInterface + "]");
        }
        return commerceConfig;
    }

    @Override
    @Nullable
    public String getUser() {
        return this.user;
    }

    private ConfigurationInterface getConfigurationInterface() {
        if (configurationInterface == null) {
            if (wsdlLocation != null && !wsdlLocation.isEmpty()) {
                logger.info("Attempting to contact the Entitlement service using [" +
                        wsdlLocation + "]");
                try {
                    URL uRL = new URL(wsdlLocation);
                    ConfigurationAPI configurationAPI = new ConfigurationAPI(uRL);
                    configurationInterface =
                            configurationAPI.getConfigurationAPISoap12EndPoint();

                    // WS-Security header
                    Map<String, Object> ctx = new HashMap<String, Object>();

                    ctx.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
                    ctx.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
                    ctx.put(WSHandlerConstants.USER,
                            commerceClientCallback.getUsername());
                    ctx.put(WSHandlerConstants.PW_CALLBACK_REF,
                             commerceClientCallback);

                    Client client = ClientProxy.getClient(configurationInterface);
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
                        "Unexpected exception connecting to Commerce configurationAPI using wsdl [" +
                            wsdlLocation + "]", e);
                }
            } else {
                logger.warn("Invalid Commerce configuration wsdl url [" +
                        wsdlLocation + "], Commerce configurationAPI will not be accessible");
            }
        }
        return configurationInterface;
    }

}
