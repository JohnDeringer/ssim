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

package org.nterlearning.entitlement.client;

import org.apache.ws.security.WSPasswordCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import java.io.IOException;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/27/12
 */
public class EntitlementClientCallback implements CallbackHandler {

    private String username;
    private String password;

    private Logger logger = LoggerFactory.getLogger(EntitlementClientCallback.class);

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {

        WSPasswordCallback passwordCallback = (WSPasswordCallback) callbacks[0];

        logger.debug("Injecting credentials for user [" + username +
                "] password [" + (password != null ? "*****" : password) + "]");

        passwordCallback.setPassword(
                getPassword(passwordCallback.getIdentifier()));
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword(String username) {
        String password = null;
        if (username.equals(this.username)) {
            password = this.password;
        }
        return password;
    }

}
