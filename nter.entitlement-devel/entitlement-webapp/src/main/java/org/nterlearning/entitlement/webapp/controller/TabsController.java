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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Controls displaying the top navigation tabs.
 *
 * @author Brian Blonski
 */
@ManagedBean(name = "tabs")
@ViewScoped
public class TabsController implements Serializable {

	//** Private Variables **//

	private String institutionName;
    private String serviceName;
	private boolean subNavRendered;

	//** Getters & Setters **//

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
		setSubNavRendered(true);
	}

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isSubNavRendered() {
		return subNavRendered;
	}

	public void setSubNavRendered(boolean subNavRendered) {
		this.subNavRendered = subNavRendered;
	}
}
