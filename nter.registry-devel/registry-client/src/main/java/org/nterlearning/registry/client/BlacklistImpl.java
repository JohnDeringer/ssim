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

package org.nterlearning.registry.client;

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

import org.nterlearning.registry.blacklist.client.*;
import org.nterlearning.registry.blacklist.client.BlacklistItem;
import org.nterlearning.registry.blacklist.client.RequestStatus;
import org.nterlearning.registry.blacklist.client.ActiveStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/7/12
 */
public class BlacklistImpl implements Blacklist {

    private static BlacklistInterface blacklist;
    private String wsdlLocation;
    private RegistryClientCallback registryClientCallback;
    private Logger logger = LoggerFactory.getLogger(BlacklistImpl.class);

    public BlacklistImpl(String user,
                        String password,
                        String wsdlLocation) {

        registryClientCallback =
                new RegistryClientCallback(user, password);

        this.wsdlLocation = wsdlLocation;
    }

    public BlacklistInterface getBlacklistApi() {
        return getBlacklist();
    }

    @Override
    public void addBlacklistItem(@NotNull BlacklistItem blacklistItem) {
        if (getBlacklist() != null) {
            AddBlacklistItem request = new AddBlacklistItem();

            request.setBlacklistItem(blacklistItem);
            AddBlacklistItemResponse response =
                    getBlacklist().addBlacklistItem(request);

            if (response.getStatus() != org.nterlearning.registry.blacklist.client.RequestStatus.SUCCESS) {
                logger.error("Unexpected response from registry addBlacklistItem [" +
                    response.getStatus() + "]");
            }
        }
    }

    @Override
    public void updateBlacklistItem(@NotNull BlacklistItem blacklistItem) {
        if (getBlacklist() != null) {
            UpdateBlacklistItem request = new UpdateBlacklistItem();

            request.setBlacklistItem(blacklistItem);
            UpdateBlacklistItemResponse response =
                    getBlacklist().updateBlacklistItem(request);

            if (response.getStatus() != RequestStatus.SUCCESS) {
                logger.error("Unexpected response from registry updateBlacklistItem [" +
                    response.getStatus() + "]");
            }
        }
    }

    @Override
    public void removeBlacklistItem(@NotNull String institution,
                                    @Nullable String service) {
        if (getBlacklist() != null) {
            RemoveBlacklistItem request = new RemoveBlacklistItem();

            request.setInstitution(institution);
            if (service != null) {
                request.setService(service);
            }
            RemoveBlacklistItemResponse response =
                    getBlacklist().removeBlacklistItem(request);

            if (response.getStatus() != RequestStatus.SUCCESS) {
                logger.error("Unexpected response from registry removeBlacklistItem [" +
                    response.getStatus() + "]");
            }
        }
    }

    @Override
    @Nullable
    public ActiveStatusEnum getBlacklistStatus(@NotNull String institution,
                                          @Nullable String service) {
        ActiveStatusEnum status = null;
        if (getBlacklist() != null) {
            GetBlacklistStatus request = new GetBlacklistStatus();

            request.setInstitution(institution);
            if (service != null) {
                request.setService(service);
            }
            GetBlacklistStatusResponse response =
                    getBlacklist().getBlacklistStatus(request);

            status = response.getStatus();
        }
        return status;
    }

    @Override
    public void setBlacklistDefault(@NotNull ActiveStatusEnum activeStatusEnum) {
        if (getBlacklist() != null) {
            SetBlacklistDefault request = new SetBlacklistDefault();

            request.setDefaultStatus(activeStatusEnum);
            SetBlacklistDefaultResponse response =
                    getBlacklist().setBlacklistDefault(request);

            if (response.getStatus() != RequestStatus.SUCCESS) {
                logger.error("Unexpected response from registry setBlacklistDefault [" +
                    response.getStatus() + "]");
            }
        }
    }

    private BlacklistInterface getBlacklist() {
        if (blacklist == null) {
            if (wsdlLocation != null && !wsdlLocation.isEmpty()) {
                logger.info("Attempting to contact Registry Service using [" +
                    wsdlLocation + "]");
                try {
                    URL blacklistURL = new URL(wsdlLocation);
                    BlacklistApi blacklistService = new BlacklistApi(blacklistURL);
                    blacklist =
                            blacklistService.getBlacklistSoap12EndPoint();

                    // WS-Security header
                    Map<String, Object> ctx = new HashMap<String, Object>();
                    ctx.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
                    ctx.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
                    ctx.put(WSHandlerConstants.USER,
                            registryClientCallback.getUsername());
                    ctx.put(WSHandlerConstants.PW_CALLBACK_REF,
                             registryClientCallback);

                    Client client = ClientProxy.getClient(blacklist);
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
                    logger.error("Unexpected exception connecting to Registry Service blacklist", e);
                }
            } else {
                logger.warn("Unable to contact registry blacklist invalid wsdl url [" +
                        wsdlLocation + "]");
            }
        }
        return blacklist;
    }
}
