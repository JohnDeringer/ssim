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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;

/**
 * @author mfrazier
 * 
 * General utility methods
 *
 */
public class UserMgmtUtils {
	private static final String NOT_FOUND_404 = "<title>404 Not Found</title>";

	private static Logger log = Logger.getLogger(UserMgmtUtils.class);
	
	// I can't believe these aren't defined in java.security.....
	public static final String UTF_8 = "UTF-8";
	public static final String SHA = "SHA";
	
	private static Base64 encoder = new Base64();
	private static ResourceController resourceController = new ResourceController();

	/**
	 * Gets a random string token
	 * @return String containing a new random token
	 */
	public static String getNewRandomToken()
	{
		return  UUID.randomUUID().toString();
	}
	
	/**
	 * Parses a string of comma delimited pairs into a Map for convenience
	 * @param KVPairs - the String of key value pairs in the for "key=value,..."
	 * @return A map of the pairs
	 */
	public static Map<String,String> convertStringToMap(String KVPairs)
	{
		 HashMap<String,String> retVal = new HashMap<String,String>();
		
		// Assume the format key1=value1,key2=value2 ....
		String[] pairs = KVPairs.split(",");
		
		// Iterate backwards over the list of pairs
		for (int count=pairs.length-1; count >= 0; count--)
		{
			// Split out the key and value pairs
			String[] splitPair = pairs[count].split("=");
			
			// check for formatting, add to map if all is OK
			if(splitPair.length >= 2 && !splitPair[0].isEmpty() && !splitPair[1].isEmpty())
			{
				retVal.put(splitPair[0].trim(), splitPair[1].trim());
			}

		}
		
		return retVal;
	}
	
	/**
	 * @return a random user id
	 */
	public static String autoGenerateUserId()
	   {		   
		   return getNewRandomToken();

	   }
	
	
	
    /**
     * Convenience method to encrypt a string using the SHA algorithm
     * @param plaintext - text to be encrypted
     * @return encrypted value
     */
    public static String encrypt(final String plaintext)  {
    	String retVal = plaintext;
    	try {
			return encrypt(plaintext,SHA,UTF_8);
		} catch (NoSuchAlgorithmException e) {
			log.error("Failed to encrypt string",e);
		} catch (UnsupportedEncodingException e) {
			log.error("Failed to encrypt string",e);
		}
		return retVal;
    }
    
    /**
     * Encrypt a string with the specified algorithm and encoding
     * @param plaintext - string to encrypt
     * @param algorithm - encryption algorithm
     * @param encoding - encoding scheme
     * @return encrypted value
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(final String plaintext,String algorithm,String encoding) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    	
    	String hash=null;

		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(plaintext.getBytes(encoding));
			
		// Original version used non-plubic API from sun.misc.BASE64Encoder!
	    // hash = (new BASE64Encoder()).encode(md.digest());
		
		hash = encoder.encodeAsString(md.digest());
    	        
    	
    	return hash;
    }	
    
	/**
	 * Loads a reource stream into a String
	 * @param templateResource the resource to load
	 * @return a string the full content of the resource
	 * @throws IOException
	 */
	public static String loadResource(Resource templateResource) throws IOException {		
		String retVal = "";
	
		if (templateResource != null) {
			byte[] bytes = new byte[(int) templateResource.contentLength()];
			templateResource.getInputStream().read(bytes);
			retVal =  new String(bytes, "UTF-8");
		}
		
		return retVal;
	}
	
	/**
	 * Finds a resource that can be overriden by a user specified location. Checks the default classpath
	 * location if nothing is found in the user specified location.
	 * @param extDir Directory to check for overrides
	 * @param basename base name of the resource
	 * @param ext extension of the resource
	 * @param l Locale use to find the name of the resource
	 * @return
	 */
	public static Resource getOverideableResource(String extDir, String basename, String ext, Locale l) {
		LocalizedResourceHelper lr = new LocalizedResourceHelper();
		Resource retVal = null;
		
		// If user has specified a location, check there first using standard localization search pattern
		if (extDir !=null) {
			retVal = lr.findLocalizedResource("file:"+extDir+"/"+basename, ext, l);
			
		}
		
		// if not found yet, look on the classpath
		if (retVal != null && !retVal.exists()) {
			retVal = lr.findLocalizedResource(basename, ext, l);
		}
		
		// For some reason, Spring api returns an object even if resource not found, have to use "exists()" method
		if (retVal !=null && !retVal.exists()) retVal = null;
		
		return retVal;
	}
	
	public static String resolveRemoteResource(String baseURL, String basename, Locale locale,
			String ext) {
		String retVal = null;
		//String baseURL="/";
		List<Locale> myLocales = resourceController.getCandidateLocales(
				basename, locale);
		for (Locale l : myLocales) {

			String fileName;
			if (!l.toString().equals("")) {
				fileName = basename + "_" + l.toString() + ext;
			} else {
				fileName = basename + ext;
			}
			
			String fullURL = baseURL + "/"+fileName;
			log.debug("checking for:" + fullURL);
			try {
				InputStream is = (new URL(fullURL)).openStream();
				

				if (is != null) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is));

					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line);	
					}
					reader.close();

					if (!(sb.toString().contains(NOT_FOUND_404)))
					{
						return fileName;
					}
					
				}

			} catch (MalformedURLException e) {
				
			} catch (IOException e) {
				//log.info(fileName+"not found");
			}

		}

		return retVal;

	}
	
	protected static class ResourceController extends ResourceBundle.Control {
		public ResourceController()
		{
			super();
		}
		
		public List<Locale> getCandidateLocales(String basename, Locale locale) {
			return super.getCandidateLocales(basename, locale);
		}
	}
		

}
