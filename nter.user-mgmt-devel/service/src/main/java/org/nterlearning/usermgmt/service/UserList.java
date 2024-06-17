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
package org.nterlearning.usermgmt.service;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.nterlearning.usermgmt.model.UserImpl;

/**
 * A container object so that SOAP will work correctly
 * @author mfrazier
 *
 */
@XmlRootElement
public class UserList {
	protected List<UserImpl> userList;
	
	public UserList() {
		
	}
	
	public UserList(List<UserImpl> newList) {
		userList = newList;
	}
	
	/**
	 * get the java.util.List of users
	 * @return
	 */
	@XmlElement
	public List<UserImpl> getList() {
		return userList;
	}
	
	/**
	 * set the java.util.List of users
	 * @param lu
	 */
	public void setList(List<UserImpl> lu)
	{
		userList = lu;
	}

}
