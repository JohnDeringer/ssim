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



import javax.annotation.Resource;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;
import org.nterlearning.usermgmt.service.jaxws.UserList;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:META-INF/test_beans.xml"})
public class IdentityServiceFactoryIT  extends Assert {
	
	@Resource
	private String serviceAddress;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	//@Ignore
	@Test
	public void doit()
	{
		IdentityService is = IdentityServiceFactory.getInstance().getClientPort(serviceAddress,"registry@nterlearning.org","Abcd#1234");
		try
		{
			System.out.println(is.ping());
		
		
//			UserList ul = is.getUsers();
//
//			for (UserImpl u:ul.getList())
//			{
//				System.out.println(u.getEmail());
//			}
			

		}
		catch (SOAPFaultException spe) {
			System.out.println("Not Authorized");
			spe.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void pingTestGood()
	{
		IdentityService is = IdentityServiceFactory.getInstance().getClientPort(serviceAddress,"registry@nterlearning.org","Abcd#1234");
		
		System.out.println(is.ping());
		
	}
	
	@Test(expected=SOAPFaultException.class)
	public void pingTestBad()
	{
		
		IdentityService is = IdentityServiceFactory.getInstance().getClientPort(serviceAddress,"emailtest1@mailinator.com","Abcd1234");
		
		System.out.println(is.ping());
		
	}
	
	//@Ignore
	@Test
	public void testGetUserByEmail()
	{
		IdentityService is = IdentityServiceFactory.getInstance().getClientPort(serviceAddress,"registry@nterlearning.org","Abcd#1234");
		
		UserImpl u = is.getUserByEmail("josso@mailinator.com");
		
		if (u != null)
			System.out.println(u.getFullName());
		else
			System.out.println("no user");
	}

}
