	/*
	 * National Training and Education Resource (NTER) Copyright (C) 2012 SRI
	 * International
	 * 
	 * This program is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License as published by the
	 * Free Software Foundation; either version 2 of the License, or (at your
	 * option) any later version.
	 * 
	 * This program is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
	 * Public License for more details.
	 * 
	 * You should have received a copy of the GNU General Public License along
	 * with this program; if not, write to the Free Software Foundation, Inc.,
	 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
	 */
package org.nterlearning.usermgmt.webapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * adds to the default behavior of searching the classpath by adding external directories to search for resources
 * @author mfrazier
 *
 */
public class NTERResourceBundleMessageSource extends
		ResourceBundleMessageSource {

	Logger log = Logger.getLogger(NTERResourceBundleMessageSource.class);

	private String location = null;
	private String basename = null;

	@Override
	protected ResourceBundle doGetBundle(String baseName, Locale locale) {
		return ResourceBundle.getBundle(baseName, locale,
				new ExternalBundleLoader());
	}

	public void setLocation(String location) {
		this.location = location;
	}


	public String getLocation() {
		return location;
	}

	public void setBasename(String basename) {
		super.setBasename(basename);
		this.basename = basename;
	}

	public String getBasename() {
		return basename;
	}

	/**
	 * extend and override the newBundle method for own special purposes
	 * @author mfrazier
	 *
	 */
	public class ExternalBundleLoader extends ResourceBundle.Control {
		private ResourceBundle lastFound;

		public ExternalBundleLoader() {
			lastFound = null;
		}

		/* (non-Javadoc)
		 * @see java.util.ResourceBundle.Control#newBundle(java.lang.String, java.util.Locale, java.lang.String, java.lang.ClassLoader, boolean)
		 */
		@Override
		public ResourceBundle newBundle(String baseName, Locale locale,
				String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException,
				IOException {

			ResourceBundle retVal = null;

			if (baseName != null && locale != null && format != null
					&& loader != null) {

				// First, go through through the default classpath lookup
				retVal = super.newBundle(baseName, locale, format, loader,
						reload);
				// System.out.println("Classpath("+locale+"):"+(bundle == null ?
				// "-------":"FOUND"));
				if (retVal != null) {
					lastFound = retVal;
				}

				// Now, see if we can find it on our external path
				
				// make sure conditions are right for this, only on java.properties and if we have
				// a location set
				if (format.equals("java.properties") && location != null) {
					String resourceName = toResourceName(
							toBundleName(baseName, locale), "properties");
					log.debug("Checking for:" + resourceName);

					// build a File reference based on locale and resource, check to see if 
					// it exists
					File f = new File(location, resourceName);
					if (f.exists()) {

						// it DOES exist, so getan input stream and create a new resource bundle
						InputStreamReader bis = new InputStreamReader(
								new FileInputStream(f), "UTF-8");

						NTERResourceBundle overrideBundle = new NTERResourceBundle(
								bis);

						// if already have a bundle, set it as this bundles parent.
						if (lastFound != null) {
							overrideBundle.setParent(lastFound);
						}
						
						retVal = overrideBundle;
						bis.close();

					} else
						log.debug(resourceName + " not found");
					// System.out.println("Override("+locale+"):"+(retVal ==
					// null ? "-------":"FOUND"));
				}

			}
			return retVal;
		}
	}

	/**
	 * Our own extension of the RersourceBundle to get at some protected apis.
	 * @author mfrazier
	 *
	 */
	class NTERResourceBundle extends ResourceBundle {
		private Properties props;

		NTERResourceBundle(InputStreamReader stream) throws IOException {
			props = new Properties();
			props.load(stream);

		}

		@Override
		protected Object handleGetObject(String key) {
			return props.getProperty(key);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Enumeration<String> getKeys() {

			return (Enumeration<String>) props.propertyNames();

		}

		/* 
		 * We do this so that we can call setParent on the extended ResourceBundle
		 */
		@Override
		public void setParent(ResourceBundle rb) {
			super.setParent(rb);
		}

	}

}
