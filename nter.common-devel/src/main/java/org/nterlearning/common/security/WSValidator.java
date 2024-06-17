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
package org.nterlearning.common.security;

import org.apache.log4j.Logger;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.validate.UsernameTokenValidator;
import org.apache.ws.security.message.token.UsernameToken;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 3/27/12
 *
 * Verifies client credentials (with the IDP) from the SOAP WS-Security header
 */
public abstract class WSValidator extends UsernameTokenValidator {

    private String user;
    private static Logger log = Logger.getLogger(WSValidator.class);

    public String getUser() {
        return user;
    }

    protected abstract boolean  authenticate(String user,String password);

    @Override
    protected void verifyDigestPassword(UsernameToken usernameToken, RequestData data)
        throws WSSecurityException {

        //String user = usernameToken.getName();
        //String password = usernameToken.getPassword();

        log.error("Password Digest is not supported");

        throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
    }

    @Override
    protected void verifyPlaintextPassword(UsernameToken usernameToken,
	            RequestData data) throws WSSecurityException {

	    String user = usernameToken.getName();
        String password = usernameToken.getPassword();

        log.info("Verifying credentials for user [" + user + "] password [" +
            (password != null ? "*****" : password) + "]");

        boolean validUser = false;

        try {
            validUser = authenticate(user, password);
        } catch (Exception e) {
            log.error("Unexpected error calling Identity Service", e);
        }
        

        if (!validUser) {
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        } else {
            this.user = user;
        }
	}

}
