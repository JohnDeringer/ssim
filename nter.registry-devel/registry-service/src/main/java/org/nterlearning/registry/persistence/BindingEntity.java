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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity(name = "BindingEntity")
@Table(name = "BINDING")
@Inheritance(strategy = InheritanceType.JOINED)
public class BindingEntity {

    protected String description;
    protected String accessPoint;
    protected BindingTypeEntity bindingType;
    protected Long key;

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "DESCRIPTION", length = 255)
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the accessPoint property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "END_POINT", nullable = false, length = 255)
    public String getAccessPoint() {
        return accessPoint;
    }

    /**
     * Sets the value of the accessPoint property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccessPoint(String value) {
        this.accessPoint = value;
    }

    /**
     * Gets the value of the bindingType property.
     *
     * @return
     *     possible object is
     *     {@link BindingTypeEntity }
     *
     */
    @ManyToOne(targetEntity = BindingTypeEntity.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "BINDING_TYPE_ID", nullable = false)
    public BindingTypeEntity getBindingType() {
        return bindingType;
    }

    /**
     * Sets the value of the bindingType property.
     *
     * @param value
     *     allowed object is
     *     {@link BindingTypeEntity }
     *
     */
    public void setBindingType(BindingTypeEntity value) {
        this.bindingType = value;
    }

    /**
     * Gets the value of the key property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    @Id
    @Column(name = "BINDING_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     *
     * @param value
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setKey(Long value) {
        this.key = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BindingEntity that = (BindingEntity) o;

        if (key != null ? !key.equals(that.key) : that.key != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
