package com.sri.ssim.userManagement;

import com.sri.ssim.schema.UserRoleEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/25/12
 */
@Component
public class TokenManager {

    // Session timeout in milliseconds
    private long sessionTimeout;
    // Session tokens
    private ConcurrentMap<String, UserSession> tokens =
                                    new ConcurrentHashMap<String, UserSession>();
    private Logger logger = LoggerFactory.getLogger(TokenManager.class);

    private TokenManager(){}

    /**
     * Constructor
     * @param timeout Session timeout in minutes
     */
    public TokenManager(int timeout) {
        sessionTimeout = (timeout * 60) * 1000;
    }

    /**
     * Generates a new unique token and stores it in the token Map
     * @param username The users unique identifier
     * @param role The users security role
     * @return A String containing a unique UUID security token
     */
    public String generateToken(String username, String role) {
        String uuid = java.util.UUID.randomUUID().toString();
        // Ensure that the token is unique to the list
        while (tokens.get(uuid) != null) {
            uuid = java.util.UUID.randomUUID().toString();
        }
        long lastAccessTime = new Date().getTime();

        UserSession userSession = new UserSession();
        userSession.setUsername(username);
        userSession.setLastAccessTime(lastAccessTime);
        try {
            userSession.setRole(UserRoleEnum.fromValue(role));
        } catch (Exception e) {
            logger.error("Error capturing enum value from string [" + role +
                    "] setting to [" + UserRoleEnum.USER + "]");
            userSession.setRole(UserRoleEnum.USER);
        }

        tokens.putIfAbsent(uuid, userSession);

        return uuid;
    }

    /**
     * Is the given token valid. e.g. does it exist and is it non-expired
     * @param token A SSIM session token
     * @return true if the token exist and is not expired
     */
    public boolean isValid(String token) {
        boolean isValid = false;

        UserSession userSession = tokens.get(token);
        if (userSession != null) {
            long lastAccess = userSession.getLastAccessTime();
            long now = new Date().getTime();
            if (lastAccess + sessionTimeout < now) {
                logger.info("Token [" + token + "] has expired [" + lastAccess + "]");
                // Expired token - remove it
                tokens.remove(token);
            } else {
                // Good token - update
                isValid = true;
                userSession.setLastAccessTime(now);
                tokens.replace(token, userSession);
            }
        } else {
            logger.info("Unable to find user session [" + token + "]");
        }

        return isValid;
    }

    public void purgeExpiredSessions() {
        for (String key : tokens.keySet()) {
            UserSession userSession = tokens.get(key);
            long lastAccess = userSession.getLastAccessTime();
            long now = new Date().getTime();
            if (lastAccess + sessionTimeout < now) {
                logger.info("Token [" + key + "] has expired [" + lastAccess + "]");
                // Expired token - remove it
                tokens.remove(key);
            }
        }
    }

}
