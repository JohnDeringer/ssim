package org.nterlearning.usermgmt.service;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.nterlearning.usermgmt.model.UserImpl;
import org.nterlearning.usermgmt.model.User;
import org.nterlearning.usermgmt.persistence.UserDao;

@RunWith(MockitoJUnitRunner.class)
public class IdentityServiceImplTest extends Mockito {

	private static final String BADUSER = "baduser@nowhere.com";

	private static final String GOODUSER = "gooduser@nowhere.com";

	private static final String GOODPASSWORD = "lksdjf";

	private static final String BADPASSWORD = null;

	@InjectMocks
	IdentityServiceImpl id;
	
	@Mock
	UserDao dao;
	
	@Mock
	UserImpl user;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
		when(dao.findByEMail(GOODUSER)).thenReturn(user);
		when(dao.findByEMail(BADUSER)).thenReturn(null);
		
		when(dao.findByUID(GOODUSER)).thenReturn(user);
		when(dao.findByUID(BADUSER)).thenReturn(null);
		
		when(dao.authenticate(GOODUSER, GOODPASSWORD)).thenReturn(true);
		when(dao.authenticate(BADUSER,eq( anyString()))).thenReturn(false);
		
		ArrayList<User> au = new ArrayList<User>();
		au.add(user);
		au.add(user);

		
		when(dao.findAllUsers()).thenReturn(au);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetUserByEmail() {
		assertEquals(user,id.getUserByEmail(GOODUSER));
	}
	
	@Test
	public void testBadGetUserByEmail() {
		assertEquals(null,id.getUserByEmail(BADUSER));
	}

	@Test
	public void testGetUsers() {
		assertNotNull(id.getUsers());
		assertEquals(2,id.getUsers().getList().size());
	}

	@Test
	public void testPing() {
		assertTrue(id.ping());
	}

	@Test
	public void testGetUserByUID() {
		assertEquals(user,id.getUserByUID(GOODUSER));
	}
	
	@Test
	public void testGetBadUserByUID() {
		assertEquals(null,id.getUserByUID(BADUSER));
	}
	
	@Test
	public void testAuthenticate() {
		assertTrue(id.authenticate(GOODUSER, GOODPASSWORD));
	}
	
	@Test
	public void testNullUserAuthenticate() {
		assertFalse(id.authenticate(null, GOODPASSWORD));
	}
	
	@Test
	public void testBadUserAuthenticate() {
		assertFalse(id.authenticate(BADUSER, GOODPASSWORD));
	}
	
	@Test
	public void testBadPasswordrAuthenticate() {
		assertFalse(id.authenticate(GOODUSER, BADPASSWORD));
	}

}
