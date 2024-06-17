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
package org.nterlearning.registry.persistence;

import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.BindingTypeEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.ServiceTypeEnum;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/6/11
 */
public interface RegistryDao {

    void createInstitution(InstitutionEntity institution);

    void updateInstitution(InstitutionEntity institution);

    void removeInstitution(Long key);

    InstitutionEntity getInstitutionByName(String name);

    InstitutionEntity getInstitutionByKey(Long key);

    InstitutionEntity getInstitutionByServiceKey(Long serviceKey);

    InstitutionEntity getInstitutionByAccessPoint(String endpoint);

    List<InstitutionEntity> getInstitutions();

    List<InstitutionEntity> getInstitutionsByServiceType(
            ServiceTypeEnum serviceType);

    void addService(Long institutionKey, ServiceEntity service);

    void updateService(ServiceEntity service);

    void removeService(Long key);

    List<ServiceEntity> getServicesByName(String name);

    ServiceEntity getServiceByName(Long institutionKey, String name);

    ServiceEntity getServiceByName(String institutionName, String name);

    ServiceEntity getServiceByKey(Long key);

    ServiceEntity getServiceWithBindingKey(Long key);

    List<ServiceEntity> getServices();

    List<ServiceEntity> getServicesByServiceType(ServiceTypeEnum serviceType);

    List<ServiceEntity> getServicesByServiceTypeAndBindingType(
            ServiceTypeEnum serviceType, BindingTypeEnum bindingType);

    void addBinding(Long serviceKey, BindingEntity binding);

    void updateBinding(BindingEntity binding);

    void removeBinding(Long key);

    BindingEntity getBindingByKey(Long key);

    List<BindingEntity> getBindingsByAccessPoint(String endPoint);

    ServiceEntity getBindingsForService(Long serviceKey);

    ServiceEntity getBindingsForService(Long serviceKey, BindingTypeEnum bindingType);

    void addBindingType(BindingTypeEntity bindingType);

    BindingTypeEntity getBindingTypeByName(String name);

    List<BindingTypeEntity> getBindingTypes();

    void addServiceType(ServiceTypeEntity serviceType);

    ServiceTypeEntity getServiceTypeByName(String name);

    List<ServiceTypeEntity> getServiceTypes();

}
