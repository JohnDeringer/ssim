/**
 * National Training and Education Resource (NTER)
 * Copyright (C) 2012  SRI International
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
package org.nterlearning.commerce.controller;

import org.nterlearning.commerce.managed.BeanUtil;
import org.nterlearning.commerce.managed.EntitlementUtil;
import org.nterlearning.entitlement.client.EntitlementPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Controls displaying institutions.
 *
 * @author Brian Blonski
 */
@ManagedBean(name = "institutions")
public class InstitutionsController implements Serializable {

	@ManagedProperty("#{entitlementUtil}")
	private EntitlementUtil entitlementUtil;

    private static final String GLOBAL_RESOURCE = "*";
	private List<String> institutions;

    public static final String SESSION_INSTITUTION_NAME_LIST =
            "org.nterlearning.commerce.institutions";

    private Logger logger = LoggerFactory.getLogger(InstitutionsController.class);

	public InstitutionsController() {
        institutions = new ArrayList<String>();
	}

	//** Data Initialization **//

	@PostConstruct
    @SuppressWarnings("unchecked")
	private void initInstitutions() {
        if (institutions.isEmpty()) {
            Object object =
                BeanUtil.getAttributeFromSession(
                        SESSION_INSTITUTION_NAME_LIST
                );
            if (object != null) {
                institutions = (ArrayList<String>)object;
            }
        }

        if (institutions.isEmpty()) {
            List<String> institutionNames = new ArrayList<String>();

            List<EntitlementPolicy> policies =
                        entitlementUtil.getPoliciesBySubject();

            for (EntitlementPolicy policy : policies) {
                if (!institutionNames.contains(policy.getResource()) &&
                        !policy.getResource().equals(GLOBAL_RESOURCE)) {
                    if (entitlementUtil.hasReadAccess(policy.getResource())) {
                        institutionNames.add(policy.getResource());
                    }
                }
            }

            institutions = institutionNames;

            BeanUtil.setAttributeInSession(
                    SESSION_INSTITUTION_NAME_LIST, institutions);
        }
	}

	//** Getters & Setters **//

	public List<String> getInstitutions() {
		return institutions;
	}

	public void setInstitutions(List<String> institutions) {
		this.institutions = institutions;
	}

	public EntitlementUtil getEntitlementUtil() {
		return entitlementUtil;
	}
	public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
		this.entitlementUtil = entitlementUtil;
	}

}
