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
import org.nterlearning.entitlement.client.Subject;

import org.apache.log4j.Logger;

import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.Serializable;
import java.util.*;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 2/24/12
 */
@ManagedBean(name = "entitlementBean")
@RequestScoped
public class EntitlementBean implements Serializable {

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{param['key']}")
    private String key;

    @ManagedProperty("#{param['email']}")
    private String email;

    @ManagedProperty("#{param['resource']}")
    private String resource;

    private EntitlementPolicy policy = null;

    private String subject;
    private String action;

    private List<Subject> subjects;
    private List<String> resourceNames;
    private List<String> actions;

    private static final String ENTITLEMENTS_PAGE = "/entitlements.xhtml";

    private static Logger logger = Logger.getLogger(EntitlementBean.class);

    public EntitlementBean() {
        subjects = new ArrayList<Subject>();
        resourceNames = new ArrayList<String>();
        actions = new ArrayList<String>();
    }

//    public AuthController getAuthController() {
//        return authController;
//    }
//    public void setAuthController(AuthController authController) {
//        this.authController = authController;
//    }
//
//    public RegistryModel getRegistry() {
//        return registry;
//    }
//    public void setRegistry(RegistryModel registry) {
//        this.registry = registry;
//    }

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
                    "Loading policy [" + policy.getSubject() + " / " +
                            policy.getResource() + "]");

            setSubject(policy.getSubject());
            setResource(policy.getResource());
            setAction(policy.getAction().value());
        }

        // Subjects/User
        if (subjects == null || subjects.isEmpty()) {
            subjects = entitlementUtil.getSubjects();
            logger.debug("Found [" + subjects.size() + "] subjects");
            // Sort the collection by email
            Collections.sort(subjects, new BeanUtil.SubjectComparator());
        }

        // Resources/Institutions
        resourceNames = entitlementUtil.getInstitutions();
        logger.info("Retrieved [" + resourceNames.size() +
                "] institutions from registry");

        // Actions/Permissions
        if (actions == null || actions.isEmpty()) {
            // Filter actions using logged-in users permission level
            Action usersPermission = entitlementUtil.getPermission();
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

    public List<String> getInstitutions(String uid) {
        return entitlementUtil.getInstitutions();
    }

    public EntitlementPolicy getPolicy() {
        String key = getKey();
        Long entitlementKey = null;

        if (key != null && !key.isEmpty()) {
            entitlementKey = Long.parseLong(key);
        }

        if (policy == null) {
            if (entitlementKey != null) {
                policy = entitlementUtil.getPolicy(entitlementKey);
            } else {
                logger.info("Attempting to retrieve entitlement policy with entitlementKey [" +
                        entitlementKey + "]");
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
            if (policy != null) {
                entitlementUtil.updatePolicy(subject, resource, action);
            } else {
                entitlementUtil.createPolicy(subject, resource, action);
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
        if (key != null && !key.isEmpty()) {
            Long entitlementKey = Long.parseLong(key);
            if (entitlementKey != null ) {
                entitlementUtil.removePolicy(entitlementKey);
            } else {
                logger.error("Unable to remove entitlement using key [" +
                        entitlementKey + "]");
            }
        }

        // Forward to: entitlements.xhtml
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String contextPath = facesContext.getExternalContext().getRequestContextPath();
            String redirectURL = contextPath + ENTITLEMENTS_PAGE;

            facesContext.getExternalContext().redirect(redirectURL);
        } catch (Exception e) {
            logger.error(e);
        }

    }

}
