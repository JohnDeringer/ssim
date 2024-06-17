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
package org.nterlearning.usermgmt.service;

import org.apache.log4j.Logger;
import org.nterlearning.common.security.WSValidator;

/**
 * @author mfrazier
 *
 * Verifies client credentials (with the IDP) from the SOAP WS-Security header
 */
public class UserMgmtValidator extends WSValidator {

    private IdentityService identityService;
    
    private Logger log = Logger.getLogger(UserMgmtValidator.class);

    /**
     * Set the identity service to use
     * @param identityService
     */
    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

	/* (non-Javadoc)
	 * @see org.nterlearning.common.security.WSValidator#authenticate(java.lang.String, java.lang.String)
	 */
	protected boolean authenticate(String user, String password) {
		boolean retVal = false;
		if (identityService != null) {
			retVal = identityService.authenticate(user, password);
		}
		else {
			log.error("No Identity Service supplied for authentication!");
		}
		
		return retVal;
	}

}
