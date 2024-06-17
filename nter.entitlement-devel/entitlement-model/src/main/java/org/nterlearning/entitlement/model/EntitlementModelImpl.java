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
package org.nterlearning.entitlement.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.entitlement.persistence.ActionEntity;
import org.nterlearning.entitlement.persistence.EntitlementDao;

import org.nterlearning.entitlement.persistence.PolicyEntity;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.EntitlementPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 * Date: 8/10/11
 * Time: 10:14 AM
 */
public class EntitlementModelImpl implements EntitlementModel {

    private Logger logger = LoggerFactory.getLogger(EntitlementModelImpl.class);
    private EntitlementDao dao;
    private static final String GLOBAL_ADMIN_RESOURCE = "*";
    private static final String DEFAULT_REALM = "/";

    @Override
    public void createPolicy(@Nullable String realm,
                             @NotNull String subject,
                             @NotNull String resource,
                             @NotNull String permission,
                             @NotNull String authorizingSubject) {

        logger.info("Authorizing Subject [" + authorizingSubject +
                "] is attempting to createPolicy using subjectId [" +
                subject + "], resource [" + resource +
                "] and action [" + permission + "]");

        if (realm == null || realm.isEmpty()) {
            logger.info("Unspecified realm [" + realm + "] defaulting to [" +
                DEFAULT_REALM + "]");
            realm = DEFAULT_REALM;
        }

        if (isGlobalAdmin(realm, subject)) {
            logger.warn(
                "Unsuccessful attempt to add an entitlement policy for Global Admin, Global Admin already has an implicit policy to this resource.");
            return;
        }

        Action action = Action.fromValue(permission);

        // Does the new policy.action == GLOBAL_ADMIN ?
        if (action == Action.GLOBAL_ADMIN) {
            // adminUser must be a global admin to authorize a new global admin
            if (dao.isAuthorized(realm, authorizingSubject, GLOBAL_ADMIN_RESOURCE, Action.GLOBAL_ADMIN)) {
                // create global admin policy
                logger.info("Authorizing Subject [" + authorizingSubject +
                    "] has been authorized to createPolicy using realm [" +
                        realm + "] subject [" +
                    subject + "], resource [" + GLOBAL_ADMIN_RESOURCE +
                    "] and action [" + action + "]");
                dao.createPolicy(realm, subject, GLOBAL_ADMIN_RESOURCE, action.value());
            } else {
                throw new RuntimeException(
                    "Assigning 'Global Admin' privileges requires 'Global Admin' user");
            }
        // User is creating a new resource, such as adding a new institution to registry.
        //   The user/subject is also the authorizingSubject and will be the Admin of the new resource.
        } else if (subject.equals(authorizingSubject) && action == Action.ADMIN) {
            // Ensure that this is a new Policy
            PolicyEntity policy  = dao.getPolicy(realm, subject, resource);
            if (policy != null) {
                throw new RuntimeException(
                    "Subject [" + subject +
                    "] is not an authorized subject for Resource [" + resource + "]");
            } else {
                // create policy
                logger.info("Authorizing Subject [" + authorizingSubject +
                        "] has been authorized to createPolicy using subject [" +
                        subject + "], resource [" + resource +
                        "] and action [" + action + "]");
                dao.createPolicy(realm, subject, resource, action.value());
            }
        // Normal case - check entitlement of authorizingSubject
        //   before adding new policy
        } else if (dao.isAuthorized(realm, authorizingSubject, resource, action)) {
            // create policy
            logger.info("Authorizing Subject [" + authorizingSubject +
                    "] has been authorized to createPolicy using subject [" +
                    subject + "], resource [" + resource +
                    "] and action [" + action + "]");
            dao.createPolicy(realm, subject, resource, action.value());
        } else {
            throw new RuntimeException(
                "Authorizing subject [" + authorizingSubject +
                "] does not have authorization to grant subject [" +
                subject + "] " + action + " access to resource [" +
                resource + "] in realm [" + realm + "]");
        }
    }

    @Override
    public void updatePolicy(@Nullable String realm,
                             @NotNull String subject,
                             @NotNull String resource,
                             @NotNull String permission,
                             @NotNull String authorizingSubject) {
        logger.info("Authorizing Subject [" + authorizingSubject +
                "] is attempting to updatePolicy with subject [" +
                subject + "], resource [" + resource +
                "] and action [" + permission + "] in realm [" + realm + "]");

        if (realm == null || realm.isEmpty()) {
            logger.info("Unspecified realm [" + realm + "] defaulting to [" +
                DEFAULT_REALM + "]");
            realm = DEFAULT_REALM;
        }

        if (isGlobalAdmin(realm, subject)) {
            logger.warn(
                "Unsuccessful attempt to modify an entitlement policy for Global Admin, Global Admin already has an implicit policy to this resource.");
            return;
        }

        Action action = Action.fromValue(permission);

        // Is the new action/permission GLOBAL_ADMIN ?
        if (action == Action.GLOBAL_ADMIN) {
            // adminUser must be a global admin to authorize a new global admin
            if (dao.isAuthorized(realm, authorizingSubject, GLOBAL_ADMIN_RESOURCE,
                    Action.GLOBAL_ADMIN)) {
                // create global admin policy
                logger.info("Authorizing Subject [" + authorizingSubject +
                    "] has been authorized to updatePolicy with subject [" +
                    subject + "], resource [" + GLOBAL_ADMIN_RESOURCE +
                    "] and action [" + action + "]");
                dao.updatePolicy(realm, subject, GLOBAL_ADMIN_RESOURCE, action.value());
            } else {
                throw new RuntimeException(
                    "Assigning 'Global Admin' privileges requires 'Global Admin' user");
            }
        // Must be an admin for this resource to assign permissions
//        } else if (dao.isAuthorized(realm, adminSubject, resource, Action.ADMIN)) {
//            // Handle request
//            logger.info("Authorizing Subject [" + adminSubject +
//                    "] has been authorized to updatePolicy with subject [" +
//                    subject + "], resource [" + resource +
//                    "] and action [" + action + "]");
//            dao.updatePolicy(realm, subject, resource, action);
        // Normal flow
        //   AuthorizingSubject must have greater or equal rights(action)
        //     than the rights(action) being given to subject
        } else if (dao.isAuthorized(realm, authorizingSubject, resource, action)) {
            // Handle request
            logger.info("Authorizing Subject [" + authorizingSubject +
                    "] has been authorized to updatePolicy with subject [" +
                    subject + "], resource [" + resource +
                    "] and action [" + action + "]");
            dao.updatePolicy(realm, subject, resource, action.value());
        } else {
            throw new RuntimeException(
                "Authorizing subject [" + authorizingSubject +
                "] does not have authorization to grant subject [" +
                subject + "] " + action + " access to resource [" +
                resource + "] in realm [" + realm + "]");
        }
    }

    @Override
    public void removePolicy(@NotNull Long policyKey, @NotNull String authorizingSubject) {
        logger.info("Authorizing Subject [" + authorizingSubject +
                "] is attempting to removePolicy using key [" + policyKey + "]");

        // Does the policy exist?
        EntitlementPolicy policy = getPolicy(policyKey);

        if (policy != null) {
            String subject = policy.getSubject();
            String realm = policy.getRealm();
            String resource = policy.getResource();

            // Is the user attempting to remove its own entitlement?
            if (subject.equals(authorizingSubject)) {
                throw new RuntimeException("Subject [" + subject +
                        "] cannot remove it's own entitlement policy");
            }

            // Get the action (permission level)
            Action action = policy.getAction();

            // Is the action/permission GLOBAL_ADMIN ?
            if (action == Action.GLOBAL_ADMIN) {
                // adminUser must be a global admin to authorize removal of a global admin
                if (dao.isAuthorized(realm, authorizingSubject, GLOBAL_ADMIN_RESOURCE,
                        Action.GLOBAL_ADMIN)) {
                    // remove global admin policy
                    logger.info("Authorizing Subject [" + authorizingSubject +
                        "] has been authorized to removePolicy with realm [" +
                            realm + "] subject [" +
                        subject + "], resource [" + GLOBAL_ADMIN_RESOURCE +
                        "] and action [" + action + "]");
                    dao.removePolicy(realm, subject, GLOBAL_ADMIN_RESOURCE, authorizingSubject);
                } else {
                    throw new RuntimeException(
                        "Removing 'Global Admin' privileges requires 'Global Admin' user");
                }
            // Must be an admin for this resource to remove permissions
            } else if (dao.isAuthorized(realm, authorizingSubject, resource, Action.ADMIN)) {
                // Remove policy
                logger.info("Authorizing Subject [" + authorizingSubject +
                        "] has been authorized to removePolicy with realm [" +
                        realm + "] subject [" +
                        subject + "], resource [" + resource + "]");
                dao.removePolicy(realm, subject, resource, authorizingSubject);
            } else {
                throw new RuntimeException(
                    "Subject [" + subject +
                    "] is not authorized to perform the operation RemovePolicy");
            }
        } else {
            logger.warn("Unable to remove policy using policyKey [" + policyKey +
                    "] because I could not find it");
        }
    }

    @Override
    public void removePolicy(@Nullable String realm, @NotNull String subject,
                             @NotNull String resource, @NotNull String authorizingSubject) {
        logger.info("Authorizing Subject [" + authorizingSubject +
                "] is attempting to removePolicy with subject [" +
                subject + "], resource [" + resource + "] in realm [" + realm + "]");

        if (realm == null || realm.isEmpty()) {
            logger.info("Unspecified realm [" + realm + "] defaulting to [" +
                DEFAULT_REALM + "]");
            realm = DEFAULT_REALM;
        }

        // Is the user attempting to remove its own entitlement?
        if (subject.equals(authorizingSubject)) {
            throw new RuntimeException("Subject [" + subject +
                    "] cannot remove it's own entitlement policy");
        }

        // Does the policy exist?
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        if (policy != null) {
        // Get the action (permission level)
            Action action = policy.getAction();

            // Is the action/permission GLOBAL_ADMIN ?
            if (action == Action.GLOBAL_ADMIN) {
                // adminUser must be a global admin to authorize removal of a global admin
                if (dao.isAuthorized(realm, authorizingSubject, GLOBAL_ADMIN_RESOURCE,
                        Action.GLOBAL_ADMIN)) {
                    // remove global admin policy
                    logger.info("Authorizing Subject [" + authorizingSubject +
                        "] has been authorized to removePolicy with realm [" +
                            realm + "] subject [" +
                        subject + "], resource [" + GLOBAL_ADMIN_RESOURCE +
                        "] and action [" + action + "]");
                    dao.removePolicy(realm, subject, GLOBAL_ADMIN_RESOURCE, authorizingSubject);
                } else {
                    throw new RuntimeException(
                        "Removing 'Global Admin' privileges requires 'Global Admin' user");
                }
            // Must be an admin for this resource to remove permissions
            } else if (dao.isAuthorized(realm, authorizingSubject, resource, Action.ADMIN)) {
                // Remove policy
                logger.info("Authorizing Subject [" + authorizingSubject +
                        "] has been authorized to removePolicy with realm [" +
                        realm + "] subject [" +
                        subject + "], resource [" + resource + "]");
                dao.removePolicy(realm, subject, resource, authorizingSubject);
            } else {
                throw new RuntimeException(
                    "Subject [" + subject +
                    "] is not authorized to perform the operation RemovePolicy");
            }
        } else {
            throw new RuntimeException("Unable to remove policy using subject [" +
                    subject + "] for resource [" + resource +
                    "] in realm [" + realm + "] because I could not find it");
        }
    }

    @Override
    @NotNull
    public List<EntitlementPolicy> getAllPolicies() {
        return convertPolicies(dao.getAllPolicies());
    }

    @Override
    @NotNull
    public List<EntitlementPolicy> getPolicyBySubject(
            @Nullable String realm, @NotNull String subject) {

        List<PolicyEntity> policyEntities;
        if (realm != null) {
            policyEntities = dao.getPoliciesBySubject(realm, subject);
        } else {
            policyEntities = dao.getPoliciesBySubject(subject);
        }
        return convertPolicies(policyEntities);
    }

    @Override
    @NotNull
    public List<EntitlementPolicy> getPolicyByResource(
            @Nullable String realm, @NotNull String resource) {
        if (realm == null || realm.isEmpty()) {
            logger.info("Unspecified realm [" + realm + "] defaulting to [" +
                DEFAULT_REALM + "]");
            realm = DEFAULT_REALM;
        }

        return convertPolicies(dao.getPoliciesByResource(realm, resource));
    }

    @Override
    @NotNull
    public List<EntitlementPolicy> getPolicyByRealm(@Nullable String realm) {
        if (realm == null || realm.isEmpty()) {
            logger.info("Unspecified realm [" + realm + "] defaulting to [" +
                DEFAULT_REALM + "]");
            realm = DEFAULT_REALM;
        }
        return convertPolicies(dao.getPoliciesByRealm(realm));
    }

    @Override
    @Nullable
    public EntitlementPolicy getPolicy(@NotNull Long policyKey) {
        EntitlementPolicy entitlementPolicy = null;
        PolicyEntity policyEntity = dao.getPolicy(policyKey);
        if (policyEntity == null) {
            logger.warn("Unable to retrieve policy using key [" + policyKey +
                "] because I could not find it.");
        } else {
            entitlementPolicy = convertPolicy(policyEntity);
        }
        return entitlementPolicy;
    }

    @Override
    @Nullable
    public EntitlementPolicy getPolicy(@Nullable String realm,
                                       @NotNull String subject,
                                       @NotNull String resource) {
        if (realm == null || realm.isEmpty()) {
            logger.info("Unspecified realm [" + realm + "] defaulting to [" +
                DEFAULT_REALM + "]");
            realm = DEFAULT_REALM;
        }

        EntitlementPolicy entitlementPolicy = null;
        PolicyEntity policyEntity = dao.getPolicy(realm, subject, resource);
        if (policyEntity == null) {
            // Check to see if this is a global_admin
            policyEntity = dao.getPolicy(realm, subject, GLOBAL_ADMIN_RESOURCE);
            if (policyEntity != null) {
                entitlementPolicy = convertPolicy(policyEntity);
                if (entitlementPolicy.getAction() != Action.GLOBAL_ADMIN) {
                    entitlementPolicy = null;
                }
            }
        } else {
            entitlementPolicy = convertPolicy(policyEntity);
        }
        return entitlementPolicy;
    }

    @Override
    @NotNull
    public Action getAuthorization(@Nullable String realm,
                                   @NotNull String subject,
                                   @NotNull String resource) {
        if (realm == null || realm.isEmpty()) {
            logger.info("Unspecified realm [" + realm + "] defaulting to [" +
                DEFAULT_REALM + "]");
            realm = DEFAULT_REALM;
        }

        Action action = Action.NONE;

        ActionEntity actionEntity = dao.getAuthorization(realm, subject, resource);
        if (actionEntity != null) {
            action = convertAction(actionEntity);
        } else {
            logger.warn("Unable to retrieve authorization for subject [" +
                subject + "] on resource [" + resource + "] in realm [" +
                realm + "]");
        }

        return action;
    }

    @Override
    public boolean isAuthorized(@Nullable String realm,
                                @NotNull String subject,
                                @NotNull String resource,
                                @NotNull Action action) {
        if (realm == null || realm.isEmpty()) {
            logger.info("Unspecified realm [" + realm + "] defaulting to [" +
                DEFAULT_REALM + "]");
            realm = DEFAULT_REALM;
        }

        return dao.isAuthorized(realm, subject, resource, action);
    }

    @Override
    public boolean isGlobalAdmin(@Nullable String realm, @NotNull String subject) {
        boolean isGlobalAdmin;
        if (realm == null || realm.isEmpty()) {
            isGlobalAdmin = dao.isGlobalAdmin(subject);
        } else {
            isGlobalAdmin = dao.isGlobalAdmin(realm, subject);
        }
        return isGlobalAdmin;
    }

    @Override
    @NotNull
    public List<Action> getActions() {
        return Arrays.asList(Action.values());
    }

    @Override
    @NotNull
    public List<String> getRealms() {
        return dao.getRealms();
    }

    @Override
    @NotNull
    public List<String> getSubjects() {
        return dao.getSubjects();
    }

    @Override
    @NotNull
    public List<String> getResources() {
        return dao.getResources();
    }

    /** Non-interface public methods **/

    /**
     * Dependency injection
     * @param dao EntitlementDao
     */
    public void setEntitlementDao(@NotNull EntitlementDao dao) {
        this.dao = dao;
    }

    /** Private methods **/

    /**
     * Converts an Action entity to an Action enum
     * @param actionEntity ActionEntity JPA object
     * @return An Action enum
     */
    @NotNull
    private Action convertAction(@NotNull ActionEntity actionEntity) {
        return Action.fromValue(actionEntity.getValue());
    }

    @NotNull
    private List<EntitlementPolicy> convertPolicies(
            @NotNull List<PolicyEntity> policyEntities) {
        List<EntitlementPolicy> entitlementPolicies = new ArrayList<EntitlementPolicy>();
        for (PolicyEntity policyEntity : policyEntities) {
            entitlementPolicies.add(convertPolicy(policyEntity));
        }

        return entitlementPolicies;
    }

    @NotNull
    private EntitlementPolicy convertPolicy(@NotNull PolicyEntity policyEntity) {
        EntitlementPolicy entitlementPolicy = new EntitlementPolicy();
        // Key
        entitlementPolicy.setKey(policyEntity.getID());
        // Realm
        String realm = policyEntity.getRealm();
        if (realm == null || realm.isEmpty()) {
            logger.info("Unspecified realm [" + realm + "] defaulting to [" +
                DEFAULT_REALM + "]");
            realm = DEFAULT_REALM;
        }
        entitlementPolicy.setRealm(policyEntity.getRealm());
        // Subject
        entitlementPolicy.setSubject(policyEntity.getSubject());
        // Resource
        entitlementPolicy.setResource(policyEntity.getResource());
        // Action
        Action action = Action.fromValue(policyEntity.getAction().getValue());
        entitlementPolicy.setAction(action);

        return entitlementPolicy;
    }

}
