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
package org.nterlearning.registry.managed;

import org.jetbrains.annotations.NotNull;

import org.nterlearning.entitlement.client.*;
import org.nterlearning.registry.controller.AuthController;

import org.nterlearning.registry.model.RegistryModel;

import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;

import org.nterlearning.usermgmt.service.jaxws.UserList;
import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.Institution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/30/12
 */
@ManagedBean(name = "entitlementUtil")
public class EntitlementUtil {
    @ManagedProperty("#{authController}")
    private AuthController authController;

    @ManagedProperty("#{entitlementService}")
	private Entitlement entitlementService;

    @ManagedProperty("#{registryModel}")
    private RegistryModel registry;

    @ManagedProperty("#{identityService}")
    private IdentityService identityService;

    @ManagedProperty("#{realmBean}")
    private RealmBean realmBean;

    private List<Subject> subjects = null;
    private List<EntitlementPolicy> policies = null;

    private String registryRealm = null;
    private String commerceRealm = null;

    private static final String DEFAULT_REALM = "/";
    private static final String GLOBAL_RESOURCE = "*";

    private Logger logger = LoggerFactory.getLogger(EntitlementUtil.class);

    @PostConstruct
    private void init() {
        if (realmBean != null) {
            registryRealm = realmBean.getRegistryRealm();
            commerceRealm = realmBean.getCommerceRealm();
        } else {
            logger.error("Unable to determine realms, invalid realmBean [" +
                realmBean + "]");
        }
    }

    public boolean hasReadAccess(@NotNull String resource) {
        String uid = authController.getUID();

        Action action = entitlementService.getAuthorization(registryRealm, uid, resource);

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
            entitlementService.getAuthorization(registryRealm, uid, resource);

        logger.debug("Action [" + action + "] returned for subject [" +
            uid + "] on resource [" + resource + "] in realm [" + registryRealm + "]");

        return (action == Action.GLOBAL_ADMIN ||
                action == Action.ADMIN ||
                action == Action.WRITE);
    }

    public boolean hasAdminAccess(@NotNull String resource) {
        String uid = authController.getUID();
        Action action = entitlementService.getAuthorization(registryRealm, uid, resource);

        return (action == Action.GLOBAL_ADMIN ||
                action == Action.ADMIN);
    }

    // TODO: GLOBAL_RESOURCE? institution?
    public boolean isAdmin() {
        String uid = authController.getUID();
        return isAdmin(uid, GLOBAL_RESOURCE);
    }

    public boolean isAdmin(@NotNull String resource) {
        boolean isAdmin = false;
        String uid = authController.getUID();

        List<EntitlementPolicy> policies =  getPoliciesRelatedToSubject(uid, resource);
        logger.debug("Searching for entitlement policies for [" + uid + "]");
        for (EntitlementPolicy policy : policies) {
            logger.debug("Found entitlement policy with Realm [" + policy.getRealm() +
                    "] resource [" + policy.getResource() +
                    "] subject [" + policy.getSubject() +
                    "] action [" + policy.getAction() + "]");
            if (policy.getAction() == Action.ADMIN ||
                    policy.getAction() == Action.GLOBAL_ADMIN) {
                isAdmin = true;
                break;
            }
        }

        return isAdmin;
    }

    public boolean isAdmin(@NotNull String uid, @NotNull String resource) {
        boolean isAdmin = false;

        List<EntitlementPolicy> policies =  getPoliciesRelatedToSubject(uid, resource);
        logger.debug("Searching for entitlement policies for [" + uid + "]");
        for (EntitlementPolicy policy : policies) {
            logger.debug("Found entitlement policy with Realm [" + policy.getRealm() +
                    "] resource [" + policy.getResource() +
                    "] subject [" + policy.getSubject() +
                    "] action [" + policy.getAction() + "]");
            if (policy.getAction() == Action.ADMIN ||
                    policy.getAction() == Action.GLOBAL_ADMIN) {
                isAdmin = true;
                break;
            }
        }

        return isAdmin;
    }

    // TODO: replace ordinal comparison with rules
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

    // Return the highest Action for the logged-in user
    public Action getPermission() {
        Action action = Action.READ;
        String uid = authController.getUID();
        List<EntitlementPolicy> policies =
                entitlementService.getPoliciesBySubject(registryRealm, uid);
        for (EntitlementPolicy policy : policies) {
            logger.debug("EntitlementPolicy.action [" + policy.getAction() +
                    "] ordinal [" + policy.getAction().ordinal() + "]");
            if (action.ordinal() < policy.getAction().ordinal()) {
                action = policy.getAction();
            }
        }

        return action;
    }

    public List<Subject> getSubjects() {
        if (subjects == null) {
            subjects = new ArrayList<Subject>();
        }

        if (subjects.isEmpty()) {
            UserList userList = null;
            try {
                userList = identityService.getUsers();
            } catch (Exception e) {
                logger.error("Unexpected exception calling IdentityService.getUsers", e);
            }
            if (userList != null) {
                for (UserImpl user : userList.getList()) {
                    if (user != null) {
                        String email = user.getEmail();
                        if (email != null && !email.isEmpty()) {
                            Subject subject = new Subject();
                            subject.setSubjectId(user.getUid());
                            subject.setEmail(email);

                            subjects.add(subject);
                        } else {
                            logger.warn("Cannot add user [" + user.getUid() +
                                    "] to the registry subject list because the user has no email address");
                        }
                    }
                }
            } else {
                logger.error("UserManagement getUsers returned [" + userList + "]");
            }
        }

        return subjects;
    }

    public List<String> getSubjectIds() {
        List<String> subjects = new ArrayList<String>();
        if (entitlementService != null) {
            try {
                subjects = entitlementService.getSubjects();
            } catch (Exception e) {
                logger.error("Unexpected error calling entitlement service", e);
            }
        } else {
            logger.error(
                    "Invalid reference to Entitlement Service [" + entitlementService + "]");
        }
        return subjects;
    }

    public List<String> getInstitutions() {
        String uid = authController.getUID();
        List<String> institutionNames = new ArrayList<String>();
        logger.debug("Checking entitlement for subject [" + uid + "]");

        if (isGlobalAdmin()) {
            logger.info("Subject [" + uid + "] is a Global Admin");
            // Get active institutions
            List<Institution> institutions =
                    registry.getInstitutions(ActiveStatusEnum.ACTIVE);
            // Add inactive institutions
            institutions.addAll(registry.getInstitutions(ActiveStatusEnum.INACTIVE));
            for (Institution institution : institutions) {
                institutionNames.add(BeanUtil.escape(institution.getName()));
            }
            logger.debug("Found [" + institutionNames.size() + "] institutions");
        } else {
            List<EntitlementPolicy> policies = getPoliciesBySubject(uid);
            for (EntitlementPolicy policy : policies) {
                if (policy.getAction() == Action.ADMIN ||
                        policy.getAction() == Action.GLOBAL_ADMIN) {
                    institutionNames.add(policy.getResource());
                }
            }
        }

        return institutionNames;
    }

    public EntitlementPolicy getPolicy(@NotNull Long policyKey) {
        EntitlementPolicy policy = null;
        if (entitlementService != null) {
            try {
                policy = entitlementService.getPolicy(policyKey);
            } catch (Exception e) {
                logger.error("Unexpected error calling entitlement service", e);
            }
        } else {
            logger.error(
                    "Invalid reference to Entitlement Service [" + entitlementService + "]");
        }
        return policy;
    }

    public List<EntitlementPolicy> getPoliciesBySubject(@NotNull String user) {
        List<EntitlementPolicy> policies = new ArrayList<EntitlementPolicy>();
        if (entitlementService != null) {
            try {
                policies = entitlementService.getPoliciesBySubject(registryRealm, user);
            } catch (Exception e) {
                logger.error("Unexpected error calling entitlement service", e);
            }
        } else {
            logger.error(
                    "Invalid reference to Entitlement Service [" + entitlementService + "]");
        }
        return policies;
    }

    public List<EntitlementPolicy> getPoliciesByResource(
            @NotNull String institution) {
        List<EntitlementPolicy> policies = new ArrayList<EntitlementPolicy>();
        if (entitlementService != null) {
            try {
                policies = entitlementService.getPoliciesByResource(registryRealm, institution);
            } catch (Exception e) {
                logger.error("Unexpected error calling entitlement service", e);
            }
        } else {
            logger.error(
                    "Invalid reference to Entitlement Service [" + entitlementService + "]");
        }
        return policies;
    }

    public List<EntitlementPolicy> getCommercePoliciesByResource(
            @NotNull String institution) {
        List<EntitlementPolicy> policies = new ArrayList<EntitlementPolicy>();
        if (entitlementService != null) {
            try {
                policies = entitlementService.getPoliciesByResource(commerceRealm, institution);
            } catch (Exception e) {
                logger.error("Unexpected error calling entitlement service", e);
            }
        } else {
            logger.error(
                    "Invalid reference to Entitlement Service [" + entitlementService + "]");
        }
        return policies;
    }

    public List<EntitlementPolicy> getPoliciesRelatedToSubject(
            @NotNull String subject) {
        return getPoliciesRelatedToSubject(subject, GLOBAL_RESOURCE);
    }

    public List<EntitlementPolicy> getPoliciesRelatedToSubject(
            @NotNull String subject, @NotNull String resource) {

        if (policies == null) {
            policies = new ArrayList<EntitlementPolicy>();
        }
        logger.info("Searching for entitlement policies related to Subject [" +
                subject + "] resource [" + resource + "] realm [" + registryRealm + "]");

        if (policies.isEmpty()) {
            // Is this a global admin uid?
            if (isGlobalAdmin()) {
                // Get all policies for the registry realm
                policies = entitlementService.getPoliciesByRealm(registryRealm);
                // Add Global admin policy
                EntitlementPolicy policy =
                        entitlementService.getPolicy(DEFAULT_REALM, subject, GLOBAL_RESOURCE);
                if (policy != null) {
                    policies.add(policy);
                } else {
                    logger.warn("Unable to find policy for subject [" + subject +
                        "] with resource [" + GLOBAL_RESOURCE + "] in realm [" + DEFAULT_REALM + "]");
                }
            } else {
                // GLOBAL_RESOURCE is to support the entitlements list UI
                if (resource.equals(GLOBAL_RESOURCE)) {
                    List<EntitlementPolicy> subjectPolicies =
                            entitlementService.getPoliciesBySubject(registryRealm, subject);
                    logger.debug("Retrieving policies for subject [" +
                                subject + "] in realm [" + registryRealm + "]");
                    for (EntitlementPolicy subjectPolicy : subjectPolicies) {
                        // If the user is an Admin, add all polices under the related resource
                        if (subjectPolicy.getAction() == Action.ADMIN) {
                            // Retrieve all policies under resource
                            List<EntitlementPolicy> resourcePolicies =
                                    entitlementService.getPoliciesByResource(
                                            registryRealm, subjectPolicy.getResource());
                            logger.debug("Retrieving policies for resource [" +
                                subjectPolicy.getResource() + "] in realm [" +
                                    registryRealm + "]");
                            for (EntitlementPolicy resourcePolicy : resourcePolicies) {
                                if (!policies.contains(resourcePolicy)  &&
                                    resourcePolicy.getRealm().equals(registryRealm)) {
                                    logger.debug("(1)Adding policy for subject [" +
                                        resourcePolicy.getSubject() +
                                            "] on resource [" + resourcePolicy.getResource() +
                                            "] in realm [" + resourcePolicy.getRealm() + "]");
                                    policies.add(resourcePolicy);
                                }
                            }
                        } else {
                            if (!policies.contains(subjectPolicy) &&
                                    subjectPolicy.getRealm().equals(registryRealm)) {
                                logger.debug("(2)Adding policy for subject [" +
                                        subjectPolicy.getSubject() +
                                            "] on resource [" + subjectPolicy.getResource() +
                                            "] in realm [" + subjectPolicy.getRealm() + "]");
                                policies.add(subjectPolicy);
                            }
                        }
                    }
                } else {
                    EntitlementPolicy policy =
                            entitlementService.getPolicy(registryRealm, subject, resource);
                    logger.debug("Retrieving policy for subject [" + subject +
                            "] on resource [" +
                                resource + "] in realm [" + registryRealm + "]");
                    if (policy.getAction() == Action.ADMIN) {
                        // Retrieve all policies under resource assigned to the logged-in admin.
                        List<EntitlementPolicy> resourcePolicies =
                                entitlementService.getPoliciesByResource(
                                        registryRealm, resource);
                        logger.debug("Retrieving policies for resource [" +
                                resource + "] in realm [" + registryRealm + "]");
                        for (EntitlementPolicy resourcePolicy : resourcePolicies) {
                            if (!policies.contains(resourcePolicy) &&
                                    resourcePolicy.getRealm().equals(registryRealm)) {
                                logger.debug("(3)Adding policy for subject [" +
                                        resourcePolicy.getSubject() +
                                            "] on resource [" + resourcePolicy.getResource() +
                                            "] in realm [" + resourcePolicy.getRealm() + "]");
                                policies.add(resourcePolicy);
                            }
                        }
                    } else {
                        if (!policies.contains(policy) &&
                                    policy.getRealm().equals(registryRealm)) {
                            logger.debug("(4)Adding policy for subject [" +
                                        policy.getSubject() +
                                            "] on resource [" + policy.getResource() +
                                            "] in realm [" + policy.getRealm() + "]");
                            policies.add(policy);
                        }
                    }
                }
            }
        }
        return policies;
    }

    public void createPolicy(@NotNull String institution, @NotNull String permission) {
        String uid = authController.getUID();
        createPolicy(uid, institution, permission);
    }

    public void createPolicy(
            @NotNull String user, @NotNull String institution, @NotNull String permission) {

        createPolicy(registryRealm, user, institution, permission);
    }

    public void createCommercePolicy(
            @NotNull String institution, @NotNull String permission) {
        String uid = authController.getUID();
        createPolicy(commerceRealm, uid, institution, permission);
    }

    public void createCommercePolicy(
            @NotNull String user, @NotNull String institution, @NotNull String permission) {

        createPolicy(commerceRealm, user, institution, permission);
    }

    public void createPolicy(
            @NotNull String realm, @NotNull String user,
            @NotNull String institution, @NotNull String permission) {

        if (entitlementService != null) {
            Action action = Action.fromValue(permission);
            entitlementService.createPolicy(realm, user, institution, action);

        } else {
            logger.error(
                    "Unable to createPolicy, invalid entitlement reference [" +
                            entitlementService + "]");
        }
    }

    public void updatePolicy(
            @NotNull String user, @NotNull String institution,
            @NotNull String permission) {

        if (entitlementService != null) {
            Action action = Action.fromValue(permission);
            entitlementService.updatePolicy(registryRealm, user, institution, action);
        } else {
            logger.error(
                    "Unable to updatePolicy, invalid entitlement reference [" +
                            entitlementService + "]");
        }
    }

    public void removePolicy(@NotNull Long policyKey) {
        if (entitlementService != null) {
            entitlementService.removePolicy(policyKey);
        } else {
            logger.error(
                    "Unable to entitlementService, invalid entitlement reference [" +
                            entitlementService + "]");
        }
    }

    public boolean isGlobalAdmin() {
        String uid = authController.getUID();
        return entitlementService.isGlobalAdmin(
                realmBean.getRegistryRealm(), uid);
    }

    public String getFullName() {
        return authController.getFullName();
    }

    public String getEmail() {
        return authController.getEmail();
    }

    public AuthController getAuthController() {
        return authController;
    }
    public void setAuthController(AuthController authController) {
        this.authController = authController;
    }

    public Entitlement getEntitlementService() {
        return entitlementService;
    }
    public void setEntitlementService(Entitlement entitlementService) {
        this.entitlementService = entitlementService;
    }

    public RegistryModel getRegistry() {
        return registry;
    }
    public void setRegistry(RegistryModel registry) {
        this.registry = registry;
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
}
