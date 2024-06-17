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
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

/**
 * Holds authorization values from Shibboleth headers and provides access to certain SSO user info.
 * Defaults to a test user if Shibboleth headers are not available.
 *
 * @author bblonski
 */
@ManagedBean(name = "authController")
public class AuthController implements Serializable {

	private String firstName;
	private String lastName;
	private String UID;
	private String email;
	private String fullName;

	/**
	 * Stores SSO values from request header map.
	 */
	public AuthController() {
		Map<String, String> headerMap = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
		firstName = headerMap.get("givenName");
		lastName = headerMap.get("sn");
		UID = headerMap.get("uid");
		email = headerMap.get("mail");
		fullName = headerMap.get("cn");
	}

	/**
	 * Gets the user's first name.
	 * @return first name
	 */
	public String getName() {
		return firstName != null ? firstName : "testFirstName";
	}

	/**
	 * Gets a unique identifier for the user.
	 * @return UID
	 */
	public String getUID() {
		return UID != null ? UID : "test";
	}

	/**
	 * Gets the user's complete name.
	 * @return full name
	 */
	public String getCN() {
		return fullName != null ? fullName : "testFullName";
	}

	/**
	 * Gets the user's surname/last name.
	 * @return last name
	 */
	public String getSN() {
		return lastName != null ? lastName : "testLastName";
	}

	/**
	 * Gets the user's email address.
	 * @return email address
	 */
	public String getMail() {
		return email != null ? email : "test@nterlearning.org";
	}
}
