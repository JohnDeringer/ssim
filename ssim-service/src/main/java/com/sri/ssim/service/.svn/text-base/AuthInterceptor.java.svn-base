package com.sri.ssim.service;

import com.sri.ssim.userManagement.TokenManager;

import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.jaxrs.interceptor.JAXRSInInterceptor;
import org.apache.cxf.message.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/23/12
 */
public class AuthInterceptor extends JAXRSInInterceptor {

    @Autowired
    TokenManager tokenManager;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String SESSION_COOKIE_NAME = "SSIM";

    public AuthInterceptor() {
        // Resource authentication exemption list
        exemptList.add("login");
        exemptList.add("createUser");
    }

    public void handleFault(Message message) {
        logger.error("Unexpected error authenticating", message);
    }

    public void handleMessage(Message message) {
        MessageContextImpl messageContext = new MessageContextImpl(message);
        String requestedResource = messageContext.getUriInfo().getPath();

        if (!isExempt(requestedResource)) {
            HttpHeaders httpHeaders = messageContext.getHttpHeaders();

            // Get the cookies from the HTTP header
            Map<String, Cookie> cookies = httpHeaders.getCookies();

            // Look for the SSIM session cookie
            String sessionToken;
            Cookie cookieValue = cookies.get(SESSION_COOKIE_NAME);
            if (cookieValue != null) {
                sessionToken = cookieValue.getValue();
            } else {
                throw new RuntimeException(
                        "Unable to find session cookie [" +
                                SESSION_COOKIE_NAME + "]");
            }

            // If found, is the session token valid?
            if (sessionToken != null) {
                // TODO: can add a check for authorization
                if (!tokenManager.isValid(sessionToken)) {
                    throw new RuntimeException(
                        "Unable to validate user session [" + sessionToken + "]");
                } else {
                    logger.info("Token [" + sessionToken + "] has been validated");
                }
            } else {
                throw new RuntimeException(
                        "Unable to validate user session [" +
                                cookieValue.getValue() + "]");
            }
        }
    }

    private boolean isExempt(String resource) {
        boolean isExempt = false;
        if (resource != null) {
            for (String item : exemptList) {
                if (resource.equals(item)) {
                    logger.info("Resource [" + resource +
                            "] is exempt from authentication check");
                    isExempt = true;
                    break;
                }
            }
        }
        //return isExempt;
        // TODO: Temporarily disable security
        return true;
    }

    private List<String> exemptList = new ArrayList<String>();

}
