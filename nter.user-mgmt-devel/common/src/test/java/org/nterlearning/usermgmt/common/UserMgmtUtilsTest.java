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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;

/**
 * @author mfrazier
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserMgmtUtilsTest extends Mockito {

	@Mock
	Resource r;
	
	@Mock
	InputStream is;
	
	@Before
	public void init()
	{
		
	}

	@Test
	public void testGetNewRandomToken() {
		
		for (int i=0;i<10;i++) {
			String one = UserMgmtUtils.getNewRandomToken();
			String two = UserMgmtUtils.getNewRandomToken();
			
			assertFalse(one.equals(two));
		}

	}
	
	@Test
	public void testEncryption()
	{
		
		// do it twice to make sure we get the same results each time.
		
		assertEquals("qvTGHdzF6KLavt4PO0gs2a6pQ00=",UserMgmtUtils.encrypt("hello"));

		assertEquals("qvTGHdzF6KLavt4PO0gs2a6pQ00=",UserMgmtUtils.encrypt("hello"));

	
	}
	
	@Test(expected=NoSuchAlgorithmException.class)
	public void testBadEncAlg() throws NoSuchAlgorithmException, UnsupportedEncodingException
	{

			UserMgmtUtils.encrypt("hello", "barf", "morebarf");
		
	}
	
	@Test(expected=UnsupportedEncodingException.class)
	public void testBadScheme() throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
	
			UserMgmtUtils.encrypt("hello", UserMgmtUtils.SHA, "morebarf");
		
	}
	
	@Test
	public void convertToMap()
	{
		Map<String,String> m = UserMgmtUtils.convertStringToMap("key1=value2,key2=value2,key3=value3");
		assertEquals(3,m.size());
		m = UserMgmtUtils.convertStringToMap("key1=value2,key2=value2,key3=");
		assertEquals(2,m.size());
		m = UserMgmtUtils.convertStringToMap("key1=value2,key2=value2,");
		assertEquals(2,m.size());
		m = UserMgmtUtils.convertStringToMap("key1=value2,key2=value2,key3,=value3");
		assertEquals(2,m.size());
		m = UserMgmtUtils.convertStringToMap("");
		assertEquals(0,m.size());
	}
	
	@Test
	public void testLoadResource()
	{
		reset(r);
		
		try {
			when(r.contentLength()).thenReturn((long)5);
			
			//when(r.getInputStream()).thenReturn(new StringBufferInputStream("hello"));
			when(r.getInputStream()).thenReturn(getStreamFromString("hello"));
			String res = UserMgmtUtils.loadResource(r);
			assertEquals("hello",res);
		} catch (IOException e) {

			e.printStackTrace();
			fail();
		}
		
	}
	
	private InputStream getStreamFromString(String s) {
		byte[] byteArray;
		ByteArrayInputStream retVal=null;
		try {
			byteArray = s.getBytes("ISO-8859-1");
			retVal = new ByteArrayInputStream(byteArray);
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} 
		
		return retVal;
		
	}
	
	@Test
	public void testLoadResourceBadResource()
	{
		String res;
		try {
			res = UserMgmtUtils.loadResource(null);
			assertEquals("",res);
		} catch (IOException e) {
			
			e.printStackTrace();
			fail();
		}
		
		
	}
	
	@Test
	public void testLoadResourceNoContent() {
		reset(r);
		try {
			when(r.contentLength()).thenReturn((long)0);
			//when(r.getInputStream()).thenReturn(new StringBufferInputStream(""));
			when(r.getInputStream()).thenReturn(getStreamFromString(""));
			String res = UserMgmtUtils.loadResource(r);
			assertEquals("",res);
		} catch (IOException e) {
			
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void randomToken()
	{
		String tok = UserMgmtUtils.getNewRandomToken();
		assertNotNull(tok);
		assertTrue(!tok.isEmpty());
	}

}
