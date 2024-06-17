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

import java.util.Locale;

import org.apache.log4j.Logger;
import org.nterlearning.usermgmt.common.EmailValidator;
import org.nterlearning.usermgmt.common.PasswordValidator;
import org.nterlearning.usermgmt.common.UserMgmtUtils;
import org.nterlearning.usermgmt.model.User;
import org.nterlearning.usermgmt.model.UserAccountNotifier;
import org.nterlearning.usermgmt.persistence.PasswordHistoryException;
import org.nterlearning.usermgmt.persistence.UserDao;
import org.nterlearning.usermgmt.webapp.utils.ConfigurableParams;
import org.nterlearning.usermgmt.webapp.utils.ContextLookup;
import org.nterlearning.usermgmt.webapp.utils.NTERResourceBundleMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Controller
public class PasswordController extends NTERController {



	Logger log = Logger.getLogger(PasswordController.class);
	
	private static final String FORGOT_PASSWORD_POST = "/"+ACTION_FORGOT_PASSWORD;
	private static final String RESET_PASSWORD_POST = "/"+ACTION_RESET_PASSWORD;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserAccountNotifier userAccountNotifier;
	
	@Autowired
	private ConfigurableParams cp;
	
	@Autowired
	private ContextLookup cl;
	
	@Autowired
	private PasswordValidator passwordValidator;
	
	@Autowired
	private EmailValidator emailValidator;
	
	@Autowired
	private SessionLocaleResolver localeResolver;
	
	@Autowired
	private NTERResourceBundleMessageSource rbm;
	
	
	public void setEmailValidator(EmailValidator emailValidator) {
		this.emailValidator = emailValidator;
	}

	public void setPasswordValidator(PasswordValidator passwordValidator) {
		this.passwordValidator = passwordValidator;
	}

	@RequestMapping(value="/password",method=RequestMethod.GET)
	public ModelAndView doGet(
			@RequestParam(value=TOKEN,required=false) String token,
			@RequestParam(value=REFERER,required=false) String referer
			)
	{
		ModelAndView retVal = new ModelAndView();
		
		// default redirect to the forgot password form
		String redirectURL = FORGOT_PASSWORD_JSP;
		Boolean isValidToken = false;

		if (token != null && !token.isEmpty()) {
				
				User nterUser =  userDao.findByPasswordChangeToken(
						token);

				if (nterUser != null) {
					isValidToken = true;
					redirectURL = PASSWORD_RESET_FORM_JSP;
					referer = nterUser.getHrefRedirect();
				} 
			
		} else {
			referer = getBaseURL(referer);	
		}

		// Forward the request to the appropriate page
		retVal.addObject(IS_VALID_TOKEN, isValidToken);
		retVal.addObject(TOKEN, token);
		retVal.addObject(REFERER, referer);
		retVal.addObject(DEFAULT_REFERER,cp.getUrl());
		retVal.addObject(VALIDATOR, passwordValidator.getClass().getSimpleName());
		retVal.addObject(MESSAGE_BASE,rbm.getBasename());
		
		retVal.setViewName(redirectURL);
		
		return retVal;
	}
	
	/**
	 * Handle POST data from the forgotPassword form
	 * @param emailAddress
	 * @param referer
	 * @return
	 */
	@RequestMapping(value=FORGOT_PASSWORD_POST,method=RequestMethod.POST)
	public ModelAndView forgotPassword(
			@RequestParam(value=EMAIL_ADDRESS) String emailAddress,
			@RequestParam(value=REFERER) String referer
	)
	{
		ModelAndView retVal = new ModelAndView();

		String redirectURL = FORGOT_PASSWORD_JSP;
		String userName = "";

		// Errors
		boolean isValidEmailAddressComplete = false;
		boolean isEmailAddressComplete = false;
		boolean isUserWithEmail = false;

		// make sure we have an email address
		if (emailAddress != null && !emailAddress.isEmpty()) {
			isEmailAddressComplete = true;
			// make sure it's valid
			if (emailValidator.validateEmail(emailAddress)) {
				isValidEmailAddressComplete = true;

				// try to locate this user
				User nterUser = userDao.findByEMail(
						emailAddress);
				
				// if we got one, set a new random password on them
				if (nterUser != null) {
					isUserWithEmail = true;
					userName = nterUser.getUid();

					// this gets the new random password
					String passwordToken = UserMgmtUtils.getNewRandomToken();

					// update their hrefredirict field if needed
					referer = getBaseURL(referer);
					if (referer != null) {
						nterUser.setHrefRedirect(referer);
						userDao.updateRedirect(userName, referer);
					}
					
					// update the password in the db
					if (userDao.updatePasswordChangeToken(userName,passwordToken)) {

						// Send the user an e-mail
						
						Locale l = localeResolver.resolveLocale(cl.getRequest());
							
						String link = this.buildLinkURL(cp.getLoginUrl(), PASSWORD_FORM, passwordToken);
						userAccountNotifier.sendResetPasswordConfirmationEmail(
								cl.getMessage(rbm,MAIL_PASSWORD_RESET_SUBJECT, l),
								nterUser.getEmail(), nterUser.getGivenName(),
								link,l);
						
						log.info("C&A:PASSWORD RESET EVENT for User: "
								+ nterUser.getFullName() + "("
								+ nterUser.getEmail() + " ,IP:"
								+ cl.getRemoteIPAddress() + " ,SSO ID:"
								+ nterUser.getUid() + ")");
	
						redirectURL = FORGOT_PASSWORD_SUCCESS_JSP;
					}
				}
			}
		}

		// Forward the request to the appropriate page
		retVal.addObject(EMAIL_ADDRESS, emailAddress);
		retVal.addObject(IS_VALID_EMAIL_ADDRESS_COMPLETE,
				isValidEmailAddressComplete);
		retVal.addObject(IS_EMAIL_ADDRESS_COMPLETE, isEmailAddressComplete);
		retVal.addObject(IS_USER_WITH_EMAIL, isUserWithEmail);
		retVal.addObject(DEFAULT_REFERER,cp.getUrl());
		retVal.addObject(REFERER, referer);
		
		retVal.setViewName(redirectURL);

		return retVal;
	}
	
	/**
	 * handle POST data from rest password form
	 * @param passwordReq
	 * @param confirmPasswordReq
	 * @param token
	 * @return
	 */
	@RequestMapping(value=RESET_PASSWORD_POST,method=RequestMethod.POST)
	public ModelAndView resetPassword(
			@RequestParam(value=PASSWORD) String passwordReq,
			@RequestParam(value=CONFIRM_PASSWORD) String confirmPasswordReq,
			@RequestParam(value=TOKEN) String token
		)
	{
		ModelAndView retVal = new ModelAndView();

		String redirectURL = PASSWORD_RESET_FORM_JSP;
		boolean isPasswordConfirm = false;
		boolean isPasswordValid = false;
		boolean isPasswordUpdated = false;
		boolean isValidToken = false;
		User nterUser = null;
		String referer = null;

		// attempt to find the user based on the token passed in
		if (token != null && !token.isEmpty()) 
		{
			nterUser = userDao.findByPasswordChangeToken(
					token);
			if (nterUser != null) {
				isValidToken = true;

				referer = nterUser.getHrefRedirect();
			}
		}
		
		passwordReq = trim(passwordReq);
		confirmPasswordReq = trim(confirmPasswordReq);

		boolean isPasswordChangeError=false;
 
		// make sure we're good to go...
		if (isValidToken) {
			// validate the new requested password
			if (passwordValidator.validatePassword(passwordReq)) {
				isPasswordValid=true;
				// make sure they confirm password matches
				if (passwordReq.equals(confirmPasswordReq)) {
					try {
						
						// update the password and log it
						isPasswordConfirm=true;
						
						isPasswordUpdated=userDao.updatePassword(nterUser.getUid(), passwordReq);
	
						// C&A REQUIREMENT: DO NOT REMOVE!
						log.info("C&A: PASSWORD CHANGE EVENT for User: "
								+ nterUser.getFullName() + "("
								+ nterUser.getEmail() + " ,IP: "
								+ cl.getRemoteIPAddress() + " ,SSO ID:"
								+ nterUser.getUid() + ")"+
								"RESULT:"+(isPasswordUpdated ? "SUCCESS":"FAILED"));
	
						redirectURL = PASSWORD_RESET_FORM_SUCCESS_JSP;
					}
					catch (PasswordHistoryException e) {
						isPasswordChangeError = true;
					}
				}
			} 
			
		}

		retVal.addObject(IS_PASSWORD_CONFIRM, isPasswordConfirm);
		retVal.addObject(IS_PASSWORD_VALID, isPasswordValid);
		retVal.addObject(IS_PASSWORD_UPDATED, isPasswordUpdated);
		retVal.addObject(IS_PASSWORD_CHANGE_ERROR, isPasswordChangeError);
		retVal.addObject(IS_VALID_TOKEN, isValidToken);
		retVal.addObject(TOKEN, token);
		retVal.addObject(REFERER, referer);
		retVal.addObject(DEFAULT_REFERER,cp.getUrl());
		retVal.addObject(VALIDATOR, passwordValidator.getClass().getSimpleName());
		retVal.addObject(MESSAGE_BASE,rbm.getBasename());
		
		retVal.setViewName(redirectURL);
		return retVal;
	}
}
