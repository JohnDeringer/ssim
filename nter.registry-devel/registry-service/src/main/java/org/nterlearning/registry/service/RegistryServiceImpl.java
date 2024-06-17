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

import org.jetbrains.annotations.NotNull;
import org.nterlearning.entitlement.client.Action;
import org.nterlearning.entitlement.client.Entitlement;
import org.nterlearning.entitlement.client.EntitlementPolicy;

import org.nterlearning.registry.model.BlacklistModel;
import org.nterlearning.registry.model.RegistryModel;

import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.*;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.RequestStatus;
import org.nterlearning.xml.nter_registry.registry_interface_0_1_0.*;
import org.nterlearning.xml.nter_registry.registry_interface_0_1_0.GetInstitutions;
import org.nterlearning.xml.nter_registry.registry_interface_0_1_0.GetInstitutionsResponse;
import org.nterlearning.xml.nter_registry.registry_interface_0_1_0_wsdl.*;

import org.apache.log4j.Logger;

import java.util.List;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/13/11
 */
public class RegistryServiceImpl
        implements RegistryInterface {

    private RegistryModel registryModel;
    private BlacklistModel blacklistModel;
    private IdentityService identityService;
    private Entitlement entitlement;
    private String registryRealm;
    private String commerceRealm;
    private boolean entitlementDisabled = false;

    private WSValidator wsValidator;

    private Logger logger = Logger.getLogger(RegistryServiceImpl.class);

    public RegistryServiceImpl() {
    }

    @Override
    public CreateInstitutionResponse createInstitution(
            CreateInstitution request) {
        CreateInstitutionResponse response = new CreateInstitutionResponse();

        Institution institution = request.getInstitution();
        logger.info("Creating institution [" + institution.getName() + "]");

        // Create the institution
        RequestStatus requestStatus = registryModel.createInstitution(institution);

        if (requestStatus == RequestStatus.SUCCESS) {
            // Create registry entitlement
            String resource = institution.getName();

            if (!entitlementDisabled) {
                String authorizingSubject = wsValidator.getUid();
                // Create a registry entitlement for the authorized user from the security header
                createAdminEntitlement(registryRealm, authorizingSubject, resource);
                // Create a commerce entitlement for the authorized user from the security header
                createAdminEntitlement(commerceRealm, authorizingSubject, resource);

                // Create an entitlement for the user in institution contact info
                Contact contact = institution.getContactInfo();
                String email;
                if (contact != null) {
                    email = contact.getEmail();
                    if (email == null || email.isEmpty()) {
                        throw new RuntimeException("Unable to create Institution, contact email is required.");
                    }

                    if (identityService != null) {
                        UserImpl user = identityService.getUserByEmail(email);
                        if (user != null) {
                            String uid = user.getUid();
                            // Create a registry entitlement for the institution contactInfo user
                            createAdminEntitlement(registryRealm, uid, resource);
                            // Create a commerce entitlement for the institution contactInfo user
                            createAdminEntitlement(commerceRealm, uid, resource);
                        } else {
                            logger.error("Unable to create entitlement for resource [" +
                                resource +
                                "] unable to find UID from IdentityService for email [" +
                                email + "]");
                        }
                    } else {
                        logger.error("Unable to create entitlement for resource [" +
                                resource +
                                "] invalid identityService reference [" +
                                identityService + "]");
                    }
                } else {
                    throw new RuntimeException(
                            "Unable to create Institution, missing contact information.");
                }
            }
        } else {
            logger.error("Unexpected response from createInstitution [" + requestStatus + "]");
        }

        response.setStatus(requestStatus);

        return response;
    }

    @Override
    public UpdateInstitutionResponse updateInstitution(UpdateInstitution request) {
        UpdateInstitutionResponse response = new UpdateInstitutionResponse();

        Institution institution = request.getInstitution();

        logger.info("Updating institution [" + institution.getName() +
                "] description [" + institution.getDescription() + "]");

        String authorizingSubject = wsValidator.getUid();
        boolean isAuthorized =
                entitlementDisabled ||
                        entitlement.isAuthorized(
                            registryRealm, authorizingSubject,
                                institution.getName(), Action.WRITE);

        RequestStatus requestStatus;
        if (isAuthorized) {
            // Update the institution
            requestStatus = registryModel.updateInstitution(institution);
        } else {
            throw new RuntimeException(
                "User [" + wsValidator.getUser() +
                    "] does not have authorization to updateInstitution [" +
                        institution.getName() + "]");
        }

        response.setStatus(requestStatus);

        return response;
    }

    @Override
    public RemoveInstitutionResponse removeInstitution(RemoveInstitution request) {
        RemoveInstitutionResponse response = new RemoveInstitutionResponse();
        RequestStatus requestStatus = RequestStatus.FAILURE;

        long key = request.getInstitutionKey();

        Institution institution = registryModel.getInstitutionByKey(key);
        if (institution != null) {
            String authorizingSubject = wsValidator.getUid();
            boolean isAuthorized =
                entitlementDisabled ||
                        entitlement.isAuthorized(
                            registryRealm, authorizingSubject,
                                institution.getName(), Action.GLOBAL_ADMIN);

            if (isAuthorized) {
                String institutionName = institution.getName();
                String email = null;
                Contact contact = institution.getContactInfo();
                if (contact != null) {
                    email = contact.getEmail();
                } else {
                    logger.warn("Unable to remove entitlements related to institution [" +
                        institutionName + "], there is no contact information to retrieve institution email");
                }

                requestStatus = registryModel.removeInstitution(key);
                // Delete entitlement policies associated with this institution
                if (requestStatus == RequestStatus.SUCCESS &&
                        email != null && !email.isEmpty()) {
                    removeEntitlements(institutionName, email);
                }
            } else {
                throw new RuntimeException(
                    "User [" + wsValidator.getUser() +
                        "] does not have authorization to removeInstitution [" +
                            institution.getName() + "]");
            }
        } else {
            logger.warn("Unable to remove institution using key [" + key +
                    "], because the institution could not be found.");
        }

        // Set response
        response.setStatus(requestStatus);

        return response;
    }

    @Override
    public GetInstitutionByKeyResponse getInstitutionByKey(
            GetInstitutionByKey request) {

        GetInstitutionByKeyResponse response =
            new GetInstitutionByKeyResponse();

        response.setInstitution(
            registryModel.getInstitutionByKey(
                    request.getInstitutionKey()
            )
        );

        return response;
    }

    @Override
    public GetInstitutionByNameResponse getInstitutionByName(
            GetInstitutionByName request) {

        GetInstitutionByNameResponse response =
            new GetInstitutionByNameResponse();

        response.setInstitution(
            registryModel.getInstitutionByName(
                request.getInstitutionName()
            )
        );

        return response;
    }

    @Override
    public GetInstitutionByAccessPointResponse getInstitutionByAccessPoint(
            GetInstitutionByAccessPoint request) {

        GetInstitutionByAccessPointResponse response =
            new GetInstitutionByAccessPointResponse();

        response.setInstitution(
            registryModel.getInstitutionByAccessPoint(
                    request.getEndPoint()
            )
        );

        return response;
    }

    @Override
    public GetInstitutionsResponse getInstitutions(
             GetInstitutions request) {

        GetInstitutionsResponse response = new GetInstitutionsResponse();

        ActiveStatusEnum status = request.getStatus();
        if (status == null) {
            logger.warn("Invalid ActiveStatus [" + status + "] setting to Unspecified");
            status = ActiveStatusEnum.UNSPECIFIED;
        }

        response.getInstitution().addAll(
            registryModel.getInstitutions(status)
        );

        return response;
    }

    @Override
    public CreateServiceResponse createService(
            CreateService request) {
        // Response document
        CreateServiceResponse response = new CreateServiceResponse();

        long institutionKey = request.getInstitutionKey();
        BusinessService service = request.getService();

        if (service == null) {
            throw new RuntimeException("Unable to add service [" + service + "]");
        }

        Institution institution;
        if (institutionKey == 0 || institutionKey == Long.MIN_VALUE) {
            String institutionName = service.getName();
            if (institutionName == null) {
                throw new RuntimeException(
                    "InstitutionKey is required to create a service [" + institutionKey + "]");
            } else {
                institution =
                    registryModel.getInstitutionByName(institutionName);
                if (institution == null) {
                    throw new RuntimeException(
                        "Cannot create service. Unable to find Institution using institutionName [" +
                                institutionName + "]");
                }
            }
        } else {
            institution =
                registryModel.getInstitutionByKey(institutionKey);
            if (institution == null) {
                throw new RuntimeException(
                    "Cannot create service. Unable to find Institution using institutionKey [" +
                                institutionKey + "]");
            }
        }

        RequestStatus requestStatus;
        String authorizingSubject = wsValidator.getUid();
        boolean isAuthorized =
                entitlementDisabled ||
                        entitlement.isAuthorized(
                            registryRealm, authorizingSubject,
                                institution.getName(), Action.WRITE);

        if (isAuthorized) {
            // Add/update institutionName in service object
            service.setInstitutionName(institution.getName());
            // Add the service
            requestStatus = registryModel.addService(institutionKey, service);
        } else {
            throw new RuntimeException(
                "User [" + wsValidator.getUser() +
                    "] does not have authorization to createService for institution [" +
                            institution.getName() + "]");
        }

        response.setStatus(requestStatus);

        return response;
    }

    @Override
    public UpdateServiceResponse updateService(UpdateService request) {
        // Response document
        UpdateServiceResponse response = new UpdateServiceResponse();

        BusinessService service = request.getService();
        if (service == null) {
            throw new RuntimeException("Invalid service [" + service + "]");
        }

        String institutionName = service.getInstitutionName();
        if (institutionName == null || institutionName.isEmpty()) {
            Institution institution =
                    registryModel.getInstitutionByServiceKey(service.getKey());
            if (institution != null) {
                institutionName = institution.getName();
            } else {
                throw new RuntimeException(
                    "Unable to update service [" + service.getName() +
                        "] unable to locate parent institution");
            }
        }

        RequestStatus requestStatus;
        String authorizingSubject = wsValidator.getUid();
        boolean isAuthorized =
                entitlementDisabled ||
                        entitlement.isAuthorized(
                            registryRealm, authorizingSubject,
                                institutionName, Action.WRITE);

        if (isAuthorized) {
            // Update the service
            requestStatus = registryModel.updateService(service);
        } else {
            throw new RuntimeException(
                "User [" + wsValidator.getUser() +
                    "] does not have authorization to updateService for institution [" +
                            institutionName + "]");
        }

        response.setStatus(requestStatus);

        return response;
    }

    @Override
    public RemoveServiceResponse removeService(RemoveService request) {
        RemoveServiceResponse response = new RemoveServiceResponse();

        long serviceKey = request.getServiceKey();
        if (serviceKey < 1) {
            throw new RuntimeException(
                "Unable to remove service using key [" + serviceKey + "]");
        }

        String institutionName;
        Institution institution =
                registryModel.getInstitutionByServiceKey(serviceKey);
        if (institution != null) {
            institutionName = institution.getName();
        } else {
            throw new RuntimeException(
                "Unable to update service using serviceKey [" + serviceKey +
                    "] unable to locate parent institution");
        }

        RequestStatus requestStatus;
        String authorizingSubject = wsValidator.getUid();
        boolean isAuthorized =
                entitlementDisabled ||
                        entitlement.isAuthorized(
                            registryRealm, authorizingSubject,
                                institutionName, Action.WRITE);

        if (isAuthorized) {
            // Remove the service
            requestStatus = registryModel.removeService(serviceKey);
        } else {
            throw new RuntimeException(
                "User [" + wsValidator.getUser() +
                    "] does not have authorization to removeService for institution [" +
                            institutionName + "]");
        }

        response.setStatus(requestStatus);

        return response;
    }

     @Override
    public GetServiceByKeyResponse getServiceByKey(GetServiceByKey request) {
        GetServiceByKeyResponse response = new GetServiceByKeyResponse();
        response.setService(
            registryModel.getServiceByKey(
                    request.getServiceKey()
            )
        );

        return response;
    }

    @Override
    public GetServiceByNameResponse getServiceByName(
            GetServiceByName request) {

        GetServiceByNameResponse response = new GetServiceByNameResponse();
        response.setService(
            registryModel.getServiceByName(
                request.getInstitutionName(),
                request.getServiceName()
            )
        );

        return response;
    }

    @Override
    public GetServicesByServiceTypeResponse getServicesByServiceType(
            GetServicesByServiceType request) {

        GetServicesByServiceTypeResponse response =
                new GetServicesByServiceTypeResponse();

        ServiceTypeEnum serviceType = request.getServiceType();
        if (serviceType == null) {
            throw new RuntimeException("Invalid ServiceType [" + serviceType +
                "] valid ServiceType values [" + getPrintableServiceTypes() + "]");
        }

        ActiveStatusEnum status = request.getStatus();
        if (status == null) {
            logger.warn("Invalid ActiveStatus [" + status + "] setting to Unspecified");
            status = ActiveStatusEnum.UNSPECIFIED;
        }

        response.getServices().addAll(
            registryModel.getServicesByServiceType(
                serviceType,
                status
            )
        );

        return response;
    }

    @Override
    public GetServicesByServiceAndBindingTypeResponse getServicesByServiceAndBindingType(
            GetServicesByServiceAndBindingType request) {

         GetServicesByServiceAndBindingTypeResponse response =
                 new GetServicesByServiceAndBindingTypeResponse();

        ServiceTypeEnum serviceType = request.getServiceType();
        if (serviceType == null) {
            throw new RuntimeException("Invalid ServiceType [" + serviceType +
                "] valid ServiceType values [" + getPrintableServiceTypes() + "]");
        }

        BindingTypeEnum bindingType = request.getBindingType();
        if (bindingType == null) {
            throw new RuntimeException("Invalid BindingType [" + bindingType +
                "] valid BindingType values [" + getPrintableBindingTypes() + "]");
        }

        ActiveStatusEnum status = request.getStatus();
        if (status == null) {
            logger.warn("Invalid ActiveStatus [" + status + "] setting to Unspecified");
            status = ActiveStatusEnum.UNSPECIFIED;
        }

        response.getServices().addAll(
            registryModel.getServicesByServiceTypeAndBindingType(
                serviceType,
                bindingType,
                status
            )
        );

        return response;
    }

    @Override
    public GetServicesResponse getServices(
            GetServices request) {

        GetServicesResponse response = new GetServicesResponse();

        ActiveStatusEnum status = request.getStatus();
        if (status == null) {
            logger.warn("Invalid ActiveStatus [" + status + "] setting to Unspecified");
            status = ActiveStatusEnum.UNSPECIFIED;
        }

        response.getServices().addAll(
            registryModel.getServices(status)
        );

        return response;
    }

    @Override
    public GetServiceTypesResponse getServiceTypes(
            GetServiceTypes request) {

        GetServiceTypesResponse response = new GetServiceTypesResponse();

        response.getServiceType().addAll(
            registryModel.getServiceTypes()
        );

        return response;
    }

    @Override
    public GetBindingTypesResponse getBindingTypes(
            GetBindingTypes request) {

        GetBindingTypesResponse response = new GetBindingTypesResponse();

        response.getBindingType().addAll(
            registryModel.getBindingTypes()
        );

        return response;
    }

    @Override
    public GetStatusTypesResponse getStatusTypes(
            GetStatusTypes request) {

        GetStatusTypesResponse response = new GetStatusTypesResponse();
        response.getActiveStatus().addAll(
            blacklistModel.getActiveStatusTypes()
        );

        return response;
    }

    @Override
    public RemoveBindingResponse removeBinding(RemoveBinding request) {
        RemoveBindingResponse response = new RemoveBindingResponse();

        long bindingKey = request.getBindingKey();
        if (bindingKey < 1) {
            throw new RuntimeException(
                "Unable to remove binding with key [" + bindingKey + "]");
        }

        BusinessService service =
                registryModel.getServiceWithBindingKey(bindingKey);
        if (service == null) {
            throw new RuntimeException(
                "Unable to removeBinding using binding key [" + bindingKey +
                    "] unable to locate parent service");
        }

        String institutionName = service.getInstitutionName();
        RequestStatus requestStatus;
        String authorizingSubject = wsValidator.getUid();

        boolean isAuthorized =
                entitlementDisabled ||
                        entitlement.isAuthorized(
                            registryRealm, authorizingSubject,
                                institutionName, Action.WRITE);

        if (isAuthorized) {
            // Remove the Binding
            requestStatus = registryModel.removeBinding(bindingKey);
        } else {
            throw new RuntimeException(
                "User [" + wsValidator.getUser() +
                    "] does not have authorization to removeBinding for institution [" +
                            institutionName + "]");
        }

        response.setStatus(requestStatus);

        return response;
    }

    @Override
    public GetBindingByKeyResponse getBindingByKey(GetBindingByKey request) {
        if (request == null || request.getKey() == 0) {
            throw new RuntimeException(
                    "Unable to retrieve binding with 'Null key");
        }

        long key = request.getKey();
        GetBindingByKeyResponse response = new GetBindingByKeyResponse();
        Binding binding = registryModel.getBindingByKey(key);
        if (binding != null) {
            response.setBinding(binding);
        }

        return response;
    }

    @Override
    public GetBindingsByAccessPointResponse getBindingsByAccessPoint(
            GetBindingsByAccessPoint request) {
        if (request == null || request.getAccessPoint() == null) {
            throw new RuntimeException(
                    "Unable to retrieve binding with 'Null access point");
        }

        String accessPoint = request.getAccessPoint();
        GetBindingsByAccessPointResponse response =
                new GetBindingsByAccessPointResponse();
        List<Binding> bindings =
                registryModel.getBindingsByAccessPoint(accessPoint);
        if (bindings != null && !bindings.isEmpty()) {
            response.getBindings().addAll(bindings);
        }

        return response;
    }

    public void setRegistryModel(RegistryModel registryModel) {
        this.registryModel = registryModel;
    }

    public void setBlacklistModel(BlacklistModel blacklistModel) {
        this.blacklistModel = blacklistModel;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public void setEntitlement(Entitlement entitlement) {
        this.entitlement = entitlement;
    }

    public void setRegistryRealm(String registryRealm) {
        this.registryRealm = registryRealm;
    }

    public void setCommerceRealm(String commerceRealm) {
        this.commerceRealm = commerceRealm;
    }

    public void setWsValidator(WSValidator wsValidator) {
        this.wsValidator = wsValidator;
    }

    public void setEntitlementDisabled(String entitlementDisabled) {
        this.entitlementDisabled = entitlementDisabled.equalsIgnoreCase("true");
        if (this.entitlementDisabled) {
            logger.warn("Entitlement is disabled on this registry instance");
        }
    }

    private void removeEntitlements(String institutionName, String email) {
        if (entitlementDisabled) {
            logger.debug("Entitlement is disabled");
            return;
        }

        if (entitlement != null) {
            String authorizingSubject = getUidFromEmail(email);

            if (authorizingSubject != null && !authorizingSubject.isEmpty()) {
                // Retrieve collection of registry policies related to the resource/institution
                for (EntitlementPolicy policy :
                        entitlement.getPoliciesByResource(registryRealm, institutionName)) {

                    logger.info("Removing entitlement policy with key [" + policy.getKey() + "]");

                    entitlement.removePolicy(policy.getKey());
                }

                // Retrieve collection of commerce policies related to the resource/institution
                for (EntitlementPolicy policy :
                        entitlement.getPoliciesByResource(commerceRealm, institutionName)) {

                    logger.info("Removing entitlement policy with key [" + policy.getKey() + "]");

                    entitlement.removePolicy(policy.getKey());
                }
            } else {
                logger.warn("Unable to remove entitlement policy for institution [" +
                    institutionName + "], unable to retrieve authorized subject using email [" +
                    email + "]");
            }
        } else {
            logger.warn("Unable to remove entitlement policy for institution [" +
                institutionName + "], reference to the entitlement service is 'null'");
        }
    }

    private void createAdminEntitlement(
            @NotNull String realm, @NotNull String subject, @NotNull String resource) {
        if (entitlementDisabled) {
            logger.debug("Entitlement is disabled");
            return;
        }

        if (entitlement != null) {
            // Does the policy already exist?
            EntitlementPolicy policy =
                    entitlement.getPolicy(realm, subject, resource);
            if (policy == null) {
                // Create a new policy
                logger.debug("Creating entitlementPolicy for subject [" +
                    subject + "] on resource [" + resource + "] in realm [" +
                    realm + "]");
                entitlement.createPolicy(
                        realm, subject, resource, Action.ADMIN);
            } else {
                logger.debug(
                    "A entitlement policy already exists for subject [" +
                    subject + "] resource [" + resource + "] in realm [" +
                    realm + "]");
            }
        } else {
            logger.error("Invalid reference to entitlement [" + entitlement +
                    "] check entitlement configuration settings");
        }
    }

    private String getUidFromEmail(@NotNull String email) {
        String uid = null;
        if (identityService != null) {
            UserImpl user = identityService.getUserByEmail(email);
            if (user != null) {
                uid = user.getUid();
            }
        }

        return uid;
    }

    private String getPrintableServiceTypes() {
        StringBuilder sb = new StringBuilder();
        ServiceTypeEnum[] serviceTypes = ServiceTypeEnum.values();
        for (int i = 0; i < serviceTypes.length; i++ ) {
            sb.append(serviceTypes[i].value());
            if (i < serviceTypes.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private String getPrintableBindingTypes() {
        StringBuilder sb = new StringBuilder();
        BindingTypeEnum[] bindingTypes = BindingTypeEnum.values();
        for (int i = 0; i < bindingTypes.length; i++ ) {
            sb.append(bindingTypes[i].value());
            if (i < bindingTypes.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
