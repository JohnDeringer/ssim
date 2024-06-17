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
package org.nterlearning.registry.controller;

import org.apache.log4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Retrieves Shibboleth headers from http request.
 *
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 02/5/12
 */
@ManagedBean(name = "authController")
public class AuthController implements Serializable {

	private String firstName;
	private String lastName;
	private String UID;
	private String email;
	private String fullName;

    private static final String LOGOUT_URL = "https://www.nterlearning.org/Shibboleth.sso/Logout";

    private Logger logger = Logger.getLogger(AuthController.class);

	/**
	 * Stores SSO values from request header map.
	 */
	public AuthController() {
        HttpServletRequest request =
			(HttpServletRequest)FacesContext
				.getCurrentInstance()
					.getExternalContext()
						.getRequest();

        firstName = (String)request.getAttribute("givenName");
        lastName = (String)request.getAttribute("sn");
		UID = (String)request.getAttribute("uid");
		email = (String)request.getAttribute("mail");
		fullName = (String)request.getAttribute("cn");

        logger.debug("Retrieving SAML values firstName [" + firstName + "]" +
                " lastName [" + lastName + "] email [" + email + "]");
	}

    // TODO: Remove test values

	/**
	 * Gets the user's first name.
	 * @return first name
	 */
	public String getFirstName() {
		return firstName != null ? firstName : "testFirstName";
	}

	/**
	 * Gets a unique identifier for the user.
	 * @return UID
	 */
	public String getUID() {
        //return UID != null ? UID : "fa9e41bb-19b4-4ecc-b293-d94ae877fe68";
        //return UID != null ? UID : "10223895-4ced-430e-9cc6-4aed8094ad40";
        return UID != null ? UID : "admin";
	}

	/**
	 * Gets the user's complete name.
	 * @return full name
	 */
	public String getFullName() {
		return fullName != null ? fullName : getFirstName() + " " + getLastName();
	}

	/**
	 * Gets the user's surname/last name.
	 * @return last name
	 */
	public String getLastName() {
		return lastName != null ? lastName : "testLastName";
	}

	/**
	 * Gets the user's email address.
	 * @return email address
	 */
	public String getEmail() {
        //return email != null ? email : "jdslo1@mailinator.com";
		//return email != null ? email : "jdslo2@mailinator.com";
        return email != null ? email : "admin@nterlearning.org";
	}

    public void signOut() {

        logger.info("Logging out of NTER - user [" + email + "]");
        FacesContext facesContext = FacesContext.getCurrentInstance();

		ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest)externalContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)externalContext.getResponse();

        UID = null;
        email = null;

        for (Cookie cookie: request.getCookies()) {
            String name = cookie.getName();
            logger.debug("Site cookie [" + name +
                    "] value [" + cookie.getValue() +
                    "] domain [" + cookie.getDomain() +
                    "] maxAge [" + cookie.getMaxAge() +
                    "] path [" + cookie.getPath() +
                    "] secure [" + cookie.getSecure() +
                    "]");
            for (String cookieName : getCookieNames()) {
                if (name.startsWith(cookieName)) {
                    logger.debug("Expiring cookie [" + name + "]");
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    response.addCookie(cookie);
                }
            }
        }

        try {
            response.sendRedirect(LOGOUT_URL);
        } catch (Exception e) {
            logger.error(e);
        }

	}

    private List<String> getCookieNames() {
        List<String> cookies = new ArrayList<String>();
        cookies.add("JSESSIONID");
        cookies.add("_shibstate_");
        cookies.add("__utm");
        cookies.add("_shibsession_");
        cookies.add("_saml_idp");

        return cookies;
    }
}
