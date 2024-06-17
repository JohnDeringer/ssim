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

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.nterlearning.usermgmt.model.User;
import org.nterlearning.usermgmt.model.UserImpl;
import org.nterlearning.usermgmt.persistence.UserDao;

/**
 * @author mfrazier
 * Implementation of the IdentityService for NTER
 *
 */
@WebService(endpointInterface = "org.nterlearning.usermgmt.service.IdentityService")
public class IdentityServiceImpl implements IdentityService {

	private static Logger log = Logger.getLogger(IdentityServiceImpl.class);
	private UserDao dao;

	/**
	 * Instantiate a new IdentityServiceImpl with a supplied DAO object
	 * @param u
	 */
	public IdentityServiceImpl(UserDao u) {
		dao = u;
	}

	/* (non-Javadoc)
	 * @see org.nterlearning.usermgmt.service.IdentityService#getUserByEmail(java.lang.String)
	 */
	public UserImpl getUserByEmail(String value) {

		// Find all users in the ldap data store
		return (UserImpl) dao.findByEMail(
				value);

	}

	/* (non-Javadoc)
	 * @see org.nterlearning.usermgmt.service.IdentityService#getUsers()
	 */
	public UserList getUsers() {

		// Find all users in the ldap data store
		List<User> userList = dao.findAllUsers();

		List<UserImpl> retVal = new ArrayList<UserImpl>();

		for (User u : userList)
			retVal.add((UserImpl) u);

		// retVal.addAll((Collection<? extends UserImpl>) userList);

		return new UserList(retVal);

	}



	/* (non-Javadoc)
	 * @see org.nterlearning.usermgmt.service.IdentityService#ping()
	 */
	@Override
	public boolean ping() {
		log.info("Ping received");
		return true;
	}

	/* (non-Javadoc)
	 * @see org.nterlearning.usermgmt.service.IdentityService#getUserByUID(java.lang.String)
	 */
	@Override
	public UserImpl getUserByUID(String uid) {

		return (UserImpl)dao.findByUID( uid);
		
	}
	
	/*
	
	@Override
	public UserImpl createUser(String firstName, String lastName,
			String emailAddress, String password) {

		EmailValidator ev = new RFC2822EmailValidator();

		UserImpl retVal = null;

		if (isValid(firstName) && isValid(lastName) && isValid(emailAddress)
				&& isValid(password)) {

			if (ev.validateEmail(emailAddress)) {

				// Check to see if the e-mail address is unique
				if (!myUser.emailInUse(emailAddress)) {

					retVal = (UserImpl) myUser.create(firstName, lastName, emailAddress, password, null);
					if (retVal != null) {
	
							log.info("New Account created for "
									+retVal.getFullName() + "("
									+ retVal.getEmail() + ")");
					}

				}
			}
		}
		

		return retVal;
	}
	
	@Override
	public boolean deleteUser(String uid) {
		return myUser.delete(getUserByUID(uid));
	};


	@Override
	public boolean updateUser(UserImpl user) {
		return myUser.update(user);
	}
	
	*/
	
	@Override
	public boolean authenticate(String email, String password) {
		return dao.authenticate(email, password);

	}


}
