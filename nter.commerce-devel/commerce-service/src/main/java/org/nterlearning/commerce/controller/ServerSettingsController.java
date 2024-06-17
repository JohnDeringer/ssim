package org.nterlearning.commerce.controller;

import org.nterlearning.commerce.model.ConfigurationModel;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.CommerceConfig;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author bblonski
 */
@ManagedBean(name = "serverSettings")
public class ServerSettingsController implements Serializable{

	@ManagedProperty("#{configurationModel}")
	private ConfigurationModel configurationAPI;

	private BigDecimal adminFee;
	private BigDecimal referralFee;

	public ConfigurationModel getConfigurationAPI() {
		return configurationAPI;
	}

	public void setConfigurationAPI(ConfigurationModel configurationAPI) {
		this.configurationAPI = configurationAPI;
		CommerceConfig config = configurationAPI.getCommerceConfig();
		if(config != null){
			referralFee = configurationAPI.getCommerceConfig().getReferrerFee();
			adminFee = configurationAPI.getCommerceConfig().getAdminFee();
		}
	}

	public BigDecimal getAdminFee() {
		return adminFee;
	}

	public void setAdminFee(BigDecimal adminFee) {
		this.adminFee = adminFee;
	}

	public BigDecimal getReferralFee() {
		return referralFee;
	}

	public void setReferralFee(BigDecimal referralFee) {
		this.referralFee = referralFee;
	}

	public void save() {
		CommerceConfig config = getConfigurationAPI().getCommerceConfig();
		if (config != null) {
			config.setAdminFee(getAdminFee());
			config.setReferrerFee(getReferralFee());
		} else {
			config = new CommerceConfig();
			config.setAdminFee(getAdminFee());
			config.setReferrerFee(getReferralFee());
		}
        // TODO: entitlement
		getConfigurationAPI().createOrUpdateCommerceConfig(config);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(facesContext.getApplication().
				getResourceBundle(facesContext, "i18n").getString("forms.saved")));
	}
}
