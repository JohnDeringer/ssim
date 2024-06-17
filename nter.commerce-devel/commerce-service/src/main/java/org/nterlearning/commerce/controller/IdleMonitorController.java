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

package org.nterlearning.commerce.controller;

import org.nterlearning.commerce.managed.BeanUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 6/8/12
 */
@ManagedBean(name = "idleMonitorController")
public class IdleMonitorController implements Serializable {

    private static final String LAST_ACCESS_TIME = "lastAccessedTime";
    private static final String IDLE_TIME = "idleTimeSeconds";

    private static Logger logger = LoggerFactory.getLogger(IdleMonitorController.class);

    public void idleListener() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest)externalContext.getRequest();
        HttpSession session = request.getSession();

        if (session != null) {
            int idleTimeSeconds;
            Date lastAccessedTime = new Date(session.getLastAccessedTime());

            Date cachedLastAccessedTime =
                    BeanUtil.getAttributeFromSession(LAST_ACCESS_TIME) != null ?
                    (Date)BeanUtil.getAttributeFromSession(LAST_ACCESS_TIME) : null;

            int cachedIdleTimeSeconds =
                    BeanUtil.getAttributeFromSession(IDLE_TIME) != null ?
                    (Integer)BeanUtil.getAttributeFromSession(IDLE_TIME) : 0;

            if (cachedLastAccessedTime != null &&
                    lastAccessedTime.after(cachedLastAccessedTime)) {
                // User activity - reset idle time
                idleTimeSeconds = 0;
            } else {
                int idleTimeInterval =
                        (int)((System.currentTimeMillis() -
                                session.getLastAccessedTime())/1000) + 1;

                // No activity - increment idle time
                idleTimeSeconds = cachedIdleTimeSeconds + idleTimeInterval;
            }

            // Update session variables
            BeanUtil.setAttributeInSession(IDLE_TIME, idleTimeSeconds);
            BeanUtil.setAttributeInSession(LAST_ACCESS_TIME,
                            new Date(System.currentTimeMillis() + 1000));

            logger.debug("Session idle time [" + idleTimeSeconds +
                    "] seconds. Session max inactive interval [" +
                    session.getMaxInactiveInterval() + "] seconds");

            if (idleTimeSeconds >= session.getMaxInactiveInterval()) {
                logger.info("System logout: User session has been idle for " +
                        idleTimeSeconds + " seconds. System timeout is set at " +
                        session.getMaxInactiveInterval() + " seconds.");

                facesContext.addMessage(
                        null, new FacesMessage(
                        FacesMessage.SEVERITY_WARN,
                        "Your session is closed", "You have been idle for " +
                        idleTimeSeconds + " seconds.")
                );
                logout();
            } else if (idleTimeSeconds >= (session.getMaxInactiveInterval() - 90)) {
                logger.debug("Session idle: User session has been idle for " +
                        idleTimeSeconds + " seconds. System timeout is set at " +
                        session.getMaxInactiveInterval() + " seconds.");

                FacesContext.getCurrentInstance().addMessage(
                    null, new FacesMessage(
                        FacesMessage.SEVERITY_WARN,
                        "Your session is expiring",
                        "You have been idle for " + idleTimeSeconds/60 + " minutes."));
            }
        } else {
            logger.error("Invalid session [" + session + "]");
        }
    }

    private void logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest)externalContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)externalContext.getResponse();
        HttpSession session = request.getSession();

        String LOGOUT_URL = "/logout.xhtml";

        for (Cookie cookie: request.getCookies()) {
            String name = cookie.getName();
            logger.debug("Site cookie [" + name +
                    "] value [" + cookie.getValue() +
                    "] domain [" + cookie.getDomain() +
                    "] maxAge [" + cookie.getMaxAge() +
                    "] path [" + cookie.getPath() +
                    "] secure [" + cookie.getSecure() +
                    "]");
            for (String cookieName : AuthController.getCookieNames()) {
                if (name.startsWith(cookieName)) {
                    logger.debug("Expiring cookie [" + name + "]");
                    Cookie newCookie = new Cookie(name, "");
                    newCookie.setMaxAge(0);
                    response.addCookie(newCookie);
                }
            }
        }

        logger.info("Invalidating HTTP Session");
        session.invalidate();

        try {
            logger.info("Forwarding to logout");
            String contextPath = externalContext.getRequestContextPath();
            String redirectURL = contextPath + LOGOUT_URL;
            externalContext.redirect(redirectURL);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
