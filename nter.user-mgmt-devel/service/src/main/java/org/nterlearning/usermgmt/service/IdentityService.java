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
package org.nterlearning.usermgmt.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.nterlearning.usermgmt.model.UserImpl;

@WebService(targetNamespace="http://jaxws.service.usermgmt.nterlearning.org")
@SOAPBinding
(
	style = SOAPBinding.Style.DOCUMENT,
	use = SOAPBinding.Use.LITERAL,
	parameterStyle = SOAPBinding.ParameterStyle.WRAPPED
)

public interface IdentityService {
	
	public static final String USER_LIST = "userList";
	public static final String USER_RETURN = "userReturn";
	public static final String USER = "user";
	public static final String SESSION_ID = "sessionId";
	public static final String USER_ID = "userId";
	public static final String LDAP_USER_NAME = "ldapUserName";
	public static final String USER_LAST_NAME = "userLastName";
	public static final String USER_FIRST_NAME = "userFirstName";
	public static final String LDAP_PASSWORD = "ldapPassword";
	public static final String LDAP_USERNAME = "ldapUsername";
	public static final String USER_DN = "userDn";
	public static final String USER_EMAIL_ADDRESS = "userEmailAddress";
	public static final String USER_PASSWORD = "userPassword";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String BOOLEAN_RETURN="booleanReturn";

	/**
	 * Gets a list of user
	 * @return a UserList object of users
	 */
	@WebMethod
	@WebResult(name=USER_LIST)
	public UserList getUsers();
	
	/**
	 * a simple ping for sanity checks
	 * @return true if successful
	 */
	@WebMethod
	@WebResult(name=BOOLEAN_RETURN)
	public boolean ping();
	
	/**
	 * get a User based on their email address
	 * @param email - the email address
	 * @return a valid user object, or null
	 */
	@WebMethod
	@WebResult(name=USER_RETURN)
	public UserImpl getUserByEmail(@WebParam(name=USER_EMAIL_ADDRESS) String email);

	/**
	 * get a User based on their UID
	 * @param uid - the uid of the user
	 * @return a valid user object, or null
	 */
	
	@WebMethod
	@WebResult(name=USER_RETURN)
	public UserImpl getUserByUID(@WebParam(name=USER_ID) String uid);
	
	/**
	 * authenticate a user
	 * @param email - the email address of the user
	 * @param password - the password of the suer
	 * @return true if authenticated, false otherwise
	 */
	@WebMethod
	@WebResult(name=BOOLEAN_RETURN)
	boolean authenticate(@WebParam(name=EMAIL) String email, @WebParam(name=PASSWORD) String password);
	
/*
	@WebMethod
	@WebResult(name=USER_RETURN)
	public UserImpl createUser(
			@WebParam(name=USER_FIRST_NAME) String firstName,
			@WebParam(name=USER_LAST_NAME) String lastName,
			@WebParam(name=USER_EMAIL_ADDRESS) String emailAddress,
			@WebParam(name=LDAP_PASSWORD) String inLdapPassword);
	@WebMethod(exclude=true)
	@WebResult(name=BOOLEAN_RETURN)
	public boolean deleteUser(@WebParam(name=USER_ID) String uid);
	
	@WebMethod(exclude=true)
	@WebResult(name=BOOLEAN_RETURN)
	public boolean updateUser(@WebParam(name=USER) UserImpl user);
*/

}
