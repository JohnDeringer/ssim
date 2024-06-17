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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.ResourceBundleMessageSource;


/**
 * a class that can lookup the context 
 * @author mfrazier
 *
 */
public interface ContextLookup {
	/**
	 * Finds the user name in the session
	 * @return
	 */
	public abstract String getUserName();
	/**
	 * Gets the remote IP address from the session
	 * @return
	 */
	public abstract String getRemoteIPAddress();
	/**
	 * Gets the current request
	 * @return
	 */
	public abstract HttpServletRequest getRequest();
	/**
	 * Finds a message in a resource bundle
	 * @param rbm ResourceBundleMessageSource
	 * @param msg name of the message we're looking for
	 * @param l Current Locale
	 * @return the content of the message
	 */
	public abstract String getMessage(ResourceBundleMessageSource rbm,String msg,Locale l);

}
