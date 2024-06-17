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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


@Entity(name = "ContactEntity")
@Table(name = "CONTACT_INFO")
@Inheritance(strategy = InheritanceType.JOINED)
public class ContactEntity {

    protected String personName;
    protected String description;
    protected String phone;
    protected String email;
    protected String address;
    protected Long key;

    /**
     * Gets the value of the personName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "PERSON_NAME", nullable = false, length = 100)
    public String getPersonName() {
        return personName;
    }

    /**
     * Sets the value of the personName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPersonName(String value) {
        this.personName = value;
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
     * Gets the value of the phone property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "PHONE", length = 255)
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the email property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "EMAIL", length = 255)
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the address property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "ADDRESS", length = 255)
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAddress(String value) {
        this.address = value;
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
    @Column(name = "CONTACT_INFO_ID", scale = 0)
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

        ContactEntity that = (ContactEntity) o;

        if (key != null ? !key.equals(that.key) : that.key != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
