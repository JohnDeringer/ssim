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

import org.nterlearning.registry.persistence.ActiveStatusEntity;
import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.ActiveStatusEnum;
import org.nterlearning.xml.nter_registry.blacklist_objects_0_1_0.BlacklistItem;
import org.nterlearning.xml.nter_registry.domain_objects_0_1_0.RequestStatus;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/13/11
 */
public interface BlacklistModel {

    RequestStatus addBlacklistItem(BlacklistItem blacklistItem);

    RequestStatus updateBlacklistItem(BlacklistItem blacklistItem) ;

    RequestStatus removeBlacklistItem(
            String institution, String service);

    BlacklistItem getBlacklistItem(
            String institution, String service);

    List<BlacklistItem> getBlacklistServices(String institution);

    List<ActiveStatusEnum> getActiveStatusTypes();

    RequestStatus setBlacklistDefault(ActiveStatusEnum activeStatusEnum);

}
