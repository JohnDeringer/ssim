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

import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Random;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/13/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:registry-beans-test.xml")
public class BlacklistDaoTest {

    @Autowired
    private BlacklistDao blacklistDao;

    @Test
    public void addBlacklistItem() throws Exception {
        BlacklistEntity blacklistItem = new BlacklistEntity();
        String institutionName = getRandomValue("institutionName");
        String serviceName = getRandomValue("serviceName");

        // Create
        blacklistItem.setInstitution(institutionName);
        blacklistItem.setService(serviceName);
        // Blacklist status
        ActiveStatusEntity activeStatusEntity = new ActiveStatusEntity();
        activeStatusEntity.setValue(ActiveStatusEnum.ACTIVE.value());
        blacklistItem.setStatus(activeStatusEntity);

        blacklistDao.addBlacklistItem(blacklistItem);

        // Retrieve
        BlacklistEntity dbBlacklistItem =
                blacklistDao.getBlacklistItem(
                        institutionName, serviceName);
        Assert.isTrue(dbBlacklistItem != null,
            "Unable to retrieve BlacklistItem [" + institutionName + ":" +
                serviceName + "]");

        // clean-up
        blacklistDao.removeBlacklistItem(institutionName, serviceName);
    }

    @Test
    public void updateBlacklistItem() throws Exception {
        BlacklistEntity blacklistItem = new BlacklistEntity();
        String institutionName = getRandomValue("institutionName");
        String serviceName = getRandomValue("serviceName");

        // Create
        blacklistItem.setInstitution(institutionName);
        blacklistItem.setService(serviceName);
        // Blacklist status
        ActiveStatusEntity activeStatusEntity = new ActiveStatusEntity();
        activeStatusEntity.setValue(ActiveStatusEnum.ACTIVE.value());
        blacklistItem.setStatus(activeStatusEntity);
        blacklistDao.addBlacklistItem(blacklistItem);

        // Update
        activeStatusEntity = new ActiveStatusEntity();
        activeStatusEntity.setValue(ActiveStatusEnum.BLACKLIST.value());
        blacklistItem.setStatus(activeStatusEntity);
        blacklistDao.updateBlacklistItem(blacklistItem);

        // Retrieve
        BlacklistEntity dbBlacklistItem =
                blacklistDao.getBlacklistItem(
                        institutionName, serviceName);
        Assert.isTrue(dbBlacklistItem != null,
            "Unable to retrieve BlacklistItem [" + institutionName + ":" +
                serviceName + "]");
        Assert.isTrue(dbBlacklistItem.getStatus().getValue().equals(
                ActiveStatusEnum.BLACKLIST.value()),
            "Expected BlackList status [" +
                ActiveStatusEnum.BLACKLIST.value() + "] but received [" +
                    dbBlacklistItem.getStatus().getValue() + "]");

        // clean-up
        blacklistDao.removeBlacklistItem(
                institutionName, serviceName);
    }

    @Test
    public void removeBlacklistItem() throws Exception {
        BlacklistEntity blacklistItem = new BlacklistEntity();
        String institutionName = getRandomValue("institutionName");
        String serviceName = getRandomValue("serviceName");

        // Create
        blacklistItem.setInstitution(institutionName);
        blacklistItem.setService(serviceName);
        // Blacklist status
        ActiveStatusEntity activeStatusEntity = new ActiveStatusEntity();
        activeStatusEntity.setValue(ActiveStatusEnum.ACTIVE.value());
        blacklistItem.setStatus(activeStatusEntity);
        blacklistDao.addBlacklistItem(blacklistItem);

        // Retrieve
        BlacklistEntity dbBlacklistItem =
            blacklistDao.getBlacklistItem(
                    institutionName, serviceName);
        Assert.isTrue(dbBlacklistItem != null,
            "Unable to retrieve BlacklistItem [" + institutionName + ":" +
                serviceName + "]");

        // Remove
        blacklistDao.removeBlacklistItem(
                institutionName, serviceName);

        // Retrieve
        dbBlacklistItem =
                blacklistDao.getBlacklistItem(
                        institutionName, serviceName);
        Assert.isTrue(dbBlacklistItem == null,
            "Expected 'Null' but received BlacklistItem [" + dbBlacklistItem + "]");
    }

    @Test
    public void uniquenessTest() throws Exception {
        BlacklistEntity blacklistItem = new BlacklistEntity();
        String institution1Name = getRandomValue("institution1Name");
        String institution2Name = getRandomValue("institution2Name");
        String service1Name = getRandomValue("service1Name");

        // Add institution1 with service1
        blacklistItem.setInstitution(institution1Name);
        blacklistItem.setService(service1Name);
        // Blacklist status
        ActiveStatusEntity activeStatusEntity = new ActiveStatusEntity();
        activeStatusEntity.setValue(ActiveStatusEnum.ACTIVE.value());
        blacklistItem.setStatus(activeStatusEntity);

        blacklistDao.addBlacklistItem(blacklistItem);

        // Retrieve
        BlacklistEntity dbBlacklistItem =
                blacklistDao.getBlacklistItem(
                        institution1Name, service1Name);
        Assert.isTrue(dbBlacklistItem != null,
            "Unable to retrieve BlacklistItem [" + institution1Name + ":" +
                service1Name + "]");


        // Add same service as above different institution (should succeed)
        blacklistItem.setInstitution(institution2Name);
        blacklistItem.setService(service1Name);
        // Blacklist status
        blacklistItem.setStatus(activeStatusEntity);

        blacklistDao.addBlacklistItem(blacklistItem);

        // Retrieve
        dbBlacklistItem =
                blacklistDao.getBlacklistItem(
                        institution2Name, service1Name);
        Assert.isTrue(dbBlacklistItem != null,
            "Unable to retrieve BlacklistItem [" + institution2Name + ":" +
                service1Name + "]");

        // Add same institution and service as above (should fail)
        blacklistItem.setInstitution(institution2Name);
        blacklistItem.setService(service1Name);
        // Blacklist status
        blacklistItem.setStatus(activeStatusEntity);

        boolean success = true;
        try {
            blacklistDao.addBlacklistItem(blacklistItem);
        } catch (Exception e) {
            success = false;
        }
        Assert.isTrue(!success, "This should have failed");

        // clean-up
        blacklistDao.removeBlacklistItem(
                institution1Name, service1Name);
        blacklistDao.removeBlacklistItem(
                institution2Name, service1Name);
    }

    private String getRandomValue(String value) {
        Random r = new Random();
        return value + ":" + r.nextInt();
    }
}
