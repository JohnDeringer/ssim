/*
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
package org.nterlearning.registry.client;


import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 3/2/12
 */
public interface Registry {

    /**
     * Create a new Institution
     * @param institution An Institution object
     */
    void createInstitution(Institution institution);

    /**
     * Update and existing institution
     * @param institution An Institution object
     */
    void updateInstitution(Institution institution);

    /**
     * Remove an existing institution. Requires Global Admin
     * @param institutionKey The unique identifier of the institution
     */
    void removeInstitution(Long institutionKey);

    /**
     * Retrieve an institution
     * @param institutionKey The unique identifier of the institution
     * @return An Institution object
     */
    Institution getInstitutionByKey(Long institutionKey);

    /**
     * Retrieve an institution
     * @param institutionName The name of the institution
     * @return An Institution object
     */
    Institution getInstitutionByName(String institutionName);

    /**
     * Retrieve an institution
     * @param endPoint The endPoint of a binding belonging to an institution
     * @return An Institution object
     */
    Institution getInstitutionByAccessPoint(String endPoint);

    /**
     * Retreive a collection of institutions
     * @param status The status of the desired institutions. ActiveStatusEnum.UNSPECIFIED returns all institutions.
     * @return A List of Institution objects
     * @see org.nterlearning.registry.client.ActiveStatusEnum
     */
    List<Institution> getInstitutions(ActiveStatusEnum status);

    /**
     * Add a service to an existing institution
     * @param institutionKey The unique identifier of the institution
     * @param service The service that is being added
     */
    void addService(Long institutionKey, BusinessService service);

    /**
     * Update an existing service
     * @param service The service that is being added
     */
    void updateService(BusinessService service);

    /**
     * Remove an existing service
     * @param key The unique identifier of the service
     */
    void removeService(Long key);

    /**
     * Retrieve an existing service
     * @param institutionKey The unique identifier of the parent institution
     * @param name The name of the service
     * @return A BusinessService object
     */
    BusinessService getServiceByName(Long institutionKey, String name);

    /**
     * Retrieve an existing service
     * @param institutionName The name of the parent institution
     * @param name The name of the service
     * @return A BusinessService object
     */
    BusinessService getServiceByName(String institutionName, String name);

    /**
     * Retrieve an existing service
     * @param serviceKey The unique identifier of the service
     * @return A BusinessService object
     */
    BusinessService getServiceByKey(Long serviceKey);

    /**
     * Retrieve a collection of services
     * @param status The status of the desired services. ActiveStatusEnum.UNSPECIFIED returns all services.
     * @return A List of BusinessService objects
     * @see org.nterlearning.registry.client.ActiveStatusEnum
     */
    List<BusinessService> getServices(ActiveStatusEnum status);

    /**
     * Retrieve a collection of services
     * @param serviceType Filter by serviceType.
     * @param status The status of the desired services. ActiveStatusEnum.UNSPECIFIED is the wild-card.
     * @return A List of BusinessService objects
     * @see org.nterlearning.registry.client.ServiceTypeEnum
     * @see org.nterlearning.registry.client.ActiveStatusEnum
     */
    List<BusinessService> getServicesByServiceType(
            ServiceTypeEnum serviceType, ActiveStatusEnum status);

    /**
     * Retrieve a collection of services
     * @param serviceType Filter by serviceType.
     * @param bindingType Filter by bindingType
     * @param status The status of the desired services. ActiveStatusEnum.UNSPECIFIED is the wild-card.
     * @return A List of BusinessService objects
     * @see org.nterlearning.registry.client.ServiceTypeEnum
     * @see org.nterlearning.registry.client.BindingTypeEnum
     * @see org.nterlearning.registry.client.ActiveStatusEnum
     */
    List<BusinessService> getServicesByServiceTypeAndBindingType(
            ServiceTypeEnum serviceType,
            BindingTypeEnum bindingType, ActiveStatusEnum status);

    /**
     * Add a binding to an existing service
     * @param serviceKey The unique identifier of the service
     * @param binding The binding that is being added
     */
    void addBinding(Long serviceKey, Binding binding);

    /**
     * Remove an existing binding
     * @param bindingKey The unique identifier of the binding
     */
    void removeBinding(Long bindingKey);

    /**
     * Retrieve a collection of service bindings
     * @param endPoint An accessPoint of the service
     * @return A List of Binding objects
     */
    List<Binding> getBindingsByAccessPoint(String endPoint);

    /**
     * Retrieve a service binding
     * @param bindingKey The unique identifier of the binding
     * @return A binding object
     */
    Binding getBindingByKey(Long bindingKey);

    /**
     * Retrieve a collection of bindingTypes
     * @return A List of BindingTypeEnum objects
     */
    List<BindingTypeEnum> getBindingTypes();

    /**
     * Retrieve a collection of serviceType
     * @return A List of ServiceTypeEnum objects
     */
    List<ServiceTypeEnum> getServiceTypes();

}
