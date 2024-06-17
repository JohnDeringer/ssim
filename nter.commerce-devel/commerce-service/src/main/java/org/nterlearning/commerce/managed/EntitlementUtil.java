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
package org.nterlearning.commerce.managed;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.commerce.controller.AuthController;
import org.nterlearning.entitlement.client.*;
import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;
import org.nterlearning.usermgmt.service.jaxws.UserList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/24/12
 */
@ManagedBean(name = "entitlementUtil")
public class EntitlementUtil implements Serializable {

    @ManagedProperty("#{authController}")
    private AuthController authController;

    @ManagedProperty("#{entitlementService}")
	private EntitlementService entitlementService;

    @ManagedProperty("#{identityService}")
	private IdentityService identityService;

    @ManagedProperty("#{realmBean}")
    private RealmBean realmBean;

    private static final String DEFAULT_REALM = "/";
    private static final String GLOBAL_RESOURCE = "*";

    private Logger logger = LoggerFactory.getLogger(EntitlementUtil.class);

    @NotNull
    public List<Subject> getSubjects() {
        List<Subject> subjects = new ArrayList<Subject>();

        try {
            UserList userList = identityService.getUsers();
            for (UserImpl user : userList.getList()) {
                Subject subject = new Subject();
                subject.setSubjectId(user.getUid());
                subject.setEmail(user.getEmail());
                subjects.add(subject);
            }
        } catch (Exception e) {
            logger.error("Unexpected exception calling IdentityService.getUsers", e);
        }

        return subjects;
    }

    @NotNull
    public Action getPermission() {
        String uid = authController.getUID();
        return getPermission(uid);
    }

    @NotNull
    public Action getPermission(@NotNull String subject) {
        Action action = Action.READ;
        List<EntitlementPolicy> policies =
                entitlementService.getPoliciesBySubject(
                        realmBean.getCommerceRealm(), subject);
        for (EntitlementPolicy policy : policies) {
            logger.debug("EntitlementPolicy.action [" + policy.getAction() +
                    "] ordinal [" + policy.getAction().ordinal() + "]");
            if (action.ordinal() < policy.getAction().ordinal()) {
                action = policy.getAction();
            }
        }

        return action;
    }

    // TODO: replace ordinal comparison with rule
    @NotNull
    public List<Action> getActions(@NotNull Action refAction) {
        List<Action> rtnActions = new ArrayList<Action>();
        for (Action action : entitlementService.getActions()) {
            logger.debug("Logged-in user permission [" + refAction.value() + "]");
            if (action.ordinal() <= refAction.ordinal()) {
                logger.debug("Available action for issuance[" + action.value() + "]");
                rtnActions.add(action);
            }
        }

        return rtnActions;
    }

    public boolean isAdmin() {
        boolean isAdmin = false;
        String uid = authController.getUID();

        List<EntitlementPolicy> policies =
                entitlementService.getPoliciesBySubject(realmBean.getCommerceRealm(), uid);

        logger.debug("Searching for entitlement policies for [" + uid + "]");
        for (EntitlementPolicy policy : policies) {
            logger.debug("Found entitlement policy with Realm [" + policy.getRealm() +
                    "] resource [" + policy.getResource() +
                    "] subject [" + policy.getSubject() +
                    "] action [" + policy.getAction() + "]");
            if (policy.getAction() == Action.GLOBAL_ADMIN) {
                isAdmin = true;
                break;
            } else if (policy.getAction() == Action.ADMIN &&
                    policy.getRealm().equals(realmBean.getCommerceRealm())) {
                isAdmin = true;
                break;
            }
        }

        return isAdmin;
    }

    public boolean isAdmin(@NotNull String resource) {
        return isAdmin(authController.getUID(), resource);
    }

    public boolean isAdmin(@NotNull String uid, @NotNull String resource) {
        boolean isAdmin = false;

        List<EntitlementPolicy> policies =
                getPoliciesRelatedToSubject(uid, resource);
        logger.debug("Searching for entitlement policies for [" + uid + "]");
        for (EntitlementPolicy policy : policies) {
            logger.debug("Found entitlement policy with Realm [" + policy.getRealm() +
                    "] resource [" + policy.getResource() +
                    "] subject [" + policy.getSubject() +
                    "] action [" + policy.getAction() + "]");
            if (policy.getAction() == Action.GLOBAL_ADMIN) {
                isAdmin = true;
                break;
            } else if (policy.getAction() == Action.ADMIN &&
                    policy.getRealm().equals(realmBean.getCommerceRealm()) &&
                    policy.getResource().equals(resource)) {
                isAdmin = true;
                break;
            }
        }

        return isAdmin;
    }

    @Nullable
    public EntitlementPolicy getPolicy(@NotNull String entitlementKey) {
        EntitlementPolicy policy = null;
        Long policyKey;
        if (!entitlementKey.isEmpty()) {
            try {
                policyKey = Long.parseLong(entitlementKey);
                policy = getPolicy(policyKey);
            } catch (NumberFormatException e) {
                logger.error("Unexpected error creating policyKey", e);
            }
        }

        return policy;
    }

    @Nullable
    public EntitlementPolicy getPolicy(@NotNull Long policyKey) {
        return entitlementService.getPolicy(policyKey);
    }

    @NotNull
    public List<EntitlementPolicy> getPoliciesBySubject() {
        return getPoliciesBySubject(authController.getUID());
    }

    @NotNull
    public List<EntitlementPolicy> getPoliciesBySubject(@NotNull String subject) {
        logger.debug("getPoliciesBySubject subject [" + subject + "] realm [" +
            realmBean.getCommerceRealm() + "]");

        List<EntitlementPolicy> policies;
        if (isGlobalAdmin()) {
            policies =
                    entitlementService.getPoliciesByRealm(
                            realmBean.getCommerceRealm()
                    );
        } else {
            policies =
                    entitlementService.getPoliciesBySubject(
                        realmBean.getCommerceRealm(), subject
                    );
        }

        return policies;
    }

    @NotNull
    public List<EntitlementPolicy> getPoliciesRelatedToSubject() {
        return getPoliciesRelatedToSubject(authController.getUID());
    }

    @NotNull
    public List<EntitlementPolicy> getPoliciesRelatedToSubject(
            @NotNull String subject) {
        return getPoliciesRelatedToSubject(subject, GLOBAL_RESOURCE);
    }

    @NotNull
    public List<EntitlementPolicy> getPoliciesRelatedToSubject(
            @NotNull String subject, @NotNull String resource) {
        List<EntitlementPolicy> policies = new ArrayList<EntitlementPolicy>();

        logger.info("Searching for entitlement policies related to Subject [" +
                subject + "] resource [" + resource + "] realm [" +
                realmBean.getCommerceRealm() + "]");

        //Is this a global admin uid?
        if (isGlobalAdmin()) {
            logger.debug("GlobalAdmin is requesting policies for realm [" +
                realmBean.getCommerceRealm() + "]");
            if (getEntitlementService() != null) {
                policies =
                    entitlementService.getPoliciesByRealm(
                        realmBean.getCommerceRealm()
                    );

                // Add the Global Admin policy
                EntitlementPolicy policy =
                    entitlementService.getPolicy(DEFAULT_REALM, subject, GLOBAL_RESOURCE);
                if (policy != null) {
                    policies.add(policy);
                }

            } else {
                logger.error("Unable to contact entitlement service [" + getEntitlementService() + "]");
            }
        } else {
            // GLOBAL_RESOURCE is to support the entitlements list UI
            if (resource.equals(GLOBAL_RESOURCE)) {
                List<EntitlementPolicy> subjectPolicies =
                        entitlementService.getPoliciesBySubject(
                                realmBean.getCommerceRealm(), subject);
                for (EntitlementPolicy subjectPolicy : subjectPolicies) {
                    // If the user is an Admin, add all polices under the related resource
                    if (subjectPolicy.getAction() == Action.ADMIN) {
                        // Retrieve all policies under resource
                        List<EntitlementPolicy> resourcePolicies =
                                entitlementService.getPoliciesByResource(
                                        realmBean.getCommerceRealm(), subjectPolicy.getResource());
                        for (EntitlementPolicy resourcePolicy : resourcePolicies) {
                            if (!policies.contains(resourcePolicy)) {
                                policies.add(resourcePolicy);
                            }
                        }
                    } else {
                        if (!policies.contains(subjectPolicy)) {
                            policies.add(subjectPolicy);
                        }
                    }
                }
            } else {
                EntitlementPolicy policy =
                        entitlementService.getPolicy(
                                realmBean.getCommerceRealm(), subject, resource);
                if (policy != null && policy.getAction() == Action.ADMIN) {
                    // Retrieve all policies under resource assigned to the logged-in admin.
                    List<EntitlementPolicy> resourcePolicies =
                            entitlementService.getPoliciesByResource(
                                    realmBean.getCommerceRealm(), resource);
                    for (EntitlementPolicy resourcePolicy : resourcePolicies) {
                        if (!policies.contains(resourcePolicy)) {
                            policies.add(resourcePolicy);
                        }
                    }
                } else {
                    if (!policies.contains(policy)) {
                        policies.add(policy);
                    }
                }
            }
        }
        return policies;
    }

    public void removePolicy(@NotNull Long policyKey) {
        entitlementService.removePolicy(policyKey);
    }

    public void updatePolicy(@NotNull String subject,
                             @NotNull String resource,
                             @NotNull String permission) {
        try {
            Action action = Action.fromValue(permission);
            updatePolicy(
                    subject, resource, action);
        } catch (Exception e) {
            logger.error("Error converting String to Action", e);
        }
    }

    public void updatePolicy(@NotNull String subject,
                             @NotNull String resource,
                             @NotNull Action action) {
        entitlementService.updatePolicy(
                realmBean.getCommerceRealm(), subject, resource, action);
    }

    public void createPolicy(@NotNull String subject,
                             @NotNull String resource,
                             @NotNull String permission) {
        try {
            Action action = Action.fromValue(permission);
            createPolicy(
                    subject, resource, action);
        } catch (Exception e) {
            logger.error("Error converting String to Action", e);
        }
    }

    public void createPolicy(@NotNull String subject,
                             @NotNull String resource,
                             @NotNull Action action) {
        entitlementService.createPolicy(
                realmBean.getCommerceRealm(), subject, resource, action);
    }


    public boolean hasReadAccess(@NotNull String resource) {
        String uid = authController.getUID();

        Action action =
                entitlementService.getAuthorization(
                        realmBean.getCommerceRealm(), uid, resource);

        logger.debug("hasReadAccess uid [" + uid + "] resource [" + resource +
            "] action [" + action + "]");

        return (action == Action.GLOBAL_ADMIN ||
                action == Action.ADMIN ||
                action == Action.WRITE ||
                action == Action.READ);
    }

    public boolean hasWriteAccess(@NotNull String resource) {
        String uid = authController.getUID();
        Action action =
                entitlementService.getAuthorization(
                        realmBean.getCommerceRealm(), uid, resource);

        return (action == Action.GLOBAL_ADMIN ||
                action == Action.ADMIN ||
                action == Action.WRITE);
    }

    public boolean hasAdminAccess(@NotNull String resource) {
        String uid = authController.getUID();
        Action action =
                entitlementService.getAuthorization(
                        realmBean.getCommerceRealm(), uid, resource);

        return (action == Action.GLOBAL_ADMIN ||
                action == Action.ADMIN);
    }

    public boolean isGlobalAdmin() {
        String uid = authController.getUID();
        return entitlementService.isGlobalAdmin(
                realmBean.getCommerceRealm(), uid);
    }

    public AuthController getAuthController() {
        return authController;
    }
    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    public EntitlementService getEntitlementService() {
        return entitlementService;
    }
    public void setEntitlementService(EntitlementService entitlementService) {
        this.entitlementService = entitlementService;
    }

    public RealmBean getRealmBean() {
        return realmBean;
    }
    public void setRealmBean(RealmBean realmBean) {
        this.realmBean = realmBean;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }
    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public String getEmail() {
        return authController.getEmail();
    }
}
