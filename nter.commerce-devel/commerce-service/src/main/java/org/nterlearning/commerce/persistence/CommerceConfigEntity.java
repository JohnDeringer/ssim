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

package org.nterlearning.commerce.persistence;

import org.jetbrains.annotations.NotNull;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.CommerceConfig;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity(name = "CommerceConfigEntity")
@Table(name = "COMMERCE_CONFIG")
@Inheritance(strategy = InheritanceType.JOINED)
public class CommerceConfigEntity {

    protected BigDecimal adminFee;
    protected BigDecimal referrerFee;
    protected Long id;

    public CommerceConfigEntity() {}

    public CommerceConfigEntity(CommerceConfig commerceConfig) {
        setCommerceConfig(commerceConfig);
    }

    @Transient
    public CommerceConfig getCommerceConfig() {
        CommerceConfig commerceConfig = new CommerceConfig();
        commerceConfig.setAdminFee(adminFee);
        commerceConfig.setReferrerFee(referrerFee);
        commerceConfig.setID(id);

        return commerceConfig;
    }

    public void setCommerceConfig(@NotNull CommerceConfig commerceConfig) {
        id = commerceConfig.getID();
        adminFee = commerceConfig.getAdminFee();
        referrerFee = commerceConfig.getReferrerFee();
    }

    /**
     * The NTER administration fee (% of course price).
     *
     * @return
     *     adminFee
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "ADMIN_FEE", precision = 5, scale = 3)
    public BigDecimal getAdminFee() {
        return adminFee;
    }

    /**
     * Sets the value of the adminFee property.
     *
     * @param adminFee
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setAdminFee(BigDecimal adminFee) {
        this.adminFee = adminFee;
    }

    /**
     * The NTER referrer fee (% of course price).
     *  This is the NTER instance that initiated the course purchase.
     *
     * @return
     *     referrerFee
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "REFERRER_FEE", precision = 5, scale = 3)
    public BigDecimal getReferrerFee() {
        return referrerFee;
    }

    /**
     * Sets the value of the referrerFee property.
     *
     * @param referrerFee
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setReferrerFee(BigDecimal referrerFee) {
        this.referrerFee = referrerFee;
    }

    /**
     * The unique identifier provided by the data store.
     *
     * @return
     *     id
     *     {@link Long }
     *
     */
    @Id
    @Column(name = "ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param id
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setID(Long id) {
        this.id = id;
    }

}
