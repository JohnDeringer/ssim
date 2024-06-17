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


// (a) Enforces minimum password complexity of mix of at least 8 characters, 
// mix of upper-case, lower-case, numerical, and special characters (at least one of each category);
/**
 * @author fritter63
 *
 */
public class IA5PasswordValidator implements PasswordValidator {
	
	public IA5PasswordValidator() {
	
	}
	
	// (?=.{8,}) at least 8 chars in length
	// (?=.*\\d) at least one digit
	// (?=.*[a-z]) at least one lower case letter
	// (?=.*[A-Z]) at least one upper case letter
	// (?=.*[!\"#$%&'()*+,-./]) at least one of these special characters
	String passwordPattern = "^.*(?=.{8,})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-./]).*$";

	/* (non-Javadoc)
	 * @see org.nterlearning.usermgmt.common.PasswordValidator#validatePassword(java.lang.String)
	 */
	@Override
	public boolean validatePassword(String newPassword) {
		boolean retVal = false;
		
		if (newPassword != null)
		{
			//retVal = newPassword.matches(passwordPattern) ? newPassword.matches(specialCharPattern):false;
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
