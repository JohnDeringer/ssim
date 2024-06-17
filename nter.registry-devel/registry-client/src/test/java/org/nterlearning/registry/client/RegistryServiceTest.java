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

import org.springframework.util.Assert;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/30/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:registry-client-test.xml")
public class RegistryServiceTest {

    @Autowired
    private Registry registryService;

    public RegistryServiceTest() {
    }

    @Before
    public void init() {
    }

    @Test
    public void createInstitution() {
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        Institution institution = createInstitution(institutionName, status);

        Assert.isTrue(institution.getName().equals(institutionName));
        Assert.isTrue(institution.activeStatus == status);
    }

    @Test
    public void updateInstitution() {
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;

        // Create institution
        Institution institutionBefore = createInstitution(institutionName, status);

        Assert.isTrue(institutionBefore.getName().equals(institutionName));
        Assert.isTrue(institutionBefore.activeStatus == status);

        // Update institution
        institutionBefore.setActiveStatus(ActiveStatusEnum.BLACKLIST);
        Contact contact = institutionBefore.getContactInfo();
        contact.setPersonName("bob");
        contact.setEmail("bob@sri.com");
        institutionBefore.setContactInfo(contact);
        Institution institutionAfter = updateInstitution(institutionBefore);

        Assert.isTrue(institutionAfter.activeStatus == ActiveStatusEnum.BLACKLIST);
        Assert.isTrue(institutionAfter.getContactInfo().getPersonName().equals("bob"));
        Assert.isTrue(institutionAfter.getContactInfo().getEmail().equals("bob@sri.com"));
    }

    @Test
    public void getInstitutionByKey() {
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;

        // Create institution
        Institution institution1 = createInstitution(institutionName, status);

        Institution institution2 =
                registryService.getInstitutionByKey(institution1.getKey());

        Assert.notNull(institution2);
        Assert.isTrue(institution1.getKey().longValue() ==
                institution2.getKey().longValue());
    }

    @Test
    public void getInstitutionByName() {
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;

        // Create institution
        Institution institution1 = createInstitution(institutionName, status);

        Institution institution2 =
                registryService.getInstitutionByName(institution1.getName());

        Assert.notNull(institution2);
        Assert.isTrue(institution1.getName().equals(institution2.getName()));
    }

    @Test
    public void getInstitutions() {
        List<Institution> institutions =
                registryService.getInstitutions(ActiveStatusEnum.UNSPECIFIED);
        int count = institutions.size();

        // Add an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        createInstitution(institutionName, status);

        institutions =
                registryService.getInstitutions(ActiveStatusEnum.UNSPECIFIED);

        Assert.isTrue(institutions.size() == (count + 1));
    }

    @Test
    public void addService() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service
        BusinessService service2 =
                registryService.getServiceByName(institution.getKey(), serviceName);

        Assert.notNull(service2);
        Assert.isTrue(service1.getName().equals(service2.getName()));
        Assert.isTrue(service1.getServiceType() == service2.getServiceType());
        Assert.isTrue(service1.getActiveStatus() == service2.getActiveStatus());

        // Retrieve the service from the institution
        Institution institution2 =
                registryService.getInstitutionByKey(institution.getKey());

        Assert.notNull(institution2);
        Assert.isTrue(!institution2.getService().isEmpty());

        BusinessService service3 = institution2.getService().get(0);
        Assert.isTrue(service1.getName().equals(service3.getName()));
        Assert.isTrue(service1.getServiceType() == service3.getServiceType());
        Assert.isTrue(service1.getActiveStatus() == service3.getActiveStatus());

        // Clean-up
        registryService.removeService(service3.getKey());
    }

    @Test
    public void updateService() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service
        BusinessService service2 =
                registryService.getServiceByName(institution.getKey(), serviceName);

        Assert.notNull(service2);
        Assert.isTrue(service1.getName().equals(service2.getName()));
        Assert.isTrue(service1.getServiceType() == service2.getServiceType());
        Assert.isTrue(service1.getActiveStatus() == service2.getActiveStatus());

        // Modify the service
        service2.setServiceType(ServiceTypeEnum.NTER_PORTAL);
        service2.setActiveStatus(ActiveStatusEnum.BLACKLIST);

        // Update the service
        registryService.updateService(service2);

        // Retrieve the service
        BusinessService service3 =
                registryService.getServiceByName(institution.getKey(), serviceName);
        Assert.notNull(service2);
        Assert.isTrue(service3.getName().equals(service1.getName()));
        Assert.isTrue(service3.getServiceType() == ServiceTypeEnum.NTER_PORTAL);
        Assert.isTrue(service3.getActiveStatus() == ActiveStatusEnum.BLACKLIST);

        // Clean-up
        registryService.removeService(service3.getKey());
    }

    @Test
    public void removeService() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service
        BusinessService service2 =
                registryService.getServiceByName(institution.getKey(), serviceName);
        Assert.notNull(service2);

        // Remove the service
        registryService.removeService(service2.getKey());

        // Retrieve the service
        BusinessService service3 =
                registryService.getServiceByName(institution.getKey(), serviceName);
        Assert.isNull(service3);
    }

    @Test
    public void getServiceByNameAndInstitutionKey() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service
        BusinessService service2 =
                registryService.getServiceByName(institution.getKey(), serviceName);
        Assert.notNull(service2);
        Assert.isTrue(service1.getName().equals(service2.getName()));
        Assert.isTrue(service1.getServiceType() == service2.getServiceType());
        Assert.isTrue(service1.getActiveStatus() == service2.getActiveStatus());

        // Clean-up
        registryService.removeService(service2.getKey());
    }

    @Test
    public void getServiceByNameAndInstitutionName() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service
        BusinessService service2 =
                registryService.getServiceByName(institution.getName(), serviceName);
        Assert.notNull(service2);
        Assert.isTrue(service1.getName().equals(service2.getName()));
        Assert.isTrue(service1.getServiceType() == service2.getServiceType());
        Assert.isTrue(service1.getActiveStatus() == service2.getActiveStatus());

        // Clean-up
        registryService.removeService(service2.getKey());
    }

    @Test
    public void getServiceByKey() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service by name to get the key
        service1 =
                registryService.getServiceByName(institution.getName(), serviceName);

        // Retrieve the service using key
        BusinessService service2 =
                registryService.getServiceByKey(service1.getKey());
        Assert.notNull(service2);
        Assert.isTrue(service1.getName().equals(service2.getName()));
        Assert.isTrue(service1.getServiceType() == service2.getServiceType());
        Assert.isTrue(service1.getActiveStatus() == service2.getActiveStatus());

        // Clean-up
        registryService.removeService(service2.getKey());
    }

    @Test
    public void getServices() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        Institution institution = createInstitution(institutionName, status);

        // Retrieve services
        List<BusinessService> services = registryService.getServices(status);
        int count = services.size();

        // Add a service
        String serviceName = getRandomValue("Service");
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve services
        services = registryService.getServices(status);
        Assert.isTrue(!services.isEmpty());
        Assert.isTrue(services.size() == count + 1);

        // Retrieve the service by name to get the key
        service1 =
                registryService.getServiceByName(
                        institution.getName(), serviceName);

        // Clean-up
        registryService.removeService(service1.getKey());
    }

    @Test
    public void getServicesByServiceType() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        Institution institution = createInstitution(institutionName, status);

        // Retrieve services
        List<BusinessService> services =
                registryService.getServicesByServiceType(serviceType, status);
        int count = services.size();

        // Add a service
        String serviceName = getRandomValue("Service");
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve services
        services = registryService.getServicesByServiceType(serviceType, status);
        Assert.isTrue(!services.isEmpty());
        Assert.isTrue(services.size() == count + 1);

        // Retrieve the service by name to get the key
        service1 =
                registryService.getServiceByName(
                        institution.getName(), serviceName);
        // Clean-up
        registryService.removeService(service1.getKey());
    }

    @Test
    public void getServicesByServiceTypeAndBindingType() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BindingTypeEnum bindingType = BindingTypeEnum.REST;
        Institution institution = createInstitution(institutionName, status);

        // Retrieve services
        List<BusinessService> services =
                registryService.getServicesByServiceTypeAndBindingType(
                        serviceType, bindingType, status);
        int count = services.size();

        // Add a service
        String serviceName = getRandomValue("Service");
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service by name to get the key
        service1 =
                registryService.getServiceByName(
                        institution.getName(), serviceName);

        // Add a binding
        Binding binding = new Binding();
        String bindingDescription = getRandomValue("Binding");
        String endPoint = getRandomValue("http://www.sri.com");
        binding.setDescription(bindingDescription);
        binding.setBindingType(bindingType);
        binding.setAccessPoint(endPoint);
        registryService.addBinding(service1.getKey(), binding);

        // Retrieve services
        services = registryService.getServicesByServiceTypeAndBindingType(
                serviceType, bindingType, status);
        Assert.isTrue(!services.isEmpty());
        Assert.isTrue(services.size() == count + 1);

        // Clean-up
        registryService.removeService(service1.getKey());
    }

    @Test
    public void addBinding() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BindingTypeEnum bindingType = BindingTypeEnum.REST;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service
        service1 =
                registryService.getServiceByName(
                        institution.getName(), serviceName);

        // Add a binding
        Binding binding = new Binding();
        String bindingDescription = getRandomValue("Binding");
        String endPoint = getRandomValue("http://www.sri.com");
        binding.setDescription(bindingDescription);
        binding.setBindingType(bindingType);
        binding.setAccessPoint(endPoint);
        registryService.addBinding(service1.getKey(), binding);

        // Retrieve the binding
        List<Binding> bindings =
                registryService.getBindingsByAccessPoint(endPoint);
        Assert.isTrue(!bindings.isEmpty());
        boolean match = false;
        for (Binding binding1 : bindings) {
            if (binding1.getDescription().equals(binding.description) &&
                    binding1.getBindingType() == binding.getBindingType() &&
                    binding1.accessPoint.equals(binding.getAccessPoint())) {
                match = true;
                break;
            }
        }
        Assert.isTrue(match, "Unable to find binding");

        // Clean-up
        registryService.removeService(service1.getKey());
    }

    @Test
    public void removeBinding() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BindingTypeEnum bindingType = BindingTypeEnum.REST;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service
        service1 =
                registryService.getServiceByName(
                        institution.getName(), serviceName);

        // Add a binding
        Binding binding = new Binding();
        String bindingDescription = getRandomValue("Binding");
        String endPoint = getRandomValue("http://www.sri.com");
        binding.setDescription(bindingDescription);
        binding.setBindingType(bindingType);
        binding.setAccessPoint(endPoint);
        registryService.addBinding(service1.getKey(), binding);

        // Retrieve the binding
        List<Binding> bindings =
                registryService.getBindingsByAccessPoint(endPoint);
        Assert.isTrue(!bindings.isEmpty());
        boolean match = false;
        for (Binding binding1 : bindings) {
            if (binding1.getDescription().equals(binding.description) &&
                    binding1.getBindingType() == binding.getBindingType() &&
                    binding1.accessPoint.equals(binding.getAccessPoint())) {
                match = true;
                binding = binding1;
                break;
            }
        }
        Assert.isTrue(match, "Unable to find binding");

        // Remove binding
        registryService.removeBinding(binding.getKey());

        // Attempt to retrieve the binding
        bindings =
                registryService.getBindingsByAccessPoint(endPoint);
        match = false;
        for (Binding binding1 : bindings) {
            if (binding1.getKey().longValue() == binding.getKey().longValue()) {
                match = true;
                break;
            }
        }
        Assert.isTrue(!match, "Found removed binding");

        // Clean-up
        registryService.removeService(service1.getKey());
    }

    @Test
    public void getInstitutionByAccessPoint() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BindingTypeEnum bindingType = BindingTypeEnum.REST;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service
        service1 =
                registryService.getServiceByName(
                        institution.getName(), serviceName);

        // Add a binding
        Binding binding = new Binding();
        String bindingDescription = getRandomValue("Binding");
        String endPoint = getRandomValue("http://www.sri.com");
        binding.setDescription(bindingDescription);
        binding.setBindingType(bindingType);
        binding.setAccessPoint(endPoint);
        registryService.addBinding(service1.getKey(), binding);

        // Retrieve the institution by endpoint
        Institution institution1 =
                registryService.getInstitutionByAccessPoint(endPoint);

        Assert.notNull(institution1);
        Assert.isTrue(institution.getKey().longValue() ==
                institution1.getKey().longValue());
        Assert.isTrue(institution.getName().equals(institutionName));
        Assert.isTrue(institution.activeStatus == status);

        // Clean-up
        registryService.removeService(service1.getKey());
    }

    @Test
    public void getBindingByKey() {
        // Create an institution
        String institutionName = getRandomValue("Institution");
        ActiveStatusEnum status = ActiveStatusEnum.ACTIVE;
        ServiceTypeEnum serviceType = ServiceTypeEnum.NTER_SERVICE;
        BindingTypeEnum bindingType = BindingTypeEnum.REST;
        Institution institution = createInstitution(institutionName, status);

        // Add a service
        String serviceName = getRandomValue("Service");
        BusinessService service1 = new BusinessService();
        service1.setName(serviceName);
        service1.setServiceType(serviceType);
        service1.setActiveStatus(status);
        registryService.addService(institution.getKey(), service1);

        // Retrieve the service
        service1 =
                registryService.getServiceByName(
                        institution.getName(), serviceName);

        // Add a binding
        Binding binding = new Binding();
        String bindingDescription = getRandomValue("Binding");
        String endPoint = getRandomValue("http://www.sri.com");
        binding.setDescription(bindingDescription);
        binding.setBindingType(bindingType);
        binding.setAccessPoint(endPoint);
        registryService.addBinding(service1.getKey(), binding);

        // Retrieve the binding
        List<Binding> bindings =
                registryService.getBindingsByAccessPoint(endPoint);
        Assert.isTrue(!bindings.isEmpty());
        boolean match = false;
        for (Binding binding1 : bindings) {
            if (binding1.getDescription().equals(binding.description) &&
                    binding1.getBindingType() == binding.getBindingType() &&
                    binding1.accessPoint.equals(binding.getAccessPoint())) {
                match = true;
                binding = binding1;
                break;
            }
        }
        Assert.isTrue(match, "Unable to find binding");

        Binding binding1 =
                registryService.getBindingByKey(binding.getKey());
        Assert.notNull(binding1);
        Assert.isTrue(binding.getKey().longValue() ==
                      binding1.getKey().longValue());
        Assert.isTrue(binding.getDescription().equals(
                      binding1.getDescription()));
        Assert.isTrue(binding.getAccessPoint().equals(
                binding1.getAccessPoint()));
        Assert.isTrue(binding.getBindingType() == binding1.getBindingType());

        // Clean-up
        registryService.removeService(service1.getKey());
    }

    @Test
    public void getBindingTypes() {
        List<BindingTypeEnum> bindingTypes =
                registryService.getBindingTypes();
        Assert.isTrue(!bindingTypes.isEmpty());
    }

    @Test
    public void getServiceTypes() {
        List<ServiceTypeEnum> serviceTypes =
                registryService.getServiceTypes();
        Assert.isTrue(!serviceTypes.isEmpty());
    }

    private Institution createInstitution(String name, ActiveStatusEnum status) {
        Institution institution = new Institution();
        institution.setName(name);
        institution.setDescription(name + "-description");

        Contact contactInfo = new Contact();
        contactInfo.setPersonName("ContactName for:" + name);
        contactInfo.setDescription("ContactDescription for:" + name);
        contactInfo.setAddress("ContactAddress for:" + name);
        contactInfo.setEmail("ContactEmail for:" + name + "@sri.com");

        institution.setContactInfo(contactInfo);

        institution.setActiveStatus(status);

        registryService.createInstitution(institution);

        return registryService.getInstitutionByName(name);
    }

    private Institution updateInstitution(Institution institution) {

        registryService.updateInstitution(institution);

        return registryService.getInstitutionByName(institution.getName());
    }

    private static String getRandomValue(String value) {
        Random r = new Random();
        return value + ":" + r.nextInt();
    }

}
