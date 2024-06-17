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

/**
 * Controls the logic for displaying the details of an entitlement.
 *
 * @author Brian Blonski
 */
@ManagedBean
public class EntitlementDetailsController {

	//** Private Variables **//

	private String name;
	private String emailLink;
	private String institution;
	private String permissionLevel;

	//** Getters & Setters **//

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.emailLink = "mailto:" + name;
	}

	public String getEmailLink() {
		return emailLink;
	}

	public void setEmailLink(String emailLink) {
		this.emailLink = emailLink;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getPermissionLevel() {
		return permissionLevel;
	}

	public void setPermissionLevel(String permissionLevel) {
		this.permissionLevel = permissionLevel;
	}
}
