package org.nterlearning.usermgmt.service;

import static org.junit.Assert.*;

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

@RunWith(MockitoJUnitRunner.class)
public class UserMgmtValidatorTest extends Mockito {

	private static final String GOODPASSWORD = "goodpassword";
	private static final String BADUSER = "baduser@nowhere.com";
	private static final String GOODUSER = "gooduser@nowhere.com";
	
	@InjectMocks
	UserMgmtValidator umv;
	
	@Mock
	IdentityService id;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		when (umv.authenticate(GOODUSER, GOODPASSWORD)).thenReturn(true);
		when (umv.authenticate(BADUSER, GOODPASSWORD)).thenReturn(false);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGoodAuthenticate() {
		assertTrue(umv.authenticate(GOODUSER, GOODPASSWORD));
	}
	
	@Test
	public void testBadAuthenticate() {
		assertFalse(umv.authenticate(BADUSER, GOODPASSWORD));
	}
	
	@Test
	public void testNullAuthenticate() {
		assertFalse(umv.authenticate(null,null));
	}
	
	@Test
	public void testNullUser() {
		assertFalse(umv.authenticate(null," "));
	}
	
	@Test
	public void testNullPassword() {
		assertFalse(umv.authenticate(GOODUSER,null));
	}
	
	@Test
	public void testBadIdService() {
		umv.setIdentityService(null);
		assertFalse(umv.authenticate(GOODUSER, GOODPASSWORD));
	}

}
