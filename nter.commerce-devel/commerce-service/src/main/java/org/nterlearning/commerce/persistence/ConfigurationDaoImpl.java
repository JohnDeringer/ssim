package org.nterlearning.commerce.persistence;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.commerce.persistence.CommerceConfigEntity;
import org.nterlearning.commerce.persistence.PaymentConfigEntity;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.CommerceConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Deringer
 * Date: 6/17/11
 * Time: 3:42 PM
 */
public class ConfigurationDaoImpl
        extends JpaDaoSupport implements ConfigurationDao {

    private Logger logger = LoggerFactory.getLogger(ConfigurationDaoImpl.class);

    @Override
    @Transactional
    public void createPaymentConfig(@NotNull PaymentConfig configurationEntry) {

        // Test for uniqueness
        PaymentConfigEntity storedEntry =
                getPaymentConfig(configurationEntry.getConfigId());
        if (storedEntry != null) {
            throw new RuntimeException("Unique constraint violation. " +
                    "A configuration entry already exists with the name [" +
                    configurationEntry.getConfigId() + "]");
        }

        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(configurationEntry);
            jpaTemplate.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error creating Configuration Entry",
                    e);
        }
    }

    @Override
    @Transactional
    public void updatePaymentConfig(@NotNull PaymentConfig configEntry) {

        PaymentConfigEntity storedEntry =
                getPaymentConfig(configEntry.getConfigId());
        if (storedEntry == null) {
            throw new RuntimeException("Unable to update Configuration " +
                    "Entry using identifier [" +
                    configEntry.getConfigId() +
                    "], because it cannot be found");
        }

        // Update data
        storedEntry.setActionURL(configEntry.getActionURL());
        storedEntry.setActiveStatus(configEntry.isActiveStatus());
        storedEntry.setApiVersion(configEntry.getApiVersion());
        storedEntry.setButtonURL(configEntry.getButtonURL());
        storedEntry.setNotifyURL(configEntry.getNotifyURL());
        storedEntry.setSellerId(configEntry.getSellerId());
        storedEntry.setSellerPassword(configEntry.getSellerPassword());
        storedEntry.setApiUsername(configEntry.getApiUsername());
        storedEntry.setApiPassword(configEntry.getApiPassword());
        storedEntry.setApiSignature(configEntry.getApiSignature());
        storedEntry.setSellerEmail(configEntry.getSellerEmail());

        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(storedEntry);
            jpaTemplate.flush();
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating Configuration Entry", e);
        }
    }

    @Override
    @Transactional
    public void removePaymentConfig(@NotNull PaymentProcessor uniqueIdentifier) {
        PaymentConfigEntity configurationEntry =
                getPaymentConfig(uniqueIdentifier);
        if (configurationEntry == null) {
            throw new RuntimeException("Unable to remove Configuration " +
                    "Entry using identifier [" +
                    uniqueIdentifier + "], because it cannot be found");
        }

        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.remove(configurationEntry);
            jpaTemplate.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error removing Configuration " +
                    "Entry",
                    e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public PaymentConfigEntity getPaymentConfig(@NotNull PaymentProcessor uniqueIdentifier) {
        PaymentConfigEntity configurationEntry = null;
        try {
            List<PaymentConfigEntity> configList = getJpaTemplate().find(
                    "select c from PaymentConfigEntity c where c.configId = ?1",
                    uniqueIdentifier);
            if (configList != null && !configList.isEmpty()) {
                configurationEntry = configList.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Configuration " +
                    "Entry using identifier [" + uniqueIdentifier + "]", e);
        }
        return configurationEntry;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<PaymentConfigEntity> getPaymentConfigs() {
        List<PaymentConfigEntity> configList;
        try {
            configList = getJpaTemplate().find(
                    "select c from PaymentConfigEntity c");
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Configuration " +
                    "Entries", e);
        }
        return configList;
    }

    @Override
    @Transactional
    public void createOrUpdateCommerceConfig(@NotNull CommerceConfig commerceConfig) {
        try {
            JpaTemplate jpaTemplate = getJpaTemplate();

            CommerceConfigEntity storedConfig = getCommerceConfig();
            // Update
            if (storedConfig != null) {
                storedConfig.setAdminFee(commerceConfig.getAdminFee());
                storedConfig.setReferrerFee(commerceConfig.getReferrerFee());
                jpaTemplate.merge(storedConfig);
            }
            // Create
            jpaTemplate.merge(commerceConfig);
            jpaTemplate.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error updating Commerce configuration entry",
                    e);
        }
    }

    @Override
    @Nullable
    public CommerceConfigEntity getCommerceConfig() {
        CommerceConfigEntity configurationEntry = null;
        try {
            @SuppressWarnings("unchecked")
            List<CommerceConfigEntity> configList = getJpaTemplate().find(
                    "select c from CommerceConfigEntity c");
            if (configList != null && !configList.isEmpty()) {
                configurationEntry = configList.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Commerce configuration");
        }
        return configurationEntry;
    }

}
