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
package org.nterlearning.commerce.controller;

import org.nterlearning.commerce.controller.AuthController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Controls displaying the top navigation tabs.
 *
 * @author Brian Blonski
 */
@ManagedBean(name = "tabs")
public class TabsController implements Serializable {

	@ManagedProperty("#{authController}")
	private AuthController authController;

	private String institutionName;
    private String entitlementName;
	private boolean subNavRendered;

	public AuthController getAuthController() {
        return authController;
    }
    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    public String getEmail() {
        String email = null;
        if (authController != null) {
            email = authController.getEmail();
        }
        return email;
    }

	public String getInstitutionName() {
		return institutionName;
	}
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
		setSubNavRendered(true);
	}

    public String getEntitlementName() {
        return entitlementName;
    }
    public void setEntitlementName(String entitlementName) {
        this.entitlementName = entitlementName;
    }

	public boolean isSubNavRendered() {
		return subNavRendered;
	}

	public void setSubNavRendered(boolean subNavRendered) {
		this.subNavRendered = subNavRendered;
	}
}
