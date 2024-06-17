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
package org.nterlearning.registry.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nterlearning.registry.persistence.ActiveStatusEntity;
import org.nterlearning.registry.persistence.BlacklistDao;
import org.nterlearning.registry.persistence.BlacklistEntity;

import org.apache.log4j.Logger;

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.BlacklistItem;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.RequestStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/13/11
 */
public class BlacklistModelImpl implements BlacklistModel, Serializable {

    private BlacklistDao blacklistDao;
    private static ActiveStatusEnum defaultBlacklistStatus = null;
    private Logger logger = Logger.getLogger(BlacklistModelImpl.class);

    @Override
    @NotNull
    public RequestStatus addBlacklistItem(@NotNull BlacklistItem blacklistItem) {
        RequestStatus requestStatus;

        ActiveStatusEnum activeStatus = blacklistItem.getStatus();

        if (activeStatus == null) {
            logger.warn("ActiveStatus is 'Null', defaulting to [" +
                    getDefaultBlacklistStatus() + "]");
            activeStatus = getDefaultBlacklistStatus();
        }

        // See if blacklist item already exists
        BlacklistItem storedBlacklistItem = getBlacklistItem(blacklistItem);

        if (storedBlacklistItem != null) {
            logger.info("Blacklist item [" + blacklistItem.getInstitution() +
                    ":" + blacklistItem.getService() +
                            "] already exists, updating...");
            blacklistItem.setKey(storedBlacklistItem.getKey());
            // Update requestStatus
            blacklistItem.setStatus(activeStatus);
            requestStatus = updateBlacklistItem(blacklistItem);

            if (requestStatus == RequestStatus.SUCCESS) {
                String serviceName = blacklistItem.getService();
                String institutionName = blacklistItem.getInstitution();

                /* If service is set to 'Active' ensure
                    that parent institution is set to 'Active' */
                if (serviceName != null &&
                        !serviceName.isEmpty() &&
                        activeStatus == ActiveStatusEnum.ACTIVE) {

                    // Is the institution already stored
                    blacklistItem = new BlacklistItem();
                    blacklistItem.setInstitution(institutionName);
                    blacklistItem.setStatus(activeStatus);
                    storedBlacklistItem = getBlacklistItem(blacklistItem);

                    if (storedBlacklistItem == null) {
                        requestStatus = addNewBlacklistItem(blacklistItem);
                    } else {
                        requestStatus = updateBlacklistItem(blacklistItem);
                    }
                }

                /* If parent institution is set to 'Blacklist'
                     set children to blacklist */
                if (serviceName == null && activeStatus == ActiveStatusEnum.BLACKLIST) {
                    for (BlacklistItem serviceItem : getBlacklistServices(institutionName)) {
                        serviceItem.setStatus(activeStatus);
                        if (requestStatus == RequestStatus.SUCCESS) {
                            requestStatus = updateBlacklistItem(serviceItem);
                        } else {
                            logger.error(
                                "Unexpected response calling updateBlacklistItem [" +
                                        requestStatus + "]");
                        }
                    }
                }
            }
        } else {
            logger.info("Blacklist item [" + blacklistItem.getInstitution() +
                    ":" + blacklistItem.getService() +
                "] does not exists, adding...");
            // Add the new Blacklist item
            requestStatus = addNewBlacklistItem(blacklistItem);

            if (requestStatus == RequestStatus.SUCCESS) {
                String serviceName = blacklistItem.getService();
                String institutionName = blacklistItem.getInstitution();

                /* If service is set to 'Active' ensure
                    that parent institution is set to 'Active' */
                if (serviceName != null &&
                        !serviceName.isEmpty() &&
                        activeStatus == ActiveStatusEnum.ACTIVE) {

                    // Is the institution already stored
                    blacklistItem = new BlacklistItem();
                    blacklistItem.setInstitution(institutionName);
                    blacklistItem.setStatus(activeStatus);
                    storedBlacklistItem = getBlacklistItem(blacklistItem);

                    if (storedBlacklistItem == null) {
                        requestStatus = addNewBlacklistItem(blacklistItem);
                    } else {
                        requestStatus = updateBlacklistItem(blacklistItem);
                    }
                }

                /* If parent institution is set to 'Blacklist'
                     set children to blacklist */
                if (serviceName == null && activeStatus == ActiveStatusEnum.BLACKLIST) {
                    for (BlacklistItem serviceItem : getBlacklistServices(institutionName)) {
                        serviceItem.setStatus(activeStatus);
                        if (requestStatus == RequestStatus.SUCCESS) {
                            requestStatus = updateBlacklistItem(serviceItem);
                        }
                    }
                }
            }
        }

        return requestStatus;
    }

    @Override
    @NotNull
    public RequestStatus updateBlacklistItem(@NotNull BlacklistItem blacklistItem) {
        if (blacklistItem.getInstitution() == null) {
            throw new RuntimeException(
                "Unable to update Blacklist with institution [" +
                        blacklistItem.getInstitution() + "]");
        }

        if (blacklistItem.getStatus() == null) {
            logger.warn("ActiveStatus is 'Null', defaulting to [" +
                    getDefaultBlacklistStatus() + "]");
            ActiveStatusEnum activeStatus = getDefaultBlacklistStatus();
            blacklistItem.setStatus(activeStatus);
        }

        RequestStatus requestStatus = RequestStatus.FAILURE;
        try {
            blacklistDao.updateBlacklistItem(convert(blacklistItem));
            requestStatus = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception updating BlacklistItem [" +
                blacklistItem.getInstitution() + ":" +
                    blacklistItem.getService() + "]", e);
        }
        return requestStatus;
    }

    @Override
    @NotNull
    public RequestStatus removeBlacklistItem(
            @NotNull String institution, @Nullable String service) {

        RequestStatus requestStatus = RequestStatus.FAILURE;
        try {
            blacklistDao.removeBlacklistItem(institution, service);
            requestStatus = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception removing BlacklistItem [" +
                institution + ":" + service + "]", e);
        }
        return requestStatus;
    }

    @Override
    @Nullable
    public BlacklistItem getBlacklistItem(
            @NotNull String institution, @Nullable String service) {

        BlacklistItem blacklistItem = null;

        try {
            BlacklistEntity blacklistEntity =
                    blacklistDao.getBlacklistItem(
                            institution, service);

            if (blacklistEntity != null) {
                blacklistItem = new BlacklistItem();
                blacklistItem.setKey(blacklistEntity.getKey());
                blacklistItem.setInstitution(blacklistEntity.getInstitution());
                blacklistItem.setService(blacklistEntity.getService());
                // Status
                ActiveStatusEnum activeStatusEnum =
                    ActiveStatusEnum.fromValue(
                        blacklistEntity.getStatus().getValue()
                    );
                blacklistItem.setStatus(activeStatusEnum);
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving BlacklistItem [" +
                institution + ":" + service + "]", e);
        }
        return blacklistItem;
    }

    @Override
    @NotNull
    public List<BlacklistItem> getBlacklistServices(@NotNull String institutionName) {
        List<BlacklistItem> blacklistItems = new ArrayList<BlacklistItem>();
        if (institutionName.isEmpty()) {
            throw new RuntimeException(
                "Unable to retrieve BlacklistServices for institution [" +
                        institutionName + "]");
        }

        try {
            List<BlacklistEntity> entities =
                    blacklistDao.getBlacklistServices(institutionName);
            if (entities != null && !entities.isEmpty()) {
                blacklistItems = convert(entities);
            }
        } catch (Exception e) {
            logger.error(
                    "Unexpected exception retrieving BlacklistItems for institution [" +
                institutionName + "]", e);
        }

        return blacklistItems;
    }

    @Override
    @NotNull
    public List<ActiveStatusEnum> getActiveStatusTypes() {
        List<ActiveStatusEnum> statusTypeEnums = new ArrayList<ActiveStatusEnum>();
        try {
            List<ActiveStatusEntity> statusTypeEntities =
                    blacklistDao.getActiveStatusTypes();
            for (ActiveStatusEntity statusTypeEntity : statusTypeEntities) {
                ActiveStatusEnum statusTypeEnum =
                        ActiveStatusEnum.fromValue(statusTypeEntity.getValue());
                statusTypeEnums.add(statusTypeEnum);
            }
        } catch (Exception e) {
            logger.error("Unexpected exception retrieving statusTypes", e);
        }
        return statusTypeEnums;
    }

    @Override
    @NotNull
    public RequestStatus setBlacklistDefault(
            @NotNull ActiveStatusEnum activeStatusEnum) {
        logger.info("Default Blacklist status is set to [" + activeStatusEnum + "]");

        defaultBlacklistStatus = activeStatusEnum;

        return RequestStatus.SUCCESS;
    }

    @Nullable
    private BlacklistItem getBlacklistItem(@NotNull BlacklistItem blacklistItem) {
        return getBlacklistItem(
            blacklistItem.getInstitution(), blacklistItem.getService());
    }

    @NotNull
    private ActiveStatusEnum getDefaultBlacklistStatus() {
        if (defaultBlacklistStatus == null) {
            defaultBlacklistStatus = ActiveStatusEnum.UNSPECIFIED;
        }
        return defaultBlacklistStatus;
    }

    @NotNull
    private RequestStatus addNewBlacklistItem(@NotNull BlacklistItem blacklistItem) {
        RequestStatus status = RequestStatus.FAILURE;
        try {
            blacklistDao.addBlacklistItem(convert(blacklistItem));
            status = RequestStatus.SUCCESS;
        } catch (Exception e) {
            logger.error("Unexpected exception persisting BlacklistItem [" +
                blacklistItem.getInstitution() + ":" + blacklistItem.getService()
                    + "]", e);
        }
        return status;
    }

    @NotNull
    private BlacklistEntity convert(@NotNull BlacklistItem blacklistItem) {
        BlacklistEntity blacklistEntity = new BlacklistEntity();
        blacklistEntity.setKey(blacklistItem.getKey());
        blacklistEntity.setInstitution(blacklistItem.getInstitution());
        blacklistEntity.setService(blacklistItem.getService());
        // Status
        if (blacklistItem.getStatus() != null) {
            ActiveStatusEntity activeStatusEntity =
                    getActiveStatusEntity(blacklistItem.getStatus().value());
            blacklistEntity.setStatus(activeStatusEntity);
        } else {
            ActiveStatusEntity activeStatusEntity =
                    getActiveStatusEntity(getDefaultBlacklistStatus().value());
            blacklistEntity.setStatus(activeStatusEntity);
        }

        return blacklistEntity;
    }

    @Nullable
    private ActiveStatusEntity getActiveStatusEntity(@NotNull String value) {
        return blacklistDao.getActiveStatusByValue(value);
    }

    @NotNull
    private BlacklistItem convert(@NotNull BlacklistEntity blacklistEntity) {
        BlacklistItem blacklistItem = new BlacklistItem();
        blacklistItem.setKey(blacklistEntity.getKey());
        blacklistItem.setInstitution(blacklistEntity.getInstitution());
        blacklistItem.setService(blacklistEntity.getService());
        // Status
        if (blacklistEntity.getStatus() != null) {
            ActiveStatusEnum activeStatusEnum =
                ActiveStatusEnum.fromValue(
                        blacklistEntity.getStatus().getValue());
            blacklistItem.setStatus(activeStatusEnum);
        }

        return blacklistItem;
    }

    @NotNull
    private List<BlacklistItem> convert(@NotNull List<BlacklistEntity> entities) {
        List<BlacklistItem> blacklistItems = new ArrayList<BlacklistItem>();

        for (BlacklistEntity blacklistEntity : entities) {
            blacklistItems.add(convert(blacklistEntity));

        }

        return blacklistItems;
    }

    // Spring dependency injection
    public void setBlacklistDao(BlacklistDao blacklistDao) {
        this.blacklistDao = blacklistDao;
    }
}
