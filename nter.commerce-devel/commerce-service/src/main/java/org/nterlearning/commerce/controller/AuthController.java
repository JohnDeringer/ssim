package org.nterlearning.commerce.controller;

import org.nterlearning.commerce.managed.BeanUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/9/12
 */
@ManagedBean(name = "authController")
public class AuthController implements Serializable {

    private String firstName;
	private String lastName;
	private String uid;
	private String email;
	private String fullName;

    private static final String GIVEN_NAME = "givenName";
    private static final String SURNAME = "sn";
    private static final String UID = "uid";
    private static final String MAIL = "mail";
    private static final String FULL_NAME = "cn";

    private static final String LOGOUT_URL = "/logout.xhtml";
    private static final String LOGIN_URL = "/institutions.xhtml";

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

	/**
	 * Stores SSO values from request header map.
	 */
	public AuthController() {
        HttpServletRequest request =
			(HttpServletRequest) FacesContext
				.getCurrentInstance()
					.getExternalContext()
						.getRequest();

        firstName = (String)request.getAttribute(GIVEN_NAME);
        lastName = (String)request.getAttribute(SURNAME);
		uid = (String)request.getAttribute(UID);
		email = (String)request.getAttribute(MAIL);
		fullName = (String)request.getAttribute(FULL_NAME);

        if (uid == null) {
            logger.error("Invalid SAML values... UID [" + uid +
                    "] firstName [" + firstName + "]" +
                    " lastName [" + lastName + "] email [" + email + "]");
        } else {
            logger.debug("Retrieving SAML values... firstName [" + firstName + "]" +
                " lastName [" + lastName + "] email [" + email + "]");
	    }
    }

	/**
	 * Gets the user's first name.
	 * @return first name
	 */
	public String getFirstName() {
		return firstName != null ? firstName : "missingFirstName";
	}

	/**
	 * Gets a unique identifier for the user.
	 * @return UID
	 */
	public String getUID() {
        if (uid == null) {
            uid = (String)BeanUtil.getAttributeFromSession(UID);
        }
        return uid;
	}
    public void setUID(String uid) {
        this.uid = uid;
        BeanUtil.setAttributeInSession(UID, uid);
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
		return lastName != null ? lastName : "missingLastName";
	}

	/**
	 * Gets the user's email address.
	 * @return email address
	 */
	public String getEmail() {
        if (email == null) {
            email = (String)BeanUtil.getAttributeFromSession(MAIL);
        }
        return email;
	}
    public void setEmail(String email) {
        this.email = email;
        BeanUtil.setAttributeInSession(MAIL, email);
    }

    public void signIn() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
		    ExternalContext externalContext = facesContext.getExternalContext();
            String contextPath = externalContext.getRequestContextPath();
            String redirectURL = contextPath + LOGIN_URL;
            externalContext.redirect(redirectURL);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void signOut() {
        logger.info("Logging-out user [" + getEmail() + "]");
        FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest)externalContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)externalContext.getResponse();

        uid = null;
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
                    cookie.setValue("");
                    logger.debug("Expiring cookie [" + name + "]");
                    Cookie newCookie = new Cookie(name, "");
                    newCookie.setMaxAge(0);
                    response.addCookie(newCookie);
                }
            }
        }

        HttpSession session = request.getSession(false);
        Enumeration enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            String name = (String)enumeration.nextElement();
            logger.info("Removing object [" + name + "] from HttpSession");
            session.removeAttribute(name);
        }

        try {
            String contextPath = externalContext.getRequestContextPath();
            String redirectURL = contextPath + LOGOUT_URL;
            externalContext.redirect(redirectURL);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

	}

    public static List<String> getCookieNames() {
        List<String> cookies = new ArrayList<String>();
        cookies.add("JSESSIONID");
        cookies.add("_shibstate_");
        cookies.add("_shibsession_");
        cookies.add("_saml_idp");
        cookies.add("USER_UUID");
        cookies.add("PASSWORD");
        cookies.add("SCREEN_NAME");
        cookies.add("LOGIN");
        cookies.add("sid");
        cookies.add("_idp_session");
        return cookies;
    }
}
