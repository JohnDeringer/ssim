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
import org.nterlearning.usermgmt.common.UserMgmtUtils;
import org.nterlearning.usermgmt.model.User;
import org.nterlearning.usermgmt.model.UserAccountNotifier;
import org.nterlearning.usermgmt.persistence.PasswordHistoryException;
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

@Controller
public class ProfileController extends NTERController {

	private static final String SLASH = "/";
	private static final String MANAGEMENT_POST = SLASH+ACTION_MANAGEMENT;
	private static final String SLASH_PROFILE_VERIFY = SLASH+PROFILE_VERIFY;

    private static HashMap<ProcessState, String> mRedirectMap;
    static {
        mRedirectMap = new HashMap<ProcessState, String>();
        mRedirectMap.put(ProcessState.PROCESS_FAILED, VALIDATE_EMAIL_CHANGE_ERROR_JSP);
        mRedirectMap.put(ProcessState.PROCESS_SUCCEEDED, VALIDATE_EMAIL_CHANGE_JSP);
        mRedirectMap.put(ProcessState.PROCESS_NOT_NEEDED, VALIDATE_EMAIL_CHANGE_ALREADY_VALID_JSP);
    }

	Logger log = Logger.getLogger(ProfileController.class);

	@Autowired
	UserDao userDao;
	
	@Autowired
	ContextLookup myContext;
	
	@Autowired
	UserAccountNotifier userAccountNotifier;
	
	@Autowired
	ConfigurableParams cp;
	
	@Autowired
	PasswordValidator passwordValidator;
	
	@Autowired
	SessionLocaleResolver localeResolver;
	
	@Autowired
	NTERResourceBundleMessageSource rbm;
	
	@Autowired
	EmailValidator ev;
	
	private static final String UID = "uid";

	public void setPasswordValidator(PasswordValidator passwordValidator) {
		this.passwordValidator = passwordValidator;
	}

	/**
	 * Handle a GET request for the profile page
	 * @param userName
	 * @param referer_request
	 * @return
	 */
	@RequestMapping(value="/management",method = RequestMethod.GET)
	public ModelAndView doGet(
			@RequestParam(value = UID, required = false) String userName,
			@RequestParam(value = REFERER, required = false) String referer_request) {
		ModelAndView retVal = new ModelAndView();

		String firstName="";
		String lastName="";
		String emailAddress="";
		String oldEmailAddress="";
		String referer = getBaseURL(referer_request);
		String org="";

		retVal.setViewName(PROFILE_JSP);
		// Grab the username
		if (userName==null)
			userName = this.GetUsername();

		User nterUser = userDao.findByUID( userName);

		// if we have a valid user, get the current data and send it on to the jsp
		if (nterUser == null) {
			log.error("Couldn't find a person for this username: " + userName);
			retVal.setViewName(PROFILE_ERROR_JSP);
		} else {
			firstName = nterUser.getGivenName();
			lastName = nterUser.getLastName();
			emailAddress = nterUser.getMailChange();
			org = nterUser.getOrganization();
			
			if (referer == null)
				referer = nterUser.getHrefRedirect();

			oldEmailAddress = nterUser.getEmail();

			if (!oldEmailAddress.equals(emailAddress)) {
				if (emailAddress == null || emailAddress.isEmpty()) {
					emailAddress = oldEmailAddress;
				}
			}
		}

		retVal.addObject(USER_NAME, userName);
		retVal.addObject(FIRST_NAME, firstName);
		retVal.addObject(LAST_NAME, lastName);
		retVal.addObject(EMAIL_ADDRESS, emailAddress);
		retVal.addObject(ORGANIZATION,org);
		retVal.addObject(OLD_EMAIL_ADDRESS, oldEmailAddress);
		retVal.addObject(REFERER, referer);
		retVal.addObject(DEFAULT_REFERER, cp.getUrl());
		retVal.addObject(VALIDATOR, passwordValidator.getClass().getSimpleName());
		retVal.addObject(MESSAGE_BASE,rbm.getBasename());
		retVal.addObject(PASSWORD_PATTERN,StringEscapeUtils.escapeHtml(passwordValidator.getRegexPattern()));

		return retVal;
	}

	/**
	 * Handl POST data from the profile form for account details (NOT password!)
	 * @param userName
	 * @param firstNameReq
	 * @param lastNameReq
	 * @param emailAddressReq
	 * @param orgReq
	 * @param command
	 * @param referer
	 * @return
	 */
	@RequestMapping(value=MANAGEMENT_POST,method=RequestMethod.POST,params=CMD_EDIT_ACCOUNT)
	public ModelAndView management(
			@RequestParam(value=USER_NAME) String userName,
			@RequestParam(value=FIRST_NAME,required=false) String firstNameReq,
			@RequestParam(value=LAST_NAME,required=false) String lastNameReq,
			@RequestParam(value=EMAIL_ADDRESS,required=false) String emailAddressReq,
			@RequestParam(value=ORGANIZATION,required=false) String orgReq,
			@RequestParam(value=CMD_EDIT_ACCOUNT) String command,
			@RequestParam(value=REFERER) String referer
			) {
		
		ModelAndView retVal = new ModelAndView();

		String redirectURL = PROFILE_ERROR_JSP;
		User nterUser = null;

		// Errors
		boolean isValidEmailAddressComplete = true;
		boolean isEmailInUse = false;
		boolean isFirstNameUpdated=false;
		boolean isLastNameUpdated=false;
		boolean isEmailAddressUpdated = false;
		boolean isOrgUpdated = false;

		// Grab the username
		if (userName == null)
			userName = this.GetUsername();

		// If username is null, move elsewhere
		if (userName == null || userName.isEmpty()) {
			log.error("Found a null or empty username in the session");
		} else {
			nterUser = userDao.findByUID(userName);
			if (nterUser == null) {
				log.error("Couldn't find a person for this username: "+userName);
				
			}
			else {
				
				redirectURL = PROFILE_JSP;
				
				String emailAddress = nterUser.getEmail();

				// ** Look for changes and update fields as appropriate **//

				if (firstNameReq != null && !firstNameReq.equals(nterUser.getGivenName())) // Update database with changes
				{
					firstNameReq = firstNameReq.trim();
					isFirstNameUpdated= userDao.updateGivenName(userName,firstNameReq);
					
					retVal.addObject(FIRST_NAME, firstNameReq);
				}
				else retVal.addObject(FIRST_NAME,nterUser.getGivenName());

				if (lastNameReq !=null && !lastNameReq.equals(nterUser.getLastName())) {
					lastNameReq = lastNameReq.trim();
					isLastNameUpdated=userDao.updateLastName(userName, lastNameReq);
					retVal.addObject(LAST_NAME, lastNameReq);
				}
				else retVal.addObject(LAST_NAME, nterUser.getLastName());


				// check to see if the email address has been changed
				if (emailAddressReq !=null && !emailAddressReq.equals(nterUser.getEmail())) {
					emailAddressReq = emailAddressReq.trim();
					
					if (ev.validateEmail(emailAddressReq)) {
						
					
						// if it has, then check to see if it is in use by an account other than this account
						if (userDao.emailInUse(emailAddressReq) && !emailAddressReq.equals(nterUser.getMailChange())) {
							isEmailInUse = true;
						}
						else {
							
							// OK to go ahead and use this email for a change request
	
							String token = UserMgmtUtils.getNewRandomToken();
							// Update the database
							userDao.updateMailChange(userName,emailAddressReq);
							isEmailAddressUpdated=userDao.updateMailChangeToken(userName,token);
							
							// Email address changed
							 String link = this.buildLinkURL(cp.getLoginUrl(), PROFILE_VERIFY_FORM, token);
								Locale l = localeResolver.resolveLocale(myContext.getRequest());
								userAccountNotifier.sendProfileChangeConfirmationEmail(
										myContext.getMessage(rbm,MAIL_VERIFY_SUBJECT, l),
										emailAddressReq, 
										firstNameReq, link
										, l);
							
							retVal.addObject(EMAIL_ADDRESS, emailAddressReq);
							retVal.addObject(OLD_EMAIL_ADDRESS, emailAddress);
							
						}
					}
					else {
						isValidEmailAddressComplete=false;
					}
					
					

				}
				else {
					// email either didn't change, or was set back to original without verifying new email 
					// request. So we wipe out the mail change request if there was one
					retVal.addObject(EMAIL_ADDRESS,nterUser.getEmail());
					userDao.updateMailChange(userName,"");
					userDao.updateMailChangeToken(userName,"");
				}
				
				if (orgReq!=null && !orgReq.equals(nterUser.getOrganization()))
				{
		
					orgReq = orgReq.trim();
					userDao.updateOrganization(userName, orgReq.isEmpty() ? null:orgReq);
					isOrgUpdated = true;
				}
			}
		}

		// Forward the request to the appropriate page
		retVal.addObject(USER_NAME, userName);

		retVal.addObject(IS_EDIT_FORM, true);
		
		// needed because two forms are sharing the same controller!
		retVal.addObject(IS_PASSWORD_FORM, false);
		retVal.addObject(IS_PASSWORD_UPDATED, false);
		retVal.addObject(IS_PASSWORD_CONFIRM, true);
		
		retVal.addObject(IS_VALID_EMAIL_ADDRESS_COMPLETE,isValidEmailAddressComplete);
		retVal.addObject(IS_EMAIL_IN_USE, isEmailInUse);
		retVal.addObject(IS_EMAIL_ADDRESS_UPDATED, isEmailAddressUpdated);
		retVal.addObject(IS_FIRST_NAME_UPDATED,isFirstNameUpdated);
		retVal.addObject(IS_LAST_NAME_UPDATED, isLastNameUpdated);
		retVal.addObject(IS_ORG_UPDATED, isOrgUpdated);

		retVal.addObject(REFERER, referer);
		retVal.addObject(DEFAULT_REFERER,cp.getUrl());
		retVal.addObject(FIRST_NAME,firstNameReq);
		retVal.addObject(LAST_NAME,lastNameReq);
		retVal.addObject(ORGANIZATION,orgReq);
		retVal.addObject(VALIDATOR, passwordValidator.getClass().getSimpleName());
		retVal.addObject(MESSAGE_BASE,rbm.getBasename());
		retVal.addObject(PASSWORD_PATTERN,StringEscapeUtils.escapeHtml(passwordValidator.getRegexPattern()));
		if (!isEmailAddressUpdated) {
			retVal.addObject(EMAIL_ADDRESS,emailAddressReq);
		}
		
		retVal.setViewName(redirectURL);
		return retVal;
	}
	
	/**
	 * Handl POST data from the profile form for passwords ONLY
	 * @param passwordReq
	 * @param confirmPasswordReq
	 * @param userName
	 * @param command
	 * @param referer
	 * @return
	 */
	@RequestMapping(value=MANAGEMENT_POST, method=RequestMethod.POST,params=CMD_PASSWORD_ACCOUNT)
	public ModelAndView changePassword(
			@RequestParam(value=PASSWORD,required=false) String passwordReq,
			@RequestParam(value=CONFIRM_PASSWORD,required=false) String confirmPasswordReq,
			@RequestParam(value=USER_NAME) String userName,
			@RequestParam(value=CMD_PASSWORD_ACCOUNT) String command,
			@RequestParam(value=REFERER) String referer
			)
	{
		ModelAndView retVal = new ModelAndView();

		String redirectURL = PROFILE_ERROR_JSP;

		// Errors
		boolean isPasswordConfirm = false;
		boolean isPasswordValid = false;
		boolean isPasswordUpdated = false;
		boolean isPasswordChangeError = false;

		// Grab the username
		if (userName == null)
			userName = this.GetUsername();

		// If username is null, move elsewhere
		if (userName == null || userName.isEmpty()) {
			log.error("Found a null or empty username in the session");
		} else {
			User nterUser = userDao.findByUID(userName);
			if (nterUser == null) {
				log.error("Couldn't find a person for this username: "+userName);
			}
			else {
				retVal.addObject(FIRST_NAME, nterUser.getGivenName());
				retVal.addObject(LAST_NAME, nterUser.getLastName());
				retVal.addObject(EMAIL_ADDRESS, nterUser.getEmail());
				retVal.addObject(ORGANIZATION,nterUser.getOrganization());
				
				
				redirectURL = PROFILE_JSP;
				passwordReq = trim(passwordReq);
				confirmPasswordReq=trim(confirmPasswordReq);
				
				// make sure new password is valid
				if (passwordValidator.validatePassword(passwordReq)) {
					isPasswordValid = true;
					
					// make sure confirm password matches
					if (passwordReq.equals(confirmPasswordReq)) {
						isPasswordConfirm = true;
						
						try {
							// update the password and log it.
							isPasswordUpdated = userDao.updatePassword(userName, passwordReq);
							
							log.info("C&A:PASSWORD CHANGE EVENT for User: "
									+ nterUser.getFullName() + "("
									+ nterUser.getEmail() + " ,IP:"
									+ myContext.getRemoteIPAddress() + " ,SSO ID:"
									+ nterUser.getUid() + ")"+ 
									"RESULT:"+(isPasswordUpdated ? "SUCCESS":"FAILED"));
							
						}
						catch (PasswordHistoryException e) {
							isPasswordChangeError = true;
						}
						
					}

				}

			}

		}

		retVal.addObject(IS_PASSWORD_FORM,true);
		
		// needed because two forms are sharing the same controller
		retVal.addObject(IS_VALID_EMAIL_ADDRESS_COMPLETE,true);
		retVal.addObject(IS_EMAIL_IN_USE, false);
		retVal.addObject(IS_FIRST_NAME_UPDATED, false);
		retVal.addObject(IS_LAST_NAME_UPDATED, false);
		retVal.addObject(IS_EMAIL_ADDRESS_UPDATED, false);
		
		
		
		retVal.addObject(IS_EDIT_FORM,false);
		retVal.addObject(IS_PASSWORD_CONFIRM, isPasswordConfirm);
		retVal.addObject(IS_PASSWORD_VALID, isPasswordValid);
		retVal.addObject(IS_PASSWORD_UPDATED, isPasswordUpdated);
		retVal.addObject(IS_PASSWORD_CHANGE_ERROR,isPasswordChangeError);
		retVal.addObject(USER_NAME, userName);
		retVal.addObject(DEFAULT_REFERER,cp.getUrl());
		retVal.addObject(REFERER, referer);
		retVal.addObject(VALIDATOR, passwordValidator.getClass().getSimpleName());
		retVal.addObject(MESSAGE_BASE,rbm.getBasename());
		retVal.addObject(PASSWORD_PATTERN,StringEscapeUtils.escapeHtml(passwordValidator.getRegexPattern()));

		retVal.setViewName(redirectURL);
		return retVal;
	}
	
	/**
	 * Hand the GET request for the profile verify page
	 * @param token
	 * @return
	 */
	@RequestMapping(value=SLASH_PROFILE_VERIFY, method = RequestMethod.GET)
	public ModelAndView emailVerify(@RequestParam(value = TOKEN) String token) {
		ModelAndView retVal = new ModelAndView();
		String referralURL = cp.getUrl();
		

		retVal.setViewName(VALIDATE_EMAIL_CHANGE_ERROR_JSP);

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
		
		return retVal;
	}
	
	private String GetUsername() {
		return myContext.getUserName();
	}
}
