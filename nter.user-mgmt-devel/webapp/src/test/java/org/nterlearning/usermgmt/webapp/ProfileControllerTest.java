/*
r * National Training and Education Resource (NTER)
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
package org.nterlearning.usermgmt.webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

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
import org.nterlearning.usermgmt.common.PasswordValidator;
import org.nterlearning.usermgmt.common.RFC2822EmailValidator;
import org.nterlearning.usermgmt.model.User;
import org.nterlearning.usermgmt.model.UserAccountNotifier;
import org.nterlearning.usermgmt.model.UserImpl;
import org.nterlearning.usermgmt.persistence.PasswordHistoryException;
import org.nterlearning.usermgmt.persistence.UserDao;
import org.nterlearning.usermgmt.webapp.utils.ConfigurableParams;
import org.nterlearning.usermgmt.webapp.utils.ContextLookup;
import org.nterlearning.usermgmt.webapp.utils.NTERResourceBundleMessageSource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * @author mfrazier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProfileControllerTest extends Mockito {


	private static final String BLAH = "blah";
	
	User user;

	@InjectMocks
	ProfileController pc;

	@Mock
	ContextLookup cl;

	@Mock
	UserDao dao;

	@Mock
	UserAccountNotifier uan;

	@Mock
	ConfigurableParams cp;
	
	@Mock
	SessionLocaleResolver slr;
	
	@Mock
	NTERResourceBundleMessageSource rbm;
	
	@Mock
	PasswordValidator pv;
	
	@Mock
	RFC2822EmailValidator ev;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		// mock out the configuration params getUrl
		when(cp.getUrl()).thenReturn("http://www.someurl.com");

		// build up a dummy user
		user = new UserImpl();
		user.setGivenName("roy");
		user.setLastName("owens");
		user.setMailChange("roy@heehaw.com");
		user.setEmail("roy@nowhere.com");
		user.setHrefRedirect(TestValues.MOCK_URL);
		user.setOrganization("HeeHaw");

		when(
				dao.findByUID(
						TestValues.MOCK_USER_NAME)).thenReturn(user);
		

		// wire up the dao to do nothing much but always return true so that we don't
		// have to hit a real database
		when(dao.findByEMailChange( "existingUser@mailinator.com")).thenReturn(user);
		when(dao.findByEMailChangeToken(TestValues.A_VALID_TOKEN)).thenReturn(user);
		
		when(dao.update(user)).thenReturn(true);
		when(dao.updatePasswordChangeToken(anyString(), anyString())).thenReturn(true);
		when(dao.updatePassword(anyString(),anyString())).thenReturn(true);
		when(dao.updatePasswordChangeToken(anyString(),anyString())).thenReturn(true);
		when(dao.updateRedirect(anyString(),anyString())).thenReturn(true);
		when(dao.updateMailChange(anyString(),anyString())).thenReturn(true);
		when(dao.updateShadowExpire(anyString(),anyString())).thenReturn(true);
		when(dao.updateEmail(anyString(),anyString())).thenReturn(true);
		when(dao.updateGivenName(anyString(),anyString())).thenReturn(true);
		when(dao.updateLastName(anyString(),anyString())).thenReturn(true);
		when(dao.updateMailChangeToken(anyString(),anyString())).thenReturn(true);
		when(dao.updateOrganization(anyString(), anyString())).thenReturn(true);
		
		when(cl.getRequest()).thenReturn(null);
		when(cl.getUserName()).thenReturn(TestValues.MOCK_USER_NAME);
		
		pc.setPasswordValidator(pv);
		
		// hardcode the base name of the resource builder
		when(rbm.getBasename()).thenReturn("nter_messages");
		
		// fake out the password validator for various conditions
		when(pv.validatePassword(TestValues.FAILED_PASSWORD)).thenReturn(true);
		when(pv.validatePassword(TestValues.GOOD_PASSWORD)).thenReturn(true);
		when(pv.validatePassword(TestValues.BAD_PASSWORD)).thenReturn(false);
		when(pv.getRegexPattern()).thenReturn(BLAH);
				
		// throw an excpeiont if updatePassword is thrown with certain parameters
		doThrow(new PasswordHistoryException(BLAH)).when(dao).updatePassword(TestValues.MOCK_USER_NAME, TestValues.FAILED_PASSWORD);

		// make sure that the email validator passes through to the real object/method for emails
		doCallRealMethod().when(ev).validateEmail(anyString());
		
	}

	@After
	public void tearDown() throws Exception {
	}



	@Test
	public void testDoGetNoUser() {

		// force a null return on getUserName call
		when(cl.getUserName()).thenReturn(null);
		ModelAndView mav = pc.doGet(null, null);

		Map<String, Object> model = mav.getModel();
		assertEquals(NTERController.PROFILE_ERROR_JSP,mav.getViewName());
		assertEquals( cp.getUrl(),model.get(NTERController.DEFAULT_REFERER));
		assertEquals(null,model.get(NTERController.REFERER) );
	}

	@Test
	public void testDoGetUserInHeader() {

		// now force a good return on getUserName
		reset(cl);
		when(cl.getUserName()).thenReturn(TestValues.MOCK_USER_NAME);
		
		ModelAndView mav = pc.doGet(null, null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());

	}

	@Test
	public void testDoGetEmptyUser() {

		ModelAndView mav = pc.doGet("", null);
		assertEquals(NTERController.PROFILE_ERROR_JSP,mav.getViewName());
	}
	
	@Test
	public void testDoGetEmptyEmail() {

		// setup a user to be returned when findByUID("bogus") is called
		UserImpl ui = new UserImpl();
		ui.setMailChange(null);
		ui.setEmail("something");
		when(dao.findByUID( "bogus")).thenReturn(ui);
		
		ModelAndView mav = pc.doGet("bogus", null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());
		assertEquals(mav.getModel().get(NTERController.EMAIL_ADDRESS),mav.getModel().get(NTERController.OLD_EMAIL_ADDRESS));
	}

	@Test
	public void testDoGetAllGood() {

		ModelAndView mav = pc.doGet(TestValues.MOCK_USER_NAME, TestValues.MOCK_URL);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());
		Map<String, Object> model = mav.getModel();

		assertEquals(model.get(NTERController.DEFAULT_REFERER), cp.getUrl());
		assertEquals(model.get(NTERController.REFERER), TestValues.MOCK_URL);

		assertEquals(model.get(NTERController.USER_NAME),
				TestValues.MOCK_USER_NAME);
		assertEquals(model.get(NTERController.FIRST_NAME), user.getGivenName());
		assertEquals(model.get(NTERController.LAST_NAME), user.getLastName());
		assertEquals(model.get(NTERController.EMAIL_ADDRESS),
				user.getMailChange());
		assertEquals(model.get(NTERController.OLD_EMAIL_ADDRESS),
				user.getEmail());
		assertEquals(model.get(NTERController.ORGANIZATION),user.getOrganization());
		assertEquals(model.get(NTERController.PASSWORD_PATTERN),BLAH);
		
		//assertTrue(model.get(NTERController.IS_PASSWORD_CHANGE_ERROR) != null);
	}

	@Test
	public void testManagementBadUserEmpty() {
		reset(cl);
		when(cl.getUserName()).thenReturn(null);
		ModelAndView mav = pc.management("", null, null, null,null, null, null);
		assertEquals(NTERController.PROFILE_ERROR_JSP,mav.getViewName());
	}
	@Test
	public void testManagementBadUser() {
		reset(cl);
		when(cl.getUserName()).thenReturn(null);
		ModelAndView mav = pc.management("completelyInvalidUser", null, null,null, null, null, null);
		assertEquals(NTERController.PROFILE_ERROR_JSP,mav.getViewName());
	}

	@Test
	public void testManagementMissingUserName() {
		ModelAndView mav = pc.management(null, null, null, null, null,null,
				null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());

	}

	@Test
	public void testManagementGoodUser() {
		ModelAndView mav = pc.management(TestValues.MOCK_USER_NAME, null, null, null, null,null,
				null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());


	}
	
	@Test
	public void testManagementExistingUser() {
		ModelAndView mav = pc.management(TestValues.MOCK_USER_NAME, null, null, "existingUser@mailinator.com",null, null,
				null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());

	}
	
	@Test
	public void testManagementGoodUserFirstName() {
		ModelAndView mav = pc.management(TestValues.MOCK_USER_NAME, "different", null, null, null, null,
				null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());

	}
	
	@Test
	public void testManagementGoodUserLastName() {
		ModelAndView mav = pc.management(TestValues.MOCK_USER_NAME, null, "different", null, null, null,
				null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());

	}
	
	@Test
	public void testManagementGoodUserEmail() {
		ModelAndView mav = pc.management(TestValues.MOCK_USER_NAME, null, null, "different@mailinator.com", null, null,
				null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());

	}

	@Test
	public void testManagementGoodUserOrg() {
		ModelAndView mav = pc.management(TestValues.MOCK_USER_NAME, null, null, null, "newOrg", null,
				null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());
		assertEquals("newOrg",mav.getModel().get(NTERController.ORGANIZATION));
		assertEquals(true,mav.getModel().get(NTERController.IS_ORG_UPDATED));

	}


	@Test
	public void testManagementChangeEmail() {
		ModelAndView mav = pc.management(TestValues.MOCK_USER_NAME, null, null,
				"somenewemail@fake.com",null, null, null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());
		assertTrue(mav.getModel().containsKey(NTERController.OLD_EMAIL_ADDRESS));
		assertEquals(true,mav.getModel().get(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE));
	}
	@Test
	public void testManagementChangeBadEmail() {
		ModelAndView mav = pc.management(TestValues.MOCK_USER_NAME, null, null,
				"somenewemail",null,null, null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());
		assertEquals(false,mav.getModel().get(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE));
		assertEquals(false,mav.getModel().get(NTERController.IS_EMAIL_ADDRESS_UPDATED));
	}

	@Test
	public void testChangePasswordNoUser() {
		ModelAndView mav = pc.changePassword(null, null, null, null, null);
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());
		Map<String, Object> model = mav.getModel();
		assertEquals(model.get(NTERController.PASSWORD_PATTERN),BLAH);
	}

	@Test
	public void testChangePasswordEmptyUser() {
		ModelAndView mav = pc.changePassword(null, null, "", null, null);
		Map<String, Object> model=mav.getModel();
		assertEquals(NTERController.PROFILE_ERROR_JSP,mav.getViewName());
		assertEquals(model.get(NTERController.PASSWORD_PATTERN),BLAH);
	}

	@Test
	public void testChangePasswordBadUser() {
		ModelAndView mav = pc.changePassword(null, null, "gobbledygook", null, null);
		Map<String, Object> model=mav.getModel();
		assertEquals(NTERController.PROFILE_ERROR_JSP,mav.getViewName());
		assertEquals(model.get(NTERController.PASSWORD_PATTERN),BLAH);
	}

	@Test
	public void testChangePasswordGoodUserNoMatch() {
		ModelAndView mav = pc.changePassword(TestValues.GOOD_PASSWORD, TestValues.BAD_PASSWORD, TestValues.MOCK_USER_NAME,
				null, null);
		Map<String, Object> model=mav.getModel();
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());
		assertEquals(model.get(NTERController.PASSWORD_PATTERN),BLAH);
	}

	@Test
	public void testChangePasswordGoodUser() {
		ModelAndView mav = pc.changePassword(TestValues.GOOD_PASSWORD, TestValues.GOOD_PASSWORD,
				TestValues.MOCK_USER_NAME, null, null);
		Map<String, Object> model=mav.getModel();
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());
		assertEquals(model.get(NTERController.PASSWORD_PATTERN),BLAH);
	}
	
	@Test
	public void testFailedPasswordChange() {
		ModelAndView mav = pc.changePassword(TestValues.FAILED_PASSWORD, TestValues.FAILED_PASSWORD, TestValues.MOCK_USER_NAME, null, null);
		Map<String, Object> model=mav.getModel();
		assertEquals(true,mav.getModel().get(NTERController.IS_PASSWORD_CHANGE_ERROR));
		assertEquals(false,mav.getModel().get(NTERController.IS_PASSWORD_UPDATED));
		assertEquals(model.get(NTERController.PASSWORD_PATTERN),BLAH);
	}
	
	@Test
	public void testFailedUpdate() {
		
		// now have the mock dao fail on user updates, but succeed on password updates
		when(dao.update((User)anyObject())).thenReturn(false);
		when(dao.updatePasswordChangeToken(anyString(), anyString())).thenReturn(true);
		try {
			when(dao.updatePassword(anyString(),anyString())).thenReturn(true);
		} catch (PasswordHistoryException e) {
			e.printStackTrace();
		}
		when(dao.updatePasswordChangeToken(anyString(),anyString())).thenReturn(true);
		ModelAndView mav = pc.changePassword(TestValues.GOOD_PASSWORD, TestValues.GOOD_PASSWORD,
				TestValues.MOCK_USER_NAME, null, null);
		Map<String, Object> model = mav.getModel();
		assertEquals(NTERController.PROFILE_JSP,mav.getViewName());
		assertTrue((Boolean)(mav.getModel().get(NTERController.IS_PASSWORD_UPDATED)));
		assertEquals(model.get(NTERController.PASSWORD_PATTERN),BLAH);
	}
	
	@Test
	public void testEmailVerifyBadToken() {
		ModelAndView mav = pc.emailVerify(TestValues.A_BAD_TOKEN);
		assertEquals(NTERController.VALIDATE_EMAIL_CHANGE_ERROR_JSP,mav.getViewName());
		assertEquals(cp.getUrl(),mav.getModel().get(NTERController.REFERRAL_URL));
	}
	
	@Test
	public void testEmailVerifyGoodToken() {
		ModelAndView mav = pc.emailVerify(TestValues.A_VALID_TOKEN);
		assertEquals(NTERController.VALIDATE_EMAIL_CHANGE_JSP,mav.getViewName());
		assertEquals(TestValues.MOCK_URL,mav.getModel().get(NTERController.REFERRAL_URL));
	}
	
}
