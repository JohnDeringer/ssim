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
package org.nterlearning.usermgmt.persistence;

import java.util.List;

import org.nterlearning.usermgmt.model.User;
import org.springframework.ldap.core.LdapTemplate;


/**
 * @author mfrazier
 *
 */
public interface UserDao {


	
	/*
	 * CRUD methods
	 */
	public boolean create(User person);
	public boolean update(User person);
	public boolean delete(User person);
	
	/*
	 * Various find methods to hide implementation details
	 */
	public User findByEMail(String email);
	public User findByEMailChange(String email);
	public User findByPasswordChangeToken(String password);
	public User findByEMailChangeToken(String token);
	public User findByUID(String uid);
	public List<User> findAllUsers();
	/*
	 * Various update methods for each attribute, to hide the actual details of implentation from the caller
	 */
	public boolean updatePassword(String inUid, String attrValue) throws PasswordHistoryException;
	public boolean updatePasswordChangeToken(String uid,String token);
	public boolean updateRedirect(String uid,String href);
	public boolean updateMailChange(String uid,String email);
	public boolean updateShadowExpire(String uid,String value);
	public boolean updateEmail(String uid,String email);
	public boolean updateGivenName(String uid,String givenName);
	public boolean updateLastName(String uid,String lastName);
	public boolean updateMailChangeToken(String uid, String token);
	public boolean updateOrganization(String uid, String org);
	
	public void setLdapTemplate(LdapTemplate ld);
	
	
	public User create(String firstName, String lastName, String email, String org, String password,String referer);
	/**
	 * Sets the LDAP group
	 * @param group
	 */
	@Deprecated
	public void setGroup(String group);
	/**
	 * gets the LDAP group
	 * @return
	 */
	public String getGroup();
	/**
	 * Checks to see if an email is in use
	 * @param email email address to check
	 * @return true if it is in use
	 */
	public boolean emailInUse(String email);
	
	/**
	 * Authenticate a user based on email address
	 * @param email - email of the user
	 * @param password - password of the user
	 * @return true if authenticated, otherwise false
	 */
	public boolean authenticate(String email,String password);
	
	
}
