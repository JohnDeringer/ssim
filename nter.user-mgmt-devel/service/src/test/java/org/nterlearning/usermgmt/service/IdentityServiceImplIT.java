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

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nterlearning.usermgmt.model.User;
import org.nterlearning.usermgmt.service.IdentityServiceImpl;
import org.nterlearning.usermgmt.service.UserList;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:META-INF/cxf_beans.xml"})
public class IdentityServiceImplIT extends Assert {
	
	@Resource
	protected  IdentityServiceImpl id_service;

	
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
	public void testGetUsers() {
		
		assertNotNull(id_service);
		
		UserList lu = id_service.getUsers();
		
		if (lu.getList().size() < 1) fail("zero users");
	}
	
	//@Ignore
	@Test
	public void testGetUser() {
		User u = id_service.getUserByEmail("josso@mailinator.com");
		
		
		assertNotNull(u);
		System.out.println(u.getGivenName());
	}
	
	@Test
	public void testCreateAndDeleteUser() {
//		UserImpl u = id_service.createUser("paul", "mcartney", "paul@fabfour.com", "strawberryfields");
//		assertNotNull(u);
//		assertTrue(id_service.deleteUser(u.getUid()));
	}
	
	@Test
	public void testDeleteUser() {
		
	}
	
	@Test
	public void validateUser() {
		
		assertTrue(id_service.authenticate("gremlin2@mailinator.com", "abcd1234"));

	}


}
