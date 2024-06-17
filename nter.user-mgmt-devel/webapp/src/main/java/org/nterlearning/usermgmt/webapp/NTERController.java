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
package org.nterlearning.usermgmt.webapp;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class NTERController  {

	private static final String SSO_LOGIN_TARGET = "/Shibboleth.sso/Login?target=";

	private static final String NTER_USER_MGMT_WEBAPP = "/nter-user-mgmt-webapp/";
	
	public static final String MAIL_VERIFY_SUBJECT = "mail.verify.subject";
	public static final String MAIL_PASSWORD_RESET_SUBJECT = "mail.password.reset.subject";
	public static final String SUPPORT_EMAIL = "supportEmail";
	
	Logger log = Logger.getLogger(NTERController.class);
	
	public static final String AGREED = "agreed";
	// Validation parameters
	public static final String IS_PASSWORD_VALID = "isPasswordValid";
	public static final String IS_PASSWORD_CONFIRM = "isPasswordConfirm";
	public static final String IS_VALID_EMAIL_ADDRESS_COMPLETE = "isValidEmailAddressComplete";
	public static final String IS_EMAIL_ADDRESS_COMPLETE = "isEmailAddressComplete";
	public static final String IS_LAST_NAME_COMPLETE = "isLastNameComplete";
	public static final String IS_FIRST_NAME_COMPLETE = "isFirstNameComplete";
	public static final String IS_EMAIL_WAITING = "isEmailWaiting";
	public static final String IS_VALID_TOKEN = "isValidToken";
	public static final String IS_PASSWORD_UPDATED = "isPasswordUpdated";
	public static final String IS_USER_WITH_EMAIL = "isUserWithEmail";
	public static final String IS_EMAIL_ADDRESS_UPDATED = "isEmailAddressUpdated";
	public static final String IS_LAST_NAME_UPDATED = "isLastNameUpdated";
	public static final String IS_FIRST_NAME_UPDATED = "isFirstNameUpdated";
	public static final String IS_EMAIL_IN_USE = "isEmailInUse";
	public static final String IS_EDIT_FORM = "isEditForm";
	public static final String IS_PASSWORD_FORM = "isPasswordForm";
	public static final String IS_TERMS_AND_CONDITIONS = "isTermsAndConditions";
	public static final String IS_PASSWORD_CHANGE_ERROR = "isPasswordChangeError";
	public static final String IS_ORG_UPDATED = "isOrgUpdated";
	//public static final String IS_UPDATED = "UPDATED";
	public static final String CREATE_ERROR = "CREATE_ERROR";

	// Param names
	public static final String USER_SIGNUP_SUCCESS = "userSignupSuccess";
	public static final String CONFIRM_PASSWORD = "confirmPassword";
	public static final String PASSWORD = "password";
	public static final String LAST_NAME = "lastName";
	public static final String FIRST_NAME = "firstName";
	public static final String ORGANIZATION = "NTERorganization";

	public static final String REFERER = "referer";
	public static final String EMAIL_ADDRESS = "emailAddress";
	public static final String USER_NAME = "userName";
	public static final String TOKEN = "token";
	public static final String DEFAULT_REFERER = "defaultReferer";
	public static final String LOGIN_URL = "loginURL";
	public static final String REFERRAL_URL = "referralURL";
	public static final String OLD_EMAIL_ADDRESS = "oldEmailAddress";
	public static final String EMAIL_NOT_USED = "emailNotUsed";
	public static final String CMD_EDIT_ACCOUNT = "cmdEditAccount";
	//public static final String INVALID_PASSWORD_MSG = "invalidPasswordMsg";
	public static final String ORIG_EMAIL = "origEmailAddress";
	
	public static final String CMD_RESET_PASSWORD = "cmdResetPassword";
	public static final String CMD_PASSWORD_ACCOUNT = "cmdPasswordAccount";
	public static final String CMD_FORGOT_PASSWORD = "cmdForgotPassword";
	public static final String CMD_RESUBMIT="cmdResubmit";
	
	public static final String TERMS_AND_CONDITIONS = "termsAndConditions";
	public static final String TERMS_AND_CONDITIONS_CONTENT = "termsAndConditionsContent";
	public static final String PASSWORD_PATTERN = "passwordPattern";
	
	// refering URL default
	public static final String NTERLEARNING_ORG = "https://www.nterlearning.org";
	
	// URL of the password form
	public static final String SIGNUP_VERIFY="signup/verify";
	public static final String PROFILE_VERIFY="profile/verify";
	public static final String PASSWORD_FORM = NTER_USER_MGMT_WEBAPP+PASSWORD;
	public static final String VERIFY_FORM=NTER_USER_MGMT_WEBAPP+SIGNUP_VERIFY;
	public static final String PROFILE_VERIFY_FORM=NTER_USER_MGMT_WEBAPP+PROFILE_VERIFY;

	// For /signup
	public static final String SUCCESS_SIGNUP_JSP = "success_signup";// was SUCCESS_3
	public static final String SUCCESS_JSP = "success";// was SUCCESS
	public static final String SUCCESS_SIGNUP_FORM_JSP = "success_signup_form";// was SUCCESS_2
	public static final String SIGNUP_JSP = "signup";

	// For /signup/verify
	public static final String VALIDATE_ERROR_JSP = "validate_error";
	public static final String VALIDATE_JSP = "validate";
	public static final String VALIDATE_ALREADY_VALID_JSP = "validate_already_valid";

	// For /management
	public static final String PROFILE_ERROR_JSP = "profile_error";
	public static final String PROFILE_JSP = "profile";

	// for /password
	public static final String FORGOT_PASSWORD_JSP = "forgot_password";
	public static final String FORGOT_PASSWORD_SUCCESS_JSP = "forgot_password_success";
	public static final String PASSWORD_RESET_FORM_SUCCESS_JSP = "password_reset_form_success";
	public static final String PASSWORD_RESET_FORM_JSP = "password_reset_form";
	
	// for /email_change_verify
	public static final String VALIDATE_EMAIL_CHANGE_JSP = "validate_email_change";
	public static final String VALIDATE_EMAIL_CHANGE_ALREADY_VALID_JSP = "validate_email_change_already_valid";
	public static final String VALIDATE_EMAIL_CHANGE_ERROR_JSP = "validate_email_change_error";
	
	// Form actions
	public static final String ACTION_SIGNUP = "signup";
	public static final String ACTION_MANAGEMENT = "management";
	public static final String ACTION_VERIFY = "verify";
	public static final String ACTION_RESET_PASSWORD = "reset_password";
	public static final String ACTION_FORGOT_PASSWORD = "forgot_password";

	
	public static final String VALIDATOR="validator";
	public static final String MESSAGE_BASE="messageBase";

	public static String getBaseURL(String referer) {
		String retVal = null;
		if (referer != null) {

			try {
				URL aURL = new URL(referer);
				retVal = aURL.getProtocol() + "://" + aURL.getHost();

				if (aURL.getPort() > 0) {
					retVal = retVal + ":" + aURL.getPort();
				}
			} catch (MalformedURLException e) {

			}
		}
		return retVal;
	}
	
	public String trim(String value)
	{
		String retVal = value;
		
		if (value != null) {
			retVal = value.trim();
		}
		
		return retVal;
	}
	
	public static String buildSSOLink(String referer) {
		return referer+SSO_LOGIN_TARGET+referer;
	}
	
	public String buildLinkURL(String loginURL, String form_url,String token)
	{
		return loginURL + form_url + "?token=" + token;
	}
}
