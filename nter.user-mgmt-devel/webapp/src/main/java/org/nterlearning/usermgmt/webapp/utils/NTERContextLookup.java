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
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author mfrazier
 *
 */
public class NTERContextLookup implements ContextLookup {

	Logger log = Logger.getLogger(NTERContextLookup.class);
	
	/* (non-Javadoc)
	 * @see org.nter.usermgmt.webapp.utils.ContextLookup#getUserName()
	 */
	@Override
	public  String getUserName() {
		
		HttpServletRequest req = 
			((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
			.getRequest();

		String userName = "";
		HttpSession session = req.getSession(true);
		userName = (String) session.getAttribute("uid");
		if (userName != null) log.info("Got UID from the SESSION");

		if (userName == null || userName.isEmpty()) {
			userName = (String) req.getAttribute("uid");
			log.info("Got UID from the REQUEST attribute");
		}

		if (userName == null || userName.isEmpty()) {
			userName = (String) req.getParameter("uid");
			log.info("Got UID from the REQUEST parameter");
		}

		if (userName == null || userName.isEmpty()) {
			userName = (String) req.getHeader("uid");
			log.info("Got UID from the REQUEST header");
		}
		if (userName == null || userName.isEmpty()) {
			userName = (String) req.getRemoteUser();
			log.info("Got UID from the REQUEST remote user");
		}

		return userName;
	}
	
	/* (non-Javadoc)
	 * @see org.nter.usermgmt.webapp.utils.ContextLookup#getRemoteIPAddress()
	 */
	@Override
	public String getRemoteIPAddress() {
		String ipAddress = "unknown";
		try {
			HttpServletRequest request = 
			((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
			.getRequest();
			ipAddress=request.getRemoteAddr();
		}
		catch (IllegalStateException ise) {
			
		}
		return ipAddress;
	}
	
	/* (non-Javadoc)
	 * @see org.nter.usermgmt.webapp.utils.ContextLookup#getRequest()
	 */
	@Override
	public HttpServletRequest getRequest()
	{
		HttpServletRequest retVal = null;
		try {
			retVal = 
			((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
			.getRequest();
		
		}
		catch (IllegalStateException ise) {
			
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.nter.usermgmt.webapp.utils.ContextLookup#getMessage(org.springframework.context.support.ResourceBundleMessageSource, java.lang.String, java.util.Locale)
	 */
	@Override
	public String getMessage(ResourceBundleMessageSource rbm,String msg,Locale l) {
		return rbm.getMessage(msg, null, l);
	}


}
