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
package org.nterlearning.registry.proxy;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.nterlearning.registry.client.BindingTypeEnum;
import org.nterlearning.registry.client.BusinessService;

import java.io.Serializable;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/30/12
 */
public class ServiceBean
        extends BusinessService
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private RegistryInstance registryInstance;
    private static Log log = LogFactoryUtil.getLog(ServiceBean.class);

    public ServiceBean() {
        registryInstance = RegistryInstance.LOCAL;
    }

    public ServiceBean(BusinessService service, RegistryInstance instance) {
        registryInstance = instance;

        setActiveStatus(service.getActiveStatus());
        String description = service.getDescription();
        if (description == null) {
            description = "";
        }
        setDescription(description);
        setInstitutionName(service.getInstitutionName());
        setKey(service.getKey());
        setName(service.getName());
        setServiceType(service.getServiceType());

        getBinding().addAll(service.getBinding());
    }

    public RegistryInstance getRegistryInstance() {
        return registryInstance;
    }

    public void setRegistryInstance(RegistryInstance regInstance) {
        registryInstance = regInstance;
    }

    public String getServiceTypeValue() {
        return this.getServiceType().value();
    }

    public String getBindingTypeValue(BindingTypeEnum bindingTypeEnum) {
        String value = null;
        if (bindingTypeEnum != null) {
            try {
                value = bindingTypeEnum.value();
            } catch (Exception e) {
               log.error("Error retrieving value from bindingTypeEnum [" +
                       bindingTypeEnum + "]", e);
            }
        }
        return value;
    }




}
