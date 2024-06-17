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
package org.nterlearning.entitlement.persistence;

import org.apache.log4j.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;

import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Deringer
 * Date: 6/16/11
 * Time: 10:26 AM
 */
public class EntitlementDaoImpl
        extends JpaDaoSupport implements EntitlementDao {

    private static final String GLOBAL_ADMIN_RESOURCE = "*";
    private Logger logger = Logger.getLogger(EntitlementDaoImpl.class);


    @Override
    @Transactional
    public void createPolicy(
            @NotNull String realm,
            @NotNull String subject,
            @NotNull String resource,
            @NotNull String action) {

        PolicyEntity dbPolicy = getPolicy(realm, subject, resource);
        if (dbPolicy != null) {
            throw new RuntimeException(
                "An entitlement policy already exist with realm [" + realm +
                "] subject [" +
                subject + "] and resource [" + resource +
                "] please delete or update the existing policy");
        }

        try {
            // Create new policy
            PolicyEntity entitlementPolicy = new PolicyEntity();

            ActionEntity actionEntity = getActionEntityFromValue(action);
            if (actionEntity != null) {
                // Global Admin
                if (actionEntity.getValue().equals(Action.GLOBAL_ADMIN.value())) {
                    resource = GLOBAL_ADMIN_RESOURCE;
                }
            } else {
                throw new RuntimeException("Unable to find action [" +
                    action + "] DataInit has failed to populate data store");
            }

            entitlementPolicy.setRealm(realm);
            entitlementPolicy.setSubject(subject);
            entitlementPolicy.setResource(resource);
            entitlementPolicy.setAction(actionEntity);

            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(entitlementPolicy);
        } catch (Exception e) {
            throw new RuntimeException("Error persisting Entitlement Policy", e);
        }
    }

    @Override
    @Transactional
    public void updatePolicy(
            @NotNull String realm,
            @NotNull String subject,
            @NotNull String resource,
            @NotNull String action) {

        PolicyEntity entitlementPolicy = getPolicy(realm, subject, resource);
        if (entitlementPolicy == null) {
            throw new RuntimeException(
                    "Unable to update Entitlement Policy using realm [" +
                     realm + "] subject [" +
                        subject + "] and resource [" + resource +
                            "] because I could not find it.");
        }

        try {
            entitlementPolicy.setAction(getActionEntityFromValue(action));

            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(entitlementPolicy);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating EntitlementPolicy using subject [" +
                    subject + "] and resource [" + resource + "]", e);
        }
    }

    @Override
    @Transactional
    public void removePolicy(
            @NotNull String realm,
            @NotNull String subject,
            @NotNull String resource,
            @NotNull String authorizingSubject) {

        // Subject cannot remove themselves
        if (subject.equals(authorizingSubject)) {
            logger.warn("A Subject cannot remove themselves [" + subject + "]");
            return;
        }

        // Retrieve policy to be removed
        PolicyEntity entitlementPolicy = getPolicy(realm, subject, resource);
        // Is this a global admin to be removed?
        if (entitlementPolicy == null && isGlobalAdmin(subject)) {
            resource = GLOBAL_ADMIN_RESOURCE;
            entitlementPolicy = getPolicy(realm, subject, resource);
        }

        // Policy does not exist
        if (entitlementPolicy == null) {
            throw new RuntimeException(
                    "Unable to remove Entitlement Policy using realm [" +
                    realm + "] subject [" +
                        subject + "] and resource [" + resource +
                    "] because I could not find it");
        }

        // Remove the policy
        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.remove(entitlementPolicy);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error removing EntitlementPolicy using subject [" +
                    subject + "] and resource [" + resource + "]", e);
        }
    }

    @Override
    @Transactional
    public void removePolicy(@NotNull Long policyKey,
                             @NotNull String authorizingSubject) {

        PolicyEntity policyEntity = getPolicy(policyKey);
        if (policyEntity == null) {
            logger.warn("Unable to remove policy using key [" + policyKey +
                " because I could not find it.");
        } else {
            String subject = policyEntity.getSubject();

            // Subject cannot remove themselves
            if (!subject.equals(authorizingSubject)) {
                // Remove the policy
                try {
                    JpaTemplate jpaTemplate = getJpaTemplate();
                    jpaTemplate.remove(policyEntity);
                } catch (Exception e) {
                    throw new RuntimeException(
                        "Error removing EntitlementPolicy using policyKey [" +
                            policyKey + "]", e);
                }
            } else {
                logger.warn("A Subject cannot remove themselves [" + subject + "]");
            }
        }
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<PolicyEntity> getAllPolicies() {
        List<PolicyEntity> policyList;
        try {
            policyList =
                getJpaTemplate().find(
                        "select o from PolicyEntity o order by o.resource, o.subject");
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving EntitlementPolicy", e);
        }
        return policyList;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<PolicyEntity> getPoliciesBySubject(String realm, String subject) {
        List<PolicyEntity> policyList;
        try {
            policyList =
                getJpaTemplate().find(
                        "select o from PolicyEntity o where o.realm = ?1 and o.subject = ?2 order by o.resource, o.subject",
                        realm, subject);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving EntitlementPolicy for subject [" +
                    subject + "]", e);
        }
        return policyList;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<PolicyEntity> getPoliciesBySubject(String subject) {
        List<PolicyEntity> policyList;
        try {
            policyList =
                getJpaTemplate().find(
                        "select o from PolicyEntity o where o.subject = ?1 order by o.resource, o.subject",
                        subject);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving EntitlementPolicy for subject [" +
                    subject + "]", e);
        }
        return policyList;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<PolicyEntity> getPoliciesByResource(
            @NotNull String realm, @NotNull String resource) {
        List<PolicyEntity> policyList;
        try {
            policyList =
                getJpaTemplate().find(
                        "select o from PolicyEntity o where o.realm = ?1 and o.resource = ?2 order by o.resource, o.subject",
                        realm, resource);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving EntitlementPolicy for resource [" +
                    resource + "]", e);
        }
        return policyList;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<PolicyEntity> getPoliciesByRealm(@NotNull String realm) {
        List<PolicyEntity> policyList;
        try {
            policyList =
                getJpaTemplate().find(
                        "select o from PolicyEntity o where o.realm = ?1 order by o.resource, o.subject",
                        realm);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving EntitlementPolicy for realm [" +
                    realm + "]", e);
        }
        return policyList;
    }

    @Override
    @Nullable
    public PolicyEntity getPolicy(@NotNull Long policyKey) {
        PolicyEntity policy = null;
        try {
            @SuppressWarnings("unchecked")
            List<PolicyEntity> policyList = getJpaTemplate()
                .find(
                    "select o from PolicyEntity o where o.id = ?1", policyKey);
            if (policyList != null && !policyList.isEmpty()) {
                policy = policyList.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving EntitlementPolicy using key [" +
                        policyKey + "]", e);
        }
        return policy;
    }

    @Override
    @Nullable
    public PolicyEntity getPolicy(
            @NotNull String realm,
            @NotNull String subject,
            @NotNull String resource) {

        PolicyEntity policy = null;
        try {
            @SuppressWarnings("unchecked")
            List<PolicyEntity> policyList = getJpaTemplate()
                .find(
                        "select o from PolicyEntity o where o.realm = ?1 and o.subject = ?2 and o.resource = ?3",
                        realm, subject, resource);
            if (policyList != null && !policyList.isEmpty()) {
                policy = policyList.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving EntitlementPolicy for realm [" + realm +
                "] subject [" + subject + "] and resource [" + resource + "]", e);
        }
        return policy;
    }

    @Override
    @Nullable
    public ActionEntity getAuthorization(
            @NotNull String realm,
            @NotNull String subject,
            @NotNull String resource) {

        ActionEntity authorization = null;
        if (isGlobalAdmin(subject)) {
            authorization = getActionEntityFromValue(Action.GLOBAL_ADMIN);
        } else {
            try {
                @SuppressWarnings("unchecked")
                List<ActionEntity> authStatusList = getJpaTemplate()
                    .find(
                            "select o.action from PolicyEntity o where o.realm = ?1 and o.subject = ?2 and o.resource = ?3",
                            realm, subject, resource);
                if (authStatusList != null && !authStatusList.isEmpty()) {
                    authorization = authStatusList.get(0);
                }
            } catch (Exception e) {
                throw new RuntimeException(
                    "Error retrieving EntitlementPolicy for realm [" + realm +
                    "] subject [" + subject + "] and resource [" + resource + "]", e);
            }
        }

        return authorization;
    }

    @Override
    public boolean isAuthorized(@NotNull String realm, @NotNull String subject,
                                @NotNull String resource, @NotNull Action permission) {

        boolean isAuthorized = false;
        Action action = getAuthorizationAction(realm, subject, resource);
        if (action.ordinal() >= permission.ordinal()) {
            isAuthorized = true;
        }

        return isAuthorized;
    }

    @Override
    public boolean isGlobalAdmin(@NotNull String subject) {
        boolean isGlobalAdmin = false;
        String resource = GLOBAL_ADMIN_RESOURCE;
        try {
            @SuppressWarnings("unchecked")
            List<ActionEntity> authStatusList = getJpaTemplate()
                .find(
                        "select p.action from PolicyEntity p where p.subject = ?1 and p.resource = ?2",
                        subject, resource);
            for (ActionEntity action : authStatusList) {
                if (action.getValue().equals(Action.GLOBAL_ADMIN.value())) {
                    isGlobalAdmin = true;
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving EntitlementPolicy for subject [" +
                    subject + "] and resource [" + resource + "]", e);
        }

        return isGlobalAdmin;
    }

    @Override
    public boolean isGlobalAdmin(@NotNull String realm, @NotNull String subject) {
        boolean isGlobalAdmin = false;
        String resource = GLOBAL_ADMIN_RESOURCE;
        try {
            @SuppressWarnings("unchecked")
            List<ActionEntity> authStatusList = getJpaTemplate()
                .find(
                        "select p.action from PolicyEntity p where p.realm = ?1 and p.subject = ?2 and p.resource = ?3",
                        realm, subject, resource);
            for (ActionEntity action : authStatusList) {
                if (action.getValue().equals(Action.GLOBAL_ADMIN.value())) {
                    isGlobalAdmin = true;
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving EntitlementPolicy for subject [" +
                    subject + "] and resource [" + resource +
                        "] in realm [" + realm + "]", e);
        }

        return isGlobalAdmin;
    }

    @Override
    @Transactional
    public void addAction(@NotNull String action) {
        try {
            // Create new action
            ActionEntity actionEntity = new ActionEntity();
            actionEntity.setValue(action);

            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(actionEntity);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error persisting ActionEntity [" + action + "]", e);
        }
    }

    @Override
    @Nullable
    public ActionEntity getAction(@NotNull String value) {
        ActionEntity actionEntity = null;
        try {
            @SuppressWarnings("unchecked")
            List<ActionEntity> actionEntities = getJpaTemplate()
                .find(
                        "select o from ActionEntity o where o.value = ?1",
                        value);
            if (actionEntities != null && !actionEntities.isEmpty()) {
                actionEntity = actionEntities.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving ActionEntity [" + value + "]", e);
        }
        return actionEntity;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<ActionEntity> getActions() {
        return getJpaTemplate().find(
                    "select o from ActionEntity o");
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<String> getSubjects() {
        return getJpaTemplate().find(
                    "select distinct o.subject from PolicyEntity o");
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<String> getResources() {
        return getJpaTemplate().find(
                    "select distinct o.resource from PolicyEntity o");
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<String> getRealms() {
        return getJpaTemplate().find(
                    "select distinct o.realm from PolicyEntity o");
    }

    @Nullable
    private ActionEntity getActionEntityFromValue(@NotNull Action action) {
        return getActionEntityFromValue(action.value());
    }

    @Nullable
    private ActionEntity getActionEntityFromValue(@NotNull String value) {
        ActionEntity action = null;
        try {
            @SuppressWarnings("unchecked")
            List<ActionEntity> actions = getJpaTemplate()
                .find(
                        "select o from ActionEntity o where o.value = ?1", value);
            if (actions != null && !actions.isEmpty()) {
                action = actions.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving ActionEntity for value [" + value + "]", e);
        }
        return action;
    }

    @NotNull
    private Action getAuthorizationAction(
            @NotNull String realm, @NotNull String subject, @NotNull String resource) {

        Action authorization = Action.NONE;

        ActionEntity actionEntity
                = getAuthorization(realm, subject, resource);

        if (actionEntity != null) {
            authorization = Action.fromValue(actionEntity.getValue());
        }

        return authorization;
    }

}
