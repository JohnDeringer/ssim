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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Random;

/**
 * User: Deringer
 * Date: 8/11/11
 * Time: 1:56 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/entitlement-persistence-beans-test.xml")
public class EntitlementPersistenceTest {

    private String defaultGlobalAdminSubject;

    @Autowired
    private EntitlementDao dao;

    @Autowired
    private DataInit dataInit;

    @Before
    public void init() {
        defaultGlobalAdminSubject = dataInit.getDefaultAdminUID();
    }

    @Test
    public void removePolicyByKey() {
        String realm = getRandomValue("REALM");
        String admin_subject = getRandomValue("ADMIN_SUBJECT");
        String read_subject = getRandomValue("ADMIN_SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String read_action = Action.READ.value();
        String admin_action = Action.ADMIN.value();

        // Create an Admin user policy
        dao.createPolicy(realm, admin_subject, resource, admin_action);

        // Retrieve the policy
        PolicyEntity policy = dao.getPolicy(realm, admin_subject, resource);

        Assert.notNull(policy, "Policy is null");
        Assert.isTrue(policy.getRealm().equals(realm),
                "Expected realm [" + realm + "] but received [" +
                        policy.getRealm() + "]");
        Assert.isTrue(policy.getSubject().equals(admin_subject),
                "Expected subject [" + admin_subject + "] but received [" +
                        policy.getSubject() + "]");
        Assert.isTrue(policy.getResource().equals(resource),
                "Expected resource [" + resource + "] but received [" +
                        policy.getResource() + "]");
        Assert.isTrue(policy.getAction().getValue().equals(admin_action),
                "Expected action [" + admin_action + "] but received [" +
                        policy.getAction() + "]");

        // Create a 'Write' policy using the previously created 'Admin'
        dao.createPolicy(realm, read_subject, resource, read_action);

        // Retrieve the policy
        policy = dao.getPolicy(realm, read_subject, resource);

        Assert.notNull(policy, "Policy is null");
        Assert.isTrue(policy.getRealm().equals(realm),
                "Expected realm [" + realm + "] but received [" +
                        policy.getRealm() + "]");
        Assert.isTrue(policy.getSubject().equals(read_subject),
                "Expected subject [" + read_subject + "] but received [" +
                        policy.getSubject() + "]");
        Assert.isTrue(policy.getResource().equals(resource),
                "Expected resource [" + resource + "] but received [" +
                        policy.getResource() + "]");
        Assert.isTrue(policy.getAction().getValue().equals(read_action),
                "Expected action [" + read_action + "] but received [" +
                        policy.getAction() + "]");

        // Retrieve the policy by key
        policy = dao.getPolicy(policy.getID());

        Assert.notNull(policy, "Policy is null");
        Assert.isTrue(policy.getRealm().equals(realm),
                "Expected realm [" + realm + "] but received [" +
                        policy.getRealm() + "]");
        Assert.isTrue(policy.getSubject().equals(read_subject),
                "Expected subject [" + read_subject + "] but received [" +
                        policy.getSubject() + "]");
        Assert.isTrue(policy.getResource().equals(resource),
                "Expected resource [" + resource + "] but received [" +
                        policy.getResource() + "]");
        Assert.isTrue(policy.getAction().getValue().equals(read_action),
                "Expected action [" + read_action + "] but received [" +
                        policy.getAction() + "]");

        // remove policy by key
        dao.removePolicy(policy.getID(), admin_subject);
        // Retrieve the policy by key
        policy = dao.getPolicy(policy.getID());

        Assert.isTrue(policy == null, "Policy is not null");

    }

    @Test
    public void addPolicy() {
        String realm = getRandomValue("REALM");
        String admin_subject = getRandomValue("ADMIN_SUBJECT");
        String read_subject = getRandomValue("ADMIN_SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String read_action = Action.READ.value();
        String admin_action = Action.ADMIN.value();

        // Create an Admin user policy
        dao.createPolicy(realm, admin_subject, resource, admin_action);

        // Retrieve the policy
        PolicyEntity policy = dao.getPolicy(realm, admin_subject, resource);

        Assert.notNull(policy, "Policy is null");
        Assert.isTrue(policy.getRealm().equals(realm),
                "Expected realm [" + realm + "] but received [" +
                        policy.getRealm() + "]");
        Assert.isTrue(policy.getSubject().equals(admin_subject),
                "Expected subject [" + admin_subject + "] but received [" +
                        policy.getSubject() + "]");
        Assert.isTrue(policy.getResource().equals(resource),
                "Expected resource [" + resource + "] but received [" +
                        policy.getResource() + "]");
        Assert.isTrue(policy.getAction().getValue().equals(admin_action),
                "Expected action [" + admin_action + "] but received [" +
                        policy.getAction() + "]");

        // Create a 'Write' policy using the previously created 'Admin'
        dao.createPolicy(realm, read_subject, resource, read_action);

        // Retrieve the policy
        policy = dao.getPolicy(realm, read_subject, resource);

        Assert.notNull(policy, "Policy is null");
        Assert.isTrue(policy.getRealm().equals(realm),
                "Expected realm [" + realm + "] but received [" +
                        policy.getRealm() + "]");
        Assert.isTrue(policy.getSubject().equals(read_subject),
                "Expected subject [" + read_subject + "] but received [" +
                        policy.getSubject() + "]");
        Assert.isTrue(policy.getResource().equals(resource),
                "Expected resource [" + resource + "] but received [" +
                        policy.getResource() + "]");
        Assert.isTrue(policy.getAction().getValue().equals(read_action),
                "Expected action [" + read_action + "] but received [" +
                        policy.getAction() + "]");

        // Clean-up

        // Remove 'Read' policy using 'Admin' subject
        dao.removePolicy(realm, read_subject, resource, admin_subject);
        // Retrieve the policy
        policy = dao.getPolicy(realm, read_subject, resource);
        Assert.isNull(policy, "Policy is null");

        // Remove 'Admin' policy using 'Global-Admin'
        dao.removePolicy(realm, admin_subject, resource, defaultGlobalAdminSubject);
        // Retrieve the policy
        policy = dao.getPolicy(realm, admin_subject, resource);
        Assert.isNull(policy, "Policy is not null");
    }

    @Test
    public void updatePolicy() {
        String realm = getRandomValue("REALM");
        String subject = getRandomValue("SUBJECT");
        String resource = getRandomValue("RESOURCE");
        String read_action = Action.READ.value();
        String admin_action = Action.ADMIN.value();

        // Create an 'Read' user policy
        dao.createPolicy(realm, subject, resource, read_action);

        // Retrieve the policy
        PolicyEntity policy = dao.getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Policy is null");
        Assert.isTrue(policy.getRealm().equals(realm),
                "Expected realm [" + realm + "] but received [" +
                        policy.getRealm() + "]");
        Assert.isTrue(policy.getSubject().equals(subject),
                "Expected subject [" + subject + "] but received [" +
                        policy.getSubject() + "]");
        Assert.isTrue(policy.getResource().equals(resource),
                "Expected resource [" + resource + "] but received [" +
                        policy.getResource() + "]");
        Assert.isTrue(policy.getAction().getValue().equals(read_action),
                "Expected action [" + read_action + "] but received [" +
                        policy.getAction() + "]");

        // Update the 'Read' policy to 'Admin'
        dao.updatePolicy(realm, subject, resource, admin_action);

        // Retrieve the policy
        policy = dao.getPolicy(realm, subject, resource);

        Assert.notNull(policy, "Policy is null");
        Assert.isTrue(policy.getRealm().equals(realm),
                "Expected realm [" + realm + "] but received [" +
                        policy.getRealm() + "]");
        Assert.isTrue(policy.getSubject().equals(subject),
                "Expected subject [" + subject + "] but received [" +
                        policy.getSubject() + "]");
        Assert.isTrue(policy.getResource().equals(resource),
                "Expected resource [" + resource + "] but received [" +
                        policy.getResource() + "]");
        Assert.isTrue(policy.getAction().getValue().equals(admin_action),
                "Expected action [" + admin_action + "] but received [" +
                        policy.getAction() + "]");

        // Clean-up

        // Remove the policy using 'Global-Admin'
        dao.removePolicy(realm, subject, resource, defaultGlobalAdminSubject);
        // Retrieve the policy
        policy = dao.getPolicy(realm, subject, resource);
        Assert.isNull(policy, "Policy is not null");
    }

    private String getRandomValue(String seed) {
        Random r = new Random();
        long value = r.nextLong();
        return seed + "_" + value;
    }

}
