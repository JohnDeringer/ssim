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

package org.nterlearning.metadata;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations={"classpath:META-INF/cxf_beans.xml"})
public class ShibMetaDataAdderTest extends Assert {

	private static final String TEMPLATE_FILE = "src/test/resources/federation-nterlearning.xml";
	private static final String SOME_HOST = "some-host";
	@Resource
	private ShibMetaDataAdder smd;
	private static final String workFile = "target/federation-nterlearning.xml";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// overriding any config for the file location so it stores locally
		File f = new File(TEMPLATE_FILE);
		
		if (f.exists()) {

			FileUtils.copyFile(f,new File(workFile));
		}
		
		smd.setConfigFileLocation(workFile);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void handleNullContent()
	{
		smd.setConfigFileLocation(workFile);
		assertFalse(smd.addMetaData(null));
	}
	
	@Test 
	public void handleEmtpyContent()
	{
		smd.setConfigFileLocation(workFile);
		assertFalse(smd.addMetaData(""));
	}

	
	
	@Test
	public void handleGoodData()
	{
		//for (int i=0;i<20;i++) {
		smd.setConfigFileLocation(workFile);
			assertTrue(smd.addMetaData("<md:EntityDescriptor entityID=\""+SOME_HOST+"\" xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\">unit test data</md:EntityDescriptor>"));
	
		//}
	}
	
	
	@Test
	public void remove()
	{	
		smd.setConfigFileLocation("src/test/resources/federation-nterlearning-delete.xml");
		assertTrue(smd.removeMetaDataElement("ec2-107-22-141-139.compute-1.amazonaws.com",false));
		assertFalse(smd.removeMetaDataElement("ec2-106-22-141-139.compute-1.amazonaws.com",false));
		
	}
	
	
}
