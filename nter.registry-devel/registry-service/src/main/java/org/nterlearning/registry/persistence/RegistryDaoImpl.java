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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.BindingTypeEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.ServiceTypeEnum;

import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/5/11
 */
public class RegistryDaoImpl
        extends JpaDaoSupport implements RegistryDao{

    @Override
    @Transactional
    public void createInstitution(@NotNull InstitutionEntity institution) {

        // Check ServiceTypes
        for (ServiceEntity serviceEntity : institution.getService()) {
            ServiceTypeEntity serviceTypeEntity = serviceEntity.getServiceType();
            if (serviceTypeEntity.getKey() == null) {
                serviceTypeEntity =
                    getServiceTypeByName(serviceTypeEntity.getValue());
                serviceEntity.setServiceType(serviceTypeEntity);
            }
            // Check BindingTypes
            for (BindingEntity bindingEntity : serviceEntity.getBinding()) {
                BindingTypeEntity bindingTypeEntity = bindingEntity.getBindingType();
                if (bindingTypeEntity.getKey() == null) {
                    bindingTypeEntity =
                            getBindingTypeByName(bindingTypeEntity.getValue());
                    bindingEntity.setBindingType(bindingTypeEntity);
                }
            }
        }

        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(institution);
        } catch (Exception e) {
            logger.error("Error creating Institution", e);
        }
    }

    @Override
    @Transactional
    public void updateInstitution(@NotNull InstitutionEntity institution) {
        if (institution.getKey() == null) {
            throw new RuntimeException(
                "Unable to update institution with key [" +
                        institution.getKey() + "]");
        }

        InstitutionEntity dbInstitution =
                getInstitutionByKey(institution.getKey());
        if (dbInstitution == null) {
            throw new RuntimeException(
                    "Unable to update Institution using institutionKey [" +
                        institution.getKey() + "] because I could not find it.");
        }

        logger.info("Updating institution [" + institution.getName() +
                "] description [" + institution.getDescription() + "]");

        // Check serviceType
        for (ServiceEntity serviceEntity : institution.getService()) {
            ServiceTypeEntity serviceTypeEntity = serviceEntity.getServiceType();
            if (serviceTypeEntity.getKey() == null) {
                serviceTypeEntity =
                    getServiceTypeByName(serviceTypeEntity.getValue());
                serviceEntity.setServiceType(serviceTypeEntity);
            }
            // Check Bindings
            for (BindingEntity bindingEntity : serviceEntity.getBinding()) {
                BindingTypeEntity bindingTypeEntity = bindingEntity.getBindingType();
                if (bindingTypeEntity.getKey() == null) {
                    bindingTypeEntity =
                            getBindingTypeByName(bindingTypeEntity.getValue());
                    bindingEntity.setBindingType(bindingTypeEntity);
                }
            }
        }

        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(institution);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating Institution using institutionId [" +
                    institution.getKey() + "]", e);
        }
    }

    @Override
    @Transactional
    public void removeInstitution(@NotNull Long key) {
        if (key < 1) {
            throw new RuntimeException(
                "Unable to remove Institution with key [" + key + "]");
        }

        InstitutionEntity dbInstitution = getInstitutionByKey(key);
        if (dbInstitution == null) {
            throw new RuntimeException(
                    "Unable to remove Institution using institutionId [" +
                        key + "] because I could not find it.");
        }
        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.remove(dbInstitution);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error remove Institution using institutionId [" +
                    key + "]", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public InstitutionEntity getInstitutionByName(@NotNull String name) {
        if (name.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve institution with name [" +
                        name + "]");
        }

        InstitutionEntity institution = null;
        try {
            List<InstitutionEntity> institutions =
                getJpaTemplate().find(
                    "select o from InstitutionEntity o left join fetch o.service s where o.name = ?1",
                        name);
            if (!institutions.isEmpty()) {
                institution = institutions.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Institution by name [" +
                    name + "]", e);
        }

        return institution;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public InstitutionEntity getInstitutionByKey(@NotNull Long key) {
        if (key < 1) {
            throw new RuntimeException(
                "Unable to retrieve Institution with key [" + key + "]");
        }

        InstitutionEntity institution = null;
        try {
            List<InstitutionEntity> institutions =
                getJpaTemplate().find(
                    "select o from InstitutionEntity o left join fetch o.service s where o.key = ?1",
                        key);
            if (!institutions.isEmpty()) {
                institution = institutions.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Institution by key [" +
                    key + "]", e);
        }

        return institution;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public InstitutionEntity getInstitutionByServiceKey(@NotNull Long serviceKey) {
        if (serviceKey < 1) {
            throw new RuntimeException(
                "Unable to retrieve Institution with serviceKey [" + serviceKey + "]");
        }

        InstitutionEntity institution = null;
        try {
            List<InstitutionEntity> institutions =
                getJpaTemplate().find(
                    "select o from InstitutionEntity o join fetch o.service s where s.key = ?1",
                        serviceKey);
            if (!institutions.isEmpty()) {
                institution = institutions.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Institution by serviceKey [" +
                    serviceKey + "]", e);
        }

        return institution;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public InstitutionEntity getInstitutionByAccessPoint(@NotNull String endpoint) {
        if (endpoint.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve institution with endpoint [" +
                        endpoint + "]");
        }

        InstitutionEntity institution = null;
        List<ServiceEntity> services = getJpaTemplate().find(
            "select s from ServiceEntity s left join fetch s.binding b where b.accessPoint = ?1",
                        endpoint);

        if (!services.isEmpty()) {
            ServiceEntity service = services.get(0);
            institution = getInstitutionByServiceKey(service.getKey());
        } else {
            logger.warn("Unable to find service with endpoint [" + endpoint + "]");
        }

        return institution;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<InstitutionEntity> getInstitutions() {
        List<InstitutionEntity> institutions;
        try {
            institutions =
                getJpaTemplate().find(
                    "select distinct o from InstitutionEntity o left join fetch o.service");
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Institutions", e);
        }

        return institutions;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<InstitutionEntity> getInstitutionsByServiceType(
            @NotNull ServiceTypeEnum serviceTypeEnum) {
        List<InstitutionEntity> institutions;

        ServiceTypeEntity serviceTypeEntity =
                getServiceTypeByName(serviceTypeEnum.value());

        try {
            institutions =
                getJpaTemplate().find(
                    "select distinct o from InstitutionEntity o join fetch o.service s where s.serviceType = ?1",
                        serviceTypeEntity);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Institutions", e);
        }

        return institutions;
    }

    @Override
    @Transactional
    public void addService(@NotNull Long institutionKey, @NotNull ServiceEntity service) {
        if (institutionKey < 1) {
            throw new RuntimeException(
                "Unable to add Service with institutionKey [" + institutionKey + "]");
        }

        InstitutionEntity institution = getInstitutionByKey(institutionKey);
        if (institution == null) {
            throw new RuntimeException(
                "Unable to add service to institution because the institution could not be found using key ["
                    + institutionKey + "]");
        }

        // Ensure ServiceType(s) are live entities
        ServiceTypeEntity serviceTypeEntity = service.getServiceType();
        if (serviceTypeEntity.getKey() == null) {
            serviceTypeEntity =
                    getServiceTypeByName(serviceTypeEntity.getValue());
            service.setServiceType(serviceTypeEntity);
        }

        // Ensure BindingType(s) are live entities
        for (BindingEntity bindingEntity : service.getBinding()) {
            BindingTypeEntity bindingTypeEntity = bindingEntity.getBindingType();
            if (bindingTypeEntity.getKey() == null) {
                bindingTypeEntity =
                        getBindingTypeByName(bindingTypeEntity.getValue());
                bindingEntity.setBindingType(bindingTypeEntity);
            }
        }

        institution.getService().add(service);
        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(institution);
        } catch (Exception e) {
            logger.error("Error adding Service to Institution [" +
                    institutionKey + "]", e);
        }
    }

    @Override
    @Transactional
    public void updateService(@NotNull ServiceEntity service) {
        ServiceEntity dbService = getServiceByKey(service.getKey());
        if (dbService == null) {
            throw new RuntimeException(
                    "Unable to update Service using key [" +
                        service.getKey() + "] because I could not find it.");
        }

        // Get serviceType
        ServiceTypeEntity serviceTypeEntity = service.getServiceType();
        if (serviceTypeEntity.getKey() == null) {
            serviceTypeEntity =
                    getServiceTypeByName(serviceTypeEntity.getValue());
            service.setServiceType(serviceTypeEntity);
        }

        // Bindings
        for (BindingEntity bindingEntity : service.getBinding()) {
            BindingTypeEntity bindingTypeEntity = bindingEntity.getBindingType();
            if (bindingTypeEntity.getKey() == null) {
                bindingTypeEntity =
                        getBindingTypeByName(bindingTypeEntity.getValue());
                bindingEntity.setBindingType(bindingTypeEntity);
            }
        }

        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(service);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating Service using key [" +
                    service.getKey() + "]", e);
        }
    }

    @Override
    @Transactional
    public void removeService(@NotNull Long key) {
        if (key < 1) {
            throw new RuntimeException(
                "Unable to remove Service with key [" + key + "]");
        }

        ServiceEntity dbService = getServiceByKey(key);
        if (dbService == null) {
            throw new RuntimeException(
                    "Unable to remove Service using serviceId [" +
                        key + "] because I could not find it.");
        }
        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.remove(dbService);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error remove Service using serviceId [" +
                    key + "]", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<ServiceEntity> getServicesByName(@NotNull String name) {
        if (name.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Services with name [" + name + "]");
        }

        List<ServiceEntity> services;
        try {
            services =
                getJpaTemplate().find(
                    "select o from ServiceEntity o left join fetch o.binding where o.name = ?1",
                        name);

        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Service by name [" +
                    name + "]", e);
        }

        return services;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ServiceEntity getServiceByName(@NotNull String institutionName,
                                          @NotNull String serviceName) {
        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Service with institutionName [" + institutionName + "]");
        }
        if (serviceName.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Service with serviceName [" + serviceName + "]");
        }

        ServiceEntity service = null;
        try {
            InstitutionEntity institution;
            List<InstitutionEntity> institutions =
                getJpaTemplate().find(
                    "select o from InstitutionEntity o left join fetch o.service s where o.name = ?1 and s.name = ?2",
                        institutionName, serviceName);
            if (!institutions.isEmpty()) {
                institution = institutions.get(0);
                if (!institution.getService().isEmpty()) {
                    service = institution.getService().get(0);
                    // Attach bindings
                    service = getBindingsForService(service.getKey());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Service by name [" +
                    serviceName + "]", e);
        }

        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ServiceEntity getServiceByName(@NotNull Long institutionKey,
                                          @NotNull String serviceName) {
        if (institutionKey < 1) {
            throw new RuntimeException(
                "Unable to retrieve Service with institutionKey [" + institutionKey + "]");
        }

        if (serviceName.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Service with serviceName [" + serviceName + "]");
        }

        ServiceEntity service = null;
        try {
            InstitutionEntity institution;
            List<InstitutionEntity> institutions =
                getJpaTemplate().find(
                    "select o from InstitutionEntity o left join fetch o.service s where o.key = ?1 and s.name = ?2",
                        institutionKey, serviceName);
            if (!institutions.isEmpty()) {
                institution = institutions.get(0);
                if (!institution.getService().isEmpty()) {
                    service = institution.getService().get(0);
                    // Attach bindings
                    service = getBindingsForService(service.getKey());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Service by name [" +
                    serviceName + "]", e);
        }

        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ServiceEntity getServiceByKey(@NotNull Long key) {
        if (key < 1) {
            throw new RuntimeException(
                "Unable to retrieve Service with Key [" + key + "]");
        }

        ServiceEntity service = null;
        try {
            List<ServiceEntity> services =
                getJpaTemplate().find(
                    "select o from ServiceEntity o left join fetch o.binding where o.key = ?1",
                        key);
            if (!services.isEmpty()) {
                service = services.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Service by key [" +
                    key + "]", e);
        }

        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ServiceEntity getServiceWithBindingKey(@NotNull Long key) {
        if (key < 1) {
            throw new RuntimeException(
                "Unable to retrieve Service with bindingKey [" + key + "]");
        }

        ServiceEntity service = null;
        try {
            List<ServiceEntity> services =
                getJpaTemplate().find(
                    "select o from ServiceEntity o left join fetch o.binding b where b.key = ?1",
                        key);
            if (!services.isEmpty()) {
                service = services.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Service by Binding Key [" +
                    key + "]", e);
        }

        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<ServiceEntity> getServices() {
        List<ServiceEntity> services;
        try {
            services =
                getJpaTemplate().find(
                    "select o from ServiceEntity o left join fetch o.binding");
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Services", e);
        }

        return services;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<ServiceEntity> getServicesByServiceType(
            @NotNull ServiceTypeEnum serviceTypeEnum) {
        List<ServiceEntity> services;

        ServiceTypeEntity serviceTypeEntity =
                getServiceTypeByName(serviceTypeEnum.value());

        try {
            services =
                getJpaTemplate().find(
                    "select o from ServiceEntity o left join fetch o.binding where o.serviceType = ?1",
                        serviceTypeEntity);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Services using ServiceType [" +
                        serviceTypeEnum.value() + "]", e);
        }

        return services;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<ServiceEntity> getServicesByServiceTypeAndBindingType(
            @NotNull ServiceTypeEnum serviceTypeEnum,
            @NotNull BindingTypeEnum bindingTypeEnum) {
        List<ServiceEntity> services;

        ServiceTypeEntity serviceTypeEntity =
                getServiceTypeByName(serviceTypeEnum.value());

        BindingTypeEntity bindingTypeEntity =
                getBindingTypeByName(bindingTypeEnum.value());

        try {
            services =
                getJpaTemplate().find(
            "select o from ServiceEntity o left join fetch o.binding b where o.serviceType = ?1 and b.bindingType = ?2",
                        serviceTypeEntity, bindingTypeEntity);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Services using ServiceType [" +
                serviceTypeEnum.value() + "] and BindingType [" +
                bindingTypeEnum + "]", e);
        }

        return services;
    }

    @Override
    @Transactional
    public void addBinding(@NotNull Long serviceKey, @NotNull BindingEntity binding) {
        if (serviceKey < 1) {
            throw new RuntimeException(
                "Unable to add Binding with serviceKey [" + serviceKey + "]");
        }

        ServiceEntity service = getServiceByKey(serviceKey);
        if (service == null) {
            throw new RuntimeException("Unable to add binding [" +
            binding.getAccessPoint() + "] to service [" + serviceKey +
                    "] because the service could not be found");
        }

        // BindingType - retrieve attached entity
        BindingTypeEntity bindingTypeEntity = binding.getBindingType();
        if (bindingTypeEntity != null &&
                bindingTypeEntity.getValue() != null) {
            bindingTypeEntity =
                getBindingTypeByName(bindingTypeEntity.getValue());
            binding.setBindingType(bindingTypeEntity);
        } else {
            throw new RuntimeException(
                "Unable to persist bindingType from value [" +
                (bindingTypeEntity != null ? bindingTypeEntity.getValue() : null) + "]");
        }

        service.getBinding().add(binding);

        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(service);
        } catch (Exception e) {
            logger.error("Error adding binding to service [" +
                    serviceKey + "]", e);
        }
    }

    @Override
    @Transactional
    public void updateBinding(@NotNull BindingEntity bindingEntity) {
        if (bindingEntity.getKey() == null || bindingEntity.getKey() == 0) {
            throw new RuntimeException("Unable to update binding [" +
                bindingEntity.getKey() +
                    "] because it could not be found");
        }

        // Look up the binding
        BindingEntity storedBinding = getBindingByKey(bindingEntity.getKey());
        if (storedBinding == null ) {
            throw new RuntimeException("Unable to update binding [" +
                bindingEntity.getKey()  +
                 "] because it could not be found");
        }

        // BindingType
        BindingTypeEntity bindingTypeEntity = bindingEntity.getBindingType();
        if (bindingTypeEntity.getKey() == null) {
            bindingTypeEntity =
                getBindingTypeByName(bindingTypeEntity.getValue());
        }

        // Update binding
        storedBinding.setDescription(bindingEntity.getDescription());
        storedBinding.setBindingType(bindingTypeEntity);
        storedBinding.setAccessPoint(bindingEntity.accessPoint);

        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(storedBinding);
        } catch (Exception e) {
            logger.error("Error updating binding [" +
                    (bindingTypeEntity != null ? bindingTypeEntity.getKey() :
                            bindingTypeEntity) + "]", e);
        }
    }

    @Override
    @Transactional
    public void removeBinding(@NotNull Long key) {
        if (key < 1) {
            throw new RuntimeException(
                "Unable to remove Binding with Key [" + key + "]");
        }

        BindingEntity dbBinding = getBindingByKey(key);
        if (dbBinding == null) {
            throw new RuntimeException(
                    "Unable to remove Binding using BindingId [" +
                        key + "] because I could not find it.");
        }
        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.remove(dbBinding);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error remove Binding using BindingId [" +
                    key + "]", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public BindingEntity getBindingByKey(@NotNull Long key) {
        if (key < 1) {
            throw new RuntimeException(
                "Unable to retrieve Binding with Key [" + key + "]");
        }

        BindingEntity binding = null;
        try {
            List<BindingEntity> bindings =
                getJpaTemplate().find(
                    "select o from BindingEntity o where o.key = ?1",
                        key);
            if (!bindings.isEmpty()) {
                binding = bindings.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Binding by key [" +
                    key + "]", e);
        }

        return binding;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<BindingEntity> getBindingsByAccessPoint(@NotNull String endPoint) {
        if (endPoint.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Bindings with endPoint [" + endPoint + "]");
        }

        List<BindingEntity> bindings;
        try {
            bindings =
                getJpaTemplate().find(
                    "select o from BindingEntity o where o.accessPoint = ?1",
                        endPoint);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving Bindings by accessPoint [" +
                    endPoint + "]", e);
        }

        return bindings;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ServiceEntity getBindingsForService(@NotNull Long serviceKey)  {
        if (serviceKey < 1) {
            throw new RuntimeException(
                "Unable to retrieve Bindings for Service with serviceKey [" + serviceKey + "]");
        }

        ServiceEntity service = null;
        try {
            List<ServiceEntity> services =
                getJpaTemplate().find(
                    "select o from ServiceEntity o left join fetch o.binding where o.key = ?1",
                        serviceKey);
            if (!services.isEmpty()) {
                service = services.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving BindingForService using serviceKey [" +
                    serviceKey + "]", e);
        }

        return service;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ServiceEntity getBindingsForService(
            @NotNull Long serviceKey, @NotNull BindingTypeEnum bindingType) {
        if (serviceKey < 1) {
            throw new RuntimeException(
                "Unable to retrieve Bindings for Service with serviceKey [" + serviceKey + "]");
        }

        ServiceEntity service = null;

        // BindingType
        BindingTypeEntity bindingTypeEntity =
                getBindingTypeByName(bindingType.value());

        try {
            List<ServiceEntity> services =
                getJpaTemplate().find(
                    "select o from ServiceEntity o left join fetch o.binding b where o.key = ?1 and b.bindingType = ?2",
                        serviceKey, bindingTypeEntity);
            if (!services.isEmpty()) {
                service = services.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving BindingForService using serviceKey [" +
                    serviceKey + "]", e);
        }

        return service;
    }

    @Override
    @Transactional
    public void addBindingType(@NotNull BindingTypeEntity bindingType) {
        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(bindingType);
        } catch (Exception e) {
            logger.error("Error adding bindingType [" +
                    bindingType.getValue() + "]", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public BindingTypeEntity getBindingTypeByName(@NotNull String name) {
        if (name.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve BindingType with name [" + name + "]");
        }

        BindingTypeEntity bindingType = null;
        try {
            List<BindingTypeEntity> bindingTypes =
                getJpaTemplate().find(
                    "select o from BindingTypeEntity o where o.value = ?1",
                        name);
            if (!bindingTypes.isEmpty()) {
                bindingType = bindingTypes.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving BindingType by name [" +
                    name + "]", e);
        }

        return bindingType;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<BindingTypeEntity> getBindingTypes() {
        List<BindingTypeEntity> bindingTypes;
        try {
            bindingTypes =
                getJpaTemplate().find(
                    "select o from BindingTypeEntity o");
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving BindingTypes", e);
        }
        if (bindingTypes == null) {
            bindingTypes = new ArrayList<BindingTypeEntity>();
        }

        return bindingTypes;
    }

    @Override
    @Transactional
    public void addServiceType(@NotNull ServiceTypeEntity serviceType) {
         try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(serviceType);
        } catch (Exception e) {
            logger.error("Error adding serviceType [" +
                    serviceType.getValue() + "]", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ServiceTypeEntity getServiceTypeByName(@NotNull String name) {
        if (name.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve ServiceType with name [" + name + "]");
        }

        ServiceTypeEntity serviceType = null;
        try {
            List<ServiceTypeEntity> serviceTypes =
                getJpaTemplate().find(
                    "select o from ServiceTypeEntity o where o.value = ?1",
                        name);
            if (!serviceTypes.isEmpty()) {
                serviceType = serviceTypes.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving ServiceType by name [" +
                    name + "]", e);
        }

        return serviceType;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<ServiceTypeEntity> getServiceTypes() {
        List<ServiceTypeEntity> serviceTypes;
        try {
            serviceTypes =
                getJpaTemplate().find(
                    "select o from ServiceTypeEntity o");
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving ServiceTypes", e);
        }
        if (serviceTypes == null) {
            serviceTypes = new ArrayList<ServiceTypeEntity>();
        }

        return serviceTypes;
    }


}
