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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity(name = "ServiceEntity")
@Table(name = "SERVICE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "SERVICE_NAME", "INSTITUTION_ID"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class ServiceEntity {

    protected String name;
    protected String description;
    protected ServiceTypeEntity serviceType;
    protected List<BindingEntity> binding;
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
    @Column(name = "SERVICE_NAME", nullable = false, length = 50)
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
     * Gets the value of the serviceType property.
     *
     * @return
     *     possible object is
     *     {@link ServiceTypeEntity }
     *
     */
    @ManyToOne(targetEntity = ServiceTypeEntity.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "SERVICE_TYPE_ID", nullable = false)
    public ServiceTypeEntity getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceTypeEntity }
     *
     */
    public void setServiceType(ServiceTypeEntity value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the binding property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the binding property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBinding().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BindingEntity }
     *
     *
     */
    @OneToMany(targetEntity = BindingEntity.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    public List<BindingEntity> getBinding() {
        if (binding == null) {
            binding = new ArrayList<BindingEntity>();
        }
        return binding;
    }

    /**
     *
     *
     */
    public void setBinding(List<BindingEntity> binding) {
        this.binding = binding;
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
    @Column(name = "SERVICE_ID", scale = 0)
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

        ServiceEntity that = (ServiceEntity) o;

        if (key != null ? !key.equals(that.key) : that.key != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
