package org.nterlearning.commerce.controller;

import org.nterlearning.commerce.form.PaymentProcessorForm;
import org.nterlearning.commerce.managed.EntitlementUtil;
import org.nterlearning.commerce.model.ConfigurationModelImpl;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls the logic for displaying payment processors.
 *
 * @author Brian Blonski
 */
@ManagedBean(name = "paymentProcessors")
public class PaymentProcessorsController implements Serializable {

	//** Private Variables **//

	private List<PaymentProcessorDetailsController> processors;

	@ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;
	@ManagedProperty("#{configurationModel}")
	private ConfigurationModelImpl configurationAPI;
    @ManagedProperty("#{paymentProcessorForm}")
	private PaymentProcessorForm form;

	private boolean formVisible;


	//** Constructors **//

	public PaymentProcessorsController() {
		processors = new ArrayList<PaymentProcessorDetailsController>();
	}

	//** Data Initialization **//

	@PostConstruct
	private void initProcessors() {
		List<PaymentConfig> configurations =
				configurationAPI.getPaymentConfigs();
		processors.clear();
		for (PaymentConfig paymentConfig : configurations) {
			PaymentProcessorDetailsController temp = new PaymentProcessorDetailsController();
			temp.setName(paymentConfig.getConfigId().value());
			temp.setAccountNumber(paymentConfig.getSellerId());
			temp.setStatus(paymentConfig.isActiveStatus());
			temp.setProcessor(paymentConfig.getConfigId());
			processors.add(temp);
		}
		for (PaymentProcessor processor : PaymentProcessor.values()) {
			boolean found = false;
			for(PaymentProcessorDetailsController temp : processors) {
				if(temp.getProcessor().equals(processor)) {
					found = true;
					break;
				}
			}
			if(!found) {
				PaymentProcessorDetailsController temp = new PaymentProcessorDetailsController();
				temp.setName(processor.value());
				temp.setProcessor(processor);
				processors.add(temp);
			}
		}
	}

	//** Getters & Setters **//

	public PaymentProcessorForm getForm() {
		return form;
	}

	public void setForm(PaymentProcessorForm form) {
		this.form = form;
	}

	public boolean isFormVisible() {
		return formVisible;
	}

	public void setFormVisible(boolean formVisible) {
		this.formVisible = formVisible;
	}

	public List<PaymentProcessorDetailsController> getProcessors() {
		initProcessors();
		return processors;
	}

	public void setProcessors(List<PaymentProcessorDetailsController> processors) {
		this.processors = processors;
	}

	public ConfigurationModelImpl getConfigurationAPI() {
		return configurationAPI;
	}

	public void setConfigurationAPI(ConfigurationModelImpl configurationAPI) {
		this.configurationAPI = configurationAPI;
	}

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }

    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    //** Actions **//

	public void showForm() {
		String selectedProcessor = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedProcessor");
		form.setSelectedProcessor(PaymentProcessor.fromValue(selectedProcessor));
		setFormVisible(true);
	}

	public void hideForm() {
		setFormVisible(false);
	}

	public void toggleStatus() {
		String selectedProcessor = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedProcessor");
		PaymentConfig processor =
                configurationAPI.getPaymentConfig(PaymentProcessor.fromValue(selectedProcessor));
		processor.setActiveStatus(!processor.isActiveStatus());
        // TODO: entitlement
		configurationAPI.updatePaymentConfig(processor);
	}

    public boolean isGlobalAdmin() {
        return entitlementUtil.isGlobalAdmin();
    }
}
