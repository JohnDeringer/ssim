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

/**
 * @author mfrazier
 *
 */
public class SimplePasswordValidator implements PasswordValidator {
	
	public SimplePasswordValidator() {
	
	}
	
	// Example: pattern below requires:
	// - minimum 8 chars
	// - combination of the letters and digits
	// - no digits in the first or last position
	// - at least one special character
	// full regex pattern = "^.*(?=.{8,})(?=.*\\d)(?=.*[a-z|A-Z])(?=^[^0-9].*[^0-9]$)(?=.*[@#$%^&+=]).*$";
	
	String passwordPattern = "^.*(?=.{8,}).*$";

	/* (non-Javadoc)
	 * @see org.nterlearning.usermgmt.common.PasswordValidator#validatePassword(java.lang.String)
	 */
	@Override
	public boolean validatePassword(String newPassword) {
		boolean retVal = false;
		
		if (newPassword != null)
		{
			retVal = newPassword.matches(passwordPattern);
		}
		
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.nterlearning.usermgmt.common.PasswordValidator#getRegexPattern()
	 */
	@Override
	public String getRegexPattern() {
		return passwordPattern;
	}

}
