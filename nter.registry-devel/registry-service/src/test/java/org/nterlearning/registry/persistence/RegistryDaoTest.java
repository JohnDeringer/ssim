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
 *         Date: 10/7/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:registry-beans-test.xml")
public class RegistryDaoTest {

    @Autowired
    private RegistryDao registryDao;

    @Test
    public void createInstitution() throws Exception {
        // Create an institution
        String institutionName1 = getRandomValue("Institution");
        InstitutionEntity institution1 = new InstitutionEntity();
        institution1.setName(institutionName1);
        institution1.setDescription(getRandomValue("Description"));
        registryDao.createInstitution(institution1);

        // Test
        institution1 =
                registryDao.getInstitutionByName(institutionName1);
        Assert.isTrue(
            institution1 != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution1.getName().equals(institutionName1),
                "Institution name [" + institution1.getName() +
                 "] does not match the expected name [" + institutionName1 + "]");

        // Create an institution with Contact Info
        String institutionName2 = getRandomValue("Institution");
        InstitutionEntity institution2 = new InstitutionEntity();
        institution2.setName(institutionName2);
        institution2.setDescription(getRandomValue("Description"));

        ContactEntity contact = new ContactEntity();
        String personName = getRandomValue("PersonName");
        contact.setPersonName(personName);
        contact.setDescription(getRandomValue("Description"));
        contact.setEmail(getRandomValue("Email"));
        contact.setPhone(getRandomValue("Phone"));
        contact.setAddress(getRandomValue("Address"));
        institution2.setContactInfo(contact);

        registryDao.createInstitution(institution2);

        // Test
        institution2 =
                registryDao.getInstitutionByName(institutionName2);
        Assert.isTrue(
            institution2 != null,
                "getInstitution returned 'Null'");
        ContactEntity storedContact = institution2.getContactInfo();
        Assert.isTrue(
            storedContact != null,
                "getContactInfo returned 'Null'");
        Assert.isTrue(
            storedContact.getPersonName().equals(personName),
                "Contact name [" + storedContact.getPersonName() +
                 "] does not match the expected name [" + personName + "]");

        // Create an institution with services
        String institutionName3 = getRandomValue("Institution");
        InstitutionEntity institution3 = new InstitutionEntity();
        institution3.setName(institutionName3);
        institution3.setDescription(getRandomValue("Description"));

        ServiceEntity service = new ServiceEntity();
        String serviceName = getRandomValue("Service");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));
        ServiceTypeEntity serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());
        service.setServiceType(serviceType);
        institution3.getService().add(service);

        registryDao.createInstitution(institution3);

        // Test
        institution3 =
                registryDao.getInstitutionByName(institutionName3);
        Assert.isTrue(
            institution3 != null,
                "getInstitution returned 'Null'");
        ServiceEntity storedService = institution3.getService().get(0);
        Assert.isTrue(
            storedService.getName().equals(serviceName),
                "Service name [" + storedService.getName() +
                 "] does not match the expected name [" + serviceName + "]");

        // Create an institution with services and bindings
        String institutionName4 = getRandomValue("Institution");
        InstitutionEntity institution4 = new InstitutionEntity();
        institution4.setName(institutionName4);
        institution4.setDescription(getRandomValue("Description"));
        // Add a service
        service = new ServiceEntity();
        serviceName = getRandomValue("Service");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));
        serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());
        service.setServiceType(serviceType);
        // Add binding
        BindingEntity binding = new BindingEntity();
        binding.setDescription(getRandomValue("Description"));
        BindingTypeEntity bindingType =
                registryDao.getBindingTypeByName(BindingTypeEnum.ACTIVITY_STREAM_ATOM.value());
        binding.setBindingType(bindingType);
        String endPoint = getRandomValue("http://endpoint");
        binding.setAccessPoint(endPoint);
        service.getBinding().add(binding);

        institution4.getService().add(service);

        registryDao.createInstitution(institution4);

        // Test
        institution4 =
                registryDao.getInstitutionByName(institutionName4);
        Assert.isTrue(
            institution4 != null,
                "getInstitution returned 'Null'");
        storedService = institution4.getService().get(0);
        Assert.isTrue(
            storedService.getName().equals(serviceName),
                "Service name [" + storedService.getName() +
                 "] does not match the expected name [" + serviceName + "]");

        // Get the bindings
        storedService =
                registryDao.getBindingsForService(storedService.getKey());
        Assert.isTrue(
            institution4 != null,
                "getInstitution returned 'Null'");

        BindingEntity storedBinding = storedService.getBinding().get(0);
        Assert.isTrue(
            storedBinding != null,
                "getBinding returned 'Null'");
        Assert.isTrue(
            storedBinding.getAccessPoint().equals(endPoint),
                "EndPoint [" + storedBinding.getAccessPoint() +
                 "] does not match the expected endPoint [" + endPoint + "]");

        /* clean-up */
        registryDao.removeInstitution(institution4.getKey());
        registryDao.removeInstitution(institution3.getKey());
        registryDao.removeInstitution(institution2.getKey());
        registryDao.removeInstitution(institution1.getKey());
    }

    @Test
    public void updateInstitution() throws Exception {
        // Create an institution only
        String institutionName = getRandomValue("institutionName");
        String institutionDescription = getRandomValue("institutionDescription");

        InstitutionEntity institution = new InstitutionEntity();
        institution.setName(institutionName);
        institution.setDescription(institutionDescription);
        registryDao.createInstitution(institution);

        // Retrieve institution - verify change
        institution =
                registryDao.getInstitutionByName(institutionName);
        Assert.isTrue(
            institution != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution.getName().equals(institutionName),
                "Institution name [" + institution.getName() +
                 "] does not match the expected name [" + institutionName + "]");

        // Update the description
        String institutionUpdatedDescription = getRandomValue("UpdatedDescription");
        institution.setDescription(institutionUpdatedDescription);
        registryDao.updateInstitution(institution);

        // Retrieve institution - verify change
        institution =
                registryDao.getInstitutionByName(institutionName);
        Assert.isTrue(
            institution != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution.getDescription().equals(institutionUpdatedDescription),
                "Institution description [" + institution.getDescription() +
                 "] does not match the expected description [" +
                        institutionUpdatedDescription + "]");

        // Update the contact info
        ContactEntity contact = new ContactEntity();
        String personName = getRandomValue("PersonName");
        String phone = getRandomValue("Phone");
        String address = getRandomValue("Address");
        String email = getRandomValue("Email");
        String description = getRandomValue("ContactDescription");

        contact.setPersonName(personName);
        contact.setPhone(phone);
        contact.setAddress(address);
        contact.setEmail(email);
        contact.setDescription(description);
        // Update the data
        institution.setContactInfo(contact);
        registryDao.updateInstitution(institution);
        // Retrieve institution - verify change
        institution =
                registryDao.getInstitutionByName(institutionName);
        Assert.isTrue(
            institution != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution.getContactInfo() != null,
                "getContactInfo returned 'Null'");
        ContactEntity updatedContact = institution.getContactInfo();
        Assert.isTrue(
            updatedContact.getPersonName().equals(
                    personName),
                "ContactInfo personName [" + updatedContact.getPersonName() +
                 "] does not match the expected personName [" + personName + "]");
        Assert.isTrue(
            updatedContact.getAddress().equals(
                    address),
                "ContactInfo address [" + updatedContact.getAddress() +
                 "] does not match the expected address [" + address + "]");
        Assert.isTrue(
            updatedContact.getPhone().equals(
                    phone),
                "ContactInfo phone [" + updatedContact.getPhone() +
                 "] does not match the expected phone [" + phone + "]");
        Assert.isTrue(
            updatedContact.getEmail().equals(
                    email),
                "ContactInfo email [" + updatedContact.getEmail() +
                 "] does not match the expected email [" + email + "]");
        Assert.isTrue(
            updatedContact.getDescription().equals(
                    description),
                "ContactInfo description [" + updatedContact.getDescription() +
                 "] does not match the expected description [" + description + "]");

        // Update the name?
        String institutionUpdatedName = getRandomValue("institutionUpdatedName");
        institution.setName(institutionUpdatedName);
        registryDao.updateInstitution(institution);
        // Retrieve institution - verify change
        institution =
                registryDao.getInstitutionByName(institutionUpdatedName);
        Assert.isTrue(
            institution != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution.getName().equals(institutionUpdatedName),
                "Institution name [" + institution.getName() +
                 "] does not match the expected name [" + institutionUpdatedName + "]");

        // clean-up
        registryDao.removeInstitution(institution.getKey());
    }

    @Test
    public void removeInstitution() throws Exception {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        InstitutionEntity institution = new InstitutionEntity();
        institution.setName(institutionName);
        institution.setDescription(getRandomValue("Description"));
        registryDao.createInstitution(institution);

        // Test for existence
        institution =
                registryDao.getInstitutionByName(institutionName);
        Assert.isTrue(
            institution != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution.getName().equals(institutionName),
                "Institution name [" + institution.getName() +
                 "] does not match the expected name [" + institutionName + "]");

        // Remove the institution
        registryDao.removeInstitution(institution.getKey());

        // Test
        institution =
                registryDao.getInstitutionByName(institutionName);
        Assert.isTrue(
            institution == null,
                "getInstitution did not return 'Null'");
    }

    @Test
    public void getInstitutionByKey() throws Exception {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        InstitutionEntity institution = new InstitutionEntity();
        institution.setName(institutionName);
        institution.setDescription(getRandomValue("Description"));
        registryDao.createInstitution(institution);

        // Test for existence
        institution =
                registryDao.getInstitutionByName(institutionName);
        Assert.isTrue(
            institution != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution.getName().equals(institutionName),
                "Institution name [" + institution.getName() +
                 "] does not match the expected name [" + institutionName + "]");

        // Test
        institution =
                registryDao.getInstitutionByKey(institution.getKey());
        Assert.isTrue(
            institution != null,
                "getInstitutionByKey returned 'Null'");
        Assert.isTrue(
            institution.getName().equals(institutionName),
                "Institution name [" + institution.getName() +
                 "] does not match the expected name [" + institutionName + "]");

        // clean-up
        registryDao.removeInstitution(institution.getKey());
    }

    @Test
    public void getInstitutions() throws Exception {
        // Create an institution
        String institution1Name = getRandomValue("Institution");
        InstitutionEntity institution1 = new InstitutionEntity();
        institution1.setName(institution1Name);
        institution1.setDescription(getRandomValue("Description"));

        // Add a service to the institution
        ServiceEntity service = new ServiceEntity();
        String serviceName = getRandomValue("Service");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));
        ServiceTypeEntity serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());
        service.setServiceType(serviceType);
        institution1.getService().add(service);

        registryDao.createInstitution(institution1);

        // Test for existence
        institution1 =
                registryDao.getInstitutionByName(institution1Name);
        Assert.isTrue(
            institution1 != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution1.getName().equals(institution1Name),
                "Institution name [" + institution1.getName() +
                 "] does not match the expected name [" + institution1Name + "]");

        // Create another institution
        String institution2Name = getRandomValue("Institution");
        InstitutionEntity institution2 = new InstitutionEntity();
        institution2.setName(institution2Name);
        institution2.setDescription(getRandomValue("Description"));

        // Add a service to the institution
        ServiceEntity service2 = new ServiceEntity();
        String serviceName2 = getRandomValue("Service2");
        service2.setName(serviceName2);
        service2.setDescription(getRandomValue("Description"));
        serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());
        service2.setServiceType(serviceType);

        // Add a binding to the service
        BindingEntity binding = new BindingEntity();
        binding.setDescription(getRandomValue("Description"));
        BindingTypeEntity bindingType_activityStream =
            registryDao.getBindingTypeByName(
                BindingTypeEnum.ACTIVITY_STREAM_ATOM.value());
        binding.setBindingType(bindingType_activityStream);
        String endPoint = getRandomValue("http://endpoint");
        binding.setAccessPoint(endPoint);
        service2.getBinding().add(binding);

        institution2.getService().add(service2);

        registryDao.createInstitution(institution2);

        // Test for existence
        institution2 =
                registryDao.getInstitutionByName(institution2Name);
        Assert.isTrue(
            institution2 != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution2.getName().equals(institution2Name),
                "Institution name [" + institution2.getName() +
                 "] does not match the expected name [" + institution2Name + "]");

        // Test
        int count  = 0;
        for (InstitutionEntity institution : registryDao.getInstitutions()) {
            if (institution.getName().equals(institution1Name) ||
                    institution.getName().equals(institution2Name)) {
                count++;
            }
        }
        Assert.isTrue(count == 2, "Expected institution count [2] does not match result [" +
            count + "]");

        // clean-up
        registryDao.removeInstitution(institution2.getKey());
        registryDao.removeInstitution(institution1.getKey());
    }

    @Test
    public void addService() throws Exception {
        // Create an institution
        String institution1Name = getRandomValue("Institution");
        InstitutionEntity institution1 = new InstitutionEntity();
        institution1.setName(institution1Name);
        institution1.setDescription(getRandomValue("Description"));
        registryDao.createInstitution(institution1);

        // Test for existence
        institution1 =
                registryDao.getInstitutionByName(institution1Name);
        Assert.isTrue(
            institution1 != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution1.getName().equals(institution1Name),
                "Institution name [" + institution1.getName() +
                 "] does not match the expected name [" + institution1Name + "]");

        // Add a service
        ServiceEntity service = new ServiceEntity();
        String serviceName = getRandomValue("Service");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));

        ServiceTypeEntity serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());

        service.setServiceType(serviceType);
        registryDao.addService(institution1.getKey(), service);

        // test for existence of the service
        ServiceEntity storedService =
                registryDao.getServiceByName(institution1.getName(), serviceName);
        Assert.isTrue(
            storedService != null,
                "getServiceByName returned 'Null'");
        Assert.isTrue(
            storedService.getName().equals(serviceName),
                "Service name [" + storedService.getName() +
                 "] does not match the expected name [" + serviceName + "]");

        // clean-up
        registryDao.removeInstitution(institution1.getKey());
    }

    @Test
    public void updateService() throws Exception {
        // Create an institution
        String institution1Name = getRandomValue("Institution");
        InstitutionEntity institution1 = new InstitutionEntity();
        institution1.setName(institution1Name);
        institution1.setDescription(getRandomValue("Description"));
        registryDao.createInstitution(institution1);

        // Test for existence
        institution1 =
                registryDao.getInstitutionByName(institution1Name);
        Assert.isTrue(
            institution1 != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution1.getName().equals(institution1Name),
                "Institution name [" + institution1.getName() +
                 "] does not match the expected name [" + institution1Name + "]");

        // Add a service
        ServiceEntity service = new ServiceEntity();
        String serviceName = getRandomValue("Service");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));
        ServiceTypeEntity serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());
        service.setServiceType(serviceType);
        registryDao.addService(institution1.getKey(), service);

        // test for existence of the service
        ServiceEntity storedService =
                registryDao.getServiceByName(institution1.getName(), serviceName);
        Assert.isTrue(
            storedService != null,
                "getServiceByName returned 'Null'");
        Assert.isTrue(
            storedService.getName().equals(serviceName),
                "Service name [" + storedService.getName() +
                 "] does not match the expected name [" + serviceName + "]");

        // Update the Service
        String serviceDescriptionUpdated =
                getRandomValue("serviceDescriptionUpdated");
        serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());
        storedService.setServiceType(serviceType);
        storedService.setDescription(serviceDescriptionUpdated);
        storedService.setName(serviceName);
        registryDao.updateService(storedService);

        // test the updates
        storedService =
                registryDao.getServiceByName(institution1.getName(), serviceName);
        Assert.isTrue(
            storedService != null,
                "getServiceByName returned 'Null'");
        Assert.isTrue(
            storedService.getServiceType() != null,
                "getServiceType() returned 'Null'");
        Assert.isTrue(
            storedService.getServiceType().getValue().equals(
                    ServiceTypeEnum.COURSE_REVIEWS.value()),
                "Service type [" + storedService.getServiceType().getValue() +
                 "] does not match the expected value [" +
                        ServiceTypeEnum.COURSE_REVIEWS.value() + "]");
        Assert.isTrue(
            storedService.getDescription().equals(serviceDescriptionUpdated),
                "Service description [" + storedService.getDescription() +
                 "] does not match the expected description [" +
                        serviceDescriptionUpdated + "]");

        // Update the service name
        String serviceNameUpdated = getRandomValue("serviceNameUpdated");
        storedService.setName(serviceNameUpdated);
        registryDao.updateService(storedService);

        // Test update
        storedService =
                registryDao.getServiceByKey(storedService.getKey());
        Assert.isTrue(
            storedService != null,
                "getServiceByKey returned 'Null'");
        Assert.isTrue(
            storedService.getName().equals(serviceNameUpdated),
                "Service name [" + storedService.getName() +
                 "] does not match the expected name [" + serviceNameUpdated + "]");

        // clean-up
        registryDao.removeInstitution(institution1.getKey());
    }

    @Test
    public void removeService() throws Exception {
//        Assert.isTrue(registryDao.getServiceTypes().size() == 5,
//                "Expected [5] serviceTypes but received [" +
//                        registryDao.getServiceTypes().size() + "]");

        // Create an institution
        String institution1Name = getRandomValue("Institution");
        InstitutionEntity institution1 = new InstitutionEntity();
        institution1.setName(institution1Name);
        institution1.setDescription(getRandomValue("Description"));
        registryDao.createInstitution(institution1);

        // Test for existence
        institution1 =
                registryDao.getInstitutionByName(institution1Name);
        Assert.isTrue(
            institution1 != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution1.getName().equals(institution1Name),
                "Institution name [" + institution1.getName() +
                 "] does not match the expected name [" + institution1Name + "]");

        // Add a service
        ServiceEntity service = new ServiceEntity();
        String serviceName = getRandomValue("Service");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));
        ServiceTypeEntity serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());
        service.setServiceType(serviceType);
        registryDao.addService(institution1.getKey(), service);

        // test for existence of the service
        ServiceEntity storedService =
                registryDao.getServiceByName(institution1.getName(), serviceName);
        Assert.isTrue(
            storedService != null,
                "getServiceByName returned 'Null'");
        Assert.isTrue(
            storedService.getName().equals(serviceName),
                "Service name [" + storedService.getName() +
                 "] does not match the expected name [" + serviceName + "]");

        // Remove the service
        registryDao.removeService(storedService.getKey());
        // test for existence of the service
        ServiceEntity storedService1 =
                registryDao.getServiceByName(institution1.getName(), serviceName);
        Assert.isTrue(
            storedService1 == null,
                "getServiceByName was expected to return 'Null'");
        storedService1 =
                registryDao.getServiceByKey(storedService.getKey());
        Assert.isTrue(
            storedService1 == null,
                "getServiceByKey was expected to return 'Null'");

        Assert.isTrue(registryDao.getServiceTypes().size() == ServiceTypeEnum.values().length,
                "Expected [" + ServiceTypeEnum.values().length + "] serviceTypes but received [" +
                        registryDao.getServiceTypes().size() + "]");

        // Ensure that serviceTypes were not removed
        List<ServiceTypeEntity> serviceTypes = registryDao.getServiceTypes();
        ServiceTypeEnum[] enums = ServiceTypeEnum.values();
        // Test the number of types in the database match the number of types defined in the xsd
        Assert.isTrue(serviceTypes.size() == enums.length,
            "The number of ServiceTypes [" + serviceTypes.size() +
                    "] does not match the number of ServiceType enums [" +
                    enums.length + "] from domain-objects.xsd");

        // clean-up
        registryDao.removeInstitution(institution1.getKey());

    }

    @Test
    public void getServices() {

        // Create an institution
        String institution1Name = getRandomValue("Institution");
        InstitutionEntity institution1 = new InstitutionEntity();
        institution1.setName(institution1Name);
        institution1.setDescription(getRandomValue("Description"));
        registryDao.createInstitution(institution1);

        // Test for existence
        institution1 =
                registryDao.getInstitutionByName(institution1Name);
        Assert.isTrue(
            institution1 != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution1.getName().equals(institution1Name),
                "Institution name [" + institution1.getName() +
                 "] does not match the expected name [" + institution1Name + "]");

        // Add first service
        ServiceTypeEntity serviceType = new ServiceTypeEntity();
        serviceType.setValue(ServiceTypeEnum.CONTENT_REPOSITORY);

        String service1Name = getRandomValue("Service1");
        String service2Name = getRandomValue("Service2");

        ServiceEntity service1 = new ServiceEntity();
        service1.setName(service1Name);
        service1.setDescription(getRandomValue("Description"));
        service1.setServiceType(serviceType);
        registryDao.addService(institution1.getKey(), service1);

        // Confirm Service was added
        ServiceEntity storedService1 =
                registryDao.getServiceByName(institution1.getName(), service1Name);
        Assert.isTrue(storedService1 != null, "getServiceByName returned 'Null'");
        Assert.isTrue(storedService1.getName().equals(service1Name),
            "Service name returned from the database [" +
            storedService1.getName() + "] does not match the expected name [" +
            service1Name + "]");

        // Add another service
        ServiceEntity service2 = new ServiceEntity();
        service2.setName(service2Name);
        service2.setDescription(getRandomValue("Description"));
        service2.setServiceType(serviceType);

        // Add binding to service
        BindingEntity binding = new BindingEntity();
        binding.setDescription(getRandomValue("Description"));
        BindingTypeEntity bindingType_activityStream =
            registryDao.getBindingTypeByName(
                BindingTypeEnum.ACTIVITY_STREAM_ATOM.value());
        binding.setBindingType(bindingType_activityStream);
        String endPoint = getRandomValue("http://endpoint");
        binding.setAccessPoint(endPoint);
        service2.getBinding().add(binding);

        registryDao.addService(institution1.getKey(), service2);

        // Confirm Service was added
        ServiceEntity storedService2 =
                registryDao.getServiceByName(institution1.getName(), service2Name);
        Assert.isTrue(storedService2 != null, "getServiceByName returned 'Null'");
        Assert.isTrue(storedService2.getName().equals(service2Name),
            "Service name returned from the database [" +
            storedService1.getName() + "] does not match the expected name [" +
            service1Name + "]");

        // Retrieve by services
        int count = 0;
        List<ServiceEntity> services = registryDao.getServices();
        for (ServiceEntity bService : services) {
            if (bService.getName().equals(service1Name) ||
                bService.getName().equals(service2Name)) {
                count++;
            }
        }
        Assert.isTrue(count == 2, "Received [" +
                count + "] services but expected [2]");

        // Test again with a different service type
        count = 0;
        services =
                registryDao.getServicesByServiceType(ServiceTypeEnum.PUB_SUB);
        for (ServiceEntity bService : services) {
            if (bService.getName().equals(service1Name) ||
                bService.getName().equals(service2Name)) {
                count++;
            }
        }
        Assert.isTrue(count == 0, "Received [" +
                count + "] services but expected [0]");

        // clean-up
        registryDao.removeInstitution(institution1.getKey());


    }

    @Test
    public void getServicesByServiceType() {
        // Create an institution
        String institution1Name = getRandomValue("Institution");
        InstitutionEntity institution1 = new InstitutionEntity();
        institution1.setName(institution1Name);
        institution1.setDescription(getRandomValue("Description"));
        registryDao.createInstitution(institution1);

        // Test for existence
        institution1 =
                registryDao.getInstitutionByName(institution1Name);
        Assert.isTrue(
            institution1 != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution1.getName().equals(institution1Name),
                "Institution name [" + institution1.getName() +
                 "] does not match the expected name [" + institution1Name + "]");

        // Add two services
        ServiceTypeEntity serviceType = new ServiceTypeEntity();
        serviceType.setValue(ServiceTypeEnum.CONTENT_REPOSITORY);

        String service1Name = getRandomValue("Service1");
        String service2Name = getRandomValue("Service2");

        ServiceEntity service1 = new ServiceEntity();
        service1.setName(service1Name);
        service1.setDescription(getRandomValue("Description"));
        service1.setServiceType(serviceType);
        registryDao.addService(institution1.getKey(), service1);

        // Confirm Service was added
        ServiceEntity storedService1 =
                registryDao.getServiceByName(institution1.getName(), service1Name);
        Assert.isTrue(storedService1 != null, "getServiceByName returned 'Null'");
        Assert.isTrue(storedService1.getName().equals(service1Name),
            "Service name returned from the database [" +
            storedService1.getName() + "] does not match the expected name [" +
            service1Name + "]");

        // Add another service
        ServiceEntity service2 = new ServiceEntity();
        service2.setName(service2Name);
        service2.setDescription(getRandomValue("Description"));
        service2.setServiceType(serviceType);
        registryDao.addService(institution1.getKey(), service2);

        // Confirm Service was added
        ServiceEntity storedService2 =
                registryDao.getServiceByName(institution1.getName(), service2Name);
        Assert.isTrue(storedService2 != null, "getServiceByName returned 'Null'");
        Assert.isTrue(storedService2.getName().equals(service2Name),
            "Service name returned from the database [" +
            storedService1.getName() + "] does not match the expected name [" +
            service1Name + "]");

        // Retrieve by service type
        int count = 0;
        List<ServiceEntity> services =
                registryDao.getServicesByServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);
        for (ServiceEntity bService : services) {
            if (bService.getName().equals(service1Name) ||
                bService.getName().equals(service2Name)) {
                count++;
            }
        }
        Assert.isTrue(count == 2, "Received [" +
                count + "] services but expected [2]");

        // Test again with a different service type
        count = 0;
        services =
                registryDao.getServicesByServiceType(ServiceTypeEnum.PUB_SUB);
        for (ServiceEntity bService : services) {
            if (bService.getName().equals(service1Name) ||
                bService.getName().equals(service2Name)) {
                count++;
            }
        }
        Assert.isTrue(count == 0, "Received [" +
                count + "] services but expected [0]");

        // clean-up
        registryDao.removeInstitution(institution1.getKey());

    }

    @Test
    public void getServicesByServiceTypeAndBindingType() throws Exception {
        // Create an institution
        String institution1Name = getRandomValue("Institution");
        InstitutionEntity institution1 = new InstitutionEntity();
        institution1.setName(institution1Name);
        institution1.setDescription(getRandomValue("Description"));
        registryDao.createInstitution(institution1);

        // Test for existence
        institution1 =
                registryDao.getInstitutionByName(institution1Name);
        Assert.isTrue(
            institution1 != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution1.getName().equals(institution1Name),
                "Institution name [" + institution1.getName() +
                 "] does not match the expected name [" + institution1Name + "]");

        // Add two services
        ServiceTypeEntity serviceType =
            registryDao.getServiceTypeByName(
                ServiceTypeEnum.COURSE_REVIEWS.value());
        BindingTypeEntity bindingType_activityStream =
            registryDao.getBindingTypeByName(
                BindingTypeEnum.ACTIVITY_STREAM_ATOM.value());

        String service1Name = getRandomValue("Service1");
        String service2Name = getRandomValue("Service2");

        ServiceEntity service = new ServiceEntity();
        service.setName(service1Name);
        service.setDescription(getRandomValue("Description"));

        service.setServiceType(serviceType);

        // Add binding to service
        BindingEntity binding = new BindingEntity();
        binding.setDescription(getRandomValue("Description"));
        binding.setBindingType(bindingType_activityStream);
        String endPoint = getRandomValue("http://endpoint");
        binding.setAccessPoint(endPoint);
        service.getBinding().add(binding);

        registryDao.addService(institution1.getKey(), service);

        // Add another service
        service = new ServiceEntity();
        service.setName(service2Name);
        service.setDescription(getRandomValue("Description"));
        service.setServiceType(serviceType);
        // Add binding to service
        binding = new BindingEntity();
        binding.setDescription(getRandomValue("Description"));
        binding.setBindingType(bindingType_activityStream);
        endPoint = getRandomValue("http://endpoint");
        binding.setAccessPoint(endPoint);
        service.getBinding().add(binding);

        registryDao.addService(institution1.getKey(), service);

        // Retrieve by service and binding type
        int count = 0;
        List<ServiceEntity> services =
                registryDao.getServicesByServiceTypeAndBindingType(
                        ServiceTypeEnum.COURSE_REVIEWS, BindingTypeEnum.ACTIVITY_STREAM_ATOM);
        for (ServiceEntity bService : services) {
            if (bService.getName().equals(service1Name) ||
                bService.getName().equals(service2Name)) {
                count++;
            }
        }
        Assert.isTrue(count == 2, "Received [" +
                count + "] services but expected [2]");

        // Test again with a different service type
        ServiceTypeEntity serviceType2 =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.PUB_SUB.value());
        count = 0;
        services =
                registryDao.getServicesByServiceTypeAndBindingType(
                        ServiceTypeEnum.PUB_SUB, BindingTypeEnum.ACTIVITY_STREAM_ATOM);
        for (ServiceEntity bService : services) {
            if (bService.getName().equals(service1Name) ||
                bService.getName().equals(service2Name)) {
                count++;
            }
        }
        Assert.isTrue(count == 0, "Received [" +
                count + "] services but expected [0]");

        // Test again with a different binding type
        BindingTypeEntity bindingType_courseCatalog =
            registryDao.getBindingTypeByName(
                BindingTypeEnum.COURSE_CATALOG_ATOM.value());
        count = 0;
        services =
                registryDao.getServicesByServiceTypeAndBindingType(
                        ServiceTypeEnum.PUB_SUB, BindingTypeEnum.COURSE_CATALOG_ATOM);
        for (ServiceEntity bService : services) {
            if (bService.getName().equals(service1Name) ||
                bService.getName().equals(service2Name)) {
                count++;
            }
        }
        Assert.isTrue(count == 0, "Received [" +
                count + "] services but expected [0]");

        // clean-up
        registryDao.removeInstitution(institution1.getKey());
    }

    @Test
    public void addBinding() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        InstitutionEntity institution = new InstitutionEntity();
        institution.setName(institutionName);
        institution.setDescription(getRandomValue("Description"));

        // Add a Service
        ServiceEntity service = new ServiceEntity();
        String serviceName = getRandomValue("Service");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));
        ServiceTypeEntity serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());
        service.setServiceType(serviceType);
        institution.getService().add(service);

        registryDao.createInstitution(institution);

        // Test
        institution =
                registryDao.getInstitutionByName(institutionName);
        Assert.isTrue(
            institution != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution.getName().equals(institutionName),
                "Institution name [" + institution.getName() +
                 "] does not match the expected name [" + institutionName + "]");
        Assert.isTrue(
            institution.getService() != null &&
                    !institution.getService().isEmpty(),
                "getService contained a null or empty collection");

        ServiceEntity storedService = institution.getService().get(0);

        // Add a binding to the service
        String bindingDescription = getRandomValue("Description");
        String endPoint = getRandomValue("http://endpoint");
        BindingTypeEntity bindingType_activityStream =
            registryDao.getBindingTypeByName(
                BindingTypeEnum.ACTIVITY_STREAM_ATOM.value());

        BindingEntity binding = new BindingEntity();
        binding.setDescription(bindingDescription);
        binding.setBindingType(bindingType_activityStream);
        binding.setAccessPoint(endPoint);
        registryDao.addBinding(storedService.getKey(), binding);

        List<BindingEntity> storedBindings =
                registryDao.getBindingsByAccessPoint(endPoint);
        Assert.isTrue(
            storedBindings != null && !storedBindings.isEmpty(),
                "getBindingsByAccessPoint returned 'Null'");

        BindingEntity storedBinding = storedBindings.get(0);
        Assert.isTrue(
            storedBinding.getDescription().equals(bindingDescription),
                "Binding description [" + storedBinding.getDescription() +
                 "] does not match the expected description [" +
                        bindingDescription + "]");
        Assert.isTrue(
            storedBinding.getBindingType().getValue().equals(
                    bindingType_activityStream.getValue()),
                "Binding type [" + storedBinding.getBindingType().getValue() +
                 "] does not match the expected binding type [" +
                        bindingType_activityStream.getValue() + "]");

        storedBinding = registryDao.getBindingByKey(storedBinding.getKey());
        Assert.isTrue(
            storedBinding != null,
                "getBindingByKey returned 'Null'");
        Assert.isTrue(
            storedBinding.getDescription().equals(bindingDescription),
                "Binding description [" + storedBinding.getDescription() +
                 "] does not match the expected description [" +
                        bindingDescription + "]");

        // clean-up
        registryDao.removeInstitution(institution.getKey());
    }

    @Test
    public void removeBinding() throws Exception {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        InstitutionEntity institution = new InstitutionEntity();
        institution.setName(institutionName);
        institution.setDescription(getRandomValue("Description"));

        // Add a Service
        ServiceEntity service = new ServiceEntity();
        String serviceName = getRandomValue("Service");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));
        ServiceTypeEntity serviceType =
                registryDao.getServiceTypeByName(
                        ServiceTypeEnum.COURSE_REVIEWS.value());
        service.setServiceType(serviceType);
        institution.getService().add(service);

        registryDao.createInstitution(institution);

        // Test
        institution =
                registryDao.getInstitutionByName(institutionName);
        Assert.isTrue(
            institution != null,
                "getInstitution returned 'Null'");
        Assert.isTrue(
            institution.getName().equals(institutionName),
                "Institution name [" + institution.getName() +
                 "] does not match the expected name [" + institutionName + "]");
        Assert.isTrue(
            institution.getService() != null &&
                    !institution.getService().isEmpty(),
                "getService contained a null or empty collection");

        ServiceEntity storedService = institution.getService().get(0);

        // Add a binding to the service
        String bindingDescription = getRandomValue("Description");
        String endPoint = getRandomValue("http://endpoint");
        BindingTypeEntity bindingType_activityStream =
            registryDao.getBindingTypeByName(
                BindingTypeEnum.ACTIVITY_STREAM_ATOM.value());

        BindingEntity binding = new BindingEntity();
        binding.setDescription(bindingDescription);
        binding.setBindingType(bindingType_activityStream);
        binding.setAccessPoint(endPoint);
        registryDao.addBinding(storedService.getKey(), binding);

        // Ensure that the binding was added
        List<BindingEntity> storedBindings =
                registryDao.getBindingsByAccessPoint(endPoint);
        Assert.isTrue(
            storedBindings != null && !storedBindings.isEmpty(),
                "getBindingsByAccessPoint returned 'Null'");

        BindingEntity storedBinding = storedBindings.get(0);
        Assert.isTrue(
            storedBinding.getDescription().equals(bindingDescription),
                "Binding description [" + storedBinding.getDescription() +
                 "] does not match the expected description [" +
                        bindingDescription + "]");
        Assert.isTrue(
            storedBinding.getBindingType().getValue().equals(
                    bindingType_activityStream.getValue()),
                "Binding type [" + storedBinding.getBindingType().getValue() +
                 "] does not match the expected binding type [" +
                        bindingType_activityStream.getValue() + "]");

        storedBinding = registryDao.getBindingByKey(storedBinding.getKey());
        Assert.isTrue(
            storedBinding != null,
                "getBindingByKey returned 'Null'");
        Assert.isTrue(
            storedBinding.getDescription().equals(bindingDescription),
                "Binding description [" + storedBinding.getDescription() +
                 "] does not match the expected description [" +
                        bindingDescription + "]");

        // Remove the binding
        registryDao.removeBinding(storedBinding.getKey());
        // Test the removal
        storedBinding = registryDao.getBindingByKey(storedBinding.getKey());
        Assert.isTrue(
            storedBinding == null,
                "getBindingByAccessPoint was expected to return 'Null'");

        // Ensure that bindingTypes have not been deleted
        List<BindingTypeEntity> bindingTypes = registryDao.getBindingTypes();
        BindingTypeEnum[] enums = BindingTypeEnum.values();
        // Test the number of binding types in the database match the number of types defined in the xsd
        Assert.isTrue(bindingTypes.size() == enums.length,
            "The number of BindingTypes [" + bindingTypes.size() +
                    "] does not match the number of BindingType enums [" +
                    enums.length + "] from domain-objects.xsd");

        // clean-up
        registryDao.removeInstitution(institution.getKey());
    }

    @Test
    public void getBindingTypes() throws Exception {
        List<BindingTypeEntity> bindingTypes = registryDao.getBindingTypes();
        BindingTypeEnum[] enums = BindingTypeEnum.values();

        // Test the number of types in the database match the number of types defined in the xsd
        Assert.isTrue(bindingTypes.size() == enums.length,
            "The number of BindingTypes [" + bindingTypes.size() +
                    "] does not match the number of BindingType enums [" +
                    enums.length + "] from domain-objects.xsd");

        // Compare BindingTypes, retrieved from the database, to BindingType enums in the xsd
        for (BindingTypeEntity bindingType : bindingTypes) {
            BindingTypeEnum bindingTypeEnum = null;
            try {
                bindingTypeEnum =
                    BindingTypeEnum.fromValue(bindingType.getValue());
            } catch (Exception e) {
                //e.printStackTrace();
            }
            Assert.isTrue(bindingTypeEnum != null, "Unable to find [" +
                    bindingType.getValue() + "] in domain-objects.xsd");
        }

        // Compare BindingType enums, from the xsd, to the database bindingTypes
        for (BindingTypeEnum enumType : BindingTypeEnum.values()) {
            BindingTypeEntity bindingtype = null;
            try {
                bindingtype =
                        registryDao.getBindingTypeByName(enumType.value());
            } catch (Exception e) {}
            Assert.isTrue(bindingtype != null, "Unable to find [" +
                    enumType.value() + "] in the database");
        }
    }

    @Test
    public void getServiceTypes() throws Exception {
        List<ServiceTypeEntity> serviceTypes = registryDao.getServiceTypes();
        ServiceTypeEnum[] enums = ServiceTypeEnum.values();

        // Test the number of types in the database match the number of types defined in the xsd
        Assert.isTrue(serviceTypes.size() == enums.length,
            "The number of ServiceTypes [" + serviceTypes.size() +
                    "] does not match the number of ServiceType enums [" +
                    enums.length + "] from domain-objects.xsd");

        // Compare ServiceTypes, retrieved from the database, to ServiceType enums in the xsd
        for (ServiceTypeEntity serviceType : serviceTypes) {
            ServiceTypeEnum serviceTypeEnum = null;
            try {
                serviceTypeEnum =
                    ServiceTypeEnum.fromValue(serviceType.getValue());
            } catch (Exception e) {
            }
            Assert.isTrue(serviceTypeEnum != null, "Unable to find [" +
                    serviceType.getValue() + "] in domain-objects.xsd");
        }

        // Compare ServiceType enums, from the xsd, to the database serviceTypes
        for (ServiceTypeEnum enumType : ServiceTypeEnum.values()) {
            ServiceTypeEntity serviceType = null;
            try {
                serviceType =
                        registryDao.getServiceTypeByName(enumType.value());
            } catch (Exception e) {}
            Assert.isTrue(serviceType != null, "Unable to find [" +
                    enumType.value() + "] in the database");
        }
    }


    private String getRandomValue(String value) {
        Random r = new Random();
        return value + ":" + r.nextInt();
    }
}
