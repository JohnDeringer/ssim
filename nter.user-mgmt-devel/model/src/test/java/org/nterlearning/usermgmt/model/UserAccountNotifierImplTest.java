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
package org.nterlearning.usermgmt.model;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.nterlearning.usermgmt.model.UserAccountNotifierImpl;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@RunWith(MockitoJUnitRunner.class)
public class UserAccountNotifierImplTest extends Mockito {
	String templateResource="NewUserEmailTemplate";

	@Mock
	private JavaMailSender jms;

	@Mock
	private SimpleMailMessage smm;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		// mock action for the mail sender
		when(jms.createMimeMessage()).thenReturn(
				(new JavaMailSenderImpl()).createMimeMessage());

		when(smm.getFrom()).thenReturn("nowhere@nowhereman.com");
		when(smm.getSubject()).thenReturn("No Subject");

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetNewUserEmailTemplate() {
		try {
			UserAccountNotifierImpl u = new UserAccountNotifierImpl();
			u.setNewUserEmailTemplateName(templateResource);
			String et = u.getNewUserEmailTemplate(new Locale("en","US"));
			assertNotNull(et);
		} catch (IOException e) {
			Assert.fail(e.toString());
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNoNewUserEmailTemplate() {

		try {
			when((mock(Resource.class)).getInputStream()).thenReturn(null);
			UserAccountNotifierImpl u = new UserAccountNotifierImpl();
			Assert.assertNull(u.getNewUserEmailTemplate(new Locale("en","US")));
		} catch (IOException e) {
			Assert.fail(e.toString());
		}

	}

	@Test
	public void testGetPasswordResetTemplate() {
		try {
			UserAccountNotifierImpl u = new UserAccountNotifierImpl();
			u.setPasswordResetTemplateName(templateResource);
			String et = u.getPasswordResetTemplate(new Locale("en","US"));
			assertNotNull(et);
		
		} catch (IOException e) {
			Assert.fail(e.toString());
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNoPasswordResetTemplate() {

		try {
			when((mock(Resource.class)).getInputStream()).thenReturn(null);
			UserAccountNotifierImpl u = new UserAccountNotifierImpl();
			Assert.assertNull(u.getPasswordResetTemplate(new Locale("en","US")));
		} catch (IOException e) {
			Assert.fail(e.toString());
		}

	}

	@Test
	public void testSendNewUserConfirmationEmail() {
		try {

			UserAccountNotifierImpl u = new UserAccountNotifierImpl();
			u.setMailSender(jms);
			u.setNewUserEmailTemplateName(templateResource);
			u.setGenericMailWithSubject(smm);
			u.sendNewUserConfirmationEmail("hey!","boogety", "bogey", "",new Locale("en","US"));
		} catch (IOException e) {
			Assert.fail(e.toString());
		}
	}

	@Test
	public void testSendProfileChangeConfirmationEmail() {
		try {

			UserAccountNotifierImpl u = new UserAccountNotifierImpl();
			u.setMailSender(jms);
			u.setEmailChangeTemplateName("EmailChangeTemplate");
			u.setGenericMailWithSubject(smm);
			u.sendProfileChangeConfirmationEmail("hey!","boogety", "bogey", "",new Locale("en","US"));
		} catch (IOException e) {
			Assert.fail(e.toString());
		}
	}

	@Test
	public void testSendResetPasswordConfirmationEmail() {
		try {

			UserAccountNotifierImpl u = new UserAccountNotifierImpl();
			u.setMailSender(jms);
			u.setPasswordResetTemplateName(templateResource);
			u.setGenericMailWithSubject(smm);
			u.sendResetPasswordConfirmationEmail("hey!","boogety", "bogey", "",new Locale("en","US"));
					
		} catch (IOException e) {
			Assert.fail(e.toString());
		}
	}

}
