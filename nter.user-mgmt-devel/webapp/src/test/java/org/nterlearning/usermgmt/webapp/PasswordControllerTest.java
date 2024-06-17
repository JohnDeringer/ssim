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
import static org.junit.Assert.assertNotSame;

import java.util.Locale;
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
import org.nterlearning.usermgmt.model.UserAccountNotifier;
import org.nterlearning.usermgmt.model.UserImpl;
import org.nterlearning.usermgmt.persistence.UserDao;
import org.nterlearning.usermgmt.webapp.NTERController;
import org.nterlearning.usermgmt.webapp.PasswordController;
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
public class PasswordControllerTest extends Mockito {
	
	private static final String EMPTY_PASSWORD = "";
	private static final String SHORT_PASSWORD = "abcd";
	private static final String GOOD_PASSWORD = "abcd#1234d";
	private String baseReferer="http://www.nterlearning.org:80";
	private String myReferer=baseReferer+"/yadayadayada";
	
	ModelAndView mav;
	
	@InjectMocks
	PasswordController pc ;
	
	@Mock
	SessionLocaleResolver lr;
	
	@Mock
	ContextLookup cl;
	
	@Mock
	UserAccountNotifier uan;
	
	@Mock
	UserDao dao;
	
	EmailValidator ev;
	
	@Mock
	ConfigurableParams cp;
	
	@Mock
	NTERResourceBundleMessageSource rbm;
	
	@Mock
	PasswordValidator pvReal;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
			
		// setup mock objects for different conditions
		MockitoAnnotations.initMocks(this);
		
		// use en_US locale for all the tests
		Locale l = new Locale("en","US");
		
		// set confirgurable params object to return mocked URLs
		when(cp.getLoginUrl()).thenReturn(TestValues.MOCK_LOGIN);
		when(cp.getUrl()).thenReturn(TestValues.MOCK_URL);
		
		// when local is requested by the LocaleResolver, return our default locale
		when(lr.resolveLocale(null)).thenReturn(l);
		
		// always return a dummy string when the contexst lookup object is used
		when(cl.getMessage(rbm,NTERController.MAIL_PASSWORD_RESET_SUBJECT,l)).thenReturn("hello!");
		when(cl.getMessage(rbm,NTERController.MAIL_VERIFY_SUBJECT,l)).thenReturn("hello!");
		
		// create a dummy User and aset the HrefRedirect field
		UserImpl ui = new UserImpl();
		ui.setHrefRedirect(baseReferer);
		
		// return the fake user if the email matches the MOCK email
		when(dao.findByEMail( TestValues.MOCK_EMAIL)).thenReturn(ui);
		
		// if bad token is passed to dao, return null
		when(dao.findByPasswordChangeToken( TestValues.A_BAD_TOKEN)).thenReturn(null);
		// on good token, return fake user
		when(dao.findByPasswordChangeToken( TestValues.A_VALID_TOKEN)).thenReturn(ui);
		
		// mock out all the dao update calls to do nothing but return true
		when(dao.update(ui)).thenReturn(true);
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
		
		// dummy up the email validator in the passwordController
		ev = new NTEREmailValidator();
		pc.setPasswordValidator(pvReal);
		pc.setEmailValidator(ev);
		
		// mock out the password validation calls
		when(pvReal.validatePassword(GOOD_PASSWORD)).thenReturn(true);
		when(pvReal.validatePassword(SHORT_PASSWORD)).thenReturn(false);
		when(pvReal.validatePassword(EMPTY_PASSWORD)).thenReturn(false);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testdoGetNoToken() {

		mav = pc.doGet(null, myReferer);
		assertEquals(NTERController.FORGOT_PASSWORD_JSP,mav.getViewName());
		Map<String,Object> model = mav.getModel();
		assertEquals(baseReferer,model.get(NTERController.REFERER));
		assertEquals(false,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(null,model.get(NTERController.TOKEN));
		assertEquals(cp.getUrl(),model.get(NTERController.DEFAULT_REFERER));
	}
	
	@Test
	public void testdoGetEmptyToken()
	{
		
		mav = pc.doGet(EMPTY_PASSWORD, myReferer);
		assertEquals(NTERController.FORGOT_PASSWORD_JSP,mav.getViewName());
	    Map<String,Object> model = mav.getModel();
		assertEquals(baseReferer,model.get(NTERController.REFERER));
		assertEquals(false,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(EMPTY_PASSWORD,model.get(NTERController.TOKEN));
		assertNotSame(null,model.get(NTERController.DEFAULT_REFERER));
	}
	
	@Test
	public void testdoGetBadToken()
	{
		
		
		mav = pc.doGet(TestValues.A_BAD_TOKEN, myReferer);
		assertEquals(NTERController.FORGOT_PASSWORD_JSP,mav.getViewName());
		Map<String,Object> model=mav.getModel();
		assertEquals(myReferer,(model.get(NTERController.REFERER)));
		assertEquals(false,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(TestValues.A_BAD_TOKEN,model.get(NTERController.TOKEN));
		assertNotSame(null,model.get(NTERController.DEFAULT_REFERER));
	}
	
	@Test
	public void testdoGetValidToken()
	{
		
		mav = pc.doGet(TestValues.A_VALID_TOKEN, myReferer);
		assertEquals(NTERController.PASSWORD_RESET_FORM_JSP,mav.getViewName());
		Map<String,Object> model=mav.getModel();
		assertEquals(baseReferer,(model.get(NTERController.REFERER)));
		assertEquals(true,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(TestValues.A_VALID_TOKEN,model.get(NTERController.TOKEN));
		assertNotSame(null,model.get(NTERController.DEFAULT_REFERER));
	}
	
	@Test
	public void testForgotPasswordNoEmail()
	{
		
		ModelAndView mav = pc.forgotPassword(null, TestValues.MOCK_URL);
		Map<String,Object> model = mav.getModel();
		assertEquals(NTERController.FORGOT_PASSWORD_JSP,mav.getViewName());
		assertEquals(null,model.get(NTERController.EMAIL_ADDRESS));
		assertEquals(false,model.get(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE));
		assertEquals(false,model.get(NTERController.IS_EMAIL_ADDRESS_COMPLETE));
		assertEquals(false,model.get(NTERController.IS_USER_WITH_EMAIL));
		assertNotSame(null,model.get(NTERController.DEFAULT_REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.REFERER));
		
	}
	@Test
	public void testForgotPasswordBadEmail()
	{
		String badEmail = "blahblah";
		
		ModelAndView mav = pc.forgotPassword(badEmail, TestValues.MOCK_URL);
		Map<String, Object> model = mav.getModel();
		assertEquals(NTERController.FORGOT_PASSWORD_JSP,mav.getViewName());
		
		assertEquals(badEmail,model.get(NTERController.EMAIL_ADDRESS));
		assertEquals(false,model.get(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE));
		assertEquals(true,model.get(NTERController.IS_EMAIL_ADDRESS_COMPLETE));
		assertEquals(false,model.get(NTERController.IS_USER_WITH_EMAIL));
		assertNotSame(null,model.get(NTERController.DEFAULT_REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.REFERER));
	}
	
	@Test
	public void testForgotPasswordGoodEmail()
	{
		
		ModelAndView mav = pc.forgotPassword(TestValues.MOCK_EMAIL, TestValues.MOCK_URL);
		Map<String,Object> model = mav.getModel();
		assertEquals(NTERController.FORGOT_PASSWORD_SUCCESS_JSP,mav.getViewName());
		
		assertEquals(TestValues.MOCK_EMAIL,model.get(NTERController.EMAIL_ADDRESS));
		assertEquals(true,model.get(NTERController.IS_VALID_EMAIL_ADDRESS_COMPLETE));
		assertEquals(true,model.get(NTERController.IS_EMAIL_ADDRESS_COMPLETE));
		assertEquals(true,model.get(NTERController.IS_USER_WITH_EMAIL));
		assertNotSame(null,model.get(NTERController.DEFAULT_REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.REFERER));
	}
	
	Map<String,Object> model;
	
	@Test
	public void testResetPasswordNoToken() {
		mav = pc.resetPassword(EMPTY_PASSWORD, EMPTY_PASSWORD, null);
		assertEquals(NTERController.PASSWORD_RESET_FORM_JSP,mav.getViewName());
		model = mav.getModel();
		
		assertEquals(false,model.get(NTERController.IS_PASSWORD_CONFIRM));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_UPDATED));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_VALID));
		assertEquals(false,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(null,model.get(NTERController.TOKEN));
		assertEquals(null,model.get(NTERController.REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.DEFAULT_REFERER));
		
	}
	
	@Test 
	public void testResetPasswordEmptyToken() {
		mav = pc.resetPassword(EMPTY_PASSWORD, EMPTY_PASSWORD, EMPTY_PASSWORD);
		assertEquals(NTERController.PASSWORD_RESET_FORM_JSP,mav.getViewName());
		
		model = mav.getModel();
		
		assertEquals(false,model.get(NTERController.IS_PASSWORD_CONFIRM));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_UPDATED));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_VALID));
		assertEquals(false,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(EMPTY_PASSWORD,model.get(NTERController.TOKEN));
		assertEquals(null,model.get(NTERController.REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.DEFAULT_REFERER));
	}
	
	@Test
	public void testResetPasswordNoPassword()
	{
		ModelAndView mav = pc.resetPassword(null, null, TestValues.A_VALID_TOKEN);
		assertEquals(NTERController.PASSWORD_RESET_FORM_JSP,mav.getViewName());

		model = mav.getModel();
		
		assertEquals(false,model.get(NTERController.IS_PASSWORD_CONFIRM));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_UPDATED));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_VALID));
		assertEquals(true,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(TestValues.A_VALID_TOKEN,model.get(NTERController.TOKEN));
		assertEquals(baseReferer,model.get(NTERController.REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.DEFAULT_REFERER));
	}
	
	@Test
	public void testResetPasswordEmptyPassword()
	{
		mav = pc.resetPassword(EMPTY_PASSWORD, null, TestValues.A_VALID_TOKEN);
		assertEquals(NTERController.PASSWORD_RESET_FORM_JSP,mav.getViewName());
		
		model = mav.getModel();
		
		assertEquals(false,model.get(NTERController.IS_PASSWORD_CONFIRM));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_UPDATED));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_VALID));
		assertEquals(true,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(TestValues.A_VALID_TOKEN,model.get(NTERController.TOKEN));
		assertEquals(this.baseReferer,model.get(NTERController.REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.DEFAULT_REFERER));
	}
	
	@Test 
	public void testResetPasswordNotEqual(){
		ModelAndView mav = pc.resetPassword(GOOD_PASSWORD, SHORT_PASSWORD, TestValues.A_VALID_TOKEN);
		assertEquals(NTERController.PASSWORD_RESET_FORM_JSP,mav.getViewName());
		assertEquals(false,mav.getModel().get(NTERController.IS_PASSWORD_CONFIRM));
		
		model = mav.getModel();
		
		assertEquals(false,model.get(NTERController.IS_PASSWORD_CONFIRM));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_UPDATED));
		assertEquals(true,model.get(NTERController.IS_PASSWORD_VALID));
		assertEquals(true,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(TestValues.A_VALID_TOKEN,model.get(NTERController.TOKEN));
		assertEquals(this.baseReferer,model.get(NTERController.REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.DEFAULT_REFERER));
	}
	
	
	@Test 
	public void testResetPasswordTooShort(){
		ModelAndView mav = pc.resetPassword(SHORT_PASSWORD, SHORT_PASSWORD, TestValues.A_VALID_TOKEN);
		assertEquals(NTERController.PASSWORD_RESET_FORM_JSP,mav.getViewName());
		assertEquals(false,mav.getModel().get(NTERController.IS_PASSWORD_VALID));
		
		model = mav.getModel();
		
		assertEquals(false,model.get(NTERController.IS_PASSWORD_CONFIRM));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_UPDATED));
		assertEquals(false,model.get(NTERController.IS_PASSWORD_VALID));
		assertEquals(true,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(TestValues.A_VALID_TOKEN,model.get(NTERController.TOKEN));
		assertEquals(this.baseReferer,model.get(NTERController.REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.DEFAULT_REFERER));
	}
	
	@Test 
	public void testResetPasswordAllGood(){

		ModelAndView mav = pc.resetPassword(GOOD_PASSWORD, GOOD_PASSWORD,  TestValues.A_VALID_TOKEN);
		assertEquals(NTERController.PASSWORD_RESET_FORM_SUCCESS_JSP,mav.getViewName());
		
		model = mav.getModel();
		
		assertEquals(true,model.get(NTERController.IS_PASSWORD_CONFIRM));
		assertEquals(true,model.get(NTERController.IS_PASSWORD_UPDATED));
		assertEquals(true,model.get(NTERController.IS_PASSWORD_VALID));
		assertEquals(true,model.get(NTERController.IS_VALID_TOKEN));
		assertEquals(TestValues.A_VALID_TOKEN,model.get(NTERController.TOKEN));
		assertEquals(this.baseReferer,model.get(NTERController.REFERER));
		assertEquals(TestValues.MOCK_URL,model.get(NTERController.DEFAULT_REFERER));
	}

}
