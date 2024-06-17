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
package org.nterlearning.registry.persistence;

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.BlacklistItem;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/5/11
 */
public class BlacklistDaoImpl
        extends JpaDaoSupport
            implements BlacklistDao {

    @Override
    @Transactional
    public void addBlacklistItem(BlacklistEntity blacklistItem) {
        try {
            // Blacklist status
            ActiveStatusEntity activeStatusEntity = blacklistItem.getStatus();
            if (activeStatusEntity.getKey() == null) {
                activeStatusEntity =
                        getActiveStatusByValue(activeStatusEntity.getValue());
                blacklistItem.setStatus(activeStatusEntity);
            }

            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(blacklistItem);
        } catch (Exception e) {
            logger.error("Error persisting blacklistItem", e);
        }
    }

    @Override
    @Transactional
    public void updateBlacklistItem(BlacklistEntity blacklistItem) {
        String institution = blacklistItem.getInstitution();
        String service = blacklistItem.getService();

        BlacklistEntity dbBlacklistItem =
                getBlacklistItem(blacklistItem);
        if (dbBlacklistItem == null) {
            throw new RuntimeException(
                    "Unable to update BlacklistItem for institution [" +
                        institution + "] and service [" +
                            service + "], because I could not find it.");
        }

        try {
            // Blacklist status
            ActiveStatusEntity activeStatusEntity = blacklistItem.getStatus();
            if (activeStatusEntity.getKey() == null) {
                activeStatusEntity =
                        getActiveStatusByValue(activeStatusEntity.getValue());
                blacklistItem.setStatus(activeStatusEntity);
            }

            dbBlacklistItem.setStatus(blacklistItem.getStatus());
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(dbBlacklistItem);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error updating BlacklistItem for institution [" +
                        institution + "] and service [" +
                            service + "]", e);
        }
    }

    @Override
    @Transactional
    public void removeBlacklistItem(
            String institution, String service) {
        BlacklistEntity dbBlacklistItem =
                getBlacklistItem(institution, service);
        if (dbBlacklistItem == null) {
            throw new RuntimeException(
                    "Unable to remove BlacklistItem for institution [" +
                        institution + "] and service [" +
                            service + "], because I could not find it.");
        }
        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.remove(dbBlacklistItem);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error remove BlacklistItem for institution [" +
                    institution + "] and service [" + service + "]", e);
        }
    }

    @Override
    public BlacklistEntity getBlacklistItem(
            String institution, String service) {

        BlacklistEntity blacklistItem = new BlacklistEntity();
        blacklistItem.setInstitution(institution);
        blacklistItem.setService(service);

        return getBlacklistItem(blacklistItem);
    }

    @Override
    public BlacklistEntity getBlacklistItem(BlacklistItem blacklistItem) {
        return getBlacklistItem(
                blacklistItem.getInstitution(),
                blacklistItem.getService());
    }

    @Override
    public BlacklistEntity getBlacklistItem(BlacklistEntity blacklistEntity) {
        BlacklistEntity blacklistItem = null;
        String institution = blacklistEntity.getInstitution();
        String service = blacklistEntity.getService();

        try {
            List<BlacklistEntity> blacklistItems;
            if (service == null) {
                blacklistItems =
                    getJpaTemplate().find(
                        "select o from BlacklistEntity o where o.institution = ?1 and o.service is null",
                            institution);
            } else {
                blacklistItems =
                    getJpaTemplate().find(
                        "select o from BlacklistEntity o where o.institution = ?1 and o.service = ?2",
                            institution, service);
            }

            if (blacklistItems != null && !blacklistItems.isEmpty()) {
                blacklistItem = blacklistItems.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving BlacklistItem for institution [" +
                    institution + "] and service [" + service + "]", e);
        }
        return blacklistItem;
    }

    @Override
    public List<BlacklistEntity> getBlacklistServices(String institution) {
        List<BlacklistEntity> blacklistItems;
        try {
            blacklistItems =
                    getJpaTemplate().find(
                        "select o from BlacklistEntity o where o.institution = ?1 and o.service is not null",
                            institution);
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving BlacklistItem for institution [" +
                    institution + "]", e);
        }
        return blacklistItems;
    }

    @Override
    @Transactional
    public void addActiveStatus(ActiveStatusEntity activeStatus) {
        try {
            JpaTemplate jpaTemplate = getJpaTemplate();
            jpaTemplate.merge(activeStatus);
        } catch (Exception e) {
            logger.error("Error persisting activeStatus", e);
        }
    }

    @Override
    public ActiveStatusEntity getActiveStatusByValue(String value) {
        ActiveStatusEntity status = null;
        try {
            List<ActiveStatusEntity> statusList =
                getJpaTemplate().find(
                    "select o from ActiveStatusEntity o where o.value = ?1",
                        value);
            if (!statusList.isEmpty()) {
                status = statusList.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving ActiveStatus", e);
        }
        return status;
    }

    @Override
    public List<ActiveStatusEntity> getActiveStatusTypes() {
        List<ActiveStatusEntity> activeStatusTypes;
        try {
            activeStatusTypes =
                getJpaTemplate().find(
                    "select o from ActiveStatusEntity o");
        } catch (Exception e) {
            throw new RuntimeException(
                "Error retrieving ActiveStatus Types", e);
        }
        return activeStatusTypes;
    }

}
