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

import org.nterlearning.registry.client.ActiveStatusEnum;
import org.nterlearning.registry.client.Binding;
import org.nterlearning.registry.client.BindingTypeEnum;
import org.nterlearning.registry.client.ServiceTypeEnum;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/23/12
 */
public interface RegistryProxy {

    void createInstitution(InstitutionBean institution);

    void updateInstitution(InstitutionBean institution);

    void removeInstitution(Long institutionKey);

    InstitutionBean getInstitutionFromAccessPoint(String endpoint);

    InstitutionBean getInstitutionByName(String institutionName);

    /**
     * Retrieve a list of institutions
     * @param localStatus The local blacklist status
     * @param globalStatus The global blacklist status
     * @param registryInstance Global or Local institutions
     * @return A List of InstitutionBeans
     */
    List<InstitutionBean> getInstitutions(
            ActiveStatusEnum localStatus,
            ActiveStatusEnum globalStatus, RegistryInstance registryInstance);

    void createService(
            Long institutionKey, ServiceBean service);

    void updateService(ServiceBean service);

    void removeService(Long serviceKey);

    ServiceBean getServiceByName(
            String institutionName, String serviceName);

    /**
     * Retrieve a list of services
     * @param localStatus The local blacklist status
     * @param globalStatus The global blacklist status
     * @param registryInstance Global or Local institutions
     * @return A List of ServiceBeans
     */
    List<ServiceBean> getServices(
            ActiveStatusEnum localStatus,
            ActiveStatusEnum globalStatus, RegistryInstance registryInstance);

    /**
     * Retrieve a list of services of the specified ServiceType
     * @param serviceType A ServiceType enumeration
     * @param localStatus The local blacklist status
     * @param globalStatus The global blacklist status
     * @return A List of ServiceBeans
     */
    List<ServiceBean> getServicesByServiceType(
            ServiceTypeEnum serviceType,
            ActiveStatusEnum localStatus,
            ActiveStatusEnum globalStatus);

    /**
     * Retrieve a list of services of the specified ServiceType and BindingType
     * @param serviceType A ServiceType enumeration
     * @param bindingType A BindingType enumeration
     * @param localStatus The local blacklist status
     * @param globalStatus The global blacklist status
     * @return A List of ServiceBeans
     */
    List<ServiceBean> getServicesByServiceAndBindingType(
            ServiceTypeEnum serviceType,
            BindingTypeEnum bindingType,
            ActiveStatusEnum localStatus,
            ActiveStatusEnum globalStatus);

    void removeBinding(Long serviceKey);

    Binding getBindingByKey(Long bindingKey);

    List<Binding> getBindingsByAccessPoint(String accessPoint);

    /**
     * Returns a list of Service Type's
     * @return A List of ServiceTypeEnum objects
     */
    List<ServiceTypeEnum> getServiceTypes();

    /**
     * Returns a list of Binding Type's
     * @return A List of BindingTypeEnum objects
     */
    List<BindingTypeEnum> getBindingTypes();

    /**
     * Returns a list of Status Type's
     * @return A List of ActiveStatusEnum objects
     */
    List<ActiveStatusEnum> getStatusTypes();

}
