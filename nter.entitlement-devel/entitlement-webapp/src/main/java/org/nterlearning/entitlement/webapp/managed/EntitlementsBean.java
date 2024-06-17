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
import org.nterlearning.entitlement.webapp.controller.EntitlementDetailsController;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.EntitlementPolicy;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 2/10/12
 */
@ManagedBean(name = "entitlementsBean")
public class EntitlementsBean {


    @ManagedProperty("#{entitlementModel}")
	private EntitlementModel entitlementModel;
    @ManagedProperty("#{authController}")
	private AuthController authController;

	private List<EntitlementDetailsController> entitlements;
    private Logger logger = Logger.getLogger(EntitlementsBean.class);

    // Constructor
    public EntitlementsBean() {
	}

    public AuthController getAuthController() {
        return authController;
    }

    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    @PostConstruct
	private void initEntitlements() {
        // Retrieve institutions for current user
        String uid = authController.getUID();
        // TODO: Hard-coded realm
        String realm = "/";

        List<EntitlementPolicy> policies =
                entitlementModel.getPolicyBySubject(realm, uid);
        logger.info("Retrieving [" + policies.size() +
                "] entitlement policies for user [" + uid + "]");
	}

	public EntitlementModel getEntitlementModel() {
		return entitlementModel;
	}

	public void setEntitlementModel(EntitlementModel entitlement) {
		entitlementModel = entitlement;
	}

	public List<EntitlementDetailsController> getEntitlements() {
		return entitlements;
	}

	public void setEntitlements(List<EntitlementDetailsController> entitlements) {
		this.entitlements = entitlements;
	}


}
