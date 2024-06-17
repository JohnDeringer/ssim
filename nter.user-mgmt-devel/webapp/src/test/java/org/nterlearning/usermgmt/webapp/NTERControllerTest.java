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
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.nterlearning.usermgmt.webapp.NTERController;

/**
 * @author mfrazier
 *
 */
public class NTERControllerTest {

	@Test
	public void testGetBaseURL() {
		String BASE_URL = "http://nterlearning.org";
		String PORT = ":8080";
		String BAD_URL = "garbage";
		
		assertEquals(BASE_URL+PORT,NTERController.getBaseURL(BASE_URL+PORT+"/someurl"));
		
		assertEquals(BASE_URL,NTERController.getBaseURL(BASE_URL+"/someurl"));
		
		assertEquals(BASE_URL,NTERController.getBaseURL(BASE_URL));
		
		assertEquals(BASE_URL+PORT,NTERController.getBaseURL(BASE_URL+PORT));
		
		assertEquals(null,NTERController.getBaseURL(BAD_URL));
	}
	
	@Test
	public void testGetBaseURLNull() {
		assertNull(NTERController.getBaseURL(null));

	}
	
	@Test
	public void testGetBaseURLEmpty() {

		assertNull(NTERController.getBaseURL(""));
	}
	
	@Test
	public void testLinkBuilder()
	{
		NTERController nc = new NTERController();
		String link = nc.buildLinkURL("http://", "my_form", "someToken");
		assertEquals("http://my_form?token=someToken",link);
	}
 
}
