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
package org.nterlearning.commerce.managed;

import org.nterlearning.entitlement.client.EntitlementPolicy;
import org.nterlearning.entitlement.client.Subject;
import org.nterlearning.entitlement.client.Action;

import java.io.Serializable;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/9/12
 */
public class Policy extends EntitlementPolicy implements Serializable {

private String email;

    public Policy(){
    }

    public Policy(String realm, Subject subject, String resource, Action action) {
        setRealm(realm);
        setSubject(subject.getSubjectId());
        setEmail(subject.getEmail());
        setResource(resource);
        setAction(action);
    }

    public Policy(String realm, String subject, String email, String resource, Action action) {
        setRealm(realm);
        setSubject(subject);
        setEmail(email);
        setResource(resource);
        setAction(action);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
