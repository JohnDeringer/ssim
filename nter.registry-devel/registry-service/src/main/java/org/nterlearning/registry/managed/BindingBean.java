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
package org.nterlearning.registry.managed;

import org.nterlearning.registry.model.RegistryModel;

import org.apache.log4j.Logger;

import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.Binding;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.BindingTypeEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.RequestStatus;

import org.primefaces.context.RequestContext;
import org.primefaces.util.MessageFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/17/12
 */
@ManagedBean(name="bindingBean")
@RequestScoped
public class BindingBean implements Serializable {

    private Logger logger = Logger.getLogger(BindingBean.class);

    @ManagedProperty("#{registryModel}")
    private RegistryModel registry;

    @ManagedProperty("#{param['serviceKey']}")
    private Long serviceKey;

    @ManagedProperty("#{param['bindingKey']}")
    private Long bindingKey;
    private String description;
    private String endpoint;
    private String bindingType;

    private Binding binding;

    @PostConstruct
	private void init() {
        Binding binding = getBinding(bindingKey);
        if (binding != null) {
            setDescription(binding.getDescription());
            setBindingType(binding.getBindingType().value());
            setEndpoint(binding.getAccessPoint());
         }
	}

    public RegistryModel getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryModel registry) {
        this.registry = registry;
    }

    public Long getServiceKey() {
        return serviceKey;
    }
    public void setServiceKey(Long serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Long getBindingKey() {
        return bindingKey;
    }
    public void setBindingKey(Long bindingKey) {
        this.bindingKey = bindingKey;
    }

    public String getBindingType() {
        return bindingType;
    }
    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
    }

    public List<BindingTypeEnum> getBindingTypeList() {
        return registry.getBindingTypes();
    }

    public List<String> getBindingTypes() {
        List<String> bindingTypes = new ArrayList<String>();
        for (BindingTypeEnum bindingType : getBindingTypeList()) {
            bindingTypes.add(bindingType.value());
        }
        return bindingTypes;
    }

    public void saveBinding(ActionEvent actionEvent) {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean valid = false;

        FacesContext ctx = FacesContext.getCurrentInstance();
        Locale locale = ctx.getViewRoot().getLocale();
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("messages", locale);

        if (description == null || description.isEmpty()) {
            logger.warn("Binding description is required");

            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                        resourceBundle.getString("forms.binding.add"),
                        resourceBundle.getString("forms.binding.required.description"));

              FacesContext.getCurrentInstance().addMessage(null, msg);
         }

        if (endpoint == null || endpoint.isEmpty()) {
            logger.warn("Binding URL is required");

            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                        resourceBundle.getString("forms.binding.add"),
                        resourceBundle.getString("forms.binding.required.endpoint"));

            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            if (bindingType.equals(BindingTypeEnum.SOA_PV_1_1.value()) ||
                bindingType.equals(BindingTypeEnum.SOA_PV_1_2.value())) {

                if (!endpoint.endsWith("?WSDL") && !endpoint.endsWith("?wsdl")) {
                    msg =
                        new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                                resourceBundle.getString("forms.binding.add"),
                                resourceBundle.getString("forms.binding.required.endpoint.invalid"));

                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }

            }
        }

        if (msg == null) {
            msg = save(locale);
        }

        if (msg == null) {
            valid = true;
            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                        resourceBundle.getString("forms.binding.add"),
                        resourceBundle.getString("forms.binding.required.success"));

                FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        requestContext.addCallbackParam("valid", valid);
    }

    public FacesMessage save(Locale locale) {
        FacesMessage msg = null;
        try {
            logger.info("bindingKey [" + bindingKey + "]");

            if (bindingKey != null && bindingKey != 0) {
                logger.info("Updating binding with key [" + bindingKey + "]");

                Binding storedBinding = getBinding(bindingKey);
                storedBinding.setBindingType(
                        getBindingTypeFromValue(
                                getBindingType()));
                storedBinding.setDescription(getDescription());
                storedBinding.setAccessPoint(getEndpoint());

                RequestStatus requestStatus =
                        registry.updateBinding(storedBinding);
                if (requestStatus != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response updating binding [" +
                            endpoint + "] RequestStatus [" + requestStatus + "]");

                    msg =
                        MessageFactory.getMessage(locale,
                                "forms.binding.required.unexpectedResponse",
                                (new Object[]{description, requestStatus}));

                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                logger.info("Adding binding with endPoint [" +
                        getEndpoint() + "] and type [" + getBindingType() + "]");

                Binding binding = new Binding();
                binding.setBindingType(
                        getBindingTypeFromValue(
                                getBindingType()));
                binding.setDescription(getDescription());
                binding.setAccessPoint(getEndpoint());

                logger.debug("Adding binding with Type [" +
                    binding.getBindingType() + "]");

                RequestStatus requestStatus =
                        registry.addBinding(serviceKey, binding);

                if (requestStatus != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response creating binding [" +
                        endpoint + "] RequestStatus [" + requestStatus + "]");

                    msg =
                        MessageFactory.getMessage(locale,
                                "forms.binding.required.unexpectedResponse",
                                (new Object[]{description, requestStatus}));

                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error updating service", e);

            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    e.getMessage(), e.getMessage());

            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        return msg;
    }

    public void removeBinding(ActionEvent event) {
        try {
            if (bindingKey != null) {
                logger.info("Deleting Binding with key [" + bindingKey + "]");

                RequestStatus requestStatus = registry.removeBinding(bindingKey);

                if (requestStatus != RequestStatus.SUCCESS) {
                    logger.error(
                        "Unexpected response when calling removeBinding [" +
                        requestStatus + "]");
                }
            } else {
                logger.error("Unable to delete binding with 'Null' bindingKey");
            }

        } catch (Exception e) {
            logger.error("Unexpected error attempting to remove binding", e);
        }
    }

    public Binding getBinding(Long bindingKey) {
        if (binding == null) {
            if (bindingKey != null && bindingKey != 0) {
                binding = registry.getBindingByKey(bindingKey);
            } else {
                logger.warn("Unable to retrieve binding with key [" +
                    bindingKey + "]");
            }
        }
        return binding;
    }

    private BindingTypeEnum getBindingTypeFromValue(String bindingTypeValue) {
        BindingTypeEnum bindingTypeEnum = null;
        if (bindingTypeValue != null) {
            try {
                bindingTypeEnum = BindingTypeEnum.fromValue(bindingTypeValue);
            } catch (Exception e) {
                logger.error("Error instantiating BindingTypeEnum using value [" +
                    bindingTypeValue + "]");
            }
        } else {
            logger.error("Error instantiating BindingTypeEnum using value [" +
                    bindingTypeValue + "]");
        }

        return bindingTypeEnum;
    }


}
