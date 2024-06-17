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

import org.nterlearning.entitlement.model.EntitlementModel;

import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Action;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.EntitlementPolicy;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.RequestStatus;
import org.nterlearning.xml.entitlement.domain_objects_0_1_0.Subject;
import org.nterlearning.xml.entitlement.entitlement_interface_0_1_0.*;
import org.nterlearning.xml.entitlement.entitlement_interface_0_1_0_wsdl.EntitlementInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class EntitlementImpl
        implements EntitlementRestInterface, EntitlementInterface {

    private EntitlementModel model;
    private WSValidator wsValidator;

    private Logger logger = LoggerFactory.getLogger(EntitlementImpl.class);

    @Override
    public CreatePolicyResponse createPolicy(CreatePolicy request) {
        RequestStatus requestStatus;
        String authorizingSubject = wsValidator.getUid();

        if (authorizingSubject != null && !authorizingSubject.isEmpty()) {
            EntitlementPolicy entitlementPolicy = request.getEntitlementPolicy();
            String realm = entitlementPolicy.getRealm();
            String subject = entitlementPolicy.getSubject();
            String resource = entitlementPolicy.getResource();
            Action action = entitlementPolicy.getAction();

            logger.info("Creating policy realm [" + realm + "] subject [" +
                    subject + "] resource [" + resource +
                    "] action [" + action + "] authorizingSubject [" +
                    authorizingSubject + "]");

            if (action == null) {
                throw new RuntimeException(
                    "Unable to create policy with incorrect action. Correct action values are [" +
                    getPrintableActions() + "]");
            }

            model.createPolicy(realm, subject, resource, action.value(), authorizingSubject);

            requestStatus = RequestStatus.SUCCESS;
        } else {
            throw new RuntimeException(
                "Unable to createPolicy, missing user from security header");
        }

        // Handle response
        CreatePolicyResponse response = new CreatePolicyResponse();
        response.setStatus(requestStatus);
        return response;
    }

    @Override
    public UpdatePolicyResponse updatePolicy(UpdatePolicy request) {
        String authorizingSubject = wsValidator.getUid();

        if (authorizingSubject != null && !authorizingSubject.isEmpty()) {
            EntitlementPolicy entitlementPolicy = request.getEntitlementPolicy();
            String realm = entitlementPolicy.getRealm();
            String subject = entitlementPolicy.getSubject();
            String resource = entitlementPolicy.getResource();
            Action action = entitlementPolicy.getAction();

            if (action == null) {
                throw new RuntimeException(
                    "Unable to create policy with incorrect action. Correct action values are [" +
                    getPrintableActions() + "]");
            }

            model.updatePolicy(realm, subject, resource, action.value(), authorizingSubject);
        } else {
            throw new RuntimeException(
                "Unable to updatePolicy, missing user from security header");
        }

        // Handle response
        UpdatePolicyResponse response = new UpdatePolicyResponse();
        response.setStatus(RequestStatus.SUCCESS);
        return response;
    }

    @Override
    public RemovePolicyResponse removePolicy(RemovePolicy request) {
        String authorizingSubject = wsValidator.getUid();

        if (authorizingSubject != null && !authorizingSubject.isEmpty()) {
            String realm = request.getRealm();
            String resource = request.getResource();
            String subject = request.getSubject();

            model.removePolicy(realm, subject, resource, authorizingSubject);
        } else {
            throw new RuntimeException(
                "Unable to removePolicy, missing user from security header");
        }

        // Handle response
        RemovePolicyResponse response = new RemovePolicyResponse();
        response.setStatus(RequestStatus.SUCCESS);
        return response;
    }

    @Override
    public RemovePolicyByKeyResponse removePolicyByKey(RemovePolicyByKey request) {
        String authorizingSubject = wsValidator.getUid();

        if (authorizingSubject != null && !authorizingSubject.isEmpty()) {
            Long policyKey = request.getPolicyKey();

            model.removePolicy(policyKey, authorizingSubject);
        } else {
            throw new RuntimeException(
                "Unable to removePolicy, missing user from security header");
        }

        // Handle response
        RemovePolicyByKeyResponse response = new RemovePolicyByKeyResponse();
        response.setStatus(RequestStatus.SUCCESS);
        return response;
    }

    @Override
    public GetAllPoliciesResponse getAllPolicies(GetAllPolicies request) {
        return getAllPolicies();
    }

    @Override
    public GetPolicyBySubjectResponse getPolicyBySubject(
            GetPolicyBySubject request) {
        String realm = request.getRealm();
        String subject = request.getSubject();
        return getPolicyBySubject(realm, subject);
    }

    @Override
    public GetPolicyByResourceResponse getPolicyByResource(
            GetPolicyByResource request) {
        String realm = request.getRealm();
        String resource = request.getResource();
        return getPolicyByResource(realm, resource);
    }

    @Override
    public GetPolicyByRealmResponse getPolicyByRealm(
            GetPolicyByRealm request) {
        String realm = request.getRealm();
        return getPolicyByRealm(realm);
    }

    @Override
    public GetPolicyResponse getPolicy(
            GetPolicy request) {
        String realm = request.getRealm();
        String subject = request.getSubject();
        String resource = request.getResource();
        return getPolicy(realm, subject, resource);
    }

    @Override
    public GetPolicyByKeyResponse getPolicyByKey(
            GetPolicyByKey request) {
        Long policyKey = request.getPolicyKey();
        return getPolicyByKey(policyKey);
    }

    @Override
    public GetAuthorizationResponse getAuthorization(GetAuthorization request) {
        String realm = request.getRealm();
        String subject = request.getSubject();
        String resource = request.getResource();
        return getAuthorization(realm, subject, resource);
    }

    @Override
    public GetSubjectsResponse getSubjects(GetSubjects request) {
        return getSubjects();
    }

    @Override
    public GetResourcesResponse getResources(GetResources request) {
        return getResources();
    }

    @Override
    public GetActionsResponse getActions(GetActions parameters) {
        return getActions();
    }

    @Override
    public GetRealmsResponse getRealms(GetRealms parameters) {
        return getRealms();
    }

    /** REST API **/

    @Override
    public GetAllPoliciesResponse getAllPolicies() {
        // Handle request
        List<EntitlementPolicy> policyList = model.getAllPolicies();
        // Handle response
        GetAllPoliciesResponse response = new GetAllPoliciesResponse();
        response.getEntitlementPolicy().addAll(policyList);
        return response;
    }

    @Override
    public GetPolicyBySubjectResponse getPolicyBySubject(String realm, String subject) {
        // Handle request
        List<EntitlementPolicy> policyList =
                model.getPolicyBySubject(realm, subject);
        // Handle response
        GetPolicyBySubjectResponse response = new GetPolicyBySubjectResponse();
        response.getEntitlementPolicy().addAll(policyList);
        return response;
    }

    @Override
    public GetPolicyByResourceResponse getPolicyByResource(String realm, String resource) {
        // Handle request
        List<EntitlementPolicy> policyList =
                model.getPolicyByResource(realm, resource);
        // Handle response
        GetPolicyByResourceResponse response =
                new GetPolicyByResourceResponse();
        response.getEntitlementPolicy().addAll(policyList);
        return response;
    }

    @Override
    public GetPolicyByRealmResponse getPolicyByRealm(String realm) {
        // Handle request
        List<EntitlementPolicy> policyList =
                model.getPolicyByRealm(realm);
        // Handle response
        GetPolicyByRealmResponse response =
                new GetPolicyByRealmResponse();
        response.getEntitlementPolicy().addAll(policyList);
        return response;
    }

    @Override
    public GetPolicyByKeyResponse getPolicyByKey(Long policyKey) {
        GetPolicyByKeyResponse response = new GetPolicyByKeyResponse();
        EntitlementPolicy policy = model.getPolicy(policyKey);
        response.setEntitlementPolicy(policy);
        return response;
    }

    @Override
    public GetPolicyResponse getPolicy(
            String realm, String subjectId, String resourceId) {
        GetPolicyResponse response = new GetPolicyResponse();
        EntitlementPolicy policy = model.getPolicy(realm, subjectId, resourceId);
        response.setEntitlementPolicy(policy);
        return response;
    }

    @Override
    public GetAuthorizationResponse getAuthorization(
            String realm, String subjectId, String resourceId) {
        // Handle request
        Action action = model.getAuthorization(realm, subjectId, resourceId);
        // Handle response
        GetAuthorizationResponse response = new GetAuthorizationResponse();
        response.setAction(action);
        return response;
    }

    @Override
    /**
     * Get the list of users from the identity provider
     */
    public GetSubjectsResponse getSubjects() {
        // Response
        GetSubjectsResponse response = new GetSubjectsResponse();
        List<Subject> subjects = new ArrayList<Subject>();

        try {
            for (String subjectId : model.getSubjects()) {
                Subject subject = new Subject();
                subject.setSubjectId(subjectId);

                subjects.add(subject);
            }
        } catch (Exception e) {
            logger.error("Unexpected error retrieving users from the identity service", e);
        }

        response.getSubject().addAll(subjects);

        return response;
    }

    @Override
    public GetResourcesResponse getResources() {
        GetResourcesResponse response = new GetResourcesResponse();
        List<String> resourceList = model.getResources();
        response.getResource().addAll(resourceList);

        return response;
    }

    @Override
    public GetActionsResponse getActions() {
        GetActionsResponse response = new GetActionsResponse();
        List<Action> actionList = model.getActions();
        response.getAction().addAll(actionList);

        return response;
    }

    @Override
    public GetRealmsResponse getRealms() {
        GetRealmsResponse response = new GetRealmsResponse();
        List<String> realms = model.getRealms();
        response.getRealm().addAll(realms);

        return response;
    }

    /** Non-interface public methods **/

    /**
     * Dependency injection
     * @param model EntitlementModel
     */
    public void setEntitlementModel(EntitlementModel model) {
        this.model = model;
    }

    /**
     * Dependency injection
     * @param wsValidator WSValidator
     */
    public void setWsValidator(WSValidator wsValidator) {
        this.wsValidator = wsValidator;
    }

    private String getPrintableActions() {
        StringBuilder sb = new StringBuilder();
        Action[] actions = Action.values();
        for (int i = 0; i < actions.length; i++) {
            sb.append(actions[i].value());
            if (i < actions.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
