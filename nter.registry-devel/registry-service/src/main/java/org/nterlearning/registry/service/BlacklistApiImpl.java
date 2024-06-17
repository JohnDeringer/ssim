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

import org.nterlearning.registry.model.BlacklistModel;

import org.apache.log4j.Logger;

import org.nterlearning.xml.nter_registry.blacklist_interface_0_1_0.*;
import org.nterlearning.xml.nter_registry.blacklist_interface_0_1_0_wsdl.BlacklistInterface;
import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.BlacklistItem;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.RequestStatus;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/13/11
 */
public class BlacklistApiImpl implements BlacklistInterface {

    private BlacklistModel blacklistModel;
    private Logger logger = Logger.getLogger(BlacklistApiImpl.class);

    @Override
    public AddBlacklistItemResponse addBlacklistItem(AddBlacklistItem request) {

        AddBlacklistItemResponse response = new AddBlacklistItemResponse();
        response.setStatus(
                blacklistModel.addBlacklistItem(
                        request.getBlacklistItem()
                )
        );

        return response;
    }

    @Override
    public UpdateBlacklistItemResponse updateBlacklistItem(UpdateBlacklistItem request) {

        UpdateBlacklistItemResponse response = new UpdateBlacklistItemResponse();
        response.setStatus(
                blacklistModel.updateBlacklistItem(
                        request.getBlacklistItem()
                )
        );

        return response;
    }

    @Override
    public RemoveBlacklistItemResponse removeBlacklistItem(
            RemoveBlacklistItem request) {

        RemoveBlacklistItemResponse response = new RemoveBlacklistItemResponse();
        response.setStatus(
                blacklistModel.removeBlacklistItem(
                        request.getInstitution(),
                        request.getService()
                )
        );

        return response;
    }

    @Override
    public GetBlacklistStatusResponse getBlacklistStatus(
            GetBlacklistStatus request) {
        ActiveStatusEnum status;

        GetBlacklistStatusResponse response = new GetBlacklistStatusResponse();

        BlacklistItem blacklistItem =
                blacklistModel.getBlacklistItem(
                        request.getInstitution(),
                        request.getService());
        if (blacklistItem != null) {
            status = blacklistItem.getStatus();
            response.setStatus(status);
        } else {
            logger.warn("Unable to retrieve blacklist item for [" +
                request.getInstitution() + ":" + request.getService() + "]");
        }

        return response;
    }

    @Override
    public SetBlacklistDefaultResponse setBlacklistDefault(
            SetBlacklistDefault request) {
        SetBlacklistDefaultResponse response = new SetBlacklistDefaultResponse();

        ActiveStatusEnum activeStatusEnum = request.getDefaultStatus();
        if (activeStatusEnum == null) {
            throw new RuntimeException(
                    "Unable to set default blacklist status with 'Null' value");
        }

        blacklistModel.setBlacklistDefault(activeStatusEnum);

        response.setStatus(RequestStatus.SUCCESS);

        return response;
    }

    public void setBlacklistModel(BlacklistModel blacklistModel) {
        this.blacklistModel = blacklistModel;
    }

}
