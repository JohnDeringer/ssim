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
package org.nterlearning.commerce.managed;

import org.nterlearning.entitlement.client.Action;
import org.nterlearning.entitlement.client.EntitlementPolicy;
import org.nterlearning.entitlement.client.Subject;
import org.nterlearning.registry.client.ActiveStatusEnum;
import org.nterlearning.registry.client.Institution;
import org.nterlearning.registry.client.RegistryImpl;

import org.primefaces.context.RequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/9/12
 */
@ManagedBean(name = "entitlementBean")
public class EntitlementBean implements Serializable {

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{registryService}")
    private RegistryImpl registryService;

    private EntitlementPolicy policy = null;

    @ManagedProperty("#{param['key']}")
    private String key;

    @ManagedProperty("#{param['email']}")
    private String email;

    @ManagedProperty("#{param['resource']}")
    private String resource;

    private String subject;
    private String action;

    private List<Subject> subjects;
    private List<String> resourceNames;
    private List<String> actions;

    private static final String ENTITLEMENTS_PAGE = "/entitlements.xhtml";

    private static Logger logger = LoggerFactory.getLogger(EntitlementBean.class);

    public EntitlementBean() {
        subjects = new ArrayList<Subject>();
        resourceNames = new ArrayList<String>();
        actions = new ArrayList<String>();
    }

    public RegistryImpl getRegistryService() {
        return registryService;
    }
    public void setRegistryService(RegistryImpl registryService) {
        this.registryService = registryService;
    }

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<String> getResourceNames() {
        return resourceNames;
    }
    public void setResourceNames(List<String> resourceNames) {
        this.resourceNames = resourceNames;
    }

    public List<String> getActions() {
        return actions;
    }
    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    @PostConstruct
    private void init() {
        EntitlementPolicy policy = getPolicy();
        if (policy != null) {
            logger.info(
                "Loading policy subject [" + policy.getSubject() +
                        "] resource [" + policy.getResource() +
                        "] action [" + policy.getAction() +
                        "] realm [" + policy.getRealm() + "]");

            setSubject(policy.getSubject());
            setResource(policy.getResource());
            setAction(policy.getAction().value());
        }

        // Subjects/User
        if (subjects.isEmpty()) {
            List<Subject> subjectList = entitlementUtil.getSubjects();
            for (Subject subject : subjectList) {
                if (subject.getEmail() != null && !subject.getEmail().isEmpty()) {
                    subjects.add(subject);
                }
            }

            // Sort the collection by email
            Collections.sort(subjects, new BeanUtil.SubjectComparator());
        }

        // Resources/Institutions
        resourceNames = getInstitutions();
        logger.info("Retrieved [" + resourceNames.size() +
                "] institutions from registry");

        // Actions/Permissions
        if (actions.isEmpty()) {
            // Filter actions using logged-in users permission level
            Action usersPermission =
                    entitlementUtil.getPermission();
            List<Action> userActions =
                    entitlementUtil.getActions(usersPermission);
            for (Action action : userActions) {
                actions.add(action.value());
            }
        }

    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public boolean getAdmin() {
        return entitlementUtil.isAdmin();
    }

    public List<String> getInstitutions() {
        List<String> institutionNames = new ArrayList<String>();

        if (entitlementUtil.isGlobalAdmin()) {
            // Get active institutions
            List<Institution> institutions =
                    registryService.getInstitutions(ActiveStatusEnum.ACTIVE);
            // Add inactive institutions
            institutions.addAll(registryService.getInstitutions(ActiveStatusEnum.INACTIVE));
            for (Institution institution : institutions) {
                institutionNames.add(BeanUtil.escape(institution.getName()));
            }
            logger.debug("Found [" + institutionNames.size() + "] institutions");
        } else {
            List<EntitlementPolicy> policies =
                    entitlementUtil.getPoliciesBySubject();
            for (EntitlementPolicy policy : policies) {
                if (policy.getAction() == Action.ADMIN ||
                        policy.getAction() == Action.GLOBAL_ADMIN) {
                    institutionNames.add(policy.getResource());
                }
            }
        }

        return institutionNames;
    }

    public EntitlementPolicy getPolicy() {
        String key = getKey();
        Long entitlementKey;

        if (key != null && !key.isEmpty()) {
            try {
                entitlementKey = Long.parseLong(key);
                policy = entitlementUtil.getPolicy(entitlementKey);
            } catch (NumberFormatException e) {
                logger.error("Error attempting to retrieve policy using key [" +
                    key + "]");
            }
        }

        return policy;
    }

    public void save(ActionEvent event) {

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg;
        boolean valid = false;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        Locale locale = facesContext.getViewRoot().getLocale();
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle("messages", locale);

        String subject = getSubject();
        String resource = BeanUtil.unescape(getResource());
        String action = getAction();

        logger.info("Saving policy subject [" + subject +
                "] resource [" + resource +
                "] action [" + action + "]");

        EntitlementPolicy policy = getPolicy();

        try {
            if (entitlementUtil != null) {
                if (policy != null) {
                    entitlementUtil.updatePolicy(subject, resource, action);
                } else {
                    entitlementUtil.createPolicy(subject, resource, action);
                }
                // Clear session cache
                BeanUtil.setAttributeInSession(
                    Policy.class.getCanonicalName(), null);
            } else {
                logger.warn(
                    "Unable to retrieve entitlement policy, invalid reference to entitlement [" +
                        entitlementUtil + "]");
            }
            valid = true;
        } catch (Exception e) {
            logger.error("Unexpected exception saving entitlement", e);

            msg =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                        resourceBundle.getString("forms.entitlement.unexpectedException"),
                        e.getLocalizedMessage());

            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        context.addCallbackParam("valid", valid);
    }

    public void remove() {
        String key = getKey();
        Long entitlementKey = null;

        if (key != null && !key.isEmpty()) {
            try {
                entitlementKey = Long.parseLong(key);
            } catch (NumberFormatException e) {
                logger.error("Error attempting to parseLong [" +
                    key + "]");
            }
        }

        if (entitlementKey != null) {
            if (entitlementUtil != null) {
                entitlementUtil.removePolicy(entitlementKey);
            } else {
                logger.warn(
                    "Unable to retrieve entitlement policy, invalid reference to entitlement [" +
                        entitlementUtil + "]");
            }
        } else {
            logger.error("Unable to remove entitlement using key [" +
                    entitlementKey + "]");
        }

        // Forward to: entitlements.xhtml
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String contextPath = facesContext.getExternalContext().getRequestContextPath();
            String redirectURL = contextPath + ENTITLEMENTS_PAGE;

            facesContext.getExternalContext().redirect(redirectURL);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

}
