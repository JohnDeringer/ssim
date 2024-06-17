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

import org.junit.Test;
import org.junit.runner.RunWith;

import org.nterlearning.registry.client.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 11/4/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:registry-proxy-beans-test.xml")
public class ServiceRegistryProxyTest {

    @Autowired
    RegistryProxy registryProxy;

    @Autowired
    org.nterlearning.registry.client.Registry globalRegistry;

    private boolean cleanup = true;

    @Test
    public void getGlobalInstitutions() {

        List<InstitutionBean> institutionBeans =
            registryProxy.getInstitutions(
                ActiveStatusEnum.UNSPECIFIED,
                ActiveStatusEnum.ACTIVE,
                RegistryInstance.GLOBAL);

        for (InstitutionBean institution : institutionBeans) {
            System.out.println(
                    "GLOBAL INSTITUTION [" + institution.getName() + "] " +
                    "RegistryInstance [" + institution.getRegistryInstance() + "]");
        }

        Assert.notNull(institutionBeans, "institutionBeans is null");
        Assert.isTrue(institutionBeans.size() > 0, "institutionBeans is empty");
        System.out.println("Registry returned [" + institutionBeans.size() + "] institutions");
    }

    @Test
    public void getGlobalServices() {
        List<ServiceBean> serviceBeans =
            registryProxy.getServices(
                ActiveStatusEnum.UNSPECIFIED,
                ActiveStatusEnum.ACTIVE,
                RegistryInstance.GLOBAL);

        Assert.notNull(serviceBeans, "serviceBeans is null");
        Assert.isTrue(serviceBeans.size() > 0, "serviceBeans is empty");
        System.out.println("Registry returned [" + serviceBeans.size() + "] services");
    }

    @Test
    public void globalServiceStatusTest() {
        ActiveStatusEnum localStatus = ActiveStatusEnum.UNSPECIFIED;
        ActiveStatusEnum globalActiveStatus = ActiveStatusEnum.ACTIVE;
        ActiveStatusEnum globalInactiveStatus = ActiveStatusEnum.INACTIVE;
        ActiveStatusEnum globalBlacklistStatus = ActiveStatusEnum.BLACKLIST;

        // Initial global active count
        List<ServiceBean> services =
                registryProxy.getServices(localStatus, globalActiveStatus, RegistryInstance.GLOBAL);
        int initialActiveServicesCount = services.size();
        // Initial global inactive count
        services =
                registryProxy.getServices(localStatus, globalInactiveStatus, RegistryInstance.GLOBAL);
        int initialInactiveServicesCount = services.size();

        // Create a global institution
        String institutionName = getRandomValue("institutionName");
        ActiveStatusEnum institutionActiveStatus = ActiveStatusEnum.ACTIVE;
        Institution institution =
                createInstitution(institutionName, institutionActiveStatus);
        globalRegistry.createInstitution(institution);

        // Retrieve the institution
        institution = globalRegistry.getInstitutionByName(institutionName);
        Assert.isTrue(institution != null,
                "Null Institution [" + institution + "]");

        // Add service1
        String service1Name = getRandomValue("Name");
        ActiveStatusEnum service1ActiveStatus = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum service1ServiceType = ServiceTypeEnum.NTER_PORTAL;
        BusinessService service1 = createService(
            service1Name, service1ServiceType, service1ActiveStatus);
        globalRegistry.addService(institution.getKey(), service1);

        // Retrieve service
        service1 = globalRegistry.getServiceByName(institutionName, service1Name);
        Assert.isTrue(service1 != null,
                "service1 is Null [" + service1 + "]");

        // Retrieve services (after 1 service added)
        services =
                registryProxy.getServices(localStatus, globalActiveStatus, RegistryInstance.GLOBAL);
        int servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount + 1,
                "Expected [" + (initialActiveServicesCount + 1) +
                        "] but received [" + (servicesCount - initialActiveServicesCount) + "]");

        // Add service2
        String service2Name = getRandomValue("Name");
        ActiveStatusEnum service2ActiveStatus = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum service2ServiceType = ServiceTypeEnum.NTER_PORTAL;
        BusinessService service2 = createService(
            service2Name, service2ServiceType, service2ActiveStatus);
        globalRegistry.addService(institution.getKey(), service2);

        // Retrieve services (after 2 services added)
        services =
                registryProxy.getServices(localStatus, globalActiveStatus, RegistryInstance.GLOBAL);
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount + 2,
                "Expected [" + (initialActiveServicesCount + 2) +
                        "] but received [" + (servicesCount - initialActiveServicesCount) + "]");

        // Change service1 to Inactive
        service1.setActiveStatus(globalInactiveStatus);
        globalRegistry.updateService(service1);

        // Test Active service count Retrieve services (after 1 service set to Inactive)
        services =
                registryProxy.getServices(localStatus, globalActiveStatus, RegistryInstance.GLOBAL);
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount + 1,
                "Expected [" + (initialActiveServicesCount + 1) +
                        "] but received [" + (servicesCount - initialActiveServicesCount) + "]");

        // Test Inactive service count Retrieve services (after 1 service set to Inactive)
        services =
                registryProxy.getServices(localStatus, globalInactiveStatus, RegistryInstance.GLOBAL);
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialInactiveServicesCount + 1,
                "Expected [" + (initialInactiveServicesCount + 1) +
                        "] but received [" + (servicesCount - initialInactiveServicesCount) + "]");

        // Change service1 to Blacklist
        service1.setActiveStatus(globalBlacklistStatus);
        globalRegistry.updateService(service1);

        // Test Active service count Retrieve services (after 1 service set to Blacklist)
        services =
                registryProxy.getServices(localStatus, globalActiveStatus, RegistryInstance.GLOBAL);
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount + 1,
                "Expected [" + (initialActiveServicesCount + 1) +
                        "] but received [" + (servicesCount - initialActiveServicesCount) + "]");

        // Test Inactive service count Retrieve services (after 1 service set to Blacklist)
        services =
                registryProxy.getServices(localStatus, globalInactiveStatus, RegistryInstance.GLOBAL);
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialInactiveServicesCount,
                "Expected [" + (initialInactiveServicesCount) +
                        "] but received [" + (servicesCount - initialInactiveServicesCount) + "]");

        // Set institution to blacklist
        institution.setActiveStatus(globalBlacklistStatus);
        globalRegistry.updateInstitution(institution);

        // Test Active service count Retrieve services (after institution set to Blacklist)
        services =
                registryProxy.getServices(localStatus, globalActiveStatus, RegistryInstance.GLOBAL);
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount,
                "Expected [" + (initialActiveServicesCount) +
                        "] but received [" + (servicesCount - initialActiveServicesCount) + "]");

        // Test Inactive service count Retrieve services (after institution set to Blacklist)
        services =
                registryProxy.getServices(localStatus, globalInactiveStatus, RegistryInstance.GLOBAL);
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialInactiveServicesCount,
                "Expected [" + (initialInactiveServicesCount) +
                        "] but received [" + (servicesCount - initialInactiveServicesCount) + "]");


        // Clean-up
        globalRegistry.removeInstitution(institution.getKey());
    }

    @Test
    public void globalInstitutionStatusTest() {
        ActiveStatusEnum blacklistStatus = ActiveStatusEnum.UNSPECIFIED;
        ActiveStatusEnum globalActiveStatus = ActiveStatusEnum.ACTIVE;
        ActiveStatusEnum globalInactiveStatus = ActiveStatusEnum.INACTIVE;
        ActiveStatusEnum globalBlacklistStatus = ActiveStatusEnum.BLACKLIST;

        // Initial global active count
        List<InstitutionBean> institutions =
                registryProxy.getInstitutions(
                        blacklistStatus,
                        globalActiveStatus,
                        RegistryInstance.GLOBAL);
        List<ServiceBean> services = new ArrayList<ServiceBean>();
        for (InstitutionBean institutionBean : institutions) {
            services.addAll(institutionBean.getServiceBeans());
        }
        int initialActiveServicesCount = services.size();

        // Initial global inactive count
        institutions =
                registryProxy.getInstitutions(
                        blacklistStatus,
                        globalInactiveStatus,
                        RegistryInstance.GLOBAL);
        services = new ArrayList<ServiceBean>();
        for (InstitutionBean institutionBean : institutions) {
            services.addAll(institutionBean.getServiceBeans());
        }
        int initialInactiveServicesCount = services.size();

        // Create a global institution - Active
        String institutionName = getRandomValue("institutionName");
        ActiveStatusEnum institutionActiveStatus = ActiveStatusEnum.ACTIVE;
        Institution institution =
                createInstitution(institutionName, institutionActiveStatus);
        globalRegistry.createInstitution(institution);

        // Retrieve the institution
        institution = globalRegistry.getInstitutionByName(institutionName);
        Assert.notNull(institution);

        // Add service1
        String service1Name = getRandomValue("service1Name");
        ActiveStatusEnum service1ActiveStatus = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum service1ServiceType = ServiceTypeEnum.NTER_PORTAL;
        BusinessService service1 = createService(
            service1Name, service1ServiceType, service1ActiveStatus);
        globalRegistry.addService(institution.getKey(), service1);

        // Retrieve service
        service1 = globalRegistry.getServiceByName(institutionName, service1Name);
        Assert.notNull(service1,
                "service1 is Null [" + service1 + "]");

        // Retrieve services - active (after 1 service added)
        institutions =
                registryProxy.getInstitutions(
                        blacklistStatus,
                        globalActiveStatus,
                        RegistryInstance.GLOBAL);
        services = new ArrayList<ServiceBean>();
        for (InstitutionBean institutionBean : institutions) {
            services.addAll(institutionBean.getServiceBeans());
        }
        int servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount + 1,
                "Expected [" + (initialActiveServicesCount + 1) +
                        "] but received [" + servicesCount + "]");

        // Add service2 - Active
        String service2Name = getRandomValue("Name");
        ActiveStatusEnum service2ActiveStatus = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum service2ServiceType = ServiceTypeEnum.NTER_PORTAL;
        BusinessService service2 = createService(
            service2Name, service2ServiceType, service2ActiveStatus);
        globalRegistry.addService(institution.getKey(), service2);

        // Retrieve services Active (after 2 services added)
        institutions =
                registryProxy.getInstitutions(blacklistStatus, globalActiveStatus, RegistryInstance.GLOBAL);
        services = new ArrayList<ServiceBean>();
        for (InstitutionBean institutionBean : institutions) {
            services.addAll(institutionBean.getServiceBeans());
        }
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount + 2,
                "Expected [" + (initialActiveServicesCount + 2) +
                        "] but received [" + servicesCount + "]");

        // Change service1 to Inactive
        service1.setActiveStatus(globalInactiveStatus);
        globalRegistry.updateService(service1);

        // Test Active service count Retrieve services (after 1 service set to Inactive)
        institutions =
                registryProxy.getInstitutions(
                        blacklistStatus,
                        globalActiveStatus,
                        RegistryInstance.GLOBAL);
        services = new ArrayList<ServiceBean>();
        for (InstitutionBean institutionBean : institutions) {
            services.addAll(institutionBean.getServiceBeans());
        }
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount + 1,
                "Expected [" + (initialActiveServicesCount + 1) +
                        "] but received [" + servicesCount + "]");

        // Test Inactive service count Retrieve services (after 1 service set to Inactive)
//        institutions =
//                registryProxy.getInstitutions(blacklistStatus, globalInactiveStatus, RegistryInstance.GLOBAL);
//        services = new ArrayList<ServiceBean>();
//        for (InstitutionBean institutionBean : institutions) {
//            services.addAll(institutionBean.getServiceBeans());
//        }
//        servicesCount = services.size();
//        Assert.isTrue(servicesCount == initialInactiveServicesCount + 1,
//                "Expected [" + (initialInactiveServicesCount + 1) +
//                        "] but received [" + (servicesCount - initialInactiveServicesCount) + "]");

        // Change service1 to Blacklist
        service1.setActiveStatus(globalBlacklistStatus);
        globalRegistry.updateService(service1);

        // Test Active service count Retrieve services (after 1 service set to Blacklist)
        institutions =
                registryProxy.getInstitutions(blacklistStatus, globalActiveStatus, RegistryInstance.GLOBAL);
        services = new ArrayList<ServiceBean>();
        for (InstitutionBean institutionBean : institutions) {
            services.addAll(institutionBean.getServiceBeans());
        }
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount + 1,
                "Expected [" + (initialActiveServicesCount + 1) +
                        "] but received [" + (servicesCount - initialActiveServicesCount) + "]");

        // Test Inactive service count Retrieve services (after 1 service set to Blacklist)
        institutions =
                registryProxy.getInstitutions(blacklistStatus, globalInactiveStatus, RegistryInstance.GLOBAL);
        services = new ArrayList<ServiceBean>();
        for (InstitutionBean institutionBean : institutions) {
            services.addAll(institutionBean.getServiceBeans());
        }
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialInactiveServicesCount,
                "Expected [" + (initialInactiveServicesCount) +
                        "] but received [" + (servicesCount - initialInactiveServicesCount) + "]");

        // Set institution to blacklist
        institution.setActiveStatus(globalBlacklistStatus);
        globalRegistry.updateInstitution(institution);

        // Test Active service count Retrieve services (after institution set to Blacklist)
        institutions =
                registryProxy.getInstitutions(blacklistStatus, globalActiveStatus, RegistryInstance.GLOBAL);
        services = new ArrayList<ServiceBean>();
        for (InstitutionBean institutionBean : institutions) {
            services.addAll(institutionBean.getServiceBeans());
        }
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialActiveServicesCount,
                "Expected [" + (initialActiveServicesCount) +
                        "] but received [" + (servicesCount - initialActiveServicesCount) + "]");

        // Test Inactive service count Retrieve services (after institution set to Blacklist)
        institutions =
                registryProxy.getInstitutions(blacklistStatus, globalInactiveStatus, RegistryInstance.GLOBAL);
        services = new ArrayList<ServiceBean>();
        for (InstitutionBean institutionBean : institutions) {
            services.addAll(institutionBean.getServiceBeans());
        }
        servicesCount = services.size();
        Assert.isTrue(servicesCount == initialInactiveServicesCount,
                "Expected [" + (initialInactiveServicesCount) +
                        "] but received [" + (servicesCount - initialInactiveServicesCount) + "]");


        // Clean-up
        globalRegistry.removeInstitution(institution.getKey());

    }

    @Test
    public void createInstitutionTest() {
        InstitutionBean institution = new InstitutionBean();
        String name = getRandomValue("Name");
        institution.setName(name);

        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("ContactName"));
        contact.setAddress(getRandomValue("Address"));
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);

        registryProxy.createInstitution(institution);

        institution = registryProxy.getInstitutionByName(name);
        Assert.notNull(institution, "Institution is 'Null'");
        Assert.isTrue(institution.getKey() != null, "Institution key is 'Null'");

        // Clean-up
        if (cleanup) {
            registryProxy.removeInstitution(institution.getKey());
        }
    }

    @Test
    public void updateInstitutionTest() {
        // Create institution
        InstitutionBean institution = new InstitutionBean();
        String name = getRandomValue("Name");
        institution.setName(name);

        String contactName1 = getRandomValue("ContactName");
        Contact contact = new Contact();
        contact.setPersonName(contactName1);
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);
        institution.setActiveStatus(ActiveStatusEnum.ACTIVE);
        institution.setRegistryInstance(RegistryInstance.LOCAL);

        System.out.println("Creating institution with contact name [" + contactName1 + "]");
        registryProxy.createInstitution(institution);

        institution = registryProxy.getInstitutionByName(name);
        Assert.notNull(institution, "Institution is 'Null'");
        Assert.isTrue(institution.getKey() != null, "Institution key is 'Null'");
        Assert.isTrue(institution.getContactInfo().getPersonName().equals(contactName1),
            "Received contact name [" + institution.getContactInfo().getPersonName() +
                "], expected [" + contactName1 + "]");
        Assert.isTrue(institution.getActiveStatus() == ActiveStatusEnum.ACTIVE, "Institution key is 'Null'");

        // Update
        String contactName2 = getRandomValue("ContactName");
        contact = new Contact();
        contact.setPersonName(contactName2);
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);

        System.out.println("Updating institution with contact name [" + contactName2 + "]");
        registryProxy.updateInstitution(institution);

        // Retrieve institution by name
        institution = registryProxy.getInstitutionByName(name);
        Assert.notNull(institution, "Institution is 'Null'");
        Assert.isTrue(institution.getKey() != null, "Institution key is 'Null'");
        Assert.isTrue(institution.getContactInfo().getPersonName().equals(contactName2),
            "Received contact name [" + institution.getContactInfo().getPersonName() +
                "], expected [" + contactName2 + "]");

        // Clean-up
        if (cleanup) {
            registryProxy.removeInstitution(institution.getKey());
            institution = registryProxy.getInstitutionByName(name);
            Assert.isTrue(institution == null, "Institution is not 'Null'");
        }
    }

    @Test
    public void getInstitutionsTest() {
        // Create institution1
        InstitutionBean institution1 = new InstitutionBean();
        String institution1Name = getRandomValue("institution1Name");
        institution1.setName(institution1Name);

        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("ContactName"));
        contact.setAddress(getRandomValue("Address"));
        contact.setEmail(getRandomValue("Email"));
        institution1.setContactInfo(contact);

        registryProxy.createInstitution(institution1);

        institution1 = registryProxy.getInstitutionByName(institution1Name);
        Assert.notNull(institution1, "Institution is 'Null'");
        Assert.isTrue(institution1.getKey() != null, "Institution key is 'Null'");

        // Add institution to blacklist
        institution1.setActiveStatus(ActiveStatusEnum.ACTIVE);
        registryProxy.updateInstitution(institution1);

        // Create institution2
        InstitutionBean institution2 = new InstitutionBean();
        String institution2Name = getRandomValue("institution2Name");
        institution2.setName(institution2Name);

        contact = new Contact();
        contact.setPersonName(getRandomValue("ContactName"));
        contact.setAddress(getRandomValue("Address"));
        contact.setEmail(getRandomValue("Email"));
        institution2.setContactInfo(contact);

        registryProxy.createInstitution(institution2);

        institution2 = registryProxy.getInstitutionByName(institution2Name);
        Assert.notNull(institution2, "Institution is 'Null'");
        Assert.isTrue(institution2.getKey() != null, "Institution key is 'Null'");

        // Add institution to blacklist
        institution2.setActiveStatus(ActiveStatusEnum.ACTIVE);
        registryProxy.updateInstitution(institution2);

        // Retrieve the institutions
        List<InstitutionBean> institutions =
            registryProxy.getInstitutions(ActiveStatusEnum.ACTIVE, ActiveStatusEnum.ACTIVE, RegistryInstance.LOCAL);
        Assert.isTrue(institutions.size() >= 2,
                "Expected 2 institutions but received [" + institutions.size() + "]");

        int count = 0;
        StringBuilder rtnInstitutions = new StringBuilder();
        for (Institution institution : institutions) {
            rtnInstitutions.append(institution.getName());
            rtnInstitutions.append('\n');
            if (institution.getName().equals(institution1Name) ||
                institution.getName().equals(institution2Name)) {
                count++;
            }

        }
        Assert.isTrue(count == 2,
                "Expected [2] institutions but received [" + count +
                        "] received the following institutions [" +
            rtnInstitutions + ", but expected " + '\n' +
                        "[" + institution1Name + "]" + '\n' + "[" +
                        institution2Name + "]"
        );

        // Clean-up
        if (cleanup) {
            registryProxy.removeInstitution(institution1.getKey());
            registryProxy.removeInstitution(institution2.getKey());
        }
    }

    @Test
    public void createServiceTest() {
        // Create an institution
        InstitutionBean institution = new InstitutionBean();
        String institutionName = getRandomValue("institutionName");
        institution.setName(institutionName);

        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("ContactName"));
        contact.setAddress(getRandomValue("Address"));
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);

        registryProxy.createInstitution(institution);

        institution = registryProxy.getInstitutionByName(institutionName);
        Assert.notNull(institution, "Institution is 'Null'");
        Assert.isTrue(institution.getKey() != null, "Institution key is 'Null'");

        ServiceBean service = new ServiceBean();
        String serviceName = getRandomValue("serviceName");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));
        service.setInstitutionName(institutionName);
        service.setServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);

        registryProxy.createService(institution.getKey(), service);

        service = registryProxy.getServiceByName(institution.getName(), serviceName);
        Assert.notNull(service, "Service is 'Null'");
        Assert.isTrue(service.getKey() != null, "Service key is 'Null'");
        Assert.isTrue(service.getName().equals(serviceName),
            "Service name mismatch: Received name [" + service.getName() +
                "], expected [" + serviceName + "]");

        // Clean-up
        if (cleanup) {
            registryProxy.removeService(service.getKey());
            service = registryProxy.getServiceByName(institution.getName(), serviceName);
            Assert.isTrue(service == null, "Service is not 'Null'");

            registryProxy.removeInstitution(institution.getKey());
            institution = registryProxy.getInstitutionByName(institutionName);
            Assert.isTrue(institution == null, "Institution is not 'Null'");
        }
    }

    @Test
    public void updateServiceTest() {
        // Create an institution
        InstitutionBean institution = new InstitutionBean();
        String institutionName = getRandomValue("institutionName");
        institution.setName(institutionName);

        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("ContactName"));
        contact.setAddress(getRandomValue("Address"));
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);

        registryProxy.createInstitution(institution);

        institution = registryProxy.getInstitutionByName(institutionName);
        Assert.notNull(institution, "Institution is 'Null'");
        Assert.isTrue(institution.getKey() != null, "Institution key is 'Null'");

        // Create a service
        ServiceBean service = new ServiceBean();
        String serviceName = getRandomValue("serviceName");
        service.setName(serviceName);
        service.setDescription(getRandomValue("Description"));
        service.setInstitutionName(institutionName);
        service.setServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);

        registryProxy.createService(institution.getKey(), service);

        service = registryProxy.getServiceByName(institution.getName(), serviceName);
        Assert.notNull(service, "Service is 'Null'");
        Assert.isTrue(service.getKey() != null, "Service key is 'Null'");
        Assert.isTrue(service.getName().equals(serviceName),
            "Service name mismatch: Received name [" + service.getName() +
                "], expected [" + serviceName + "]");
        Assert.isTrue(service.getServiceType() == ServiceTypeEnum.CONTENT_REPOSITORY,
            "Incorrect ServiceType [" + service.getServiceType() +
                "], Expected type [" + ServiceTypeEnum.CONTENT_REPOSITORY + "]");

        // Update serviceType
        service.setServiceType(ServiceTypeEnum.COURSE_REVIEWS);

        // Add a binding
        Binding bindingDto = new Binding();
        String bindingDescription = getRandomValue("bindingDescription");
        bindingDto.setDescription(bindingDescription);
        bindingDto.setBindingType(BindingTypeEnum.ACTIVITY_STREAM_ATOM);
        String accessPoint = getRandomValue("http://someService:");
        bindingDto.setAccessPoint(accessPoint);
        service.getBinding().add(bindingDto);

        // Update
        registryProxy.updateService(service);

        // Retrieve service
        service = registryProxy.getServiceByName(institution.getName(), serviceName);
        Assert.notNull(service, "Service is 'Null'");
        Assert.isTrue(service.getServiceType() == ServiceTypeEnum.COURSE_REVIEWS,
            "Incorrect ServiceType [" + service.getServiceType() +
                "], Expected type [" + ServiceTypeEnum.COURSE_REVIEWS + "]");
        Assert.isTrue(service.getBinding() != null, "Bindings is 'Null'");
        Assert.isTrue(!service.getBinding().isEmpty(), "No Bindings returned");
        Assert.isTrue(service.getBinding().get(0).getBindingType() ==
                BindingTypeEnum.ACTIVITY_STREAM_ATOM,
            "Incorrect BindingType [" + service.getBinding().get(0).getBindingType() +
                "], Expected type [" + BindingTypeEnum.ACTIVITY_STREAM_ATOM + "]");
        Assert.isTrue(service.getBinding().get(0).getAccessPoint().equals(accessPoint),
            "Incorrect AccessPoint [" + service.getBinding().get(0).getAccessPoint() +
                "], Expected [" + accessPoint + "]");
        Assert.isTrue(service.getBinding().get(0).getDescription().equals(
                bindingDescription),
            "Incorrect Binding description [" +
                    service.getBinding().get(0).getDescription() +
                "], Expected [" + bindingDescription + "]");

        // Clean-up
        if (cleanup) {
            registryProxy.removeService(service.getKey());
            service = registryProxy.getServiceByName(institution.getName(), serviceName);
            Assert.isTrue(service == null, "Service is not 'Null'");

            registryProxy.removeInstitution(institution.getKey());
            institution = registryProxy.getInstitutionByName(institutionName);
            Assert.isTrue(institution == null, "Institution is not 'Null'");
        }
    }

    @Test
    public void getServicesTest() {
        // Create an institution
        InstitutionBean institution = new InstitutionBean();
        String institutionName = getRandomValue("institutionName");
        institution.setName(institutionName);

        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("ContactName"));
        contact.setAddress(getRandomValue("Address"));
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);

        registryProxy.createInstitution(institution);

        institution = registryProxy.getInstitutionByName(institutionName);
        Assert.isTrue(institution != null, "Institution is 'Null'");
        Assert.isTrue(institution.getKey() != null, "Institution key is 'Null'");

        // Add institution to blacklist
        institution.setActiveStatus(ActiveStatusEnum.ACTIVE);
        registryProxy.updateInstitution(institution);

        // Create a service1
        ServiceBean service1 = new ServiceBean();
        String serviceName1 = getRandomValue("serviceName");
        service1.setName(serviceName1);
        service1.setDescription(getRandomValue("Description"));
        service1.setInstitutionName(institutionName);
        service1.setServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);

        registryProxy.createService(institution.getKey(), service1);

        service1 = registryProxy.getServiceByName(institution.getName(), serviceName1);
        Assert.isTrue(service1 != null, "Service is 'Null'");
        Assert.isTrue(service1.getKey() != null, "Service key is 'Null'");
        Assert.isTrue(service1.getName().equals(serviceName1),
            "Service name mismatch: Received name [" + service1.getName() +
                "], expected [" + serviceName1 + "]");

        institution = registryProxy.getInstitutionByName(institutionName);
        Assert.isTrue(institution != null, "Institution is 'Null'");
        Assert.isTrue(institution.getKey() != null, "Institution key is 'Null'");

        // Set active status
        service1.setActiveStatus(ActiveStatusEnum.ACTIVE);
        registryProxy.updateService(service1);

        // Create a service2
        ServiceBean service2 = new ServiceBean();
        String serviceName2 = getRandomValue("serviceName");
        service2.setName(serviceName2);
        service2.setDescription(getRandomValue("Description"));
        service2.setInstitutionName(institutionName);
        service2.setServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);

        // Add binding
        Binding bindingDto = new Binding();
        bindingDto.setDescription(getRandomValue("bindingDescription"));
        bindingDto.setBindingType(BindingTypeEnum.ACTIVITY_STREAM_ATOM);
        bindingDto.setAccessPoint(getRandomValue("http://someService"));
        service2.getBinding().add(bindingDto);

        registryProxy.createService(institution.getKey(), service2);

        // Retrieve the service
        service2 =
                registryProxy.getServiceByName(institution.getName(), serviceName2);

        Assert.isTrue(service2 != null,
            "Service [" + serviceName2 + "] is 'Null'");
        Assert.isTrue(service2.getKey() != null, "Service key is 'Null'");
        Assert.isTrue(service2.getName().equals(serviceName2),
            "Service name mismatch: Received name [" + service2.getName() +
                "], expected [" + serviceName2 + "]");
        Assert.isTrue(service2.getBinding() != null, "Null Binding");
        Assert.isTrue(service2.getBinding().size() > 0, "Missing Binding");

        // Add service to blacklist
        service2.setActiveStatus(ActiveStatusEnum.ACTIVE);
        registryProxy.updateService(service2);

        // Retrieve local services
        List<ServiceBean> services =
            registryProxy.getServices(ActiveStatusEnum.ACTIVE, ActiveStatusEnum.ACTIVE, RegistryInstance.LOCAL);

        Assert.isTrue(services != null, "Services are Null");
        Assert.isTrue(!services.isEmpty(), "Services are Empty");
        int count = 0;
        for (ServiceBean service : services) {
            System.out.println("Found service [" + service.getName() +
                            "] for institution [" + service.getInstitutionName() + "]");
            if (service.getInstitutionName().equals(institutionName)) {
                if (service.getName().equals(serviceName1) ||
                    service.getName().equals(serviceName2)) {
                    System.out.println("Found created service [" + service.getName() +
                            "] for institution [" + service.getInstitutionName() + "]");
                    count++;
                }
            }
        }
        Assert.isTrue(count == 2,
            "Unexpected number of services [" + count + "], expected '2'");

        // Clean-up
        if (cleanup) {
            registryProxy.removeService(service2.getKey());
            service2 = registryProxy.getServiceByName(institution.getName(), serviceName2);
            Assert.isTrue(service2 == null, "Service is not 'Null'");

            registryProxy.removeService(service1.getKey());
            service1 = registryProxy.getServiceByName(institution.getName(), serviceName1);
            Assert.isTrue(service1 == null, "Service is not 'Null'");

            registryProxy.removeInstitution(institution.getKey());
            institution = registryProxy.getInstitutionByName(institutionName);
            Assert.isTrue(institution == null, "Institution is not 'Null'");
        }
    }

    @Test
    public void getServicesByServiceTypeTest() {
        // Create an institution
        InstitutionBean institution = new InstitutionBean();
        String institutionName = getRandomValue("institutionName");
        institution.setName(institutionName);

        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("ContactName"));
        contact.setAddress(getRandomValue("Address"));
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);

        registryProxy.createInstitution(institution);

        institution = registryProxy.getInstitutionByName(institutionName);
        Assert.notNull(institution, "Institution is 'Null'");
        Assert.isTrue(institution.getKey() != null, "Institution key is 'Null'");

        // Add institution to blacklist
        institution.setActiveStatus(ActiveStatusEnum.ACTIVE);
        registryProxy.updateInstitution(institution);

        // Create a service1
        ServiceBean service1 = new ServiceBean();
        String serviceName1 = getRandomValue("serviceName");
        service1.setName(serviceName1);
        service1.setDescription(getRandomValue("Description"));
        service1.setInstitutionName(institutionName);
        service1.setServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);

        registryProxy.createService(institution.getKey(), service1);

        service1 = registryProxy.getServiceByName(institution.getName(), serviceName1);
        Assert.notNull(service1, "Service is 'Null'");
        Assert.isTrue(service1.getKey() != null, "Service key is 'Null'");
        Assert.isTrue(service1.getName().equals(serviceName1),
            "Service name mismatch: Received name [" + service1.getName() +
                "], expected [" + serviceName1 + "]");

        // Add service to blacklist
        service1.setActiveStatus(ActiveStatusEnum.ACTIVE);
        registryProxy.updateService(service1);

        // Create a service2
        ServiceBean service2 = new ServiceBean();
        String serviceName2 = getRandomValue("serviceName");
        service2.setName(serviceName2);
        service2.setDescription(getRandomValue("Description"));
        service2.setInstitutionName(institutionName);
        service2.setServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);

        // Add binding
        Binding bindingDto = new Binding();
        bindingDto.setDescription(getRandomValue("bindingDescription"));
        bindingDto.setBindingType(BindingTypeEnum.ACTIVITY_STREAM_ATOM);
        bindingDto.setAccessPoint(getRandomValue("http://someService"));
        service2.getBinding().add(bindingDto);

        registryProxy.createService(institution.getKey(), service2);

        // Retrieve the service
        service2 = registryProxy.getServiceByName(institution.getName(), serviceName2);

        Assert.notNull(service2,
            "Service [" + serviceName2 + "] is 'Null'");
        Assert.isTrue(service2.getKey() != null, "Service key is 'Null'");
        Assert.isTrue(service2.getName().equals(serviceName2),
            "Service name mismatch: Received name [" + service2.getName() +
                "], expected [" + serviceName2 + "]");
        Assert.isTrue(service2.getBinding() != null, "Null Binding");
        Assert.isTrue(service2.getBinding().size() > 0, "Missing Binding");

        // Add service to blacklist
        service2.setActiveStatus(ActiveStatusEnum.ACTIVE);
        registryProxy.updateService(service2);

        // Retrieve local services
        List<ServiceBean> services =
            registryProxy.getServicesByServiceType(
                ServiceTypeEnum.CONTENT_REPOSITORY, ActiveStatusEnum.ACTIVE, ActiveStatusEnum.ACTIVE);

        Assert.notNull(services, "Services are Null");
        Assert.isTrue(!services.isEmpty(), "Services are Empty");
        Assert.isTrue(services.size() > 1,
            "Unexpected number of services [" + services.size() + "], expected '2'");

        // Clean-up
        if (cleanup) {
            registryProxy.removeService(service2.getKey());
            service2 = registryProxy.getServiceByName(institution.getName(), serviceName2);
            Assert.isTrue(service2 == null, "Service is not 'Null'");

            registryProxy.removeService(service1.getKey());
            service1 = registryProxy.getServiceByName(institution.getName(), serviceName1);
            Assert.isTrue(service1 == null, "Service is not 'Null'");

            registryProxy.removeInstitution(institution.getKey());
            institution = registryProxy.getInstitutionByName(institutionName);
            Assert.isTrue(institution == null, "Institution is not 'Null'");
        }
    }

    @Test
    public void getServicesByServiceTypeAndBindingTypeTest() {
        // Retrieve existing services
        List<ServiceBean> servicesByServiceTypeAndBindingType =
                registryProxy.getServicesByServiceAndBindingType(
                ServiceTypeEnum.CONTENT_REPOSITORY,
                BindingTypeEnum.STUDENT_PROGRESS_ATOM,
                ActiveStatusEnum.ACTIVE, ActiveStatusEnum.ACTIVE);

        for (ServiceBean serviceBean : servicesByServiceTypeAndBindingType) {
            System.out.println("Existing service [" + serviceBean.getName() +
                    "] Status [" + serviceBean.getActiveStatus() + "]" +
                    "] Institution [" + serviceBean.getInstitutionName() + "]");
            Assert.isTrue(serviceBean.getActiveStatus() == ActiveStatusEnum.ACTIVE,
                    "Service [" + serviceBean.getName() + "] has an inValid value [" +
                            serviceBean.getActiveStatus() + "]");
            Assert.isTrue(serviceBean.getServiceType() == ServiceTypeEnum.CONTENT_REPOSITORY,
                    "Service [" + serviceBean.getName() + "] has an inValid value [" +
                            serviceBean.getServiceType() + "]");
            for (Binding binding : serviceBean.getBinding()) {
                Assert.isTrue(binding.getBindingType() == BindingTypeEnum.STUDENT_PROGRESS_ATOM,
                    "Service [" + serviceBean.getName() + "] has an inValid value [" +
                            binding.getBindingType() + "]");
            }

            Assert.isTrue(serviceBean.getServiceType() == ServiceTypeEnum.CONTENT_REPOSITORY,
                    "Service [" + serviceBean.getName() + "] has an inValid value [" +
                            serviceBean.getServiceType() + "]");
        }

        int serviceCount =
            servicesByServiceTypeAndBindingType.size();

        System.out.println("Count [" + serviceCount + "]");

        // Create an institution
        InstitutionBean institution = new InstitutionBean();
        String institutionName = getRandomValue("institutionName");
        institution.setName(institutionName);

        Contact contact = new Contact();
        contact.setPersonName(getRandomValue("ContactName"));
        contact.setAddress(getRandomValue("Address"));
        contact.setEmail(getRandomValue("Email"));
        institution.setContactInfo(contact);
        institution.setRegistryInstance(RegistryInstance.LOCAL);
        institution.setActiveStatus(ActiveStatusEnum.ACTIVE);

        System.out.println("Creating institution [" + institutionName + "]");
        registryProxy.createInstitution(institution);

        institution = registryProxy.getInstitutionByName(institutionName);
        Assert.notNull(institution, "Institution is 'Null'");
        Assert.isTrue(institution.getKey() != null, "Institution key is 'Null'");

        // Create a service1
        ServiceBean service1 = new ServiceBean();
        String serviceName1 = getRandomValue("serviceName");
        service1.setName(serviceName1);
        service1.setDescription(getRandomValue("Description"));
        service1.setInstitutionName(institutionName);
        service1.setServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);
        service1.setRegistryInstance(RegistryInstance.LOCAL);
        service1.setActiveStatus(ActiveStatusEnum.ACTIVE);

        // Add binding
        Binding bindingDto = new Binding();
        bindingDto.setDescription(getRandomValue("bindingDescription"));
        bindingDto.setBindingType(BindingTypeEnum.STUDENT_PROGRESS_ATOM);
        bindingDto.setAccessPoint(getRandomValue("http://someService"));
        service1.getBinding().add(bindingDto);

        registryProxy.createService(institution.getKey(), service1);

        service1 = registryProxy.getServiceByName(institution.getName(), serviceName1);
        Assert.notNull(service1, "Service is 'Null'");
        Assert.isTrue(service1.getKey() != null, "Service key is 'Null'");
        Assert.isTrue(service1.getName().equals(serviceName1),
            "Service name mismatch: Received name [" + service1.getName() +
                "], expected [" + serviceName1 + "]");

        // Retrieve Services
        servicesByServiceTypeAndBindingType =
                registryProxy.getServicesByServiceAndBindingType(
                ServiceTypeEnum.CONTENT_REPOSITORY,
                BindingTypeEnum.STUDENT_PROGRESS_ATOM,
                ActiveStatusEnum.ACTIVE, ActiveStatusEnum.ACTIVE);
        for (ServiceBean serviceBean : servicesByServiceTypeAndBindingType) {
            System.out.println("Services after add-1 [" + serviceBean.getName() +
                    "] Status [" + serviceBean.getActiveStatus() +
                    "] Institution [" + serviceBean.getInstitutionName() + "]");
        }

        Assert.isTrue(servicesByServiceTypeAndBindingType.size() == (serviceCount + 1),
            "Expected Service Count [" + (serviceCount + 1) +
                    "] but received [" + servicesByServiceTypeAndBindingType.size() + "]");


//        // Create a service2
//        ServiceBean service2 = new ServiceBean();
//        String serviceName2 = getRandomValue("serviceName");
//        service2.setName(serviceName2);
//        service2.setDescription(getRandomValue("Description"));
//        service2.setInstitutionName(institutionName);
//        service2.setServiceType(ServiceTypeEnum.CONTENT_REPOSITORY);
//
//        // Add binding
//        bindingDto = new Binding();
//        bindingDto.setDescription(getRandomValue("bindingDescription"));
//        bindingDto.setBindingType(BindingTypeEnum.ACTIVITY_STREAM_ATOM);
//        bindingDto.setAccessPoint(getRandomValue("http://someService"));
//        service2.getBinding().add(bindingDto);
//
//        registryProxy.createService(institution.getKey(), service2);
//
//        // Retrieve the service
//        service2 = registryProxy.getServiceByName(institution.getName(), serviceName2);
//
//        Assert.isTrue(service2 != null,
//            "Service [" + serviceName2 + "] is 'Null'");
//        Assert.isTrue(service2.getKey() != null, "Service key is 'Null'");
//        Assert.isTrue(service2.getName().equals(serviceName2),
//            "Service name mismatch: Received name [" + service2.getName() +
//                "], expected [" + serviceName2 + "]");
//        Assert.isTrue(service2.getBinding() != null, "Null Binding");
//        Assert.isTrue(service2.getBinding().size() > 0, "Missing Binding");
//
//        // Retrieve local services
//        List<ServiceBean> services =
//            registryProxy.getServicesByServiceAndBindingType(
//                ServiceTypeEnum.CONTENT_REPOSITORY,
//                    BindingTypeEnum.ACTIVITY_STREAM_ATOM, ActiveStatusEnum.ACTIVE);
//
//        // Retrieve All services
//        services =
//            registryProxy.getServicesByServiceAndBindingType(
//                ServiceTypeEnum.CONTENT_REPOSITORY,
//                    BindingTypeEnum.ACTIVITY_STREAM_ATOM, ActiveStatusEnum.ACTIVE);
//
        // Clean-up
        if (cleanup) {
//            registryProxy.removeService(service2.getKey());
//            service2 = registryProxy.getServiceByName(institution.getName(), serviceName2);
//            Assert.isTrue(service2 == null, "Service is not 'Null'");

//            registryProxy.removeService(service1.getKey());
//            service1 = registryProxy.getServiceByName(institution.getName(), serviceName1);
//            Assert.isTrue(service1 == null, "Service is not 'Null'");
//
            registryProxy.removeInstitution(institution.getKey());
            institution = registryProxy.getInstitutionByName(institutionName);
            Assert.isTrue(institution == null, "Institution is not 'Null'");
        }
    }

    private Institution createInstitution(String name, ActiveStatusEnum activeStatus) {
        Institution institution = new Institution();
        institution.setName(name);
        Contact contact = new Contact();
        contact.setPersonName("Joe Smith");
        contact.setEmail("joe.smith@nter.com");
        institution.setContactInfo(contact);

        institution.setActiveStatus(activeStatus);

        return institution;
    }

    private BusinessService createService(
            String name, ServiceTypeEnum serviceType, ActiveStatusEnum activeStatus) {
        BusinessService service = new BusinessService();
        service.setName(name);
        service.setServiceType(serviceType);
        service.setActiveStatus(activeStatus);

        return service;
    }

    private String getRandomValue(String pre) {
        Random r = new Random();
        return pre + "_" + r.nextInt();
    }
}
