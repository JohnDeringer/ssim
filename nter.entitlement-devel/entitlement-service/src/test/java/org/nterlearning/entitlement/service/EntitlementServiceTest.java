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
package org.nterlearning.entitlement.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.nterlearning.entitlement.persistence.DataInit;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.EntitlementPolicy;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.RequestStatus;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Subject;
import org.nterlearning.xml.entitlement.entitlement_interface_0_1_0.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/22/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:entitlement-service-beans-test.xml")
/**
 * This test is disabled until I can update it with an HTTP client
 */
public class EntitlementServiceTest {

    @Autowired
    EntitlementImpl entitlement;
    @Autowired
    DataInit dataInit;

    private String defaultGlobalAdminUID;

    @Before
    public void init() {
        defaultGlobalAdminUID = dataInit.getDefaultAdminUID();
    }

    //@Test
    public void testGlobalAdmin() throws Exception {
        String newGlobalAdminUser1 = getRandomValue("SUBJECT");
        String newGlobalAdminUser2 = getRandomValue("SUBJECT");
        String realm = getRandomValue("REALM");

        // Ensure the policy does not already exist (e.g. 'newGlobalAdminUser'
        //  is not already a GLOBAL_ADMIN
        try {
            removePolicy(realm, newGlobalAdminUser1, "*", defaultGlobalAdminUID);
        } catch (Exception e) {}

        // Create a policy - make 'newGlobalAdminUser' a GLOBAL_ADMIN
        try {
            createPolicy(realm, newGlobalAdminUser1, "*", Action.GLOBAL_ADMIN,
                    defaultGlobalAdminUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve the policy
        GetPolicy request = new GetPolicy();
        request.setRealm(realm);
        request.setSubject(newGlobalAdminUser1);
        request.setResource("*");

        GetPolicyResponse response = entitlement.getPolicy(request);
        EntitlementPolicy policy = response.getEntitlementPolicy();

        // Test
        Assert.notNull(policy, "Failure to create policy using subject [" +
            newGlobalAdminUser1 + "], resource [*], using Admin [" +
            defaultGlobalAdminUID + "]");
        Assert.isTrue(policy.getSubject().equals(newGlobalAdminUser1),
                "Expected subject [" + newGlobalAdminUser1 +
                        " but retrieved subject [" + policy.getSubject() + "]");
        Assert.isTrue(policy.getResource().equals("*"),
                "Retrieved resource [" + policy.getResource() + "] is incorrect");
        Assert.isTrue(policy.getAction() == Action.GLOBAL_ADMIN,
                "Retrieved action [" + policy.getAction() + "] is not GLOBAL_ADMIN");

        // Create a policy - make 'newGlobalAdminUser2' a GLOBAL_ADMIN using
        //  'newGlobalAdminUser1' as the authorizing user
        try {
            createPolicy(realm, newGlobalAdminUser2, "*", Action.GLOBAL_ADMIN,
                    newGlobalAdminUser1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve the policy
        request = new GetPolicy();
        request.setRealm(realm);
        request.setSubject(newGlobalAdminUser2);
        request.setResource("*");
        response = entitlement.getPolicy(request);
        policy = response.getEntitlementPolicy();

        // Test
        Assert.notNull(policy, "Failure to create policy using subject [" +
            newGlobalAdminUser2 + "], resource [*], using Admin [" +
        newGlobalAdminUser1 + "]");
        Assert.isTrue(policy.getSubject().equals(newGlobalAdminUser2),
                "Retrieved subject [" + policy.getSubject() + "] is incorrect");
        Assert.isTrue(policy.getResource().equals("*"),
                "Retrieved resource [" + policy.getResource() + "] is incorrect");
        Assert.isTrue(policy.getAction() == Action.GLOBAL_ADMIN,
                "Retrieved action [" + policy.getAction() + "] is not GLOBAL_ADMIN");

        // Update the policy - using newGlobalAdminUser1 as the authorizing user
        updatePolicy(realm, newGlobalAdminUser2, "*", Action.READ, newGlobalAdminUser1);

        // Retrieve the policy
        request = new GetPolicy();
        request.setRealm(realm);
        request.setSubject(newGlobalAdminUser2);
        request.setResource("*");
        response = entitlement.getPolicy(request);
        policy = response.getEntitlementPolicy();

        // Test
        Assert.notNull(policy, "Failure to create policy using subject [" +
            newGlobalAdminUser2 + "], resource [*], using Admin [" +
        newGlobalAdminUser1 + "]");
        Assert.isTrue(policy.getSubject().equals(newGlobalAdminUser2),
                "Retrieved subject [" + policy.getSubject() + "] is incorrect");
        Assert.isTrue(policy.getResource().equals("*"),
                "Retrieved resource [" + policy.getResource() + "] is incorrect");
        Assert.isTrue(policy.getAction() == Action.GLOBAL_ADMIN,
                "Retrieved action [" + policy.getAction() + "] is not GLOBAL_ADMIN");

        // Remove policy
        removePolicy(realm, newGlobalAdminUser2, "*", newGlobalAdminUser1);

        // Retrieve the policy
        try {
            request = new GetPolicy();
            request.setRealm(realm);
            request.setSubject(newGlobalAdminUser2);
            request.setResource("*");
            response = entitlement.getPolicy(request);
            policy = response.getEntitlementPolicy();
        } catch (Exception e) {}

        Assert.isTrue(policy == null,
                "Policy with subject [" + newGlobalAdminUser2 + "] and resource [*]" +
            " was not successfully remove");

        // Clean-up
        removePolicy(realm, newGlobalAdminUser1, "*", defaultGlobalAdminUID);
    }

    //@Test
    public void testGlobalAdminFailure() throws Exception {
        String newGlobalAdminUser = getRandomValue("SUBJECT");
        String newAdminUser = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

         // Ensure the policy does not already exist (e.g. 'adminUser'
        //  is not already an ADMIN
        try {
            removePolicy(realm, newAdminUser, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}

        // Create a policy - make 'adminUser' an ADMIN user
        try {
            createPolicy(realm, newAdminUser, resource, Action.ADMIN,
                    defaultGlobalAdminUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve the policy
        GetPolicy request = new GetPolicy();
        request.setRealm(realm);
        request.setSubject(newAdminUser);
        request.setResource(resource);
        GetPolicyResponse response = entitlement.getPolicy(realm, newAdminUser, resource);
        EntitlementPolicy policy = response.getEntitlementPolicy();

        // Test
        Assert.notNull(policy, "Failure to create policy using subject [" +
            newAdminUser + "], resource [" + resource + "]" + " using AdminUser [" +
            defaultGlobalAdminUID + "]");
        Assert.isTrue(policy.getSubject().equals(newAdminUser),
                "Retrieved subject [" + policy.getSubject() + "] is incorrect");
        Assert.isTrue(policy.getResource().equals(resource),
                "Retrieved resource [" + policy.getResource() + "] is incorrect");
        Assert.isTrue(policy.getAction() == Action.ADMIN,
                "Retrieved action [" + policy.getAction() + "] should be [ADMIN]");

        // Attempt to create a new GLOBAL_ADMIN using an ADMIN user for
        //  authorization - this should fail
        // First ensure the policy does not already exist (e.g. 'adminUser'
        //  is not already an ADMIN
        try {
            removePolicy(realm, newGlobalAdminUser, resource, defaultGlobalAdminUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            createPolicy(realm, newGlobalAdminUser, resource, Action.GLOBAL_ADMIN,
                    newAdminUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve the policy - should not exist
        request = new GetPolicy();
        request.setRealm(realm);
        request.setSubject(newGlobalAdminUser);
        request.setResource(resource);

        response = entitlement.getPolicy(request);
        policy = response.getEntitlementPolicy();

        // Test - should be null
        Assert.isNull(policy, "ADMIN user [" + newAdminUser +
                "] successfully created a GLOBAL_ADMIN [" +
                newGlobalAdminUser + "]");

        // Create a GLOBAL_ADMIN policy using the DEFAULT_GLOBAL_ADMIN
        // First ensure the policy does not already exist.
        try {
            removePolicy(realm, newGlobalAdminUser, "*", defaultGlobalAdminUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            createPolicy(realm, newGlobalAdminUser, resource, Action.GLOBAL_ADMIN,
                    defaultGlobalAdminUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve the policy - resource [*] = GLOBAL_ADMIN
        response = entitlement.getPolicy(realm, newGlobalAdminUser, "*");
        policy = response.getEntitlementPolicy();

        Assert.notNull(policy, "Failure creating policy with subject [" +
            newGlobalAdminUser + "] and resource [" + resource +
                "] using adminUser [" + defaultGlobalAdminUID + "]");

        // Try to update the GLOBAL_ADMIN using an ADMIN for authorization - Failure
        try {
             updatePolicy(realm, newGlobalAdminUser, "*", Action.READ, newGlobalAdminUser);
        } catch (Exception e) {
        }

        // Retrieve the policy - Should be null
        response = entitlement.getPolicy(realm, newGlobalAdminUser, "*");
        policy = response.getEntitlementPolicy();
        Assert.isTrue(policy.getAction() != Action.READ, "ADMIN user [" +
                newAdminUser + "] successfully updated a GLOBAL_ADMIN");
        Assert.isTrue(policy.getAction() == Action.GLOBAL_ADMIN, "ADMIN user [" +
                newAdminUser +
                "] successfully updated a GLOBAL_ADMIN");

        // Try to remove a GLOBAL_ADMIN using an ADMIN user - should FAIL
        try {
            removePolicy(realm, newGlobalAdminUser, "*", newAdminUser);
        } catch (Exception e) {}

        // Retrieve the policy - Should still exists
        response = entitlement.getPolicy(realm, newGlobalAdminUser, "*");
        policy = response.getEntitlementPolicy();
        Assert.notNull(policy, "ADMIN user [" + newAdminUser +
                "] successfully removed a GLOBAL_ADMIN [" + newGlobalAdminUser + ']');
        Assert.isTrue(policy.getAction() == Action.GLOBAL_ADMIN, "ADMIN user [" +
                newAdminUser + "] successfully updated a GLOBAL_ADMIN");

        // Clean-up
        removePolicy(realm, newAdminUser, resource, newGlobalAdminUser);
        removePolicy(realm, newGlobalAdminUser, "*", defaultGlobalAdminUID);

    }

    //@Test
    public void createPolicyAdmin() throws Exception {
        String readOnlyUser = getRandomValue("SUBJECT");
        String newAdminUser = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Ensure policy does not already exist (e.g. 'newAdminUser' is not
        //   already a GLOBAL_ADMIN
        try {
            removePolicy(realm, newAdminUser, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}

        // Create a policy - make 'newAdminUser' a GLOBAL_ADMIN
        try {
            createPolicy(realm, newAdminUser, resource, Action.ADMIN, defaultGlobalAdminUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create New policy for the 'readOnly' user, using new GLOBAL_ADMIN user

        // Ensure policy does not already exist
        try {
            removePolicy(realm, readOnlyUser, resource, newAdminUser);
        } catch (Exception e) {}

        // Endure the policy does not exist
        GetPolicyResponse response = entitlement.getPolicy(realm, readOnlyUser, resource);
        EntitlementPolicy policy = response.getEntitlementPolicy();
        Assert.isNull(policy, "Error removing policy with admin user [" +
                newAdminUser + "]");

        // Create New policy for the 'readOnly' user, using new GLOBAL_ADMIN
        try {
            createPolicy(realm, readOnlyUser, resource, Action.READ, newAdminUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve the policy
        response = entitlement.getPolicy(realm, readOnlyUser, resource);
        policy = response.getEntitlementPolicy();

        // Test
        Assert.notNull(policy, "Failure to create policy using subject [" +
            readOnlyUser + "], resource [" + resource + "], using Admin [" +
        newAdminUser + "]");
        Assert.isTrue(policy.getSubject().equals(readOnlyUser), "");
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.READ);

        // Clean-up
        removePolicy(realm, readOnlyUser, resource, newAdminUser);
        removePolicy(realm, newAdminUser, resource, defaultGlobalAdminUID);
    }

    //@Test
    public void createPolicyAdminFailure() throws Exception {
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}

        // Create a policy
        try {
            createPolicy(realm, subject, resource, Action.READ, subject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve the policy
        GetPolicyResponse response = entitlement.getPolicy(realm, subject, resource);
        Assert.isNull(response.getEntitlementPolicy());

    }

    //@Test
    public void updatePolicy() throws Exception {
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}

        // Create a policy
        createPolicy(realm, subject, resource, Action.READ, defaultGlobalAdminUID);

        // Retrieve the policy
        GetPolicyResponse response = entitlement.getPolicy(realm, subject, resource);
        EntitlementPolicy policy = response.getEntitlementPolicy();

        // Test
        Assert.isTrue(policy.getSubject().equals(subject));
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.READ);

        // Update the policy
        updatePolicy(realm, subject, resource, Action.WRITE, defaultGlobalAdminUID);

        // Retrieve the policy
        response = entitlement.getPolicy(realm, subject, resource);
        policy = response.getEntitlementPolicy();

        // Test
        Assert.isTrue(policy.getSubject().equals(subject));
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.WRITE);

        // Clean-up
        removePolicy(realm, subject, resource, defaultGlobalAdminUID);

        // Retrieve removed policy
        response = entitlement.getPolicy(realm, subject, resource);
        Assert.isNull(response.getEntitlementPolicy());
    }

   // @Test
    public void getPoliciesBySubject() throws Exception {
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}

        // Get policy by subject - baseline
        GetPolicyBySubject request = new GetPolicyBySubject();
        request.setSubject(subject);
        GetPolicyBySubjectResponse response =
                entitlement.getPolicyBySubject(realm, subject);
        List<EntitlementPolicy> policies = response.getEntitlementPolicy();

        // Create a policy
        createPolicy(realm, subject, resource, Action.READ, defaultGlobalAdminUID);

        // Get policy by subject
        request = new GetPolicyBySubject();
        request.setSubject(subject);
        response =
                entitlement.getPolicyBySubject(realm, subject);
        List<EntitlementPolicy> policies2 = response.getEntitlementPolicy();

        // Test
        Assert.isTrue(policies2.size() == policies.size() + 1);

        // Clean-up
        removePolicy(realm, subject, resource, defaultGlobalAdminUID);

        // Retrieve removed policy
        GetPolicyResponse getPolicyResponse = entitlement.getPolicy(realm, subject, resource);
        Assert.isNull(getPolicyResponse.getEntitlementPolicy());
    }

    //@Test
    public void getPoliciesByResource() throws Exception {
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Get policy by subject - baseline
        GetPolicyByResource request = new GetPolicyByResource();
        request.setResource(resource);
        GetPolicyByResourceResponse response = entitlement.getPolicyByResource(request);
        List<EntitlementPolicy> policies = response.getEntitlementPolicy();
        int initSize = policies.size();

        // Create a policy
        createPolicy(realm, subject, resource, Action.READ, defaultGlobalAdminUID);

        // Get policy by resource
        request = new GetPolicyByResource();
        request.setRealm(realm);
        request.setResource(resource);
        response = entitlement.getPolicyByResource(request);
        policies = response.getEntitlementPolicy();

        // Test
        Assert.isTrue(policies.size() == (initSize + 1), "Expected [" +
            (initSize + 1) + "] but received [" + policies.size() + "]");

        // Clean-up
        removePolicy(realm, subject, resource, defaultGlobalAdminUID);

        // Retrieve removed policy
        GetPolicyResponse getPolicyResponse =
                entitlement.getPolicy(realm, subject, resource);
        Assert.isNull(getPolicyResponse.getEntitlementPolicy());
    }

    //@Test
    public void getPolicy() throws Exception {
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}

        // Retrieve removed policy
        GetPolicyResponse response =
                entitlement.getPolicy(realm, subject, resource);
        EntitlementPolicy removedPolicy = response.getEntitlementPolicy();

        // Test
        Assert.isNull(removedPolicy);

        // Create a policy
        createPolicy(realm, subject, resource, Action.READ, defaultGlobalAdminUID);

        // Retrieve policy
        response =
                entitlement.getPolicy(realm, subject, resource);
        EntitlementPolicy policy = response.getEntitlementPolicy();

        // Test
        Assert.isTrue(policy.getSubject().equals(subject));
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.READ);

        // Clean-up
        removePolicy(realm, subject, resource, defaultGlobalAdminUID);

        // Retrieve removed policy
        response =
                entitlement.getPolicy(realm, subject, resource);
        Assert.isNull(response.getEntitlementPolicy());
    }

    //@Test
    public void removePolicyByKey() throws Exception {
        String adminSubject = getRandomValue("SUBJECT");
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Create an Admin policy
        createPolicy(realm, adminSubject, resource, Action.ADMIN, defaultGlobalAdminUID);

        // Retrieve policy
        GetPolicyResponse response =
                entitlement.getPolicy(realm, adminSubject, resource);
        EntitlementPolicy policy = response.getEntitlementPolicy();

        // Test
        Assert.notNull(policy, "Policy is Null");
        Assert.isTrue(policy.getSubject().equals(adminSubject));
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.ADMIN);

        // Create a Read policy using a normal subject using the
        //  previously created Admin subject
        createPolicy(realm, subject, resource, Action.READ, adminSubject);

        // Retrieve policy
        response =
                entitlement.getPolicy(realm, subject, resource);
        policy = response.getEntitlementPolicy();

        // Test
        Assert.notNull(policy, "Policy is Null");
        Assert.isTrue(policy.getSubject().equals(subject));
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.READ);

        // Remove the policy using the policy key
        RemovePolicyByKey removePolicyByKey = new RemovePolicyByKey();
        removePolicyByKey.setPolicyKey(policy.getKey());
        //removePolicyByKey.setAuthorizingSubject(adminSubject);

        RemovePolicyByKeyResponse removePolicyByKeyResponse =
                entitlement.removePolicyByKey(removePolicyByKey);

        Assert.isTrue(removePolicyByKeyResponse.getStatus() == RequestStatus.SUCCESS);

        // Retrieve the policy
        GetPolicy getPolicy = new GetPolicy();
        getPolicy.setRealm(realm);
        getPolicy.setSubject(subject);
        getPolicy.setResource(resource);
        GetPolicyResponse getPolicyResponse = entitlement.getPolicy(getPolicy);

        policy = getPolicyResponse.getEntitlementPolicy();
        // Test - should be null
        Assert.isNull(policy, "Policy is not null");

        // Clean-up
        removePolicy(realm, adminSubject, resource, defaultGlobalAdminUID);

        // Retrieve removed policy
        response =
                entitlement.getPolicy(realm, adminSubject, resource);
        Assert.isNull(response.getEntitlementPolicy());
    }

    //@Test
    public void getAuthorization() throws Exception {
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}

        // Retrieve removed policy
        GetPolicyResponse response =
                entitlement.getPolicy(realm, subject, resource);
        EntitlementPolicy removedPolicy = response.getEntitlementPolicy();

        // Ensure policy has been removed
        Assert.isNull(removedPolicy);

        // Create a policy
        createPolicy(realm, subject, resource, Action.READ, defaultGlobalAdminUID);

        // Retrieve permission level
        GetAuthorization request = new GetAuthorization();
        request.setRealm(realm);
        request.setSubject(subject);
        request.setResource(resource);

        GetAuthorizationResponse getAuthorizationResponse =
                entitlement.getAuthorization(request);
        Action action = getAuthorizationResponse.getAction();

        // Test
        Assert.isTrue(action == Action.READ,
                "Subject [" + subject + "] does not have READ access");
        Assert.isTrue(action != Action.WRITE,
                "Subject [" + subject + "] has WRITE access but should not");
        Assert.isTrue(isAuthorized(realm, subject, resource, Action.READ),
                "Subject [" + subject + "] is not authorized");
        Assert.isTrue(!isAuthorized(realm, subject, resource, Action.WRITE),
                "Subject [" + subject + "] is not authorized, but should not be");

        // Clean-up
        removePolicy(realm, subject, resource, defaultGlobalAdminUID);

        // Retrieve removed policy
        response = entitlement.getPolicy(realm, subject, resource);
        Assert.isNull(response.getEntitlementPolicy());
    }

    //@Test
    public void getAllPolicies() throws Exception {
        String subject1 = getRandomValue("SUBJECT");
        String subject2 = getRandomValue("SUBJECT");
        String subject3 = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject1, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}
        // Retrieve removed policy
        GetPolicyResponse response =
                entitlement.getPolicy(realm, subject1, resource);
        EntitlementPolicy removedPolicy = response.getEntitlementPolicy();

        // Test
        Assert.isNull(removedPolicy);

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject2, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}
        // Retrieve removed policy
        response =
                entitlement.getPolicy(realm, subject2, resource);
        removedPolicy = response.getEntitlementPolicy();

        // Test
        Assert.isNull(removedPolicy);

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject3, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}
        // Retrieve removed policy
        response =
                entitlement.getPolicy(realm, subject3, resource);
        removedPolicy = response.getEntitlementPolicy();

        // Test
        Assert.isNull(removedPolicy);

        // Baseline policies
        GetAllPolicies getAllPolicies = new GetAllPolicies();
        GetAllPoliciesResponse getAllPoliciesResponse =
                entitlement.getAllPolicies(getAllPolicies);
        List<EntitlementPolicy> policies1 = getAllPoliciesResponse.getEntitlementPolicy();

        // Create a policies
        createPolicy(realm, subject1, resource, Action.READ, defaultGlobalAdminUID);
        createPolicy(realm, subject2, resource, Action.READ, defaultGlobalAdminUID);
        createPolicy(realm, subject3, resource, Action.READ, defaultGlobalAdminUID);

        // Retrieve all policies
        getAllPolicies = new GetAllPolicies();
        getAllPoliciesResponse =
                entitlement.getAllPolicies(getAllPolicies);
        List<EntitlementPolicy> policies2 =
                getAllPoliciesResponse.getEntitlementPolicy();

        // Test
        Assert.isTrue(policies2.size() == policies1.size() + 3);

        // Clean-up
        removePolicy(realm, subject1, resource, defaultGlobalAdminUID);
        removePolicy(realm, subject2, resource, defaultGlobalAdminUID);
        removePolicy(realm, subject3, resource, defaultGlobalAdminUID);

        // Retrieve all policies
        getAllPolicies = new GetAllPolicies();
        getAllPoliciesResponse =
                entitlement.getAllPolicies(getAllPolicies);
        List<EntitlementPolicy> policies3 =
                getAllPoliciesResponse.getEntitlementPolicy();

        // Test
        Assert.isTrue(policies3.size() == policies1.size());
    }

    ///@Test
    public void removeAdmin() throws Exception {
        String newGlobalAdminUser = getRandomValue("SUBJECT");
        String newAdminUser = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Test removal of an 'Admin' user

        // Ensure the policy does not already exist.
        try {
            removePolicy(realm, newAdminUser, resource, defaultGlobalAdminUID);
        } catch (Exception e) {}
        // Create a policy - make 'newAdminUser' an ADMIN
        try {
            createPolicy(realm, newAdminUser, resource, Action.ADMIN,
                    defaultGlobalAdminUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Retrieve the policy
        GetPolicyResponse response = entitlement.getPolicy(realm, newAdminUser, resource);
        EntitlementPolicy policy = response.getEntitlementPolicy();

        Assert.isTrue(policy != null, "Unexpected null policy");
        // Attempt to remove the policy - admin should not be able to remove itself
        try {
            removePolicy(realm, newAdminUser, resource, newAdminUser);
        } catch (Exception e) {
        }
        response = entitlement.getPolicy(realm, newAdminUser, resource);
        policy = response.getEntitlementPolicy();

        Assert.isTrue(policy != null, "Unexpected null policy");

        // Test removal of an 'Global_Admin' user
        // Ensure the policy does not already exist.
        try {
            removePolicy(realm, newGlobalAdminUser, "*", defaultGlobalAdminUID);
        } catch (Exception e) {}
        // Create a policy - make 'newGlobalAdminUser' a GLOBAL_ADMIN
        try {
            createPolicy(realm, newGlobalAdminUser, resource, Action.GLOBAL_ADMIN,
                    defaultGlobalAdminUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve the policy
        response = entitlement.getPolicy(realm, newGlobalAdminUser, resource);
        policy = response.getEntitlementPolicy();

        Assert.isTrue(policy != null, "Unexpected null policy");

        // Attempt to remove the policy - global admin should not be able to remove itself
        try {
            removePolicy(realm, newGlobalAdminUser, resource, newGlobalAdminUser);
        } catch (Exception e) {
        }
        response = entitlement.getPolicy(realm, newAdminUser, resource);
        policy = response.getEntitlementPolicy();

        Assert.isTrue(policy != null, "Unexpected null policy");

        // Clean-up
        removePolicy(realm, newAdminUser, resource, defaultGlobalAdminUID);
        removePolicy(realm, newGlobalAdminUser, resource, defaultGlobalAdminUID);
    }

    //@Test
    public void getSubjects() throws Exception {
        GetSubjects request = new GetSubjects();

        GetSubjectsResponse response = entitlement.getSubjects(request);
        List<Subject> subjects = response.getSubject();
        Assert.isTrue(subjects.size() > 0);
    }

    private void createPolicy(
            String realm, String subject, String resource, Action action, String adminUser) {

        CreatePolicy createPolicy = new CreatePolicy();

        EntitlementPolicy entitlementPolicy = new EntitlementPolicy();
        entitlementPolicy.setRealm(realm);
        entitlementPolicy.setSubject(subject);
        entitlementPolicy.setResource(resource);
        entitlementPolicy.setAction(action);

        createPolicy.setEntitlementPolicy(entitlementPolicy);

        entitlement.createPolicy(createPolicy);
    }

    private void updatePolicy(String realm, String subject, String resource, Action action, String adminUser) {
        UpdatePolicy updatePolicy = new UpdatePolicy();

        EntitlementPolicy entitlementPolicy = new EntitlementPolicy();
        entitlementPolicy.setRealm(realm);
        entitlementPolicy.setSubject(subject);
        entitlementPolicy.setResource(resource);
        entitlementPolicy.setAction(action);

        updatePolicy.setEntitlementPolicy(entitlementPolicy);

        entitlement.updatePolicy(updatePolicy);
    }

    private void removePolicy(String realm, String subject, String resource, String adminUser) {
        RemovePolicy removePolicy = new RemovePolicy();

        removePolicy.setRealm(realm);
        removePolicy.setSubject(subject);
        removePolicy.setResource(resource);

        entitlement.removePolicy(removePolicy);
    }

    private boolean isAuthorized(String realm, String subject,
                                String resource, Action permission) {
        boolean isAuthorized = false;

        GetAuthorization request = new GetAuthorization();
        request.setRealm(realm);
        request.setSubject(subject);
        request.setResource(resource);

        GetAuthorizationResponse response = entitlement.getAuthorization(request);
        Action action = response.getAction();
        if (action.ordinal() >= permission.ordinal()) {
            isAuthorized = true;
        }

        return isAuthorized;
    }

    private String getRandomValue(String seed) {
        Random r = new Random();
        long value = r.nextLong();
        return seed + "_" + value;
    }

}
