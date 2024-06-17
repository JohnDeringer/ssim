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
package org.nterlearning.registry.managed;

import org.nterlearning.registry.model.RegistryModel;

import org.apache.log4j.Logger;

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.Institution;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.io.Serializable;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/18/12
 */
@ManagedBean(name = "institutionsBean")
public class InstitutionsBean implements Serializable {

    private Logger logger = Logger.getLogger(InstitutionsBean.class);

    @ManagedProperty("#{registryModel}")
    private RegistryModel registry;

    private List<Institution> institutions = null;

    @PostConstruct
	private void initInstitutions() {
        institutions = getInstitutions();
	}

    public List<Institution> getInstitutions() {
        if (institutions == null || institutions.isEmpty()) {
            institutions = registry.getInstitutions(ActiveStatusEnum.UNSPECIFIED);
        }

        return institutions;
    }
    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    public RegistryModel getRegistry() {
		return registry;
	}

	public void setRegistry(RegistryModel registry) {
		this.registry = registry;
	}

}
