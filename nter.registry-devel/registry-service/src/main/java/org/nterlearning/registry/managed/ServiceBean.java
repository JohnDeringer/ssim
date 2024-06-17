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

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.*;

import org.primefaces.context.RequestContext;
import org.primefaces.util.MessageFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/18/12
 */
@ManagedBean(name = "serviceBean")
public class ServiceBean implements Serializable {

    private Logger logger = Logger.getLogger(ServiceBean.class);

    @ManagedProperty("#{entitlementUtil}")
	private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{registryModel}")
    private RegistryModel registry;

    private BusinessService service = null;

    @ManagedProperty("#{param['institutionKey']}")
    private Long institutionKey;

    @ManagedProperty("#{param['institutionName']}")
    private String institutionName;

    @ManagedProperty("#{param['serviceKey']}")
    private Long serviceKey;

    private String serviceName;
    private String description;
    private String serviceType;
    private String activeStatus;
    private List<Binding> bindings;

    @PostConstruct
    private void init() {
        BusinessService service = getService();
        if (service != null) {
            logger.info(
                    "Loading service [" + serviceName + "]");

            setServiceName(service.getName());
            setDescription(service.getDescription());
            setServiceType(service.getServiceType().value());
            setActiveStatus(service.getActiveStatus().value());

            setBindings(service.getBinding());

            setInstitutionName(service.getInstitutionName());
        }
    }

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    public RegistryModel getRegistry() {
        return registry;
    }
    public void setRegistry(RegistryModel registry) {
        this.registry = registry;
    }

    public Long getInstitutionKey() {
        return institutionKey;
    }
    public void setInstitutionKey(Long institutionKey) {
        this.institutionKey = institutionKey;
    }

    public Long getServiceKey() {
        return serviceKey;
    }
    public void setServiceKey(Long serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceType() {
        return serviceType;
    }
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public ActiveStatusEnum getActiveStatus(String value) {
        ActiveStatusEnum activeStatusEnum = ActiveStatusEnum.INACTIVE;
        if (value != null) {
            activeStatusEnum = ActiveStatusEnum.fromValue(value);
        }
        return activeStatusEnum;
    }
    public String getActiveStatus() {
        return activeStatus;
    }
    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getInstitutionName() {
        return institutionName;
    }
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public List<Binding> getBindings() {
        return bindings;
    }
    public void setBindings(List<Binding> bindings) {
        this.bindings = bindings;
    }

    public List<String> getActiveStatusList() {
        List<String> statusTypes = new ArrayList<String>();
        for (ActiveStatusEnum activeStatusEnum : ActiveStatusEnum.values()) {
            if (activeStatusEnum != ActiveStatusEnum.UNSPECIFIED) {
                statusTypes.add(activeStatusEnum.value());
            }
        }
        return statusTypes;
    }

    public List<String> getServiceTypeList() {
        List<String> serviceTypes = new ArrayList<String>();
        for (ServiceTypeEnum serviceTypeEnum : ServiceTypeEnum.values()) {
            serviceTypes.add(serviceTypeEnum.value());
        }
        return serviceTypes;
    }

    public void saveService(ActionEvent actionEvent) {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Locale locale = facesContext.getViewRoot().getLocale();
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("messages", locale);
        FacesMessage msg = null;
        boolean valid = false;

        if (serviceName == null || serviceName.isEmpty()) {
            logger.warn("Service name is required");

            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                        resourceBundle.getString("forms.service.add"),
                        resourceBundle.getString("forms.service.required.name"));

              FacesContext.getCurrentInstance().addMessage(null, msg);
         }

        if (msg == null) {
            msg = save(resourceBundle, locale);
        }

        if (msg == null) {
            valid = true;
            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                        resourceBundle.getString("forms.service.add"),
                        resourceBundle.getString("forms.service.required.success"));

            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        requestContext.addCallbackParam("valid", valid);
    }

    public FacesMessage save(ResourceBundle resourceBundle, Locale locale) {
        FacesMessage msg = null;
        try {
            logger.info("serviceKey [" + serviceKey + "]");

            String _serviceName = BeanUtil.unescape(serviceName);
            String _institutionName = BeanUtil.unescape(institutionName);

            if (serviceKey != null && serviceKey != 0) {
                logger.info("Updating service with service key [" +
                        serviceKey + "] and institution name [" +
                        _institutionName + "]");

                BusinessService service = getService();
                service.setName(_serviceName);
                service.setDescription(description);
                // ServiceType
                ServiceTypeEnum serviceTypeEnum =
                        ServiceTypeEnum.fromValue(getServiceType());
                service.setServiceType(serviceTypeEnum);
                service.setActiveStatus(getActiveStatus(activeStatus));
                service.setInstitutionName(_institutionName);

                if (entitlementUtil.hasWriteAccess(
                        service.getInstitutionName())) {
                    RequestStatus requestStatus =
                            registry.updateService(service);
                    if (requestStatus != RequestStatus.SUCCESS) {
                        logger.error("Unexpected response updating service [" +
                            _serviceName + "] RequestStatus [" + requestStatus + "]");

                        msg =
                            MessageFactory.getMessage(locale,
                                    "forms.service.required.unexpectedResponse",
                                    (new Object[]{_institutionName, requestStatus}));

                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    }
                } else {
                    logger.info("Unable to update service [" + service.getName() +
                        "] User does not have write permission");
                }
            } else {
                logger.info("Adding service with name [" + _serviceName +
                        "] to institution name [" + _institutionName +
                        " and institution key [" + institutionKey + "]");

                BusinessService service = new BusinessService();
                service.setName(_serviceName);
                service.setDescription(description);
                // ServiceType
                ServiceTypeEnum serviceTypeEnum =
                        ServiceTypeEnum.fromValue(getServiceType());
                service.setServiceType(serviceTypeEnum);
                service.setActiveStatus(getActiveStatus(activeStatus));
                service.setInstitutionName(_institutionName);

                if (entitlementUtil.hasWriteAccess(
                        service.getInstitutionName())) {
                    RequestStatus requestStatus =
                            registry.addService(institutionKey, service);
                    if (requestStatus != RequestStatus.SUCCESS) {
                        logger.error("Unexpected response creating service [" +
                            _serviceName + "] RequestStatus [" + requestStatus + "]");

                        msg =
                            MessageFactory.getMessage(locale,
                                    "forms.service.required.unexpectedResponse",
                                    (new Object[]{_institutionName, requestStatus}));

                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    }
                } else {
                    logger.info("Unable to add service [" + service.getName() +
                        "] User does not have write permission");
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error updating service [" +
                    serviceName + "]", e);
            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    resourceBundle.getString("forms.service.add"),
                        e.getMessage());

            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return msg;
    }

    public BusinessService getService() {
        if (service == null) {
            if (serviceKey != null) {
                service = registry.getServiceByKey(serviceKey);
            } else {
                logger.warn("Attempting to retrieve service with serviceKey [" +
                    serviceKey + "]");
            }
        }
        return service;
    }

    public void removeService() {
        RequestStatus requestStatus;
        BusinessService service = getService();

        if (service != null) {
            try {
                if (entitlementUtil.hasWriteAccess(
                        service.getInstitutionName())) {
                    requestStatus =
                            registry.removeService(service.getKey());
                    // Forward to services.xhtml
                    if (requestStatus == RequestStatus.SUCCESS) {
                        try {
                            String contextPath =
                                    FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
                            String redirectURL = contextPath + "/services.xhtml";

                            FacesContext.getCurrentInstance().getExternalContext().redirect(redirectURL);
                        } catch (Exception e) {
                            logger.error(e);
                        }
                    } else {
                        logger.error(
                            "Unexpected response when calling removeService [" +
                            requestStatus + "]");
                    }
                } else {
                    logger.info("Unable to remove service [" + service.getName() +
                        "] User does not have write permission");
                }
            } catch (Exception e) {
                logger.error("Unexpected error removing service", e);
            }
        } else {
            logger.error("Unable to remove service [" +
                    serviceName + "] because it could not be found");
        }
	}

    public String getBindingTypeValue(BindingTypeEnum bindingType) {
        String value = null;
        if (bindingType != null) {
            value = bindingType.value();
        }

        return value;
    }

    public boolean getAdmin() {
        return entitlementUtil.isAdmin();
    }

    public boolean getAdmin(String resource) {
        return entitlementUtil.hasAdminAccess(resource);
    }

    public boolean getEditable(String resource) {
        return entitlementUtil.hasWriteAccess(resource);
    }

}
