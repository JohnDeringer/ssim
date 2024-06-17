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

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import org.nterlearning.registry.blacklist.client.BlacklistItem;
import org.nterlearning.registry.blacklist.client.ActiveStatusEnum;

import java.util.Random;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/7/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:registry-client-test.xml")
public class BlacklistApiTest {

    @Autowired
    private Blacklist blacklist;

    @Test
    public void addBlacklistItem() {
        String institution = getRandomValue("institution");

        // Create Institution blacklist item
        ActiveStatusEnum activeStatusEnum = ActiveStatusEnum.ACTIVE;
        BlacklistItem blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institution);
        blacklistItem.setStatus(activeStatusEnum);
        blacklist.addBlacklistItem(blacklistItem);

        ActiveStatusEnum status =
                blacklist.getBlacklistStatus(institution, null);

        Assert.isTrue(status == activeStatusEnum);

        // Create Service blacklist item
        activeStatusEnum = ActiveStatusEnum.BLACKLIST;
        String service = getRandomValue("service");
        blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institution);
        blacklistItem.setService(service);
        blacklistItem.setStatus(activeStatusEnum);
        blacklist.addBlacklistItem(blacklistItem);

        status =
                blacklist.getBlacklistStatus(institution, service);

        Assert.isTrue(status == activeStatusEnum);

        // Clean-up
        blacklist.removeBlacklistItem(institution, null);
        blacklist.removeBlacklistItem(institution, service);
    }

    @Test
    public void updateBlacklistItem() {
        String institution = getRandomValue("institution");
        String service = getRandomValue("service");

        // Create blacklist item
        ActiveStatusEnum activeStatusEnum = ActiveStatusEnum.ACTIVE;
        BlacklistItem blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institution);
        blacklistItem.setService(service);
        blacklistItem.setStatus(activeStatusEnum);
        blacklist.addBlacklistItem(blacklistItem);

        ActiveStatusEnum status =
                blacklist.getBlacklistStatus(institution, service);

        Assert.isTrue(status == activeStatusEnum);

        // Update status
        activeStatusEnum = ActiveStatusEnum.BLACKLIST;
        blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institution);
        blacklistItem.setService(service);
        blacklistItem.setStatus(activeStatusEnum);
        blacklist.addBlacklistItem(blacklistItem);

        status =
                blacklist.getBlacklistStatus(institution, service);

        Assert.isTrue(status == activeStatusEnum,
                "Expected status [" + activeStatusEnum + "] but received [" +
                status + "]");

        // Clean-up
        blacklist.removeBlacklistItem(institution, null);
        blacklist.removeBlacklistItem(institution, service);
    }

    @Test
    public void removeBlacklistItem() {
        String institution = getRandomValue("institution");
        String service = getRandomValue("service");

        // Create blacklist item
        ActiveStatusEnum activeStatusEnum = ActiveStatusEnum.ACTIVE;
        BlacklistItem blacklistItem = new BlacklistItem();
        blacklistItem.setInstitution(institution);
        blacklistItem.setService(service);
        blacklistItem.setStatus(activeStatusEnum);
        blacklist.addBlacklistItem(blacklistItem);

        ActiveStatusEnum status =
                blacklist.getBlacklistStatus(institution, service);

        Assert.isTrue(status == activeStatusEnum);

        // Remove blacklist item - service
        blacklist.removeBlacklistItem(institution, service);

        status =
                blacklist.getBlacklistStatus(institution, service);

        Assert.isNull(status, "Expected null but received [" + status + "]");

        // Remove blacklist item - institution
        blacklist.removeBlacklistItem(institution, null);

        status =
                blacklist.getBlacklistStatus(institution, null);

        Assert.isNull(status, "Expected null but received [" + status + "]");
    }

    private static String getRandomValue(String value) {
        Random r = new Random();
        return value + ":" + r.nextInt();
    }

}
