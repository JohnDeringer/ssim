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
package org.nterlearning.usermgmt.common;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nterlearning.usermgmt.common.IA5PasswordValidator;
import org.nterlearning.usermgmt.common.PasswordValidator;

/**
 * @author mfrazier
 *
 */
public class IA5PasswordValidatorTest {

	private static final String GOOD_PASSWORD = "Abcd#1234";
	PasswordValidator pv = new IA5PasswordValidator();

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
	
	@Test
	public void nullPassword() {
		assertFalse(pv.validatePassword(null));
		assertTrue(pv.validatePassword(GOOD_PASSWORD));
	}
	
	@Test
	public void emptyPassword() {
		assertFalse(pv.validatePassword(""));
		assertTrue(pv.validatePassword(GOOD_PASSWORD));
	}
	
	@Test
	public void tooShort() {
		assertFalse(pv.validatePassword("122"));
		assertTrue(pv.validatePassword(GOOD_PASSWORD));
	}
	
	@Test 
	public void combination() {
		assertFalse(pv.validatePassword("abcd1234"));
		assertFalse(pv.validatePassword("Abcd1234"));
		assertFalse(pv.validatePassword("abcd#1234"));
		assertTrue(pv.validatePassword(GOOD_PASSWORD));
	}
	
	@Test
	public void specialChars() {
		
		String specialChars = "!\"#$%&'()*+,-./";
		
		String pwd;
		for (int i=0;i<specialChars.length();i++) {
			pwd = "Abcd"+specialChars.charAt(i)+"1234";
			assertTrue(pwd+" failed test",pv.validatePassword(pwd));
		}

		assertFalse(pv.validatePassword("abcd*1234d"));
		assertTrue(pv.validatePassword(GOOD_PASSWORD));
	}
}
