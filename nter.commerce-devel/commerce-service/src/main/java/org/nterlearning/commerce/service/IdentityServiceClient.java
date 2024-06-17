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

import org.nterlearning.usermgmt.factory.IdentityServiceFactory;
import org.nterlearning.usermgmt.service.jaxws.IdentityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 6/6/12
 */
public class IdentityServiceClient {
    private String wsdlLocation;
    private String username;
    private String password;
    private static IdentityService identityService;

    private Logger logger = LoggerFactory.getLogger(IdentityServiceClient.class);

    public IdentityServiceClient(String wsdlLocation, String username, String password) {
        this.wsdlLocation = wsdlLocation;
        this.username = username;
        this.password = password;
    }

    public IdentityService getIdentityService() {
        try {
            if (identityService == null || !identityService.ping()) {
                identityService =
                        IdentityServiceFactory.getInstance().getClientPort(
                                wsdlLocation, username, password);
            }
        } catch (Exception e) {
            logger.error(
                "Unexpected exception connection to Identity Service using url [" +
                    wsdlLocation + "]");
        }
        return identityService;
    }

}
