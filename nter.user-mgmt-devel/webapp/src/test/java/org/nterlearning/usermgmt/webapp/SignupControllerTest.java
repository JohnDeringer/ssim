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
import org.nterlearning.usermgmt.common.EmailValidator;
import org.nterlearning.usermgmt.common.NTEREmailValidator;
import org.nterlearning.usermgmt.common.PasswordValidator;
import org.nterlearning.usermgmt.model.User;
import org.nterlearning.usermgmt.model.UserAccountNotifier;
import org.nterlearning.usermgmt.model.UserImpl;
import org.nterlearning.usermgmt.persistence.UserDao;
import org.nterlearning.usermgmt.webapp.utils.ConfigurableParams;
import org.nterlearning.usermgmt.webapp.utils.ContextLookup;
import org.nterlearning.usermgmt.webapp.utils.NTERResourceBundleMessageSource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration (locations={"classpath:META-INF/testApplicationContext.xml"})
/**
 * @author mfrazier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SignupControllerTest extends Mockito {
	
	private static final String ROY2_HEEHAW_COM = "roy2@heehaw.com";
	private static final String ROY_HEEHAW_COM = "roy@heehaw.com";
	private static final String IN_USE_EMAIL = "roy3@heehaw.com";
	private static final String GOOD_PASSWORD = "Abcd#1234d";
	private static final String BLAH="blah";

	User user;
	
	@InjectMocks
	SignupController sc;
	
	@Mock
	SessionLocaleResolver slr;
	
	@Mock
	ContextLookup cl;
	
	@Mock
	ConfigurableParams cp;
	
	@Mock
	UserDao dao;
	
	@Mock
	UserAccountNotifier uan;
	
	@Mock
	NTERResourceBundleMessageSource rbm;
	
	@Mock
	PasswordValidator pv ;
	EmailValidator ev = new NTEREmailValidator();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
		// set up mock return values
		
		when(cp.getUrl()).thenReturn("http://www.someurl.com");
		
		//when(slr.resolveLocale(null)).thenReturn(new Locale("en","US"));
		
		when(cl.getRequest()).thenReturn(null);
		
		when (pv.getRegexPattern()).thenReturn(BLAH);
		
		// dummy user for mock objects to return
		user = new UserImpl();
		user.setGivenName("roy");
		user.setLastName("owens");
		user.setMailChange(ROY_HEEHAW_COM);
		user.setEmail("roy@nowhere.com");
		user.setHrefRedirect(TestValues.MOCK_URL);
		user.setShadowExpire("0");
		
		// make all dao calls succeed without hitting the database
		when(dao.findByUID( TestValues.MOCK_USER_NAME)).thenReturn(user);
		when(dao.findByEMailChangeToken(TestValues.A_VALID_TOKEN)).thenReturn(user);
		when(dao.findByEMailChange( user.getEmail())).thenReturn(user);
		when(dao.findByEMailChange( user.getMailChange())).thenReturn(user);
		when(dao.emailInUse(IN_USE_EMAIL)).thenReturn(true);
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
		
		// set the mocked up password and email validators
		sc.setPasswordValidator(pv);
		sc.setEmailValidator(ev);
		
		when(rbm.getBasename()).thenReturn("nter_messages");
		
		// make sure that any password string will get validated
		when(pv.validatePassword(anyString())).thenReturn(true);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSignupVerifyBadToken() {
		 ModelAndView mav = sc.signupVerify(TestValues.A_BAD_TOKEN);
		assertEquals(NTERController.VALIDATE_ERROR_JSP,mav.getViewName());
		assertEquals(cp.getUrl(),mav.getModel().get(NTERController.REFERRAL_URL));
	}
	
	@Test
	public void testSignupVerifyGoodToken() {
		 ModelAndView mav = sc.signupVerify(TestValues.A_VALID_TOKEN);
		assertEquals(NTERController.VALIDATE_JSP,mav.getViewName());
		assertEquals(TestValues.MOCK_URL,mav.getModel().get(NTERController.REFERRAL_URL));

        // run the test a second time to ensure the user is still found
        mav = sc.signupVerify(TestValues.A_VALID_TOKEN);
		assertEquals(NTERController.VALIDATE_JSP,mav.getViewName());
		assertEquals(TestValues.MOCK_URL,mav.getModel().get(NTERController.REFERRAL_URL));
	}

	@Test
	public void testHandleRefererParam() {
		ModelAndView mav = sc.handleRefererParam(null);
		 Map<String, Object> model=mav.getModel();
		assertEquals(NTERController.SIGNUP_JSP,mav.getViewName());
		assertEquals(model.get(NTERController.PASSWORD_PATTERN),BLAH);
	}

	@Test
	public void testHandleEmailParam() {
	
		ModelAndView mav = sc.handleEmailParam(TestValues.MOCK_EMAIL);
		assertEquals(NTERController.SUCCESS_SIGNUP_FORM_JSP,mav.getViewName());
		assertTrue(mav.getModel().containsKey(NTERController.EMAIL_ADDRESS));
		assertEquals(TestValues.MOCK_EMAIL,mav.getModel().get(NTERController.EMAIL_ADDRESS));
	}

	@Test
	public void testResubmitNull() {
		ModelAndView mav = sc.resubmit(null,null);
		 Map<String, Object> model=mav.getModel();
		assertEquals(mav.getViewName(),NTERController.SUCCESS_SIGNUP_FORM_JSP);
		model = mav.getModel();
		
		assertEquals(model.get(NTERController.IS_EMAIL_ADDRESS_COMPLETE),false);
		assertEquals(model.get(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE),false);
		assertEquals(model.get(NTERController.IS_EMAIL_WAITING),false);
		assertEquals(model.get(NTERController.EMAIL_ADDRESS),null);
		assertEquals(model.get(NTERController.REFERER),null);
		assertEquals(model.get(NTERController.DEFAULT_REFERER),cp.getUrl());
		
	}
	
	@Test
	public void testResubmitEmpty() {
		ModelAndView mav = sc.resubmit("","");
		assertEquals(mav.getViewName(),NTERController.SUCCESS_SIGNUP_FORM_JSP);
		
		Map<String, Object> model = mav.getModel();
		
		assertEquals(model.get(NTERController.IS_EMAIL_ADDRESS_COMPLETE),false);
		assertEquals(model.get(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE),false);
		assertEquals(model.get(NTERController.IS_EMAIL_WAITING),false);
		assertEquals(model.get(NTERController.EMAIL_ADDRESS),"");
		assertEquals(model.get(NTERController.REFERER),null);
		assertEquals(model.get(NTERController.DEFAULT_REFERER),cp.getUrl());

	}
	
	@Test
	public void testResubmitGoodEmail() {
	
		ModelAndView mav = sc.resubmit(user.getEmail(),user.getEmail());
		assertEquals(mav.getViewName(),NTERController.SUCCESS_SIGNUP_JSP);
		
		Map<String, Object> model = mav.getModel();
		
		assertEquals(model.get(NTERController.IS_EMAIL_ADDRESS_COMPLETE),true);
		assertEquals(model.get(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE),true);
		assertEquals(model.get(NTERController.IS_EMAIL_WAITING),true);
		assertEquals(model.get(NTERController.EMAIL_ADDRESS),user.getEmail());
		assertEquals(model.get(NTERController.REFERER),user.getHrefRedirect());
		assertEquals(model.get(NTERController.DEFAULT_REFERER),cp.getUrl());
	}
	
	@Test
	public void testResubmitBadEmail() {
		ModelAndView mav = sc.resubmit("bademail@nwhere.com","bademail@nwhere.com");
		assertEquals(mav.getViewName(),NTERController.SUCCESS_SIGNUP_FORM_JSP);
		
		Map<String, Object> model = mav.getModel();
		
		assertEquals(model.get(NTERController.IS_EMAIL_ADDRESS_COMPLETE),true);
		assertEquals(model.get(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE),true);
		assertEquals(model.get(NTERController.IS_EMAIL_WAITING),false);
		assertEquals(model.get(NTERController.EMAIL_ADDRESS),"bademail@nwhere.com");
		assertEquals(model.get(NTERController.REFERER),null);
		assertEquals(model.get(NTERController.DEFAULT_REFERER),cp.getUrl());
		
	}
	
	@Test
	public void testResubmitDifferentEmail()
	{
		ModelAndView mav = sc.resubmit(ROY_HEEHAW_COM,ROY_HEEHAW_COM);
		assertEquals(NTERController.SUCCESS_SIGNUP_JSP,mav.getViewName());
		mav = sc.resubmit(ROY2_HEEHAW_COM,ROY_HEEHAW_COM);
		assertEquals(NTERController.SUCCESS_SIGNUP_JSP,mav.getViewName());
		mav = sc.resubmit("",ROY_HEEHAW_COM);
		assertEquals(NTERController.SUCCESS_SIGNUP_FORM_JSP,mav.getViewName());
		assertTrue(mav.getModel().get(NTERController.EMAIL_ADDRESS) != null);
	
	}
	
	@Test
	public void testResubmitDifferentEmailInUse()
	{
		ModelAndView mav = sc.resubmit(IN_USE_EMAIL,ROY_HEEHAW_COM);
		assertEquals(NTERController.SUCCESS_SIGNUP_FORM_JSP,mav.getViewName());
	
	}

	@Test
	public void testSignup() {
		String firstName="nobody";
		String lastName="else";
		String emailAddress="unused@nowhereman.com";
		String password=GOOD_PASSWORD;
		String referer=NTERController.NTERLEARNING_ORG;
		String org="???";
		
		// this will fail on empty firstname
		ModelAndView  mav = sc.signup("", lastName, emailAddress, org, password, password, referer, NTERController.AGREED);
		assertEquals(NTERController.SIGNUP_JSP,mav.getViewName());
		assertEquals(org,mav.getModel().get(NTERController.ORGANIZATION));
		
		// set up to fail based on existing email address
		when(dao.findByEMailChange( TestValues.MOCK_EMAIL)).thenReturn(null);
		when(dao.findByEMail(TestValues.MOCK_EMAIL)).thenReturn(new UserImpl());
		when(dao.create(firstName, lastName, emailAddress, org, password, referer)).thenReturn(user);
		mav = sc.signup(firstName, lastName, TestValues.MOCK_EMAIL, org,password, password, referer, NTERController.AGREED);
		assertEquals(NTERController.SIGNUP_JSP,mav.getViewName());
		
		// setup dao to not find existing user, and create success 
		reset(dao);
		when(dao.findByEMailChange( emailAddress)).thenReturn(null);
		when(dao.findByEMail( emailAddress)).thenReturn(null);
		when(dao.create(firstName, lastName, emailAddress, org, password, referer)).thenReturn(user);
		mav = sc.signup(firstName, lastName, emailAddress, org, password, password, referer, NTERController.AGREED);
		assertEquals(NTERController.SUCCESS_JSP,mav.getViewName());
		
		
		// now set up for a failure to create new user
		when(dao.create(firstName, lastName, emailAddress, org, password, referer)).thenReturn(null);
		mav = sc.signup(firstName, lastName, emailAddress, org, password, password, referer, NTERController.AGREED);
		assertEquals(NTERController.SIGNUP_JSP,mav.getViewName());
		
		// make sure we fail if terms and conditions are not agreed to 
		mav = sc.signup(firstName, lastName, emailAddress, org, password, password, referer, "");
		assertEquals(NTERController.SIGNUP_JSP,mav.getViewName());
		assertEquals(mav.getModel().get(NTERController.PASSWORD_PATTERN),BLAH);

		
		
	}

}
