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
package org.nterlearning.registry.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.registry.persistence.*;

import org.apache.log4j.Logger;

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.BlacklistItem;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/5/11
 */
public class RegistryModelImpl implements RegistryModel, Serializable {

    private RegistryDao registryDao;
    private BlacklistModel blacklist;

    private Logger logger = Logger.getLogger(RegistryModelImpl.class);

    @Override
    @NotNull
    public RequestStatus createInstitution(@NotNull Institution institution) {
        // Check uniqueness
        InstitutionEntity institutionEntity =
                getInstitutionEntityByName(
                        institution.getName()
                );
        if (institutionEntity != null) {
            throw new RuntimeException(
                "Institution name [" + institution.getName() +
                    "] is not unique. Please enter a unique institution Name.");
        }

        // Required values
        if (institution.getContactInfo() == null ||
                institution.getContactInfo().getPersonName() == null) {
            throw new RuntimeException("Institution Contact name is required");
        }
        if (institution.getContactInfo() == null ||
                institution.getContactInfo().getEmail() == null) {
            throw new RuntimeException("Institution Contact email is required");
        }

        // Response object
        RequestStatus status = RequestStatus.FAILURE;
        try {
            registryDao.createInstitution(convert(institution));

            ActiveStatusEnum activeStatus = institution.getActiveStatus();
            if (activeStatus == null) {
                activeStatus = ActiveStatusEnum.UNSPECIFIED;
            }

            // Set blacklist status
            setBlacklistStatus(
                institution.getName(),
                    null, activeStatus);

            status = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception creating institution", e);
        }

        return status;
    }

    @Override
    @NotNull
    public RequestStatus updateInstitution(@NotNull Institution institution) {
        // Required values
        if (institution.getKey() == null) {
            throw new RuntimeException(
                "Unable to update institution with key [" + institution.getKey() + "]");
        }
        if (institution.getContactInfo() == null ||
                institution.getContactInfo().getPersonName() == null) {
            throw new RuntimeException("Institution Contact name is required");
        }

        logger.info("Updating institution [" + institution.getName() +
                "] description [" + institution.getDescription() + "]");

        RequestStatus status = RequestStatus.FAILURE;
        try {
            registryDao.updateInstitution(convert(institution));
            // Set blacklist status
            setBlacklistStatus(
                    institution.getName(), null, institution.getActiveStatus());

            status = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception updating institution", e);
        }

        return status;
    }

    @Override
    @NotNull
    public RequestStatus removeInstitution(@NotNull Long institutionKey) {
        RequestStatus status = RequestStatus.FAILURE;

        InstitutionEntity institution =
                registryDao.getInstitutionByKey(institutionKey);
        if (institution == null) {
            throw new RuntimeException(
                "Unable to remove institution using key [" + institutionKey +
                    "] because the institution could not be found");
        }

        try {
            registryDao.removeInstitution(institutionKey);
            status = RequestStatus.SUCCESS;

            // Remove item from blacklist
            removeBlacklistItem(institution.getName(), null);
        } catch (Exception e) {
            logger.error("Unexpected exception removing institution", e);
        }

        return status;
    }

    @Override
    @Nullable
    public Institution getInstitutionByKey(@NotNull Long institutionKey) {
        Institution institution = null;

        try {
            InstitutionEntity institutionEntity =
                        registryDao.getInstitutionByKey(institutionKey);
            if (institutionEntity != null) {
                // Return object
                institution = convertShallow(institutionEntity);
                String institutionName = institution.getName();
                // Set blacklist status - institution
                institution.setActiveStatus(
                        getBlacklistStatus(
                                institutionName, null)
                );

                // Retrieve bindings
                for (ServiceEntity serviceEntity : institutionEntity.getService()) {
                    // Bindings
                    serviceEntity =
                        registryDao.getBindingsForService(
                            serviceEntity.getKey());

                    // Convert the entities to transport objects
                    BusinessService businessService = convert(serviceEntity);
                    // Set institution name
                    businessService.setInstitutionName(institution.getName());
                    // Set blacklist status - service
                    businessService.setActiveStatus(
                        getBlacklistStatus(
                                institutionName,
                                businessService.getName()

                    ));
                    institution.getService().add(businessService);
                }
            } else {
                logger.warn("Unable to find institution by key [" + institutionKey + "]");
            }
        } catch (Exception e) {
            logger.error(
                "Unexpected exception retrieving institution by key [" +
                    institutionKey + "]", e);
        }

        return institution;
    }

    @Override
    @Nullable
    public Institution getInstitutionByName(@NotNull String name) {
        if (name.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve institution using name [" + name + "]");
        }

        Institution institution = null;
        try {
            InstitutionEntity institutionEntity =
                        registryDao.getInstitutionByName(name);
            if (institutionEntity != null) {
                // Return object
                institution = convertShallow(institutionEntity);
                String institutionName = institution.getName();
                // Set blacklist status - institution
                institution.setActiveStatus(
                        getBlacklistStatus(
                                institutionName, null)
                );

                // Retrieve bindings
                for (ServiceEntity serviceEntity : institutionEntity.getService()) {
                    // Bindings
                    serviceEntity =
                        registryDao.getBindingsForService(
                            serviceEntity.getKey());

                    // Convert the entities to transport objects
                    BusinessService businessService = convert(serviceEntity);
                    // Set institution name
                    businessService.setInstitutionName(name);
                    // Set blacklist status - service
                    businessService.setActiveStatus(
                        getBlacklistStatus(
                                institutionName,
                                businessService.getName()

                    ));
                    institution.getService().add(businessService);
                }
            } else {
                logger.warn("Unable to find institution [" + name + "]");
            }
        } catch (Exception e) {
            logger.error(
                "Unexpected exception retrieving institution by name [" +
                    name + "]", e);
        }

        return institution;
    }

    @Override
    @Nullable
    public Institution getInstitutionByServiceKey(@NotNull Long serviceKey) {
        Institution institution = null;
        try {
            InstitutionEntity institutionEntity =
                        registryDao.getInstitutionByServiceKey(serviceKey);
            if (institutionEntity != null) {
                // Return object
                institution = convertShallow(institutionEntity);
                String institutionName = institution.getName();
                // Set blacklist status - institution
                institution.setActiveStatus(
                        getBlacklistStatus(
                                institutionName, null)
                );

                // Retrieve bindings
                for (ServiceEntity serviceEntity : institutionEntity.getService()) {
                    // Bindings
                    serviceEntity =
                        registryDao.getBindingsForService(
                            serviceEntity.getKey());

                    // Convert the entities to transport objects
                    BusinessService businessService = convert(serviceEntity);
                    // Set institution name
                    businessService.setInstitutionName(institutionName);
                    // Set blacklist status - service
                    businessService.setActiveStatus(
                        getBlacklistStatus(
                                institutionName,
                                businessService.getName()

                    ));
                    institution.getService().add(businessService);
                }
            } else {
                logger.warn("Unable to find institution by ServiceKey [" +
                        serviceKey + "]");
            }
        } catch (Exception e) {
            logger.error(
                "Unexpected exception retrieving institution by ServiceKey [" +
                    serviceKey + "]", e);
        }

        return institution;
    }

    @Override
    @Nullable
    public Institution getInstitutionByAccessPoint(@NotNull String endPoint) {
        if (endPoint.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve institution using endpoint [" + endPoint + "]");
        }

        Institution institution = null;
        try {
            InstitutionEntity institutionEntity =
                        registryDao.getInstitutionByAccessPoint(endPoint);
            if (institutionEntity != null) {
                // Return object
                institution = convertShallow(institutionEntity);
                String institutionName = institution.getName();
                // Set blacklist status - institution
                institution.setActiveStatus(
                        getBlacklistStatus(
                                institutionName, null)
                );

                // Retrieve bindings
                for (ServiceEntity serviceEntity : institutionEntity.getService()) {
                    // Bindings
                    serviceEntity =
                        registryDao.getBindingsForService(
                            serviceEntity.getKey());

                    // Convert the entities to transport objects
                    BusinessService businessService = convert(serviceEntity);
                    // Set institution name
                    businessService.setInstitutionName(institutionName);
                    // Set blacklist status - service
                    businessService.setActiveStatus(
                        getBlacklistStatus(
                                institutionName,
                                businessService.getName()

                        ));
                    institution.getService().add(businessService);
                }
            } else {
                logger.warn("Unable to find institution by endpoint [" + endPoint + "]");
            }
        } catch (Exception e) {
            logger.error(
                "Unexpected exception retrieving institution by endpoint [" +
                    endPoint + "]", e);
        }

        return institution;
    }

    @Override
    @NotNull
    public List<Institution> getInstitutions(@NotNull ActiveStatusEnum status) {
        List<Institution> institutions = new ArrayList<Institution>();
        try {
            List<InstitutionEntity> institutionEntities =
                    registryDao.getInstitutions();

            for (InstitutionEntity institutionEntity : institutionEntities) {
                String institutionName = institutionEntity.getName();

                // Check institution Blacklist status
                ActiveStatusEnum blacklistStatus =
                        getBlacklistStatus(
                                institutionName, null);

                // Filter by blacklist status before adding to institution collection
                if (status == ActiveStatusEnum.UNSPECIFIED ||
                        status == blacklistStatus) {
                    // Populate Institution
                    Institution institution = convertShallow(institutionEntity);
                    // Set blacklist status
                    institution.setActiveStatus(blacklistStatus);

                    // Add services if blacklist status match
                    for (ServiceEntity serviceEntity : institutionEntity.getService()) {
                        ActiveStatusEnum activeStatusEnum =
                            getBlacklistStatus(
                                    institutionName,
                                    serviceEntity.getName());
                        // Check service blacklist
                        if (status == ActiveStatusEnum.UNSPECIFIED ||
                                status == activeStatusEnum) {

                            // Bindings
                            serviceEntity =
                                    registryDao.getBindingsForService(
                                            serviceEntity.getKey());

                            BusinessService businessService = convert(serviceEntity);
                            // Set institution name
                            businessService.setInstitutionName(institution.getName());
                            // Set blacklist status
                            businessService.setActiveStatus(activeStatusEnum);
                            // Add service to institution
                            institution.getService().add(businessService);
                        }
                    }
                    institutions.add(institution);
                } // End if - Institution Blacklist
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving institutions", e);
        }

        return institutions;
    }

    @Override
    @NotNull
    public RequestStatus addService(@NotNull Long institutionKey,
                                    @NotNull BusinessService service) {
        // Check uniqueness
        ServiceEntity serviceEntity =
                getServiceEntityByName(institutionKey, service.getName());

        if (serviceEntity != null) {
            throw new RuntimeException(
                "Service name [" + service.getName() +
                        "] is not unique. Please enter a unique service Name.");
        }

        RequestStatus status = RequestStatus.FAILURE;

        try {
            registryDao.addService(institutionKey, convert(service));
            // Ensure there is an institutionName
            String institutionName = service.getInstitutionName();
            if (institutionName == null || institutionName.isEmpty()) {
                InstitutionEntity institutionEntity =
                        registryDao.getInstitutionByKey(institutionKey);
                if (institutionEntity != null) {
                    institutionName = institutionEntity.getName();
                } else {
                    throw new RuntimeException(
                        "Cannot add service [" + service.getName() +
                            "] unable to find institution using key [" +
                                institutionKey + "]");
                }
            }

            // Set blacklist status
            setBlacklistStatus(
                institutionName,
                    service.getName(), service.getActiveStatus());
            status = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception persisting service", e);
        }

        return status;
    }

    @Override
    @NotNull
    public RequestStatus updateService(@NotNull BusinessService service) {
        if (service.getKey() == null) {
            throw new RuntimeException(
                "Unable to update service with 'Null' key");
        }

        RequestStatus status = RequestStatus.FAILURE;
        try {
            registryDao.updateService(convert(service));
            // Set blacklist status
            ActiveStatusEnum activeStatusEnum = service.getActiveStatus();
            setBlacklistStatus(
                service.getInstitutionName(),
                    service.getName(), activeStatusEnum);
            status = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception updating service", e);
        }

        return status;
    }

    @Override
    @NotNull
    public RequestStatus removeService(@NotNull Long serviceKey) {
        RequestStatus status = RequestStatus.FAILURE;

        InstitutionEntity institution =
                registryDao.getInstitutionByServiceKey(serviceKey);

        ServiceEntity service =
                registryDao.getServiceByKey(serviceKey);
        if (service == null) {
            throw new RuntimeException(
                "Unable to remove service using key [" + serviceKey +
                    "] because the service could not be found");
        }

        try {
            registryDao.removeService(serviceKey);
            status = RequestStatus.SUCCESS;

            // Remove item from blacklist
            if (institution != null) {
                removeBlacklistItem(institution.getName(), service.getName());
            } else {
                logger.error(
                    "Unable to remove service [" + serviceKey +
                    "] from blacklist because I could not find the parent Institution");
            }
        } catch (Exception e) {
            logger.error("Unexpected exception removing service", e);
        }

        return status;
    }

    @Override
    @NotNull
    public List<BusinessService> getServicesByName(@NotNull String name) {
        List<BusinessService> services = new ArrayList<BusinessService>();
        try {
            List<ServiceEntity> serviceEntities =
                    registryDao.getServicesByName(name);
            for (ServiceEntity serviceEntity : serviceEntities) {
                // Retrieve institution name
                InstitutionEntity institutionEntity =
                    registryDao.getInstitutionByServiceKey(serviceEntity.getKey());
                String institutionName = institutionEntity.getName();

                // Return object
                BusinessService businessService = convert(serviceEntity);
                // Institution name
                businessService.setInstitutionName(institutionName);
                // Set blacklist status
                businessService.setActiveStatus(
                        getBlacklistStatus(institutionName,
                                businessService.getName()
                        )
                );
                // Add service to services list
                services.add(businessService);
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving service", e);
        }

        return services;
    }

    @Override
    @Nullable
    public BusinessService getServiceByName(
            @NotNull String institutionName,
            @NotNull String serviceName) {

        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                "Invalid institution name [" + institutionName + "]");
        }

        if (serviceName.isEmpty()) {
            throw new RuntimeException(
                "Invalid service name [" + serviceName + "]");
        }

        BusinessService businessService = null;
        try {
            ServiceEntity serviceEntity =
                    registryDao.getServiceByName(institutionName, serviceName);
            if (serviceEntity != null) {
                businessService = convert(serviceEntity);
                // Set blacklist status
                businessService.setActiveStatus(
                    getBlacklistStatus(
                        institutionName,
                            businessService.getName()
                    )
                );
                // Add institution name to service
                businessService.setInstitutionName(institutionName);
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving service", e);
        }

        return businessService;
    }

    @Override
    @Nullable
    public BusinessService getServiceByName(
            @NotNull Long institutionKey, @NotNull String serviceName) {

        if (serviceName.isEmpty()) {
            throw new RuntimeException(
                "Invalid service name [" + serviceName + "]");
        }

        InstitutionEntity institution =
                    registryDao.getInstitutionByKey(institutionKey);
        if (institution == null) {
            throw new RuntimeException(
                "Unable to retrieve institution with 'key [" +
                    institutionKey + "] because I could not find it");
        }

        BusinessService businessService = null;
        try {
            ServiceEntity serviceEntity =
                    registryDao.getServiceByName(institutionKey, serviceName);
            businessService = convert(serviceEntity);
            // Set blacklist status
            businessService.setActiveStatus(
                getBlacklistStatus(
                    institution.getName(),
                        businessService.getName()
                )
            );
            // Add institution name to service
            businessService.setInstitutionName(institution.getName());
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving service", e);
        }

        return businessService;
    }

    @Override
    @Nullable
    public BusinessService getServiceByKey(@NotNull Long serviceKey) {
        BusinessService businessService = null;
        try {
            ServiceEntity serviceEntity =
                    registryDao.getServiceByKey(serviceKey);
            if (serviceEntity != null) {
                // Retrieve institution name
                InstitutionEntity institutionEntity =
                        registryDao.getInstitutionByServiceKey(serviceEntity.getKey());
                String institutionName = institutionEntity.getName();

                businessService = convert(serviceEntity);
                // Set blacklist status
                businessService.setActiveStatus(
                        getBlacklistStatus(institutionName,
                                businessService.getName()
                        )
                );

                // Add institution name to service
                businessService.setInstitutionName(institutionName);
            } else {
                logger.error("Unable to find service with key [" + serviceKey + "]");
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving service", e);
        }

        return businessService;
    }

    @Override
    @Nullable
    public BusinessService getServiceWithBindingKey(@NotNull Long bindingKey) {
        BusinessService businessService = null;
        try {
            ServiceEntity serviceEntity =
                    registryDao.getServiceWithBindingKey(bindingKey);
            if (serviceEntity != null) {
                // Retrieve institution name
                InstitutionEntity institutionEntity =
                        registryDao.getInstitutionByServiceKey(serviceEntity.getKey());
                String institutionName = institutionEntity.getName();

                businessService = convert(serviceEntity);
                // Set blacklist status
                businessService.setActiveStatus(
                        getBlacklistStatus(institutionName,
                                businessService.getName()
                        )
                );

                // Add institution name to service
                businessService.setInstitutionName(institutionName);
            } else {
                logger.error("Unable to find service with BindingKey [" + bindingKey + "]");
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving service", e);
        }

        return businessService;
    }

    @Override
    @NotNull
    public List<BusinessService> getServices(@NotNull ActiveStatusEnum status) {
        logger.info("Retrieving services using activeStatus [" + status + "]");
        List<BusinessService> services = new ArrayList<BusinessService>();
        try {
            // Retrieve the institution list
            for (InstitutionEntity institutionEntity : registryDao.getInstitutions()) {

                // Filter institution using blacklist
                if (ActiveStatusEnum.BLACKLIST != getBlacklistStatus(
                        institutionEntity.getName(), null)) {

                    for (ServiceEntity serviceEntity : institutionEntity.getService()) {
                        // Filter service by Blacklist status
                        ActiveStatusEnum activeStatusEnum =
                                getBlacklistStatus(
                                    institutionEntity.getName(),
                                        serviceEntity.getName());
                        if (status == ActiveStatusEnum.UNSPECIFIED ||
                                status == activeStatusEnum) {
                            // Bindings
                            serviceEntity =
                                    registryDao.getBindingsForService(
                                            serviceEntity.getKey());
                            BusinessService businessService = convert(serviceEntity);
                            businessService.setInstitutionName(
                                    institutionEntity.getName());
                            // Set blacklist status
                            businessService.setActiveStatus(
                                activeStatusEnum
                            );
                            // Add service to services list
                            services.add(businessService);
                        } // End if - ActiveStatus
                    } // End for - services
                } // End if - ActiveStatus
            } // End for - institutions
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving services", e);
        }

        return services;
    }

    @Override
    @NotNull
    public List<BusinessService> getServicesByServiceType(
            @NotNull ServiceTypeEnum serviceType,
            @NotNull ActiveStatusEnum status) {

        List<BusinessService> services = new ArrayList<BusinessService>();
        try {
            // Retrieve the institution list
            List<InstitutionEntity> institutionEntities =
                    registryDao.getInstitutionsByServiceType(serviceType);

            for (InstitutionEntity institutionEntity : institutionEntities) {
                // Filter institution using blacklist
                if (status == ActiveStatusEnum.UNSPECIFIED ||
                        status == getBlacklistStatus(
                        institutionEntity.getName(), null)) {
                    for (ServiceEntity serviceEntity : institutionEntity.getService()) {
                        // Filter service by Blacklist status
                        ActiveStatusEnum activeStatusEnum =
                                getBlacklistStatus(
                                    institutionEntity.getName(),
                                        serviceEntity.getName());
                        if (status == ActiveStatusEnum.UNSPECIFIED ||
                                status == activeStatusEnum) {
                            // Retrieve the bindings for the service
                            serviceEntity =
                                registryDao.getBindingsForService(
                                    serviceEntity.getKey());
                            // Convert to transport object
                            BusinessService businessService = convert(serviceEntity);
                            // Add institution name
                            businessService.setInstitutionName(
                                    institutionEntity.getName());
                            // Set blacklist status
                            businessService.setActiveStatus(activeStatusEnum);
                            // Add service to services List
                            services.add(businessService);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving services", e);
        }

        return services;
    }

    @Override
    @NotNull
    public List<BusinessService> getServicesByServiceTypeAndBindingType(
            @NotNull ServiceTypeEnum serviceType,
            @NotNull BindingTypeEnum bindingType,
            @NotNull ActiveStatusEnum status) {

        List<BusinessService> services = new ArrayList<BusinessService>();
        try {
            // Retrieve the institutions list
            List<InstitutionEntity> institutionEntities =
                    registryDao.getInstitutionsByServiceType(serviceType);

            for (InstitutionEntity institutionEntity : institutionEntities) {
                // Filter institution using blacklist
                if (status == ActiveStatusEnum.UNSPECIFIED ||
                        status == getBlacklistStatus(
                                    institutionEntity.getName(), null)) {
                    // Filter service by Blacklist status
                    for (ServiceEntity serviceEntity : institutionEntity.getService()) {

                        ActiveStatusEnum activeStatusEnum =
                                getBlacklistStatus(
                                    institutionEntity.getName(),
                                        serviceEntity.getName());
                        if (status == ActiveStatusEnum.UNSPECIFIED ||
                                status == activeStatusEnum) {
                            // Retrieve the bindings for the service
                            serviceEntity =
                                registryDao.getBindingsForService(
                                    serviceEntity.getKey(), bindingType);
                            if (serviceEntity != null) {
                                // Convert to transport object
                                BusinessService businessService = convert(serviceEntity);
                                // Add institution name
                                businessService.setInstitutionName(institutionEntity.getName());
                                // Set blacklist status
                                businessService.setActiveStatus(activeStatusEnum);
                                // Add service to services List
                                services.add(businessService);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving services", e);
        }

        return services;
    }

    @Override
    @NotNull
    public RequestStatus addBinding(
            @NotNull Long serviceKey,
            @NotNull Binding binding) {

        RequestStatus status = RequestStatus.FAILURE;
        try {
            logger.debug("Adding binding with Type [" +
                    binding.getBindingType() + "]");
            registryDao.addBinding(serviceKey, convert(binding));
            status = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception persisting binding", e);
        }
        return status;
    }

    @Override
    @NotNull
    public RequestStatus updateBinding(@NotNull Binding binding) {
        if (binding.getKey() == null) {
            throw new RuntimeException(
                "Binding must have a valid binding key to update");
        }

        RequestStatus status = RequestStatus.FAILURE;
        try {
            registryDao.updateBinding(convert(binding));
            status = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception persisting binding", e);
        }
        return status;
    }

    @Override
    @NotNull
    public RequestStatus removeBinding(@NotNull Long bindingKey) {
        RequestStatus status = RequestStatus.FAILURE;
        try {
            registryDao.removeBinding(bindingKey);
            status = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception removing binding", e);
        }
        return status;
    }

    @Override
    @Nullable
    public List<Binding> getBindingsByAccessPoint(@NotNull String endPoint) {

        if (endPoint.isEmpty()) {
            throw new RuntimeException(
                "Invalid AccessPoint [" + endPoint + "]");
        }

        List<Binding> bindings = null;
        try {
            List<BindingEntity> bindingEntities =
                    registryDao.getBindingsByAccessPoint(endPoint);
            if (bindingEntities != null && !bindingEntities.isEmpty()) {
                bindings = new ArrayList<Binding>();
                for (BindingEntity bindingEntity : bindingEntities) {
                    bindings.add(convert(bindingEntity));
                }
            }
        } catch (Exception e) {
            logger.error(
                "Unexpected exception retrieving bindings by accessPoint [" +
                    endPoint + "]", e);
        }

        return bindings;
    }

    @Override
    @Nullable
    public Binding getBindingByKey(@NotNull Long bindingKey) {
        Binding binding = null;
        try {
            BindingEntity bindingEntity =
                    registryDao.getBindingByKey(bindingKey);
            if (bindingEntity != null) {
                binding = convert(bindingEntity);
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving binding", e);
        }

        return binding;
    }

    @Override
    @NotNull
    public List<BindingTypeEnum> getBindingTypes() {
        List<BindingTypeEnum> bindingTypeEnums = new ArrayList<BindingTypeEnum>();
        try {
            List<BindingTypeEntity> bindingTypeEntities =
                    registryDao.getBindingTypes();
            for (BindingTypeEntity bindingTypeEntity : bindingTypeEntities) {
                BindingTypeEnum bindingTypeEnum =
                        BindingTypeEnum.fromValue(bindingTypeEntity.getValue());
                bindingTypeEnums.add(bindingTypeEnum);
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving bindingTypes", e);
        }

        return bindingTypeEnums;
    }

    @Override
    @NotNull
    public List<ServiceTypeEnum> getServiceTypes() {
        List<ServiceTypeEnum> serviceTypeEnums = new ArrayList<ServiceTypeEnum>();
        try {
            List<ServiceTypeEntity> serviceTypeEntities =
                    registryDao.getServiceTypes();
            for (ServiceTypeEntity serviceTypeEntity : serviceTypeEntities) {
                ServiceTypeEnum serviceTypeEnum =
                        ServiceTypeEnum.fromValue(serviceTypeEntity.getValue());
                serviceTypeEnums.add(serviceTypeEnum);
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving serviceTypes", e);
        }

        return serviceTypeEnums;
    }

    @Nullable
    private ServiceEntity getServiceEntityByName(
            @NotNull Long institutionKey, @NotNull String name) {

        if (name.isEmpty()) {
            throw new RuntimeException(
                "Invalid Service name [" + name + "]");
        }

        ServiceEntity serviceEntity = null;
        try {
            serviceEntity = registryDao.getServiceByName(institutionKey, name);
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving service", e);
        }

        return serviceEntity;
    }

    private void setBlacklistStatus(
            @NotNull String institutionName,
            @Nullable String serviceName,
            @NotNull ActiveStatusEnum activeStatus) {

        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                "Invalid Institution name [" + institutionName + "]");
        }

        if (blacklist == null) {
            logger.warn("Blacklist service is not properly configured");
            return;
        }

        BlacklistItem blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institutionName);
        if (serviceName != null) {
            blacklistItem.setService(serviceName);
        }
        // Blacklist status
        blacklistItem.setStatus(activeStatus);

        blacklist.addBlacklistItem(blacklistItem);
    }

    @NotNull
    private ActiveStatusEnum getBlacklistStatus(
            @NotNull String institution, @Nullable String service) {

        if (institution.isEmpty()) {
            throw new RuntimeException(
                "Invalid Institution name [" + institution + "]");
        }

        BlacklistItem blacklistItem =
                blacklist.getBlacklistItem(institution, service);

        // If the item does not exist add it.
        if (blacklistItem == null) {
            logger.info("Adding [" + institution + ":" + service +
                    "] to blacklist");

            blacklistItem = new BlacklistItem();
            blacklistItem.setInstitution(institution);
            if (service != null) {
                blacklistItem.setService(service);
            }
            blacklist.addBlacklistItem(blacklistItem);
        }

        return blacklistItem.getStatus();
    }

    @NotNull
    private RequestStatus removeBlacklistItem(
            @NotNull String institutionName, @Nullable String serviceName) {

        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                "Invalid Institution name [" + institutionName + "]");
        }

        RequestStatus status = RequestStatus.FAILURE;
        try {
            status = blacklist.removeBlacklistItem(institutionName, serviceName);
        } catch (Exception e) {
            logger.error("Unable to remove blacklist item [" +
                institutionName + "]", e);
        }

        return status;
    }

    @NotNull
    private BindingEntity convert(@NotNull Binding binding) {
        BindingEntity bindingEntity = new BindingEntity();
        bindingEntity.setKey(binding.getKey());
        bindingEntity.setDescription(binding.getDescription());
        bindingEntity.setAccessPoint(binding.getAccessPoint());
        // BindingType
        if (binding.getBindingType() != null) {
            BindingTypeEntity bindingTypeEntity = new BindingTypeEntity();
            logger.debug("Setting BindingType [" + binding.getBindingType() + "]");
            bindingTypeEntity.setValue(binding.getBindingType());
            bindingEntity.setBindingType(bindingTypeEntity);
        } else {
            throw new RuntimeException(
                "Error converting BindingType to persistence entity with 'Null' value");
        }

        return bindingEntity;
    }

    @NotNull
    private Binding convert(@NotNull BindingEntity bindingEntity) {
        Binding binding = new Binding();
        binding.setKey(bindingEntity.getKey());
        binding.setDescription(bindingEntity.getDescription());
        binding.setAccessPoint(bindingEntity.getAccessPoint());
        // BindingType
        BindingTypeEnum bindingTypeEnum =
            BindingTypeEnum.fromValue(
                    bindingEntity.getBindingType().getValue());
        binding.setBindingType(bindingTypeEnum);

        return binding;
    }

    @NotNull
    private ServiceEntity convert(@NotNull BusinessService businessService) {
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setKey(businessService.getKey());
        serviceEntity.setName(businessService.getName());
        serviceEntity.setDescription(businessService.getDescription());
        // ServiceType
        ServiceTypeEntity serviceTypeEntity = new ServiceTypeEntity();
        serviceTypeEntity.setValue(businessService.getServiceType());
        serviceEntity.setServiceType(serviceTypeEntity);
        // Bindings
        for (Binding binding : businessService.getBinding()) {
            BindingEntity bindingEntity = convert(binding);
            // Add binding to service
            serviceEntity.getBinding().add(bindingEntity);
        }

        return serviceEntity;
    }

    @NotNull
    private BusinessService convert(@NotNull ServiceEntity serviceEntity) {
        BusinessService businessService = new BusinessService();
        businessService.setKey(serviceEntity.getKey());
        businessService.setName(serviceEntity.getName());
        businessService.setDescription(serviceEntity.getDescription());

        // ServiceType
        ServiceTypeEnum serviceTypeEnum =
                ServiceTypeEnum.fromValue(
                        serviceEntity.getServiceType().getValue());
        businessService.setServiceType(serviceTypeEnum);

        // Bindings
        for (BindingEntity bindingEntity : serviceEntity.getBinding()) {
            // Add binding to service
            businessService.getBinding().add(convert(bindingEntity));
        }

        return businessService;
    }

    @NotNull
    private InstitutionEntity convert(@NotNull Institution institution) {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setKey(institution.getKey());
        institutionEntity.setName(institution.getName());
        institutionEntity.setDescription(institution.getDescription());
        // Contact Info
        Contact contact = institution.getContactInfo();
        if (contact != null) {
            ContactEntity contactEntity = new ContactEntity();
            contactEntity.setKey(contact.getKey());
            contactEntity.setPersonName(contact.getPersonName());
            contactEntity.setDescription(contact.getDescription());
            contactEntity.setAddress(contact.getAddress());
            contactEntity.setEmail(contact.getEmail());
            contactEntity.setPhone(contact.getPhone());
            institutionEntity.setContactInfo(contactEntity);
        }
        // Services
        for (BusinessService businessService : institution.getService()) {
            // Add service to institution
            institutionEntity.getService().add(convert(businessService));
        }

        return institutionEntity;
    }

    @NotNull
    private Institution convertShallow(@NotNull InstitutionEntity institutionEntity) {
        Institution institution = new Institution();
        institution.setKey(institutionEntity.getKey());
        institution.setName(institutionEntity.getName());
        institution.setDescription(institutionEntity.getDescription());
        // Contact Info
        ContactEntity contactEntity = institutionEntity.getContactInfo();
        if (contactEntity != null) {
            Contact contact = new Contact();
            contact.setKey(contactEntity.getKey());
            contact.setPersonName(contactEntity.getPersonName());
            contact.setDescription(contactEntity.getDescription());
            contact.setAddress(contactEntity.getAddress());
            contact.setEmail(contactEntity.getEmail());
            contact.setPhone(contactEntity.getPhone());
            institution.setContactInfo(contact);
        }

        return institution;
    }

    @Nullable
    private InstitutionEntity getInstitutionEntityByName(@NotNull String name) {
        InstitutionEntity institution = null;
        try {
            institution = registryDao.getInstitutionByName(name);
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving institution", e);
        }

        return institution;
    }

    // Spring dependency injection
    public void setRegistryDao(RegistryDao registryDao) {
        this.registryDao = registryDao;
    }
    // Spring dependency injection
    public void setBlacklistModel(BlacklistModel blacklist) {
        this.blacklist = blacklist;
    }

}
