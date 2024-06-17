package com.sri.ssim.persistence;

import com.sri.ssim.schema.UserRoleEnum;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Random;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/26/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/repository-model-beans.xml")
public class UserManagementDaoTest {

    @Autowired
    UserManagementDao userManagementDao;

    @Test
    public void createUser() {
        // Create a user
        String username = getRandomValue("username");
        String password = getRandomValue("password");
        UserRoleEnum roleEnum = UserRoleEnum.USER;
        User user = createUser(username, password, roleEnum);

        Assert.notNull(user,
                "Error creating User Entity [" + user + "]");
        Assert.isTrue(user.getUsername().equals(username),
                "Expected username [" + username +
                        "] but received [" + user.getUsername() + "]");
        Assert.isTrue(Arrays.equals(user.getPassword(),password.getBytes()),
                "Expected password [" + password.getBytes() +
                        "] but received [" + user.getPassword() + "]");
        Assert.isTrue(user.getRole().getRole().equals(roleEnum.value()),
                "Expected role [" + roleEnum.value() +
                        "] but received [" + user.getRole().getRole() + "]");

        // Clean-up
        deleteUser(username);
    }

    @Test
    public void getUser() {
        // Create a user
        String username = getRandomValue("username");
        String password = "myPassword";
        UserRoleEnum roleEnum = UserRoleEnum.USER;
        User createUser = createUser(username, password, roleEnum);

        Assert.notNull(createUser,
                "Error creating User Entity [" + createUser + "]");

        User getUser = userManagementDao.getUser(username);
        Assert.notNull(getUser,
                "Error creating User Entity [" + getUser + "]");
        Assert.isTrue(getUser.getUsername().equals(username),
                "Expected username [" + username +
                        "] but received [" + getUser.getUsername() + "]");
        Assert.isTrue(Arrays.equals(getUser.getPassword(),password.getBytes()),
                "Expected password [" + password.getBytes() +
                        "] but received [" + getUser.getPassword() + "]");
        Assert.isTrue(getUser.getRole().getRole().equals(roleEnum.value()),
                "Expected role [" + roleEnum.value() +
                        "] but received [" + getUser.getRole().getRole() + "]");

        // Clean-up
        deleteUser(username);
    }

    @Test
    public void findUserRoleByValue() {
        UserRole userRole =
                userManagementDao.findUserRoleByValue(UserRoleEnum.USER);
        Assert.notNull(userRole,
                "Error finding UserRole Entity [" + UserRoleEnum.USER + "]");
        Assert.isTrue(userRole.getRole().equals(UserRoleEnum.USER.value()),
                "Expected value [" + UserRoleEnum.USER.value() +
                        "] but received [" + userRole.getRole() + "]");

        UserRole adminRole =
                userManagementDao.findUserRoleByValue(UserRoleEnum.ADMIN);
        Assert.notNull(adminRole,
                "Error finding UserRole Entity [" + UserRoleEnum.ADMIN + "]");
        Assert.isTrue(adminRole.getRole().equals(UserRoleEnum.ADMIN.value()),
                "Expected value [" + UserRoleEnum.ADMIN.value() +
                        "] but received [" + adminRole.getRole() + "]");
    }

    private User createUser(String username, String password, UserRoleEnum roleEnum) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password.getBytes());
        UserRole userRole = new UserRole();
        userRole.setEnumValue(roleEnum);
        user.setRole(userRole);

        return userManagementDao.createUser(user);
    }

    private void deleteUser(String username) {
        userManagementDao.deleteUser(username);
    }

    private String getRandomValue(String seed) {
        Random r = new Random();
        int nextI = r.nextInt();
        return (nextI > 0 ? seed + ":" + nextI : seed + ":" + nextI * -1);
    }

}
