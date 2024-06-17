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

import org.nterlearning.xml.entitlement.entitlement_interface_0_1_0.*;

import javax.ws.rs.*;

import org.apache.cxf.jaxrs.model.wadl.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 */
public interface EntitlementRestInterface {

    @GET
    @Path("/policies/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves all existing entitlement policies", target = DocTarget.METHOD),
       @Description(value = "/", target = DocTarget.REQUEST),
       @Description(value = "A GetAllPoliciesResponse document containing a list of entitlement policies", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves all existing entitlement policies", target = DocTarget.RESOURCE)
    })
    GetAllPoliciesResponse getAllPolicies();

    @GET
    @Path("/policies/subject/{realm}/{subject}/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves existing entitlement policies for a given subject", target = DocTarget.METHOD),
       @Description(value = "parameters: subject", target = DocTarget.REQUEST),
       @Description(value = "A GetPolicyBySubjectResponse document containing a list of entitlement policies", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves existing entitlement policies for a given subject", target = DocTarget.RESOURCE)
    })
    GetPolicyBySubjectResponse getPolicyBySubject(
        @Description("Realm: security realm")
            @PathParam("realm") String realm,
        @Description("Subject: normally a users unique identifier")
            @PathParam("subject") String subject);

    @GET
    @Path("/policies/resource/{realm}/{resource}/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves existing entitlement policies for a given resource", target = DocTarget.METHOD),
       @Description(value = "parameters: resource", target = DocTarget.REQUEST),
       @Description(value = "A GetPolicyByResourceResponse document containing a list of entitlement policies", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves existing entitlement policies for a given resource", target = DocTarget.RESOURCE)
    })
    GetPolicyByResourceResponse getPolicyByResource(
        @Description("Realm: security realm")
            @PathParam("realm") String realm,
        @Description("Resource: normally a NTER instance or Content Provider identifier")
            @PathParam("resource") String resource);

    @GET
    @Path("/policies/resource/{realm}/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves existing entitlement policies for a given security realm", target = DocTarget.METHOD),
       @Description(value = "parameters: resource", target = DocTarget.REQUEST),
       @Description(value = "A GetPolicyByRealmResponse document containing a list of entitlement policies", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves existing entitlement policies for a given security realm", target = DocTarget.RESOURCE)
    })
    GetPolicyByRealmResponse getPolicyByRealm(
        @Description("Realm: security realm")
            @PathParam("realm") String realm);

    @GET
    @Path("/policyByKey/{policyKey}/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves an existing entitlement policy for a given policyKey", target = DocTarget.METHOD),
       @Description(value = "parameters: policyKey, the unique identifier of a policy", target = DocTarget.REQUEST),
       @Description(value = "A GetPolicyByKeyResponse document contains requested the entitlement policy", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves an existing entitlement policy for a given policyKey", target = DocTarget.RESOURCE)
    })
    GetPolicyByKeyResponse getPolicyByKey(
        @Description("Realm: security realm")
            @PathParam("policyKey") Long policyKey);

    @GET
    @Path("/policy/{realm}/{subject}/{resource}/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves an existing entitlement policy for a given realm, subject an resource", target = DocTarget.METHOD),
       @Description(value = "parameters: subject, resource", target = DocTarget.REQUEST),
       @Description(value = "A GetPolicyByResponse document contains requested the entitlement policy", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves an existing entitlement policy for a given realm, subject and resource", target = DocTarget.RESOURCE)
    })
    GetPolicyResponse getPolicy(
        @Description("Realm: security realm")
            @PathParam("realm") String realm,
        @Description("Subject: normally a users unique identifier")
            @PathParam("subject") String subject,
        @Description("Resource: normally a NTER instance or Content Provider identifier")
            @PathParam("resource") String resource);

    @GET
    @Path("/authorize/{realm}/{subject}/{resource}/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves the authorization permission for a given realm, subject and resource", target = DocTarget.METHOD),
       @Description(value = "parameters: subjectId, resourceId", target = DocTarget.REQUEST),
       @Description(value = "A GetAuthorizationResponse document containing the authorization permission", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves the authorization permission for a given realm, subject and resource", target = DocTarget.RESOURCE)
    })
    GetAuthorizationResponse getAuthorization(
        @Description("Realm: security realm")
            @PathParam("realm") String realm,
        @Description("Subject: normally a users unique identifier")
            @PathParam("subject") String subject,
        @Description("Resource: normally a NTER instance or Content Provider identifier")
            @PathParam("resource") String resource);

    @GET
    @Path("/subjects/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves all existing subjects/users", target = DocTarget.METHOD),
       @Description(value = "/", target = DocTarget.REQUEST),
       @Description(value = "A GetSubjectsResponse document containing a list of subjects/users", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves all existing subjects", target = DocTarget.RESOURCE)
    })
    GetSubjectsResponse getSubjects();

    @GET
    @Path("/resources/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves all existing resources", target = DocTarget.METHOD),
       @Description(value = "/", target = DocTarget.REQUEST),
       @Description(value = "A GetResourcesResponse document containing a list of resources", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves all existing resources", target = DocTarget.RESOURCE)
    })
    GetResourcesResponse getResources();

    @GET
    @Path("/actions/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves a list of permission actions", target = DocTarget.METHOD),
       @Description(value = "/", target = DocTarget.REQUEST),
       @Description(value = "A GetActionsResponse document containing a list of permission action", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves permission actions", target = DocTarget.RESOURCE)
    })
    GetActionsResponse getActions();

    @GET
    @Path("/realms/")
    @Produces("application/xml")
    @Descriptions({
       @Description(value = "Retrieves a list of security realms", target = DocTarget.METHOD),
       @Description(value = "/", target = DocTarget.REQUEST),
       @Description(value = "A GetRealmsResponse document containing a list of security realms", target = DocTarget.RESPONSE),
       @Description(value = "Retrieves security realms", target = DocTarget.RESOURCE)
    })
    GetRealmsResponse getRealms();

}