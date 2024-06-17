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

import org.nterlearning.entitlement.client.EntitlementPolicy;
import org.nterlearning.entitlement.client.Subject;
import org.nterlearning.registry.controller.AuthController;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 2/10/12
 */
@ManagedBean(name = "entitlementsBean")
public class EntitlementsBean {

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

    private Logger logger = Logger.getLogger(EntitlementsBean.class);

    // Constructor
    public EntitlementsBean() {
	}

    @PostConstruct
	private void initEntitlements() {
        if (entitlements == null || entitlements.isEmpty()) {
            // Retrieve institutions for current user
            String uid = authController.getUID();

            logger.debug("Retrieving policies for user [" + uid + "]");

            entitlements = new ArrayList<Policy>();
            for (EntitlementPolicy entitlementPolicy :
                            entitlementUtil.getPoliciesRelatedToSubject(uid)) {

                Policy policy = new Policy();
                policy.setKey(entitlementPolicy.getKey());
                policy.setRealm(entitlementPolicy.getRealm());
                policy.setSubject(entitlementPolicy.getSubject());
                policy.setEmail(getEmailForUid(entitlementPolicy.getSubject()));
                policy.setResource(entitlementPolicy.getResource());
                policy.setAction(entitlementPolicy.getAction());

                entitlements.add(policy);
            }

            // Sort the collection by email
            Collections.sort(entitlements, new BeanUtil.PolicyComparator());
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
            administrativeRights =
                    entitlementUtil.hasAdminAccess(resource);
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
        return entitlementUtil.getSubjects();
    }

}
