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
package org.nterlearning.entitlement.webapp.controller;

import org.apache.log4j.Logger;
import org.nterlearning.entitlement.model.EntitlementModel;
import org.nterlearning.entitlement.webapp.controller.forms.EntitlementFormController;
import org.nterlearning.entitlement.webapp.managed.EntitlementUtil;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.EntitlementPolicy;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls the logic for displaying entitlements.
 *
 * @author Brian Blonski
 */
@ManagedBean(name = "entitlements")
public class EntitlementsController implements Serializable {

	//** Private Variables **//

	@ManagedProperty("#{entitlementModel}")
	private EntitlementModel entitlementModel;

    @ManagedProperty("#{authController}")
	private AuthController authController;

    @ManagedProperty("#{entitlementUtil}")
	private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{entitlementForm}")
	private EntitlementFormController form;

	private List<EntitlementDetailsController> entitlements;
	private boolean formVisible;

	private String subjectId;

    private Logger logger = Logger.getLogger(EntitlementsController.class);

	//** Constructors **//

	public EntitlementsController() {
		entitlements = new ArrayList<EntitlementDetailsController>();
	}

    public AuthController getAuthController() {
        return authController;
    }
    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    public EntitlementModel getEntitlementModel() {
        return entitlementModel;
    }
    public void setEntitlementModel(EntitlementModel entitlementModel) {
        this.entitlementModel = entitlementModel;
    }

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    //** Data Initialization **//

	@PostConstruct
	private void initEntitlements() {
		//entitlements.clear();

        // Retrieve institutions for current user
        String uid = authController.getUID();
        // TODO: Hard-coded realm
        String realm = "/";

        List<EntitlementPolicy> policies =
                entitlementUtil.getPoliciesRelatedToSubject(realm, uid);

        logger.info("Retrieving [" + policies.size() +
                "] entitlement policies for user [" + uid + "]");

		for (EntitlementPolicy policy : policies) {
			EntitlementDetailsController temp = new EntitlementDetailsController();
			temp.setName(policy.getSubject());
			temp.setInstitution(policy.getResource());
			temp.setPermissionLevel(policy.getAction().value());
			entitlements.add(temp);
		}
	}

	//** Getters & Setters **//

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public EntitlementFormController getForm() {
		return form;
	}

	public void setForm(EntitlementFormController form) {
		this.form = form;
	}

	public List<EntitlementDetailsController> getEntitlements() {
		return entitlements;
	}

	public void setEntitlements(List<EntitlementDetailsController> entitlements) {
		this.entitlements = entitlements;
	}

	public boolean isFormVisible() {
		return formVisible;
	}

	public void setFormVisible(boolean formVisible) {
		this.formVisible = formVisible;
	}

	//** Actions **//

	public void showForm() {
		EntitlementDetailsController entitlement = (EntitlementDetailsController)FacesContext.getCurrentInstance()
				.getExternalContext().getRequestMap().get("entitlement");
		if(entitlement != null) {
			form.setSelectedUser(entitlement.getName());
			form.setSelectedInstitution(entitlement.getInstitution());
			form.setSelectedPermission(entitlement.getPermissionLevel());
		}
		setFormVisible(true);
	}

	public void toggleForm() {
		setFormVisible(!isFormVisible());
	}

	public void saveForm() {
		form.save();
		initEntitlements();
		setFormVisible(false);
	}

	public void remove() {
        // TODO: Realm
        String realm = null;
		String entitlementName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("resourceId");
		String subjectId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("subjectId");
		entitlementModel.removePolicy(realm, subjectId, entitlementName, getSubjectId());
		initEntitlements();
	}

    public boolean getAdmin() {
        // TODO: Hard-coded realm
        String realm = "/";
        return entitlementUtil.isAdmin(realm);
    }

    public boolean getAdmin(String resource) {
        // TODO: Realm
        String realm = null;
        return entitlementUtil.hasAdminPrivileges(realm, resource);
    }

    public boolean getEditable(String resource) {
        // TODO: Realm
        String realm = null;
        return entitlementUtil.hasEditPrivileges(realm, resource);
    }

}
