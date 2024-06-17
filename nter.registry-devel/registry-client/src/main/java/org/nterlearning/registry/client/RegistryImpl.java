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
package org.nterlearning.registry.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class RegistryImpl implements Registry {

    private RegistryInterface registry;
    private String wsdlLocation;
    private RegistryClientCallback registryClientCallback;
    private Logger logger = LoggerFactory.getLogger(RegistryImpl.class);

    public RegistryImpl(String user,
                        String password,
                        String wsdlLocation) {

        registryClientCallback =
                new RegistryClientCallback(user, password);

        this.wsdlLocation = wsdlLocation;
    }

    public RegistryInterface getRegistryService() {
        return getRegistry();
    }

    @Override
    public void createInstitution(@NotNull Institution institution) {
        if (getRegistry() != null) {
            try {
                CreateInstitution request = new CreateInstitution();

                request.setInstitution(institution);

                CreateInstitutionResponse response =
                        registry.createInstitution(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Registry createInstitution [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling createInstitution", e);
            }
        }
    }

    @Override
    public void updateInstitution(@NotNull Institution institution) {
        if (getRegistry() != null) {
            try {
                UpdateInstitution request = new UpdateInstitution();

                request.setInstitution(institution);

                UpdateInstitutionResponse response =
                        registry.updateInstitution(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Registry updateInstitution [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling updateInstitution", e);
            }
        }
    }

    @Override
    public void removeInstitution(@NotNull Long institutionKey) {
        if (getRegistry() != null) {
            try {
                RemoveInstitution request = new RemoveInstitution();

                request.setInstitutionKey(institutionKey);

                RemoveInstitutionResponse response =
                        registry.removeInstitution(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Registry removeInstitution [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling removeInstitution", e);
            }
        }
    }

    @Override
    @Nullable
    public Institution getInstitutionByKey(@NotNull Long institutionKey) {
        Institution institution = null;
        if (getRegistry() != null) {
            try {
                GetInstitutionByKey request = new GetInstitutionByKey();

                request.setInstitutionKey(institutionKey);

                GetInstitutionByKeyResponse response =
                        registry.getInstitutionByKey(request);

                institution = response.getInstitution();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getInstitutionByKey", e);
            }
        }
        return institution;
    }

    @Override
    @Nullable
    public Institution getInstitutionByName(@NotNull String name) {
        Institution institution = null;
        if (getRegistry() != null) {
            try {
                GetInstitutionByName request = new GetInstitutionByName();

                request.setInstitutionName(name);

                GetInstitutionByNameResponse response =
                        registry.getInstitutionByName(request);

                institution = response.getInstitution();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getInstitutionByName", e);
            }
        }
        return institution;
    }

    @Override
    @Nullable
    public Institution getInstitutionByAccessPoint(@NotNull String endPoint) {
        Institution institution = null;
        if (getRegistry() != null) {
            try {
                GetInstitutionByAccessPoint request = new GetInstitutionByAccessPoint();

                request.setEndPoint(endPoint);

                GetInstitutionByAccessPointResponse response =
                        registry.getInstitutionByAccessPoint(request);

                institution = response.getInstitution();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getInstitutionByAccessPoint", e);
            }
        }
        return institution;
    }

    @Override
    @NotNull
    public List<Institution> getInstitutions(@NotNull ActiveStatusEnum status) {
        List<Institution> institutions = new ArrayList<Institution>();
        if (getRegistry() != null) {
            try {
                GetInstitutions request = new GetInstitutions();

                request.setStatus(status);

                GetInstitutionsResponse response =
                        registry.getInstitutions(request);

                institutions = response.getInstitution();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getInstitutions", e);
            }
        }
        return institutions;
    }

    @Override
    public void addService(
            @NotNull Long institutionKey, @NotNull BusinessService service) {
        if (getRegistry() != null) {
            try {
                CreateService request = new CreateService();

                request.setInstitutionKey(institutionKey);
                request.setService(service);

                CreateServiceResponse response =
                        registry.createService(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Registry addService [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling addService", e);
            }
        }
    }

    @Override
    public void updateService(@NotNull BusinessService service) {
        if (getRegistry() != null) {
            try {
                UpdateService request = new UpdateService();

                request.setService(service);

                UpdateServiceResponse response =
                        registry.updateService(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Registry updateService [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling updateService", e);
            }
        }
    }

    @Override
    public void removeService(@NotNull Long key) {
        if (getRegistry() != null) {
            try {
                RemoveService request = new RemoveService();

                request.setServiceKey(key);

                RemoveServiceResponse response =
                        registry.removeService(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Registry removeService [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling removeService", e);
            }
        }
    }

    @Override
    @Nullable
    public BusinessService getServiceByName(
            @NotNull Long institutionKey, @NotNull String serviceName) {

        BusinessService service = null;
        if (getRegistry() != null) {
            try {
                Institution institution = getInstitutionByKey(institutionKey);
                if (institution == null) {
                    throw new RuntimeException(
                        "Unable to find institution using key [" +
                            institutionKey + "]");
                }

                GetServiceByName request = new GetServiceByName();

                request.setInstitutionName(institution.getName());
                request.setServiceName(serviceName);

                GetServiceByNameResponse response =
                        registry.getServiceByName(request);

                service = response.getService();
            } catch (javax.xml.ws.soap.SOAPFaultException e) {
                logger.error("Unexpected exception calling getServiceByName", e);
            }
        }

        return service;
    }

    @Override
    @Nullable
    public BusinessService getServiceByName(
            @NotNull String institutionName, @NotNull String serviceName) {
        BusinessService service = null;
        if (getRegistry() != null) {
            try {
                GetServiceByName request = new GetServiceByName();

                request.setInstitutionName(institutionName);
                request.setServiceName(serviceName);

                GetServiceByNameResponse response =
                        registry.getServiceByName(request);

                service = response.getService();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getServiceByName", e);
            }
        }

        return service;
    }

    @Override
    @Nullable
    public BusinessService getServiceByKey(@NotNull Long serviceKey) {
        BusinessService service = null;
        if (getRegistry() != null) {
            try {
                GetServiceByKey request = new GetServiceByKey();

                request.setServiceKey(serviceKey);

                GetServiceByKeyResponse response =
                        registry.getServiceByKey(request);

                service = response.getService();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getServiceByKey", e);
            }
        }

        return service;
    }

    @Override
    @NotNull
    public List<BusinessService> getServices(@NotNull ActiveStatusEnum status) {
        List<BusinessService> services = new ArrayList<BusinessService>();
        if (getRegistry() != null) {
            try {
                GetServices request = new GetServices();

                request.setStatus(status);

                GetServicesResponse response =
                        registry.getServices(request);

                services = response.getServices();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getServices", e);
            }
        }

        return services;
    }

    @Override
    @NotNull
    public List<BusinessService> getServicesByServiceType(
            @NotNull ServiceTypeEnum serviceType, @NotNull ActiveStatusEnum status) {

        List<BusinessService> services = new ArrayList<BusinessService>();
        if (getRegistry() != null) {
            try {
                GetServicesByServiceType request = new GetServicesByServiceType();

                request.setServiceType(serviceType);
                request.setStatus(status);

                GetServicesByServiceTypeResponse response =
                        registry.getServicesByServiceType(request);

                services = response.getServices();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getServicesByServiceType", e);
            }
        }

        return services;
    }

    @Override
    @NotNull
    public List<BusinessService> getServicesByServiceTypeAndBindingType(
            @NotNull ServiceTypeEnum serviceType,
            @NotNull BindingTypeEnum bindingType,
            @NotNull ActiveStatusEnum status) {

        List<BusinessService> services = new ArrayList<BusinessService>();
        if (getRegistry() != null) {
            try {
                GetServicesByServiceAndBindingType request = new GetServicesByServiceAndBindingType();

                request.setServiceType(serviceType);
                request.setBindingType(bindingType);
                request.setStatus(status);

                GetServicesByServiceAndBindingTypeResponse response =
                        registry.getServicesByServiceAndBindingType(request);

                services = response.getServices();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getServicesByServiceTypeAndBindingType", e);
            }
        }

        return services;
    }

    @Override
    public void addBinding(@NotNull Long serviceKey, @NotNull Binding binding) {
        if (getRegistry() != null) {
            try {
                BusinessService service = getServiceByKey(serviceKey);
                if (service == null) {
                    throw new RuntimeException(
                        "Unable to find service using key [" + serviceKey + "]");
                }

                service.getBinding().add(binding);

                updateService(service);
            } catch (javax.xml.ws.soap.SOAPFaultException e) {
                logger.error("Unexpected exception calling addBinding", e);
            }
        }
    }

    @Override
    public void removeBinding(@NotNull Long bindingKey) {
        if (getRegistry() != null) {
            try {
                RemoveBinding request = new RemoveBinding();

                request.setBindingKey(bindingKey);

                RemoveBindingResponse response =
                        registry.removeBinding(request);

                if (response.getStatus() != RequestStatus.SUCCESS) {
                    logger.error("Unexpected response from Registry removeBinding [" +
                        response.getStatus() + "]");
                }
            } catch (Exception e) {
                logger.error("Unexpected exception calling removeBinding", e);
            }
        }
    }

    @Override
    @NotNull
    public List<Binding> getBindingsByAccessPoint(@NotNull String endPoint) {
        List<Binding> bindings = new ArrayList<Binding>();
        if (getRegistry() != null) {
            try {
                GetBindingsByAccessPoint request = new GetBindingsByAccessPoint();

                request.setAccessPoint(endPoint);

                GetBindingsByAccessPointResponse response =
                        registry.getBindingsByAccessPoint(request);

                bindings = response.getBindings();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getBindingsByAccessPoint", e);
            }
        }

        return bindings;
    }

    @Override
    @Nullable
    public Binding getBindingByKey(@NotNull Long bindingKey) {
        Binding binding = null;
        if (getRegistry() != null) {
            try {
                GetBindingByKey request = new GetBindingByKey();

                request.setKey(bindingKey);

                GetBindingByKeyResponse response =
                        registry.getBindingByKey(request);

                binding = response.getBinding();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getBindingByKey", e);
            }
        }

        return binding;
    }

    @Override
    @NotNull
    public List<BindingTypeEnum> getBindingTypes() {
        List<BindingTypeEnum> bindingTypes = new ArrayList<BindingTypeEnum>();
        if (getRegistry() != null) {
            try {
                GetBindingTypes request = new GetBindingTypes();

                GetBindingTypesResponse response =
                        registry.getBindingTypes(request);

                bindingTypes = response.getBindingType();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getBindingTypes", e);
            }
        }

        return bindingTypes;
    }

    @Override
    @NotNull
    public List<ServiceTypeEnum> getServiceTypes() {
        List<ServiceTypeEnum> serviceTypes = new ArrayList<ServiceTypeEnum>();
        if (getRegistry() != null) {
            try {
                GetServiceTypes request = new GetServiceTypes();

                GetServiceTypesResponse response =
                        registry.getServiceTypes(request);

                serviceTypes = response.getServiceType();
            } catch (Exception e) {
                logger.error("Unexpected exception calling getServiceTypes", e);
            }
        }

        return serviceTypes;
    }

    private RegistryInterface getRegistry() {
        if (registry == null) {
            if (wsdlLocation != null && !wsdlLocation.isEmpty()) {
                logger.info("Attempting to contact Registry Service using [" +
                    wsdlLocation + "]");
                try {
                    URL registryURL = new URL(wsdlLocation);
                    RegistryService registryService = new RegistryService(registryURL);
                    registry =
                            registryService.getRegistryServiceSoap12EndPoint();

                    // WS-Security header
                    Map<String, Object> ctx = new HashMap<String, Object>();
                    ctx.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
                    ctx.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
                    ctx.put(WSHandlerConstants.USER,
                            registryClientCallback.getUsername());
                    ctx.put(WSHandlerConstants.PW_CALLBACK_REF,
                             registryClientCallback);

                    Client client = ClientProxy.getClient(registry);
                    HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
                    HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
                    httpClientPolicy.setConnectionTimeout(10000);
                    httpClientPolicy.setReceiveTimeout(10000);
                    httpConduit.setClient(httpClientPolicy);

                    Endpoint cxfEndpoint = client.getEndpoint();

                    // WS-Security - WSS4J interceptor
                    cxfEndpoint.getOutInterceptors().add(new WSS4JOutInterceptor(ctx));

                    // Logging interceptors
                    if (logger.isDebugEnabled()) {
                        cxfEndpoint.getInInterceptors().add(new LoggingInInterceptor());
                        cxfEndpoint.getOutInterceptors().add(new LoggingOutInterceptor());
                    }
                } catch (Exception e) {
                    logger.error("Unexpected exception connecting to Registry Service at [" +
                        wsdlLocation + "]", e);
                }
            } else {
                logger.warn("Unable to contact registry invalid wsdl url [" +
                        wsdlLocation + "]");
            }
        }
        return registry;
    }


}
