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
package org.nterlearning.entitlement.webapp.managed;

import org.apache.log4j.Logger;
import org.nterlearning.entitlement.model.EntitlementModel;
import org.nterlearning.entitlement.webapp.controller.AuthController;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.EntitlementPolicy;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 2/17/12
 */
@ManagedBean(name = "entitlementUtil")
public class EntitlementUtil {

    @ManagedProperty("#{entitlementModel}")
	private EntitlementModel entitlementModel;

    @ManagedProperty("#{authController}")
	private AuthController authController;

    private Logger logger = Logger.getLogger(EntitlementUtil.class);

    public EntitlementModel getEntitlementModel() {
        return entitlementModel;
    }
    public void setEntitlementModel(EntitlementModel entitlementModel) {
        this.entitlementModel = entitlementModel;
    }

    public AuthController getAuthController() {
        return authController;
    }

    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    public boolean isAdmin(String realm) {
        boolean isAdmin = false;
        String subjectId = authController.getUID();

        List<EntitlementPolicy> policies =
                getPoliciesRelatedToSubject(realm, subjectId);
        for (EntitlementPolicy policy : policies) {
            if (policy.getAction() == Action.ADMIN ||
                    policy.getAction() == Action.GLOBAL_ADMIN) {
                isAdmin = true;
                break;
            }
        }

        return isAdmin;
    }

    public List<EntitlementPolicy> getPoliciesRelatedToSubject(String realm, String subjectId) {
        List<EntitlementPolicy> policies = new ArrayList<EntitlementPolicy>();
        // Get a list of policies assigned to the logged-user
        List<EntitlementPolicy> subjectPolicies =
                entitlementModel.getPolicyBySubject(realm, subjectId);
        // Retrieve collection of resources assigned to the logged-in user.
        Set<String> resources = new TreeSet<String>();
        for (EntitlementPolicy policy : subjectPolicies) {
            resources.add(policy.getResource());
        }
        // Retrieve policies assigned to the retrieved resources
        for (String resource : resources) {
            List<EntitlementPolicy> resourcePolicies =
                    entitlementModel.getPolicyByResource(realm, resource);
            for (EntitlementPolicy policy : resourcePolicies) {
                if (!policies.contains(policy)) {
                    policies.add(policy);
                }
            }
        }
        return policies;
    }

    public boolean hasAdminPrivileges(String realm, String resource) {
        String user = authController.getUID();

        Action action = entitlementModel.getAuthorization(realm, user, resource);

        logger.info("Authorization for User [" + user +
            "] Institution [" + resource + "] Action [" + action + "]");

        return (action == Action.ADMIN ||
                action == Action.GLOBAL_ADMIN);
    }

    public List<Action> getActions(Action refAction) {
        List<Action> rtnActions = new ArrayList<Action>();
        for (Action action : entitlementModel.getActions()) {
            if (action.ordinal() <= refAction.ordinal()) {
                rtnActions.add(action);
            }
        }

        return rtnActions;
    }

    public boolean hasEditPrivileges(String realm, String resource) {
        String user = authController.getUID();
        Action action = entitlementModel.getAuthorization(realm, user, resource);

        logger.info("Authorization for User [" + user +
            "] Institution [" + resource + "] Action [" + action + "]");

        return (action == Action.READ ||
                action == Action.ADMIN ||
                action == Action.GLOBAL_ADMIN);
    }

}
