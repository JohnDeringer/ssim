/**
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
package org.nterlearning.commerce.form;

import org.nterlearning.commerce.managed.EntitlementUtil;
import org.nterlearning.commerce.model.ConfigurationModel;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Controls the logic for adding and updating payment processors.
 *
 * @author Brian Blonski
 */
@ManagedBean(name = "paymentProcessorForm")
public class PaymentProcessorForm implements Serializable {

	//** Private Variables **//

	@ManagedProperty("#{configurationModel}")
	private ConfigurationModel configurationAPI;

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

	private PaymentProcessor selectedProcessor;
	private String accountId;
	private String password;
	private String passwordConfirm;
	private String actionURL;
	private String notifyURL;
	private String buttonURL;
    private String apiUsername;
    private String apiPassword;
    private String apiSignature;
	private String apiVersion;

    private Logger logger = LoggerFactory.getLogger(PaymentProcessorForm.class);

	//** Getters & Setters **//

	public PaymentProcessor getSelectedProcessor() {
		return selectedProcessor;
	}

	public void setSelectedProcessor(PaymentProcessor selectedProcessor) {
		this.selectedProcessor = selectedProcessor;
		PaymentConfig paymentConfig =
                configurationAPI.getPaymentConfig(selectedProcessor);
		if(paymentConfig != null) {
			setAccountId(paymentConfig.getSellerId());
            setPassword(paymentConfig.getSellerPassword());
            setPasswordConfirm(paymentConfig.getSellerPassword());
			setActionURL(paymentConfig.getActionURL());
			setNotifyURL(paymentConfig.getNotifyURL());
			setButtonURL(paymentConfig.getButtonURL());

			setApiUsername(paymentConfig.getApiUsername());
            setApiPassword(paymentConfig.getApiPassword());
            setApiSignature(paymentConfig.getApiSignature());
            setApiVersion(paymentConfig.getApiVersion());
		}
	}

	public ConfigurationModel getConfigurationAPI() {
		return configurationAPI;
	}
	public void setConfigurationAPI(ConfigurationModel configurationAPI) {
		this.configurationAPI = configurationAPI;
	}

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    public List<PaymentProcessor> getProcessorNames() {
		return Arrays.asList(PaymentProcessor.values());
	}

	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getActionURL() {
		return actionURL;
	}
	public void setActionURL(String actionURL) {
		this.actionURL = actionURL;
	}

	public String getNotifyURL() {
		return notifyURL;
	}
	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}

	public String getButtonURL() {
		return buttonURL;
	}
	public void setButtonURL(String buttonURL) {
		this.buttonURL = buttonURL;
	}

    public String getApiUsername() {
		return apiUsername;
	}
    public void setApiUsername(String apiUsername) {
		this.apiUsername = apiUsername;
	}

    public String getApiPassword() {
		return apiPassword;
	}
    public void setApiPassword(String apiPassword) {
		this.apiPassword = apiPassword;
	}

    public String getApiSignature() {
		return apiSignature;
	}
    public void setApiSignature(String apiSignature) {
		this.apiSignature = apiSignature;
	}

	public String getApiVersion() {
		return apiVersion;
	}
    public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	//** Actions**//

	public void save() {

		boolean isNew = false;
		PaymentConfig config = configurationAPI.getPaymentConfig(getSelectedProcessor());
		if (config == null) {
			config = new PaymentConfig();
			isNew = true;
		}

        config.setConfigId(getSelectedProcessor());
		config.setSellerId(getAccountId());
		config.setSellerPassword(getPassword());
		config.setActionURL(getActionURL());
		config.setNotifyURL(getNotifyURL());
		config.setButtonURL(getButtonURL());

        config.setApiUsername(getApiUsername());
        config.setApiPassword(getApiPassword());
        config.setApiSignature(getApiSignature());
        config.setApiVersion(getApiVersion());

        if (entitlementUtil.isGlobalAdmin()) {
            if (isNew) {
                config.setActiveStatus(true);
                configurationAPI.createPaymentConfig(config);
            } else {
                configurationAPI.updatePaymentConfig(config);
            }
        } else {
            logger.warn("User [" + entitlementUtil.getEmail() +
                    "] does not hav authorization to call this operation");
        }
	}
}
