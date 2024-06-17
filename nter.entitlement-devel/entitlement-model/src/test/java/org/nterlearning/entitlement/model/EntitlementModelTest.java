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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nterlearning.entitlement.persistence.DataInit;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.EntitlementPolicy;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;

/**
 * User: Deringer
 * Date: 8/11/11
 * Time: 1:56 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/entitlement-model-beans-test.xml")
public class EntitlementModelTest {

    private String defaultAdminUID;

    @Autowired
    private EntitlementModel model;

    @Autowired
    DataInit dataInit;

    @Before
    public void init() {
        defaultAdminUID = dataInit.getDefaultAdminUID();
    }

    @Test
    public void createNewPolicy() throws Exception {
        String readOnlyUser = getRandomValue("SUBJECT");
        String newAdminUser = getRandomValue("SUBJECT");
        String resource = getRandomValue("INSTITUTION");
        String realm = null;

        // Ensure policy does not already exist (e.g. 'newAdminUser' is not
        //   already a GLOBAL_ADMIN
        try {
            removePolicy(realm, newAdminUser, resource, newAdminUser);
        } catch (Exception e) {
        }

        // Create a policy - make 'newAdminUser' a GLOBAL_ADMIN
        try {
            createPolicy(realm, newAdminUser, resource, Action.ADMIN, newAdminUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create New policy for the 'readOnly' user, using new GLOBAL_ADMIN user

        // Ensure policy does not already exist
        try {
            removePolicy(realm, readOnlyUser, resource, newAdminUser);
        } catch (Exception e) {
        }

        // Endure the policy does not exist
        EntitlementPolicy policy = model.getPolicy(realm, readOnlyUser, resource);
        Assert.isNull(policy, "Error removing policy with admin user [" +
                newAdminUser + "]");

        // Create New policy for the 'readOnly' user, using new GLOBAL_ADMIN
        try {
            createPolicy(realm, readOnlyUser, resource, Action.READ, newAdminUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve the policy
        policy = model.getPolicy(realm, readOnlyUser, resource);

        // Test
        Assert.notNull(policy, "Failure to create policy using subject [" +
                readOnlyUser + "], resource [" + resource + "], using Admin [" +
                newAdminUser + "]");
        Assert.isTrue(policy.getSubject().equals(readOnlyUser), "");
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.READ);

        // Clean-up
        removePolicy(realm, readOnlyUser, resource, newAdminUser);
        Assert.isNull(model.getPolicy(realm, readOnlyUser, resource));

        removePolicy(realm, newAdminUser, resource, defaultAdminUID);
        Assert.isNull(model.getPolicy(realm, newAdminUser, resource));
    }

    @Test
    public void updatePolicy() throws Exception {
        String adminSubject = getRandomValue("SUBJECT");
        String readSubject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Create an Admin policy
        createPolicy(realm, adminSubject, resource, Action.ADMIN, defaultAdminUID);

        // Create a read policy
        createPolicy(realm, readSubject, resource, Action.READ, adminSubject);

        // Retrieve the policy
        EntitlementPolicy policy = model.getPolicy(realm, readSubject, resource);

        // Test
        Assert.isTrue(policy.getSubject().equals(readSubject));
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.READ);

        // Attempt to upgrade readSubject to writeSubject using readSubject
        //  using readSubject authorization - should fail
        try {
            model.updatePolicy(realm, readSubject, resource, Action.WRITE.value(),
                    readSubject);
        } catch (Exception e) {
        }

        // Retrieve the policy
        EntitlementPolicy updatedPolicy = model.getPolicy(realm, readSubject, resource);

        Assert.isTrue(updatedPolicy.getAction() == Action.READ,
                "Expected READ action but received [" + updatedPolicy.getAction() + "]");

        // Attempt to upgrade readSubject to writeSubject using adminSubject
        model.updatePolicy(realm, readSubject, resource, Action.WRITE.value(),
                adminSubject);

        // Retrieve the policy
        EntitlementPolicy updatePolicy = model.getPolicy(realm, readSubject, resource);

        // Test
        Assert.isTrue(updatePolicy.getSubject().equals(readSubject));
        Assert.isTrue(updatePolicy.getResource().equals(resource));
        Assert.isTrue(updatePolicy.getAction() == Action.WRITE);

        // Clean-up
        removePolicy(realm, readSubject, resource, adminSubject);
        // Retrieve removed policy
        Assert.isNull(model.getPolicy(realm, readSubject, resource));

        removePolicy(realm, adminSubject, resource, defaultAdminUID);
        // Retrieve removed policy
        Assert.isNull(model.getPolicy(realm, adminSubject, resource));
    }

    @Test
    public void getPoliciesBySubject() throws Exception {
        String adminSubject = getRandomValue("SUBJECT");
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Create an Admin policy
        createPolicy(realm, adminSubject, resource, Action.ADMIN, defaultAdminUID);

        // Get policy by subject - baseline
        List<EntitlementPolicy> policies =
                model.getPolicyBySubject(realm, subject);

        // Create a policy
        createPolicy(realm, subject, resource, Action.READ, adminSubject);

        // Get policy by subject
        List<EntitlementPolicy> policies2 =
                model.getPolicyBySubject(realm, subject);

        // Test
        Assert.isTrue(policies2.size() == policies.size() + 1);

        // Clean-up
        removePolicy(realm, subject, resource, adminSubject);
        Assert.isNull(model.getPolicy(realm, subject, resource));

        removePolicy(realm, adminSubject, resource, defaultAdminUID);
        Assert.isNull(model.getPolicy(realm, adminSubject, resource));
    }

    @Test
    public void getPoliciesByResource() throws Exception {
        String adminSubject = getRandomValue("SUBJECT");
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Create an Admin policy
        createPolicy(realm, adminSubject, resource, Action.ADMIN, defaultAdminUID);

        // Get policy by subject - baseline
        List<EntitlementPolicy> policies =
                model.getPolicyByResource(realm, resource);

        // Create a policy
        createPolicy(realm, subject, resource, Action.READ, adminSubject);

        // Get policy by subject
        List<EntitlementPolicy> policies2 =
                model.getPolicyByResource(realm, resource);

        // Test
        Assert.isTrue(policies2.size() == policies.size() + 1);

        // Clean-up
        removePolicy(realm, subject, resource, adminSubject);
        Assert.isNull(model.getPolicy(realm, subject, resource));

        removePolicy(realm, adminSubject, resource, defaultAdminUID);
        Assert.isNull(model.getPolicy(realm, adminSubject, resource));
    }

    @Test
    public void getPolicy() throws Exception {
        String adminSubject = getRandomValue("SUBJECT");
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Create an Admin policy
        createPolicy(realm, adminSubject, resource, Action.ADMIN, defaultAdminUID);

        // Create a read policy
        createPolicy(realm, subject, resource, Action.READ, adminSubject);

        // Retrieve policy
        EntitlementPolicy policy =
                model.getPolicy(realm, subject, resource);

        // Test
        Assert.isTrue(policy.getSubject().equals(subject));
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.READ);

        // Clean-up
        removePolicy(realm, subject, resource, adminSubject);
        Assert.isNull(model.getPolicy(realm, subject, resource));

        removePolicy(realm, adminSubject, resource, defaultAdminUID);
        Assert.isNull(model.getPolicy(realm, adminSubject, resource));
    }

    @Test
    public void getAuthorization() throws Exception {
        String adminSubject = getRandomValue("SUBJECT");
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Create an Admin policy
        createPolicy(realm, adminSubject, resource, Action.ADMIN, defaultAdminUID);

        // Create a policy
        createPolicy(realm, subject, resource, Action.READ, adminSubject);

        // Retrieve permission level
        Action action = model.getAuthorization(realm, subject, resource);

        // Test
        Assert.isTrue(action == Action.READ,
                "Subject [" + subject + "] does not have READ access");
        Assert.isTrue(action != Action.WRITE,
                "Subject [" + subject + "] has WRITE access but should not");
        Assert.isTrue(model.isAuthorized(realm, subject, resource, Action.READ),
                "Subject [" + subject + "] is not authorized");
        Assert.isTrue(!model.isAuthorized(realm, subject, resource, Action.WRITE),
                "Subject [" + subject + "] is not authorized, but should not be");

        // Clean-up
        removePolicy(realm, subject, resource, adminSubject);
        Assert.isNull(model.getPolicy(realm, subject, resource));

        removePolicy(realm, adminSubject, resource, defaultAdminUID);
        Assert.isNull(model.getPolicy(realm, adminSubject, resource));
    }

    @Test
    public void getAllPolicies() throws Exception {
        String adminSubject = getRandomValue("SUBJECT");
        String subject1 = getRandomValue("SUBJECT");
        String subject2 = getRandomValue("SUBJECT");
        String subject3 = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Create an Admin policy
        createPolicy(realm, adminSubject, resource, Action.ADMIN, defaultAdminUID);

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject1, resource, adminSubject);
        } catch (Exception e) {
        }
        // Retrieve removed policy
        Assert.isNull(model.getPolicy(realm, subject1, resource));

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject2, resource, adminSubject);
        } catch (Exception e) {
        }
        // Retrieve removed policy
        Assert.isNull(model.getPolicy(realm, subject2, resource));

        // Ensure policy does not already exist
        try {
            removePolicy(realm, subject3, resource, adminSubject);
        } catch (Exception e) {
        }
        // Retrieve removed policy
        Assert.isNull(model.getPolicy(realm, subject3, resource));

        // Baseline policies
        List<EntitlementPolicy> policies1 = model.getAllPolicies();

        // Create a policies
        createPolicy(realm, subject1, resource, Action.READ, adminSubject);
        createPolicy(realm, subject2, resource, Action.READ, adminSubject);
        createPolicy(realm, subject3, resource, Action.READ, adminSubject);

        // Retrieve all policies
        List<EntitlementPolicy> policies2 = model.getAllPolicies();

        // Test
        Assert.isTrue(policies2.size() == policies1.size() + 3);

        // Clean-up
        removePolicy(realm, subject1, resource, adminSubject);
        removePolicy(realm, subject2, resource, adminSubject);
        removePolicy(realm, subject3, resource, adminSubject);

        // Retrieve all policies
        List<EntitlementPolicy> policies3 = model.getAllPolicies();

        // Test
        Assert.isTrue(policies3.size() == policies1.size());

        // Remove Admin subject
        removePolicy(realm, adminSubject, resource, defaultAdminUID);
    }

    @Test
    public void removePolicyByKey() throws Exception {
        String adminSubject = getRandomValue("SUBJECT");
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String realm = getRandomValue("REALM");

        // Create an Admin policy
        createPolicy(realm, adminSubject, resource, Action.ADMIN, defaultAdminUID);

        // Create a read policy
        createPolicy(realm, subject, resource, Action.READ, adminSubject);

        // Retrieve policy
        EntitlementPolicy policy =
                model.getPolicy(realm, subject, resource);

        // Test
        Assert.isTrue(policy != null, "Policy is Null");
        Assert.isTrue(policy.getSubject().equals(subject));
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.READ);

        // Retrieve policy by key
        policy = model.getPolicy(policy.getKey());

        // Test
        Assert.isTrue(policy != null, "Policy is Null");
        Assert.isTrue(policy.getSubject().equals(subject));
        Assert.isTrue(policy.getResource().equals(resource));
        Assert.isTrue(policy.getAction() == Action.READ);

        // Remove policy using key
        model.removePolicy(policy.getKey(), adminSubject);

        // Clean-up
        removePolicy(realm, adminSubject, resource, defaultAdminUID);
        Assert.isNull(model.getPolicy(realm, adminSubject, resource));
    }

    private void createPolicy(
            String realm, String subject, String resource, Action action, String adminUser) {
        model.createPolicy(realm, subject, resource, action.value(), adminUser);
    }

    private void removePolicy(String realm, String subject, String resource, String adminUser) {
        model.removePolicy(realm, subject, resource, adminUser);
    }

    private String getRandomValue(String seed) {
        Random r = new Random();
        long value = r.nextLong();
        return seed + "_" + value;
    }
}
