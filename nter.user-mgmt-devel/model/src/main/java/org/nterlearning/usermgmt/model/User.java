/*
 * National Training and Education Resource (NTER)
 * Copyright (C) 2012 SRI International
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
package org.nterlearning.usermgmt.model;


import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


import com.sun.xml.bind.AnyTypeAdapter;

/**
 * @author mfrazier
 *
 */
@XmlJavaTypeAdapter(AnyTypeAdapter.class)
@XmlSeeAlso(UserImpl.class)
@XmlRootElement
public interface User {

	public abstract String getGroup();

	public abstract void setGroup(String group);

	public abstract String getUid();

	public abstract void setUid(String uid);

	public abstract String getLastName();

	public abstract void setLastName(String lastName);

	public abstract String getGivenName();

	public abstract void setGivenName(String givenName);

	public abstract String getFullName();

	public abstract void setFullName(String fullName);

	public abstract void setFullName(String firstName, String lastName);

	public abstract String getUserPassword();

	public abstract void setUserPassword(String userPassword);

	public abstract String getEmail();

	public abstract void setEmail(String email);

	public abstract String getShadowExpire();

	public abstract void setShadowExpire(String shadowExpire);

	public abstract String getMailChange();

	public abstract void setMailChange(String mailChange);

	public abstract String getMailChangeToken();

	public abstract void setMailChangeToken(String mailChangeToken);

	public abstract Map<String, String> getBaseDn();

	public abstract List<String> getObjectClasses();

	public abstract void setObjectClasses(String inObjectClassesStr);

	public abstract void setObjectClasses(List<String> inObjectClasses);

	public abstract void setObjectClasses(String[] inObjectClassesStr);

	public abstract void addObjectClasses(String inObjectClassStr);

	public abstract String getPasswordChangeToken();

	public abstract void setPasswordChangeToken(String passwordChangeToken);

	public abstract String getHrefRedirect();

	public abstract void setHrefRedirect(String hrefRedirect);

	public abstract String getGoogleId();

	public abstract void setGoogleId(String googleId);

	public abstract String getFacebookId();

	public abstract void setFacebookId(String facebookId);
	
	public abstract void setOrganization(String org);
	
	public abstract String getOrganization();
	
	@Deprecated
	public abstract void clear();

}