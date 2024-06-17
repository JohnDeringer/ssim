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
package org.nterlearning.metadata;

import org.apache.log4j.Logger;
import org.nterlearning.common.security.WSValidator;
import org.nterlearning.usermgmt.common.UserMgmtUtils;
import org.nterlearning.usermgmt.factory.IdentityServiceFactory;
import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 3/27/12
 *
 * Verifies client credentials (with the IDP) from the SOAP WS-Security header
 */
public class MetadataValidator extends WSValidator {

    private IdentityService identityService;
    private String identityServiceAddress;
    private String idUser;
    private String idPassword;
    private String sharedSecret;
    
    public String getIdentityServiceAddress() {
		return identityServiceAddress;
	}

	public void setIdentityServiceAddress(String identityServiceAddress) {
		this.identityServiceAddress = identityServiceAddress;
	}

    private Logger logger = Logger.getLogger(MetadataValidator.class);

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

	@Override
	protected boolean authenticate(String user, String password) {
		boolean retVal = false;
	
		logger.debug("Attempting to authenticate user ("+user+")");

		identityService = IdentityServiceFactory.getInstance().getClientPort(identityServiceAddress, idUser, idPassword);

		if (identityService != null) {
			// here, we use our own well know user/password pair to query for the uid
			UserImpl u = identityService.getUserByEmail(user);

			if (u != null) {
				// now use the uid along witht the shared secret to hash and compare to what we got
				retVal = computeHash(u.getUid()).equals(password);
					
			}
			else {
				logger.error("NULL User returned from identity service email lookup");
			}
		}
		else {
			logger.error("No Identity Service supplied for authentication!");
		}
		
		return retVal;
	}

	private String computeHash(String uid) {

		return UserMgmtUtils.encrypt(uid+sharedSecret);
	
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdPassword(String idPassword) {
		this.idPassword = idPassword;
	}

	public String getIdPassword() {
		return idPassword;
	}

	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	public String getSharedSecret() {
		return sharedSecret;
	}

}
