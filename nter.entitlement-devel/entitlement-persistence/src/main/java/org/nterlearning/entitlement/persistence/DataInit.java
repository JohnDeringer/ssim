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
package org.nterlearning.entitlement.persistence;

import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;

import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Pre-loads enums into the database
 * Adds 'Global_Admin' subject
 *
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 2/13/12
 */
public class DataInit {

    private EntitlementDao entitlementDao;
    private IdentityService identityService;
    private List<ActionEntity> actions;
    private String adminUID;

    private static final String GLOBAL_ADMIN_RESOURCE = "*";
    private static final String DEFAULT_REALM = "/";

    private Logger logger = LoggerFactory.getLogger(DataInit.class);

    public DataInit(EntitlementDao entitlementDao,
                    IdentityService identityService,
                    String adminUserEmail) {

        this.entitlementDao = entitlementDao;
        this.identityService = identityService;

        // Load Actions
        for (Action action : Action.values()) {
            logger.info("Searching for action [" + action.value() + "]");
            if (!doesActionExist(action.value())) {
                logger.info("Adding action [" + action.value() + "]");
                entitlementDao.addAction(action.value());
            }
        }

        // Add global admin policy
        addAdminUser(adminUserEmail);
    }

    public String getDefaultAdminUID() {
        return adminUID;
    }

    public String getUidFromEmail(String email) {
        String uid = null;
        UserImpl user = null;
        if (identityService != null) {
            try {
                user = identityService.getUserByEmail(email);
            } catch (Exception e) {
                logger.error("Error connecting to Identity Service", e);
            }
        } else {
            logger.info("Unable to contact identityService [" + identityService + "]");
        }

        if (user != null) {
            uid = user.getUid();
        }
        return uid;
    }

    private void addAdminUser(String adminUserEmail) {
        adminUID = getUidFromEmail(adminUserEmail);
        if (adminUID != null) {
            if (!doesGlobalAdminExists(adminUID)) {
                logger.info("Adding subject [" + adminUserEmail + "] as Global Admin");
                entitlementDao.createPolicy(
                        DEFAULT_REALM, adminUID, GLOBAL_ADMIN_RESOURCE, Action.GLOBAL_ADMIN.value());
            } else {
                logger.info("Global Admin [" + adminUserEmail + "] already exist, will not add");
            }
        } else {
            logger.error("Unable to retrieve admin user [" + adminUserEmail +
                    "] from idp");
        }
    }

    private boolean doesGlobalAdminExists(String uid) {
        PolicyEntity policy =
                entitlementDao.getPolicy(DEFAULT_REALM, uid, GLOBAL_ADMIN_RESOURCE);
        return (policy != null);
    }

    private boolean doesActionExist(String value) {
        boolean doesActionExist = false;
        if (actions == null) {
            actions = entitlementDao.getActions();
        }
        logger.info("doesActionExist [" + value + "]");
        for (ActionEntity actionEntity : actions) {
            if (actionEntity.getValue().equals(value)) {
                doesActionExist = true;
                break;
            }
        }
        return doesActionExist;
    }

}
