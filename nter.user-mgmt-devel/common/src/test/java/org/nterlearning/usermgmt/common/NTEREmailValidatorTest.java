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
import org.nterlearning.usermgmt.common.NTEREmailValidator;

/**
 * @author mfrazier
 *
 */
public class NTEREmailValidatorTest extends Assert{

	private static NTEREmailValidator ev;

	@Before
	public void init()
	{
		 ev = new NTEREmailValidator();
	}
	
	@Test
	public void goodPattern() {
		assertTrue(ev.validateEmail("man@nowhere.com"));

	}
	
	@Test
	public void goodPatternMixed() {
		assertTrue(ev.validateEmail("ManAbc@nowhere.com"));
		assertTrue(ev.validateEmail("ManAbcD@nowhere.com"));
		assertTrue(ev.validateEmail("ManAbcD@nowhere.Com"));
		
		assertTrue(ev.validateEmail("1happyYMarkTest@flO0ber.cOm"));
		assertTrue(ev.validateEmail("1happyYMarkTest@flO0ber.cOM"));
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
}
