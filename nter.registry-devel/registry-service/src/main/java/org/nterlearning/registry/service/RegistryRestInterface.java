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
package org.nterlearning.registry.service;

import org.apache.cxf.jaxrs.model.wadl.Description;
import org.apache.cxf.jaxrs.model.wadl.Descriptions;
import org.apache.cxf.jaxrs.model.wadl.DocTarget;
import org.nterlearning.xml.nter_registry.registry_interface_0_1_0.*;

import javax.ws.rs.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 6/22/11
 */
public interface RegistryRestInterface {

    @GET
    @Path("/institution/{name}/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieve an institution from JUDDI given the institution name.", target = DocTarget.METHOD),
            @Description(value = "name: A unique identifier of the institution.", target = DocTarget.REQUEST),
            @Description(value = "Returns an Institution object wrapped in a GetInstitutionByNameResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieve an institution.", target = DocTarget.RESOURCE)
    })
    GetInstitutionByNameResponse getInstitutionByName(
            @Description("The name of the institution to be retrieved")
            @PathParam("name")
            String name);
/*
    @GET
    @Path("/institution/key/{key}/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieve an institution from JUDDI given the institution key", target = DocTarget.METHOD),
            @Description(value = "key: A unique identifier of the institution.", target = DocTarget.REQUEST),
            @Description(value = "Returns an Institution object wrapped in a GetInstitutionByKeyResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieve an institution.", target = DocTarget.RESOURCE)
    })
    GetInstitutionByKeyResponse getInstitutionByKey(
            @Description("The unique KeyedReference identifier of the Institution to be returned.")
            @PathParam("key")
            String key);
*/

    @GET
    @Path("/institutions/{activeStatus}/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieves a list of Institution objects from JUDDI", target = DocTarget.METHOD),
            @Description(value = "Returns a List of Institution objects wrapped in a GetInstitutionsResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieves a list of Institution objects", target = DocTarget.RESOURCE)
    })
    GetInstitutionsResponse getInstitutions(
            @Description("'Active', 'BlackList', 'Unspecified'")
            @PathParam("activeStatus")
            String activeStatus);

    @GET
    @Path("/service/name/{name}/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieve a service from JUDDI given the service name.", target = DocTarget.METHOD),
            @Description(value = "name: A unique identifier of the service.", target = DocTarget.REQUEST),
            @Description(value = "Returns a BusinessService object wrapped in a GetServiceByNameResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieve a service.", target = DocTarget.RESOURCE)
    })
    GetServiceByNameResponse getServiceByName(
            @Description("The name of the service to be retrieved.")
            @PathParam("name")
            String name);

 /*
    @GET
    @Path("/service/key/{key}/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieve a service from JUDDI given the service key", target = DocTarget.METHOD),
            @Description(value = "name: A unique identifier of the service.", target = DocTarget.REQUEST),
            @Description(value = "Returns a BusinessService object wrapped in a GetServiceByKeyResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieve a service", target = DocTarget.RESOURCE)
    })
    GetServiceByKeyResponse getServiceByKey(
            @Description("key: the unique KeyedReference identifier of the service to be retrieved")
            @PathParam("key")
            String key);
 */

    @GET
    @Path("/services/{activeStatus}/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieve a list of services from the NTER registry filtered by status", target = DocTarget.METHOD),
            @Description(value = "activeStatus: e.g. {'Active', 'BlackList', 'Unspecified'}, etc.", target = DocTarget.REQUEST),
            @Description(value = "Returns a list of BusinessService objects wrapped in a GetServicesResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieve a list of services", target = DocTarget.RESOURCE)
    })
    GetServicesResponse getServices(
            @Description("'Active', 'BlackList', 'Unspecified' etc. see /statusTypes")
            @PathParam("activeStatus")
            String activeStatus);

    @GET
    @Path("/service/serviceType/{serviceType}/{activeStatus}/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieves a list of BusinessService objects from JUDDI given a serviceType", target = DocTarget.METHOD),
            @Description(value = "serviceType: {'Course Feed', 'Student Feed', 'Course Search', 'Pub-Sub'}, activeStatus: {'Active', 'Inactive', 'BlackList'}.", target = DocTarget.REQUEST),
            @Description(value = "Returns a BusinessService object wrapped in a GetServiceByServiceTypeResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieves a list of services", target = DocTarget.RESOURCE)
    })
    GetServicesByServiceTypeResponse getServicesByServiceType(
            @Description("'Course Feed', 'Student Feed', 'Course Search', 'Pub-Sub', etc. see /serviceTypes")
            @PathParam("serviceType")
            String serviceType,
            @Description("'Active', 'BlackList', 'Unspecified'")
            @PathParam("activeStatus")
            String activeStatus);

    @GET
    @Path("/service/{serviceType}/{bindingType}/{activeStatus}/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieves a list of BusinessService objects from JUDDI given a ServiceType and BindingType", target = DocTarget.METHOD),
            @Description(value = "serviceType: {'Course Feed', 'Student Feed', 'Course Search', 'Pub-Sub'}, bindingType: {'REST', 'WSDLv1.1', 'WSDLv1.2', 'XML/HTTP'}, activeStatus: {'Active', 'Inactive', 'BlackList'}.", target = DocTarget.REQUEST),
            @Description(value = "Returns a list of BusinessService object wrapped in a GetServiceByServiceAndBindingTypeResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieves a list of services", target = DocTarget.RESOURCE)
    })
    GetServicesByServiceAndBindingTypeResponse getServicesByServiceAndBindingType(
            @Description("'Course Feed', 'Student Feed', 'Solr Instance', 'Pub-Sub', etc. see /serviceTypes")
            @PathParam("serviceType")
            String serviceType,
            @Description("'REST', 'WSDLv1.1', 'WSDLv1.2', 'XML/HTTP', etc. see /bindingTypes")
            @PathParam("bindingType")
            String bindingType,
            @Description("'Active', 'BlackList', 'Unspecified'")
            @PathParam("activeStatus")
            String activeStatus);

    @GET
    @Path("/serviceTypes/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieves a list of ServiceType tModel objects from JUDDI", target = DocTarget.METHOD),
            @Description(value = "Returns a List of ServiceType tModel objects wrapped in a GetServiceTypesResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieves a list of service Types", target = DocTarget.RESOURCE)
    })
    GetServiceTypesResponse getServiceTypes();

    @GET
    @Path("/bindingTypes/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieves a list of BindingType tModel objects from JUDDI", target = DocTarget.METHOD),
            @Description(value = "Returns a List of BindingType tModel objects wrapped in a GetBindingTypesResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieves a list of binding types", target = DocTarget.RESOURCE)
    })
    GetBindingTypesResponse getBindingTypes();

    @GET
    @Path("/statusTypes/")
    @Produces("application/xml")
    @Descriptions({
            @Description(value = "Retrieves a list of ActiveStatus objects from JUDDI", target = DocTarget.METHOD),
            @Description(value = "Returns a List of ActiveStatus tModel objects wrapped in a GetStatusTypesResponse object.", target = DocTarget.RESPONSE),
            @Description(value = "Retrieves the list of status types", target = DocTarget.RESOURCE)
    })
    GetStatusTypesResponse getStatusTypes();

}
