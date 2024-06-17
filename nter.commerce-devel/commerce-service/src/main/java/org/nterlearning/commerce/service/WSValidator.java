/*
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

package org.nterlearning.commerce.service;

import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.message.token.UsernameToken;
import org.apache.ws.security.validate.UsernameTokenValidator;

import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/11/12
 */
public class WSValidator extends UsernameTokenValidator {

    private String user;
    private String uid;
    private IdentityService identityService;
    private Logger logger = LoggerFactory.getLogger(WSValidator.class);

    public String getUser() {
        return user;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public String getUid() {
        if (identityService != null && user != null) {
            logger.debug("Requesting UID from IDP for user [" + user + "]");
            try {
                UserImpl userImpl = identityService.getUserByEmail(user);
                uid = userImpl.getUid();
                logger.debug("Located UID [" + uid + "] for user [" + user + "]");
            } catch (Exception e) {
                logger.error("Error connecting to Identity Service", e);
            }
        } else {
            logger.error("Unable to retrieve UID from IDP identityService ref [" +
                identityService + "] for user [" + user + "]");
        }
        return uid;
    }

    @Override
    protected void verifyPlaintextPassword(UsernameToken usernameToken,
	            RequestData data) throws WSSecurityException {

	    String user = usernameToken.getName();
        String password = usernameToken.getPassword();

        logger.info("Verifying credentials for user [" + user + "] password [" +
            (password != null ? "*****" : password) + "]");

        boolean validUser = false;
        if (identityService != null) {
            try {
                validUser = identityService.authenticate(user, password);
            } catch (Exception e) {
                logger.error("Unexpected error calling Identity Service", e);
            }
        } else {
            logger.error("Unable to contact Identity Service [" + identityService + "]");
        }

        if (!validUser) {
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        } else {
            this.user = user;
        }
	}
}
