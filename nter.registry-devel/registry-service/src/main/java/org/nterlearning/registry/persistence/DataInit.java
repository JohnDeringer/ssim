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

import org.nterlearning.entitlement.client.Entitlement;
import org.nterlearning.entitlement.client.EntitlementPolicy;
import org.nterlearning.entitlement.client.EntitlementService;

import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/6/11
 */
public class DataInit {

    private RegistryDao registryDao;
    private BlacklistDao blacklistDao;
    private IdentityService identityService;
    private Entitlement entitlement;

    private List<BindingTypeEntity> bindingTypes;
    private List<ServiceTypeEntity> serviceTypes;
    private List<ActiveStatusEntity> activeStatusTypes;
    private String adminUID;
    private String registryRealm = null;

    private static final String GLOBAL_ADMIN_RESOURCE = "*";

    private Logger logger = LoggerFactory.getLogger(DataInit.class);

    public DataInit(RegistryDao registryDao,
                    BlacklistDao blacklistDao,
                    EntitlementService entitlement,
                    String registryRealm,
                    String registryAdminEmail) {

        this.registryDao = registryDao;
        this.blacklistDao = blacklistDao;
        this.registryRealm = registryRealm;
        this.entitlement = entitlement;

        // Load BindingTypes
        for (BindingTypeEnum bindingTypeEnum : BindingTypeEnum.values()) {
            System.out.println("Searching for bindingType [" + bindingTypeEnum.value() + "]");
            if (!bindingTypeExist(bindingTypeEnum.value())) {
                System.out.println("Adding bindingType [" + bindingTypeEnum.value() + "]");
                BindingTypeEntity bindingType = new BindingTypeEntity();
                bindingType.setValue(bindingTypeEnum.value());
                registryDao.addBindingType(bindingType);
            }
        }
        // Load ServiceTypes
        for (ServiceTypeEnum serviceTypeEnum : ServiceTypeEnum.values()) {
            System.out.println("Searching for serviceType [" + serviceTypeEnum.value() + "]");
            if (!serviceTypeExist(serviceTypeEnum.value())) {
                System.out.println("Adding serviceType [" + serviceTypeEnum.value() + "]");
                ServiceTypeEntity serviceType = new ServiceTypeEntity();
                serviceType.setValue(serviceTypeEnum.value());
                registryDao.addServiceType(serviceType);
            }
        }
        // Load ActiveStatus types
        for (ActiveStatusEnum activeStatusEnum : ActiveStatusEnum.values()) {
            System.out.println("Searching for activeStatusEnum [" + activeStatusEnum.value() + "]");
            if (!activeStatusExist(activeStatusEnum.value())) {
                System.out.println("Adding activeStatus [" + activeStatusEnum.value() + "]");
                ActiveStatusEntity activeStatusEntity = new ActiveStatusEntity();
                activeStatusEntity.setValue(activeStatusEnum.value());
                blacklistDao.addActiveStatus(activeStatusEntity);
            }
        }
        // Add global admin policy
        addAdminUser(registryAdminEmail);
    }

    public String getDefaultAdminUID() {
        return adminUID;
    }

    public String getUidFromEmail(String email) {
        String uid = null;
        if (getIdentityService() != null) {
            UserImpl user = identityService.getUserByEmail(email);
            if (user != null) {
                uid = user.getUid();
            }
        } else {
            logger.info("Invalid reference to IDP [" + identityService + "]");
        }
        return uid;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    private boolean bindingTypeExist(String value) {
        boolean doesBindingTypeExist = false;
        if (bindingTypes == null) {
            bindingTypes = registryDao.getBindingTypes();
        }

        for (BindingTypeEntity bindingType : bindingTypes) {
            if (bindingType.getValue().equals(value)) {
                doesBindingTypeExist = true;
                break;
            }
        }
        return doesBindingTypeExist;
    }

    private boolean serviceTypeExist(String value) {
        boolean doesServiceTypeExist = false;
        if (serviceTypes == null) {
            serviceTypes = registryDao.getServiceTypes();
        }

        for (ServiceTypeEntity serviceType : serviceTypes) {
            if (serviceType.getValue().equals(value)) {
                doesServiceTypeExist = true;
                break;
            }
        }
        return doesServiceTypeExist;
    }

    private boolean activeStatusExist(String value) {
        boolean doesActiveStatusExist = false;
        if (activeStatusTypes == null) {
            activeStatusTypes = blacklistDao.getActiveStatusTypes();
        }
        System.out.println("doesActiveStatusExist [" + value + "]");
        for (ActiveStatusEntity activeStatus : activeStatusTypes) {
            if (activeStatus.getValue().equals(value)) {
                doesActiveStatusExist = true;
                break;
            }
        }
        return doesActiveStatusExist;
    }

    // Add entitlement for Admin user
    private void addAdminUser(String registryAdminEmail) {

        logger.info("Adding entitlement for Admin user [" + registryAdminEmail + "]");

        if (registryAdminEmail != null) {
            adminUID = getUidFromEmail(registryAdminEmail);

            if (adminUID != null) {
                if (!doesGlobalAdminExists(adminUID)) {
                    logger.info("Adding subject [" + registryAdminEmail +
                            "] as the Registry Global Admin");
                    if (entitlement != null) {
                        entitlement.createPolicy(
                                registryRealm, adminUID, GLOBAL_ADMIN_RESOURCE,
                                org.nterlearning.entitlement.client.Action.GLOBAL_ADMIN);
                    } else {
                        logger.info("Unable to contact Entitlement Service [" + entitlement + "]");
                    }
                } else {
                    logger.info("Global Admin [" + registryAdminEmail + "] already exist, will not add");
                }
            } else {
                logger.error("Unable to retrieve admin user [" + registryAdminEmail +
                        "] from idp");
            }
        } else {
            logger.error("Unable to add entitlement for Admin user [" + registryAdminEmail + "]");
        }
    }

    private boolean doesGlobalAdminExists(String uid) {
        EntitlementPolicy policy = null;
        if (entitlement != null) {
            policy = entitlement.getPolicy(registryRealm, uid, GLOBAL_ADMIN_RESOURCE);
        } else {
            logger.info("Unable to contact Entitlement Service [" + entitlement + "]");
        }
        return (policy != null);
    }

}
