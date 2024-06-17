package com.sri.ssim.model;

import com.sri.ssim.persistence.User;
import com.sri.ssim.persistence.UserManagementDao;
import com.sri.ssim.persistence.UserRole;
import com.sri.ssim.schema.UserRoleEnum;
import com.sri.ssim.userManagement.TokenManager;
import com.sri.ssim.userManagement.UserMgmtUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/24/12
 */
public class UserManagementImpl implements UserManagement {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserManagementDao userManagementDao;

    @Autowired
    TokenManager tokenManager;

    @Override
    public boolean isTokenValid(@NotNull String token) {
        return tokenManager.isValid(token);
    }

    @Override
    public String login(@NotNull String username, @NotNull String password) {

        // look-up user
        User user = userManagementDao.getUser(username);
        if (user == null) {
            logger.info("Unable to find user [" + username + "]");
            throw new RuntimeException("Invalid username or password");
        }

        // Validate password
        if (!UserMgmtUtil.isPasswordValid(password, user.getPassword())) {
            logger.info("Unable to validate password [" + password + "]");
            throw new RuntimeException("Invalid username or password");
        }

        // Generate token and return
        return tokenManager.generateToken(username, user.getRole().getRole());
    }

    @Override
    public User createUser(
            @NotNull String username, @NotNull String password, @Nullable String role) {

        // Make sure that this user does not already exist
        User user = userManagementDao.getUser(username);
        if (user != null) {
            throw new RuntimeException("The username [" + username + "] is already taken");
        } else {
            user = new User();
        }

        user.setUsername(username);

        byte[] salt = UserMgmtUtil.generateSalt();
        byte[] hashedPassword = UserMgmtUtil.hashPassword(salt, password);
        // Store the salt pre-pended to the password
        byte[] saltPlusPassword =
                UserMgmtUtil.combinePasswordSaltAndHash(salt, hashedPassword);

        user.setPassword(saltPlusPassword);

        UserRole userRole;
        if (role == null) {
            userRole = userManagementDao.findUserRoleByValue(UserRoleEnum.USER);
        } else {
            userRole =
                    userManagementDao.findUserRoleByValue(
                            UserRoleEnum.fromValue(role)
                    );
        }
        user.setRole(userRole);

        userManagementDao.createUser(user);

        return user;
    }

    @Override
    public void deleteUser(@NotNull String username) {
        User user = userManagementDao.getUser(username);
        if (user == null) {
            logger.warn("Unable to delete user [" + username +
                    "] because I could not find it");
        } else {
            userManagementDao.deleteUser(username);
        }
    }

}
