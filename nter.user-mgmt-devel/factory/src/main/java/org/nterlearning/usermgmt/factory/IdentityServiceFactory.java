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

package org.nterlearning.usermgmt.factory;


import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.nterlearning.common.security.CredentialsClientCallback;
import org.nterlearning.usermgmt.service.jaxws.IdentityService;


public class IdentityServiceFactory {
	
	private static final String QNAMEPort = "IdentityServicePort";
	private static final String QNAMEService = "IdentityServiceService";
	private static final String namespace = "http://service.usermgmt.nterlearning.org/";
	private static IdentityServiceFactory myInstance = new IdentityServiceFactory();
	
	
	protected IdentityServiceFactory() {
		
	}
	
	public static IdentityServiceFactory getInstance() {
		return myInstance;
	}
	
	public IdentityService getClientPort(String serviceAddress,String userName,String password)
	{
		IdentityService retVal = null;

        QName serviceName = new QName(namespace, QNAMEService);
        QName portName = new QName(namespace, QNAMEPort);

        Service service = Service.create(serviceName);
        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING,serviceAddress);
        retVal = service.getPort(portName, org.nterlearning.usermgmt.service.jaxws.IdentityService.class);
        
        // WS-Security header
        Map<String, Object> ctx = new HashMap<String, Object>();

        ctx.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        ctx.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);

        CredentialsClientCallback ecb = new CredentialsClientCallback();
        ecb.setUsername(userName);
        ecb.setPassword(password);
        ctx.put(WSHandlerConstants.USER,
                ecb.getUsername());
        ctx.put(WSHandlerConstants.PW_CALLBACK_REF,
                 ecb);

        Client client = ClientProxy.getClient(retVal);


        Endpoint cxfEndpoint = client.getEndpoint();
        cxfEndpoint.getOutInterceptors().add(new WSS4JOutInterceptor(ctx));
		
		return retVal;
	}
	

}
