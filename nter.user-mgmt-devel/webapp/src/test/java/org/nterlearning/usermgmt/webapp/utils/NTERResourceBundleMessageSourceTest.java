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
package org.nterlearning.usermgmt.webapp.utils;


import static org.junit.Assert.assertNotNull;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nterlearning.usermgmt.webapp.utils.NTERResourceBundleMessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * @author mfrazier
 *
 */
public class NTERResourceBundleMessageSourceTest {
	static NTERResourceBundleMessageSource bms;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	    bms = new NTERResourceBundleMessageSource();
		bms.setLocation("/Users/fritter63");
		bms.setBasename("nter_messages");
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
	

	@Test(expected=NoSuchMessageException.class)
	public void nullMsg()
	{
		bms.getMessage(null, null, new Locale("en","US"));
	}

	@Test(expected=NoSuchMessageException.class)
	public void emptyMsg()
	{
		bms.getMessage("", null, new Locale("en","US"));

	}
	
	//@Ignore
	@Test
	public void badLocale()
	{

		assertNotNull(bms.getMessage("password.invalid.error.msg", null, null));

		assertNotNull(bms.getMessage("password.invalid.error.msg", null, new Locale("sp","US")));
	}
	//@Ignore
	@Test
	public void goodData()
	{

		assertNotNull(bms.getMessage("password.invalid.error.msg", null, new Locale("en","US")));
		
		 assertNotNull(bms.getMessage("mail.verify.subject", null, new Locale("en","US")));

	}
	
	//@Ignore
	@Test
	public void noAltLocation()
	{
		 bms = new NTERResourceBundleMessageSource();
			bms.setLocation("");
			bms.setBasename("nter_messages");

		assertNotNull(bms.getMessage("password.invalid.error.msg", null, new Locale("en","US")));
	}
	
	@Test
	public void testController()
	{
		NTERResourceBundleMessageSource n = new NTERResourceBundleMessageSource();
		ResourceBundle rb = ResourceBundle.getBundle("nter_messages", new Locale("en","US"),n.new ExternalBundleLoader());
		assertNotNull(rb);

		assertNotNull(rb.getString("password.invalid.error.msg"));
	}

}
