package com.sri.ssim.model;

import com.sri.ssim.persistence.User;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/24/12
 */
public interface UserManagement {

    /**
     * Validate a client token
     * @param token A String containing the system generated token
     * @return True if the token is valid
     */
    boolean isTokenValid(String token);

    /**
     * User log-in, passing a username, password and returning and token
     * @param username A String containing a unique identifier
     * @param password A String containing a secret key
     * @return A system generated token
     */
    String login(String username, String password);

    /**
     * Create a new User passing a username, password and optional role
     * @param username A users unique identifier
     * @param password A users secret key
     * @param role The users security role, normally UserRoleEnum.USER;
     * @return A User entity object
     */
    User createUser(String username, String password, String role);

    /**
     * Delete an existing user
     * @param username A users unique identifier
     */
    void deleteUser(String username);
}
