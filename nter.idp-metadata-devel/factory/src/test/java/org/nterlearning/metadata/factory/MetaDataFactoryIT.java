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

package org.nterlearning.metadata.factory;


import javax.annotation.Resource;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nterlearning.metadata.jaxws.MetaDataAdder;
import org.nterlearning.usermgmt.common.UserMgmtUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:META-INF/test_beans.xml"})
public class MetaDataFactoryIT {
	
	@Resource
	private String serviceAddress;
	@Resource
	private String sharedSecret;

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
	
	@Test(expected=SOAPFaultException.class)
	public void badCred()
	{
		
		MetaDataAdder mdf = MetaDataFactory.getInstance().getClientPort(serviceAddress,"emailtest1@mailinator.com","Abcd1234");
		
		mdf.ping();
	}
	
	@Test
	public void goodCred()
	{
		String uid="8beff655-2800-4043-a311-965db4243b24";
		String pwd = UserMgmtUtils.encrypt(uid+sharedSecret);
		
		
		MetaDataAdder mdf = MetaDataFactory.getInstance().getClientPort(serviceAddress,"emailtest1@mailinator.com",pwd);
		
		mdf.ping();
	}

}
