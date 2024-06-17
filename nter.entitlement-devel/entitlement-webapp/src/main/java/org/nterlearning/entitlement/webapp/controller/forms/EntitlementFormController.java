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
package org.nterlearning.entitlement.webapp.controller.forms;

import org.apache.log4j.Logger;


import org.nterlearning.entitlement.model.EntitlementModel;
import org.nterlearning.entitlement.webapp.controller.AuthController;
import org.nterlearning.entitlement.webapp.managed.DataCache;
import org.nterlearning.entitlement.webapp.managed.EntitlementUtil;

import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.EntitlementPolicy;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Subject;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls the logic for adding or updating entitlements.
 *
 * @author Brian Blonski
 */
@ManagedBean(name = "entitlementForm")
public class EntitlementFormController implements Serializable {

	//** Private Variables **//

//    @ManagedProperty("#{identityService}")
//	private IdentityService identityService;
//
//    @ManagedProperty("#{registryInterface}")
//	private RegistryInterface registryInterface;

	@ManagedProperty("#{entitlementModel}")
	private EntitlementModel entitlementModel;

    @ManagedProperty("#{authController}")
	private AuthController authController;

    @ManagedProperty("#{entitlementUtil}")
	private EntitlementUtil entitlementUtil;

	private static List<String> users;
	private List<String> institutionNames;
	private List<String> permissions;
	private String selectedUser;
	private String selectedInstitution;
	private String selectedPermission;
	private String subjectId;

    private static final String GLOBAL_REALM = "/";

    private Logger logger = Logger.getLogger(EntitlementFormController.class);


	//** Constructors **//

	public EntitlementFormController() {
		users = DataCache.getUsers();// new ArrayList<String>();
		institutionNames = new ArrayList<String>();
		permissions = new ArrayList<String>();
	}

	//** Data Initialization **//

	@PostConstruct
	private void init() {
		//users.clear();
		//permissions.clear();

        subjectId = authController.getUID();

        if (users == null || users.isEmpty()) {
            List<Subject> subjects = getSubjects();
            logger.info("Retrieved [" + subjects.size() + "] users from idp");
            for (Subject subject : subjects) {
                logger.info("Retrieved subject email[" + subject.getEmail() +
                        "] for UID [" + subject.getSubjectId() + "]");
                users.add(subject.getSubjectId());
                DataCache.setUsers(users);
            }
        }

		institutionNames = getInstitutions();
        logger.info("Retrieved [" + institutionNames.size() +
                "] institutions from registry");

        if (permissions == null || permissions.isEmpty()) {
            // Filter actions using current users permission level
            Action usersPermission = getPermission(subjectId);
logger.info("selectedInstitution [" + selectedInstitution + "]");
            List<Action> actions =
                    entitlementUtil.getActions(usersPermission);
            for (Action action : actions) {
                permissions.add(action.value());
            }
        }
    }

    private List<Subject> getSubjects() {
        List<Subject> subjects = new ArrayList<Subject>();

        return subjects;
    }

    private List<String> getInstitutions() {
        List<String> institutionNames = new ArrayList<String>();

        return institutionNames;
    }

    private Action getPermission(String uid) {
        Action action = Action.READ;
        List<EntitlementPolicy> policies =
                 entitlementModel.getPolicyBySubject(GLOBAL_REALM, uid);
        for (EntitlementPolicy policy : policies) {
logger.info("EntitlementPolicy.action [" + policy.getAction() +
        "] ordinal [" + policy.getAction().ordinal());
            if (action.ordinal() < policy.getAction().ordinal()) {
                action = policy.getAction();
            }
        }

        return action;
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
	public void setEntitlementModel(EntitlementModel entitlement) {
		this.entitlementModel = entitlement;
	}

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    //** Getters & Setters **//

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSelectedUser() {
		return selectedUser;
	}

    public void setSelectedUser(String selectedUser) {
		this.selectedUser = selectedUser;
	}

	public String getSelectedInstitution() {
		return selectedInstitution;
	}

	public void setSelectedInstitution(String selectedInstitution) {
		this.selectedInstitution = selectedInstitution;
	}

	public String getSelectedPermission() {
		return selectedPermission;
	}

	public void setSelectedPermission(String selectedPermission) {
		this.selectedPermission = selectedPermission;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public List<String> getInstitutionNames() {
		return institutionNames;
	}

	public void setInstitutionNames(List<String> institutions) {
		this.institutionNames = institutions;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	//** Actions **//

	public void save() {
        logger.info("Saving new policy user [" + getSelectedUser() +
                "] institution [" + getSelectedInstitution() +
                "] adminUser [" + getSubjectId() + "]");
		EntitlementPolicy policy =
            entitlementModel.getPolicy(
                    GLOBAL_REALM, getSelectedUser(), getSelectedInstitution());
		if (policy != null) {
			entitlementModel.updatePolicy(
                    GLOBAL_REALM, getSelectedUser(), getSelectedInstitution(),
                    getSelectedPermission(), getSubjectId());
		} else {
			entitlementModel.createPolicy(
                    GLOBAL_REALM, getSelectedUser(), getSelectedInstitution(),
                    getSelectedPermission(), getSubjectId());
		}
	}

}
