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
package org.nterlearning.commerce.form;

import org.nterlearning.entitlement.client.Action;
import org.nterlearning.entitlement.client.Subject;

import java.util.List;

/**
 * User: Deringer
 * Date: 8/10/11
 * Time: 12:22 PM
 */
public class AddEntitlementForm {

    private String institutionName;
    private String subject;
    private Action action;
    private String referrer;
    private List<String> institutions;
    private List<Subject> subjects;
    private List<Action> actions;

    public String getInstitutionName() {
        return institutionName;
    }
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }

    public String getReferrer() {
        return referrer;
    }
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public List<String> getInstitutions() {
        return institutions;
    }
    public void setInstitutions(List<String> institutions) {
        this.institutions = institutions;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Action> getActions() {
        return actions;
    }
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
