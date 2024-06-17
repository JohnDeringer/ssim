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
package org.nterlearning.entitlement.client;


import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 3/2/12
 */
public class EntitlementService implements Entitlement {

    private static EntitlementInterface entitlementService;
    private String wsdlLocation = null;
    private EntitlementClientCallback entitlementClientCallback;
    private String user = null;
    private Logger logger = LoggerFactory.getLogger(EntitlementService.class);

    public EntitlementService(String user,
                              String password,
                              String wsdlLocation) {
        entitlementClientCallback = new EntitlementClientCallback();
        this.user = user;
        entitlementClientCallback.setUsername(this.user);
        entitlementClientCallback.setPassword(password);

        this.wsdlLocation = wsdlLocation;
    }

    @Override
    public void createPolicy(@Nullable String realm,
                             @NotNull String subject,
                             @NotNull String resource,
                             @NotNull Action action) {
        if (getEntitlementService() != null) {
            CreatePolicy request = new CreatePolicy();

            EntitlementPolicy policy = new EntitlementPolicy();
            if (realm != null && !realm.isEmpty()) {
                policy.setRealm(realm);
            }
            policy.setSubject(subject);
            policy.setResource(resource);
            policy.setAction(action);

            request.setEntitlementPolicy(policy);

            try {
                CreatePolicyResponse response = entitlementService.createPolicy(request);
                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Entitlement createPolicy [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.createPolicy", e);
            }
        }
    }

    @Override
    public void updatePolicy(@Nullable String realm,
                             @NotNull String subject,
                             @NotNull String resource,
                             @NotNull Action action) {
        if (getEntitlementService() != null) {
            UpdatePolicy request = new UpdatePolicy();

            EntitlementPolicy policy = new EntitlementPolicy();
            if (realm != null && !realm.isEmpty()) {
                policy.setRealm(realm);
            }
            policy.setSubject(subject);
            policy.setResource(resource);
            policy.setAction(action);

            request.setEntitlementPolicy(policy);

            try {
                UpdatePolicyResponse response =
                        entitlementService.updatePolicy(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Entitlement updatePolicy [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.updatePolicy", e);
            }
        }
    }

    @Override
    public void removePolicy(@NotNull Long policyKey) {
        if (getEntitlementService() != null) {
            RemovePolicyByKey request = new RemovePolicyByKey();
            request.setPolicyKey(policyKey);

            try {
                RemovePolicyByKeyResponse response =
                        entitlementService.removePolicyByKey(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Entitlement removePolicy [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.removePolicy", e);
            }
        }
    }

    @Override
    public void removePolicy(@Nullable String realm,
                             @NotNull String subject,
                             @NotNull String resource) {
        if (getEntitlementService() != null) {
            RemovePolicy request = new RemovePolicy();
            if (realm != null && !realm.isEmpty()) {
                request.setRealm(realm);
            }
            request.setSubject(subject);
            request.setResource(resource);

            try {
                RemovePolicyResponse response =
                        entitlementService.removePolicy(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Entitlement removePolicy [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.removePolicy", e);
            }
        }
    }

    @Override
    @NotNull
    public List<EntitlementPolicy> getAllPolicies() {
        List<EntitlementPolicy> policies = new ArrayList<EntitlementPolicy>();
        if (getEntitlementService() != null) {
            GetAllPolicies request = new GetAllPolicies();

            try {
                GetAllPoliciesResponse response =
                        entitlementService.getAllPolicies(request);

                policies = response.getEntitlementPolicy();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getAllPolicies", e);
            }
        }
        return policies;
    }

    @Override
    public List<EntitlementPolicy> getPoliciesBySubject(@Nullable String realm,
                                                      @NotNull String subject) {
        List<EntitlementPolicy> policies = new ArrayList<EntitlementPolicy>();
        if (getEntitlementService() != null) {
            GetPolicyBySubject request = new GetPolicyBySubject();
            if (realm != null && !realm.isEmpty()) {
                request.setRealm(realm);
            }
            request.setSubject(subject);

            try {
                GetPolicyBySubjectResponse response =
                        entitlementService.getPolicyBySubject(request);

                policies = response.getEntitlementPolicy();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getPoliciesBySubject", e);
            }
        }
        return policies;
    }

    @Override
    @NotNull
    public List<EntitlementPolicy> getPoliciesByResource(@Nullable String realm,
                                                       @NotNull String resource) {
        List<EntitlementPolicy> policies = new ArrayList<EntitlementPolicy>();
        if (getEntitlementService() != null) {
            GetPolicyByResource request = new GetPolicyByResource();
            if (realm != null && !realm.isEmpty()) {
                request.setRealm(realm);
            }
            request.setResource(resource);

            try {
                GetPolicyByResourceResponse response =
                        entitlementService.getPolicyByResource(request);

                policies = response.getEntitlementPolicy();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getPoliciesByResource", e);
            }
        }
        return policies;
    }

    @Override
    @NotNull
    public List<EntitlementPolicy> getPoliciesByRealm(@Nullable String realm) {
        List<EntitlementPolicy> policies = new ArrayList<EntitlementPolicy>();
        if (getEntitlementService() != null) {
            GetPolicyByRealm request = new GetPolicyByRealm();
            if (realm != null && !realm.isEmpty()) {
                request.setRealm(realm);
            }

            try {
                GetPolicyByRealmResponse response =
                        entitlementService.getPolicyByRealm(request);

                policies = response.getEntitlementPolicy();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getPoliciesByRealm", e);
            }
        }
        return policies;
    }

    @Override
    @Nullable
    public EntitlementPolicy getPolicy(@NotNull Long policyKey) {
        EntitlementPolicy policy = null;
        if (getEntitlementService() != null) {
            GetPolicyByKey request = new GetPolicyByKey();
            request.setPolicyKey(policyKey);

            try {
                GetPolicyByKeyResponse response =
                        entitlementService.getPolicyByKey(request);

                policy = response.getEntitlementPolicy();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getPolicy", e);
            }
        }
        return policy;
    }

    @Override
    @Nullable
    public EntitlementPolicy getPolicy(@Nullable String realm,
                                       @NotNull String subject,
                                       @NotNull String resource) {
        EntitlementPolicy policy = null;
        if (getEntitlementService() != null) {
            GetPolicy request = new GetPolicy();
            if (realm != null && !realm.isEmpty()) {
                request.setRealm(realm);
            }
            request.setSubject(subject);
            request.setResource(resource);

            try {
                GetPolicyResponse response =
                        entitlementService.getPolicy(request);

                policy = response.getEntitlementPolicy();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getPolicy", e);
            }
        }
        return policy;
    }

    @Override
    @NotNull
    public Action getAuthorization(@Nullable String realm,
                                   @NotNull String subject,
                                   @NotNull String resource) {
        Action action = Action.NONE;
        if (getEntitlementService() != null) {
            GetAuthorization request = new GetAuthorization();
            if (realm != null && !realm.isEmpty()) {
                request.setRealm(realm);
            }
            request.setSubject(subject);
            request.setResource(resource);

            try {
                GetAuthorizationResponse response =
                        entitlementService.getAuthorization(request);

                action = response.getAction();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getAuthorization", e);
            }
        }
        return action;
    }

    @Override
    @NotNull
    public List<Action> getActions() {
        List<Action> actions = new ArrayList<Action>();
        if (getEntitlementService() != null) {
            GetActions request = new GetActions();

            try {
                GetActionsResponse response =
                        entitlementService.getActions(request);

                actions = response.getAction();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getActions", e);
            }
        }
        return actions;
    }

    @Override
    @NotNull
    public List<String> getRealms() {
        List<String> realms = new ArrayList<String>();
        if (getEntitlementService() != null) {
            GetRealms request = new GetRealms();

            try {
                GetRealmsResponse response =
                        entitlementService.getRealms(request);

                realms = response.getRealm();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getRealms", e);
            }
        }
        return realms;
    }

    @Override
    @NotNull
    public List<String> getSubjects() {
        List<String> subjectIds = new ArrayList<String>();
        if (getEntitlementService() != null) {
            GetSubjects request = new GetSubjects();

            try {
                GetSubjectsResponse response =
                        entitlementService.getSubjects(request);
                for (Subject subject : response.getSubject()) {
                    subjectIds.add(subject.getSubjectId());
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getSubjects", e);
            }
        }
        return subjectIds;
    }

    @Override
    @NotNull
    public List<String> getResources() {
        List<String> resources = new ArrayList<String>();
        if (getEntitlementService() != null) {
            GetResources request = new GetResources();

            try {
                GetResourcesResponse response =
                        entitlementService.getResources(request);

                resources = response.getResource();
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.getSubjects", e);
            }
        }
        return resources;
    }

    @Override
    public boolean isAuthorized(@Nullable String realm,
                                @NotNull String subject,
                                @NotNull String resource,
                                @NotNull Action action) {
        boolean isAuthorized = false;
        if (getEntitlementService() != null) {
            GetAuthorization request = new GetAuthorization();
            if (realm != null && !realm.isEmpty()) {
                request.setRealm(realm);
            }
            request.setSubject(subject);
            request.setResource(resource);

            try {
                GetAuthorizationResponse response =
                        entitlementService.getAuthorization(request);

                Action responseAction = response.getAction();
                if (responseAction.ordinal() >= action.ordinal()) {
                    isAuthorized = true;
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.isAuthorized", e);
            }
        }
        return isAuthorized;
    }

    @Override
    public boolean isGlobalAdmin(@Nullable String realm, @NotNull String subject) {
        boolean isGlobalAdmin = false;
        if (getEntitlementService() != null) {
            GetPolicyBySubject request = new GetPolicyBySubject();
            request.setSubject(subject);

            try {
                GetPolicyBySubjectResponse response =
                        entitlementService.getPolicyBySubject(request);

                for (EntitlementPolicy policy : response.getEntitlementPolicy()) {
                    if (policy.getAction() == Action.GLOBAL_ADMIN) {
                        isGlobalAdmin = true;
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling entitlement.isGlobalAdmin", e);
            }
        }
        return isGlobalAdmin;
    }

    @Override
    public String getUser() {
        return user;
    }

    private EntitlementInterface getEntitlementService() {
        if (entitlementService == null) {
            if (wsdlLocation != null && !wsdlLocation.isEmpty()) {
                logger.info("Attempting to contact the Entitlement service using [" +
                        wsdlLocation + "]");
                try {
                    URL uRL = new URL(wsdlLocation);
                    EntitlementAPI entitlementApi = new EntitlementAPI(uRL);
                    entitlementService =
                            entitlementApi.getEntitlementImplPort();

                    // WS-Security header
                    Map<String, Object> ctx = new HashMap<String, Object>();

                    ctx.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
                    ctx.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
                    ctx.put(WSHandlerConstants.USER,
                            entitlementClientCallback.getUsername());
                    ctx.put(WSHandlerConstants.PW_CALLBACK_REF,
                             entitlementClientCallback);

                    Client client = ClientProxy.getClient(entitlementService);
                    HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

                    HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
                    httpClientPolicy.setConnectionTimeout(10000);
                    httpClientPolicy.setReceiveTimeout(10000);
                    httpClientPolicy.setAutoRedirect(true);
                    httpClientPolicy.setMaxRetransmits(0);

                    httpConduit.setClient(httpClientPolicy);

                    Endpoint cxfEndpoint = client.getEndpoint();
                    cxfEndpoint.getOutInterceptors().add(new WSS4JOutInterceptor(ctx));

                    if (logger.isDebugEnabled()) {
                        cxfEndpoint.getInInterceptors().add(new LoggingInInterceptor());
                        cxfEndpoint.getOutInterceptors().add(new LoggingOutInterceptor());
                    }

                } catch (Exception e) {
                    logger.error("Unexpected exception connecting to Entitlement service", e);
                }
            } else {
                logger.warn("Invalid Entitlement wsdl url [" + wsdlLocation + "], Entitlement will not be accessible");
            }
        }
        return entitlementService;
    }


}
