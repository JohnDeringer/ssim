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

import java.util.regex.Pattern;

/**
 * @author mfrazier
 *
 */
public class RFC2822EmailValidator implements EmailValidator {

	
	public static final Pattern rfc2822 = Pattern.compile(
	        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
	);
	
	/* (non-Javadoc)
	 * @see org.nterlearning.usermgmt.common.EmailValidator#validateEmail(java.lang.String)
	 */
	@Override
	public boolean validateEmail(String email) {
		boolean retVal = false;
		
		if (email != null)
		 retVal =  rfc2822.matcher(email).matches();
		
		return retVal;
	}

}
