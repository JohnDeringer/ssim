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
import javax.persistence.UniqueConstraint;


@Entity(name = "BlacklistEntity")
@Table(name = "BLACK_LIST", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "INSTITUTION", "SERVICE"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class BlacklistEntity {

    protected String institution;
    protected String service;
    protected ActiveStatusEntity status;
    protected Long key;

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "INSTITUTION", nullable = false, length = 100)
    public String getInstitution() {
        return institution;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInstitution(String value) {
        this.institution = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "SERVICE", nullable = true, length = 100)
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link ActiveStatusEntity }
     *
     */
    @ManyToOne(targetEntity = ActiveStatusEntity.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "ACTIVE_STATUS_ID", nullable = false)
    public ActiveStatusEntity getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value
     *     allowed object is
     *     {@link ActiveStatusEntity }
     *
     */
    public void setStatus(ActiveStatusEntity value) {
        this.status = value;
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
    @Column(name = "BLACK_LIST_ID", scale = 0)
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

        BlacklistEntity that = (BlacklistEntity) o;

        if (key != null ? !key.equals(that.key) : that.key != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
