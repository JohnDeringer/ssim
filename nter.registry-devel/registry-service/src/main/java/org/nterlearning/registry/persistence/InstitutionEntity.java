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

import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity(name = "InstitutionEntity")
@Table(name = "INSTITUTION", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "INSTITUTION_NAME"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class InstitutionEntity {

    protected String name;
    protected String description;
    protected ContactEntity contactInfo;
    protected List<ServiceEntity> service;
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
    @Column(name = "INSTITUTION_NAME", nullable = false, length = 75)
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

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
     * Gets the value of the contactInfo property.
     *
     * @return
     *     possible object is
     *     {@link ContactEntity }
     *
     */
    @OneToOne(targetEntity = ContactEntity.class, cascade = {
        CascadeType.REMOVE,
        CascadeType.ALL
    })
    @JoinColumn(name = "CONTACT_INFO_ID", unique = true)
    public ContactEntity getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets the value of the contactInfo property.
     *
     * @param value
     *     allowed object is
     *     {@link ContactEntity }
     *
     */
    public void setContactInfo(ContactEntity value) {
        this.contactInfo = value;
    }

    @OneToMany(targetEntity = ServiceEntity.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "INSTITUTION_ID", nullable = false)
    public List<ServiceEntity> getService() {
        if (service == null) {
            service = new ArrayList<ServiceEntity>();
        }
        return service;
    }

    /**
     *
     *
     */
    public void setService(List<ServiceEntity> service) {
        this.service = service;
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
    @Column(name = "INSTITUTION_ID", scale = 0)
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

        InstitutionEntity that = (InstitutionEntity) o;

        if (key != null ? !key.equals(that.key) : that.key != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
