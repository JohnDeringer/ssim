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

import org.nterlearning.usermgmt.model.UserImpl;
import org.nterlearning.usermgmt.service.IdentityService;
import org.nterlearning.usermgmt.service.UserList;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Sample client which uses spring injection throught the CXF configuration to inject a 
 * generated client class which can be used as a proxy. Checked in config assumes the service is running on 
 * localhost
 * @author fritter63
 *
 */
public class IdentityServiceClient {

	IdentityService client;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		IdentityServiceClient c = new IdentityServiceClient();
		c.doit();
	}

	public void doit() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "client_beans.xml" });

		client = (IdentityService) context.getBean("idClient");
		
		System.out.println(client.ping() ? "Ping SUCCESS":"Ping FAILURE");

		UserList l = client.getUsers();
		
		System.out.println("Got "+l.getList().size()+" users");
		
		UserImpl u = client.getUserByEmail("gremlin2@mailinator.com");
		
		System.out.println("user:"+u.getUid());
		
	}

}
