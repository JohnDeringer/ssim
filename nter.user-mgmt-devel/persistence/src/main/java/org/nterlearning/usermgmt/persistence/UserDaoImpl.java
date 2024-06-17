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
package org.nterlearning.usermgmt.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.apache.log4j.Logger;
import org.nterlearning.usermgmt.common.UserMgmtUtils;
import org.nterlearning.usermgmt.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ldap.InvalidAttributeValueException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;

/**
 * @author mfrazier
 *
 */
public class UserDaoImpl implements UserDao {

	

	private static Logger log = Logger.getLogger(UserDaoImpl.class);
	
	protected static final String FACEBOOK_ID = "facebookId";
	protected static final String GOOGLE_ID = "googleId";
	protected static final String HREF_REDIRECT = "hrefRedirect";
	protected static final String PASSWORD_CHANGE_TOKEN = "passwordChangeToken";
	protected static final String MAIL_CHANGE_TOKEN = "mailChangeToken";
	protected static final String MAIL_CHANGE = "mailChange";
	protected static final String SHADOW_EXPIRE = "shadowExpire";
	protected static final String MAIL = "mail";
	protected static final String USER_PASSWORD = "userPassword";
	protected static final String CN = "cn";
	protected static final String GIVEN_NAME = "givenName";
	protected static final String SN = "sn";
	protected static final String UID="uid";
	protected static final String OU = "ou";
	protected static final String OBJECTCLASS = "objectclass";
	private static final String NTER_OBJECT_CLASS = "nterUser";
	private static final String ORGANIZATION = "o";
	
	public static final String SHADOW_EXPIRE_DEFAULT = "0";
	private LdapTemplate ldapTemplate;
	private String ldapGroup;
	
	public static final String SHA_PREFIX = "{SHA}";
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public UserDaoImpl()
	{
		
	}
	
	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
		this.findByName("nobody");
	}
	
	@Override
	public boolean create(User user) {
		boolean retVal = false;
		
		try {
			if (user != null) {
				DirContextAdapter context = new DirContextAdapter(buildDn(user));
				
				mapToContext(user, context);
				ldapTemplate.bind(context);
				
				retVal = true;
				
			}
		}
		catch (RuntimeException e) {
			log.error(e);
		}	

		return retVal;
	}
	
	@Override
	public boolean update(User user) {
		boolean retVal = false;
		try
		{
			if (user != null) {
				Name dn = buildDn(user);
				DirContextOperations context = ldapTemplate.lookupContext(dn);
				if (context != null)
				{
					mapToContext(user, context);
					ldapTemplate.modifyAttributes(context);
					retVal = true;
				}
			}
		}
		catch (RuntimeException e) {
			log.error(e);
		}
		
		return retVal;
	}
	
	@Override
	public boolean delete(User user) {
		boolean retVal = false;
		try {
			if (user != null) {
				ldapTemplate.unbind(buildDn(user));
				retVal = true;
			}
		}
		catch (RuntimeException e) {
			log.error(e);
		}
		
		return retVal;
	}
	

	protected User findByPrimaryKey(String inUid, String inBaseDn) {
		Name dn = buildDn(inUid, UserMgmtUtils.convertStringToMap(inBaseDn));
		return (User) ldapTemplate.lookup(dn, getContextMapper());
	}
	
	@SuppressWarnings("unchecked")
	protected List<User> findByName(String name) {
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter(OBJECTCLASS, "person")).and(new WhitespaceWildcardsFilter(CN,name));
		return ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), getContextMapper());
	}
	
	@SuppressWarnings("unchecked")
	protected List<User> findByAttribute(String inAttributeName, String inAttributeValue) {
		
		AndFilter filter = new AndFilter();
		//filter.and(new EqualsFilter(OBJECTCLASS, "person")).and(new EqualsFilter(inAttributeName,inAttributeValue));
		filter.and(new EqualsFilter(OBJECTCLASS, "inetOrgPerson")).and(new EqualsFilter(inAttributeName,inAttributeValue));
		return ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), getContextMapper());
	}	
	
	protected User findOnlyOneByAttribute(String attrName,String attrValue) {
		User retVal = null;
		
		List<User> p = findByAttribute(attrName,attrValue);
		
		if (p!=null && p.size() >= 1) {
			retVal = p.get(0);
		}
		
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<User> findAllUsers() {
		EqualsFilter filter = new EqualsFilter(OBJECTCLASS, "person");
	      SearchControls sc = new SearchControls();
	      sc.setCountLimit(10000);
	      sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
	      
		return ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), sc, getContextMapper());
		//return ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), getContextMapper());
	}
	
	protected ContextMapper getContextMapper() {
		return new UserContextMapper(applicationContext);
	}
	
	protected Name buildDn(User person) {
		return buildDn(person.getUid(), person.getBaseDn());
	}
	
	protected Name buildDn(String inUid, Map<String,String> inBaseDn) {
		DistinguishedName dn = new DistinguishedName();
		
		
		for (Map.Entry<String,String> e:inBaseDn.entrySet())
		{
			dn.add(e.getKey(),e.getValue());
		}
		
		dn.add(UID,inUid);
		
		return dn;
	}
	
	public static String encryptPassword(String password) {
		return SHA_PREFIX
		+ UserMgmtUtils.encrypt(password);
	}
	
	protected void mapToContext(User u, DirContextOperations context) {
		context.setAttributeValues(OBJECTCLASS, u.getObjectClasses().toArray());  // Object classes
		context.setAttributeValue(OU, u.getGroup());
		context.setAttributeValue(UID, u.getUid());
		context.setAttributeValue(SN, u.getLastName());
		context.setAttributeValue(GIVEN_NAME, u.getGivenName());
		context.setAttributeValue(CN, u.getFullName());		
		context.setAttributeValue(USER_PASSWORD, u.getUserPassword());
		context.setAttributeValue(MAIL, u.getEmail());
		context.setAttributeValue(SHADOW_EXPIRE, u.getShadowExpire());
		context.setAttributeValue(MAIL_CHANGE, u.getMailChange());
		context.setAttributeValue(MAIL_CHANGE_TOKEN, u.getMailChangeToken());
		context.setAttributeValue(PASSWORD_CHANGE_TOKEN, u.getPasswordChangeToken());
		context.setAttributeValue(HREF_REDIRECT, u.getHrefRedirect());
		context.setAttributeValue(GOOGLE_ID, u.getGoogleId());
		context.setAttributeValue(FACEBOOK_ID, u.getFacebookId());
		context.setAttributeValue(ORGANIZATION, u.getOrganization());
		
	}
	


	protected boolean updateAttribute(String inUid, String inAttributeName, String inAttributeValue) {
		boolean retVal= false;
		
		try {
			if (inUid != null && inAttributeName != null) {
				Name dn = buildDn(inUid,new HashMap<String,String>());
				DirContextOperations context = ldapTemplate.lookupContext(dn);
				
				if (context != null) {
					context.setAttributeValue(inAttributeName, inAttributeValue);
					
					ldapTemplate.modifyAttributes(context);
					retVal = true;
				}
			}
		}
		catch (RuntimeException re) 
		{
			log.error(re);
			
			throw(re);
		}
		return retVal;
	}

	@Override
	public User create(String firstName, String lastName, String email, String org,
			String password, String referer) {
		User retVal = null;

		if (isValid(firstName) && isValid(lastName) && isValid(email) && isValid(password))
		{
			if (!emailInUse(email)) {

				User newUser = (User)applicationContext.getBean("bean_user");
	
				String token = UserMgmtUtils.getNewRandomToken();
	
				newUser.setShadowExpire(SHADOW_EXPIRE_DEFAULT);
		
				newUser.setGroup(ldapGroup);
				newUser.setEmail(""); 
				newUser.setFullName(firstName, lastName);
				newUser.setGivenName(firstName);
				newUser.setLastName(lastName);
				newUser.setMailChange(email);
				newUser.setMailChangeToken(token);
				newUser.setUserPassword(password);
				newUser.setHrefRedirect(referer);
	
				newUser.setUid(getNewUid());
				newUser.setOrganization(org);
	
				// Add this record to the database
				if (create(newUser))
					retVal = newUser;
				
			}
		}
		return retVal;

	}
	
	private boolean isValid(String val)
	{
		return (val!=null && !val.isEmpty());
	}
	
	private String getNewUid() {
		String retVal = null;
		boolean isUserNameInUse;
		User p;

		isUserNameInUse = true;

		while (isUserNameInUse) {
			retVal = UserMgmtUtils.autoGenerateUserId();

			p = findOnlyOneByAttribute(UID, retVal);
			if (p == null) {
				isUserNameInUse = false;
			}
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.nter.usermgmt.persistence.UserDao#setGroup(java.lang.String)
	 */
	@Override
	public void setGroup(String group) {
		ldapGroup = group;
		
	}

	/* (non-Javadoc)
	 * @see org.nter.usermgmt.persistence.UserDao#getGroup()
	 */
	@Override
	public String getGroup() {

		return ldapGroup;
	}

	/* (non-Javadoc)
	 * @see org.nter.usermgmt.persistence.UserDao#emailInUse(java.lang.String)
	 */
	@Override
	public boolean emailInUse(String email) {
		User p = findOnlyOneByAttribute(MAIL, email);
		User p2 = findOnlyOneByAttribute(MAIL_CHANGE, email);
		return (p != null || p2 != null);
	
	}

	@Override
	public boolean updatePassword(String uid,String attrValue) throws PasswordHistoryException {
		boolean retVal=false;
		
		try {
			this.updateAttribute(uid, USER_PASSWORD, attrValue);
			retVal = true;
		}
		catch (InvalidAttributeValueException iave) {
			throw new PasswordHistoryException(iave.getMessage());
		}
		return retVal;
	}

	@Override
	public User findByEMail(String email) {
		return findOnlyOneByAttribute(MAIL, email);
	}

	@Override
	public User findByEMailChange(String email) {
		return findOnlyOneByAttribute(MAIL_CHANGE,email);
	}

	@Override
	public User findByPasswordChangeToken(String password) {
		return findOnlyOneByAttribute(PASSWORD_CHANGE_TOKEN, password);
	}

	@Override
	public User findByUID(String uid) {
		return this.findOnlyOneByAttribute(UID, uid);
	}

	@Override
	public User findByEMailChangeToken(String token) {
		return this.findOnlyOneByAttribute(MAIL_CHANGE_TOKEN, token);
	}

	@Override
	public boolean updatePasswordChangeToken(String uid, String token) {		
		return updateAttribute(uid, PASSWORD_CHANGE_TOKEN, token);	
	}

	@Override
	public boolean updateRedirect(String uid, String href) {
		return updateAttribute(uid, HREF_REDIRECT, href);		
	}

	@Override
	public boolean updateMailChange(String uid, String email) {
	   return updateAttribute(uid, MAIL_CHANGE,email);	
	}

	@Override
	public boolean updateShadowExpire(String uid, String value) {
		return updateAttribute(uid, SHADOW_EXPIRE,value);
	}

	@Override
	public boolean updateEmail(String uid, String email) {
		return updateAttribute(uid, MAIL,email);
	}

	@Override
	public boolean updateGivenName(String uid, String givenName) {
		return updateAttribute(uid, GIVEN_NAME,givenName);
	}

	@Override
	public boolean updateLastName(String uid, String lastName) {
		return updateAttribute(uid, SN,lastName);
	}
	
	@Override
	public boolean updateOrganization(String uid, String org) {
		return updateAttribute(uid,ORGANIZATION,org);
	}

	@Override
	public boolean updateMailChangeToken(String uid, String token) {
		return updateAttribute(uid, MAIL_CHANGE_TOKEN,token);
	}
	
	/* (non-Javadoc)
	 * @see org.nterlearning.usermgmt.persistence.UserDao#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean authenticate(String email, String password) {
		boolean retVal = true;
		
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter(OBJECTCLASS, NTER_OBJECT_CLASS));
		filter.and(new EqualsFilter(MAIL, email));
		
		retVal = ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter.toString(), password);

		return retVal;
	}
	
	protected class UserContextMapper extends AbstractContextMapper {
		
		ApplicationContext appContext;
		
		public UserContextMapper(ApplicationContext ac) {
			appContext = ac;
		}
		
		@Override
		public Object doMapFromContext(DirContextOperations context) {
			//log.debug("Converting user: "+context.getStringAttribute(MAIL));
			User u = (User)appContext.getBean("bean_user");
			
			if (u != null) {
				u.setObjectClasses(context.getStringAttributes(OBJECTCLASS));
				u.setUid(context.getStringAttribute(UID));
				u.setLastName(context.getStringAttribute(SN));
				u.setGivenName(context.getStringAttribute(GIVEN_NAME));
				u.setFullName(context.getStringAttribute(CN));
				
				// make sure the password hasn't been hacked to empty
				Object rawPassword = context.getObjectAttribute(USER_PASSWORD);
				if (rawPassword !=null) {
					u.setUserPassword(new String(((byte[]) rawPassword)));
				}
				
				u.setEmail(context.getStringAttribute(MAIL));
				u.setShadowExpire(context.getStringAttribute(SHADOW_EXPIRE));
				u.setMailChange(context.getStringAttribute(MAIL_CHANGE));
				u.setMailChangeToken(context.getStringAttribute(MAIL_CHANGE_TOKEN));
				u.setPasswordChangeToken(context.getStringAttribute(PASSWORD_CHANGE_TOKEN));
				u.setHrefRedirect(context.getStringAttribute(HREF_REDIRECT));
				u.setGoogleId(context.getStringAttribute(GOOGLE_ID));
				u.setFacebookId(context.getStringAttribute(FACEBOOK_ID));
				u.setOrganization(context.getStringAttribute(ORGANIZATION));
			}
			
			return u;
		}
	}
	


}