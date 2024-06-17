package org.nterlearning.commerce.service;

import org.jetbrains.annotations.NotNull;

import org.nterlearning.commerce.model.ConfigurationModel;
import org.nterlearning.entitlement.client.Entitlement;
import org.nterlearning.xml.commerce.configuration_interface_0_1_0.*;
import org.nterlearning.xml.commerce.configuration_interface_0_1_0_wsdl.ConfigurationInterface;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Deringer
 * Date: 6/14/11
 * Time: 3:27 PM
 */
public class ConfigurationImpl
        implements ConfigurationInterface {

    private ConfigurationModel model;
    private Entitlement entitlement;
    private WSValidator wsValidator;
    private String commerceRealm;
    private Logger logger = LoggerFactory.getLogger(ConfigurationImpl.class);

    @Override
    public CreatePaymentConfigResponse createPaymentConfig(
            CreatePaymentConfig request) {

        PaymentConfig configEntry = request.getConfigurationEntry();

        String authorizingSubject = wsValidator.getUid();
        if (entitlement.isGlobalAdmin(commerceRealm, authorizingSubject)) {
            model.createPaymentConfig(configEntry);
        } else {
            throw new RuntimeException(
                    "Unable to perform operation, insufficient authorization");
        }

        CreatePaymentConfigResponse response = new CreatePaymentConfigResponse();
        response.setStatus(RequestStatus.SUCCESS);
        return response;
    }

    @Override
    public UpdatePaymentConfigResponse updatePaymentConfig(
            UpdatePaymentConfig request) {

        PaymentConfig configEntry = request.getConfigurationEntry();

        String authorizingSubject = wsValidator.getUid();
        if (entitlement.isGlobalAdmin(commerceRealm, authorizingSubject)) {
            model.updatePaymentConfig(configEntry);
        } else {
            throw new RuntimeException(
                    "Unable to perform operation, insufficient authorization");
        }

        UpdatePaymentConfigResponse response = new UpdatePaymentConfigResponse();
        response.setStatus(RequestStatus.SUCCESS);
        return response;
    }

    @Override
    public RemovePaymentConfigResponse removePaymentConfig(
            RemovePaymentConfig request) {
        PaymentProcessor uniqueIdentifier = request.getConfigId();

        String authorizingSubject = wsValidator.getUid();
        if (entitlement.isGlobalAdmin(commerceRealm, authorizingSubject)) {
            model.removePaymentConfig(uniqueIdentifier);
        } else {
            throw new RuntimeException(
                    "Unable to perform operation, insufficient authorization");
        }

        RemovePaymentConfigResponse response = new RemovePaymentConfigResponse();
        response.setStatus(RequestStatus.SUCCESS);
        return response;
    }

    @Override
    public GetPaymentConfigResponse getPaymentConfig(GetPaymentConfig request) {
        // Retrieve data
        PaymentConfig configEntry;
        String authorizingSubject = wsValidator.getUid();
        if (entitlement.isGlobalAdmin(commerceRealm, authorizingSubject)) {
            PaymentProcessor paymentProcessorId = request.getConfigId();
            configEntry = model.getPaymentConfig(paymentProcessorId);
        } else {
            throw new RuntimeException(
                    "Unable to perform operation, insufficient authorization");
        }
        // Create response
        GetPaymentConfigResponse response = new GetPaymentConfigResponse();
        response.setConfigurationEntry(configEntry);

        return response;
    }

    @Override
    public GetClientPaymentConfigResponse getClientPaymentConfig(
            GetClientPaymentConfig request) {
        PaymentProcessor paymentProcessorId = request.getConfigId();
        PaymentConfig paymentConfig = model.getPaymentConfig(paymentProcessorId);

        GetClientPaymentConfigResponse response =
                new GetClientPaymentConfigResponse();

        if (paymentConfig != null) {
            ClientPaymentConfig clientPaymentConfig = new ClientPaymentConfig();
            clientPaymentConfig.setConfigId(paymentConfig.getConfigId());
            clientPaymentConfig.setActionURL(paymentConfig.getActionURL());
            clientPaymentConfig.setButtonURL(paymentConfig.getButtonURL());
            clientPaymentConfig.setNotifyURL(paymentConfig.getNotifyURL());
            clientPaymentConfig.setSellerId(paymentConfig.getSellerId());

            response.setConfigurationEntry(clientPaymentConfig);
        }

        return response;
    }

    @Override
    public GetPaymentConfigsResponse getPaymentConfigs(
            GetPaymentConfigs request) {
        return getPaymentConfigs();
    }

    @Override
    public CreateOrUpdateCommerceConfigResponse createOrUpdateCommerceConfig(
            CreateOrUpdateCommerceConfig request) {
        CommerceConfig commerceConfig = request.getCommerceConfig();

        CreateOrUpdateCommerceConfigResponse response =
                new CreateOrUpdateCommerceConfigResponse();

        String authorizingSubject = wsValidator.getUid();
        if (entitlement.isGlobalAdmin(commerceRealm, authorizingSubject)) {
            model.createOrUpdateCommerceConfig(commerceConfig);
        } else {
            throw new RuntimeException(
                    "Unable to perform operation, insufficient authorization");
        }

        response.setStatus(RequestStatus.SUCCESS);

        return response;
    }

    @Override
    public GetCommerceConfigResponse getCommerceConfig(
            GetCommerceConfig request) {
        return getCommerceConfig();
    }

    @NotNull
    private GetPaymentConfigsResponse getPaymentConfigs() {
        List<PaymentConfig> configEntries;
        // Retrieve data
        String authorizingSubject = wsValidator.getUid();
        if (entitlement.isGlobalAdmin(commerceRealm, authorizingSubject)) {
            configEntries = model.getPaymentConfigs();
        } else {
            throw new RuntimeException(
                    "Unable to perform operation, insufficient authorization");
        }
        // Create response
        GetPaymentConfigsResponse response = new GetPaymentConfigsResponse();
        response.getConfigurationEntry().addAll(configEntries);

        return response;
    }

    @NotNull
    private GetCommerceConfigResponse getCommerceConfig() {
        GetCommerceConfigResponse response = new GetCommerceConfigResponse();
        CommerceConfig commerceConfig;

        String authorizingSubject = wsValidator.getUid();
        if (entitlement.isGlobalAdmin(commerceRealm, authorizingSubject)) {
            commerceConfig = model.getCommerceConfig();
            response.setCommerceConfig(commerceConfig);
        } else {
            throw new RuntimeException(
                    "Unable to perform operation, insufficient authorization");
        }

        return response;
    }

    /**
     * Spring dependency injection
     * @param model ConfigurationModel
     */
    public void setConfigurationModel(ConfigurationModel model) {
        this.model = model;
    }

    /**
     * Spring dependency injection
     * @param wsValidator WSValidator
     */
    public void setWsValidator(WSValidator wsValidator) {
        this.wsValidator = wsValidator;
    }

    /**
     * Spring dependency injection
     * @param entitlement Entitlement
     */
    public void setEntitlement(Entitlement entitlement) {
        this.entitlement = entitlement;
    }

    /**
     * Spring dependency injection
     * @param commerceRealm String containing entitlement realm
     */
    public void setCommerceRealm(String commerceRealm) {
        this.commerceRealm = commerceRealm;
    }

}
