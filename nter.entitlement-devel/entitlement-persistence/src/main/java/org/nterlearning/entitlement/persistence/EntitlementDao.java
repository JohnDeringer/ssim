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

import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;

import java.util.List;

/**
 * User: Deringer
 * Date: 6/16/11
 * Time: 10:26 AM
 */
public interface EntitlementDao {

    /**
     * Create a new entitlement policy. The policy will create an authorization
     * to the resource by the subject. The type of authorization is defined by
     * the action.
     * @param realm The security realm that provides context for the users entitlement
     * @param subject The user, or machine, to be given authorization.
     * @param resource The target of the authorization, generally an
     * institution such as an NTER or Content Repository instance.
     * @param action The type of authorization (READ, WRITE, ADMIN).
     */
    void createPolicy(
            String realm, String subject, String resource,
            String action);

    /**
     * Update an existing entitlement policy, the policy is an authorization
     * to a resource by a subject.
     * @param realm The security realm that provides context for the users entitlement
     * @param subject The user, or machine, to be given authorization.
     * @param resource The target of the authorization, generally an
     * institution such as an NTER or Content Repository instance.
     * @param action The type of authorization (READ, WRITE, ADMIN).
    */
    void updatePolicy(
            String realm, String subject, String resource,
            String action);

    /**
     * Remove an existing entitlement policy, the policy is an authorization
     * to a resource by a subject.
     * @param realm The security realm that provides context for the users entitlement
     * @param subject The user, or machine, with the authorization.
     * @param resource The target of the authorization, generally an
     * institution such as an NTER or Content Repository instance.
     * @param authorizingSubject The user, with admin privileges, removing the authorization.
     */
    void removePolicy(String realm, String subject, String resource,
                      String authorizingSubject);

    /**
     * Remove an existing entitlement policy, the policy is an authorization
     * to a resource by a subject.
     * @param policyKey The unique identifier of the entitlement policy
     * institution such as an NTER or Content Repository instance.
     * @param authorizingSubject The user, with admin privileges, removing the authorization.
     */
    void removePolicy(Long policyKey,
                      String authorizingSubject);

    /**
     * Retrieves a list of all Entitlement Policies.
     * @return A list of PolicyEntity objects containing the subject,
     * resource and action of the authorization.
     */
    List<PolicyEntity> getAllPolicies();

    /**
     * Retrieves a list of all Entitlement Policies for a particular subject.
     * @param realm The security realm that provides context for the users entitlement
     * @param subject The user, or machine, with the authorization.
     * @return A list of PolicyEntity objects containing the subject,
     * resource and action of the authorization.
     */
    List<PolicyEntity> getPoliciesBySubject(String realm, String subject);

    /**
     * Retrieves a list of all Entitlement Policies for a particular subject.
     * @param subject The user, or machine, with the authorization.
     * @return A list of PolicyEntity objects containing the subject,
     * resource and action of the authorization.
     */
    List<PolicyEntity> getPoliciesBySubject(String subject);

    /**
     * Retrieves a list of all Entitlement Policies for a particular resource.
     * @param realm The security realm that provides context for the users entitlement
     * @param resource The target of the authorization, generally an
     * institution such as an NTER or Content Repository instance.
     * @return A list of PolicyEntity objects containing the subject,
     * resource and action of the authorization.
     */
    List<PolicyEntity> getPoliciesByResource(String realm, String resource);

    /**
     * Retrieves a list of all Entitlement Policies for a particular resource.
     * @param realm The security realm that provides context for the users entitlement
     * @return A list of PolicyEntity objects containing the subject,
     * resource and action of the authorization.
     */
    List<PolicyEntity> getPoliciesByRealm(String realm);

    /**
     * Retrieves an Entitlement Policy
     * @param policyKey The entitlement policy unique identifier
     * institution such as an NTER or Content Repository instance.
     * @return An PolicyEntity object containing the subject,
     * resource and action of the authorization.
     */
    PolicyEntity getPolicy(Long policyKey);

    /**
     * Retrieves an Entitlement Policy
     * @param realm The security realm that provides context for the users entitlement
     * @param subject The user, or machine, with the authorization.
     * @param resource The target of the authorization, generally an
     * institution such as an NTER or Content Repository instance.
     * @return An PolicyEntity object containing the subject,
     * resource and action of the authorization.
     */
    PolicyEntity getPolicy(String realm, String subject, String resource);

    /**
     * Returns the type of authorization for a given policy.
     * @param realm The security realm that provides context for the users entitlement
     * @param subject The user, or machine, with the authorization.
     * @param resource The target of the authorization, generally an
     * institution such as an NTER or Content Repository instance.
     * @return An Action enum object containing the type of authorization (READ, WRITE, ADMIN)
     */
    ActionEntity getAuthorization(String realm, String subject, String resource);

    /**
     * Returns a boolean, 'true', if a subject has authorization for the resource
     * at the level, or greater, of the permission specified in the action parameter.
     * @param realm The security realm that provides context for the users entitlement
     * @param subject The user, or machine, with the authorization.
     * @param resource The target of the authorization, generally an
     * institution such as an NTER or Content Repository instance.
     * @param action An Action entity object containing the type of authorization (READ, WRITE, ADMIN)
     * @return A boolean specifying if the subject has the authorization level,
     * of greater, of the specified action to the resource.
     */
    boolean isAuthorized(String realm, String subject, String resource, Action action);

    /**
     * Returns true if the subject has Global Admin privileges
     * @param subject The user, or machine, with the authorization.
     * @return A boolean specifying if the subject is a Global Admin.
     */
    boolean isGlobalAdmin(String subject);

    /**
     * Returns true if the subject has Global Admin privileges
     * @param realm The security realm that provides context for the users entitlement
     * @param subject The user, or machine, with the authorization.
     * @return A boolean specifying if the subject is a Global Admin.
     */
    boolean isGlobalAdmin(String realm, String subject);

    void addAction(String action);

    ActionEntity getAction(String value);

    List<ActionEntity> getActions();

    List<String> getSubjects();

    List<String> getResources();

    List<String> getRealms();

}
