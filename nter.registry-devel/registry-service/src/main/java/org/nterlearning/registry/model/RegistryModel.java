/**
 * National Training and Education Resource (NTER)
 * Copyright (C) 2011  SRI International
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
package org.nterlearning.registry.model;

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.*;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/5/11
 */
public interface RegistryModel {

    RequestStatus createInstitution(Institution institution);

    RequestStatus updateInstitution(Institution institution);

    RequestStatus removeInstitution(Long institutionKey);

    Institution getInstitutionByKey(Long institutionKey);

    Institution getInstitutionByName(String name);

    Institution getInstitutionByServiceKey(Long serviceKey);

    Institution getInstitutionByAccessPoint(String endPoint);

    List<Institution> getInstitutions(ActiveStatusEnum status);

    RequestStatus addService(Long institutionKey, BusinessService service);

    RequestStatus updateService(BusinessService service);

    RequestStatus removeService(Long key);

    BusinessService getServiceByName(Long institutionKey, String name);

    BusinessService getServiceByName(String institutionName, String name);

    BusinessService getServiceByKey(Long serviceKey);

    BusinessService getServiceWithBindingKey(Long bindingKey);

    List<BusinessService> getServicesByName(String name);

    List<BusinessService> getServices(ActiveStatusEnum status);

    List<BusinessService> getServicesByServiceType(
            ServiceTypeEnum serviceType, ActiveStatusEnum status);

    List<BusinessService> getServicesByServiceTypeAndBindingType(
            ServiceTypeEnum serviceType,
            BindingTypeEnum bindingType, ActiveStatusEnum status);

    RequestStatus addBinding(Long serviceKey, Binding binding);

    RequestStatus updateBinding(Binding binding);

    RequestStatus removeBinding(Long bindingKey);

    List<Binding> getBindingsByAccessPoint(String endPoint);

    Binding getBindingByKey(Long bindingKey);

    List<BindingTypeEnum> getBindingTypes();

    List<ServiceTypeEnum> getServiceTypes();

}
