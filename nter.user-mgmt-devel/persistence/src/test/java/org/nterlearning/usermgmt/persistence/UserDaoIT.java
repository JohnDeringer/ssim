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
package org.nterlearning.usermgmt.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nterlearning.usermgmt.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author mfrazier
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:META-INF/testApplicationContext.xml"})
public class UserDaoIT extends UserDaoImpl {

	
	User emptyUser;
	User invalidUser;
	
	@Resource
	UserDao bean_userDao;
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Before
	public void setup()
	{
		emptyUser = (User)applicationContext.getBean("bean_user");
		invalidUser = (User)applicationContext.getBean("bean_user");
		invalidUser.setUid("garbage");
	}


	@Test
	public void getUsers()
	{	
		
		List<User> l = bean_userDao.findAllUsers();
		
		Assert.assertFalse(l.size() == 0);	
		
	}
	
	// insert, retrieve, update, and delete a record
	@Test
	public void CRUD()
	{
		User u;
		
		// First, attempt to insert a new user for testing
		 u = bean_userDao.create("nobody", "nobody", "nobody2@nowhere.com", null, "nothing", null);
		assertFalse(u == null);
		
		// find it
		u = bean_userDao.findByEMailChange("nobody2@nowhere.com");
		assertFalse(u == null);
		
		// update it
		assertTrue(bean_userDao.updateOrganization(u.getUid(), "hello"));

		// find it again
		u = bean_userDao.findByEMailChange("nobody2@nowhere.com");
		assertFalse(u == null);
		assertEquals("hello",u.getOrganization());
		
		assertTrue(bean_userDao.delete(u));
		
		// make sure it's gone!
		u = bean_userDao.findByEMailChange("nobody2@nowhere.com");
		assertTrue(u == null);
		
	}
	
	@Test
	public void updateEmpty()
	{
		assertFalse(bean_userDao.update(emptyUser));
	}

	
	@Test
	public void updateWithNull()
	{
		assertFalse(bean_userDao.update(null));
	}
	
	@Test
	public void createEmpty()
	{
		assertFalse(bean_userDao.create(emptyUser));
	}
	
	@Test
	public void createWithNull()
	{
		assertFalse(bean_userDao.create(null));
	}
	
	@Test
	public void deleteEmpty()
	{
		assertFalse(bean_userDao.delete(emptyUser));
	}
	@Test
	public void deleteWithNull()
	{
		assertFalse(bean_userDao.delete(null));
	}
	
	@Test
	public void updateInvalid()
	{
		assertFalse(bean_userDao.update(invalidUser));
	}
	
	// spring ldap api doesn't throw an error if user object doesn't exist
	@Ignore
	@Test
	public void deleteInvalid()
	{
		assertFalse(bean_userDao.delete(invalidUser));
	}
	
	@Test
	public void createInvalid()
	{
		assertFalse(bean_userDao.create(invalidUser));
	}
	
	@Test
	public void testAuthenticate() {
		
		// make sure that all the filters are working and not preserving previous values
		// from queries, so it should work sequentially
		assertTrue(bean_userDao.authenticate("gremlin2@mailinator.com", "abcd1234"));
		assertFalse(bean_userDao.authenticate("gremlin2@mailinator.com", "abcd234"));
		assertTrue(bean_userDao.authenticate("gremlin2@mailinator.com", "abcd1234"));

	}
}
