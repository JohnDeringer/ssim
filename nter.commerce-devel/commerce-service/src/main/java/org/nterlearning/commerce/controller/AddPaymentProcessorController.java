package org.nterlearning.commerce.controller;

import org.nterlearning.commerce.managed.EntitlementUtil;
import org.nterlearning.commerce.model.ConfigurationModelImpl;

import org.nterlearning.commerce.form.AddPaymentProcessorForm;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import javax.faces.bean.ManagedProperty;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContext;

import java.io.Serializable;
import java.util.List;
import java.util.Arrays;

/**
 * User: Deringer
 * Date: 8/10/11
 * Time: 11:56 AM
 */
@Controller
@RequestMapping("add-payment-processor.html")
public class AddPaymentProcessorController implements Serializable {

    @Autowired
    private ConfigurationModelImpl configurationAPI;

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String getProcessors(
            @RequestParam(value="processorId", required=false)
                PaymentProcessor processorId,
            HttpServletRequest request,
            Model model) {

		boolean isGlobalAdmin = entitlementUtil.isGlobalAdmin();
		boolean hasMultipleOrgs = false;
		if (!isGlobalAdmin) {
			hasMultipleOrgs = (entitlementUtil.getPoliciesBySubject().size() > 1);
		}

        AddPaymentProcessorForm form = new AddPaymentProcessorForm();

        model.addAttribute("addPaymentProcessorForm", form);

		List<PaymentProcessor> processors = Arrays.asList(PaymentProcessor.values());
		form.setNames(processors);

		if (processorId != null) {
			PaymentConfig processor = configurationAPI.getPaymentConfig(processorId);
			if (processor != null) {
				model.addAttribute("processor", processor);
				form.setName(processor.getConfigId());
				form.setAccountId(processor.getSellerId());
				form.setPassword(processor.getSellerPassword());
				form.setActionUrl(processor.getActionURL());
				form.setNotifyUrl(processor.getNotifyURL());
				form.setButtonUrl(processor.getButtonURL());
				form.setApiVersion(processor.getApiVersion());
			} else {
				form.setName(processorId);
			}
		}

		String pageTitle = new RequestContext(request).getMessage("title.addPaymentProcessor");

		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("currentPage", "add-payment-processor");
		model.addAttribute("isGlobalAdmin", isGlobalAdmin);
		model.addAttribute("hasMultipleOrgs", hasMultipleOrgs);

        return "add-payment-processor";
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public String saveProcessor(
            @ModelAttribute("addPaymentProcessorForm")
            AddPaymentProcessorForm form, BindingResult result) {

		PaymentProcessor name = form.getName();
        String accountId = form.getAccountId();
        String password = form.getPassword();
        String actionUrl = form.getActionUrl();
        String notifyUrl = form.getNotifyUrl();
        String buttonUrl = form.getButtonUrl();
        String apiVersion = form.getApiVersion();

		boolean isNew = false;
		PaymentConfig config = configurationAPI.getPaymentConfig(name);
		if (config == null) {
			config = new PaymentConfig();
			isNew = true;
		}
		config.setConfigId(name);
		config.setSellerId(accountId);
		config.setSellerPassword(password);
		config.setActionURL(actionUrl);
		config.setNotifyURL(notifyUrl);
		config.setButtonURL(buttonUrl);
		config.setApiVersion(apiVersion);

		if (isNew) {
			config.setActiveStatus(true);
			configurationAPI.createPaymentConfig(config);
		} else {
			configurationAPI.updatePaymentConfig(config);
		}

        return "redirect:payment-processors.html";
    }

}
