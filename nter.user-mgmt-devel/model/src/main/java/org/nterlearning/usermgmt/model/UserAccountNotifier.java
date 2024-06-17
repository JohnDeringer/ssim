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
package org.nterlearning.usermgmt.model;

import java.util.Locale;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author fritter63
 *
 */
public interface UserAccountNotifier {

	/**
	 * sets the mail sender to use
	 * @param mailSender - the new mail sender
	 */
	public  void setMailSender(JavaMailSender mailSender);

	/**
	 * Sets a new mail message template
	 * @param mail - the mail message template
	 */
	public  void setGenericMailWithSubject(SimpleMailMessage mail);
	
	/**
	 * Gets a locale based email template for new user notifications
	 * @param locale - the locale
	 * @return the email template
	 */
	 
	public  String getNewUserEmailTemplate(Locale locale); 
	/**
	 * Gets a locale based email template for password reset notifications
	 * @param locale - the locale
	 * @return the email template
	 */
	
	public  String getPasswordResetTemplate(Locale locale) ;
	/**
	 * Gets a locale based email template for email change notifications
	 * @param locale - the locale
	 * @return the email template
	 */
	public  String getEmailChangeTemplate(Locale locale);
	
	// name of templates used
	public void setNewUserEmailTemplateName(String name);
	public void setPasswordResetTemplateName(String name);
	public void setEmailChangeTemplateName(String name);

// send mail methods for different notifications
	public void sendNewUserConfirmationEmail(String subject,String newEmail, String givenName,
			String link, Locale locale);

	public void sendProfileChangeConfirmationEmail(String subject,String email, String givenName,
			String link, Locale locale);

	public void sendResetPasswordConfirmationEmail(String subject,String email, String givenName,
			String link, Locale locale);

}