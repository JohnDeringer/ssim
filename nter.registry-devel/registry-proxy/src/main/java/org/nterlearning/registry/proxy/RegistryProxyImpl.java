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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.registry.blacklist.client.BlacklistItem;

import org.nterlearning.registry.client.*;
import org.nterlearning.registry.client.ActiveStatusEnum;
import org.nterlearning.registry.client.Binding;
import org.nterlearning.registry.client.BindingTypeEnum;
import org.nterlearning.registry.client.BusinessService;
import org.nterlearning.registry.client.Institution;
import org.nterlearning.registry.client.ServiceTypeEnum;

import java.util.*;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/20/12
 */
public class RegistryProxyImpl implements RegistryProxy {

    private static Registry globalRegistry;
    private static Registry localRegistry;
    private static Blacklist blacklistService;

    private String localRegistryWsdlLocation = null;
    private String globalRegistryWsdlLocation = null;
    private String blacklistWsdlLocation = null;
    private String registryUser = null;
    private String registryPass = null;
    private String defaultActiveStatus = null;

    private ActiveStatusEnum defaultBlacklistStatus;

    private static final String NULL_VALUE = null;

    private static Log log = LogFactoryUtil.getLog(RegistryProxyImpl.class);

    // Constructor is used for testing
    public RegistryProxyImpl(String localRegistryWsdlLocation,
                             String globalRegistryWsdlLocation,
                             String blacklistWsdlLocation,
                             String registryUser,
                             String registryPass,
                             String defaultActiveStatus) {

        this.localRegistryWsdlLocation = localRegistryWsdlLocation;
        this.globalRegistryWsdlLocation = globalRegistryWsdlLocation;
        this.blacklistWsdlLocation = blacklistWsdlLocation;

        this.registryUser = registryUser;
        this.registryPass = registryPass;
        this.defaultActiveStatus = defaultActiveStatus;

        init();
    }

    public RegistryProxyImpl() {
        init();
    }

    private void init() {
        // Local Registry wsdl
        if (localRegistryWsdlLocation == null) {
            try {
                localRegistryWsdlLocation =
                        PropsUtil.get(PropKeyConst.LOCAL_REGISTRY_URL);
            } catch (Exception e) {
                log.warn("Error retrieving local registry URL from Liferay [" +
                    PropKeyConst.LOCAL_REGISTRY_URL + ']');
            }
        }

        // Global Registry wsdl
        if (globalRegistryWsdlLocation == null) {
            try {
                globalRegistryWsdlLocation =
                        PropsUtil.get(PropKeyConst.GLOBAL_REGISTRY_URL);
            } catch (Exception e) {
                log.warn("Error retrieving global registry URL from Liferay [" +
                        PropKeyConst.GLOBAL_REGISTRY_URL + "]");
            }
        }

        // Blacklist wsdl
        if (blacklistWsdlLocation == null) {
            try {
                blacklistWsdlLocation =
                        PropsUtil.get(PropKeyConst.BLACKLIST_URL);
            } catch (Exception e) {
                log.warn("Error retrieving blacklist URL from Liferay [" +
                        PropKeyConst.BLACKLIST_URL + "]");
            }
        }

        // Registry user
        if (registryUser == null) {
            try {
                registryUser = PropsUtil.get(PropKeyConst.REGISTRY_ADMIN_USER);
            } catch (Exception e) {
                log.warn("Error retrieving registry user from Liferay [" +
                        PropKeyConst.REGISTRY_ADMIN_USER + "]");
            }
        }

        // Registry password
        if (registryPass == null) {
            try {
                registryPass = PropsUtil.get(PropKeyConst.REGISTRY_ADMIN_PASS);
            } catch (Exception e) {
                log.warn("Error retrieving registry password from Liferay [" +
                        PropKeyConst.REGISTRY_ADMIN_PASS + "]");
            }
        }

        // Retrieve Global Registry
        if (globalRegistryWsdlLocation != null && !globalRegistryWsdlLocation.isEmpty()) {
            globalRegistry =
                new RegistryImpl(registryUser, registryPass, globalRegistryWsdlLocation);
        } else {
            log.error("Unable to contact Global Registry with wsdl [" +
                globalRegistryWsdlLocation + "]");
        }


        // Retrieve Local Registry
        if (localRegistryWsdlLocation != null && !localRegistryWsdlLocation.isEmpty()) {
            localRegistry =
                new RegistryImpl(registryUser, registryPass, localRegistryWsdlLocation);
        } else {
            log.error("Unable to contact Local Registry with wsdl [" +
                globalRegistryWsdlLocation + "]");
        }

        // Retrieve blacklist
        if (blacklistWsdlLocation != null && !blacklistWsdlLocation.isEmpty()) {
            blacklistService =
                new BlacklistImpl(registryUser, registryPass, blacklistWsdlLocation);
        } else {
            log.error("Unable to contact Blacklist API with wsdl [" +
                blacklistWsdlLocation + "]");
        }

        // Set the default blacklist status
        if (defaultActiveStatus == null) {
            try {
                defaultActiveStatus =
                        PropsUtil.get(PropKeyConst.BLACKLIST_DEFAULT_STATUS);
            } catch (Exception e) {
                log.warn("Error setting blacklist default status setting [" +
                        PropKeyConst.BLACKLIST_DEFAULT_STATUS + "]", e);
            }
        }

        if (defaultActiveStatus == null || defaultActiveStatus.isEmpty()) {
                defaultBlacklistStatus = ActiveStatusEnum.UNSPECIFIED;
        } else {
            defaultBlacklistStatus =
                getActiveStatusFromValue(defaultActiveStatus);
        }
        log.info("Setting default blacklist status to [" + defaultActiveStatus + "]");
        setDefaultBlacklistStatus(defaultBlacklistStatus);
    }

    @Override
    public void createInstitution(@NotNull InstitutionBean institution) {
        if (institution.getRegistryInstance() == RegistryInstance.LOCAL) {
            if (localRegistry != null) {
                try {
                    localRegistry.createInstitution(institution);
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.info("local registry is not properly configured");
            }
        } else {
            log.error("The registry client proxy can only create an institution in the Local Registry");
        }
    }

    @Override
    public void updateInstitution(@NotNull InstitutionBean institution) {
        log.info("Updating Institution [" + institution.getName() +
                "] RegistryInstance [" + institution.getRegistryInstance() +
                "] Description [" + institution.getDescription() + "]");

        if (institution.getRegistryInstance() == RegistryInstance.LOCAL) {
            if (localRegistry != null) {
                try {
                    localRegistry.updateInstitution(institution);
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.info("local registry is not properly configured");
            }
        } else if (institution.getRegistryInstance() == RegistryInstance.GLOBAL) {
            // Global registry - update blacklist
            ActiveStatusEnum status = institution.getActiveStatus();
            setBlacklistStatus(
                    institution.getName(),
                    NULL_VALUE,
                    status);
        } else {
            throw new RuntimeException("Unrecognized RegistryInstance [" +
                    institution.getRegistryInstance() + "]");
        }
    }

    @Override
    public void removeInstitution(@NotNull Long institutionKey) {
        if (institutionKey < 1) {
            throw new RuntimeException(
                    "Unable to remove institution with key [" + institutionKey + ']');
        }

        InstitutionBean institution = getInstitutionByKey(institutionKey);
        if (institution == null) {
            log.warn("Unable to remove institution with key [" +
                    institutionKey + "] because I could not find it");
            return;
        }

        if (institution.getRegistryInstance() == RegistryInstance.LOCAL) {
            if (localRegistry != null) {
                try {
                    localRegistry.removeInstitution(institutionKey);
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.info("local registry is not properly configured");
            }
        } else if (institution.getRegistryInstance() == RegistryInstance.GLOBAL) {
            // remove from blacklist only
            removeBlacklistItem(institution.getName(), NULL_VALUE);
        } else {
            throw new RuntimeException(
                "Unrecognized RegistryInstance [" + institution.getRegistryInstance() + ']');
        }
    }

    @Override
    @Nullable
    public InstitutionBean getInstitutionByName(@NotNull String institutionName) {
        InstitutionBean institution;

        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Institution by name [" + institutionName + "]");
        }

         // Search first locally
        institution = getLocalInstitutionByName(institutionName);

        // Not found, search Global registry
        if (institution == null) {
            institution = getGlobalInstitutionByName(institutionName);
        }

        return institution;
    }

    @Nullable
    private InstitutionBean getGlobalInstitutionByName(
            @NotNull String institutionName) {
        InstitutionBean institutionBean = null;

        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Institution by name [" + institutionName + "]");
        }

        if (globalRegistry != null) {
            Institution institution =
                    globalRegistry.getInstitutionByName(institutionName);

            if (institution != null) {
                // Filter services
                institutionBean =
                        filterServicesByBlacklist(institution,
                                institution.getActiveStatus());

                // Evaluate / update blacklist status
                institutionBean =
                    evaluateBlacklistStatus(institutionBean);
            } else {
                log.warn("Unable to find institution by name [" +
                        institutionName + "]");
            }

        } else {
            log.info("Global registry is not configured");
        }

        return institutionBean;
    }

    @Nullable
    private InstitutionBean getLocalInstitutionByName(@NotNull String institutionName) {
        InstitutionBean institutionBean = null;

        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Institution by name [" + institutionName + "]");
        }

        if (localRegistry != null) {
            Institution institution =
                    localRegistry.getInstitutionByName(institutionName);

            if (institution != null) {
                institutionBean =
                        new InstitutionBean(institution, RegistryInstance.LOCAL);
            }
        } else {
            log.info("Local registry is not configured");
        }

        return institutionBean;
    }

    @Override
    @Nullable
    public InstitutionBean getInstitutionFromAccessPoint(@NotNull String endpoint) {
        InstitutionBean institution;

        if (endpoint.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Institution with endpoint [" + endpoint + "]");
        }

         // Search first locally
        institution = getLocalInstitutionFromEndPoint(endpoint);

        // Not found, search Global registry
        if (institution == null) {
            institution = getGlobalInstitutionFromEndPoint(endpoint);
        }

        return institution;
    }

    @Nullable
    private InstitutionBean getGlobalInstitutionFromEndPoint(
            @NotNull String endpoint) {
        InstitutionBean institutionBean = null;

        if (endpoint.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Institution with endpoint [" + endpoint + "]");
        }

        if (globalRegistry != null) {
            Institution institution =
                    globalRegistry.getInstitutionByAccessPoint(endpoint);

            if (institution != null) {
                institutionBean =
                        new InstitutionBean(institution, RegistryInstance.GLOBAL);
                // Get blacklist status
                ActiveStatusEnum activeStatus =
                    getBlacklistStatus(institution.getName(), NULL_VALUE);
                if (activeStatus != ActiveStatusEnum.UNSPECIFIED) {
                    institutionBean.setActiveStatus(activeStatus);
                }

                // Retrieve the services and set blacklist status
                for (BusinessService service : institution.getService()) {
                    // Get the blacklist status
                    ActiveStatusEnum activeStatusEnum =
                        getBlacklistStatus(service.getInstitutionName(),
                                service.getName());
                    if (activeStatusEnum != ActiveStatusEnum.UNSPECIFIED) {
                        service.setActiveStatus(activeStatusEnum);
                    }
                }
            }
        } else {
            log.info("Global registry is not configured");
        }

        return institutionBean;
    }

    @Nullable
    private InstitutionBean getLocalInstitutionFromEndPoint(
            @NotNull String endpoint) {
        InstitutionBean institutionBean = null;

        if (endpoint.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve Institution with endpoint [" + endpoint + "]");
        }

        if (localRegistry != null) {
            Institution institution =
                    localRegistry.getInstitutionByAccessPoint(endpoint);

            if (institution != null) {
                institutionBean = new InstitutionBean(institution, RegistryInstance.LOCAL);
            }
        } else {
            log.info("Local registry is not configured");
        }

        return institutionBean;
    }

    @Override
    @NotNull
    public List<InstitutionBean> getInstitutions(
            @NotNull ActiveStatusEnum localStatus,
            @NotNull ActiveStatusEnum globalStatus,
            @NotNull RegistryInstance registryInstance) {
        List<InstitutionBean> institutions = new ArrayList<InstitutionBean>();

        // Global institutions
        if (registryInstance == RegistryInstance.GLOBAL) {
            institutions.addAll(
                    getGlobalInstitutions(localStatus, globalStatus)
            );
        } else if (registryInstance == RegistryInstance.LOCAL) {
            // Local institutions
            institutions.addAll(
                    getLocalInstitutions(localStatus)
            );
        } else {
            throw new RuntimeException(
                    "Unrecognized RegistryInstance [" + registryInstance + ']');
        }

        return institutions;
    }

    @NotNull
    private List<InstitutionBean> getGlobalInstitutions(
            @NotNull ActiveStatusEnum blacklistStatus,
            @NotNull ActiveStatusEnum globalStatus) {
        List<InstitutionBean> institutionBeans = new ArrayList<InstitutionBean>();

        if (globalRegistry != null) {
            // Retrieve institutions from global registry
            List<Institution> institutions =
                    globalRegistry.getInstitutions(globalStatus);

            // Evaluate / update blacklist status
            institutions =
                    evaluateBlacklistStatus(institutions);

            institutionBeans =
                    filterByBlacklist(
                        institutions, blacklistStatus);

        } else {
            log.info("Global registry is not configured");
        }

        return institutionBeans;
    }

    @NotNull
    private List<InstitutionBean> filterByBlacklist(
            @NotNull List<Institution> institutions,
            @NotNull ActiveStatusEnum blacklistStatus) {

        List<InstitutionBean> filteredInstitutions = new ArrayList<InstitutionBean>();

        for (Institution institution : institutions) {

            if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED ||
                    blacklistStatus == institution.getActiveStatus()) {
                // Create institutionBean
                InstitutionBean institutionBean =
                        new InstitutionBean(institution, RegistryInstance.GLOBAL);

                // Services
                if (!institutionBean.getService().isEmpty()) {
                    for (BusinessService service : institutionBean.getService()) {

                        if (blacklistStatus != ActiveStatusEnum.UNSPECIFIED &&
                                blacklistStatus != service.getActiveStatus()) {
                            // Remove service from collection
                            institutionBean.getService().remove(service);
                            // Smart JRE - this is no longer required?, but just in case.
                            service = null;
                        }
                    }
                }
                // Add institutionBean to filtered collection
                filteredInstitutions.add(institutionBean);
            } // End if - blacklistStatus

        } // End for - Institution

        return filteredInstitutions;
    }

    @NotNull
    private InstitutionBean filterServicesByBlacklist(
            @NotNull Institution institution, @NotNull ActiveStatusEnum parentStatus) {

        // Services
        List<BusinessService> services = new ArrayList<BusinessService>();
        if (!institution.getService().isEmpty()) {
            for (BusinessService service : institution.getService()) {
                if (defaultBlacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                    if (service.getActiveStatus() == ActiveStatusEnum.ACTIVE ||
                            service.getActiveStatus() == ActiveStatusEnum.INACTIVE) {
                        services.add(service);
                    }
                } else {
                    if (service.getActiveStatus() == ActiveStatusEnum.ACTIVE) {
                        services.add(service);
                    }
                }
            }
        }

        return new InstitutionBean(institution, services, RegistryInstance.GLOBAL);
    }

    @NotNull
    private List<ServiceBean> filterServiceByBlacklist(
            @NotNull List<BusinessService> services, @NotNull ActiveStatusEnum blacklistStatus) {

        List<ServiceBean> filteredServices = new ArrayList<ServiceBean>();

        for (BusinessService service : services) {

            if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED ||
                    blacklistStatus == service.getActiveStatus()) {
                // Create serviceBean
                ServiceBean serviceBean =
                        new ServiceBean(service, RegistryInstance.GLOBAL);

                // Add serviceBean to filtered collection
                filteredServices.add(serviceBean);
            } // End if - blacklistStatus

        } // End for - service

        return filteredServices;
    }

    @NotNull
    private InstitutionBean evaluateBlacklistStatus(
            @NotNull InstitutionBean institution) {
        ActiveStatusEnum blacklistStatus =
            getBlacklistStatus(institution.getName(), NULL_VALUE);

        switch (defaultBlacklistStatus) {
            case UNSPECIFIED:
                // If blacklist entry does not exist set blacklist status to global status.
                if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                    blacklistStatus = institution.getActiveStatus();

                    // Update blacklist
                    setBlacklistStatus(
                        institution.getName(), NULL_VALUE, blacklistStatus);
                }

                break;
            default:
                // If blacklist entry does not exist set blacklist status to default status.
                if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                    blacklistStatus = defaultBlacklistStatus;
                    // Update blacklist
                    setBlacklistStatus(
                        institution.getName(), NULL_VALUE, blacklistStatus);
                }

                break;
        } // End switch

        // Update institution status
        institution.setActiveStatus(blacklistStatus);

        // Services
        for (BusinessService service : institution.getService()) {
            // Retrieve blacklist status
            blacklistStatus =
                getBlacklistStatus(
                        service.getInstitutionName(),
                        service.getName());
            switch (defaultBlacklistStatus) {
                case UNSPECIFIED:
                    // If blacklist entry does not exist set blacklist status to global status.
                    if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                        blacklistStatus = service.getActiveStatus();
                        // Update blacklist
                        setBlacklistStatus(
                            service.getInstitutionName(),
                            service.getName(),
                            blacklistStatus
                        );
                    }
                    break;
                default:
                    // If blacklist entry does not exist set blacklist status to default status.
                    if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                        blacklistStatus = defaultBlacklistStatus;
                        // Update blacklist
                        setBlacklistStatus(
                            service.getInstitutionName(),
                            service.getName(),
                            blacklistStatus
                        );
                    }
                    break;
            } // End switch

            // Update service status
            service.setActiveStatus(blacklistStatus);

        } // End for - Services

        return institution;
    }

    @NotNull
    private List<Institution> evaluateBlacklistStatus(
            @NotNull List<Institution> institutions) {

        for (Institution institution : institutions) {
            ActiveStatusEnum blacklistStatus =
                getBlacklistStatus(institution.getName(), NULL_VALUE);

            switch (defaultBlacklistStatus) {
                case UNSPECIFIED:
                    // If blacklist entry does not exist set blacklist status to global status.
                    if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                        blacklistStatus = institution.getActiveStatus();

                        // Update blacklist
                        setBlacklistStatus(
                            institution.getName(), NULL_VALUE, blacklistStatus);
                    }

                    break;
                default:
                    // If blacklist entry does not exist set blacklist status to default status.
                    if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                        blacklistStatus = defaultBlacklistStatus;
                        // Update blacklist
                        setBlacklistStatus(
                            institution.getName(), NULL_VALUE, blacklistStatus);
                    }

                    break;
            } // End switch

            // Update institution status
            institution.setActiveStatus(blacklistStatus);

            // Services
            for (BusinessService service : institution.getService()) {
                // Retrieve blacklist status
                blacklistStatus =
                    getBlacklistStatus(
                            service.getInstitutionName(),
                            service.getName());

                switch (defaultBlacklistStatus) {
                    case UNSPECIFIED:
                        // If blacklist entry does not exist set blacklist status to global status.
                        if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                            blacklistStatus = service.getActiveStatus();
                            // Update blacklist
                            setBlacklistStatus(
                                service.getInstitutionName(),
                                service.getName(),
                                blacklistStatus
                            );
                        }
                        break;
                    default:
                        // If blacklist entry does not exist set blacklist status to default status.
                        if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                            blacklistStatus = defaultBlacklistStatus;
                            // Update blacklist
                            setBlacklistStatus(
                                service.getInstitutionName(),
                                service.getName(),
                                blacklistStatus
                            );
                        }
                        break;
                } // End switch

                // Update service status
                service.setActiveStatus(blacklistStatus);

            } // End for - Services
        } // End for - Institutions

        return institutions;
    }

    @NotNull
    private List<BusinessService> evaluateServiceBlacklistStatus(
            @NotNull List<BusinessService> services) {

        // Services
        for (BusinessService service : services) {
            // Retrieve blacklist status
            ActiveStatusEnum blacklistStatus =
                getBlacklistStatus(
                        service.getInstitutionName(),
                        service.getName());

            switch (defaultBlacklistStatus) {
                case UNSPECIFIED:
                    // If blacklist entry does not exist set blacklist status to global status.
                    if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                        blacklistStatus = service.getActiveStatus();
                        // Update blacklist
                        setBlacklistStatus(
                            service.getInstitutionName(),
                            service.getName(),
                            blacklistStatus
                        );
                    }
                    break;
                default:
                    // If blacklist entry does not exist set blacklist status to default status.
                    if (blacklistStatus == ActiveStatusEnum.UNSPECIFIED) {
                        blacklistStatus = defaultBlacklistStatus;
                        // Update blacklist
                        setBlacklistStatus(
                            service.getInstitutionName(),
                            service.getName(),
                            blacklistStatus
                        );
                    }
                    break;
            } // End switch

            // Update service status
            service.setActiveStatus(blacklistStatus);

        } // End for - Services

        return services;
    }

    @NotNull
    private List<InstitutionBean> getLocalInstitutions(
            @NotNull ActiveStatusEnum activeStatus) {
        List<InstitutionBean> institutionBeans = new ArrayList<InstitutionBean>();

        if (localRegistry != null) {
            // Filter by Blacklist status
            List<Institution> institutions =
                    localRegistry.getInstitutions(activeStatus);

            for (Institution institution : institutions) {
                InstitutionBean institutionBean =
                        new InstitutionBean(institution, RegistryInstance.LOCAL);
                // Add to bean list
                institutionBeans.add(institutionBean);
            }
        } else {
            log.info("Local registry is not configured");
        }

        return institutionBeans;
    }

    @Override
    public void createService(
            @NotNull Long institutionKey, @NotNull ServiceBean service) {

        if (institutionKey < 1) {
            throw new RuntimeException(
                    "Unable to create service with institutionKey [" + institutionKey + "]");
        }

        log.info("Creating service [" + service.getName() +
                "] with RegistryInstance [" + service.getRegistryInstance() + "]");

        if (service.getRegistryInstance() == RegistryInstance.LOCAL) {
            if (localRegistry != null) {
                try {
                     localRegistry.addService(institutionKey, service);
                } catch (Exception e) {
                    log.error("Unexpected error creating service", e);
                }
            } else {
                log.info("local registry is not properly configured");
            }
        } else {
            log.error("The registry client proxy can only create a service in the Local Registry");
        }
    }

    @Override
    public void updateService(@NotNull ServiceBean service) {
         if (service.getRegistryInstance() == RegistryInstance.LOCAL) {
            if (localRegistry != null) {
                try {
                    log.info("Updating service [" + service.getName() + "]");

                    localRegistry.updateService(service);
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.info("local registry is not properly configured");
            }
        } else if (service.getRegistryInstance() == RegistryInstance.GLOBAL) {
            // Global registry - update blacklist
            ActiveStatusEnum status = service.getActiveStatus();
            setBlacklistStatus(
                    service.getInstitutionName(),
                    service.getName(),
                    status);
        } else {
            throw new RuntimeException("Unrecognized RegistryInstance [" +
                    service.getRegistryInstance() + "]");
        }
    }

    @Override
    public void removeService(@NotNull Long serviceKey) {
        if (serviceKey < 1) {
            throw new RuntimeException(
                    "Unable to remove service with key [" + serviceKey + "]");
        }

        ServiceBean service = getServiceByKey(serviceKey);
        if (service == null) {
            log.warn("Unable to remove service with key [" +
                    serviceKey + "] because I could not find it");
        } else {
            if (service.getRegistryInstance() == RegistryInstance.LOCAL) {
                if (localRegistry != null) {
                    try {
                        localRegistry.removeService(serviceKey);
                    } catch (Exception e) {
                        log.error(e);
                    }
                } else {
                    log.warn("local registry is not properly configured");
                }
            } else if (service.getRegistryInstance() == RegistryInstance.GLOBAL) {
                // remove from blacklist only
                removeBlacklistItem(service.getInstitutionName(), service.getName());
            } else {
                throw new RuntimeException(
                    "Unrecognized RegistryInstance [" + service.getRegistryInstance() + ']');
            }
        }
    }

    @Override
    @Nullable
    public ServiceBean getServiceByName(
            @NotNull String institutionName, @NotNull String serviceName) {
        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                    "Unable to retrieve service with institutionName [" + institutionName + "]");
        }
        if (serviceName.isEmpty()) {
            throw new RuntimeException(
                    "Unable to retrieve service with serviceName [" + serviceName + "]");
        }

        ServiceBean service;

        // First search the local registry
        service = getLocalServiceByName(institutionName, serviceName);

        // Search the global registry
        if (service == null) {
            service = getGlobalServiceByName(institutionName, serviceName);
        }

        return service;
    }

    @Nullable
    private ServiceBean getGlobalServiceByName(
            @NotNull String institutionName, @NotNull String serviceName) {
        ServiceBean serviceBean = null;

        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                    "Unable to retrieve service with institutionName [" + institutionName + "]");
        }
        if (serviceName.isEmpty()) {
            throw new RuntimeException(
                    "Unable to retrieve service with serviceName [" + serviceName + "]");
        }

        if (globalRegistry != null) {
            BusinessService service =
                    globalRegistry.getServiceByName(institutionName, serviceName);

            if (service != null) {
                serviceBean = new ServiceBean(service, RegistryInstance.GLOBAL);
                // Get blacklist status
                ActiveStatusEnum activeStatus =
                    getBlacklistStatus(institutionName, serviceName);
                if (activeStatus != ActiveStatusEnum.UNSPECIFIED) {
                    serviceBean.setActiveStatus(activeStatus);
                }
            }
        } else {
            log.info("Global registry not configured");
        }

        return serviceBean;
    }

    @Nullable
    private ServiceBean getLocalServiceByName(
            @NotNull String institutionName, @NotNull String serviceName) {
        ServiceBean serviceBean = null;

        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                    "Unable to retrieve service with institutionName [" + institutionName + "]");
        }
        if (serviceName.isEmpty()) {
            throw new RuntimeException(
                    "Unable to retrieve service with serviceName [" + serviceName + "]");
        }

        if (localRegistry != null) {
            BusinessService service =
                    localRegistry.getServiceByName(institutionName, serviceName);
            if (service != null) {
                serviceBean = new ServiceBean(service, RegistryInstance.LOCAL);
            }
        } else {
            log.info("Local registry not configured");
        }

        return serviceBean;
    }

    @Override
    @NotNull
    public List<ServiceBean> getServices(
            @NotNull ActiveStatusEnum localStatus,
            @NotNull ActiveStatusEnum globalStatus,
            @NotNull RegistryInstance registryInstance) {
        List<ServiceBean> services = new ArrayList<ServiceBean>();

        if (registryInstance == RegistryInstance.GLOBAL) {
            // Global services
            services.addAll(
                    getGlobalServices(localStatus, globalStatus)
            );
        } else if (registryInstance == RegistryInstance.LOCAL) {
            // Local services
            services.addAll(
                    getLocalServices(localStatus)
            );
        } else {
            throw new RuntimeException(
                    "Unrecognized RegistryInstance [" + registryInstance + "]");
        }

        return services;
    }

    @NotNull
    private List<ServiceBean> getGlobalServices(
            @NotNull ActiveStatusEnum blacklistStatus,
            @NotNull ActiveStatusEnum globalStatus) {

        List<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();

        if (globalRegistry != null) {
            log.info("Retrieving global services using ActiveStatus [" + globalStatus + "]");

            List<BusinessService> services =
                    globalRegistry.getServices(globalStatus);

            serviceBeans =
                    filterServiceByBlacklist(
                        services, blacklistStatus);
        }

        return serviceBeans;
    }

    @NotNull
    private List<ServiceBean> getLocalServices(@NotNull ActiveStatusEnum activeStatus) {
        List<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();

       if (localRegistry != null) {
            List<BusinessService> services =
                        localRegistry.getServices(activeStatus);

           for (BusinessService service : services) {
                ServiceBean serviceBean =
                        new ServiceBean(service, RegistryInstance.LOCAL);
                // Add serviceBean to collection
                serviceBeans.add(serviceBean);
            }
        } else {
            log.info("Local registry is not configured");
        }

        return serviceBeans;
    }

    @Override
    @NotNull
    public List<ServiceBean> getServicesByServiceType(
            @NotNull ServiceTypeEnum serviceType,
            @NotNull ActiveStatusEnum localStatus,
            @NotNull ActiveStatusEnum globalStatus) {
        List<ServiceBean> services = new ArrayList<ServiceBean>();

        // Global services
        services.addAll(
                getGlobalServicesByServiceType(serviceType, localStatus, globalStatus)
        );

        // Local services
        services.addAll(
                getLocalServicesByServiceType(serviceType, localStatus)
        );

        return services;
    }

    @NotNull
    private List<ServiceBean> getGlobalServicesByServiceType(
            @NotNull ServiceTypeEnum serviceType,
            @NotNull ActiveStatusEnum localStatus,
            @NotNull ActiveStatusEnum globalStatus) {
        List<ServiceBean> services = new ArrayList<ServiceBean>();

       if (globalRegistry != null) {
            List<BusinessService> businessServices =
                    globalRegistry.getServicesByServiceType(serviceType, globalStatus);

           // Loop through response, add service to ServiceBean list
            for (BusinessService service : businessServices) {
                ActiveStatusEnum activeStatusEnum =
                    getBlacklistStatus(
                        service.getInstitutionName(), service.getName());
                // Check blacklist before adding
                if (localStatus == ActiveStatusEnum.UNSPECIFIED ||
                        localStatus == activeStatusEnum) {
                    ServiceBean serviceBean =
                            new ServiceBean(service, RegistryInstance.GLOBAL);
                    // Set the blacklist status
                    if (activeStatusEnum != ActiveStatusEnum.UNSPECIFIED) {
                        serviceBean.setActiveStatus(activeStatusEnum);
                    }
                    // Add to collection
                    services.add(serviceBean);
                }
            }
        } else {
            log.info("Global registry is not configured");
        }

        return services;
    }

    @NotNull
    private List<ServiceBean> getLocalServicesByServiceType(
            @NotNull ServiceTypeEnum serviceType,
            @NotNull ActiveStatusEnum activeStatus) {
        List<ServiceBean> services = new ArrayList<ServiceBean>();

        if (localRegistry != null) {
            List<BusinessService> businessServices =
                localRegistry.getServicesByServiceType(serviceType, activeStatus);

            for (BusinessService service : businessServices) {
                ServiceBean serviceBean =
                        new ServiceBean(service, RegistryInstance.LOCAL);
                // Add to collection
                services.add(serviceBean);
            }
        } else {
            log.info("Local registry is not configured");
        }

        return services;
    }

    @Override
    @NotNull
    public List<ServiceBean> getServicesByServiceAndBindingType(
            @NotNull ServiceTypeEnum serviceType,
            @NotNull BindingTypeEnum bindingType,
            @NotNull ActiveStatusEnum localStatus,
            @NotNull ActiveStatusEnum globalStatus) {
        List<ServiceBean> services = new ArrayList<ServiceBean>();

        // Global services
        services.addAll(
                getGlobalServicesByServiceAndBindingType(
                        serviceType, bindingType, localStatus, globalStatus)
        );

        // Local services
        services.addAll(
                getLocalServicesByServiceAndBindingType(
                        serviceType, bindingType, localStatus)
        );

        return services;
    }

    @NotNull
    private List<ServiceBean> getGlobalServicesByServiceAndBindingType(
            @NotNull ServiceTypeEnum serviceType,
            @NotNull BindingTypeEnum bindingType,
            @NotNull ActiveStatusEnum localStatus,
            @NotNull ActiveStatusEnum globalStatus) {
        List<ServiceBean> services = new ArrayList<ServiceBean>();

        if (globalRegistry != null) {
            List<BusinessService> businessServices =
                globalRegistry.getServicesByServiceTypeAndBindingType(
                        serviceType, bindingType, globalStatus);

            // Add services to ServiceBean list
            for (BusinessService service : businessServices) {
                // Set the blacklist status
                ActiveStatusEnum activeStatusEnum =
                    getBlacklistStatus(
                        service.getInstitutionName(), service.getName());
                // Only add service if blacklist status matches
                if (localStatus == ActiveStatusEnum.UNSPECIFIED ||
                        localStatus == activeStatusEnum) {
                    ServiceBean serviceBean =
                            new ServiceBean(service, RegistryInstance.GLOBAL);
                    // Set status
                    if (activeStatusEnum != ActiveStatusEnum.UNSPECIFIED) {
                        serviceBean.setActiveStatus(activeStatusEnum);
                    }
                    // Add to collection
                    services.add(serviceBean);
                }
            }
        } else {
            log.info("Global registry is not configured");
        }

        return services;
    }

    @NotNull
    private List<ServiceBean> getLocalServicesByServiceAndBindingType(
            @NotNull ServiceTypeEnum serviceType,
            @NotNull BindingTypeEnum bindingType,
            @NotNull ActiveStatusEnum activeStatus) {
        List<ServiceBean> services = new ArrayList<ServiceBean>();

        if (localRegistry != null) {
            List<BusinessService> businessServices =
                    localRegistry.getServicesByServiceTypeAndBindingType(
                            serviceType, bindingType, activeStatus);

            for (BusinessService service : businessServices) {
                ServiceBean serviceBean =
                        new ServiceBean(service, RegistryInstance.LOCAL);
                // Add to collection
                services.add(serviceBean);
            }
        } else {
            log.info("Local registry is not configured");
        }

        return services;
    }

    @Override
    public void removeBinding(@NotNull Long bindingKey) {
        if (bindingKey < 1) {
            throw new RuntimeException(
                    "Unable to remove binding with key [" + bindingKey + "]");
        }

        Binding binding = getBindingByKey(bindingKey);
        if (binding == null) {
            log.warn("Unable to remove binding with key [" +
                    bindingKey + "] because I could not find it");
        } else {
            if (localRegistry != null) {
                try {
                    localRegistry.removeBinding(bindingKey);
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.warn("local registry is not properly configured");
            }
        }
    }

    @Override
    @Nullable
    public Binding getBindingByKey(@NotNull Long bindingKey) {
        Binding binding = null;

        if (bindingKey < 1) {
            throw new RuntimeException(
                    "Invalid binding key [" + bindingKey + "]");
        }

        if (localRegistry != null) {
            binding = localRegistry.getBindingByKey(bindingKey);
        }

        return binding;
    }

    @Override
    @NotNull
    public List<Binding> getBindingsByAccessPoint(@NotNull String accessPoint) {
        if (accessPoint.isEmpty()) {
            throw new RuntimeException(
                    "Invalid AccessPoint [" + accessPoint + "]");
        }

        List<Binding> bindings = new ArrayList<Binding>();

        if (localRegistry != null) {
            bindings.addAll(localRegistry.getBindingsByAccessPoint(accessPoint));
        }

        if (globalRegistry != null) {
            bindings.addAll(globalRegistry.getBindingsByAccessPoint(accessPoint));
        }

        return bindings;
    }

    @Override
    @NotNull
    public List<ServiceTypeEnum> getServiceTypes() {
        return new ArrayList<ServiceTypeEnum>(
                Arrays.asList(
                        ServiceTypeEnum.values()
                )
        );
    }

    @Override
    @NotNull
    public List<BindingTypeEnum> getBindingTypes() {
        return new ArrayList<BindingTypeEnum>(
                Arrays.asList(
                        BindingTypeEnum.values()
                )
        );
    }

    @Override
    @NotNull
    public List<ActiveStatusEnum> getStatusTypes() {
        return new ArrayList<ActiveStatusEnum>(
                Arrays.asList(
                        ActiveStatusEnum.values()
                )
        );
    }

    @Nullable
    private InstitutionBean getInstitutionByKey(@NotNull Long institutionKey) {
        if (institutionKey < 1) {
            throw new RuntimeException(
                "Unable to retrieve institution with key [" + institutionKey + ']');
        }

        InstitutionBean institution;

        // First search the local registry
        institution = getLocalInstitutionByKey(institutionKey);

        // Search the global registry
        if (institution == null) {
            institution = getGlobalInstitutionByKey(institutionKey);
        }

        return institution;
    }

    @Nullable
    private InstitutionBean getGlobalInstitutionByKey(@NotNull Long institutionKey) {
        if (institutionKey < 1) {
            throw new RuntimeException("Invalid institutionKey [" +
                    institutionKey + "]");
        }

        InstitutionBean institutionBean = null;

        if (globalRegistry != null) {
            Institution institution =
                    globalRegistry.getInstitutionByKey(institutionKey);

            if (institution != null) {
                institutionBean = new InstitutionBean(
                        institution, RegistryInstance.GLOBAL);
                // Set status
                ActiveStatusEnum activeStatus =
                    getBlacklistStatus(
                            institution.getName(), NULL_VALUE);
                if (activeStatus != ActiveStatusEnum.UNSPECIFIED) {
                    institutionBean.setActiveStatus(activeStatus);
                }

                // Retrieve the services and set blacklist status
                for (BusinessService service : institution.getService()) {
                    // Get the blacklist status
                    ActiveStatusEnum activeStatusEnum =
                        getBlacklistStatus(service.getInstitutionName(),
                                service.getName());
                    if (activeStatusEnum != ActiveStatusEnum.UNSPECIFIED) {
                        service.setActiveStatus(activeStatusEnum);
                    }
                }
            }
        } else {
            log.info("Global registry not configured");
        }

        return institutionBean;
    }

    @Nullable
    private InstitutionBean getLocalInstitutionByKey(@NotNull Long institutionKey) {
        if (institutionKey < 1) {
            throw new RuntimeException("Invalid institutionKey [" + institutionKey + "]");
        }

        InstitutionBean institutionBean = null;

        if (localRegistry != null) {
            Institution institution =
                    localRegistry.getInstitutionByKey(institutionKey);
            if (institution != null) {
                institutionBean = new InstitutionBean(institution, RegistryInstance.LOCAL);
            }
        } else {
            log.info("Local registry not configured");
        }

        return institutionBean;
    }

    @Nullable
    private ServiceBean getServiceByKey(@NotNull Long serviceKey) {
        if (serviceKey < 1) {
            throw new RuntimeException("Invalid serviceKey [" + serviceKey + "]");
        }

        ServiceBean service;

        // First search the local registry
        service = getLocalServiceByKey(serviceKey);

        // Search the global registry
        if (service == null) {
            service = getGlobalServiceByKey(serviceKey);
        }

        return service;
    }

    @Nullable
    private ServiceBean getGlobalServiceByKey(@NotNull Long serviceKey) {
        if (serviceKey < 1) {
            throw new RuntimeException("Invalid serviceKey [" + serviceKey + "]");
        }

        ServiceBean serviceBean = null;

        if (globalRegistry != null) {

            BusinessService service =
                    globalRegistry.getServiceByKey(serviceKey);

            if (service != null) {
                serviceBean = new ServiceBean(service, RegistryInstance.GLOBAL);
                // Set status
                ActiveStatusEnum activeStatus =
                    getBlacklistStatus(
                            service.getInstitutionName(), service.getName());
                if (activeStatus != ActiveStatusEnum.UNSPECIFIED) {
                    serviceBean.setActiveStatus(activeStatus);
                }
            }
        } else {
            log.info("Global registry not configured");
        }

        return serviceBean;
    }

    @Nullable
    private ServiceBean getLocalServiceByKey(@NotNull Long serviceKey) {
        if (serviceKey < 1) {
            throw new RuntimeException("Invalid serviceKey [" + serviceKey + "]");
        }

        ServiceBean serviceBean = null;

        if (localRegistry != null) {
            BusinessService service =
                    localRegistry.getServiceByKey(serviceKey);

            if (service != null) {
                serviceBean = new ServiceBean(service, RegistryInstance.LOCAL);
            }
        } else {
            log.info("Local registry not configured");
        }

        return serviceBean;
    }

    @NotNull
    private ActiveStatusEnum getBlacklistStatus(
            @NotNull String institutionName, @Nullable String serviceName) {
        if (institutionName.isEmpty()) {
            throw new RuntimeException("Invalid institutionName [" + institutionName + "]");
        }

        ActiveStatusEnum activeStatus = ActiveStatusEnum.UNSPECIFIED;

        if (blacklistService == null) {
            log.info("Blacklist is not properly configured, unable to connect");
            return activeStatus;
        }

        try {
            org.nterlearning.registry.blacklist.client.ActiveStatusEnum status =
                    blacklistService.getBlacklistStatus(institutionName, serviceName);

            if (status == null) {
                log.info("Unable to find blacklist status for institution [" +
                    institutionName + "]" +
                        (serviceName != null ? " service [" + serviceName + "]" : "") +
                    " setting to 'Unspecified'");
                status = org.nterlearning.registry.blacklist.client.ActiveStatusEnum.UNSPECIFIED;
            }

            activeStatus = ActiveStatusEnum.fromValue(status.value());
        } catch (Exception e) {
            log.error("Error calling getBlacklistStatus", e);
        }

        return activeStatus;
    }

    private void setBlacklistStatus(
            @NotNull String institutionName,
            @Nullable String serviceName,
            @NotNull ActiveStatusEnum activeStatus) {

        if (blacklistService == null) {
            log.info("Blacklist is not properly configured, unable to connect");
            return;
        }

        if (institutionName.isEmpty()) {
            throw new RuntimeException("Invalid institutionName [" + institutionName + ']');
        }

        try {
            org.nterlearning.registry.blacklist.client.BlacklistItem blacklistItem = new BlacklistItem();
            blacklistItem.setInstitution(institutionName);
            if (serviceName != null && !serviceName.isEmpty()) {
                blacklistItem.setService(serviceName);
            }
            org.nterlearning.registry.blacklist.client.ActiveStatusEnum blacklistStatus =
                    org.nterlearning.registry.blacklist.client.ActiveStatusEnum.fromValue(
                            activeStatus.value()
                    );

            blacklistItem.setStatus(blacklistStatus);

            blacklistService.addBlacklistItem(blacklistItem);

        } catch (Exception e) {
            log.error("Error adding blacklist entry", e);
        }
    }

    private void removeBlacklistItem(@NotNull String institutionName,
                                     @Nullable String serviceName) {
        if (blacklistService == null) {
            log.info("Blacklist is not properly configured, unable to connect");
            return;
        }

        if (institutionName.isEmpty()) {
            throw new RuntimeException("Invalid institutionName [" + institutionName + ']');
        }

        try {
            blacklistService.removeBlacklistItem(
                    institutionName, serviceName);
        } catch (Exception e) {
            log.error("Error removing blacklist entry", e);
        }
    }

    @NotNull
    private ActiveStatusEnum getActiveStatusFromValue(@NotNull String value) {
        ActiveStatusEnum activeStatusEnum = ActiveStatusEnum.UNSPECIFIED;
        try {
            activeStatusEnum =
                    ActiveStatusEnum.fromValue(value);
        } catch (Exception e) {
            log.error("Unexpected exception creating ActiveStatusEnum from value [" +
                value + "], acceptable values are [" +
                    printStatusTypeValues() + "]");
        }
        return activeStatusEnum;
    }

    private void setDefaultBlacklistStatus(@NotNull ActiveStatusEnum activeStatus) {
        if (blacklistService == null) {
            log.warn("Blacklist is not properly configured, unable to connect");
            return;
        }

        try {
            org.nterlearning.registry.blacklist.client.ActiveStatusEnum blacklistStatus =
                    org.nterlearning.registry.blacklist.client.ActiveStatusEnum.fromValue(
                            activeStatus.value()
                    );

            blacklistService.setBlacklistDefault(blacklistStatus);
        } catch (Exception e) {
            log.error("Unexpected error setting default Blacklist status", e);
        }
    }

    @NotNull
    private String printServiceTypeValues() {
        StringBuilder sb = new StringBuilder();
        ServiceTypeEnum[] values =
                ServiceTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i].value());
            if (i < values.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @NotNull
    private String printBindingTypeValues() {
        StringBuilder sb = new StringBuilder();
        BindingTypeEnum[] values =
                BindingTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i].value());
            if (i < values.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @NotNull
    private String printStatusTypeValues() {
        StringBuilder sb = new StringBuilder();
        ActiveStatusEnum[] values =
                ActiveStatusEnum.values();
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i].value());
            if (i < values.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }


}
