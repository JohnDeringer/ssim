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
package org.nterlearning.entitlement.persistence;

import javax.persistence.*;

/**
 *
 * A policy maps a subject to a resource for the purpose of authorization
 *
 */
@Entity(name = "PolicyEntity")
@Table(name = "ENTITLEMENT_POLICY", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "SUBJECT", "REALM", "RESOURCE"
    })
})

@Inheritance(strategy = InheritanceType.JOINED)
public class PolicyEntity {

    protected String subject;
    protected String resource;
    protected String realm;
    protected ActionEntity action;
    protected Long id;

    @Basic
    @Column(name = "SUBJECT", length = 255)
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Basic
    @Column(name = "RESOURCE", length = 255)
    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }

    @Basic
    @Column(name = "REALM", length = 255)
    public String getRealm() {
        return realm;
    }
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * Gets the value of the action property.
     *
     * @return
     *     possible object is
     *     {@link ActionEntity }
     *
     */
    @ManyToOne(targetEntity = ActionEntity.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "ACTION_ID", nullable = false)
    public ActionEntity getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     *
     * @param action
     *     allowed object is
     *     {@link ActionEntity }
     *
     */
    public void setAction(ActionEntity action) {
        this.action = action;
    }

    /**
     * Primary key
     * @return A auto-generated primary key of type Long
     */
    @Id
    @Column(name = "POLICY_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getID() {
        return id;
    }
    public void setID(Long value) {
        this.id = value;
    }

}
