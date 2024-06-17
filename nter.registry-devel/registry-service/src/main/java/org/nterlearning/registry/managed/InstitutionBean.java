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

import org.nterlearning.entitlement.client.Action;
import org.nterlearning.entitlement.client.EntitlementPolicy;
import org.nterlearning.registry.model.RegistryModel;

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.BusinessService;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.Contact;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.Institution;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.RequestStatus;

import org.primefaces.context.RequestContext;
import org.primefaces.util.MessageFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/18/12
 */
@ManagedBean(name = "institutionBean")
public class InstitutionBean implements Serializable {

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{registryModel}")
    private RegistryModel registry;

    private Institution institution;

    @ManagedProperty("#{param['institutionName']}")
    private String institutionName;

    private Long institutionKey;

    private String description;
    private String contactName;
    private String contactDescription;
    private String contactAddress;
    private String contactPhone;
    private String contactEmail;
    private String activeStatus;

    private Logger logger = Logger.getLogger(InstitutionBean.class);

    @PostConstruct
    private void init() {
        Institution institution = getInstitution();
        if (institution != null) {
            logger.info(
                    "Loading institution [" +
                        institutionName + "]");
            setInstitutionKey(institution.getKey());
            setDescription(institution.getDescription());
            setActiveStatus(institution.getActiveStatus().value());

            Contact contact = institution.getContactInfo();
            if (contact != null) {
                setContactName(contact.getPersonName());
                setContactDescription(contact.getDescription());
                setContactAddress(contact.getAddress());
                setContactPhone(contact.getPhone());
                setContactEmail(contact.getEmail());
            }
        } else {
            String fullName = entitlementUtil.getFullName();
            if (fullName != null) {
                setContactName(fullName);
            }

            String email = entitlementUtil.getEmail();
            if (email != null) {
                setContactEmail(email);
            }
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

    public String getInstitutionName() {
        if (institutionName != null) {
            return BeanUtil.unescape(institutionName);
        } else {
            return null;
        }
	}
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
		this.description = description;
	}

    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
		this.contactName = contactName;
	}

    public String getContactDescription() {
        return contactDescription;
    }
    public void setContactDescription(String contactDescription) {
		this.contactDescription = contactDescription;
	}

    public String getContactAddress() {
        return contactAddress;
    }
    public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

    public String getContactPhone() {
        return contactPhone;
    }
    public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

    public String getContactEmail() {
        return contactEmail;
    }
    public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
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

    public List<String> getActiveStatusList() {
        List<String> statusTypes = new ArrayList<String>();
        for (ActiveStatusEnum activeStatusEnum: ActiveStatusEnum.values()) {
            if (activeStatusEnum != ActiveStatusEnum.UNSPECIFIED) {
                statusTypes.add(activeStatusEnum.value());
            }
        }
        return statusTypes;
    }

    public void saveInstitution(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean valid = false;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        Locale locale = facesContext.getViewRoot().getLocale();
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("messages", locale);

        String institutionName = getInstitutionName();

        if (institutionName == null || institutionName.isEmpty()) {
            logger.warn("Institution name is required");

            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                        resourceBundle.getString("forms.institutions.add"),
                        resourceBundle.getString("forms.institution.required.name"));

            facesContext.addMessage(null, msg);
        }

        if (getContactName() == null || getContactName().isEmpty()) {
            logger.warn("Contact name is required");

            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                        resourceBundle.getString("forms.institutions.add"),
                        resourceBundle.getString("forms.institution.required.contactName"));

            facesContext.addMessage(null, msg);
        }

        if (getContactEmail() == null || getContactEmail().isEmpty()) {
            logger.warn("Contact email is required");

            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                        resourceBundle.getString("forms.institutions.add"),
                        resourceBundle.getString("forms.institution.required.contactEmail"));

            facesContext.addMessage(null, msg);
        } else {
            Pattern p =
                    Pattern.compile(
                            resourceBundle.getString("forms.email.regex")
                    );
            Matcher m = p.matcher(getContactEmail());
            boolean match = m.matches();

            if (!match) {
                logger.warn("Contact email is invalid [" + getContactEmail() + "]");
                msg =
                    new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                            resourceBundle.getString("forms.institutions.add"),
                            resourceBundle.getString("forms.email.invalid"));

                facesContext.addMessage(null, msg);
            }
        }

        if (msg == null) {
            msg = save(locale);
        }

        // Success
        if (msg == null) {
            valid = true;
            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                        resourceBundle.getString("forms.institutions.add"),
                        resourceBundle.getString("forms.institution.required.success"));

            facesContext.addMessage(null, msg);
        }

        context.addCallbackParam("valid", valid);
    }

    private FacesMessage save(Locale locale) {
        FacesMessage msg = null;
        try {
            Institution institution = new Institution();

            String institutionName = getInstitutionName();

            institution.setName(institutionName);
            institution.setDescription(getDescription());

            Contact contact = new Contact();
            contact.setPersonName(getContactName());
            contact.setDescription(getContactDescription());
            contact.setPhone(getContactPhone());
            contact.setEmail(getContactEmail());
            contact.setAddress(getContactAddress());

            institution.setContactInfo(contact);
            institution.setActiveStatus(getActiveStatus(activeStatus));

            if (institutionKey != null && institutionKey != 0) {
                logger.info("Updating institution with key [" + institutionKey + "]");

                institution.setKey(institutionKey);

                if (entitlementUtil.hasWriteAccess(institution.getName())) {
                    RequestStatus requestStatus =
                            registry.updateInstitution(institution);
                    if (requestStatus != RequestStatus.SUCCESS) {
                        logger.error("Unexpected response updating institution [" +
                            institutionName + "] RequestStatus [" + requestStatus + "]");

                        msg =
                            MessageFactory.getMessage(locale,
                                    "forms.institution.required.unexpectedResponse",
                                    (new Object[]{institutionName, requestStatus}));

                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    }
                } else {
                    logger.info("Unable to update institution [" + institution.getName() +
                        "] User does not have write permission");
                }
            } else {
                logger.info("Creating institution with name [" + institutionName + "]");

                RequestStatus requestStatus =
                        registry.createInstitution(institution);
                if (requestStatus == RequestStatus.SUCCESS) {
                    // Create a registry entitlement for this user and institution
                    try {
                        entitlementUtil.createPolicy(
                                institutionName, Action.ADMIN.value());
                    } catch (Exception e) {
                        logger.error(e);
                    }

                    // Create a commerce entitlement for this user and institution
                    try {
                        entitlementUtil.createCommercePolicy(
                                institutionName, Action.ADMIN.value());
                    } catch (Exception e) {
                        logger.error(e);
                    }
                } else {
                    logger.error("Unexpected response creating institution [" +
                        institutionName + "] RequestStatus [" + requestStatus + "]");

                    msg =
                        MessageFactory.getMessage(locale,
                                "forms.institution.required.unexpectedResponse",
                                (new Object[]{institutionName, requestStatus}));

                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error updating institution [" +
                    institutionName + "]", e);

                msg =
                    MessageFactory.getMessage(locale,
                            "forms.institution.required.unexpected",
                            (new Object[]{institutionName}));

            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return msg;
    }

    public Institution getInstitution() {
        String institutionName = getInstitutionName();
        logger.debug(
                    "Looking for institution [" +
                        institutionName + "]");
        if (institution == null && institutionName != null) {
            institution = registry.getInstitutionByName(institutionName);
        }

        return institution;
    }

    public List<BusinessService> getServices() {
        List<BusinessService> services = new ArrayList<BusinessService>();
        Institution institution = getInstitution();
        if (institution != null) {
            services = institution.getService();
        }

        return services;
    }

    public void removeInstitution(ActionEvent actionEvent) {
        RequestStatus requestStatus = RequestStatus.FAILURE;
        FacesMessage msg;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Locale locale = facesContext.getViewRoot().getLocale();
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("messages", locale);

        String institutionName = getInstitutionName();
        try {
            Institution institution = getInstitution();
            if (institution != null) {
                if (entitlementUtil.hasWriteAccess(institution.getName())) {
                    requestStatus =
                            registry.removeInstitution(institution.getKey());
                    if (requestStatus != RequestStatus.SUCCESS) {
                        logger.error(
                            "Unexpected response when calling removeInstitution [" +
                            requestStatus + "]");

                        msg =
                            MessageFactory.getMessage(locale,
                                    "forms.institution.required.unexpectedResponse",
                                    (new Object[]{institutionName, requestStatus}));

                        facesContext.addMessage(null, msg);
                    }
                } else {
                    logger.info("Unable to remove institution [" +
                            institution.getName() +
                        "] User does not have permission");
                }
            } else {
                logger.error("Unable to remove institution [" +
                        institutionName + "] because it could not be found");
                msg =
                    MessageFactory.getMessage(locale,
                        "forms.institution.delete.notFound",
                            (new Object[]{institutionName}));

                facesContext.addMessage(null, msg);
            }
        } catch (Exception e) {
            logger.error("Unexpected error attempting to remove institution", e);
            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                        resourceBundle.getString("forms.institution.delete"),
                        resourceBundle.getString("forms.institution.delete.unexpected"));

            facesContext.addMessage(null, msg);
        }

        if (requestStatus == RequestStatus.SUCCESS) {
            logger.info("Successfully removed institution [" + institutionName + "]");
            // Remove entitlements associated with this institution
            removeEntitlements(institutionName);

            // Forward to institutions.xhtml
            try {
                logger.debug("Forwarding to /institutions.xhtml");
                String contextPath = facesContext.getExternalContext().getRequestContextPath();
                String redirectURL = contextPath + "/institutions.xhtml";

                facesContext.getExternalContext().redirect(redirectURL);
            } catch (Exception e) {
                logger.error(e);
            }
        } else {
            logger.error("Received unexpected response from removeInstitution [" +
                requestStatus + "]");
        }
	}

    public String escape(String value) {
        return BeanUtil.escape(value);
    }

    public boolean getGlobalAdmin() {
        return entitlementUtil.isGlobalAdmin();
    }

    public boolean getAdmin() {
        return entitlementUtil.isAdmin();
    }

    public boolean getAdmin(String resource) {
        return entitlementUtil.hasAdminAccess(resource);
    }

    public boolean getEditable() {
        return entitlementUtil.hasWriteAccess(institutionName);
    }

    private void removeEntitlements(String institutionName) {
        // Remove registry entitlements
        List<EntitlementPolicy> policies =
                entitlementUtil.getPoliciesByResource(institutionName);
        for (EntitlementPolicy policy : policies) {
            logger.info("Removing entitlement policy with key [" + policy.getKey() + "]");
            entitlementUtil.removePolicy(policy.getKey());
        }
        // Remove commerce entitlements
        policies =
                entitlementUtil.getCommercePoliciesByResource(institutionName);
        for (EntitlementPolicy policy : policies) {
            logger.info("Removing entitlement policy with key [" + policy.getKey() + "]");
            entitlementUtil.removePolicy(policy.getKey());
        }
    }

}
