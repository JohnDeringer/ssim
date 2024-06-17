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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;



@XmlRootElement
public class UserImpl implements User,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String group; 
	protected String uid;
	protected String lastName;
	protected String givenName;
	protected String fullName;
	protected String userPassword;
	protected String email;
	protected String shadowExpire;
	protected String mailChange;
	protected String mailChangeToken;
	protected String passwordChangeToken;
	protected String hrefRedirect;
	protected String googleId;
	protected String facebookId;
    protected String organization;
	protected Map<String,String> baseDn;
    protected List<String> objectClasses;

   
    public UserImpl() {
    	this.addObjectClasses("nterUser");
    	this.addObjectClasses("shadowAccount");
    	this.addObjectClasses("inetOrgPerson");
    	
    	baseDn = new HashMap<String,String>();
    }
    
	@Override
	public String getGroup() {
		return group;
	}
	
	@Override
	public void setGroup(String group) {
		this.group = group;
	}
	
	@XmlElement(required=true)
	@Override
	public String getUid() {
		return uid;
	}
	
	@Override
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@XmlElement(required=true)
	@Override
	public String getLastName() {
		return lastName;
	}
	
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	@XmlElement(required=true)
	public String getGivenName() {
		return givenName;
	}

	@Override
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	@XmlElement(required=true)
	@Override
	public String getFullName() {
		return fullName;
	}
	
	@Override
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	@Override
	public void setFullName(String firstName, String lastName) {
		this.fullName = firstName + " " + lastName;
	}	

	@XmlTransient
	@Override
	public String getUserPassword() {
		return userPassword;
	}

	@Override
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	@XmlElement(required=true)
	@Override
	public String getEmail() {
		return email;
	}
	
	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	@XmlTransient
	@Override
	public String getShadowExpire() {
		return shadowExpire;
	}
	
	@Override
	public void setShadowExpire(String shadowExpire) {
		this.shadowExpire = shadowExpire;
	}
	
	@XmlTransient
	@Override
	public String getMailChange() {
		return mailChange;
	}
	
	@Override
	public void setMailChange(String mailChange) {
		this.mailChange = mailChange;
	}

	@XmlTransient
	@Override
	public String getMailChangeToken() {
		return mailChangeToken;
	}
	
	@Override
	public void setMailChangeToken(String mailChangeToken) {
		this.mailChangeToken = mailChangeToken;
	}

	@XmlTransient
	@Override
	public Map<String,String> getBaseDn()
	{	
		return baseDn;
	}
	
	@XmlTransient
	@Override
	public List<String> getObjectClasses()
	{
		return objectClasses;
	}

	@Override
	public void setObjectClasses(String inObjectClassesStr)
	{
		objectClasses = null;
		addObjectClasses(inObjectClassesStr);
	}	
	
	@Override
	public void setObjectClasses(List<String> inObjectClasses)
	{
		this.objectClasses = inObjectClasses;
	}
	
	@Override
	public void setObjectClasses(String[] inObjectClassesStr)
	{
		objectClasses = new ArrayList<String>();

		for (String s:inObjectClassesStr)
		{
			objectClasses.add(s);
		}
	}	

	@Override
	public void addObjectClasses(String inObjectClassStr)
	{
		if(objectClasses == null)
		{
			objectClasses = new ArrayList<String>();
		}
		
		String[] objectStrings = inObjectClassStr.split(",");
		if(objectStrings.length > 1)
		{
			for(String objectString : objectStrings)
			{
				objectClasses.add(objectString.trim());
			}
		}
		else
		{
			objectClasses.add(inObjectClassStr);
		}
		
	}

	@XmlTransient
	@Override
	public String getPasswordChangeToken() {
		return passwordChangeToken;
	}

	@Override
	public void setPasswordChangeToken(String passwordChangeToken) {
		this.passwordChangeToken = passwordChangeToken;
	}
	
	@Override
	public String getHrefRedirect() {
		return hrefRedirect;
	}
	
	@Override
	public void setHrefRedirect(String hrefRedirect) {
		this.hrefRedirect = hrefRedirect;
	}

	@Override
	public String getGoogleId() {
		return googleId;
	}
	
	@Override
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}
	
	@Override
	public String getFacebookId() {
		return facebookId;
	}
	
	@Override
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
   
	@Override 
	public String toString()
	{
		return this.getEmail()+":"+this.givenName;
	}

	@Override
	@Deprecated
	public void clear() {
		 uid=null;
		 lastName=null;
		 givenName=null;
		 fullName=null;
		 userPassword=null;
		 email=null;
		 mailChange=null;
		 mailChangeToken=null;
		 passwordChangeToken=null;
		 hrefRedirect=null;
		 googleId=null;
		 facebookId=null;
		
	}

	@Override
	public void setOrganization(String org) {
		this.organization = org;
		
	}

	@XmlElement(required=true)
	@Override
	public String getOrganization() {
		return organization;
	}

}
