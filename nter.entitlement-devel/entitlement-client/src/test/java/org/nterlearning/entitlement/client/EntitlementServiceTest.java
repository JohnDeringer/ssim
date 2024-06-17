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

package org.nterlearning.entitlement.client;

import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;

import org.springframework.util.Assert;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/30/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:entitlement-client-test.xml")
public class EntitlementServiceTest {

    @Autowired
    private Entitlement service;
    @Autowired
    private IdentityService identityService;

    private static final String realm = "TEST_REALM";
    private static String adminSubject = null;
    private static String resource = getRandomValue("resource");

    @Before
    public void init() {
        if (adminSubject == null) {
            adminSubject = getUidFromEmail(service.getUser());
            Action action = Action.ADMIN;

            // Create an Admin policy
            createPolicy(realm, adminSubject, resource, action);
        }
    }

    @Test
    public void testCreatePolicy() {
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;

        String email = getEmailByUID(subject);
        System.out.println("GET_EMAIL [" + email + "]");

        // Create a policy
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        // Clean-up
        removePolicy(policy.getKey());
    }

    @Test
    public void updatePolicy() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        // Update the policy to WRITE
        action = Action.WRITE;
        service.updatePolicy(realm, subject, resource, action);

        // Retrieve the policy
        policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        // Clean-up
        removePolicy(policy.getKey());
    }

    @Test
    public void removePolicyByKey() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        // Remove policy
        removePolicy(policy.getKey());

        // Retrieve the policy
        policy = null;
        try {
            policy = getPolicy(realm, subject, resource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.isNull(policy, "Policy should be Null");
    }

    @Test
    public void removePolicy() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        // Remove policy
        service.removePolicy(realm, subject, resource);

        // Retrieve the policy
        policy = null;
        try {
            policy = getPolicy(realm, subject, resource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.isNull(policy, "Policy should be Null");
    }

    @Test
    public void getAllPolicies() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        List<EntitlementPolicy> policies = service.getAllPolicies();
        Assert.isTrue(!policies.isEmpty(),
                "Expected at least '1' policy but received [" +
                        policies.size() + "]");

        boolean found = false;
        for (EntitlementPolicy retPolicy : policies) {
            if (retPolicy.getKey().equals(policy.getKey())) {
                found = true;
            }
        }
        Assert.isTrue(found, "Unable to find policy [" + policy.getKey() + "]");

        // Clean-up
        removePolicy(policy.getKey());
    }

    @Test
    public void getPolicyBySubject() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        List<EntitlementPolicy> policies =
                service.getPoliciesBySubject(realm, subject);
        Assert.isTrue(!policies.isEmpty(),
                "Expected at least '1' policy but received [" +
                        policies.size() + "]");

        boolean found = false;
        for (EntitlementPolicy retPolicy : policies) {
            if (retPolicy.getKey().equals(policy.getKey())) {
                found = true;
            }
        }
        Assert.isTrue(found, "Unable to find policy [" + policy.getKey() + "]");

        // Clean-up
        removePolicy(policy.getKey());
    }

    @Test
    public void getPolicyByResource() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        List<EntitlementPolicy> policies =
                service.getPoliciesByResource(realm, resource);
        Assert.isTrue(!policies.isEmpty(),
                "Expected at least '1' policy but received [" +
                        policies.size() + "]");

        boolean found = false;
        for (EntitlementPolicy retPolicy : policies) {
            if (retPolicy.getKey().equals(policy.getKey())) {
                found = true;
            }
        }
        Assert.isTrue(found, "Unable to find policy [" + policy.getKey() + "]");

        // Clean-up
        removePolicy(policy.getKey());
    }

    @Test
    public void getPolicyByRealm() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        List<EntitlementPolicy> policies =
                service.getPoliciesByRealm(realm);
        Assert.isTrue(!policies.isEmpty(),
                "Expected at least '1' policy but received [" +
                        policies.size() + "]");

        boolean found = false;
        for (EntitlementPolicy retPolicy : policies) {
            if (retPolicy.getKey().equals(policy.getKey())) {
                found = true;
            }
        }
        Assert.isTrue(found, "Unable to find policy [" + policy.getKey() + "]");

        // Clean-up
        removePolicy(policy.getKey());
    }

    @Test
    public void getPolicyByKey() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        policy = service.getPolicy(policy.getKey());
        Assert.notNull(policy,
                "Unexpected policy [" + policy + "]");

        // Clean-up
        removePolicy(policy.getKey());
    }

    @Test
    public void getAuthorization() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        Action rtnAction = service.getAuthorization(realm, subject, resource);
        Assert.isTrue(action == rtnAction,
                "Expected action [" + action + "] but received [" + rtnAction + "]");

        // Clean-up
        removePolicy(policy.getKey());
    }

    @Test
    public void isAuthorized() {
        // Create a READ policy
        String subject = getUidFromEmail("jdslo2@mailinator.com");
        Action action = Action.READ;
        createPolicy(realm, subject, resource, action);

        // Retrieve the policy
        EntitlementPolicy policy = getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Unexpected Null value");
        Assert.isTrue(policy.getAction() == action,
                "Expected Action [" + action + "] but received [" +
                        policy.getAction() + "]");

        boolean isAuthorized = service.isAuthorized(realm, subject, resource, action);
        Assert.isTrue(isAuthorized,
                "Unexpected response [" + isAuthorized + "]");

        // Clean-up
        removePolicy(policy.getKey());
    }

    @Test
    public void getActions() {
        Set<Action> testSet = new TreeSet<Action>();
        List<Action> actions = service.getActions();
        Assert.isTrue(!actions.isEmpty(),
                "Unexpected response [" + actions.size() + "]");

        for (Action action : actions) {
            testSet.add(action);
        }
        Assert.isTrue(testSet.size() == actions.size(),
                "Expected [" + testSet.size() +
                        "] but received [" + actions.size() + "]");
    }

    @Test
    public void getRealms() {
        Set<String> testSet = new TreeSet<String>();
        List<String> realms = service.getRealms();
        Assert.isTrue(!realms.isEmpty(),
                "Unexpected response [" + realms.size() + "]");

        for (String realm : realms) {
            testSet.add(realm);
        }
        Assert.isTrue(testSet.size() == realms.size(),
                "Expected [" + testSet.size() +
                        "] but received [" + realms.size() + "]");
    }

    @Test
    public void getSubjects() {
        Set<String> testSet = new TreeSet<String>();
        List<String> subjects = service.getSubjects();
        Assert.isTrue(!subjects.isEmpty(),
                "Unexpected response [" + subjects.size() + "]");

        for (String subject : subjects) {
            testSet.add(subject);
        }
        Assert.isTrue(testSet.size() == subjects.size(),
                "Expected [" + testSet.size() +
                        "] but received [" + subjects.size() + "]");
    }

    @Test
    public void getResources() {
        Set<String> testSet = new TreeSet<String>();
        List<String> resources = service.getResources();
        Assert.isTrue(!resources.isEmpty(),
                "Unexpected response [" + resources.size() + "]");

        for (String resource : resources) {
            testSet.add(resource);
        }
        Assert.isTrue(testSet.size() == resources.size(),
                "Expected [" + testSet.size() +
                        "] but received [" + resources.size() + "]");
    }

    @Test
    public void isGlobalAdmin() {
        String globalAdminSubject = getGlobalAdminSubject();
        String realm = null;
        Assert.isTrue(service.isGlobalAdmin(realm, globalAdminSubject),
                "Unexpected 'FALSE' response");
    }


    private static String getRandomValue(String value) {
        Random r = new Random();
        return value + ":" + r.nextInt();
    }

    private void createPolicy(String realm, String subject, String resource, Action action) {
        service.createPolicy(realm, subject, resource, action);
    }

    private EntitlementPolicy getPolicy(String realm, String subject, String resource) {
        return service.getPolicy(realm, subject, resource);
    }

    private void removePolicy(Long policyKey) {
        service.removePolicy(policyKey);
    }

    private String getUidFromEmail(String email) {
        String uid = null;
        UserImpl user = identityService.getUserByEmail(email);
        if (user != null) {
            uid = user.getUid();
        }
        return uid;
    }

    private String getEmailByUID(String uid) {
        String email = null;
        UserImpl user = identityService.getUserByUID(uid);
        if (user != null) {
            email = user.getEmail();
        }
        return email;
    }

    private String getGlobalAdminSubject() {
        String globalAdminSubject = null;
        List<EntitlementPolicy> policies =
                service.getPoliciesByResource("/", "*");
        if (policies != null && !policies.isEmpty()) {
            EntitlementPolicy policy = policies.get(0);
            globalAdminSubject = policy.getSubject();
        }

        return globalAdminSubject;
    }

}
