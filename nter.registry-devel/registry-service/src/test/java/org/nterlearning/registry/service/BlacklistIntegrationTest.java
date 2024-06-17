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
package org.nterlearning.registry.service;

import org.nterlearning.xml.nter_registry.blacklist_interface_0_1_0.AddBlacklistItem;
import org.nterlearning.xml.nter_registry.blacklist_interface_0_1_0.RemoveBlacklistItem;
import org.nterlearning.xml.nter_registry.blacklist_interface_0_1_0.UpdateBlacklistItem;

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.BlacklistItem;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.*;
import org.nterlearning.xml.nter_registry.registry_interface_0_1_0.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/18/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:registry-beans-test.xml")
public class BlacklistIntegrationTest {

    @Autowired
    RegistryServiceImpl registryService;
    @Autowired
    BlacklistApiImpl blacklist;

    @Test
    public void createService() {
        // Create institution
        CreateInstitution request = new CreateInstitution();

        Institution institution = new Institution();
        String institutionName = getRandomValue("institution");
        institution.setName(institutionName);
        institution.setDescription(getRandomValue("description"));
        institution.setActiveStatus(ActiveStatusEnum.ACTIVE);

        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("PersonName"));
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);

        request.setInstitution(institution);
        // persist
        CreateInstitutionResponse response =
                registryService.createInstitution(request);

        Assert.isTrue(response.getStatus() == RequestStatus.SUCCESS,
                "Unexpected Response [" + response.getStatus().value() + "]");

        // Retrieve institution
        GetInstitutionByName getInstitutionByName = new GetInstitutionByName();
        getInstitutionByName.setInstitutionName(institutionName);
        GetInstitutionByNameResponse getInstitutionByNameResponse =
                registryService.getInstitutionByName(getInstitutionByName);
        institution = getInstitutionByNameResponse.getInstitution();
        Assert.isTrue(institution != null, "'Null' Institution");

        // Create service
        CreateService createService = new CreateService();
        createService.setInstitutionKey(institution.getKey());

        BusinessService service1 = new BusinessService();
        // ServiceType
        service1.setServiceType(ServiceTypeEnum.SEARCH_INDEXER);
        String serviceName1 = getRandomValue("serviceName");
        service1.setName(serviceName1);
        service1.setDescription(getRandomValue("Description"));
        service1.setActiveStatus(ActiveStatusEnum.ACTIVE);

        createService.setService(service1);
        registryService.createService(createService);

        // Retrieve service
        GetServiceByName getServiceByName = new GetServiceByName();
        getServiceByName.setInstitutionName(institution.getName());
        getServiceByName.setServiceName(serviceName1);
        GetServiceByNameResponse getServiceByNameResponse =
                registryService.getServiceByName(getServiceByName);
        service1 = getServiceByNameResponse.getService();
        Assert.isTrue(service1 != null, "getServiceByName returned 'Null'");
        Assert.isTrue(service1.getName().equals(serviceName1),
            "Incorrect service name [" + service1.getName() +
                "] expected [" + serviceName1 + "]");
        Assert.isTrue(service1.getInstitutionName().equals(institutionName),
                "Expected institutionName [" + institutionName +
                "] but received [" + service1.getInstitutionName() + "]");

        // Add another service with binding
        createService = new CreateService();
        createService.setInstitutionKey(institution.getKey());

        BusinessService service2 = new BusinessService();
        // ServiceType
        service2.setServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);
        String serviceName2 = getRandomValue("serviceName");
        service2.setName(serviceName2);
        service2.setDescription(getRandomValue("Description"));
        service2.setActiveStatus(ActiveStatusEnum.ACTIVE);

        // Add a binding to the service
        Binding binding = new Binding();
        // BindingType
        binding.setBindingType(BindingTypeEnum.COURSE_CATALOG_ATOM);
        String endpoint = getRandomValue("http://someEndPoint");
        binding.setAccessPoint(endpoint);
        service2.getBinding().add(binding);

        createService.setService(service2);
        registryService.createService(createService);

        // Retrieve institution - EndPoint
        GetInstitutionByAccessPoint getInstitutionByAccessPoint = new GetInstitutionByAccessPoint();
        getInstitutionByAccessPoint.setEndPoint(endpoint);
        GetInstitutionByAccessPointResponse getInstitutionByAccessPointResponse =
                registryService.getInstitutionByAccessPoint(getInstitutionByAccessPoint);
        institution = getInstitutionByAccessPointResponse.getInstitution();
        Assert.isTrue(institution != null, "'Null' Institution");

        // Retrieve service
        getServiceByName = new GetServiceByName();
        getServiceByName.setInstitutionName(institution.getName());
        getServiceByName.setServiceName(serviceName2);
        getServiceByNameResponse =
                registryService.getServiceByName(getServiceByName);
        service2 = getServiceByNameResponse.getService();
        Assert.isTrue(service2 != null, "getServiceByName returned 'Null'");
        Assert.isTrue(service2.getName().equals(serviceName2),
            "Incorrect service name [" + service2.getName() +
                "] expected [" + serviceName2 + "]");
        Assert.isTrue(service2.getInstitutionName().equals(institutionName),
                "Expected institutionName [" + institutionName +
                "] but received [" + service2.getInstitutionName() + "]");

        Assert.isTrue(service2.getBinding() != null, "Expected 1 binding but received: 'Null' ");
        Assert.isTrue(service2.getBinding().size() == 1,
            "Expected 1 binding but received [" + service2.getBinding().size() + "]");
        Assert.isTrue(service2.getBinding().get(0).getAccessPoint().equals(endpoint),
            "Expected endpont [" + endpoint + "] but received [" +
                    service2.getBinding().get(0).getAccessPoint() + "]");

        /** Clean-up **/

        // Remove service2
        RemoveService removeService = new RemoveService();
        //removeService.setAuthToken(getAuthToken());
        removeService.setServiceKey(service2.getKey());
        registryService.removeService(removeService);
        // Remove service1
        removeService = new RemoveService();
        //removeService.setAuthToken(getAuthToken());
        removeService.setServiceKey(service1.getKey());
        registryService.removeService(removeService);
        // Remove institution
        RemoveInstitution removeInstitution = new RemoveInstitution();
        removeInstitution.setInstitutionKey(institution.getKey());
        registryService.removeInstitution(removeInstitution);

        // Ensure serviceTypes were not removed
        GetServiceTypes getServiceTypes = new GetServiceTypes();
        GetServiceTypesResponse getServiceTypesResponse =
                registryService.getServiceTypes(getServiceTypes);
        Assert.isTrue(getServiceTypesResponse.getServiceType() != null,
            "Expected [" + ServiceTypeEnum.values().length +
                    "] but received 'Null'");
        Assert.isTrue(ServiceTypeEnum.values().length ==
                getServiceTypesResponse.getServiceType().size(),
            "Expected [" + ServiceTypeEnum.values().length +
                    "] but received [" +
                    getServiceTypesResponse.getServiceType().size() + ']');
    }

    @Test
    public void getServiceTypes() {
        GetServiceTypes getServiceTypes = new GetServiceTypes();
        GetServiceTypesResponse response =
                registryService.getServiceTypes(getServiceTypes);

        Assert.isTrue(response.getServiceType() != null,
            "Expected [" + ServiceTypeEnum.values().length +
                    "] but received 'Null'");
        Assert.isTrue(ServiceTypeEnum.values().length ==
                response.getServiceType().size(),
            "Expected [" + ServiceTypeEnum.values().length +
                    "] but received [" + response.getServiceType().size() + ']');
    }

    @Test
    public void getBindingTypes() {
        GetBindingTypes getBindingTypes = new GetBindingTypes();
        GetBindingTypesResponse response =
                registryService.getBindingTypes(getBindingTypes);

        Assert.isTrue(response.getBindingType() != null,
            "Expected [" + BindingTypeEnum.values().length +
                    "] but received 'Null'");
        Assert.isTrue(BindingTypeEnum.values().length ==
                response.getBindingType().size(),
            "Expected [" + BindingTypeEnum.values().length +
                    "] but received [" + response.getBindingType().size() + ']');
    }

    @Test
    public void getStatusTypes() {
        GetStatusTypes getStatusTypes = new GetStatusTypes();
        GetStatusTypesResponse response =
                registryService.getStatusTypes(getStatusTypes);

        Assert.isTrue(response.getActiveStatus() != null,
            "Expected [" + ActiveStatusEnum.values().length +
                    "] but received 'Null'");
        Assert.isTrue(ActiveStatusEnum.values().length ==
                response.getActiveStatus().size(),
            "Expected [" + ActiveStatusEnum.values().length +
                    "] but received [" + response.getActiveStatus().size() + ']');
    }

    @Test
    public void getServicesByServiceAndBindingType() {
        // Create institution
        CreateInstitution request = new CreateInstitution();

        Institution institution = new Institution();
        String institutionName = getRandomValue("institution");
        institution.setName(institutionName);
        institution.setDescription(getRandomValue("description"));

        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("PersonName"));
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);

        institution.setActiveStatus(ActiveStatusEnum.ACTIVE);

        request.setInstitution(institution);
        // persist
        CreateInstitutionResponse response =
                registryService.createInstitution(request);

        Assert.isTrue(response.getStatus() == RequestStatus.SUCCESS,
                "Unexpected Response [" + response.getStatus().value() + "]");

        // Retrieve institution
        GetInstitutionByName getInstitutionByName = new GetInstitutionByName();
        getInstitutionByName.setInstitutionName(institutionName);
        GetInstitutionByNameResponse getInstitutionByNameResponse =
                registryService.getInstitutionByName(getInstitutionByName);
        institution = getInstitutionByNameResponse.getInstitution();
        Assert.isTrue(institution != null, "'Null' Institution");

        // Create service1
        CreateService createService = new CreateService();
        createService.setInstitutionKey(institution.getKey());

        BusinessService service1 = new BusinessService();
        service1.setServiceType(ServiceTypeEnum.COURSE_REVIEWS);
        String serviceName1 = getRandomValue("serviceName");
        service1.setName(serviceName1);
        service1.setDescription(getRandomValue("Description"));
        service1.setActiveStatus(ActiveStatusEnum.ACTIVE);

        // Add a binding to the service
        Binding binding = new Binding();
        binding.setBindingType(BindingTypeEnum.COURSE_CATALOG_ATOM);
        String endpoint = getRandomValue("http://someEndPoint");
        binding.setAccessPoint(endpoint);
        service1.getBinding().add(binding);

        createService.setService(service1);
        registryService.createService(createService);

        // Retrieve service1
        GetServiceByName getServiceByName = new GetServiceByName();
        getServiceByName.setInstitutionName(institution.getName());
        getServiceByName.setServiceName(serviceName1);
        GetServiceByNameResponse getServiceByNameResponse =
                registryService.getServiceByName(getServiceByName);
        service1 = getServiceByNameResponse.getService();
        Assert.isTrue(service1 != null, "getServiceByName returned 'Null'");
        Assert.isTrue(service1.getName().equals(serviceName1),
            "Incorrect service name [" + service1.getName() +
                "] expected [" + serviceName1 + "]");
        Assert.isTrue(service1.getInstitutionName().equals(institutionName),
                "Expected institutionName [" + institutionName +
                "] but received [" + service1.getInstitutionName() + "]");

        // Create service2
        createService = new CreateService();
        //createService.setAuthToken(authToken);
        createService.setInstitutionKey(institution.getKey());

        BusinessService service2 = new BusinessService();
        // ServiceType
        service2.setServiceType(ServiceTypeEnum.COURSE_REVIEWS);
        String serviceName2 = getRandomValue("serviceName");
        service2.setName(serviceName2);
        service2.setDescription(getRandomValue("Description"));

        service2.setActiveStatus(ActiveStatusEnum.ACTIVE);

        // Add a binding to the service
        Binding binding2 = new Binding();
        // BindingType
        binding2.setBindingType(BindingTypeEnum.ACTIVITY_STREAM_ATOM);
        String endpoint2 = getRandomValue("http://someEndPoint");
        binding2.setAccessPoint(endpoint2);
        service2.getBinding().add(binding2);

        createService.setService(service2);
        registryService.createService(createService);

        // Retrieve service2
        getServiceByName = new GetServiceByName();
        getServiceByName.setInstitutionName(institution.getName());
        getServiceByName.setServiceName(serviceName2);
        getServiceByNameResponse =
                registryService.getServiceByName(getServiceByName);
        service2 = getServiceByNameResponse.getService();
        Assert.isTrue(service2 != null, "getServiceByName returned 'Null'");
        Assert.isTrue(service2.getName().equals(serviceName2),
            "Incorrect service name [" + service2.getName() +
                "] expected [" + serviceName2 + "]");
        Assert.isTrue(service2.getInstitutionName().equals(institutionName),
                "Expected institutionName [" + institutionName +
                "] but received [" + service2.getInstitutionName() + "]");

        // Retrieve the services
        GetServicesByServiceAndBindingType getServicesRequest =
                new GetServicesByServiceAndBindingType();
        // Set the serviceType
        getServicesRequest.setServiceType(ServiceTypeEnum.COURSE_REVIEWS);
        // Set the binding type
        getServicesRequest.setBindingType(BindingTypeEnum.ACTIVITY_STREAM_ATOM);
        // Set the activeStatus
        getServicesRequest.setStatus(ActiveStatusEnum.ACTIVE);
        // Make the call
        GetServicesByServiceAndBindingTypeResponse getServicesResponse =
                registryService.getServicesByServiceAndBindingType(getServicesRequest);

        Assert.isTrue(getServicesResponse.getServices() != null,
                "getServicesByServiceAndBindingType returned 'Null'");
        Assert.isTrue(getServicesResponse.getServices().size() == 1,
            "getServicesByServiceAndBindingType returned [" +
                getServicesResponse.getServices().size() +
                    "] but expected [1]");

        /** Clean-up **/

        // Remove service2
        RemoveService removeService = new RemoveService();
        //removeService.setAuthToken(getAuthToken());
        removeService.setServiceKey(service2.getKey());
        registryService.removeService(removeService);
        // Remove service1
        removeService = new RemoveService();
        //removeService.setAuthToken(getAuthToken());
        removeService.setServiceKey(service1.getKey());
        registryService.removeService(removeService);
        // Remove institution
        RemoveInstitution removeInstitution = new RemoveInstitution();
        removeInstitution.setInstitutionKey(institution.getKey());
        registryService.removeInstitution(removeInstitution);
    }

    @Test
    public void getActiveInactiveInstitutions() throws Exception {
        // Initial count - Active Institutions
        GetInstitutions getInstitutions = new GetInstitutions();
        // Blacklist status
        getInstitutions.setStatus(ActiveStatusEnum.ACTIVE);
        GetInstitutionsResponse getInstitutionsResponse =
                registryService.getInstitutions(getInstitutions);
        int initialActiveCount = getInstitutionsResponse.getInstitution().size();

        // Create an institution
        CreateInstitution createInstitution = new CreateInstitution();

        // Institution
        String institutionName1 = getRandomValue("Institution");
        Institution institution1 = new Institution();
        institution1.setName(institutionName1);
        institution1.setActiveStatus(ActiveStatusEnum.ACTIVE);
        // Contact Info
        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("PersonName"));
        contact.setEmail("active@nter.com");
        institution1.setContactInfo(contact);
        // Add institution to document wrapper
        createInstitution.setInstitution(institution1);
        // Persist
        registryService.createInstitution(createInstitution);

        // Retrieve institutions - should be one 'Active'
        getInstitutions = new GetInstitutions();
        // Blacklist status
        getInstitutions.setStatus(ActiveStatusEnum.ACTIVE);
        getInstitutionsResponse =
                registryService.getInstitutions(getInstitutions);
        Assert.isTrue(getInstitutionsResponse.getInstitution().size() == (initialActiveCount + 1),
                "Expected [" + (initialActiveCount + 1) + "] institutions but received [" +
                        getInstitutionsResponse.getInstitution().size() + "]");


        // Initial count - Inactive Institutions
        getInstitutions = new GetInstitutions();
        // Blacklist status
        getInstitutions.setStatus(ActiveStatusEnum.INACTIVE);
        getInstitutionsResponse =
                registryService.getInstitutions(getInstitutions);
        int initialInactiveCount = getInstitutionsResponse.getInstitution().size();

        // Create an institution
        createInstitution = new CreateInstitution();

        // Institution
        String institutionName2 = getRandomValue("Institution");
        Institution institution2 = new Institution();
        institution2.setName(institutionName2);
        institution2.setActiveStatus(ActiveStatusEnum.INACTIVE);
        // Contact Info
        contact = new Contact();
        contact.setPersonName(getRandomValue("PersonName"));
        contact.setEmail("inactive@nter.com");
        institution2.setContactInfo(contact);
        // Add institution to document wrapper
        createInstitution.setInstitution(institution2);
        // Persist
        registryService.createInstitution(createInstitution);

        // Retrieve institutions - should be one 'Active'
        getInstitutions = new GetInstitutions();
        // Blacklist status
        getInstitutions.setStatus(ActiveStatusEnum.INACTIVE);
        getInstitutionsResponse =
                registryService.getInstitutions(getInstitutions);
        Assert.isTrue(getInstitutionsResponse.getInstitution().size() == (initialInactiveCount + 1),
                "Expected [" + (initialInactiveCount + 1) + "] institutions but received [" +
                        getInstitutionsResponse.getInstitution().size() + "]");


    }

    @Test
    public void getInstitutions() throws Exception {
        // Initial count
        GetInstitutions getInstitutions = new GetInstitutions();
        // Blacklist status
        getInstitutions.setStatus(ActiveStatusEnum.ACTIVE);
        GetInstitutionsResponse getInstitutionsResponse =
                registryService.getInstitutions(getInstitutions);

        int initialCount = getInstitutionsResponse.getInstitution().size();

        // Create an institution
        CreateInstitution createInstitution = new CreateInstitution();

        // Institution
        String institutionName1 = getRandomValue("Institution");
        Institution institution1 = new Institution();
        institution1.setName(institutionName1);
        institution1.setDescription(getRandomValue("Description"));
        // Contact Info
        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("PersonName"));
        institution1.setContactInfo(contact);
        // Add institution to document wrapper
        createInstitution.setInstitution(institution1);
        // Persist
        registryService.createInstitution(createInstitution);

        // Create another institution
        createInstitution = new CreateInstitution();

        // Institution
        String institutionName2 = getRandomValue("Institution");
        Institution institution2 = new Institution();
        institution2.setName(institutionName2);
        institution2.setDescription(getRandomValue("Description"));
        // Contact Info
        contact = new Contact();
        contact.setPersonName(getRandomValue("PersonName"));
        institution2.setContactInfo(contact);
        // Add institution to document wrapper
        createInstitution.setInstitution(institution2);
        // Persist
        registryService.createInstitution(createInstitution);

        // Retrieve institution1
        GetInstitutionByName getInstitutionByName = new GetInstitutionByName();
        getInstitutionByName.setInstitutionName(institutionName1);
        GetInstitutionByNameResponse getInstitutionByNameResponse1 =
                registryService.getInstitutionByName(getInstitutionByName);
        Assert.isTrue(getInstitutionByNameResponse1.getInstitution() != null,
                "Expected Institution [" + institutionName1 + "]");
        Assert.isTrue(getInstitutionByNameResponse1.getInstitution().getName().equals(institutionName1),
                "Expected Institution [" + institutionName1 +
                        "] but received [" +
                        getInstitutionByNameResponse1.getInstitution().getName() + "]");

        // Set Blacklist status for institution1 - Active
        institution1 = getInstitutionByNameResponse1.getInstitution();

        // Retrieve institution2
        getInstitutionByName = new GetInstitutionByName();
        getInstitutionByName.setInstitutionName(institutionName2);
        GetInstitutionByNameResponse getInstitutionByNameResponse2 =
                registryService.getInstitutionByName(getInstitutionByName);
        Assert.isTrue(getInstitutionByNameResponse2.getInstitution() != null,
                "Expected Institution [" + institutionName2 + "]");
        Assert.isTrue(
                getInstitutionByNameResponse2.getInstitution().getName().equals(
                        institutionName2),
                "Expected Institution [" + institutionName2 +
                        "] but received [" +
                        getInstitutionByNameResponse2.getInstitution().getName() + "]");

        // Set Blacklist status for institution2 - Active
        institution2 = getInstitutionByNameResponse2.getInstitution();

        // Retrieve institutions - should be two active
        getInstitutions = new GetInstitutions();
        // Blacklist status
        getInstitutions.setStatus(ActiveStatusEnum.ACTIVE);
        getInstitutionsResponse =
                registryService.getInstitutions(getInstitutions);

        Assert.isTrue(getInstitutionsResponse.getInstitution().size() == (initialCount + 2),
                "Expected [" + (initialCount + 2) + "] institutions but received [" +
                        getInstitutionsResponse.getInstitution().size() + "]");

        // Blacklist institution1
        institution1 = getInstitutionByNameResponse1.getInstitution();

        // Retrieve institutions - should be one 'Active'
        getInstitutions = new GetInstitutions();
        // Blacklist status
        getInstitutions.setStatus(ActiveStatusEnum.ACTIVE);
        getInstitutionsResponse =
                registryService.getInstitutions(getInstitutions);
        Assert.isTrue(getInstitutionsResponse.getInstitution().size() == (initialCount + 1),
                "Expected [" + (initialCount + 1) + "] institutions but received [" +
                        getInstitutionsResponse.getInstitution().size() + "]");

        boolean foundInstitution = false;
        List<Institution> institutions = getInstitutionsResponse.getInstitution();
        for (Institution institution : institutions) {
            if (institution.getName().equals(institution2.getName())) {
                foundInstitution = true;
                break;
            }
        }
        Assert.isTrue(
            foundInstitution,
                "Expected Institution [" + institution2.getName() + "]");

        // Clean-up

        RemoveInstitution removeInstitution = new RemoveInstitution();
        //removeInstitution.setAuthToken(authToken);
        removeInstitution.setInstitutionKey(institution1.getKey());
        registryService.removeInstitution(removeInstitution);

        removeInstitution = new RemoveInstitution();
        //removeInstitution.setAuthToken(authToken);
        removeInstitution.setInstitutionKey(
                getInstitutionByNameResponse2.getInstitution().getKey());
        registryService.removeInstitution(removeInstitution);

    }

    @Test
    public void createServices() throws Exception {
        // Initial active count
        GetServices getServices = new GetServices();
        getServices.setStatus(ActiveStatusEnum.ACTIVE);
        GetServicesResponse getServicesResponse =
                registryService.getServices(getServices);
        int initialActiveCount = getServicesResponse.getServices().size();
        // Initial inActive count
        getServices = new GetServices();
        getServices.setStatus(ActiveStatusEnum.INACTIVE);
        getServicesResponse =
                registryService.getServices(getServices);
        int initialInactiveCount = getServicesResponse.getServices().size();

         // Create an institution
        CreateInstitution createInstitution = new CreateInstitution();
        // Institution
        String institutionName = getRandomValue("Institution");
        Institution institution = new Institution();
        institution.setName(institutionName);
        institution.setDescription(getRandomValue("Description"));
        institution.setActiveStatus(ActiveStatusEnum.ACTIVE);

        System.out.println("Creating institution [" + institutionName + "]");

        // Contact Info
        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("PersonName"));
        institution.setContactInfo(contact);

        // Add the institution to the request wrapper
        createInstitution.setInstitution(institution);
        // Persist
        CreateInstitutionResponse createInstitutionResponse =
                registryService.createInstitution(createInstitution);

        Assert.isTrue(createInstitutionResponse.getStatus() == RequestStatus.SUCCESS,
                "Received unexpected value [" + createInstitutionResponse.getStatus() + "]");

        // Retrieve institution1
        GetInstitutionByName getInstitutionByName = new GetInstitutionByName();
        getInstitutionByName.setInstitutionName(institutionName);
        GetInstitutionByNameResponse getInstitutionByNameResponse1 =
                registryService.getInstitutionByName(getInstitutionByName);
        institution = getInstitutionByNameResponse1.getInstitution();
        Assert.isTrue(institution != null,
                "Institution is 'Null', Expected Institution [" + institutionName + "]");

        // Add Service1
        CreateService createService = new CreateService();
        BusinessService service1 = new BusinessService();
        String serviceName1 = getRandomValue("service");
        service1.setName(serviceName1);
        service1.setInstitutionName(institutionName);
        service1.setActiveStatus(ActiveStatusEnum.ACTIVE);
        // ServiceType
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_PORTAL;
        service1.setServiceType(serviceType);
        createService.setService(service1);

        CreateServiceResponse createServiceResponse =
                registryService.createService(createService);

        Assert.isTrue(createServiceResponse.getStatus() == RequestStatus.SUCCESS,
                "Received unexpected value [" + createServiceResponse.getStatus() + "]");

        // Add Service2
        createService = new CreateService();
        BusinessService service2 = new BusinessService();
        String serviceName2 = getRandomValue("service");
        service2.setName(serviceName2);
        service2.setInstitutionName(institutionName);
        service2.setActiveStatus(ActiveStatusEnum.INACTIVE);
        // ServiceType
        serviceType = ServiceTypeEnum.NTER_PORTAL;
        service2.setServiceType(serviceType);
        createService.setService(service2);

        createServiceResponse =
                registryService.createService(createService);

        Assert.isTrue(createServiceResponse.getStatus() == RequestStatus.SUCCESS,
                "Received unexpected value [" + createServiceResponse.getStatus() + "]");

        // Retrieve Active services
        getServices = new GetServices();
        getServices.setStatus(ActiveStatusEnum.ACTIVE);
        getServicesResponse =
                registryService.getServices(getServices);
        List<BusinessService> activeServices = getServicesResponse.getServices();
        int numberOfServices = activeServices.size();
        Assert.isTrue((numberOfServices - initialActiveCount) == 1,
                "Expected 1 service but received [" +
                        (numberOfServices - initialActiveCount) + "]");

        // Retrieve Inactive services
        getServices = new GetServices();
        getServices.setStatus(ActiveStatusEnum.INACTIVE);
        getServicesResponse =
                registryService.getServices(getServices);
        List<BusinessService> inActiveServices = getServicesResponse.getServices();
        numberOfServices = inActiveServices.size();
        Assert.isTrue((numberOfServices - initialInactiveCount) == 1,
                "Expected 1 service but received [" +
                        (numberOfServices - initialInactiveCount) + "]");

        // Clean-up
        RemoveInstitution removeInstitution = new RemoveInstitution();
        removeInstitution.setInstitutionKey(institution.getKey());
        registryService.removeInstitution(removeInstitution);
    }

    @Test
    public void getServices() throws Exception {
        // Initial count
        ActiveStatusEnum activeStatus = ActiveStatusEnum.ACTIVE;
        GetServices getServices = new GetServices();
        getServices.setStatus(activeStatus);
        GetServicesResponse getServicesResponse =
                registryService.getServices(getServices);
        int initialCount = getServicesResponse.getServices().size();

         // Create an institution
        CreateInstitution createInstitution = new CreateInstitution();

        // Institution
        String institutionName = getRandomValue("Institution");
        Institution institution = new Institution();
        institution.setName(institutionName);
        institution.setDescription(getRandomValue("Description"));

        // Add institution to blacklist - activeStatus
        BlacklistItem blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institutionName);
        // Blacklist status
        ActiveStatusEnum status_active = ActiveStatusEnum.ACTIVE;
        blacklistItem.setStatus(status_active);
        AddBlacklistItem addBlacklistItem = new AddBlacklistItem();
        //addBlacklistItem.setAuthToken(authToken);
        addBlacklistItem.setBlacklistItem(blacklistItem);
        blacklist.addBlacklistItem(addBlacklistItem);

        // Add a Service
        BusinessService service1 = new BusinessService();
        String serviceName1 = getRandomValue("serviceName1");
        service1.setName(serviceName1);
        // ServiceType
        ServiceTypeEnum serviceType = ServiceTypeEnum.SEARCH_INDEXER;
        service1.setServiceType(serviceType);

        // Add service to blacklist - activeStatus
        blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institutionName);
        blacklistItem.setService(serviceName1);
        blacklistItem.setStatus(status_active);
        addBlacklistItem = new AddBlacklistItem();
        addBlacklistItem.setBlacklistItem(blacklistItem);
        blacklist.addBlacklistItem(addBlacklistItem);

        // Add the service to the institution
        institution.getService().add(service1);

        // Contact Info
        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("PersonName"));
        institution.setContactInfo(contact);
        // Add the institution to the request wrapper
        createInstitution.setInstitution(institution);
        // Persist
        registryService.createInstitution(createInstitution);

        // Retrieve institution1
        GetInstitutionByName getInstitutionByName = new GetInstitutionByName();
        getInstitutionByName.setInstitutionName(institutionName);
        GetInstitutionByNameResponse getInstitutionByNameResponse1 =
                registryService.getInstitutionByName(getInstitutionByName);
        Assert.isTrue(getInstitutionByNameResponse1.getInstitution() != null,
                "Institution is 'Null', Expected Institution [" + institutionName + "]");

        institution = getInstitutionByNameResponse1.getInstitution();

        // Retrieve service1
        GetServiceByName getServiceByName = new GetServiceByName();
        getServiceByName.setInstitutionName(institution.getName());
        getServiceByName.setServiceName(serviceName1);
        GetServiceByNameResponse getServiceByNameResponse =
                registryService.getServiceByName(getServiceByName);
        // Test service is not null
        Assert.isTrue(getServiceByNameResponse.getService() != null,
                "Expected Service [" + serviceName1 + "]");
        // Test Service name
        Assert.isTrue(getServiceByNameResponse.getService().getName().equals(serviceName1),
                "Expected Service [" + serviceName1 + "] but received [" +
                getServiceByNameResponse.getService().getName() + "]");
        // Test institution name
        Assert.isTrue(getServiceByNameResponse.getService().getInstitutionName().equals(institutionName),
                "Expected institutionName [" + institutionName +
                "] but received [" + getServiceByNameResponse.getService().getInstitutionName() + "]");

        // Add another service
        CreateService createService = new CreateService();
        createService.setInstitutionKey(institution.getKey());

        BusinessService service2 = new BusinessService();
        String serviceName2 = getRandomValue("serviceName2");
        service2.setName(serviceName2);
        service2.setServiceType(serviceType);
        createService.setService(service2);
        // Persist
        registryService.createService(createService);

        // Add service to blacklist - activeStatus
        blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institutionName);
        blacklistItem.setService(serviceName2);
        blacklistItem.setStatus(status_active);
        addBlacklistItem = new AddBlacklistItem();
        addBlacklistItem.setBlacklistItem(blacklistItem);
        blacklist.addBlacklistItem(addBlacklistItem);

        // Retrieve service2
        GetServiceByName getServiceByName2 = new GetServiceByName();
        getServiceByName2.setInstitutionName(institution.getName());
        getServiceByName2.setServiceName(serviceName2);
        // Retrieve service by name
        GetServiceByNameResponse getServiceByNameResponse2 =
                registryService.getServiceByName(getServiceByName2);
        // Test for null
        Assert.isTrue(getServiceByNameResponse2.getService() != null,
                "Expected Service [" + serviceName2 + "]");
        // Test for correct service name
        Assert.isTrue(getServiceByNameResponse2.getService().getName().equals(serviceName2),
                "Expected Service [" + serviceName2 + "] but received [" +
                getServiceByNameResponse2.getService().getName() + "]");
        // Test for correct institution name
        Assert.isTrue(getServiceByNameResponse2.getService().getInstitutionName().equals(institutionName),
                "Expected institutionName [" + institutionName +
                "] but received [" + getServiceByNameResponse2.getService().getInstitutionName() + "]");

        // Ensure that there are two services
        getServices.setStatus(activeStatus);
        getServicesResponse =
                registryService.getServices(getServices);
        Assert.isTrue(getServicesResponse.getServices().size() == (initialCount + 2),
                "Expected [" + (initialCount + 2) + "] Services but received [" +
                        getServicesResponse.getServices().size() + "]");

        // Blacklist a service
        blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institutionName);
        blacklistItem.setService(serviceName1);
        // Blacklist status
        ActiveStatusEnum status_black = ActiveStatusEnum.BLACKLIST;
        blacklistItem.setStatus(status_black);
        UpdateBlacklistItem updateBlacklistItem = new UpdateBlacklistItem();
        updateBlacklistItem.setBlacklistItem(blacklistItem);
        blacklist.updateBlacklistItem(updateBlacklistItem);

        // Ensure that there is only one service returned - service2
        getServices = new GetServices();
        getServices.setStatus(activeStatus);
        getServicesResponse =
                registryService.getServices(getServices);
        Assert.isTrue(getServicesResponse.getServices().size() == (initialCount + 1),
                "Expected [" + (initialCount + 1) + "] Services but received [" +
                        getServicesResponse.getServices().size() + "]");

        boolean foundInstitution = false;
        List<BusinessService> services = getServicesResponse.getServices();
        for (BusinessService service : services) {
            if (service.getName().equals(serviceName2)) {
                foundInstitution = true;
                break;
            }
        }
        Assert.isTrue(
            foundInstitution,
                "Expected service [" + serviceName2 + "]");

        // Clean-up
        RemoveBlacklistItem removeBlacklistItem = new RemoveBlacklistItem();
        removeBlacklistItem.setInstitution(institutionName);
        removeBlacklistItem.setService(serviceName1);
        blacklist.removeBlacklistItem(removeBlacklistItem);

        removeBlacklistItem = new RemoveBlacklistItem();
        removeBlacklistItem.setInstitution(institutionName);
        removeBlacklistItem.setService(serviceName2);
        blacklist.removeBlacklistItem(removeBlacklistItem);

        RemoveService removeService = new RemoveService();
        removeService.setServiceKey(getServiceByNameResponse.getService().getKey());
        registryService.removeService(removeService);

        removeService = new RemoveService();
        removeService.setServiceKey(getServiceByNameResponse2.getService().getKey());
        registryService.removeService(removeService);

        RemoveInstitution removeInstitution = new RemoveInstitution();
        removeInstitution.setInstitutionKey(institution.getKey());
        registryService.removeInstitution(removeInstitution);
    }

    private String getRandomValue(String value) {
        Random r = new Random();
        return value + ":" + r.nextInt();
    }

}
