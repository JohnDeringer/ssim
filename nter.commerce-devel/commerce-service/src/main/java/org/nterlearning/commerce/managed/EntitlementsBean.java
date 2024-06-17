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

import org.nterlearning.commerce.controller.AuthController;

import org.nterlearning.entitlement.client.EntitlementPolicy;
import org.nterlearning.entitlement.client.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/9/12
 */
@ManagedBean(name = "entitlementsBean")
public class EntitlementsBean implements Serializable {

    @ManagedProperty("#{entitlementUtil}")
	private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{authController}")
	private AuthController authController;

    @ManagedProperty("#{param['subject']}")
    private String subject;

    @ManagedProperty("#{param['email']}")
    private String email;

    @ManagedProperty("#{param['action']}")
    private String action;

    @ManagedProperty("#{param['resource']}")
    private String resource;

	private List<Policy> entitlements;
    private static List<Subject> subjects;

    private Logger logger = LoggerFactory.getLogger(EntitlementsBean.class);

    // Constructor
    public EntitlementsBean() {
        entitlements = new ArrayList<Policy>();
	}

    @PostConstruct
    @SuppressWarnings("unchecked")
	private void initEntitlements() {
        if (entitlements.isEmpty()) {
            Object object =
                BeanUtil.getAttributeFromSession(
                    Policy.class.getCanonicalName()
                );
            if (object != null) {
                entitlements = (ArrayList<Policy>)object;
                logger.debug("Found session object [" +
                    Policy.class.getCanonicalName() +
                        "] with a size of [" + entitlements.size() + "]");
            }
        }

        if (entitlements.isEmpty()) {
            // Retrieve institutions for current user
            entitlements = new ArrayList<Policy>();
            for (EntitlementPolicy entitlementPolicy :
                            entitlementUtil.getPoliciesRelatedToSubject()) {

                Policy policy = new Policy();
                policy.setKey(entitlementPolicy.getKey());
                policy.setRealm(entitlementPolicy.getRealm());
                policy.setSubject(entitlementPolicy.getSubject());
                policy.setEmail(getEmailForUid(entitlementPolicy.getSubject()));
                policy.setResource(entitlementPolicy.getResource());
                policy.setAction(entitlementPolicy.getAction());

                entitlements.add(policy);
            }
            BeanUtil.setAttributeInSession(
                    Policy.class.getCanonicalName(), entitlements);
        }
	}

    public AuthController getAuthController() {
        return authController;
    }
    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

	public EntitlementUtil getEntitlementUtil() {
		return entitlementUtil;
	}
	public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
		this.entitlementUtil = entitlementUtil;
	}

	public List<Policy> getEntitlements() {
		return entitlements;
	}
	public void setEntitlements(List<Policy> entitlements) {
		this.entitlements = entitlements;
	}

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public String getResource() {
        if (resource != null) {
            return BeanUtil.unescape(resource);
        } else {
            return null;
        }
    }
    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

    public boolean getAdmin() {
        return entitlementUtil.isAdmin(resource);
    }

    public boolean getAdministrative(String subject, String resource) {
        boolean administrativeRights = false;
        String uid = authController.getUID();
        // Can't delete yourself
        if (!subject.equals(uid)) {
            administrativeRights = entitlementUtil.hasAdminAccess(resource);
        }
        return administrativeRights;
    }

    public boolean getEditable(String resource) {
        return entitlementUtil.hasWriteAccess(resource);
    }

    private String getEmailForUid(String uid) {
        String email = uid;
        for (Subject subject : getSubjects()) {
            if (subject.getSubjectId().equals(uid)) {
                String idpEmail = subject.getEmail();
                if (idpEmail != null && !idpEmail.isEmpty()) {
                    email = idpEmail;
                } else {
                    logger.warn("The IDP did not return an email address for subject [" +
                        uid + "], using subject instead of email");
                }
                break;
            }
        }

        return email;
    }

    private List<Subject> getSubjects() {
        if (subjects == null || subjects.isEmpty()) {
            subjects = new ArrayList<Subject>();
            List<Subject> subjectList = entitlementUtil.getSubjects();
            for (Subject subject : subjectList) {
                if (subject.getEmail() != null && !subject.getEmail().isEmpty()) {
                    subjects.add(subject);
                }
            }
        }
        return subjects;
    }

}
