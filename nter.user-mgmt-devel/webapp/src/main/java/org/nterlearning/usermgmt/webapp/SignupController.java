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

import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.nterlearning.usermgmt.common.EmailValidator;
import org.nterlearning.usermgmt.common.PasswordValidator;
import org.nterlearning.usermgmt.model.User;
import org.nterlearning.usermgmt.model.UserAccountNotifier;
import org.nterlearning.usermgmt.persistence.UserDao;
import org.nterlearning.usermgmt.webapp.enumeration.ProcessState;
import org.nterlearning.usermgmt.webapp.utils.ConfigurableParams;
import org.nterlearning.usermgmt.webapp.utils.ContextLookup;
import org.nterlearning.usermgmt.webapp.utils.NTERResourceBundleMessageSource;
import org.nterlearning.usermgmt.webapp.utils.ProfileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * @author mfrazier
 *
 */
@Controller
//@RequestMapping("/signup")
public class SignupController extends NTERController {

	private static final String SLASH = "/";
	private static final String SLASH_VERIFY = SLASH+ACTION_VERIFY;
	private static final String SLASH_SIGNUP = SLASH+ACTION_SIGNUP;
	private static final String SLASH_SIGNUP_VERIFY = SLASH+SIGNUP_VERIFY;

    private static HashMap<ProcessState, String> mRedirectMap;
    static {
        mRedirectMap = new HashMap<ProcessState, String>();
        mRedirectMap.put(ProcessState.PROCESS_FAILED, VALIDATE_ERROR_JSP);
        mRedirectMap.put(ProcessState.PROCESS_SUCCEEDED, VALIDATE_JSP);
        mRedirectMap.put(ProcessState.PROCESS_NOT_NEEDED, VALIDATE_ALREADY_VALID_JSP);
    }

	Logger log = Logger.getLogger(SignupController.class);
	
	@Autowired
	private UserDao userDao;

	@Autowired
	private UserAccountNotifier userAccountNotifier;
	
	@Autowired
	private ConfigurableParams cp;
	
	@Autowired
	private PasswordValidator passwordValidator;
	
	@Autowired
	private EmailValidator emailValidator;
	
	@Autowired
	private SessionLocaleResolver localeResolver;
	
	@Autowired
	private NTERResourceBundleMessageSource rbm;
	
	@Autowired
	private ContextLookup cl;
	
	String passwordValidatorName="";

	public void setEmailValidator(EmailValidator emailValidator) {
		this.emailValidator = emailValidator;
		
	}

	public void setPasswordValidator(PasswordValidator passwordValidator) {
		this.passwordValidator = passwordValidator;
		passwordValidatorName=passwordValidator.getClass().getSimpleName();
	}

	/**
	 * Hand the GET request for the signup verify form
	 * @param token
	 * @return
	 */
	@RequestMapping(value=SLASH_SIGNUP_VERIFY, method = RequestMethod.GET)
	public ModelAndView signupVerify(@RequestParam(value = TOKEN) String token) {
		ModelAndView retVal = new ModelAndView();
		String referralURL = cp.getUrl();

		retVal.setViewName(VALIDATE_ERROR_JSP);

		// Check to see if the username is unique		
		User updatePerson = userDao.findByEMailChangeToken(token);

		if (updatePerson != null) {
		
			String hrefRedirect = updatePerson.getHrefRedirect();
			if (hrefRedirect != null && !hrefRedirect.isEmpty()) {
				referralURL = hrefRedirect;
			}

            ProcessState changeRequestState = ProfileUtils.processMailChangeRequest(userDao, updatePerson);
            retVal.setViewName(mRedirectMap.get(changeRequestState));
		}

		retVal.addObject(REFERRAL_URL, referralURL);
		retVal.addObject(DEFAULT_REFERER, cp.getUrl());
		retVal.addObject(PASSWORD_PATTERN, StringEscapeUtils.escapeHtml(passwordValidator.getRegexPattern()));
		
		return retVal;
	}

	/**
	 * handle the GET request for the signup form
	 * @param referer
	 * @return
	 */
	@RequestMapping(value=SLASH_SIGNUP, method = RequestMethod.GET,params=(REFERER))
	public ModelAndView handleRefererParam(
			@RequestParam(value = REFERER) String referer) {
		ModelAndView retVal = new ModelAndView();
		retVal.setViewName(SIGNUP_JSP);

		retVal.addObject(REFERER,getBaseURL(referer));
		retVal.addObject(DEFAULT_REFERER, cp.getUrl());
		retVal.addObject(IS_TERMS_AND_CONDITIONS,true);
		retVal.addObject(LOGIN_URL, cp.getLoginUrl());
		retVal.addObject(VALIDATOR, passwordValidator.getClass().getSimpleName());
		retVal.addObject(MESSAGE_BASE,rbm.getBasename());
		retVal.addObject(PASSWORD_PATTERN,StringEscapeUtils.escapeHtml(passwordValidator.getRegexPattern()));
		
		return retVal;
	}

	/**
	 * hand GET request for signup form with an email parameter
	 * @param emailAddress
	 * @return
	 */
	@RequestMapping(value=SLASH_SIGNUP,method = RequestMethod.GET,params=(EMAIL_ADDRESS))
	public ModelAndView handleEmailParam(@RequestParam(value = EMAIL_ADDRESS) String emailAddress) {
		ModelAndView retVal = new ModelAndView();
		retVal.setViewName(SUCCESS_SIGNUP_FORM_JSP);
		retVal.addObject(EMAIL_ADDRESS, emailAddress);
		retVal.addObject(DEFAULT_REFERER, cp.getUrl());
		retVal.addObject(LOGIN_URL, cp.getLoginUrl());
		
		return retVal;
	}

	/**
	 * hand the POST data from the verify form
	 * @param emailAddress
	 * @param origEmailAddress
	 * @return
	 */
	@RequestMapping(value=SLASH_VERIFY, method = RequestMethod.POST)
	public ModelAndView resubmit(
			@RequestParam(value = EMAIL_ADDRESS) String emailAddress,
			@RequestParam(value = ORIG_EMAIL) String origEmailAddress
			) {

		ModelAndView retVal = new ModelAndView();

		boolean isEmailAddressComplete = false;
		boolean isValidEmailAddressComplete = false;
		boolean isEmailWaiting = false;
		String referer = null;
		
		retVal.setViewName(SUCCESS_SIGNUP_FORM_JSP);

		// Check to be sure all fields were filled out
		if (emailAddress != null && !emailAddress.isEmpty()) {
			isEmailAddressComplete = true;

			if (emailValidator.validateEmail(emailAddress)) {
				isValidEmailAddressComplete = true;
					
				User nterUser = null;
				if (emailAddress.equals(origEmailAddress)) {
					// Check to see who the mail address belongs to
					nterUser = userDao.findByEMailChange(
							emailAddress);
				}
				else {
					if (!userDao.emailInUse(emailAddress)) {
						nterUser = userDao.findByEMailChange(
								origEmailAddress);
						if (nterUser != null) {
							nterUser.setMailChange(emailAddress);
							userDao.updateMailChange(nterUser.getUid(), emailAddress);
							origEmailAddress = emailAddress;
						}
					}
				}
				
				if (nterUser != null) {
		
					String shadowExp = nterUser.getShadowExpire();
					if (shadowExp != null && shadowExp.equals("0")) {
						// Send the user an e-mail
						// Let the e-mail notifier know of fields in ldap
						// account
						
						Locale l = localeResolver.resolveLocale(cl.getRequest());
						
						String link = this.buildLinkURL(cp.getLoginUrl(), VERIFY_FORM, nterUser.getMailChangeToken());
						userAccountNotifier.sendNewUserConfirmationEmail(
								cl.getMessage(rbm,MAIL_VERIFY_SUBJECT, l),
								emailAddress, nterUser.getGivenName(), link,
								l);

						referer = nterUser.getHrefRedirect();
						isEmailWaiting=true;
						
						retVal.setViewName(SUCCESS_SIGNUP_JSP);
					}
				}
				
			}
		}

		retVal.addObject(IS_EMAIL_ADDRESS_COMPLETE, isEmailAddressComplete);
		retVal.addObject(IS_VALID_EMAIL_ADDRESS_COMPLETE,
				isValidEmailAddressComplete);
		retVal.addObject(IS_EMAIL_WAITING, isEmailWaiting);
		retVal.addObject(EMAIL_ADDRESS, origEmailAddress);
		retVal.addObject(REFERER, referer);
		retVal.addObject(DEFAULT_REFERER,cp.getUrl());
		retVal.addObject(VALIDATOR, passwordValidator.getClass().getSimpleName());
		retVal.addObject(MESSAGE_BASE,rbm.getBasename());
		retVal.addObject(PASSWORD_PATTERN,StringEscapeUtils.escapeHtml(passwordValidator.getRegexPattern()));

		return retVal;

	}

	/**
	 * Hand the POST data from the signup form
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param organization
	 * @param password
	 * @param confirmPassword
	 * @param referer
	 * @param termsAndConditions
	 * @return
	 */
	@RequestMapping(value=SLASH_SIGNUP, method = RequestMethod.POST)
	public ModelAndView signup(
			@RequestParam(value = FIRST_NAME) String firstName,
			@RequestParam(value = LAST_NAME) String lastName,
			@RequestParam(value = EMAIL_ADDRESS) String emailAddress,
			@RequestParam(value = ORGANIZATION) String organization,
			@RequestParam(value = PASSWORD) String password,
			@RequestParam(value = CONFIRM_PASSWORD) String confirmPassword,
			@RequestParam(value = REFERER) String referer,
			@RequestParam(value = TERMS_AND_CONDITIONS, required=false) String termsAndConditions) {
		
		ModelAndView retVal = new ModelAndView();

		String userName = "";

		// Get parameters from the form
		firstName = firstName.trim();
		lastName = lastName.trim();
		emailAddress = emailAddress.trim();
		password = password.trim();
		confirmPassword = confirmPassword.trim();
		organization = organization.trim();
		
		boolean createError=false;
		// Check to be sure all fields were filled out

		HashMap<String, Boolean> errors = this.checkFormValues(firstName,
				lastName, emailAddress, password, confirmPassword,termsAndConditions);

		if (referer == null || referer.isEmpty()) {
			referer = cp.getUrl();
		}

		retVal.setViewName(SIGNUP_JSP);

		if (noErrorsExist(errors)) {
			
			// For some reason we cannot store the organization field ("o" in ldap") as an empty strign. Might be the spring
			// ldap api which is failing here. So if empty we use null, otherwise the value. 
			User newUser = userDao.create(firstName, lastName, emailAddress, (organization.isEmpty() ? null:organization), password, referer);
			
			if (newUser != null)
			{

				String link=buildLinkURL(cp.getLoginUrl(), VERIFY_FORM, newUser.getMailChangeToken());
				Locale l = localeResolver.resolveLocale(cl.getRequest());
				userAccountNotifier.sendNewUserConfirmationEmail(
						cl.getMessage(rbm,MAIL_VERIFY_SUBJECT, l),
						emailAddress,
						firstName, link,
						l);
	
				retVal.setViewName(SUCCESS_JSP);
				log.info("C&A: New user created: " + newUser.getEmail());
			}
			else {
				createError = true;
			}

		}

		// Forward the request to the appropriate page

		for (String key : errors.keySet()) {
			retVal.addObject(key, errors.get(key));
		}

		retVal.addObject(FIRST_NAME, firstName);
		retVal.addObject(LAST_NAME, lastName);
		retVal.addObject(USER_NAME, userName);
		retVal.addObject(EMAIL_ADDRESS, emailAddress);
		retVal.addObject(ORGANIZATION,organization);
		retVal.addObject(REFERER, referer);
		retVal.addObject(DEFAULT_REFERER, cp.getUrl());
		retVal.addObject(LOGIN_URL, cp.getLoginUrl());
		retVal.addObject(VALIDATOR, passwordValidator.getClass().getSimpleName());
		retVal.addObject(MESSAGE_BASE,rbm.getBasename());
		retVal.addObject(CREATE_ERROR, createError);
		retVal.addObject(SUPPORT_EMAIL,cp.getSupportEmail());
		retVal.addObject(PASSWORD_PATTERN,StringEscapeUtils.escapeHtml(passwordValidator.getRegexPattern()));

		return retVal;
	}


	/**
	 * convenience method to iterate through the errors and check any flags
	 * @param errors
	 * @return
	 */
	private boolean noErrorsExist(HashMap<String, Boolean> errors) {
		boolean retVal = true;

		// if ANY values in here are false, we have an error
		for (Boolean b : errors.values()) {
			if (!b.booleanValue())
				retVal = false;
		}

		return retVal;
	}

	/**
	 * checks the form data and builds a Map of flags to indicate errors. This lets us keep from adding a
	 * ton of variables for each field.
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param password
	 * @param confirmPassword
	 * @param termsAndConditions
	 * @return
	 */
	private HashMap<String, Boolean> checkFormValues(String firstName,
			String lastName, String emailAddress, String password,
			String confirmPassword, String termsAndConditions) {
		HashMap<String, Boolean> retVal = new HashMap<String, Boolean>();

		retVal.put(IS_TERMS_AND_CONDITIONS, termsAndConditions != null && termsAndConditions.equals(AGREED));
		retVal.put(IS_FIRST_NAME_COMPLETE, (firstName != null && !firstName.isEmpty()));

		retVal.put(IS_LAST_NAME_COMPLETE, (lastName != null && !lastName.isEmpty()));

		retVal.put(IS_EMAIL_ADDRESS_COMPLETE, (emailAddress != null && !emailAddress.isEmpty()));
		
		retVal.put(EMAIL_NOT_USED, !userDao.emailInUse(emailAddress));
		
		retVal.put(IS_VALID_EMAIL_ADDRESS_COMPLETE, (emailAddress != null) && emailValidator.validateEmail(emailAddress));
	
		
		retVal.put(IS_PASSWORD_CONFIRM, (password!=null && password.equals(confirmPassword)));

		retVal.put(IS_PASSWORD_VALID, passwordValidator.validatePassword(password));

		return retVal;
	}

}
