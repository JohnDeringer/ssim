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

package org.nterlearning.metadata;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace="http://jaxws.metadata.nterlearning.org/")
public interface MetaDataAdder {
	/**
	 * Adds a block of MetaData to the SHIB SP metadata file
	 * @param content - The MetaData XML string
	 * @return
	 */
	@WebMethod
	public boolean addMetaData(
			@WebParam(name="content")String content);
	
	/**
	 * Removes a block of MetaData from the SHIB SP metadata file
	 * @param sp_hostname - the hostname of the block of metadata to be removed
	 * @return
	 */
	@WebMethod
	public boolean removeMetaData(
			@WebParam(name="hostname")String sp_hostname);
	
	/**
	 * Allows a client to ping the service to check for response.
	 * @return
	 */
	@WebMethod
	public boolean ping();
	

}
