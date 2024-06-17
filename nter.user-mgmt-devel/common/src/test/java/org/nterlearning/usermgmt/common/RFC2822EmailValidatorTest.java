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


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nterlearning.usermgmt.common.RFC2822EmailValidator;

/**
 * @author mfrazier
 *
 */
public class RFC2822EmailValidatorTest extends Assert{

	private static RFC2822EmailValidator ev;

	@Before
	public void init() {
		 ev = new RFC2822EmailValidator();
	}
	
	@Test
	public void goodPattern() {
		assertTrue(ev.validateEmail("man@nowhere.com"));
	}
	
	@Test
	public void badPatternMixed() {
		assertFalse(ev.validateEmail("1happyYMarkTest@flO0ber.cOM"));
	}
	
	@Test
	public void userNameOnly() {
		assertFalse(ev.validateEmail("man"));
	}
	
	@Test
	public void domainOnly() {
		assertFalse(ev.validateEmail("nowhere.com"));
	}
	
	@Test 
	public void userOmitted() {
		assertFalse(ev.validateEmail("@nowhere.com"));
	}
	
	@Test
	public void domainOmitted() {
		assertFalse(ev.validateEmail("man@"));
	}
	
	@Test
	public void nullEmail() {
		assertFalse(ev.validateEmail(null));
	}
}
