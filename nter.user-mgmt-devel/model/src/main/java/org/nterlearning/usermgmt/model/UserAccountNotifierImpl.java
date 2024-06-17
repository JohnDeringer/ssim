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

import java.io.IOException;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.nterlearning.usermgmt.common.UserMgmtUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class UserAccountNotifierImpl implements UserAccountNotifier {

	public static String TEMPLATE_EXT=".txt";
	private static Logger log = Logger.getLogger(UserAccountNotifierImpl.class); 
	
	private JavaMailSender mailSender;
	private SimpleMailMessage genericMailWithSubject;
	private String newUserEmailTemplateName;
	private String passwordResetTemplateName;
	private String emailChangeTemplateName;
	private String overrideDir="";
	

	public SimpleMailMessage getGenericMailWithSubject() {
		return genericMailWithSubject;
	}

	public void setGenericMailWithSubject(SimpleMailMessage mail) {
		this.genericMailWithSubject = mail;
	}

	public String getOverrideDir() {
		return overrideDir;
	}

	public void setOverrideDir(String overrideDir) {
		this.overrideDir = overrideDir;
	}

	
	/**
	 * Loads a template as a String, based on Locale
	 * @param templateName the base name of the template
	 * @param l current Locale
	 * @return A string with the template contents
	 */
	public String getTemplate(String templateName, Locale l) {
		String retVal = "";
		try {
			retVal = UserMgmtUtils.loadResource(UserMgmtUtils.getOverideableResource(overrideDir, templateName, TEMPLATE_EXT, l));
		} catch (IOException e) {
			log.error("Couldn't load email template:",e);
		}
		return retVal;
	}
	
	public String getNewUserEmailTemplate(Locale l) {
		return getTemplate(newUserEmailTemplateName,l);
		
	}
	
	public String getPasswordResetTemplate(Locale l) {
		return getTemplate(passwordResetTemplateName,l);
	}
	
	public String getEmailChangeTemplate(Locale l) {
		return getTemplate(emailChangeTemplateName,l);
	}


	public UserAccountNotifierImpl() throws IOException
	{

	}
	
	/* (non-Javadoc)
	 * @see org.nter.usermgmt.model.UserAccountNotifier#sendNewUserConfirmationEmail(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Locale)
	 */
	@Override
	public void sendNewUserConfirmationEmail(String subject,String mailChange,String givenName,String link, Locale l)
	{		

		SimpleMailMessage smm = new SimpleMailMessage(getGenericMailWithSubject());
		smm.setSubject(subject);
							
		//String messageStr = String.format(smm.getText(), givenName, confirmationLink, confirmationLink); 
		String messageStr = String.format(getNewUserEmailTemplate(l), givenName, link, link); 

		this.sendMimeMailMessage(smm, mailChange, messageStr);
				
		
	}	
	
	
	/* (non-Javadoc)
	 * @see org.nter.usermgmt.model.UserAccountNotifier#sendProfileChangeConfirmationEmail(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Locale)
	 */
	@Override
	public void sendProfileChangeConfirmationEmail(String subject, String email,String givenName,String link,Locale l)
	{		
		SimpleMailMessage smm = new SimpleMailMessage(getGenericMailWithSubject());
		smm.setSubject(subject);					

			String messageStr = String.format(getEmailChangeTemplate(l), givenName, link, link); 

			this.sendMimeMailMessage(smm, email, messageStr);
	
		
	}
	
	/*
	 * This isn't really for a new user, but it sends the user a new password
	 */
	/* (non-Javadoc)
	 * @see org.nter.usermgmt.model.impl.UserAccountNotifierTmp#sendResetPasswordConfirmationEmail(java.lang.String)
	 */
	
	@Override
	public void sendResetPasswordConfirmationEmail(String subject, String email,String givenName,String link,Locale l)
	{			

		SimpleMailMessage smm = new SimpleMailMessage(getGenericMailWithSubject());
		smm.setSubject(subject);

		String messageStr = String.format(getPasswordResetTemplate(l),givenName, link, link);

		sendMimeMailMessage(smm, email, messageStr);
	}
	
	@SuppressWarnings("unused")
	private void sendMailMessage(SimpleMailMessage smm,String to, String message)
	{
		try {

			smm.setTo(to);
			smm.setText(message);

			this.mailSender.send(smm);
		} catch (MailException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMimeMailMessage(SimpleMailMessage smm,String to, String message)
	{
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
		
			MimeMessageHelper helper = 
				new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(smm.getFrom());
			helper.setTo(to);
			helper.setSubject(smm.getSubject()); 
			helper.setText(message,message);

			this.mailSender.send(mimeMessage);
		} catch (MailException e) {
			log.error("Couldn't send mail message:"+e);
		} catch (MessagingException e) {
			log.error("Couldn't send mail message:"+e);
		}
	}

	
	/* (non-Javadoc)
	 * @see org.nter.usermgmt.model.impl.UserAccountNotifierTmp#setMailSender(org.springframework.mail.javamail.JavaMailSender)
	 */
	@Override
	public void setMailSender(JavaMailSender inMailSender)
	{
		this.mailSender = inMailSender;
	}
	
	
	@Override
	public void setNewUserEmailTemplateName(String name) {
		this.newUserEmailTemplateName = name;
		
	}

	@Override
	public void setPasswordResetTemplateName(String name) {
		this.passwordResetTemplateName = name;
		
	}

	public void setEmailChangeTemplateName(String emailChangeTemplateName) {
		this.emailChangeTemplateName = emailChangeTemplateName;
	}
	
}
