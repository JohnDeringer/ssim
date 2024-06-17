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
package org.nterlearning.registry.proxy;

import org.nterlearning.registry.client.BusinessService;
import org.nterlearning.registry.client.Contact;
import org.nterlearning.registry.client.Institution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/30/12
 */
public class InstitutionBean
        extends Institution
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private RegistryInstance registryInstance;

    public InstitutionBean() {
        registryInstance = RegistryInstance.LOCAL;
    }

    public InstitutionBean(Institution institution, RegistryInstance instance) {
        registryInstance = instance;

        this.setKey(institution.getKey());
        this.setName(institution.getName());
        this.setActiveStatus(institution.getActiveStatus());

        Contact contact = institution.getContactInfo();
        if (contact == null) {
            this.setContactInfo(new Contact());
        } else {
            this.setContactInfo(institution.getContactInfo());
        }

        String description = institution.getDescription();
        if (description == null) {
            description = "";
        }
        this.setDescription(description);

        this.getService().addAll(institution.getService());
    }

    public InstitutionBean(
            Institution institution,
            List<BusinessService> services,
            RegistryInstance instance) {

        registryInstance = instance;

        this.setKey(institution.getKey());
        this.setName(institution.getName());
        this.setActiveStatus(institution.getActiveStatus());

        Contact contact = institution.getContactInfo();
        if (contact == null) {
            this.setContactInfo(new Contact());
        } else {
            this.setContactInfo(institution.getContactInfo());
        }

        String description = institution.getDescription();
        if (description == null) {
            description = "";
        }
        this.setDescription(description);

        this.getService().addAll(services);
    }

    public void setContactInfo(Contact contact) {
        if (contact != null) {
            String contactName = contact.getPersonName();
            if (contactName == null) {
                contact.setPersonName("");
            }

            String email = contact.getEmail();
            if (email == null || email.equals("null")) {
                contact.setEmail("");
            }

            String address = contact.getAddress();
            if (address == null) {
                contact.setAddress("");
            }

            String description = contact.getDescription();
            if (description == null) {
                contact.setDescription("");
            }

            String phone = contact.getPhone();
            if (phone == null) {
                contact.setPhone("");
            }
            super.setContactInfo(contact);
        }
    }

    public RegistryInstance getRegistryInstance() {
        return registryInstance;
    }

    public void setRegistryInstance(RegistryInstance regInstance) {
        registryInstance = regInstance;
    }

    public List<ServiceBean> getServiceBeans() {
        List<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();

        for (BusinessService service : getService()) {
            serviceBeans.add(new ServiceBean(service, getRegistryInstance()));
        }

        return serviceBeans;
    }

}
